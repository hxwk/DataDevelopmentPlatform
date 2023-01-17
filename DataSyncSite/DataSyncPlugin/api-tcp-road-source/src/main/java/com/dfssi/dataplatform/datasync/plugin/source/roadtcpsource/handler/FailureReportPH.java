package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.road.entity.FailureCodeBean;
import com.dfssi.dataplatform.datasync.model.road.entity.FailureItem;
import com.dfssi.dataplatform.datasync.model.road.entity.Req_D001;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.ProcessKafka;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.X0200BitParse;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.FailureCodeDTO;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.FailureCodeUtils.getFailureCode;
import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ProtoUtil.readTime;

/**
 * Description
 * D760故障解析
 * @author JIANKANG
 * @version 2018/9/19 14:19
 */
public class FailureReportPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(FailureReportPH.class);

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if(upMsg.msgId == (short)0xD001){
            do_D001(upMsg,taskId,channelProcessor);
        } else {
            throw new UnsupportedOperationException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

    }

    @Override
    public void setup() {

    }

    private void do_D001(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.info("开始进行D001故障报文解析，sim:"+upMsg.sim);
        Req_D001 q = new Req_D001();
        FailureCodeBean failureCodeBean = new FailureCodeBean();
        FailureItem item ;
        StringBuilder builder;
        List<FailureItem> failureItems = Lists.newArrayList();
        try {
            ByteBuf buf = upMsg.dataBuf;
            q.setSim(upMsg.sim);
            failureCodeBean.setId(UUID.randomUUID().toString());
            failureCodeBean.setSim(upMsg.sim);
            failureCodeBean.setMsgId("D001");
            failureCodeBean.setVid(upMsg.vid);
            while (buf.readableBytes() >= 29) {
                long alarm = buf.readInt();
                failureCodeBean.setAlarm(alarm);
                failureCodeBean.setAlarms(X0200BitParse.parseAlarm(alarm));
                failureCodeBean.setStatus(buf.readInt());
                failureCodeBean.setLatitude(buf.readInt());
                failureCodeBean.setLongitude(buf.readInt());
                failureCodeBean.setHeight(buf.readUnsignedShort());
                failureCodeBean.setSpeed(buf.readUnsignedShort());
                failureCodeBean.setDir(buf.readUnsignedShort());
                Date date = readTime(buf.readBytes(6));
                failureCodeBean.setTime(date.getTime());
                short failureCodeNum = buf.readByte();
                failureCodeBean.setFailureNum(failureCodeNum);
                while (failureCodeNum-- > 0) {
                    item =new FailureItem();
                    builder = new StringBuilder();
                    //source address =>ID
                    item.setSa(buf.readByte());
                    item.setFailureNum(buf.readByte());
                    //two byte insert into SPN
                    int twoByte = buf.readUnsignedShortLE();
                    String twoByteStr = String.format("%04x",twoByte);
                    short thirdByte = buf.readUnsignedByte();
                    //third byte insert into spn
                    String thirdSpn = String.format("%02x",(thirdByte & 0xE0)>>>5);
                    builder.append(thirdSpn);
                    builder.append(twoByteStr);
                    int fmi = thirdByte & 0x1F;
                    int value = Integer.valueOf(builder.toString(),16);
                    item.setSpn(value);
                    item.setFmi(fmi);
                    short cmOroc = buf.readUnsignedByte();
                    int spnCm = (cmOroc & 0x80)>>>7;
                    int oc = cmOroc & 0x7F;
                    FailureCodeDTO fcdto = getFailureCode(String.valueOf(item.getSpn()),
                            String.valueOf(item.getFmi()),"0x"+String.format("%02x",item.getSa()));
                    item.setSysCategory(fcdto.getSysCategory());
                    item.setModel(fcdto.getModel());
                    item.setDesc(fcdto.getDesc());
                    item.setDiagnosticCode(fcdto.getDiagnosticCode());
                    item.setFailureGrade(Integer.valueOf(fcdto.getFailureGrade()));
                    item.setFailureUnit(fcdto.getFailureUnit());
                    item.setSpnCm(spnCm);
                    item.setOc(oc);
                    failureItems.add(item);
                }
                failureCodeBean.setFailureItemList(failureItems);
                q.setFailureCodeBean(failureCodeBean);
                //logger.info("q:{}",q.getFailureCodeBean().toString());
                logger.info("解析D001报文完毕，推送至kafkaChannel，sim：{}",upMsg.sim);
            }
            ProcessKafka.processEvent(JSON.toJSONString(failureCodeBean), taskId, q.id(), Constants.FAILUREREPORT_TOPIC, channelProcessor);
        }catch (Exception ex){
            logger.error("upMsg:{} protocol parse error:{}",upMsg, ex);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        //updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
    }
}
