package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Author: 孙震
 * Time: 2014-02-25 10:35
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class TravellingDataRecorder2003PH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TravellingDataRecorder2003PH.class);

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {

    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8700_V03) {//行驶记录数据采集
            do_8700_03(ctx, (Req_8700_V03) dnReq);
        } else if (dnReq instanceof Req_8701) {//行驶记录参数下传命令
            do_8701_03(ctx, (Req_8701_V03) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_8700_03(final DmsContext ctx, final Req_8700_V03 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0700_V03 res = new Res_0700_V03();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8700;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getCmd());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0700);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(2);
                res.setCmd(result.dataBuf.readByte());
                int n = result.dataBuf.readableBytes();
                byte[] data = new byte[n];
                result.dataBuf.readBytes(data);
                res.setData(data);
                res.setRc(JtsResMsg.RC_OK);

                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("发送行驶记录数据采集命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_8701_03(final DmsContext ctx, final Req_8701_V03 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8700;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getCmd());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0700);
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
