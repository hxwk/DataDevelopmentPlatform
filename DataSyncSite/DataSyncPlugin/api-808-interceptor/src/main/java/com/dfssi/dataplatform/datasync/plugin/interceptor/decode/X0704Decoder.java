package com.dfssi.dataplatform.datasync.plugin.interceptor.decode;

import com.dfssi.dataplatform.datasync.common.utils.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.GpsVo;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.Req_0704;
import com.dfssi.dataplatform.datasync.plugin.interceptor.common.X0200BitParse;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.interceptor.common.ProtoUtil.readTime;
import static com.dfssi.dataplatform.datasync.plugin.interceptor.common.ProtoUtil.readU8Bytes;

/**
 * x0704 protocol message body parse
 * @author jianKang
 * @date 2017/12/16
 */
public class X0704Decoder {
    static final Logger logger = LoggerFactory.getLogger(X0704Decoder.class);
    X0200BitParse x0200BitParse = new X0200BitParse();
    ByteBufUtils byteBufUtils = new ByteBufUtils();
    /**
     * 位置信息批量汇报
     * @param upMsg
     * @return
     */
    public byte[] do_0704(final ProtoMsg upMsg) {
        Req_0704 q = new Req_0704();
        //异常驾驶行为告警
        List<Object> abnormalData;
        List<String> abnormalDrivingType;
        Short abnormalDrivingDegree;

        GpsVo gps = null;
        //解析上行请求协议
        try {
            q.setSim(upMsg.sim);
            q.setVid(upMsg.vid);
            List<GpsVo> gpsList = Lists.newArrayList();
//            String beforeBody = new String(upMsg.dataBuf);
//            ByteBuf msgData = byteBufUtils.hexStringToByteBuf(beforeBody);
            ByteBuf msgData= Unpooled.copiedBuffer(upMsg.getDataBuf());
            short dataCount = msgData.readShort();
            logger.info("数据项个数 "+dataCount);
            q.setPositionDataType(msgData.readByte());
            logger.info("位置数据类型 "+q.getPositionDataType());

            for (int i = 0; (i < dataCount)&&(msgData.readableBytes()>28); i++) {
                short dataLen = msgData.readShort();
                if (dataLen > 0) {
                    gps = new GpsVo();
                    int startReadIndex = msgData.readerIndex();
                    gps.setAlarmCode(msgData.readInt());
                    if(0!=gps.getAlarmCode()){
                        gps.setAlarms(x0200BitParse.parseAlarm(gps.getAlarmCode()));
                    }
                    gps.setState(msgData.readInt());
                    if(0!=gps.getState()){
                        gps.setVehicleStatus(X0200BitParse.getStateDesp(gps.getState()));
                    }
                    gps.setLat(msgData.readInt());
                    gps.setLon(msgData.readInt());
                    gps.setAlt(msgData.readShort());
                    gps.setSpeed(msgData.readShort());
                    gps.setDir(msgData.readShort());
                    gps.setGpsTime(readTime(msgData.readBytes(6)));
                    gps.setSim(upMsg.sim);
                    List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
                    while (dataLen > msgData.readerIndex() - startReadIndex){    //&&msgData.readableBytes()>0)
                        byte extraInfoId = msgData.readByte();
                        /**
                         * 里程
                         */
                        if (extraInfoId == 0x01) {
                            msgData.skipBytes(1);
                            gps.setMile(msgData.readInt());
                        }
                        /**
                         * 油耗
                         */
                        else if (extraInfoId == 0x02) {
                            msgData.skipBytes(1);
                            int shortValue = msgData.readUnsignedShort();
                            gps.setFuel(shortValue / 100.0f);
                        }
                        /**
                         * 行驶记录仪速度
                         */
                        else if (extraInfoId == 0x03) {
                            msgData.skipBytes(1);
                            gps.setSpeed1(msgData.readShort());
                        }
                        /*
                        异常驾驶行为报警
                        */
                        else if (extraInfoId == 0x18) {
                            msgData.skipBytes(1);
                            if(msgData.readableBytes() > 0){
                                ByteBuf tmpBuf = msgData.readBytes(3);
                                //异常驾驶行为类型 判空
                                abnormalData = x0200BitParse.parseAbnormalDrivingData(tmpBuf);
                                if(abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData){
                                    abnormalDrivingType = (List<String>) abnormalData.get(0);
                                    gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                                }
                                //异常驾驶行为程度 判空
                                if(abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData){
                                    abnormalDrivingDegree = (Short)abnormalData.get(1);
                                    //判断是不是有效值
                                    if(abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100){
                                        gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
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
                            msgData.skipBytes(1);
                            gps.setSignalState(msgData.readInt());
                            if(0!=gps.getSignalState()){
                                gps.setSignalStates(X0200BitParse.parseVehicleStatus(gps.getSignalState()));
                            }
                        }
                        /**
                         * IO状态位
                         */
                        else if (extraInfoId == 0x2A) {
                            msgData.skipBytes(1);
                            gps.setIoState(msgData.readShort());
                        }
                        /*
                        电平电压
                        */
                        else if(extraInfoId ==0xE1){
                            msgData.skipBytes(1);
                            gps.setBatteryVoltage(msgData.readShort());
                        }
                        /*
                        累计油耗
                        * */
                        else if(extraInfoId == 0xE2) {
                            msgData.skipBytes(1);
                            gps.setCumulativeOilConsumption(msgData.readUnsignedInt());
                        }
                        /*
                        累计油耗 0.1 和A司沟通新增E3作为累计油耗
                        * */
                        else if(extraInfoId == 0xE3){
                            msgData.skipBytes(1);
                            long cumulativeOilConsumption = msgData.readUnsignedInt();
                            if(0!=cumulativeOilConsumption){
                                gps.setCumulativeOilConsumption(cumulativeOilConsumption*100);
                            }
                        }
                        /*
                        总计油耗
                        */
                        else if(extraInfoId == 0xF1){
                            msgData.skipBytes(1);
                            gps.setTotalFuelConsumption(msgData.readUnsignedInt());
                        }
                        /*
                        油箱液位
                        * */
                        else if(extraInfoId == 0xF2){
                            msgData.skipBytes(1);
                            gps.setHydraulicTank(msgData.readUnsignedByte());
                        }
                        /*
                        车载重量
                        */
                        else if(extraInfoId == 0xF3) {
                            msgData.skipBytes(1);
                            gps.setVehicleWeight(msgData.readUnsignedShort());
                        }
                        /*
                        0位:CAN状态 1位：油量液位状态
                        * */
                        else if(extraInfoId == 0xF4){
                            msgData.skipBytes(1);
                            gps.setCanAndHydraulicTankStatus(msgData.readUnsignedInt());
                        }
                        else {
                            GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                            item.setId(extraInfoId);
                            int byteLength = msgData.readUnsignedByte();
                            byte[] bytes = new byte[byteLength];
//                            item.setData(readU8Bytes(msgData));
                            msgData.skipBytes(byteLength);
                            extraInfoItems.add(item);
                        }
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    gpsList.add(gps);
                }
            }
            q.setGpsVos(gpsList);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);

        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}]接收到上行请求消息:{},packCount:{},packIndex{}", upMsg.sim, q,upMsg.packCount,upMsg.packIndex);
        }
        logger.info(gps.toString());
        return q.toString().getBytes();
    }
}
