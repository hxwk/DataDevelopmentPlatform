package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.GpsAndCanDataPkg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.GpsAndCanDataStructure;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.Req_D004;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.SecondDataPkg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.D004DataParse;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;

/**
 * @author JianKang
 * @date 2018/5/29
 * @description
 */
public class PositionAndCanPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(PositionAndCanPH.class);
    private static final byte lowByte = 0x0F;
    @Override
    public void setup() {

    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if(upMsg.msgId == -12284){//D004 实时数据上报 Gps位置和Can数据
            do_D004(upMsg,taskId,channelProcessor);
        }
    }

    private void do_D004(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        Req_D004 q = new Req_D004();
        GpsAndCanDataPkg gpsAndCanDataPkg;
        List<GpsAndCanDataPkg> gpsAndCanDataPkgs = Lists.newArrayList();
        SecondDataPkg seconDataPkg;
        List<SecondDataPkg> secondDataPgks;
        try{
            ByteBuf reqBuf = upMsg.dataBuf;
            GpsAndCanDataStructure gpsAndCan = new GpsAndCanDataStructure();
            gpsAndCan.setId(UUID.randomUUID().toString());
            gpsAndCan.setSim(upMsg.sim);
            gpsAndCan.setVid(upMsg.vid);
            gpsAndCan.setReceiveTime(System.currentTimeMillis());
            gpsAndCan.setMsgId("D004");
            gpsAndCan.setVersion(reqBuf.readUnsignedByte());
            int primaryPkgNum = reqBuf.readUnsignedByte();
            gpsAndCan.setPrimaryPkgNum(primaryPkgNum);
            //read data package
            while(reqBuf.readableBytes()>0 && primaryPkgNum-->0){
                gpsAndCanDataPkg = new GpsAndCanDataPkg();
                gpsAndCanDataPkg.setLatitude(reqBuf.readUnsignedInt());
                gpsAndCanDataPkg.setLongitute(reqBuf.readUnsignedInt());
                gpsAndCanDataPkg.setHeight(reqBuf.readUnsignedShort());
                gpsAndCanDataPkg.setDirection(reqBuf.readUnsignedShort());
                Long dateTime = readTime(reqBuf.readBytes(6)).getTime();
                gpsAndCanDataPkg.setDataTime(dateTime);
                int secondPkgNum = reqBuf.readUnsignedByte()&lowByte;
                logger.debug("secondPkgNum:{}",secondPkgNum);
                gpsAndCanDataPkg.setSecondPkgNum(secondPkgNum);
                secondDataPgks = Lists.newArrayList();
                while(reqBuf.readableBytes()>0 && secondPkgNum-->0) {
                    seconDataPkg = new SecondDataPkg();
                    seconDataPkg.setId(UUID.randomUUID().toString());
                    seconDataPkg.setRpm(reqBuf.readUnsignedShort());
                    seconDataPkg.setInstrumentSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setWheelSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setGpsSpeed(reqBuf.readUnsignedShort());
                    seconDataPkg.setThrottleOpening(reqBuf.readUnsignedByte());
                    seconDataPkg.setPercentagetorque(reqBuf.readUnsignedByte());
                    Short switchCodes = reqBuf.readUnsignedByte();
                    seconDataPkg.setSwitchsCode(switchCodes);
                    seconDataPkg.setSwitchStatus(D004DataParse.getStatus(switchCodes));
                    seconDataPkg.setCurrentBlock(reqBuf.readUnsignedByte());
                    seconDataPkg.setTargetGear(reqBuf.readUnsignedByte());
                    seconDataPkg.setEngineFuelRate(reqBuf.readUnsignedShort());
                    seconDataPkg.setGrade(reqBuf.readUnsignedShort());
                    seconDataPkg.setLoad(reqBuf.readUnsignedShort());
                    seconDataPkg.setFuelLevel(reqBuf.readUnsignedByte());
                    seconDataPkg.setWaterTemp(reqBuf.readUnsignedByte());
                    seconDataPkg.setBarometricPressure(reqBuf.readUnsignedByte());
                    seconDataPkg.setIntakeAirTemp(reqBuf.readUnsignedShort());
                    seconDataPkg.setAirTemp(reqBuf.readUnsignedByte());
                    seconDataPkg.setExhaustTemp(reqBuf.readUnsignedShort());
                    seconDataPkg.setIntakeQGpressure(reqBuf.readUnsignedShort());
                    seconDataPkg.setRelativePress(reqBuf.readUnsignedShort());
                    seconDataPkg.setEngineTorqueMode(reqBuf.readUnsignedByte());
                    seconDataPkg.setOilPressure(reqBuf.readUnsignedShort());
                    seconDataPkg.setUreaLevel(reqBuf.readUnsignedByte());
                    seconDataPkg.setStateFlag(reqBuf.readUnsignedInt());
                    seconDataPkg.setBrakePedalOpen(reqBuf.readUnsignedByte());
                    seconDataPkg.setGpsDir(reqBuf.readUnsignedShort());
                    seconDataPkg.setAirCompressorStatus(reqBuf.readUnsignedByte());
                    seconDataPkg.setTransmissionOutputSpeed(reqBuf.readUnsignedShort());
                    secondDataPgks.add(seconDataPkg);
                }
                gpsAndCanDataPkg.setSecondDataPkgList(secondDataPgks);
                gpsAndCanDataPkgs.add(gpsAndCanDataPkg);
            }
            gpsAndCan.setPkgs(gpsAndCanDataPkgs);
            q.setGpsAndCan(gpsAndCan);
            processEvent(JSON.toJSONString(gpsAndCan),taskId,q.id(), Constants.GPSANDCANINFORMATIC_TOPIC,channelProcessor);
            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
            if(logger.isDebugEnabled()){
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        }catch (Exception e){
            logger.warn("协议解析失败：:{},E:{}",upMsg,e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

    }
}











