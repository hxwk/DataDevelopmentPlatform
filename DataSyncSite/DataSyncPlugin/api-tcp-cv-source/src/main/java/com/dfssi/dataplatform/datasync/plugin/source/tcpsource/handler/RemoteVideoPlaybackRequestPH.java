package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_9201;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Res_0001;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndInstructionResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class  RemoteVideoPlaybackRequestPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(CanInformationPH.class);
    //    static String dbcFormat = CanDBCFormat.OLD.name();
    private static Properties prop;
    private static String dbcFastDFSFileId ;
    private ConcurrentHashMap<String, String> vid2fileid = new ConcurrentHashMap();


    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_9201) {//请求资源列表
            do_9201((Req_9201) dnReq, taskId, channelProcessor);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_9201(final Req_9201 dnReq, String taskId, ChannelProcessor channelProcessor){
        byte serverIPAddressLength = dnReq.getServerIPAddressLength();
        String serverIPAddress = dnReq.getServerIPAddress();
        short serverAudioVideoMonitorPortTCP = dnReq.getServerAudioVideoMonitorPortTCP();
        short serverAudioVideoMonitorPortUDP = dnReq.getServerAudioVideoMonitorPortUDP();
        byte logicalChannelNum = dnReq.getLogicalChannelNum();
        byte audioVideoResourceType = dnReq.getAudioVideoResourceType();
        byte bitStreamType = dnReq.getBitStreamType();
        byte storageType = dnReq.getStorageType();
        byte refluxMode = dnReq.getRefluxMode();
        byte fastForwardOrBackMultiple = dnReq.getFastForwardOrBackMultiple();
        String startTime = dnReq.getStartTime();
        String endTime = dnReq.getEndTime();

        String str = String.format("下发远程录像回放请求:服务器IP地址长度{"+serverIPAddressLength+"},服务器IP地址{"+serverIPAddress+"}," +
                        "服务器音视频通道监听端口号TCP{"+serverAudioVideoMonitorPortTCP+"},服务器音视频通道监听端口号UDP{"+serverAudioVideoMonitorPortUDP+"}," +
                         "逻辑通道号{"+logicalChannelNum+"}" +
                        "音视频类型{"+audioVideoResourceType+"},码流类型{"+bitStreamType+"}," +
                        "存储器类型{"+storageType+"},回流方式{"+refluxMode+"},快进或快退倍数{"+fastForwardOrBackMultiple+"}" +
                        ",开始时间{"+startTime+"},结束时间{"+endTime+"}");

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x9201;
            int length = byteToInt(dnReq.getServerIPAddressLength());
            logger.info("获得9201指令中ip的长度："+length+",将其加23，设置为dataBuf的长度");
            req.dataBuf = Unpooled.buffer(23+length);
            req.dataBuf.writeByte(dnReq.getServerIPAddressLength());
            req.dataBuf.writeBytes(dnReq.getServerIPAddress().getBytes());
            req.dataBuf.writeShort(dnReq.getServerAudioVideoMonitorPortTCP());
            req.dataBuf.writeShort(dnReq.getServerAudioVideoMonitorPortUDP());
            req.dataBuf.writeByte(dnReq.getLogicalChannelNum());
            req.dataBuf.writeByte(dnReq.getAudioVideoResourceType());
            req.dataBuf.writeByte(dnReq.getBitStreamType());
            req.dataBuf.writeByte(dnReq.getStorageType());
            req.dataBuf.writeByte(dnReq.getRefluxMode());
            req.dataBuf.writeByte(dnReq.getFastForwardOrBackMultiple());
            req.dataBuf.writeBytes(str2Bcd(dnReq.getStartTime()));
            req.dataBuf.writeBytes(str2Bcd(dnReq.getEndTime()));
            logger.info("RemoteVideoPlaybackRequestPH根据9201协议向车机终端发送报文:{}",ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x1205);//终端回传1205类型的报文 并封装成对应的class 取到消息后推送到kafka
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.info("平台下发远程录像回放请求成功");
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("平台下发远程录像回放请求失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }



    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static int byteToInt(byte b) {
//Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    @Override
    public void setup() {

    }
}
