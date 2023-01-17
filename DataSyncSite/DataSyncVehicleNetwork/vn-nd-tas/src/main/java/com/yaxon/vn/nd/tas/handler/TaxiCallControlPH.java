package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.writeString;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.writeTime;

/**
 * Author: 赖贵明
 * Time: 2014-08-29 09:19
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 电召协议处理
 */
@Component
public class TaxiCallControlPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaxiCallControlPH.class);

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
       if (upMsg.msgId == 0x0B01) {//驾驶员抢答
            do_0B01(upMsg);
        } else if (upMsg.msgId == 0x0B07) {//驾驶员完成电召任务
            do_0B07(upMsg);
        } else if (upMsg.msgId == 0x0B08) {//驾驶员取消电召任务
            do_0B08(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {

        if (dnReq instanceof Req_8B00) {
            do_8B00(ctx, (Req_8B00) dnReq);
        }else if(dnReq instanceof Req_8B01){
            do_8B01(ctx, (Req_8B01) dnReq);
        }else if(dnReq instanceof Req_8B09){
            do_8B09(ctx, (Req_8B09) dnReq);
        }  else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    /**
     * 下行协议：下发召车指令
     *
     * @param dnReq
     * @return
     */
    private void do_8B00(final DmsContext ctx, final Req_8B00 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8B00;
            req.dataBuf = Unpooled.buffer(32);
            int id=dnReq.getBusinessId();
            req.dataBuf.writeInt(id);
            req.dataBuf.writeByte(dnReq.getBusinessType());
            //要车时间
            writeTime(req.dataBuf,dnReq.getTime());
            //描述 (写字符串)
            writeString(req.dataBuf, dnReq.getDesc());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        //发送车台处理，并添加回调
        sendMessage(req);
        //putTcpFuture(ctx,req,res,"下发召车指令");
    }
    /**
     * 下行协议：下发乘客电召信息
     *
     * @param dnReq
     * @return
     */
    private void do_8B01(final DmsContext ctx, final Req_8B01 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8B01;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeInt(dnReq.getBusinessId());
            req.dataBuf.writeByte(dnReq.getBusinessType());
            //联系电话
            writeString(req.dataBuf, dnReq.getTel());
            //描述 (写字符串)
            writeString(req.dataBuf, dnReq.getDesc());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        //发送车台处理，并添加回调
        sendMessage(req);
        //putTcpFuture(ctx,req,res,"下发乘客电召信息");
    }
    /**
     * 下行协议：乘客取消召车
     *
     * @param dnReq
     * @return
     */
    private void do_8B09(final DmsContext ctx, final Req_8B09 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8B09;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeInt(dnReq.getBusinessId());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        //发送车台处理，并添加回调
        sendMessage(req);
       // putTcpFuture(ctx,req,res,"乘客取消召车");
    }

    /**
     * 上行协议：驾驶员抢答消息
      * @param upMsg
     */
    private void do_0B01(final ProtoMsg upMsg) {
        Req_0B01 q = new Req_0B01();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            Integer businessId=reqBuf.readInt();

            q.setBusinessId(businessId);
            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //tbp处理，添加回调处理
        putTbpFuture(upMsg,q);

        return;
    }
    /**
     * 上行协议：驾驶员任务完成确认消息
     * @param upMsg
     */
    private void do_0B07(final ProtoMsg upMsg) {
        Req_0B07 q = new Req_0B07();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            Integer businessId=reqBuf.readInt();

            q.setBusinessId(businessId);
            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //tbp处理，添加回调处理
        putTbpFuture(upMsg,q);

        return;
    }
    /**
     * 上行协议：驾驶员任务完成确认消息
     * @param upMsg
     */
    private void do_0B08(final ProtoMsg upMsg) {
        Req_0B08 q = new Req_0B08();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            Integer businessId=reqBuf.readInt();
            Byte type=reqBuf.readByte();

            q.setBusinessId(businessId);
            q.setType(type);//取消原因
            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //tbp处理，添加回调处理
        putTbpFuture(upMsg,q);

        return;
    }

    /**
     * 上行协议处理：tbp处理，添加回调处理
     * @param upMsg
     * @param q
     */
    public void putTbpFuture(final ProtoMsg upMsg,JtsReqMsg q){
        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        /*//异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
                sendCenterGeneralRes(upMsg, r.getRc());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });*/
    }

    /**
     * 下行协议处理：发送车台处理，添加回调
     * @param ctx
     * @param req
     * @param res
     * @param text
     */
    public void putTcpFuture(final DmsContext ctx,ProtoMsg req,final JtsResMsg res,final String text){
        //发送车台处理
        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        //添加回调处理
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
                logger.warn(text+"失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                ctx.reply(res);
            }
        });
    }
}
