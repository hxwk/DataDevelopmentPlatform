package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_0108;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_8108;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Res_0001;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndInstructionResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author SHUAIHONG
 * 远程升级
 */
public class RemoteUpgradeOTAPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(RemoteUpgradeOTAPH.class);


    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId==(short)0x0108){
            do_0108(upMsg, taskId, channelProcessor);
        }else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_8108){   //下发终端升级包
            do_8108((Req_8108) dnReq, taskId, channelProcessor);
        }else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }



    private void do_8108(final Req_8108 dnReq, String taskId, ChannelProcessor channelProcessor) {
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        String str = String.format("下发终端升级包");

        rcp.setInstructionDesc(str);
        rcp.setStatus(0);


        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x8108;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getUpdateType());
            byte ManufacturerId[]=new byte[5];
            ManufacturerId=dnReq.getManufacturerId();
            req.dataBuf.writeBytes(ManufacturerId);

            req.dataBuf.writeByte(dnReq.getVersionLength());

            int n=dnReq.getVersionLength();
            byte version[]=new byte[n];
            version=dnReq.getVersion().getBytes();
            req.dataBuf.writeBytes(version);

            req.dataBuf.writeLong(dnReq.getUpdateDataLength());
            req.dataBuf.writeBytes(dnReq.getUpdateData());
            logger.info("RemoteUpgradeOTAPH 根据8108协议向车机终端发送空消息体:{}", ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }
    }




/*********************************终端响应监控中心***************************************/

private void do_0108(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
    logger.warn("do_0108 start...");
    Req_0108 q = new Req_0108();

    //解析上行请求协议
    try {
        ByteBuf reqBuf = upMsg.dataBuf;

        q.setSim(upMsg.sim);
        q.setVid(upMsg.vid);

        q.setUpdateType(reqBuf.readByte());
        q.setUpdateResult(reqBuf.readByte());

        //存到远程升级响应topic
        processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.REMOTEUPGRADEOTAPH_TOPIC, channelProcessor);

        if (logger.isDebugEnabled()) {
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
        }
        updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    } catch (Exception e) {
        logger.warn("协议解析失败:" + upMsg, e);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
    }
}

    @Override
    public void setup() {

    }
}
