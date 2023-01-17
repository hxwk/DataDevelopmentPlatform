package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.google.common.base.Throwables;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readString;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.writeString;

public class OtherProtocolPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(OtherProtocolPH.class);
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    @Override
    public void setup() {

    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

    private void do_0900(final ProtoMsg upMsg) {
        Req_0900 q = new Req_0900();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            byte type = reqBuf.readByte();
            q.setTransparentTransmitMsgType(type);
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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8900) {//数据下行透传
            do_8900((Req_8900) dnReq);
        } else if (dnReq instanceof Req_8B10) {//数据下行透传LED显示屏
            do_8B10((Req_8B10) dnReq);
        } else if (dnReq instanceof Req_8A00) {//平台RSA公钥
            do_8A00((Req_8A00) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }


    private void do_8A00(final Req_8A00 dnReq) {
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
            return;
        }

    }

    private void do_8900(final Req_8900 dnReq) {
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
            return;
        }

    }

    private void do_8B10(final Req_8B10 dnReq) {
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
            return;
        }
    }
}
