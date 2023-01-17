package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.io.BaseEncoding;
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

import java.util.ArrayList;
import java.util.List;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.*;

/**
 * Author: Sun Zhen
 * Time: 2014-01-20 15:54
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class OtherProtocolPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(OtherProtocolPH.class);
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0900) {//数据上行透传
            do_0900(upMsg);
        } else if (upMsg.msgId == 0x0901) {//数据压缩上报
            do_0901(upMsg);
        } else if (upMsg.msgId == 0x0A00) {//终端RSA公钥
            do_0A00(upMsg);
        } else if (upMsg.msgId == 0x0B10) {//LED屏终端上报
            do_0B10(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    private void do_0A00(final ProtoMsg upMsg) {
        Req_0A00 q = new Req_0A00();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;

            q.setE(reqBuf.readUnsignedInt());
            q.setN(readString(reqBuf, 128));

            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.error("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0B10(final ProtoMsg upMsg) {
        Req_0B10 q = new Req_0B10();

        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setTypeId(reqBuf.readByte());
            q.setManufacturerMark(reqBuf.readByte());
            q.setCmdType(reqBuf.readShort());
            if (reqBuf.readableBytes() > 0) {
                byte[] data = new byte[reqBuf.readableBytes()];
                reqBuf.readBytes(data);
                q.setData(data);
                System.out.println("响应:" + hex.encode(data));
                q.setVid(upMsg.vid);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.error("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }


    private void do_0901(final ProtoMsg upMsg) {
        Req_0901 q = new Req_0901();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;

            //压缩消息体

            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.error("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0900(final ProtoMsg upMsg) {
        Req_0900 q = new Req_0900();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            byte type = reqBuf.readByte();
            q.setTransparentTransmitMsgType(type);
           /* if (type == 0xF2) {
                q.setHitchCodeType(reqBuf.readShort());
                reqBuf.skipBytes(4);
                q.setHitchCodeTime(readTime(reqBuf));
                q.setIndicatorLight1(reqBuf.readByte());
                q.setIndicatorLight2(reqBuf.readByte());

                byte num = reqBuf.readByte();
                List<HitchCodeItem> hitchCodeItems = new ArrayList<HitchCodeItem>();
                for (int i = 0; i < num; i++) {
                    HitchCodeItem hitchCodeItem = new HitchCodeItem();
                    hitchCodeItem.setSpn(reqBuf.readMedium());
                    hitchCodeItem.setFmi(reqBuf.readByte());
                    hitchCodeItem.setOc(reqBuf.readByte());
                    hitchCodeItems.add(hitchCodeItem);
                }
                q.setHitchCodeItems(hitchCodeItems);
            }*/
            if (reqBuf.readableBytes() > 0) {
                byte[] content = new byte[reqBuf.readableBytes()];
                reqBuf.readBytes(content);
                q.setContent(content);
            }

            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8900) {//数据下行透传
            do_8900(ctx, (Req_8900) dnReq);
        } else if (dnReq instanceof Req_8B10) {//数据下行透传LED显示屏
            do_8B10(ctx, (Req_8B10) dnReq);
        } else if (dnReq instanceof Req_8A00) {//平台RSA公钥
            do_8A00(ctx, (Req_8A00) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }


    private void do_8A00(final DmsContext ctx, final Req_8A00 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8A00;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeInt(dnReq.getE().intValue());
            writeString(req.dataBuf, dnReq.getN());

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
                logger.warn("平台RSA公钥失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_8900(final DmsContext ctx, final Req_8900 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8900;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getTransparentTransmitMsgType());
            req.dataBuf.writeBytes(dnReq.getContent());

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
                logger.warn("数据下行透传失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_8B10(final DmsContext ctx, final Req_8B10 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8B10;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getTypeId());
            req.dataBuf.writeShort(dnReq.getDataType());
            req.dataBuf.writeBytes(dnReq.getData());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }
        sendMessage(req);
        //TODO: 要给应答吗?
    }
}
