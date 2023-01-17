package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.BitParser;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.X0200BitParse;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;

public class PositionAndAlarmPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(PositionAndAlarmPH.class);


    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId == 0x0200) {//位置信息汇报
            do_0200(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0704) { //位置信息批量汇报
            do_0704(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8201) {//位置信息查询
            do_8201((Req_8201) dnReq);
        } else if (dnReq instanceof Req_8202) {//临时位置跟踪控制
            do_8202((Req_8202) dnReq);
        } else if (dnReq instanceof Req_8203) {//人工确认报警消息
            do_8203((Req_8203) dnReq);
        } else {
            throw new UnsupportedProtocolException("未知的下行请求消息类型：" + dnReq.getClass().getName());
        }
    }

    private void do_8203(final Req_8203 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8203;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeShort(dnReq.getAlarmMsgSerialNo());
            req.dataBuf.writeInt(dnReq.getAlarmType());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

    }

    private void do_8202(final Req_8202 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8202;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeShort(dnReq.getTimeDistance());
            req.dataBuf.writeInt(dnReq.getUsefulLife());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

    }

    private void do_8201(final Req_8201 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0201 res = new Res_0201();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8201;
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

    }

    /**
     * 位置信息汇报
     *
     * @param upMsg
     * @return
     */
    private void do_0200(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        Req_0200 q = new Req_0200();

        byte[] bytes;
        //异常驾驶行为告警
        List<Object> abnormalData;
        List<String> abnormalDrivingType;
        Short abnormalDrivingDegree;

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            GpsVo gps = new GpsVo();
            gps.setSim(upMsg.sim);
            gps.setVid(upMsg.vid);
            //告警
            int alarm = reqBuf.readInt();
            gps.setAlarm(alarm);
            if (0 != alarm) {
                gps.setAlarms(X0200BitParse.parseAlarm(alarm));
            }
            //车辆状态
            int vstate = reqBuf.readInt();
            gps.setState(vstate);
            if (0 != vstate) {
                gps.setVehicleStatus(X0200BitParse.getStateDesp(vstate));
            }
            gps.setLat(reqBuf.readInt());
            gps.setLon(reqBuf.readInt());
            gps.setAlt(reqBuf.readShort());
            gps.setSpeed(reqBuf.readShort());
            gps.setDir(reqBuf.readShort());
            gps.setGpsTime(readTime(reqBuf.readBytes(6)));

            List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
            while (reqBuf.readableBytes() > 0) {
                short extraInfoId = reqBuf.readUnsignedByte();
                if (extraInfoId == 0x01) {//里程
                    reqBuf.skipBytes(1);
                    gps.setMile(reqBuf.readInt());
                } else if (extraInfoId == 0x02) {//油耗
                    reqBuf.skipBytes(1);
                    int shortValue = reqBuf.readUnsignedShort();
                    gps.setFuel(shortValue / 100.0f);
                } else if (extraInfoId == 0x03) {//行驶记录仪速度
                    reqBuf.skipBytes(1);
                    gps.setSpeed1(reqBuf.readShort());
                } else if (extraInfoId == 0x18) { //异常驾驶行为报警
                    reqBuf.skipBytes(1);
                    if (reqBuf.readableBytes() > 0) {
                        ByteBuf tmpBuf = reqBuf.readBytes(3);
                        //异常驾驶行为类型 判空
                        abnormalData = BitParser.parseAbnormalDrivingData(tmpBuf);
                        if (abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData) {
                            abnormalDrivingType = (List<String>) abnormalData.get(0);
                            gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                        }
                        //异常驾驶行为程度 判空
                        if (abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData) {
                            abnormalDrivingDegree = (Short) abnormalData.get(1);
                            //判断是不是有效值
                            if (abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100) {
                                gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                            } else {
                                logger.warn("不是有效异常驾驶行为程度值");
                            }
                        }
                    }
                } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                    reqBuf.skipBytes(1);
                    int signalState = reqBuf.readInt();
                    logger.info("signalState:{}",signalState);
                    gps.setSignalState(signalState);
                    if (0 != signalState) {
                        gps.setSignalStates(X0200BitParse.parseVehicleStatus(signalState));
                    }
                } else if (extraInfoId == 0x2A) {//IO状态位
                    reqBuf.skipBytes(1);
                    gps.setIoState(reqBuf.readShort());
                } else if (extraInfoId == 0xE1) { //电平电压
                    reqBuf.skipBytes(1);
                    gps.setBatteryVoltage(reqBuf.readShort());
                } else if (extraInfoId == 0xE2) { //累计油耗
                    reqBuf.skipBytes(1);
                    gps.setCumulativeOilConsumption(reqBuf.readUnsignedInt());
                } else if (extraInfoId == 0xE3) { //累计油耗 0.1 和A司沟通新增E3作为累计油耗
                    reqBuf.skipBytes(1);
                    long cumulativeOilConsumption = reqBuf.readUnsignedInt();
                    if (0 != cumulativeOilConsumption) {
                        gps.setCumulativeOilConsumption(cumulativeOilConsumption * 100);
                    }
                } else if (extraInfoId == 0xF1) { //总计油耗
                    reqBuf.skipBytes(1);
                    gps.setTotalFuelConsumption(reqBuf.readUnsignedInt());
                } else if (extraInfoId == 0xF2) { //报警信息
                    reqBuf.skipBytes(1);
                    long vdrAlarmInforCode = reqBuf.readUnsignedInt();
                    gps.setAlarmInforCode(vdrAlarmInforCode);
                    gps.setAlarmLists(X0200BitParse.getVdrAlarmLists(vdrAlarmInforCode));
                    //gps.setHydraulicTank(reqBuf.readUnsignedByte());
                } /*else if(extraInfoId ==0xF3){   //位置附加信息定义
                    float engineSpeed=reqBuf.readUnsignedShort();
                    int torquePercentage=reqBuf.readByte();
                    int throttleOpen=reqBuf.readByte();
                    gps.setEngineSpeed(engineSpeed);
                    gps.setTorquePercentage(torquePercentage);
                    gps.setThrottleOpen(throttleOpen);
                }*/
                else if (extraInfoId == 0xF3) { //车载重量
                    reqBuf.skipBytes(1);
                    gps.setVehicleWeight(reqBuf.readUnsignedInt());
                }
                else if (extraInfoId == 0xF4) { //0位:CAN状态 1位：油量液位状态
                    reqBuf.skipBytes(1);
                    gps.setCanAndHydraulicTankStatus(reqBuf.readUnsignedInt());
                } else {
                    GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                    item.setId(extraInfoId);
                    int byteLength = reqBuf.readUnsignedByte();
                    bytes = new byte[byteLength];
                    item.setData(bytes);
                    reqBuf.skipBytes(byteLength);
                    extraInfoItems.add(item);
                }
            }
            gps.setExtraInfoItems(extraInfoItems);
            gps.setMsgId("0200");
            gps.setReceiveMsgTime(System.currentTimeMillis());

            processEvent(JSON.toJSONString(gps), taskId, q.id(), Constants.POSITIONINFORMATION_TOPIC, channelProcessor);

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


    /**
     * 位置信息批量汇报
     *
     * @param upMsg
     * @return
     */
    private void do_0704(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        Req_0704 q = new Req_0704();
        //异常驾驶行为告警
        List<Object> abnormalData;
        List<String> abnormalDrivingType;
        Short abnormalDrivingDegree;
        //解析上行请求协议
        try {
            GpsVo gps;
            AddGpsVo addGpsVo = new AddGpsVo();
            ByteBuf msgData = upMsg.dataBuf;
            short dataCount = msgData.readShort();
            q.setPositionDataType(msgData.readByte());
            for (int i = 0; (i < dataCount) && (msgData.readableBytes() > 28); i++) {
                short dataLen = msgData.readShort();
                if (dataLen > 0) {
                    gps = new GpsVo();
                    int startReadIndex = msgData.readerIndex();
                    //告警
                    int alarm = msgData.readInt();
                    gps.setAlarm(alarm);
                    if (0 != alarm) {
                        gps.setAlarms(X0200BitParse.parseAlarm(alarm));
                    }
                    int cState = msgData.readInt();
                    logger.debug("cState = " + cState);
                    gps.setState(cState);
                    if (0 != cState) {
                        gps.setVehicleStatus(X0200BitParse.getStateDesp(cState));
                    }
                    gps.setLat(msgData.readInt());
                    gps.setLon(msgData.readInt());
                    gps.setAlt(msgData.readShort());
                    gps.setSpeed(msgData.readShort());
                    gps.setDir(msgData.readShort());
                    gps.setGpsTime(readTime(msgData.readBytes(6)));
                    gps.setSim(upMsg.sim);
                    List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
                    while (dataLen > msgData.readerIndex() - startReadIndex) {
                        short extraInfoId = msgData.readUnsignedByte();
                        if (extraInfoId == 0x01) {//里程
                            msgData.skipBytes(1);
                            gps.setMile(msgData.readInt());
                        } else if (extraInfoId == 0x02) {//油耗
                            msgData.skipBytes(1);
                            int shortValue = msgData.readUnsignedShort();
                            gps.setFuel(shortValue / 100.0f);
                        } else if (extraInfoId == 0x03) {//行驶记录仪速度
                            msgData.skipBytes(1);
                            gps.setSpeed1(msgData.readShort());
                        } else if (extraInfoId == 0x18) {//异常驾驶行为报警
                            msgData.skipBytes(1);
                            if (msgData.readableBytes() > 0) {
                                ByteBuf tmpBuf = msgData.readBytes(3);
                                //异常驾驶行为类型 判空
                                abnormalData = BitParser.parseAbnormalDrivingData(tmpBuf);
                                if (abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData) {
                                    abnormalDrivingType = (List<String>) abnormalData.get(0);
                                    gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                                }
                                //异常驾驶行为程度 判空
                                if (abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData) {
                                    abnormalDrivingDegree = (Short) abnormalData.get(1);
                                    //判断是不是有效值
                                    if (abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100) {
                                        gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                                    } else {
                                        logger.warn("不是有效异常驾驶行为程度值");
                                    }
                                }
                            }
                        } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                            msgData.skipBytes(1);
                            int signalState = msgData.readInt();
                            gps.setSignalState(signalState);
                            if (0 != signalState) {
                                gps.setSignalStates(X0200BitParse.parseVehicleStatus(signalState));
                            }
                        } else if (extraInfoId == 0x2A) {//IO状态位
                            msgData.skipBytes(1);
                            gps.setIoState(msgData.readShort());
                        } else if (extraInfoId == 0xE1) { //电平电压
                            msgData.skipBytes(1);
                            gps.setBatteryVoltage(msgData.readShort());
                        } else if (extraInfoId == 0xE2) { //累计油耗
                            msgData.skipBytes(1);
                            gps.setCumulativeOilConsumption(msgData.readUnsignedInt());
                        } else if (extraInfoId == 0xE3) { //累计油耗 0.1 和A司沟通新增E3作为累计油耗
                            msgData.skipBytes(1);
                            long cumulativeOilConsumption = msgData.readUnsignedInt();
                            if (0 != cumulativeOilConsumption) {
                                gps.setCumulativeOilConsumption(cumulativeOilConsumption * 100);
                            }
                        } else if (extraInfoId == 0xF1) { //总计油耗
                            msgData.skipBytes(1);
                            gps.setTotalFuelConsumption(msgData.readUnsignedInt());
                        } else if (extraInfoId == 0xF2) { //F2 告警列表
                            msgData.skipBytes(1);
                            long f2AlarmsCode = msgData.readUnsignedInt();
                            gps.setAlarmInforCode(f2AlarmsCode);
                            gps.setAlarmLists(X0200BitParse.getVdrAlarmLists(f2AlarmsCode));
                            //gps.setHydraulicTank(msgData.readUnsignedByte());
                        } else if (extraInfoId == 0xF3) { //车载重量
                            msgData.skipBytes(1);
                            gps.setVehicleWeight(msgData.readUnsignedInt());
                        } else if (extraInfoId == 0xF4) { //0位:CAN状态 1位：油量液位状态
                            msgData.skipBytes(1);
                            gps.setCanAndHydraulicTankStatus(msgData.readUnsignedInt());
                        } else {
                            GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                            item.setId(extraInfoId);
                            int byteLength = msgData.readUnsignedByte();
                            byte[] bytes = new byte[byteLength];
                            item.setData(bytes);
                            msgData.skipBytes(byteLength);
                            extraInfoItems.add(item);
                        }
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    addGpsVo.setVid(upMsg.vid);
                    addGpsVo.setGpsVo(gps);
                    addGpsVo.setPositionDataType(q.getPositionDataType());
                    addGpsVo.setMsgId("0704");
                    addGpsVo.setReceiveMsgTime(System.currentTimeMillis());

                    processEvent(JSON.toJSONString(addGpsVo), taskId, q.id(), Constants.POSITIONINFORMATIONS_TOPIC, channelProcessor);
                }
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

//        updateVehicleStatus2Redis(upMsg.vid, taskId);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        return;
    }

    @Override
    public void setup() {

    }


}
