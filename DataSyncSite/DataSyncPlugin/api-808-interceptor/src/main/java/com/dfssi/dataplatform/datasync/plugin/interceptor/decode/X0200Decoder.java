package com.dfssi.dataplatform.datasync.plugin.interceptor.decode;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.utils.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.interceptor.common.X0200BitParse;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.GpsVo;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.Req_0200;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.interceptor.common.ProtoUtil.readTime;

/**
 * X0200 protocol message body parse
 * @author jianKang
 * @date 2017/12/16
 * @modifydate 2018/01/16
 */
public class X0200Decoder {
    static final Logger logger = LoggerFactory.getLogger(X0200Decoder.class);
    X0200BitParse x0200BitParse = new X0200BitParse();
    ByteBufUtils byteBufUtils = new ByteBufUtils();

    public byte[] do_0200(final ProtoMsg protoMsg){
        if (protoMsg==null ||protoMsg.getDataBuf().length==0){
            logger.info("message body cannot be Null");
            return null;
        }
//        String beforeBody = new String(protoMsg.getDataBuf());
//        ByteBuf byteBuffer = byteBufUtils.hexStringToByteBuf(beforeBody);
        ByteBuf byteBuffer= Unpooled.copiedBuffer(protoMsg.getDataBuf());
        GpsVo gpsVo = null;
        List<GpsVo.ExtraInfoItem> extraInfoItems = null;

        Req_0200 req0200 = new Req_0200();
        //异常驾驶行为告警
        List<Object> abnormalData ;
        List<String> abnormalDrivingType ;
        Short abnormalDrivingDegree ;


        //解析上行请求协议
        try {
            req0200.setVid(protoMsg.vid);
            req0200.setSim(protoMsg.getSim());
            gpsVo = new GpsVo();
            gpsVo.setSim(protoMsg.getSim());
            //告警
            gpsVo.setAlarmCode(byteBuffer.readUnsignedInt());
            if(0!=gpsVo.getAlarmCode()){
                gpsVo.setAlarms(X0200BitParse.parseAlarm(gpsVo.getAlarmCode()));
            }
            logger.debug("alarm BinaryString "+Long.toBinaryString(gpsVo.getAlarmCode()));
            logger.debug("alarm Lists "+ x0200BitParse.parseAlarm(gpsVo.getAlarmCode()));

            //车辆状态
            gpsVo.setState(byteBuffer.readUnsignedInt());
            if(0!=gpsVo.getState()){
                gpsVo.setVehicleStatus(x0200BitParse.getStateDesp(gpsVo.getState()));
            }
            logger.debug("state BinaryString "+Long.toBinaryString(gpsVo.getState()));
            logger.debug("vehicle state Lists "+ x0200BitParse.getStateDesp(gpsVo.getState()));
            gpsVo.setLat(byteBuffer.readUnsignedInt());
            gpsVo.setLon(byteBuffer.readUnsignedInt());
            gpsVo.setAlt(byteBuffer.readUnsignedShort());
            gpsVo.setSpeed(byteBuffer.readUnsignedShort());
            gpsVo.setDir(byteBuffer.readUnsignedShort());
            gpsVo.setGpsTime(readTime(byteBuffer.readBytes(6)));

            extraInfoItems = Lists.newArrayList();
            while (byteBuffer.readableBytes() > 0) {
                short extraInfoId = byteBuffer.readUnsignedByte();
//                int extraInfoIntId = extraInfoId & 0xff;
                logger.debug("extraInfo id:{}",Integer.toHexString(extraInfoId));
                /**
                 * 里程
                 */
                if (extraInfoId == 0x01) {
                    byteBuffer.skipBytes(1);
                    gpsVo.setMile(byteBuffer.readUnsignedInt());
                    logger.info("里程 "+gpsVo.getMile());
                }
                /**
                 * 瞬时油耗
                 */
                else if (extraInfoId == 0x02) {
                    byteBuffer.skipBytes(1);
                    int shortValue = byteBuffer.readUnsignedShort();
                    gpsVo.setFuel(shortValue / 100.0f);
                    logger.info("瞬时油耗 "+gpsVo.getFuel());
                }
                /**
                 行驶记录仪速度
                 */
                else if (extraInfoId == 0x03) {
                    byteBuffer.skipBytes(1);
                    gpsVo.setSpeed1(byteBuffer.readUnsignedShort());
                    logger.info("形势记录仪速度 "+gpsVo.getSpeed1());
                }
//                else if(extraInfoId == 0x04){
//                    byteBuffer.skipBytes(1);
//                    gpsVo.setArtificialConfirmedAlarm(byteBuffer.readUnsignedShort());
//                    logger.info("需要人工确认报警事件的ID,从1开始计数:{} ",gpsVo.getArtificialConfirmedAlarm());
//                }
                 /*
                 异常驾驶行为报警
                 */
                else if(extraInfoId==0x18){
                    byteBuffer.skipBytes(1);
                    if(byteBuffer.readableBytes() > 0){
                        ByteBuf tmpBuf = byteBuffer.readBytes(3);
                        //异常驾驶行为类型 判空
                        abnormalData = X0200BitParse.parseAbnormalDrivingData(tmpBuf);
                        if(abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData){
                            abnormalDrivingType = (List<String>) abnormalData.get(0);
                            gpsVo.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                        }
                        //异常驾驶行为程度 判空
                        if(abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData){
                            abnormalDrivingDegree = (Short)abnormalData.get(1);
                            //判断是不是有效值
                            if(abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100){
                                gpsVo.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                            }else{
                                logger.warn("不是有效异常驾驶行为程度值");
                            }
                        }
                    }
                }
                /**
                 * 扩展车辆信号状态位
                 */
                else if (extraInfoId == 0x25) {
                    byteBuffer.skipBytes(1);
                    gpsVo.setSignalState(byteBuffer.readUnsignedInt());
                    if(0!=gpsVo.getSignalState()){
                        gpsVo.setSignalStates(X0200BitParse.parseVehicleStatus(gpsVo.getSignalState()));
                    }
                    logger.info("扩展车辆信号状态位: " + x0200BitParse.parseVehicleStatus(gpsVo.getSignalState()));
                }
                /**
                 * IO状态位
                 */
                else if (extraInfoId == 0x2A) {
                    byteBuffer.skipBytes(1);
                    gpsVo.setIoState(byteBuffer.readUnsignedShort());
                    logger.info("IO状态位：{}",gpsVo.getIoState());
                }
                /**
                 * 电平电压
                 */
                else if (extraInfoId == 0xE1){
                    byteBuffer.skipBytes(1);
                    gpsVo.setSupplyVoltage(byteBuffer.readShort());
                    logger.info("主电源电压:{}",gpsVo.getSupplyVoltage());
                }
                /**
                 * 累计油耗 （/1000 && /100 单位升）
                 */
                else if (extraInfoId == 0xE2){
                    byteBuffer.skipBytes(1);
                    double cumulativeOilConsumption = byteBuffer.readUnsignedInt();
                    gpsVo.setCumulativeOilConsumption(cumulativeOilConsumption);
                    logger.info("累计油耗:{}",gpsVo.getCumulativeOilConsumption());
                }
                 /*
                累计油耗 0.1 和A司沟通新增E3作为累计油耗
                */
                else if(extraInfoId == 0xE3){
                    byteBuffer.skipBytes(1);
                    long cumulativeOilConsumption = byteBuffer.readUnsignedInt();
                    if(0!=cumulativeOilConsumption){
                        gpsVo.setCumulativeOilConsumption(cumulativeOilConsumption*100);
                    }
                }
                else if(extraInfoId == 0xF1) { //总计油耗
                    byteBuffer.skipBytes(1);
                    gpsVo.setTotalFuelConsumption(byteBuffer.readUnsignedInt());
                    logger.info("总计油耗:{}",gpsVo.getTotalFuelConsumption());
                } else if(extraInfoId == 0xF2){ //油箱液位
                    byteBuffer.skipBytes(1);
                    gpsVo.setHydraulicTank(byteBuffer.readUnsignedByte());
                    logger.info("油箱液位:{}",gpsVo.getHydraulicTank());
                }else if(extraInfoId == 0xF3) { //车载重量
                    byteBuffer.skipBytes(1);
                    gpsVo.setVehicleWeight(byteBuffer.readUnsignedShort());
                    logger.info("车载重量:{}",gpsVo.getVehicleWeight());
                }else if(extraInfoId == 0xF4){ //0位:CAN状态 1位：油量液位状态
                    byteBuffer.skipBytes(1);
                    gpsVo.setCanAndHydraulicTankStatus(byteBuffer.readUnsignedInt());
                    logger.info("0位:CAN状态 1位：油量液位状态:{}",gpsVo.getCanAndHydraulicTankStatus());
                } else {
                    /**
                     * 非指定的附加信息ID （除了0x01 0x02 0x03 0x25 0x2A 0xE1 0xE2）
                     */
                    GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                    item.setId(extraInfoId);
                    int length = byteBuffer.readUnsignedByte();
                    byte[] body = new byte[length];
                    byteBuffer.readBytes(body);
                    item.setData(body);
//                    byteBuffer.skipBytes(length);
                    //item.setData(readU8Bytes(byteBuffer));
                    extraInfoItems.add(item);
                    logger.info("CANNOT PARSE DATA ITEM:{} ",item);
                }
            }

            logger.debug("位置信息解析：{}",gpsVo.toString());

            gpsVo.setExtraInfoItems(extraInfoItems);
            req0200.setGps(gpsVo);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", protoMsg.sim, req0200);
            }
        }catch (Exception ex){
            logger.error("协议解析失败{}:error{}",protoMsg,ex.getMessage());
        }
        logger.info("extra info items:{}",extraInfoItems);
        logger.info(gpsVo.toString());
        return JSON.toJSONString(req0200).getBytes();
    }
}
