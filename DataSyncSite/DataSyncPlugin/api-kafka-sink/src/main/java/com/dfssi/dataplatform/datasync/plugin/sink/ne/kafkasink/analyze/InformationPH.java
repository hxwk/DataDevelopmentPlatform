package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.model.newen.entity.*;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.NEStateCode2Name;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.NEStateConstant;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.exception.ParseException;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.utils.ProtoUtil;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasync.common.common.EventHeader.HEADER_KEY;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.ByteBufUtils.bytes2ShortArr;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.exception.ParseException.*;

/**
 * @author : JianKang
 * @Modify Time:2018-01-15 10:12
 */
public class InformationPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(InformationPH.class);
    private static long collecteTime;
    private static short informationType;
    private static String inforType;
    private static final byte realCmdCode = 0x02;
    private static final byte fillCmdCode = 0x03;
    private static int hasValidNum = 0;//当前有效报文数

    /*public static void main(String[] args) {
        //注册当前类
        InformationPH parse = new InformationPH();
        ProtoMsg upMsg = new ProtoMsg();
        //模拟16进制到字符串
        //String s = "1202080D31380103030107FF007FFFFF0D20274C31013607FF3131020101047F4E2007FF7F07FF2744050106CCBFE801D08D680601010DAD01020DAC01014401014407000000000000000000";
        //03 fuel Cell
        //String s = "12 04 04 00 3B 35 01 01 03 01 00 00 00 00 25 EE 16 95 27 2F 46 02 00 01 50 00 00 02 01 01 04 3C 4E 20 9C 40 3B 16 63 27 28 05 00 06 AF F0 40 01 EA 90 79 06 01 01 0F 11 01 1A 0F 0B 01 1D 42 01 4E 3C 07 00 00 00 00 00 00 00 00 00 08 01 01 16 95 27 2F 00 96 00 01 96 0F 11 0F 11 0F 10 0F 0F 0F 0F 0F 0F 0F 0F 0F 0E 0F 0E 0F 0E 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0D 0F 0F 0F 0F 0F 0E 0F 0F 0F 0D 0F 0F 0F 0F 0F 0E 0F 0B 0F 0D 0F 0E 0F 0E 0F 0E 0F 0E 0F 0D 0F 0D 0F 0E 0F 0F 0F 0E 0F 0F 0F 0F 0F 0E 0F 0F 0F 0E 0F 0D 0F 0D 0F 0D 0F 0E 0F 0E 0F 0D 0F 0D 0F 0D 0F 0D 0F 0D 0F 0E 0F 0E 0F 0E 0F 0E 0F 0D 0F 0E 0F 0D 0F 0B 0F 0D 0F 0E 0F 0D 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0F 0F 0F 0F 0F 0F 0E 0F 0E 0F 0E 0F 0F 0F 0F 0F 10 0F 10 0F 10 0F 0F 0F 10 0F 0E 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0E 0F 0E 0F 0E 0F 10 0F 0F 0F 0E 0F 0E 0F 0E 0F 0F 0F 10 0F 10 0F 11 0F 11 0F 11 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 11 0F 11 0F 11 0F 11 0F 0F 0F 11 0F 0F 0F 0C 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 10 0F 0E 0F 10 0F 10 0F 10 0F 10 0F 0E 0F 0D 0F 0E 0F 0E 0F 0F 0F 0F 0F 0D 0F 0D 0F 0D 0F 0D 0F 0D 0F 0E 0F 0E 0F 0E 0F 0E 0F 10 0F 0E 0F 0E 09 01 01 00 4E 3F 3F 40 40 40 40 40 40 40 40 40 3F 3D 3F 3F 41 40 40 41 40 40 41 41 40 3F 3D 3F 40 42 41 41 41 41 41 41 42 41 40 3D 3F 40 41 41 41 41 41 40 41 41 41 3F 3D 3F 3F 41 40 40 40 40 40 40 41 3F 40 3D 3F 3F 40 40 40 40 3F 3F 40 40 3F 3E 3C 66";
        //String s = "12 04 1A 09 28 34 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 05 00 00 00 00 00 00 00 00 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 00 00 00 00 00 00 00 02 00 00 00 08 00 00 00 08 08 01 01 16 96 27 10 00 96 00 01 96 0F 11 0F 11 0F 0F 0F 0F 0F 0F 0F 0F 0F 10 0F 0E 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0F 0E 0F 0F 0F 0E 0F 0F 0F 0F 0F 0D 0F 0F 0F 0F 0F 0F 0F 0C 0F 0E 0F 0E 0F 0D 0F 0E 0F 0E 0F 0E 0F 0D 0F 0E 0F 11 0F 0E 0F 0F 0F 0F 0F 0F 0F 0F 0F 0E 0F 0D 0F 0D 0F 0D 0F 0E 0F 0D 0F 0D 0F 0E 0F 0D 0F 0D 0F 0E 0F 0E 0F 0F 0F 0E 0F 0E 0F 0E 0F 0E 0F 0D 0F 0C 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 10 0F 10 0F 10 0F 10 0F 0F 0F 0E 0F 0F 0F 0E 0F 0F 0F 0E 0F 10 0F 10 0F 10 0F 0E 0F 10 0F 10 0F 0F 0F 0F 0F 10 0F 0F 0F 0F 0F 0F 0F 0F 0F 0E 0F 0E 0F 0E 0F 10 0F 10 0F 0F 0F 0E 0F 0F 0F 10 0F 10 0F 11 0F 11 0F 11 0F 11 0F 0F 0F 0F 0F 11 0F 0F 0F 0F 0F 11 0F 11 0F 11 0F 11 0F 11 0F 11 0F 10 0F 0C 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 0E 0F 10 0F 0F 0F 0E 0F 0E 0F 10 0F 10 0F 10 0F 0F 0F 0E 0F 0F 0F 0E 0F 0F 0F 0F 0F 0F 0F 0E 0F 0D 0F 0D 0F 0E 0F 0E 0F 10 0F 10 0F 0E 0F 0F 0F 0F 0F 10 09 01 01 00 4E 3F 3F 40 40 40 40 40 40 40 40 40 3F 3D 3F 3F 41 40 40 41 40 40 41 41 40 3F 3D 3F 40 42 41 41 41 41 41 41 42 41 40 3D 3F 3F 41 41 41 41 41 40 41 41 41 3F 3D 3F 3F 41 40 40 40 40 40 40 41 3F 40 3D 3E 3F 40 40 3F 40 40 3F 40 40 3F 3E 3C AA";
        //String s = "110C190D052C0103030107FF007FFFFF0D20274C31013607FF3131020101FF7F4E2007FF7F07FF2744050006CE7F5F01D17C970601010DAD01020DAC010144010144";
        String s = "23 23 02 FE 4C 47 4A 45 31 33 45 41 30 48 4D 38 38 38 38 38 38 01 00 78 12 0A 1D 0F 0D 3A 01 01 03 02 00 00 00 00 00 00 00 00 27 10 00 01 00 00 00 00 00 02 01 01 01 00 00 00 00 00 00 00 00 00 00 03 00 00 00 00 00 00 00 00 00 00 01 00 00 01 00 00 01 01 04 02 00 00 00 00 05 00 00 00 00 00 00 00 00 00 06 01 01 00 01 01 01 00 01 01 01 01 01 01 01 07 00 00 00 00 00 00 00 00 00 08 01 01 00 00 00 00 00 01 00 01 01 00 00 09 01 01 00 00 BA";
        //16进制字符串转成ByteBuf
        ByteBuf bb = ByteBufUtils.hexStringToByteBuf(s);
        upMsg.dataBuf = bb;
        //实时上报数据解析
        parse.do_realDataOrAddData(upMsg);
    }*/

    @Override
    public String doUpMsg(ProtoMsg upMsg) {
        //实时信息上报
        if (realCmdCode == upMsg.commandSign) {
            return do_02(upMsg);
        }//补发信息上报
        else if (fillCmdCode == upMsg.commandSign) {
            return do_03(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    /**
     * 实时信息上报
     *
     * @param upMsg
     * @return
     */
    private String do_02(final ProtoMsg upMsg) {
        return do_realDataOrAddData(upMsg);
    }

    /**
     * 补发信息上报
     * @param upMsg
     * @return
     */
    private String do_03(final ProtoMsg upMsg) {
        return do_realDataOrAddData(upMsg);
    }

    /**
     * 处理实时数据和附加上传数据
     * @param upMsg
     */
    private String do_realDataOrAddData(final ProtoMsg upMsg) {
        Req_02 req_02 = new Req_02();
        ByteBuf msgBody = upMsg.dataBuf;
        req_02.setCommandSign(String.valueOf(upMsg.commandSign));
        req_02.setVin(upMsg.vin);
        req_02.setReceiveTime(System.currentTimeMillis());
        //JoinOriginMsg joinOriginMsg = new JoinOriginMsg(upMsg);
        //req_02.setOriginalMsg(joinOriginMsg.getOriginMsg());
        ByteBuf byteBufCopy = Unpooled.copiedBuffer(msgBody);
        String originalMsg = ByteBufUtil.hexDump(byteBufCopy).toUpperCase();
        if(StringUtils.isNotEmpty(originalMsg)){
            req_02.setOriginalMsg(originalMsg);
        }else{
            req_02.setOriginalMsg(StringUtils.EMPTY);
        }
        logger.debug("original msg:{} ",req_02.getOriginalMsg());
        ParseException.clearSet();
        ParseException.resetCompleteItemSet();
        //初始化必备数据项
        ParseException.init();

        try {
            //获取车企和车型信息
            VehicleDTO vehicleDTO = getVehicleInfo(upMsg);

            if (null == vehicleDTO) {
                /*req_02.setVehicleCompany(StringUtils.EMPTY);
                req_02.setVehicleType(StringUtils.EMPTY);*/
                logger.warn("车辆不存在，无法接入数据：{}", upMsg.vin);
                return null;
            } else {
                req_02.setVehicleCompany(vehicleDTO.getVehicleCompany());
                req_02.setVehicleType(vehicleDTO.getVehicleType());
                req_02.setVehicleUse(vehicleDTO.getVehicleUse());
            }
        }catch (Exception ex){
            logger.error("获取车企车型出错: {}",ex.getMessage());
        }
        /**
         * 传过来原始完整报文,需要跳过24个头
         */
        msgBody.skipBytes(24);
        if (msgBody.readableBytes() >= 6) {
            //采集时间
            collecteTime = ProtoUtil.readTimeNE(msgBody.readBytes(6));
            Date date = new Date();
            date.setTime(collecteTime);
            req_02.setCollectTime(collecteTime);
        } else {
            logger.debug("REAL OR UNREAL DATA INVALID!");
        }
        while (msgBody.readableBytes() > 1) {
            //信息类型标识
            try {
                informationType = Byte.parseByte(String.valueOf(msgBody.readByte()), 16);
            } catch (Exception ex) {
                logger.error("InformationPh:information type read error:{}.", ex.getMessage());
                break;
            }
            //信息类型
            try {
                inforType = NEStateCode2Name.hex2String(informationType);
            } catch (Exception ex) {
                logger.warn("convert hex information Type error:{}", ex.getMessage());
            }
            logger.debug("information Type:" + inforType);
            logger.debug("msgBody:" + ByteBufUtil.hexDump(msgBody));
            switch (inforType) {
                //1.整车数据解析
                case NEStateConstant.VEHICLEDATA:
                    do_vehicleData(req_02, msgBody);
                    break;
                //2.驱动电机数据解析
                case NEStateConstant.DRIVERMOTOR:
                    do_driverMotorData(req_02, msgBody);
                    break;
                case NEStateConstant.FUELCELL://3.燃料电池数据解析
                    do_fuelCellData(req_02, msgBody);
                    break;
                case NEStateConstant.MOTORDATA://4.发动机部分数据解析
                    do_motorData(req_02, msgBody);
                    break;
                case NEStateConstant.NEGPS://5.车辆位置数据解析
                    do_gpsData(req_02, msgBody);
                    break;
                case NEStateConstant.EXTREMUM://6.极值数据解析
                    do_extremumData(req_02, msgBody);
                    break;
                case NEStateConstant.ALARM://7.报警数据解析
                    do_alarmData(req_02, msgBody);
                    break;
                case NEStateConstant.ENERGYSTORAGEVOLTAGE://8.充电储能装置电压数据
                    do_chargeStorageVoltage(req_02, msgBody);
                    break;
                case NEStateConstant.ENERGYSTORAGETEMPERATURE://9.充电储能装置电压温度
                    do_chargeStorageTemperature(req_02, msgBody);
                    break;
                default:
                    logger.debug("no match handle method!");
            }
        }
        //有效的数据项个数
        req_02.setHasValidNum(hasValidNum);
        logger.debug("有效的数据项数:{}",hasValidNum);
        //是否完整的数据
        logger.debug("新能源数据项:{}",Constants.NUM_NE_DATA_VALUE);
        req_02.setIntact(verifyIntact(req_02,hasValidNum,Constants.NUM_NE_DATA_VALUE));
        //释放msgBody
        msgBody.release();
        return JSON.toJSONString(getNeMsgObject(req_02));
    }

    //根据解析的值和数据完整性值判断是否完整，1：完整；-1：不完整；0：不确定
    private String verifyIntact(Req_02 req_02,int hasValidNum,int value){
        NEVehicleBean neVehicleBean = req_02.getNeVehicleBean();
        if ((neVehicleBean.getChargingStatusCode() == 0x01) && (neVehicleBean.getVehicleStatusCode() == 0x02) ||
                (neVehicleBean.getChargingStatusCode() == 0x04) && (neVehicleBean.getVehicleStatusCode() == 0x02)) {
            value -= 9;
        }
        String intactFlag = hasValidNum >= value ? ONE : ((hasValidNum < value) && (hasValidNum > 0)
                ? inCompleteItems() : ZERO);
        return intactFlag;
    }

    private void do_chargeStorageVoltage(Req_02 req_02,ByteBuf msgBody){
        Short storageSubSysNum = 0;
        List<Integer> cellVoltageList;
        NEChargeVoltage neChargeVoltage = new NEChargeVoltage();
        NEChargeVoltageBean neChargeVoltageBean;
        List<NEChargeVoltageBean> neChargeVoltList = Lists.newArrayList();
        neChargeVoltage.setChargeVoltageInformationType((short) 0x08);
        ParseException.addCompleteItems((byte)0x08);
        if(msgBody.readableBytes()>0){
            storageSubSysNum = msgBody.readUnsignedByte();
            neChargeVoltage.setStorageVoltageSubSysNum(storageSubSysNum);
            hasValidNum = ParseException.computerCount("storageSubSysNum",storageSubSysNum);
        }
        while(storageSubSysNum-->0 && msgBody.readableBytes()>10){
            neChargeVoltageBean = new NEChargeVoltageBean();
            cellVoltageList = Lists.newArrayList();
            neChargeVoltageBean.setStorageSubSysNo(msgBody.readUnsignedByte());
            hasValidNum = ParseException.computerCount("storageSubSysNo",neChargeVoltageBean.getStorageSubSysNo());
            neChargeVoltageBean.setStorageVoltage(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("storageVoltage",neChargeVoltageBean.getStorageVoltage());
            neChargeVoltageBean.setStorageCurrent(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("storageCurrent",neChargeVoltageBean.getStorageCurrent());
            neChargeVoltageBean.setCellTotal(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("cellTotal",neChargeVoltageBean.getCellTotal());
            neChargeVoltageBean.setSerailOfFrame(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("serailOfFrame",neChargeVoltageBean.getSerailOfFrame());
            short cellNumOfFrame = msgBody.readUnsignedByte();
            neChargeVoltageBean.setCellNumOfFrame(cellNumOfFrame);
            hasValidNum = ParseException.computerCount("cellNumOfFrame",cellNumOfFrame);
            while (cellNumOfFrame-- > 0) {
                if(msgBody.readableBytes()>0) {
                    Integer tmp = msgBody.readUnsignedShort();
                    cellVoltageList.add(tmp);
                }else{
                    logger.warn("这个地方不应该为空的,报文有问题,数据个数和单体电压个数不符");
                    cellVoltageList.add(0);
                }
                hasValidNum = ParseException.computerCount("cellVoltageList",cellVoltageList.get(0));
            }
            neChargeVoltageBean.setCellVoltage(cellVoltageList);
            neChargeVoltList.add(neChargeVoltageBean);
        }
        neChargeVoltage.setNeChargeVoltageBeanList(neChargeVoltList);
        req_02.setNeChargeVoltage(neChargeVoltage);
    }

    private void do_chargeStorageTemperature(Req_02 req_02,ByteBuf msgBody){
        short storageSubSysNum=0;
        NEChargeTemp neChargeTemp = new NEChargeTemp();
        NEChargeTempBean neChargeTempBean ;
        List<NEChargeTempBean> neChargeTempList = Lists.newArrayList();
        neChargeTemp.setChargeTempInformationType((short) 0x09);
        ParseException.addCompleteItems((byte)0x09);

        if(msgBody.readableBytes()>0) {
            storageSubSysNum = msgBody.readUnsignedByte();
            neChargeTemp.setStorageTempSubSysNum(storageSubSysNum);
            hasValidNum = ParseException.computerCount("storageTempSubSysNum",storageSubSysNum);
        }
        while(storageSubSysNum-->0&&msgBody.readableBytes()>=3){
            int num = 0;
            neChargeTempBean = new NEChargeTempBean();
            neChargeTempBean.setStorageSubSerial(msgBody.readUnsignedByte());
            hasValidNum = ParseException.computerCount("storageSubSerial",neChargeTempBean.getStorageSubSerial());
            int tempProbeNum = msgBody.readUnsignedShort();
            neChargeTempBean.setStorageTempProbeNum(tempProbeNum);
            hasValidNum = ParseException.computerCount("tempProbeNum",tempProbeNum);
            int[] probes = new int[tempProbeNum];
            while (num < tempProbeNum) {
                probes[num++] = msgBody.readUnsignedByte();
            }
            neChargeTempBean.setStorageTempAllProbeNums(probes);
            if (probes.length == 0) {
                logger.debug("probes length is 0!");
            } else {
                hasValidNum = ParseException.computerCount("probes", probes[0]);
            }
            neChargeTempList.add(neChargeTempBean);
        }
        neChargeTemp.setNeChargeTempBeanList(neChargeTempList);
        req_02.setNeChargeTemp(neChargeTemp);
    }

    private void do_vehicleData(Req_02 req_02, ByteBuf msgBody) {
        ByteBuf vehicleData;
        if (msgBody.readableBytes() >= 20) {
            vehicleData = msgBody.readBytes(20);
            NEVehicleBean neVehicleBean = do_vehicle(vehicleData);
            req_02.setNeVehicleBean(neVehicleBean);
        } else {
            logger.debug("不是完整的整车报文数据");
        }
    }

    private void do_driverMotorData(Req_02 req_02, ByteBuf msgBody) {
        short driverMotorNum = 0;
        NEDriverMotor neDriverMotor = new NEDriverMotor();

        //当前驱动电机数据存在
        if (msgBody.readableBytes() > 0) {
            //驱动电机个数
            driverMotorNum = msgBody.readUnsignedByte();
            neDriverMotor.setDriverMotorNumber(driverMotorNum);
            hasValidNum = ParseException.computerCount("driverMotorNum",driverMotorNum);
        } else {
            logger.warn("报文数据不足");
        }
        NEDriverMotor neDriverMotors = do_driverMotor(msgBody, driverMotorNum, neDriverMotor);
        req_02.setNeDriverMotor(neDriverMotors);
    }

    private void do_alarmData(Req_02 req_02, ByteBuf msgBody) {
        NEAlarmBean neAlarmBean = null;
        if (msgBody.readableBytes() >= 0) {
            neAlarmBean = do_alarm(msgBody);
        }
        req_02.setNeAlarmBean(neAlarmBean);
    }

    private void do_extremumData(Req_02 req_02, ByteBuf msgBody) {
        NEExtremumBean neExtremumBean = null;
        if (msgBody.readableBytes() >= 14) {
            neExtremumBean = do_extremum(msgBody);
        }
        req_02.setNeExtremumBean(neExtremumBean);
    }

    private void do_gpsData(Req_02 req_02, ByteBuf msgBody) {
        NEGpsBean neGpsBean = new NEGpsBean();
        neGpsBean.setGpsInformationType((short) 0x05);
        ParseException.addCompleteItems((byte)0x05);
        if (msgBody.readableBytes() >= 9) {
            short locationCode = msgBody.readUnsignedByte();
            //定位状态Code
            neGpsBean.setLocationCode(locationCode);
            hasValidNum = ParseException.computerCount("locationCode",locationCode);
            //定位状态
            neGpsBean.setLocations(NEStateCode2Name.locationStates(locationCode));
            //经度
            long longitude = msgBody.readUnsignedInt();
            neGpsBean.setLongitude(longitude);
            hasValidNum = ParseException.computerCount("longitude",longitude);
            //纬度
            long latitude = msgBody.readUnsignedInt();
            neGpsBean.setLatitude(latitude);
            hasValidNum = ParseException.computerCount("latitude",latitude);
            logger.debug(latitude * Math.pow(10, -6) + "," + longitude * Math.pow(10, -6));
        }
        req_02.setNeGpsBean(neGpsBean);
    }

    private void do_motorData(Req_02 req_02, ByteBuf msgBody) {
        NEEngineBean neEngineBean = new NEEngineBean();
        neEngineBean.setEngineInformationType((short) 0x04);
        ParseException.addCompleteItems((byte)0x04);
        if (msgBody.readableBytes() >= 5) {
            //发动机状态code
            short engineStateCode = msgBody.readUnsignedByte();
            neEngineBean.setEngineStateCode(engineStateCode);
            hasValidNum = ParseException.computerCount("engineStateCode",engineStateCode);
            //发动机状态 启动还是关闭
            neEngineBean.setEngineState(NEStateCode2Name.motorState(engineStateCode));
            //曲轴转速
            int speedOfCrankshaft = msgBody.readUnsignedShort();
            hasValidNum = ParseException.computerCount("speedOfCrankshaft",speedOfCrankshaft);
            if (speedOfCrankshaft >= 0 && speedOfCrankshaft <= 60000) {
                neEngineBean.setSpeedOfCrankshaft(speedOfCrankshaft);
            } else {
                neEngineBean.setSpeedOfCrankshaft(speedOfCrankshaft);
            }
            //燃料消耗率
            int specificFuelConsumption = msgBody.readUnsignedShort();
            if (specificFuelConsumption >= 0 && specificFuelConsumption <= 60000) {
                neEngineBean.setSpecificFuelConsumption(specificFuelConsumption);
                hasValidNum = ParseException.computerCount("specificFuelConsumption",specificFuelConsumption);
            } else {
                neEngineBean.setSpecificFuelConsumption(specificFuelConsumption);
            }
        }
        req_02.setNeEngineBean(neEngineBean);
    }

    private void do_fuelCellData(Req_02 req_02, ByteBuf msgBody) {
        ByteBuf fuelCellData;
        //处理燃料电池的电压 电流 消耗率
        NEFuelCellBean neFuelCellBean = new NEFuelCellBean();
        //燃料电池数据存在
        if (msgBody.readableBytes() > 0) {
            //燃料电池电压
            neFuelCellBean.setFuelCellVoltage(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("fuelCellVoltage",neFuelCellBean.getFuelCellVoltage());
            //燃料电池电流
            neFuelCellBean.setFuelCellCurrent(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("fuelCellCurrent",neFuelCellBean.getFuelCellCurrent());
            //燃料消耗率
            neFuelCellBean.setRateOfFuelConsumption(msgBody.readUnsignedShort());
            hasValidNum = ParseException.computerCount("rateOfFuelConsumption",neFuelCellBean.getRateOfFuelConsumption());

            //读取燃料电池温度探针总数
            /*msgBody.readBytes(probeNum);
            int probeN = bytes2OctNum(probeNum);*/
            int probeN = msgBody.readUnsignedShort();
            //燃料电池温度探针总数
            neFuelCellBean.setFuelCellProbeNumber(probeN);
            hasValidNum = ParseException.computerCount("probeN",probeN);

            //判断燃料电池温度探针数量合理范围 probeN >= 0 && probeN < 65531
            if (probeN >= 0 && probeN <= 65531) {
                //燃料电池数据解析
                if (msgBody.readableBytes() >= 10 + probeN) {
                    fuelCellData = msgBody.readBytes(10 + probeN);
                    NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                    req_02.setNeFuelCellBean(neFuelCellBean1);
                }
            } else{
                fuelCellData = msgBody.readBytes(10 + 1);
                NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                req_02.setNeFuelCellBean(neFuelCellBean1);
                logger.error("probe num is incorrect");
            }
        }
    }

    /**
     * 处理整车数据
     * @param msgBody
     * @return NEVehicleBean
     */
    private NEVehicleBean do_vehicle(ByteBuf msgBody) {
        NEVehicleBean vehicleBean = new NEVehicleBean();
        vehicleBean.setVehicleInformationType((short) 0x01);
        ParseException.addCompleteItems((byte)0x01);
        //车辆状态
        short vehicleStateCode = msgBody.readUnsignedByte();
        vehicleBean.setVehicleStatusCode(vehicleStateCode);
        vehicleBean.setVehicleStatus(NEStateCode2Name.vehicleState(vehicleStateCode));
        hasValidNum = ParseException.computerCount("vehicleStateCode",vehicleStateCode);

        //充电状态
        short chargeStateCode = msgBody.readUnsignedByte();
        vehicleBean.setChargingStatusCode(chargeStateCode);
        vehicleBean.setChargingStatus(NEStateCode2Name.chargeState(chargeStateCode));
        hasValidNum = ParseException.computerCount("chargeStateCode",chargeStateCode);

        //运行模式
        short runModeCode = msgBody.readUnsignedByte();
        vehicleBean.setRunModeCode(runModeCode);
        vehicleBean.setRunMode(NEStateCode2Name.runMode(runModeCode));
        hasValidNum = ParseException.computerCount("runModeCode",runModeCode);

        //车速
        int speed = msgBody.readUnsignedShort();
        vehicleBean.setSpeed(speed);
        hasValidNum = ParseException.computerCount("speed",speed);

        //累计里程
        long accMile = msgBody.readUnsignedInt();
        vehicleBean.setAccumulativeMile(accMile);
        hasValidNum = ParseException.computerCount("accMile",accMile);

        //总电压
        int totalVoltage = msgBody.readUnsignedShort();
        vehicleBean.setTotalVoltage(totalVoltage);
        hasValidNum = ParseException.computerCount("totalVoltage",totalVoltage);

        //总电流
        int totalElec = msgBody.readUnsignedShort();
        vehicleBean.setTotalElectricity(totalElec);
        hasValidNum = ParseException.computerCount("totalElec",totalElec);

        //SOC
        short soc = msgBody.readUnsignedByte();
        vehicleBean.setSoc(soc);
        hasValidNum = ParseException.computerCount("soc",soc);

        //DC状态
        short dcStateCode = msgBody.readUnsignedByte();
        vehicleBean.setDcStatusCode(dcStateCode);
        vehicleBean.setDcStatus(NEStateCode2Name.dcState(dcStateCode));
        hasValidNum = ParseException.computerCount("dcStateCode",dcStateCode);

        //档位
        short gearCode = msgBody.readUnsignedByte();
        vehicleBean.setGearCode(gearCode);
        vehicleBean.setGears(NEStateCode2Name.gearStates(gearCode));
        hasValidNum = ParseException.computerCount("gearCode",gearCode);

        //绝缘电阻
        int insulationResistance = msgBody.readUnsignedShort();
        vehicleBean.setInsulationResistance(insulationResistance);
        hasValidNum = ParseException.computerCount("insulationResistance",insulationResistance);
        //加速踏板行程值
        short acceleratorPedal = msgBody.readUnsignedByte();
        vehicleBean.setAcceleratorPedal(acceleratorPedal);
        hasValidNum = ParseException.computerCount("acceleratorPedal",acceleratorPedal);
        //制动踏板状态
        short brakePedalStatus = msgBody.readUnsignedByte();
        vehicleBean.setBrakePedalStatus(brakePedalStatus);
        hasValidNum = ParseException.computerCount("brakePedalStatus",brakePedalStatus);
        return vehicleBean;
    }

    /**
     * 处理驱动电机数据
     * @param msgBody
     * @param driverMotorNum
     */
    private NEDriverMotor do_driverMotor(ByteBuf msgBody, short driverMotorNum, NEDriverMotor driverMotor) {
        int currentDriverMotorNo = 0;
        driverMotor.setDriveMotorInformationType((short) 0x02);
        ParseException.addCompleteItems((byte)0x02);
        NEDriverMotorBean neDriverMotorBean;
        //存储驱动电机总成信息列表
        List<NEDriverMotorBean> neDriverMotorBeanList = Lists.newArrayList();
        //驱动电机总成信息
        while ((currentDriverMotorNo < driverMotorNum) && msgBody.readableBytes() >= 12) {
            neDriverMotorBean = new NEDriverMotorBean();
            //当前标号
            currentDriverMotorNo++;
            //驱动电机序号
            short driverMotorSerial = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorSerial(driverMotorSerial);
            hasValidNum = ParseException.computerCount("driverMotorSerial",driverMotorSerial);

            //驱动电机状态码
            short driverMotorStateCode = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorStateCode(driverMotorStateCode);
            hasValidNum = ParseException.computerCount("driverMotorStateCode",driverMotorStateCode);
            //驱动电机状态
            neDriverMotorBean.setDriverMotorState(NEStateCode2Name.driverMotor(driverMotorStateCode));
            //驱动电机控制器温度
            short driverMotorCT = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorControllerTemperature(driverMotorCT);
            hasValidNum = ParseException.computerCount("driverMotorCT",driverMotorCT);

            //驱动电机转速
            int driverMotorRPM = msgBody.readUnsignedShort();
            neDriverMotorBean.setDriverMotorRPM(driverMotorRPM);
            hasValidNum = ParseException.computerCount("driverMotorRPM",driverMotorRPM);

            //驱动电机转矩
            int driverMotorTorque = msgBody.readUnsignedShort();
            neDriverMotorBean.setDriverMotorTorque(driverMotorTorque);
            hasValidNum = ParseException.computerCount("driverMotorTorque",driverMotorTorque);

            //驱动电机温度
            short driverMotorT = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorTemperature(driverMotorT);
            hasValidNum = ParseException.computerCount("driverMotorT",driverMotorT);

            //电机控制器输入电压
            int motorControllerInputV = msgBody.readUnsignedShort();
            neDriverMotorBean.setMotorControllerInputVoltage(motorControllerInputV);
            hasValidNum = ParseException.computerCount("motorControllerInputV",motorControllerInputV);

            //电机控制器直流母线电流
            int motorControllerNegativeDCC = msgBody.readUnsignedShort();
            neDriverMotorBean.setMotorControllerNegativeDCCurrent(motorControllerNegativeDCC);
            hasValidNum = ParseException.computerCount("motorControllerNegativeDCC",motorControllerNegativeDCC);

            //将当前驱动电机总成信息存入List内存
            neDriverMotorBeanList.add(neDriverMotorBean);
        }
        //将驱动电机总成信息列表存储到驱动电机中
        driverMotor.setNEDriverMotorBeans(neDriverMotorBeanList);
        return driverMotor;
    }

    /**
     * 处理燃料电池数据
     *
     * @param msgBody
     */
    private NEFuelCellBean do_fuelCell(ByteBuf msgBody, NEFuelCellBean neFuelCellBean, int probeNumber) {
        neFuelCellBean.setFuelCellInformationType((short) 0x03);
        ParseException.addCompleteItems((byte)0x03);
        //探针温度值列表
        ByteBuf probeTemperatureBuf = msgBody.readBytes(probeNumber);
        neFuelCellBean.setProbeTemperatures(bytes2ShortArr(probeTemperatureBuf, probeNumber));
        hasValidNum = ParseException.computerCount("probeTemperatureBuf",0);
        //氢系统中最高温度
        int maxTempInHydrogen = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxTemperatureInHydrogenSystem(maxTempInHydrogen);
        hasValidNum = ParseException.computerCount("maxTempInHydrogen",maxTempInHydrogen);

        //氢系统中最高温度探针代号
        short maxTempProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxTemperatureProbeSerial(maxTempProbeSerial);
        hasValidNum = ParseException.computerCount("maxTempProbeSerial",maxTempProbeSerial);

        //氢气最高浓度
        int maxHydrogenConcentration = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxHydrogenConcentration(maxHydrogenConcentration);
        hasValidNum = ParseException.computerCount("maxHydrogenConcentration",maxHydrogenConcentration);

        //氢气最高浓度传感器代号
        short maxHydrogenProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxHydrogenConcentrationProbeSerial(maxHydrogenProbeSerial);
        hasValidNum = ParseException.computerCount("maxHydrogenProbeSerial",maxHydrogenProbeSerial);

        //氢气最高压力
        int maxPressureHydrogen = msgBody.readUnsignedShort();
        neFuelCellBean.setMaxPressureHydrogen(maxPressureHydrogen);
        hasValidNum = ParseException.computerCount("maxPressureHydrogen",maxPressureHydrogen);

        //氢气最高压力传感器代号
        short maxPressureHydrogenProbeSerial = msgBody.readUnsignedByte();
        neFuelCellBean.setMaxPressureHydrogenProbeSerial(maxPressureHydrogenProbeSerial);
        hasValidNum = ParseException.computerCount("maxPressureHydrogenProbeSerial",maxPressureHydrogenProbeSerial);

        //高压DC/DC状态Code
        short dcState = msgBody.readUnsignedByte();
        neFuelCellBean.setHighPressDCStateCode(dcState);
        hasValidNum = ParseException.computerCount("dcState",dcState);

        //高压DC/DC状态
        neFuelCellBean.setHighPressDCState(NEStateCode2Name.fuelCellDCState(dcState));
        return neFuelCellBean;
    }

    /**
     * 处理极值数据
     *
     * @param msgBody
     */
    private NEExtremumBean do_extremum(ByteBuf msgBody) {
        NEExtremumBean neExtremumBean = new NEExtremumBean();
        neExtremumBean.setExtremumInformationType((short) 0x06);
        ParseException.addCompleteItems((byte)0x06);
        //最高电压电池子系统号
        short highVBatterySubNum = msgBody.readUnsignedByte();
        neExtremumBean.setHighVBatterySubNum(highVBatterySubNum);
        hasValidNum = ParseException.computerCount("highVBatterySubNum",highVBatterySubNum);

        //最高电压电池单体代号
        short highVBatteryCellCode = msgBody.readUnsignedByte();
        neExtremumBean.setHighVBatteryCellCode(highVBatteryCellCode);
        hasValidNum = ParseException.computerCount("highVBatteryCellCode",highVBatteryCellCode);

        //电池单体电压最高值
        int maximumBatteryVoltage = msgBody.readUnsignedShort();
        neExtremumBean.setMaximumBatteryVoltage(maximumBatteryVoltage);
        hasValidNum = ParseException.computerCount("maximumBatteryVoltage",maximumBatteryVoltage);

        //最低电压电池子系统号
        short lowVBatterySubNum = msgBody.readUnsignedByte();
        neExtremumBean.setLowVBatterySubNum(lowVBatterySubNum);
        hasValidNum = ParseException.computerCount("lowVBatterySubNum",lowVBatterySubNum);

        //最低电压电池单体代号
        short lowVBatteryCellCode = msgBody.readUnsignedByte();
        neExtremumBean.setLowVBatteryCellCode(lowVBatteryCellCode);
        hasValidNum = ParseException.computerCount("lowVBatteryCellCode",lowVBatteryCellCode);

        //电池单体电压最低值
        int minimumBatteryVoltage = msgBody.readUnsignedShort();
        neExtremumBean.setMinimumBatteryVoltage(minimumBatteryVoltage);
        hasValidNum = ParseException.computerCount("minimumBatteryVoltage",minimumBatteryVoltage);

        //最高温度子系统号
        short highTemperatureSubNum = msgBody.readUnsignedByte();
        neExtremumBean.setHighTemperatureSubNum(highTemperatureSubNum);
        hasValidNum = ParseException.computerCount("highTemperatureSubNum",highTemperatureSubNum);

        //最高温度探针序号
        short highTemperatureProbeSerial = msgBody.readUnsignedByte();
        neExtremumBean.setHighTemperatureProbeSerial(highTemperatureProbeSerial);
        hasValidNum = ParseException.computerCount("highTemperatureProbeSerial",highTemperatureProbeSerial);

        //最高温度值
        short maxTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setMaxTemperatureValue(maxTemperatureValue);
        hasValidNum = ParseException.computerCount("maxTemperatureValue",maxTemperatureValue);

        //最低温度子系统号
        short lowTemperatureSubnum = msgBody.readUnsignedByte();
        neExtremumBean.setLowTemperatureSubNum(lowTemperatureSubnum);
        hasValidNum = ParseException.computerCount("lowTemperatureSubnum",lowTemperatureSubnum);

        //最低温度探针序号
        short lowTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setLowTemperatureProbeSerial(lowTemperatureValue);
        hasValidNum = ParseException.computerCount("lowTemperatureValue",lowTemperatureValue);

        //最低温度值
        short minTemperatureValue = msgBody.readUnsignedByte();
        neExtremumBean.setMinTemperatureValue(minTemperatureValue);
        hasValidNum = ParseException.computerCount("minTemperatureValue",minTemperatureValue);

        return neExtremumBean;
    }

    //告警消息数据处理
    private NEAlarmBean do_alarm(ByteBuf msgBody) {
        NEAlarmBean neAlarmBean = new NEAlarmBean();
        neAlarmBean.setAlarmInformationType((short) 0x07);
        ParseException.addCompleteItems((byte)0x07);
        //可充电储能装置故障代码列表
        List<Long> rechargeableStorageCodeList = Lists.newArrayList();
        //驱动电机故障代码列表
        List<Long> driverMotorFailureCodeList = Lists.newArrayList();
        //发动机故障代码列表
        List<Long> engineFailureCodeList = Lists.newArrayList();
        //其他故障代码列表
        List<Long> otherFailureCodeList = Lists.newArrayList();
        //最高报警等级
        short maxAlarmRate = msgBody.readUnsignedByte();
        hasValidNum = ParseException.computerCount("maxAlarmRate",maxAlarmRate);
        if (maxAlarmRate >= 0 && maxAlarmRate <= 3) {
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate==0?"无故障":(maxAlarmRate==1?"1级故障":(maxAlarmRate==2?"2级故障":"3级故障")));
        } else {
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate==0?"无故障":(maxAlarmRate==1?"1级故障":(maxAlarmRate==2?"2级故障":"3级故障")));
            new ParseException(maxAlarmRate, "最高报警等级");

        }
        //通用报警标志
        long alarmIdentification = msgBody.readUnsignedInt();
        neAlarmBean.setAlarmIdentification(alarmIdentification);
        neAlarmBean.setAlarmIdentificationList(NEStateCode2Name.parseGeneralAlarms((alarmIdentification)));
        hasValidNum = ParseException.computerCount("alarmIdentification",alarmIdentification);

        //可充电储能装置故障总数N1
        short rechargeAbleN1 = msgBody.readUnsignedByte();
        neAlarmBean.setRechargeableStorageDeviceN1(rechargeAbleN1);
        hasValidNum = ParseException.computerCount("rechargeAbleN1",rechargeAbleN1);
        if (rechargeAbleN1 > 0 && rechargeAbleN1 <= 252) {
            while (rechargeAbleN1-- > 0) {
                rechargeableStorageCodeList.add(msgBody.readUnsignedInt());
            }
            //可充电储能装置故障代码列表
            neAlarmBean.setRechargeableStorageCodeList(rechargeableStorageCodeList);
            hasValidNum = ParseException.computerCount("rechargeableStorageCodeList",rechargeableStorageCodeList.get(0));
        } else {
            while (rechargeAbleN1-- > 0) {
                rechargeableStorageCodeList.add(msgBody.readUnsignedInt());
            }
            //可充电储能装置故障代码列表
            neAlarmBean.setRechargeableStorageCodeList(rechargeableStorageCodeList);
            new ParseException(rechargeAbleN1, "可充电储能装置故障总数N1");
        }

        //驱动电机故障总数N2
        short driverMotorfailN2 = msgBody.readUnsignedByte();
        neAlarmBean.setDriverMotorFailureN2(driverMotorfailN2);
        hasValidNum = ParseException.computerCount("driverMotorfailN2",driverMotorfailN2);

        if (driverMotorfailN2 > 0 && driverMotorfailN2 <= 252) {
            while (driverMotorfailN2-- > 0) {
                driverMotorFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //驱动电机故障代码列表
            neAlarmBean.setDriverMotorFailureCodeList(driverMotorFailureCodeList);
            hasValidNum = ParseException.computerCount("driverMotorFailureCodeList",driverMotorFailureCodeList.get(0));
        } else {
            while (driverMotorfailN2-- > 0) {
                driverMotorFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //驱动电机故障代码列表
            neAlarmBean.setDriverMotorFailureCodeList(driverMotorFailureCodeList);
            new ParseException(driverMotorfailN2, "驱动电机故障总数N2");
        }
        //发动机故障总数N3
        short engineFailN3 = msgBody.readUnsignedByte();
        neAlarmBean.setEngineFailureN3(engineFailN3);
        hasValidNum = ParseException.computerCount("engineFailN3",engineFailN3);
        if (engineFailN3 > 0 && engineFailN3 <= 252) {
            while (engineFailN3-- > 0) {
                engineFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //发动机故障代码列表
            neAlarmBean.setEngineFailureCodeList(engineFailureCodeList);
            hasValidNum = ParseException.computerCount("engineFailureCodeList",engineFailureCodeList.get(0));
        } else {
            while (engineFailN3-- > 0) {
                engineFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //发动机故障代码列表
            neAlarmBean.setEngineFailureCodeList(engineFailureCodeList);
            new ParseException(engineFailN3, "发动机故障总数N3");
        }
        //其他故障总数N4
        short otherFailN4 = msgBody.readUnsignedByte();
        neAlarmBean.setOtherFailureN4(otherFailN4);
        hasValidNum = ParseException.computerCount("otherFailN4",otherFailN4);

        if (otherFailN4 > 0 && otherFailN4 <= 252) {
            while (otherFailN4-- > 0) {
                otherFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //其他故障代码列表
            neAlarmBean.setOtherFailureCodeList(otherFailureCodeList);
            hasValidNum = ParseException.computerCount("otherFailureCodeList",otherFailureCodeList.get(0));
        } else {
            while (otherFailN4-- > 0) {
                otherFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //其他故障代码列表
            neAlarmBean.setOtherFailureCodeList(otherFailureCodeList);
            new ParseException(otherFailN4, "其他故障总数N4");
        }
        return neAlarmBean;
    }

    public NEMessage2KafkaBean getNeMsgObject(Req_02 req) {
        //获取到解析后要入kafka的对象
        NEMessage2KafkaBean neMsg = new NEMessage2KafkaBean();
        //collectTime
        neMsg.setCollectTime(collecteTime);
        //vin
        neMsg.setVin(req.getVin());
        neMsg.setVehicleType(req.getVehicleType());
        neMsg.setVehicleCompany(req.getVehicleCompany());
        neMsg.setVehicleUse(req.getVehicleUse());
        neMsg.setOriginalMsg(req.getOriginalMsg());
        //receiveTime
        neMsg.setReceiveTime(req.getReceiveTime());
        //有效报文数
        neMsg.setHasValidNum(req.getHasValidNum());
        //报文完整性
        neMsg.setIntact(req.getIntact());
        //commandSign
        neMsg.setMsgId("32960_0" + req.getCommandSign());
        if (null != req.getNeVehicleBean() || null != req.getNeAlarmBean() || null != req.getNeDriverMotor()
                || null != req.getNeEngineBean() || null != req.getNeExtremumBean() || null != req.getNeFuelCellBean() ||
                null != req.getNeGpsBean()) {
            NEVehicleBean neVehicle = req.getNeVehicleBean();
            if (null != neVehicle) {
                neMsg.setNeVehicleBean(neVehicle);
            }
            //获取驱动电机实时数据实体
            NEDriverMotor neDriverMotor = req.getNeDriverMotor();
            if (null != neDriverMotor) {
                neMsg.setNeDriverMotor(neDriverMotor);
            }
            //获取燃料电池实时数据实体
            NEFuelCellBean neFuelCellBean = req.getNeFuelCellBean();
            if (null != neFuelCellBean) {
                neMsg.setNeFuelCellBean(neFuelCellBean);
            }
            //获取发动机实时数据实体
            NEEngineBean neEngineBean = req.getNeEngineBean();
            if (null != neEngineBean) {
                neMsg.setNeEngineBean(neEngineBean);
            }
            //获取车辆位置数据实体
            NEGpsBean neGpsBean = req.getNeGpsBean();
            if (null != neGpsBean) {
                neMsg.setNeGpsBean(neGpsBean);
            }
            //获取实时极值数据实体
            NEExtremumBean neExtremumBean = req.getNeExtremumBean();
            if (null != neExtremumBean) {
                neMsg.setNeExtremumBean(neExtremumBean);
            }
            //获取实时报警数据实体
            NEAlarmBean neAlarmBean = req.getNeAlarmBean();
            if (null != neAlarmBean) {
                neMsg.setNeAlarmBean(neAlarmBean);
            }
            //获取实时可充电储能电压
            NEChargeVoltage neChargeVoltage = req.getNeChargeVoltage();
            if(null!= neChargeVoltage){
                neMsg.setNeChargeVoltage(neChargeVoltage);
            }
            //获取实时可充电储能温度
            NEChargeTemp neChargeTemp = req.getNeChargeTemp();
            if(null!=neChargeTemp){
                neMsg.setNeChargeTemp(neChargeTemp);
            }
        }
        return neMsg;
    }

    public String getRealDataObject(Req_02 req, Map<String, String> headers, byte[] eventBody) {
        //将收到的报文消息体处理一下,往后推开始解析
        String body = null;
        ProtoMsg protoMsg = new ProtoMsg();
        if (eventBody.length > 0) {
            protoMsg.bytes = eventBody;
        }
        if (headers.get(HEADER_KEY) != null) {
            String[] keys = headers.get(HEADER_KEY).split("##");
            protoMsg.vin = keys[0];
            protoMsg.commandSign = Byte.parseByte(keys[1]);
        }
        //JSON parse ByteBuf lose, deal with it.
        ByteBuf protocolData = Unpooled.copiedBuffer(protoMsg.bytes);
        protoMsg.dataBuf = protocolData;
        logger.debug("protoMsg:{},dataBuf readable byte size:{}.", protoMsg.toString(), protoMsg.dataBuf.readableBytes());
        if (null != protocolData && protocolData.readableBytes() > 0) {
            body = this.doUpMsg(protoMsg);
        }
        return body;
    }
}
