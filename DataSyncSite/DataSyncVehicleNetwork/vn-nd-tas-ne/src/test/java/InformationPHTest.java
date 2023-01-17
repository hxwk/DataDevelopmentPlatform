import com.google.common.collect.Lists;
import com.yaxon.vn.nd.ne.tas.common.ByteBufCustomTool;
import com.yaxon.vn.nd.ne.tas.common.InformationTypeCode;
import com.yaxon.vn.nd.ne.tas.common.NEStateCode2Name;
import com.yaxon.vn.nd.ne.tas.common.ProtoUtil;
import com.yaxon.vn.nd.ne.tas.exception.ParseException;
import com.yaxon.vn.nd.ne.tas.handler.InformationPH;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tbp.si.ne.*;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.yaxon.vn.nd.ne.tas.common.ByteBufCustomTool.bytes2OctNum;
import static com.yaxon.vn.nd.ne.tas.common.ByteBufCustomTool.bytes2ShortArr;

/**
 * @author JianKang
 * @date 2018/3/21
 * @description
 */
public class InformationPHTest {
    private static final Logger logger = LoggerFactory.getLogger(InformationPH.class);
    private static long collecteTime;
    private static short informationType;
    public static void main(String[] args) {
        //注册当前类
        InformationPHTest parse = new InformationPHTest();
        ProtoMsg upMsg = new ProtoMsg();
        //模拟16进制到字符串
        //String s = "1202080D31380103030107FF007FFFFF0D20274C31013607FF3131020101047F4E2007FF7F07FF2744050106CCBFE801D08D680601010DAD01020DAC01014401014407000000000000000000";
        //03 fuel Cell
          String s = "12 05 08 14 31 10 01 01 03 01 00 62 00 00 50 B8 0D 77 27 74 1E 02 2E 07 D0 04 00 02 01 01 03 60 00 00 26 2A 63 0D 34 27 67 05 01 06 CE 7C D6 01 D1 7E E8 06 01 3E 0E 0A 01 4E 0E 03 01 01 4C 01 1E 47 07 03 00 04 00 20 03 03 D3 03 59 F4 C0 00 00 00 00 00 00 00 00 00 08 01 01 0D 77 27 74 00 60 00 01 60 0E 04 0E 06 0E 07 0E 07 0E 07 0E 05 0E 06 0E 07 0E 05 0E 08 0E 07 0E 08 0E 08 0E 07 0E 07 0E 07 0E 06 0E 07 0E 07 0E 09 0E 07 0E 07 0E 05 0E 08 0E 05 0E 08 0E 06 0E 06 0E 04 0E 06 0E 05 0E 06 0E 04 0E 05 0E 06 0E 07 0E 07 0E 08 0E 08 0E 07 0E 04 0E 07 0E 07 0E 07 0E 07 0E 06 0E 07 0E 07 0E 07 0E 09 0E 07 0E 07 0E 07 0E 07 0E 09 0E 08 0E 05 0E 06 0E 09 0E 07 0E 08 0E 0A 0E 09 0E 0A 0E 08 0E 09 0E 0A 0E 0A 0E 0A 0E 08 0E 08 0E 08 0E 06 0E 07 0E 09 0E 08 0E 04 0E 03 0E 04 0E 03 0E 07 0E 08 0E 07 0E 07 0E 0A 0E 09 0E 08 0E 08 0E 06 0E 08 0E 09 0E 09 0E 08 0E 08 0E 09 0E 0A 09 01 01 00 30 4C 4C 4C 4B 4B 4A 4A 4A 49 49 49 49 4A 49 48 48 48 48 49 48 48 48 49 48 48 48 48 48 48 47 48 47 48 48 49 48 49 48 48 48 48 48 48 48 4B 4B 4B 4B 38";
        //String s = "110C190D052C0103030107FF007FFFFF0D20274C31013607FF3131020101FF7F4E2007FF7F07FF2744050006CE7F5F01D17C970601010DAD01020DAC010144010144";
        //16进制字符串转成ByteBuf
        ByteBuf bb = ByteBufCustomTool.hexStringToByteBuf(s);
        upMsg.dataBuf = bb;
        //实时上报数据解析
        parse.do_realDataOrAddData(upMsg);
    }

    /**
     * 处理实时数据和附加上传数据
     * @param upMsg
     */
    public void do_realDataOrAddData(final ProtoMsg upMsg) {
        Req_02 req_02 = new Req_02();
        ByteBuf msgBody = upMsg.dataBuf;
        req_02.setVin(upMsg.vin);
        //采集时间
        if (msgBody.readableBytes() > 0) {
            collecteTime = ProtoUtil.readTimeNE(msgBody.readBytes(6));
            req_02.setCollectTime(collecteTime);
        } else {
            logger.debug("实时数据无效");
        }
        while (msgBody.readableBytes() > 0) {
            //信息类型标识
            informationType = Byte.parseByte(String.valueOf(msgBody.readByte()), 16);
            //信息类型
            String inforType = NEStateCode2Name.hex2String(informationType);
            logger.debug("inforType:{}", inforType);

            //1.整车数据解析
            if (InformationTypeCode.VEHICLEDATA.getTypeCode().equals(inforType)) {
                ByteBuf vehicleData;
                if (msgBody.readableBytes() >= 20) {
                    vehicleData = msgBody.readBytes(20);
                    NEVehicleBean neVehicleBean = do_vehicle(vehicleData);
                    req_02.setNeVehicleBean(neVehicleBean);
                } else {
                    logger.debug("不是完整的整车报文数据");
                }
            }

            //2.驱动电机数据解析
            else if (InformationTypeCode.DRIVERMOTOR.getTypeCode().equals(inforType)) {
                short driverMotorNum = 0;
                NEDriverMotor neDriverMotor = new NEDriverMotor();

                //当前驱动电机数据存在
                if (msgBody.readableBytes() > 0) {
                    //驱动电机个数
                    driverMotorNum = msgBody.readUnsignedByte();
                    neDriverMotor.setDriverMotorNumber(driverMotorNum);
                } else {
                    logger.warn("报文数据不足");
                }
                NEDriverMotor neDriverMotors =  do_driverMotor(msgBody, driverMotorNum, neDriverMotor);
                req_02.setNeDriverMotor(neDriverMotors);
            }

            //3.燃料电池数据解析
            else if (InformationTypeCode.FUELCELL.getTypeCode().equals(inforType)) {
                ByteBuf fuelCellData;
                //处理燃料电池的电压 电流 消耗率
                NEFuelCellBean neFuelCellBean = new NEFuelCellBean();
                byte[] probeNum = new byte[2];

                //燃料电池数据存在
                if(msgBody.readableBytes() > 0){
                    //燃料电池电压
                    neFuelCellBean.setFuelCellVoltage(msgBody.readUnsignedShort());
                    //燃料电池电流
                    neFuelCellBean.setFuelCellCurrent(msgBody.readUnsignedShort());
                    //燃料消耗率
                    neFuelCellBean.setRateOfFuelConsumption(msgBody.readUnsignedShort());
                    //读取燃料电池温度探针总数
                    int probeN = msgBody.readUnsignedShort();
                    //燃料电池温度探针总数
                    neFuelCellBean.setFuelCellProbeNumber(probeN);
                    //判断燃料电池温度探针数量合理范围 probeN >= 0 && probeN < 65531
                    if(probeN >= 0 && probeN <= 65531){
                        //燃料电池数据解析
                        if(msgBody.readableBytes() >= 10 + probeN){
                            fuelCellData = msgBody.readBytes(10 + probeN);
                            NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                            req_02.setNeFuelCellBean(neFuelCellBean1);
                        }
                    }else{
                        if(probeN == 65535 ||probeN == 65534){
                            fuelCellData = msgBody.readBytes(10 + 1);
                            NEFuelCellBean neFuelCellBean1 = do_fuelCell(fuelCellData, neFuelCellBean, probeN);
                            req_02.setNeFuelCellBean(neFuelCellBean1);
                        }
                        logger.error("probe num is incorrect");
                    }
                }
            }

            //4.发动机部分数据解析
            else if (InformationTypeCode.MOTORDATA.getTypeCode().equals(inforType)) {
                NEEngineBean neEngineBean = new NEEngineBean();
                neEngineBean.setEngineInformationType((short)0x04);
                if(msgBody.readableBytes() >= 5){
                    //发动机状态code
                    short engineStateCode = msgBody.readUnsignedByte();
                    neEngineBean.setEngineStateCode(engineStateCode);
                    //发动机状态 启动还是关闭
                    neEngineBean.setEngineState(NEStateCode2Name.motorState(engineStateCode));
                    //曲轴转速
                    int speedOfCrankshaft = msgBody.readUnsignedShort();
                    if (speedOfCrankshaft >= 0 && speedOfCrankshaft <= 60000) {
                        neEngineBean.setSpeedOfCrankshaft(speedOfCrankshaft);
                    }
                    //燃料消耗率
                    int specificFuelConsumption = msgBody.readUnsignedShort();
                    if(specificFuelConsumption >= 0 && specificFuelConsumption <= 60000){
                        neEngineBean.setSpecificFuelConsumption(specificFuelConsumption);
                    }
                }
                req_02.setNeEngineBean(neEngineBean);
            }

            //5.车辆位置数据解析
            else if (InformationTypeCode.NEGPS.getTypeCode().equals(inforType)) {
                NEGpsBean neGpsBean = new NEGpsBean();
                neGpsBean.setGpsInformationType((short)0x05);
                if(msgBody.readableBytes() >= 9){
                    short locationCode = msgBody.readUnsignedByte();
                    //定位状态Code
                    neGpsBean.setLocationCode(locationCode);
                    //定位状态
                    neGpsBean.setLocations(NEStateCode2Name.locationStates(locationCode));
                    //经度
                    long longitude = msgBody.readUnsignedInt();
                    neGpsBean.setLongitude(longitude);
                    //纬度
                    long latitude = msgBody.readUnsignedInt();
                    neGpsBean.setLatitude(latitude);
                    logger.debug(latitude*Math.pow(10,-6)+","+longitude*Math.pow(10,-6));
                }
                req_02.setNeGpsBean(neGpsBean);
            }

            //6.极值数据解析
            else if (InformationTypeCode.EXTREMUM.getTypeCode().equals(inforType)) {
                NEExtremumBean neExtremumBean = null;
                if(msgBody.readableBytes()>=14){
                    neExtremumBean = do_extremum(msgBody);
                }
                req_02.setNeExtremumBean(neExtremumBean);
            }

            //7.报警数据解析
            else if (InformationTypeCode.ALARM.getTypeCode().equals(inforType)) {
                NEAlarmBean neAlarmBean=null;
                if(msgBody.readableBytes()>=0){
                    neAlarmBean = do_alarm(msgBody);
                }
                req_02.setNeAlarmBean(neAlarmBean);
            }
        }
        logger.info("Req_02:{}",req_02.toString());
    }

    /**
     * 处理整车数据
     *
     * @param msgBody
     * @return NEVehicleBean
     */
    private NEVehicleBean do_vehicle(ByteBuf msgBody) {
        NEVehicleBean vehicleBean = new NEVehicleBean();
        vehicleBean.setVehicleInformationType((short)0x01);
        //车辆状态
        short vehicleStateCode = msgBody.readUnsignedByte();
        vehicleBean.setVehicleStatusCode(vehicleStateCode);
        vehicleBean.setVehicleStatus(NEStateCode2Name.vehicleState(vehicleStateCode));
        //充电状态
        short chargeStateCode = msgBody.readUnsignedByte();
        vehicleBean.setChargingStatusCode(chargeStateCode);
        vehicleBean.setChargingStatus(NEStateCode2Name.chargeState(chargeStateCode));
        //运行模式
        short runModeCode = msgBody.readUnsignedByte();
        vehicleBean.setRunModeCode(runModeCode);
        vehicleBean.setRunMode(NEStateCode2Name.runMode(runModeCode));
        //车速
        vehicleBean.setSpeed(msgBody.readUnsignedShort());
        //累计里程
        vehicleBean.setAccumulativeMile(msgBody.readUnsignedInt());
        //总电压
        vehicleBean.setTotalVoltage(msgBody.readUnsignedShort());
        //总电流
        vehicleBean.setTotalElectricity(msgBody.readUnsignedShort());
        //SOC
        vehicleBean.setSoc(msgBody.readUnsignedByte());
        //DC状态
        short dcStateCode = msgBody.readUnsignedByte();
        vehicleBean.setDcStatusCode(dcStateCode);
        vehicleBean.setDcStatus(NEStateCode2Name.dcState(dcStateCode));
        //档位
        short gearCode = msgBody.readUnsignedByte();
        vehicleBean.setGearCode(gearCode);
        vehicleBean.setGears(NEStateCode2Name.gearStates(gearCode));
        //绝缘电阻
        vehicleBean.setInsulationResistance(msgBody.readUnsignedShort());
        //加速踏板行程值
        vehicleBean.setAcceleratorPedal(msgBody.readUnsignedByte());
        //制动踏板状态
        vehicleBean.setBrakePedalStatus(msgBody.readUnsignedByte());
        //预留
        //vehicleBean.setReserved(msgBody.readUnsignedShort());
        return vehicleBean;
    }

    /**
     * 处理驱动电机数据
     *
     * @param msgBody
     * @param driverMotorNum
     */
    private NEDriverMotor do_driverMotor(ByteBuf msgBody, short driverMotorNum, NEDriverMotor driverMotor) {
        int currentDriverMotorNo = 0;
        driverMotor.setDriveMotorInformationType((short)0x02);
        NEDriverMotorBean neDriverMotorBean = new NEDriverMotorBean();
        //存储驱动电机总成信息列表
        List<NEDriverMotorBean> neDriverMotorBeanList = Lists.newArrayList();
        //驱动电机总成信息
        while ((currentDriverMotorNo < driverMotorNum) && msgBody.readableBytes() >= 12) {
            //当前标号
            currentDriverMotorNo++;
            //驱动电机序号
            neDriverMotorBean.setDriverMotorSerial(msgBody.readUnsignedByte());
            //驱动电机状态码
            short driverMotorStateCode = msgBody.readUnsignedByte();
            neDriverMotorBean.setDriverMotorStateCode(driverMotorStateCode);
            //驱动电机状态
            neDriverMotorBean.setDriverMotorState(NEStateCode2Name.driverMotor(driverMotorStateCode));
            //驱动电机控制器温度
            neDriverMotorBean.setDriverMotorControllerTemperature(msgBody.readUnsignedByte());
            //驱动电机转速
            neDriverMotorBean.setDriverMotorRPM(msgBody.readUnsignedShort());
            //驱动电机转矩
            neDriverMotorBean.setDriverMotorTorque(msgBody.readUnsignedShort());
            //驱动电机温度
            neDriverMotorBean.setDriverMotorTemperature(msgBody.readUnsignedByte());
            //电机控制器输入电压
            neDriverMotorBean.setMotorControllerInputVoltage(msgBody.readUnsignedShort());
            //电机控制器直流母线电流
            neDriverMotorBean.setMotorControllerNegativeDCCurrent(msgBody.readUnsignedShort());

            //将当前驱动电机总成信息存入List内存
            neDriverMotorBeanList.add(neDriverMotorBean);
        }
        //将驱动电机总成信息列表存储到驱动电机中
        driverMotor.setNEDriverMotorBeans(neDriverMotorBeanList);
        return driverMotor;
    }

    /**
     * 处理燃料电池数据
     * @param msgBody
     */
    private NEFuelCellBean do_fuelCell(ByteBuf msgBody,NEFuelCellBean neFuelCellBean,int probeNumber) {
        neFuelCellBean.setFuelCellInformationType((short)0x03);
        //探针温度值列表
        ByteBuf probeTemperatureBuf = msgBody.readBytes(probeNumber);
        neFuelCellBean.setProbeTemperatures(bytes2ShortArr(probeTemperatureBuf, probeNumber));
        //氢系统中最高温度
        neFuelCellBean.setMaxTemperatureInHydrogenSystem(msgBody.readUnsignedShort());
        //氢系统中最高温度探针代号
        neFuelCellBean.setMaxTemperatureProbeSerial(msgBody.readUnsignedByte());
        //氢气最高浓度
        neFuelCellBean.setMaxHydrogenConcentration(msgBody.readUnsignedShort());
        //氢气最高浓度传感器代号
        neFuelCellBean.setMaxHydrogenConcentrationProbeSerial(msgBody.readUnsignedByte());
        //氢气最高压力
        neFuelCellBean.setMaxPressureHydrogen(msgBody.readUnsignedShort());
        //氢气最高压力传感器代号
        neFuelCellBean.setMaxPressureHydrogenProbeSerial(msgBody.readUnsignedByte());
        //高压DC/DC状态Code
        short dcState = msgBody.readUnsignedByte();
        neFuelCellBean.setHighPressDCStateCode(dcState);
        //高压DC/DC状态
        neFuelCellBean.setHighPressDCState(NEStateCode2Name.fuelCellDCState(dcState));
        return neFuelCellBean;
    }

    /**
     * 处理极值数据
     * @param msgBody
     */
    private NEExtremumBean do_extremum(ByteBuf msgBody) {
        NEExtremumBean neExtremumBean = new NEExtremumBean();
        neExtremumBean.setExtremumInformationType((short)0x06);
        //最高电压电池子系统号
        neExtremumBean.setHighVBatterySubNum(msgBody.readUnsignedByte());
        //最高电压电池单体代号
        neExtremumBean.setHighVBatteryCellCode(msgBody.readUnsignedByte());
        //电池单体电压最高值
        neExtremumBean.setMaximumBatteryVoltage(msgBody.readUnsignedShort());
        //最低电压电池子系统号
        neExtremumBean.setLowVBatterySubNum(msgBody.readUnsignedByte());
        //最低电压电池单体代号
        neExtremumBean.setLowVBatteryCellCode(msgBody.readUnsignedByte());
        //电池单体电压最低值
        neExtremumBean.setMinimumBatteryVoltage(msgBody.readUnsignedShort());
        //最高温度子系统号
        neExtremumBean.setHighTemperatureSubNum(msgBody.readUnsignedByte());
        //最高温度探针序号
        neExtremumBean.setHighTemperatureProbeSerial(msgBody.readUnsignedByte());
        //最高温度值
        neExtremumBean.setMaxTemperatureValue(msgBody.readUnsignedByte());
        //最低温度子系统号
        neExtremumBean.setLowTemperatureSubNum(msgBody.readUnsignedByte());
        //最低温度探针序号
        neExtremumBean.setLowTemperatureProbeSerial(msgBody.readUnsignedByte());
        //最低温度值
        neExtremumBean.setMinTemperatureValue(msgBody.readUnsignedByte());
        return neExtremumBean;
    }

    //告警消息数据处理
    private NEAlarmBean do_alarm(ByteBuf msgBody) {
        NEAlarmBean neAlarmBean = new NEAlarmBean();
        neAlarmBean.setAlarmInformationType((short)0x07);
        //可充电储能装置故障代码列表
        List<Long> rechargeableStorageCodeList = Lists.newArrayList();
        //驱动电机故障代码列表
        List<Long> driverMotorFailureCodeList = Lists.newArrayList();
        //发动机故障代码列表
        List<Long> engineFailureCodeList= Lists.newArrayList();
        //其他故障代码列表
        List<Long> otherFailureCodeList= Lists.newArrayList();
        //最高报警等级
        short maxAlarmRate = msgBody.readUnsignedByte();
        if(maxAlarmRate >= 0 && maxAlarmRate <= 3){
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate==0?"无故障":(maxAlarmRate==1?"1级故障":(maxAlarmRate==2?"2级故障":"3级故障")));
        }else{
            neAlarmBean.setMaxAlarmRating(maxAlarmRate);
            neAlarmBean.setMaxAlarmRatingName(maxAlarmRate==0?"无故障":(maxAlarmRate==1?"1级故障":(maxAlarmRate==2?"2级故障":"3级故障")));
            new ParseException(maxAlarmRate,"最高故障等级");
        }
        //通用报警标志
        long alarmIdentification = msgBody.readUnsignedInt();
        neAlarmBean.setAlarmIdentification(alarmIdentification);
        neAlarmBean.setAlarmIdentificationList(NEStateCode2Name.parseGeneralAlarms((alarmIdentification)));
        //可充电储能装置故障总数N1
        short rechargeAbleN1 = msgBody.readUnsignedByte();
        neAlarmBean.setRechargeableStorageDeviceN1(rechargeAbleN1);
        if(rechargeAbleN1>0&&rechargeAbleN1<=252){
            while(rechargeAbleN1-->0){
                rechargeableStorageCodeList.add(msgBody.readUnsignedInt());
            }
            //可充电储能装置故障代码列表
            neAlarmBean.setRechargeableStorageCodeList(rechargeableStorageCodeList);
        }else{
            new ParseException(rechargeAbleN1,"可充电储能装置故障总数N1");
        }
        //驱动电机故障总数N2
        short driverMotorfailN2 = msgBody.readUnsignedByte();
        neAlarmBean.setDriverMotorFailureN2(driverMotorfailN2);
        if(driverMotorfailN2>0&&driverMotorfailN2<=252){
            while(driverMotorfailN2-->0){
                driverMotorFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //驱动电机故障代码列表
            neAlarmBean.setDriverMotorFailureCodeList(driverMotorFailureCodeList);
        }else{
            new ParseException(driverMotorfailN2,"驱动电机故障总数N2");
        }
        //发动机故障总数N3
        short engineFailN3 = msgBody.readUnsignedByte();
        neAlarmBean.setEngineFailureN3(engineFailN3);
        if(engineFailN3>0&&engineFailN3<=252){
            while(engineFailN3-->0){
                engineFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //发动机故障代码列表
            neAlarmBean.setEngineFailureCodeList(engineFailureCodeList);
        }else{
            new ParseException(engineFailN3,"发动机故障总数N3");
        }
        //其他故障总数N4
        short otherFailN4 = msgBody.readUnsignedByte();
        neAlarmBean.setOtherFailureN4(otherFailN4);
        if(otherFailN4>0&&otherFailN4<=252){
            while(otherFailN4-->0){
                otherFailureCodeList.add(msgBody.readUnsignedInt());
            }
            //其他故障代码列表
            neAlarmBean.setOtherFailureCodeList(otherFailureCodeList);
        }else{
            new ParseException(otherFailN4,"其他故障总数N4");
        }
        return neAlarmBean;
    }
}
