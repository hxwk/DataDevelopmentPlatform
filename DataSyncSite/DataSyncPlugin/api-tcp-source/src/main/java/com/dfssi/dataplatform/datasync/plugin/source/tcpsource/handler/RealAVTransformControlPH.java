package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_9101_nd;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_9102_nd;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Res_0001;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndInstructionResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JIANKANG
 * 实时音视频处理控制和传输指令逻辑
 */
public class RealAVTransformControlPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(RealAVTransformControlPH.class);

    @Override
    public void setup() {

    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_9101_nd) {//传输
            do_9101_nd((Req_9101_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_9102_nd) {//控制
            do_9102_nd((Req_9102_nd)dnReq,taskId,channelProcessor);
        }else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId == (short)0xF101) {//
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    private void do_9101_nd(final Req_9101_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        String sim = dnReq.getSim();
        byte ipLength = dnReq.getIpLength();
        String ip = dnReq.getIp();
        short tcpPort = dnReq.getTcpPort();
        short udpPort = dnReq.getUdpPort();
        byte channelNo = dnReq.getChannelNo();
        byte dataType = dnReq.getDataType();
        byte streamType = dnReq.getStreamType();
        String str = String.format("实时音视频传输:sim:%s,ip长度:%d,服务器IP:%s,TCP端口:%x,UDP端口:%x,通道号:%x,数据类型:%x,码流类型:%x",
                sim,ipLength,ip,tcpPort,udpPort,channelNo,dataType,streamType);

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        //rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.info("协议处理器接收到下行请求:{}", dnReq);
        final ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.vid = dnReq.getVid();
            req.sim = sim;
            req.msgId = (short) 0x9101;
            req.dataBuf = Unpooled.buffer(28);
            req.dataBuf.writeByte(ipLength);
            byte[] ips = ip.getBytes();
            req.dataBuf.writeBytes(ips);
            req.dataBuf.writeShort(tcpPort);
            req.dataBuf.writeShort(udpPort);
            req.dataBuf.writeByte(channelNo);
            req.dataBuf.writeByte(dataType);
            req.dataBuf.writeByte(streamType);
            logger.info("ipLength:{},ip's bytes:{},tcpPort:{},udpPort:{},channelNo:{},dataType:{}",
                    ipLength,ips,tcpPort,udpPort,channelNo,dataType,streamType);
            //logger.warn("encode 9101 protocol:{}",ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.info("实时音视频传输设置成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("实时音视频传输设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }

    private void do_9102_nd(final Req_9102_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        byte channelNo = dnReq.getChannelNo();
        byte controlCommand = dnReq.getControlCommand();
        byte shutDownAVType = dnReq.getShutDownAVType();
        byte streamType = dnReq.getStreamType();
        String str = String.format("实时音视频控制:通道号%d,控制命令%d,关闭音视频类型%d,码流类型%d",
                channelNo,controlCommand,shutDownAVType,streamType);

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        //rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x9102;
            req.dataBuf = Unpooled.buffer(4);
            req.dataBuf.writeByte(channelNo);
            req.dataBuf.writeInt(controlCommand);
            req.dataBuf.writeShort(shutDownAVType);
            req.dataBuf.writeByte(streamType);
            logger.info("channelNo:{},controlCommand:{},shutDownAVType:{},streamType:{}",
                    channelNo,controlCommand,shutDownAVType,streamType);
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x9102);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("实时音视频传输设置成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
            @Override
            public void onFailure(Throwable t) {
                logger.warn("实时音视频传输设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }
}
