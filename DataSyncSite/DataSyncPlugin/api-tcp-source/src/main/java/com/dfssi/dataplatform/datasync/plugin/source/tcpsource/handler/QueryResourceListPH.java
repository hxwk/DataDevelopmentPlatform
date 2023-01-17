package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.X0200BitParse;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Modify by JIANKANG
 * 新增了1205报文响应解析流程
 */

public class  QueryResourceListPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(CanInformationPH.class);
    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_9205) {//请求资源列表
            do_9205((Req_9205) dnReq, taskId, channelProcessor);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_9205(final Req_9205 dnReq, String taskId, ChannelProcessor channelProcessor){
        byte logicalChannelNum = dnReq.getLogicalChannelNum();
        String startTime = dnReq.getStartTime();
        String endTime = dnReq.getEndTime();
        Long alarmSign = dnReq.getAlarmSign();
        byte audioVideoResourceType = dnReq.getAudioVideoResourceType();
        byte bitStreamType = dnReq.getBitStreamType();
        byte storageType = dnReq.getStorageType();
        String str = String.format("平台向终端请求资源列表:逻辑通道号{"+logicalChannelNum+"},开始时间{"+startTime+"},结束时间{"+endTime+"}," +
                        "报警标志{"+alarmSign+"},音视频资源类型{"+audioVideoResourceType+"},码流类型{"+bitStreamType+"},存储器类型{"+storageType+"}");

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Req_1205 res = new Req_1205();
        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x9205;
            req.dataBuf = Unpooled.buffer(24);
            req.dataBuf.writeByte(dnReq.getLogicalChannelNum());
            req.dataBuf.writeBytes(str2Bcd(dnReq.getStartTime()));
            req.dataBuf.writeBytes(str2Bcd(dnReq.getEndTime()));
            Long alarmSigns = dnReq.getAlarmSign();
            //req.dataBuf.writeLong(alarmSigns);
            req.dataBuf.writeLong(0);
            logger.info("获得-alarmsign:{},alarmsign stringvalue:{}",alarmSigns,alarmSigns.toString());
            byte avResourceType = dnReq.getAudioVideoResourceType();
            req.dataBuf.writeByte(avResourceType);
            logger.info("获得-avResourceType");
            req.dataBuf.writeByte(dnReq.getBitStreamType());
            req.dataBuf.writeByte(dnReq.getStorageType());

            logger.info("QueryResourceListPH 根据9205协议向车机终端发送消息体:{}",ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            //res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x1205);//终端回传1205类型的报文 并封装成对应的class 取到消息后推送到kafka
        logger.info("QueryResourceListPH开始请求资源列表");
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.info("QueryResourceListPH请求资源列表成功");
                if(result.msgId != 0x1205){
                    processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
                    return;
                }
                Date dateStart = null;
                Date dateEnd = null;
                SimpleDateFormat formatter=new SimpleDateFormat("yyMMddHHmmss");
                List<AVResourceListItem> avResourceListItemList = Lists.newArrayList();
                //解析上行请求协议
                try {
                    ByteBuf reqBuf = result.dataBuf;
                    AVResourceListVo avResourceListVo = new AVResourceListVo();

                    AVResourceListItem item ;
                    avResourceListVo.setSim(result.sim);
                    avResourceListVo.setVid(result.vid);

                    avResourceListVo.setFlowNo(reqBuf.readUnsignedShort());
                    avResourceListVo.setAvResourceNum(reqBuf.readInt());

                    while(reqBuf.readableBytes()>=28){
                        item = new AVResourceListItem();
                        item.setChannelNo(reqBuf.readUnsignedByte());
                        byte[] startTime = new byte[6];
                        reqBuf.readBytes(startTime);
                        byte[] endTime = new byte[6];
                        reqBuf.readBytes(endTime);
                        try {
                            dateStart = formatter.parse(ProtoUtil.bcd2Str(startTime));
                            dateEnd = formatter.parse(ProtoUtil.bcd2Str(endTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        item.setStartTime(dateStart.getTime());
                        item.setEndTime(dateEnd.getTime());
                        byte[] alarms = new byte[8];
                        reqBuf.readBytes(alarms);
                        item.setAlarmMark(alarms);
                        item.setAlarmMarks(X0200BitParse.parseAlarm(X0200BitParse.longFrom8Bytes(alarms, 0, true)));
                        item.setAvResourceType(reqBuf.readByte());
                        item.setStreamType(reqBuf.readByte());
                        item.setStorageType(reqBuf.readByte());
                        item.setFileSize(reqBuf.readInt());
                        avResourceListItemList.add(item);
                    }
                    avResourceListVo.setAvResourceListItemList(avResourceListItemList);
                    res.setAvResourceList(avResourceListVo);

                    //存到音视频资源列表topic
                    processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.AVRESOURCELIST_TOPIC, channelProcessor);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}]接收到上行请求消息:{}", result.sim, res);
                    }
                    updateVehicleStatus2Redis(result, taskId,channelProcessor);
                    sendCenterGeneralRes(result, ProtoConstants.RC_OK);
                } catch (Exception e) {
                    logger.warn("协议解析失败:" + result, e);
                    sendCenterGeneralRes(result, ProtoConstants.RC_BAD_REQUEST);
                }
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("QueryResourceListPH请求资源列表失败", t);
                //res.setRc(JtsResMsg.RC_FAIL);
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
}
