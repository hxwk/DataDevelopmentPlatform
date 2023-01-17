package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.net.tcp.TcpConnection;
import com.yaxon.vn.nd.tas.net.tcp.TcpConnectionManager;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yaxon.vn.nd.redis.RedisConstants.*;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.*;
import static com.yaxon.vndp.common.util.CodecUtils.*;


/**
 * Author: 程行荣
 * Time: 2013-11-22 14:31
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

@Component
public class TerminalManagementPH extends BaseProtoHandler {

    private static final Logger logger = LoggerFactory.getLogger(TerminalManagementPH.class);



    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{configProperties['dms.nodeId']}")
    private String nodeId;

    @Value("#{configProperties['terminal.maxidletimemillis']}")
    private Long terminalMaxIdleTimeMillis;

    @Autowired
    private AvsProtocolPH avsProtocolPH;


    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8103) {//设置终端参数
            do_8103(ctx, (Req_8103) dnReq);
        } else if (dnReq instanceof Req_8104) {//查询终端参数
            do_8104(ctx, (Req_8104) dnReq);
        } else if (dnReq instanceof Req_8105) {//终端控制
            do_8105(ctx, (Req_8105) dnReq);
        } else if (dnReq instanceof Req_8106) {//查询终端指定的参数
            do_8106(ctx, (Req_8106) dnReq);
        } else if(dnReq instanceof Req_DisconnectVehiceFromTas){//断开终端与前置机的连接
            do_disconnectVehiceFromTas(ctx,(Req_DisconnectVehiceFromTas)dnReq);
        } else if(dnReq instanceof Req_8106_nd){//南斗自定义查询  查询IP锁定状态
            do_8106_nd(ctx,(Req_8106_nd) dnReq);
        }
        else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0002) {//终端心跳
            do_0002(upMsg);
        } else if (upMsg.msgId == 0x0100) {//终端注册
            do_0100(upMsg);
        } else if (upMsg.msgId == 0x0102) {//终端鉴权
            do_0102(upMsg);
        } else if (upMsg.msgId == 0x0003) {//终端注销
            do_0003(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    /**
     * 终端心跳
     *
     * @param upMsg
     * @return
     */
    private void do_0002(final ProtoMsg upMsg) {
        try {
            ValueOperations<String, String> op = redisTemplate.opsForValue();
            op.set(GK_VEHICLE_STATE + upMsg.vid, nodeId, terminalMaxIdleTimeMillis, TimeUnit.MILLISECONDS);//2015/3/09 by xwq 金采反馈修改未10分钟，原来为180分钟
        } catch (Exception e) {
            logger.warn("在Redis中更新车辆状态失败", e);
        }
        //不做业务处理
    }

    /**
     * 终端注册
     *
     * @param upMsg
     * @return
     */
    private void do_0100(final ProtoMsg upMsg) {
        final Req_0100 q = new Req_0100();
        //解析上行请求协议
        try {
            q.setVid(upMsg.sim); //注册时还没有vid，临时用sim代替，便于负载均衡
            q.setSim(upMsg.sim);
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setProvId(reqBuf.readShort());
            q.setCityId(reqBuf.readShort());
            q.setManufacturerId(readString(reqBuf, 5));
            q.setModel(readString(reqBuf, 20));
            q.setDeviceNo(readString(reqBuf, 7));
            q.setCol(reqBuf.readByte());//车牌颜色
            q.setLpn(readString(reqBuf, reqBuf.readableBytes()));//车牌号
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8100 r = (Res_8100) result;

                ProtoMsg res = new ProtoMsg();
                res.sim = upMsg.sim;
                res.msgId = ProtoConstants.TERMINAL_REGISTER_RES;
                res.vid = r.getVid();
                res.dataBuf = Unpooled.buffer(16);
                byte rc = 0;
                if (r.getRc() != 0) { //自定义应答码转换
                    if (r.getRc() > 0x10) {
                        rc = (byte) (r.getRc() - 0x10);
                    } else {
                        rc = 5; //其他异常
                    }

                    logger.warn("终端注册失败({}):{}:{}", rc, r.getRcMsg(r.getRc()), q);
                }
                res.dataBuf.writeShort(upMsg.sn);
                res.dataBuf.writeByte(rc);
                writeString(res.dataBuf, r.getAuthCode());

                sendMessage(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("终端注册失败:{}:{}", q, t.getMessage());
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            }
        });
        return;
    }

    /**
     * 终端鉴权(0102->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0102(final ProtoMsg upMsg) {

        final Req_0102 q = new Req_0102();
        //解析上行请求协议
        try {
            q.setVid(upMsg.sim); //鉴权时还没有vid，临时用sim代替，便于负载均衡
            q.setSim(upMsg.sim);
            q.setLogFlag(Req_0102.LOG_IN);
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setAuthCode(readString(reqBuf, reqBuf.readableBytes()));
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            //closeConnBySim(upMsg.sim);
            return;
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
                logger.info("终端鉴权应答:{}",r);
                if (r.getRc() == 1) {
                    logger.warn("终端鉴权失败({}):{}", r.getRc(), q);
                    //closeConnBySim(upMsg.sim);
                }
                if (r.getRc() == 3) {
                    logger.warn("终端已停用，无法上线({}):{}", r.getRc(), q);
                    //closeConnBySim(upMsg.sim);
                }
                upMsg.vid = r.getVid();
                sendCenterGeneralRes(upMsg, r.getRc());
                avsProtocolPH.doTerminalLogin(upMsg.sim, q.getAuthCode());

                try {
                    ValueOperations<String, String> op = redisTemplate.opsForValue();
                    op.set(GK_VEHICLE_STATE + r.getVid(), nodeId, terminalMaxIdleTimeMillis, TimeUnit.MILLISECONDS);  //将终端登录节点ID缓存，为了tbp下发指令时路由
                    redisTemplate.convertAndSend(IM_T8_0102 + r.getVid(), "1");
                    redisTemplate.convertAndSend(IM_T8_0102_SIM + upMsg.sim, "1");
                } catch (Exception e) {
                    logger.warn("在Redis中更新车辆状态失败", e);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("终端鉴权失败:{}:{}", q, t.getMessage());
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
                //closeConnBySim(upMsg.sim);
            }
        });
        return;
    }

    /**
     * 上行协议：终端注销(0003->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0003(final ProtoMsg upMsg) {
        Req_0003 q = new Req_0003();
        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;

                sendCenterGeneralRes(upMsg, r.getRc());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 下行协议：设置终端参数
     *
     * @param dnReq
     * @return
     */
    private void do_8103(final DmsContext ctx, final Req_8103 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            int paramNum = dnReq.getParamItems().size();
            Validate.isTrue(paramNum <= 255, "单次设置的参数太多");

            req.dataBuf.writeByte(paramNum);
            for (ParamItem paramItem : dnReq.getParamItems()) {
                req.dataBuf.writeInt(paramItem.getParamId());
                switch (paramItem.getParamType()) {
                    case ParamItem.PT_UINT8:
                        req.dataBuf.writeByte(1);
                        req.dataBuf.writeByte(parseUnsignedByte(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_UINT16:
                        req.dataBuf.writeByte(2);
                        req.dataBuf.writeShort(parseUnsignedShort(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_UINT32:
                        req.dataBuf.writeByte(4);
                        req.dataBuf.writeInt((int) parseUnsignedInt(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_STR:
                        writeU8String(req.dataBuf, paramItem.getParamVal());
                        break;
                    case ParamItem.PT_BYTES:
                        writeU8Bytes(req.dataBuf, hex2Bytes(paramItem.getParamVal()));
                        break;
                }
            }
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
                logger.warn("设置终端参数成功: " + result.vid);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("设置终端参数失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：查询终端参数
     *
     * @param dnReq
     * @return
     */
    private void do_8104(final DmsContext ctx, final Req_8104 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0104 res = new Res_0104();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8104;
            req.dataBuf = Unpooled.EMPTY_BUFFER; //消息体为空
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        fetchTerminalParams(ctx, req);
    }

    /**
     * 终端控制
     *
     * @param dnReq
     * @return
     */
    private void do_8105(final DmsContext ctx, final Req_8105 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8105;
            req.dataBuf = Unpooled.buffer(32);

            byte commandWord = dnReq.getCommandWord();
            req.dataBuf.writeByte(commandWord);

            if (commandWord == 1 || commandWord == 2) {
                writeString(req.dataBuf, dnReq.getCommandParam());
            }
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("控制终端失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_8106_nd(final DmsContext ctx, final Req_8106_nd dnReq){
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_8106_nd res = new Res_8106_nd();
        try{
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8106;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(1);//参数个数
            req.dataBuf.writeInt(0xF006);
            req.dataBuf.writeZero(16);
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(8);//跳过应答流水号、参数个数、参数ID、参数长度
                res.setIp1(result.dataBuf.readShort());
                result.dataBuf.skipBytes(6);
                res.setIp2(result.dataBuf.readShort());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询IP锁状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }
    /**
     * 查询终端的指定参数
     *
     * @param dnReq
     * @return
     */
    private void do_8106(final DmsContext ctx, final Req_8106 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0104 res = new Res_0104();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8106;
            req.dataBuf = Unpooled.buffer(32);
            int idNum = dnReq.getParamIds().length;
            req.dataBuf.writeByte(idNum);
            for (int paramId : dnReq.getParamIds()) {
                req.dataBuf.writeInt(paramId);
            }
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        fetchTerminalParams(ctx, req);
    }

    /**
     * 断开终端与前置机连接
     * @param ctx
     * @param dnReq
     */
    private void do_disconnectVehiceFromTas(final DmsContext ctx, final Req_DisconnectVehiceFromTas dnReq)
    {
        Res_DisconectVehicleFromTas res = new Res_DisconectVehicleFromTas();
        if (logger.isDebugEnabled()) {
            logger.debug("接收到断开终端与前置机连接请求:{}", dnReq);
        }
        try {

            TcpConnectionManager tcpConnectionManager = getTcpConnectionManager();
            TcpConnection vid2Connection = tcpConnectionManager.getConnectionByVid(dnReq.getVid());
            if(vid2Connection!=null) {
                //tcpConnectionManager.removeConnection(vid2Connection);
                //注释掉上面的移除链接缓存，改为下面这个强制断开链接，断开链接的方法里面会移除相应缓存
                //只是单纯的移除链接缓存，车台不知道自己已经掉线了
                vid2Connection.close();
                res.setRc(JtsResMsg.RC_OK);
                logger.debug("断开终端与前置机连接请求成功:{}", dnReq);
            }else {
                res.setRc(JtsResMsg.RC_FAIL);
                res.setErrMsg("车辆未登录,断开终端与前置机连接失败.");
                logger.debug("车辆未登录:{}", dnReq);
            }
        }catch (Exception e){
            logger.debug("断开终端与前置机连接请求异常:{}", e.getMessage());
            res.setRc(JtsResMsg.RC_FAIL);

        }
        ctx.reply(res);
    }

    private void fetchTerminalParams(final DmsContext ctx, final ProtoMsg req) {
        final Res_0104 res = new Res_0104();
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                res.setRc(JtsResMsg.RC_OK);
                result.dataBuf.skipBytes(2);
                short paramNum = result.dataBuf.readUnsignedByte();//参数个数
                List<ParamItem> paramItems = new ArrayList<ParamItem>(paramNum);
                for (int i = 0; i < paramNum; i++) {
                    ParamItem pi = new ParamItem();
                    pi.setParamId(result.dataBuf.readInt());
                    byte[] paramVal = readU8Bytes(result.dataBuf);
                    pi.setParamVal(bytes2Hex(paramVal)); //不知道参数类型，所以先转换成16进行字符串
                    paramItems.add(pi);
                }
                res.setParamItems(paramItems);
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询终端参数失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(req.vid);
                ctx.reply(res);
            }
        });
    }
}
