package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;
import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readString;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;


public class  RemoteVideoPlaybackControlPH extends BaseProtoHandler {
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
        if (dnReq instanceof Req_9202) {//请求资源列表
            do_9202((Req_9202) dnReq, taskId, channelProcessor);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_9202(final Req_9202 dnReq, String taskId, ChannelProcessor channelProcessor){
        byte AudioVideoChannelNum = dnReq.getAudioVideoChannelNum();
        byte playbackControl = dnReq.getPlaybackControl();
        byte fastForwardOrBackMultiple = dnReq.getFastForwardOrBackMultiple();
        String dragPlaybackPosition = dnReq.getDragPlaybackPosition();
        String str = String.format("下发远程录像回放控制:音视频通道号{"+AudioVideoChannelNum+"},回放控制{"+playbackControl+"},快进或快退倍数{"+fastForwardOrBackMultiple+"},拖动回放位置{"+dragPlaybackPosition+"}");

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
            req.msgId = (short) 0x9202;
            req.dataBuf = Unpooled.buffer(9);
            req.dataBuf.writeByte(dnReq.getAudioVideoChannelNum());
            req.dataBuf.writeByte(dnReq.getPlaybackControl());
            req.dataBuf.writeByte(dnReq.getFastForwardOrBackMultiple());
            if(String.valueOf(playbackControl).equals("5")){
                req.dataBuf.writeBytes(str2Bcd(dnReq.getDragPlaybackPosition()));
            }

            logger.info("RemoteVideoPlaybackControlPH根据9202协议向车机终端发送报文:{}",ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x1205);//终端回传1205类型的报文 并封装成对应的class 取到消息后推送到kafka
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.info("下发远程录像回放控制成功");
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("下发远程录像回放控制失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }

    @Override
    public void setup() {

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

    public static void main(String[] args) {
        Req_9202 req_9202 = new Req_9202();
        System.out.println(req_9202);

    }
}
