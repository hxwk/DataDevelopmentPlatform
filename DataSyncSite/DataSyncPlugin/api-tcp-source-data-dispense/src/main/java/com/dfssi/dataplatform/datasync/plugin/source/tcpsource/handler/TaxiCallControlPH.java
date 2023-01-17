package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.google.common.base.Throwables;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.*;


/**
 * 电召协议处理
 */
public class TaxiCallControlPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaxiCallControlPH.class);

    @Override
    public void setup() {

    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
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
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

        if (dnReq instanceof Req_8B00) {
            do_8B00((Req_8B00) dnReq);
        }else if(dnReq instanceof Req_8B01){
            do_8B01((Req_8B01) dnReq);
        }else if(dnReq instanceof Req_8B09){
            do_8B09((Req_8B09) dnReq);
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
    private void do_8B00(final Req_8B00 dnReq) {
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
            return;
        }

        //发送车台处理，并添加回调
//        sendMessage(req);
        //putTcpFuture(ctx,req,res,"下发召车指令");
    }
    /**
     * 下行协议：下发乘客电召信息
     *
     * @param dnReq
     * @return
     */
    private void do_8B01(final Req_8B01 dnReq) {
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
            return;
        }

        //发送车台处理，并添加回调
//        sendMessage(req);
        //putTcpFuture(ctx,req,res,"下发乘客电召信息");
    }
    /**
     * 下行协议：乘客取消召车
     *
     * @param dnReq
     * @return
     */
    private void do_8B09(final Req_8B09 dnReq) {
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
            return;
        }

        //发送车台处理，并添加回调
//        sendMessage(req);
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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

}
