package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.ByteBufUtil;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.readString;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTime;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-24 18:02
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class LockVehicleControlPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(LockVehicleControlPH.class);
    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_F001_nd) {//设置锁车参数
            do_F001_nd(ctx, (Req_F001_nd) dnReq);
        } else if(dnReq instanceof Req_F003_nd){//查询锁车各个状态
            do_F003_nd(ctx,(Req_F003_nd) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        //ID超出了Java short型的范围，上报的时候强转，变成了负数，这边也强转才能对应上
        if (upMsg.msgId == (short)0xF101) {//锁车控制的结果上报。
            do_F101(upMsg);
        } else if(upMsg.msgId == (short) 0xF002){//锁车心跳
            do_F002(upMsg);
        } else if(upMsg.msgId == (short) 0xF102){//超时上报
            do_F102(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    private void do_F101(final ProtoMsg upMsg) {
        final Req_F101_nd req = new Req_F101_nd();
        try{
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            req.setInstruction((int)reqBuf.readByte());
            reqBuf.skipBytes(3);//跳过3个预留字节
            req.setResult((int)reqBuf.readShort());//控制命令执行结果
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, req);
        }catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(req);

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
    }

    private void do_F002(final ProtoMsg upMsg) {
        logger.debug(" 1 接收到锁车心跳:{}", upMsg);

        final Req_F002_nd req = new Req_F002_nd();
        try{
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            byte byte1 = reqBuf.readByte();
            String byte1String = ByteBufUtil.byte2String(byte1);
            logger.info("2 接收到锁车心跳:byte1 = {}, byte1String = {}", byte1, byte1String);
            req.setOnBitchSign(Integer.parseInt(byte1String.substring(7), 2));
            req.setAccBitchSign(Integer.parseInt(byte1String.substring(6, 7), 2));
            req.setOffLineLockVehicleSign(Integer.parseInt(byte1String.substring(5, 6), 2));
            req.setWriteVDR_IDSign(Integer.parseInt(byte1String.substring(4, 5), 2));
            req.setOpenAuthSign(Integer.parseInt(byte1String.substring(3, 4), 2));
            req.setCloseAuthSign(Integer.parseInt(byte1String.substring(2, 3), 2));
            req.setICHeartBeatSign(Integer.parseInt(byte1String.substring(0, 2), 2));
            byte byte2 = reqBuf.readByte();
            String byte2String = ByteBufUtil.byte2String(byte2);
            logger.info("2 接收到锁车心跳:byte2 = {}, byte2String = {}", byte2, byte2String);
            req.setLimpStatusSign(Integer.parseInt(byte2String.substring(4), 2));
            req.setProcessMonitorSign(Integer.parseInt(byte2String.substring(3, 4), 2));
            req.setOfflineMonitorSign(Integer.parseInt(byte2String.substring(2, 3), 2));
            req.setRealLimpStatusSign(Integer.parseInt(byte2String.substring(0, 2), 2));
            byte byte3 = reqBuf.readByte();
            String byte3String = ByteBufUtil.byte2String(byte3);
            logger.info("2 接收到锁车心跳:byte3 = {}, byte3String = {}", byte3, byte3String);
            req.setVehicleBusinessStatus(Integer.parseInt(byte3String.substring(4), 2));
            req.setICBreakSign(Integer.parseInt(byte3String.substring(3, 4), 2));
            req.setEecuSign(Integer.parseInt(byte3String.substring(2, 3), 2));
            req.setLimpCommand(Integer.parseInt(byte3String.substring(1, 2), 2));
            req.setMeterBreakSign(Integer.parseInt(byte3String.substring(0, 1), 2));
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, req);
        }catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(req);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
                logger.debug("锁车心跳应答：" + r);
                sendCenterGeneralRes(upMsg, (byte)0xDF);
            }
            @Override
            public void onFailure(Throwable t) {
                logger.warn("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
    }

    private void do_F102(final ProtoMsg upMsg) {
        logger.debug("接收到超时上报:{}", upMsg);
        //暂时没什么业务逻辑，收到南斗定义的超时上报就直接回复通用应答
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    }

    private void do_F001_nd(final DmsContext ctx,final Req_F001_nd dnReq){
        logger.debug("接收到下行请求:{}", dnReq);
        final ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF001;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getInstruction());
            if(dnReq.getInstruction() != 0x09){
                req.dataBuf.writeByte(dnReq.getBusiness());
                req.dataBuf.writeByte(0);
            }else {
                req.dataBuf.writeByte(dnReq.getTsc1());
                req.dataBuf.writeByte(dnReq.getLimpStatus());
                //目前只要命令字是0x09，这里就只有0x03一种情况，其他情况的下发什么值还没定义
                //后面南斗定义了其他情况的下发值后，这边在添加
                if(dnReq.getLimpStatus() == 0x03){
                    req.dataBuf.writeByte(dnReq.getSpeedOfRevolution()&0x00FF);//转速 低字节
                    req.dataBuf.writeByte((dnReq.getSpeedOfRevolution()&0xFF00)>>8);//转速 高字节
                    req.dataBuf.writeByte(dnReq.getTorque());//扭矩
                }
                req.dataBuf.writeByte(0xFF);
                req.dataBuf.writeByte(0xFF);
            }
        }catch (Exception e) {
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
                logger.warn("锁车设置成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("锁车设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_F003_nd(final DmsContext ctx,final Req_F003_nd dnReq){
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_F003_nd res = new Res_F003_nd();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF003;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getInstruction());

            req.dataBuf.writeInt(0xFFFF);
            req.dataBuf.writeShort(0xFFFF);
            req.dataBuf.writeByte(0xFFFF);
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0xF103);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                ByteBuf dataBuf = result.dataBuf;
                res.setVid(result.vid);
                dataBuf.skipBytes(2);//跳过应答流水号
                res.setInstruction((int)dataBuf.readByte());
                res.setRespTime(readTime(dataBuf.readBytes(6)));
                res.setRc(dataBuf.readByte());
                //心跳状态结构表第一个字节 转为8位的二进制字符串list
                List<String> byte1 = get8bitBinaryStringList(dataBuf.readByte());
                res.setOn(Byte.parseByte(byte1.get(7),2));
                res.setAcc(Byte.parseByte(byte1.get(6),2));
                res.setNotOnlineLock(Byte.parseByte(byte1.get(5),2));
                res.setWriteVdrId(Byte.parseByte(byte1.get(4),2));
                res.setStartupAuthentication(Byte.parseByte(byte1.get(3),2));
                res.setFunctionShutdown(Byte.parseByte(byte1.get(2),2));
                res.setIcHeartbeat(Byte.parseByte(byte1.get(0)+byte1.get(1),2));
                //心跳状态结构表第二个字节 转为8位的二进制字符串list
                List<String> byte2 = get8bitBinaryStringList(dataBuf.readByte());
                res.setLimpStatus(Byte.parseByte(byte2.get(4)+byte2.get(5)+byte2.get(6)+byte2.get(7),2));
                res.setProcessMonitorSwitch(Byte.parseByte(byte2.get(3),2));
                res.setNotOnlineMonitorSwitch(Byte.parseByte(byte2.get(2),2));
                res.setEecuLimpStatus(Byte.parseByte(byte2.get(0)+byte2.get(1),2));
                //心跳状态结构表第三个字节 转为8位的二进制字符串list
                List<String> byte3 = get8bitBinaryStringList(dataBuf.readByte());
                res.setVehicleBusiness(Byte.parseByte(byte3.get(4)+byte3.get(5)+byte3.get(6)+byte3.get(7),2));
                res.setIcFault(Byte.parseByte(byte3.get(3),2));
                res.setVdrEecuMessage(Byte.parseByte(byte3.get(2),2));
                res.setLimp(Byte.parseByte(byte3.get(1),2));
                res.setMeterBusiness(Byte.parseByte(byte3.get(0),2));
                //查询结果的第四字节
                List<String> byte4 = get8bitBinaryStringList(dataBuf.readByte());
                res.setCanOff(Byte.parseByte(byte4.get(7),2));
                res.setCanContinuity(Byte.parseByte(byte4.get(6),2));
                //
                res.setVdrId(readString(dataBuf,7));
                res.setVin(readString(dataBuf,8));
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询锁车状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private List<String> get8bitBinaryStringList(byte b){
        List<String> stringList = new ArrayList<>();
        String s =  Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
        char[] charArr = s.toCharArray();
        for(char c:charArr){
            stringList.add(String.valueOf(c));
        }
        return stringList;
    }
}
