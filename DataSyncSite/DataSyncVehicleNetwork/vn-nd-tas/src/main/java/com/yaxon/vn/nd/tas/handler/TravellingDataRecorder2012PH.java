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

/**
 * Author: 杨俊辉
 * Time: 2014-08-29 09:19
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class TravellingDataRecorder2012PH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TravellingDataRecorder2012PH.class);

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0700) {//行驶记录仪数据上传（12部标协议）
            do_0700(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {

        if (dnReq instanceof Req_8700_2012) {
            do_8700_2012(ctx, (Req_8700_2012) dnReq);
        } else if (dnReq instanceof Req_8701_2012) {
            do_8701_2012(ctx, (Req_8701_2012) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }


    private void do_0700(final ProtoMsg upMsg) {
        Req_0700_2012 q = new Req_0700_2012();
        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            if (reqBuf.readableBytes() > 0) {
                byte[] data = new byte[reqBuf.readableBytes()];
                reqBuf.readBytes(data);
                q.setData(data);
                q.setVid(upMsg.vid);
                //logger.info("do_0700的数据为:{}",q.getData());
            }
//            if (logger.isDebugEnabled()) {
//                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
//            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
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

    /**
     * 下行协议：行驶记录数据采集
     *
     * @param dnReq
     * @return
     */
    private void do_8700_2012(final DmsContext ctx, final Req_8700_2012 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8700;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeBytes(dnReq.getData());
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
                logger.info("行驶记录仪2012响应成功");
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("行驶记录数据采集失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：行驶记录参数下传命令
     *
     * @param dnReq
     * @return
     */
    private void do_8701_2012(final DmsContext ctx, final Req_8701_2012 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8701;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeBytes(dnReq.getData());
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
                logger.warn("行驶记录参数下传失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }


}
