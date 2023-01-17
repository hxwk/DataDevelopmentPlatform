package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/13
 * @description state code to state name
 */
public class NEStateCode2Name {
    static final Logger logger = LoggerFactory.getLogger(NEStateCode2Name.class);
    /**
     * 车辆状态码转换成车辆状态名
     * @param code
     * @return 车辆状态名
     */
    public static String vehicleState(short code){
        String stateCode = hex2String(code);
        String stateName ;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.VEHICLESTATEON;
                break;
            case "0x02":
                stateName = NEStateConstant.VEHICLESTATEOFF;
                break;
            case "0x03":
                stateName = NEStateConstant.OTHERSTATE;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * 充电状态码转充电状态名称
     * @param code
     * @return 充电状态名
     */
    public static String chargeState(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.VEHICLECHARGESTATESTOPCAR;
                break;
            case "0x02":
                stateName = NEStateConstant.VEHICLECHARGESTATEDRIVINGCAR;
                break;
            case "0x03":
                stateName = NEStateConstant.VEHICLENOTCHARGING;
                break;
            case "0x04":
                stateName = NEStateConstant.VEHICLECHARGECOMPLETE;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * 运行模式状态码转状态名
     * @param code
     * @return 运行模式状态名
     */
    public static String runMode(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.RUNMODEPUREELECTRIC;
                break;
            case "0x02":
                stateName = NEStateConstant.RUNMODEHYBRID;
                break;
            case "0x03":
                stateName = NEStateConstant.RUNMODEFUELOIL;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * DC-DC状态码转状态名
     * @param code
     * @return DC状态名字
     */
    public static String dcState(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.DCSTATEWORK;
                break;
            case "0x02":
                stateName = NEStateConstant.DCSTATEDISCONNECT;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * 驱动电机码转名称
     * @param code
     * @return 驱动电机名称
     */
    public static String driverMotor(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.DRIVERMOTORPOWERCONSUMPTION;
                break;
            case "0x02":
                stateName = NEStateConstant.DRIVERMOTORPOWERPRODUCT;
                break;
            case "0x03":
                stateName = NEStateConstant.DRIVERMOTORSHUTDOWN;
                break;
            case "0x04":
                stateName = NEStateConstant.DRIVERMOTORPREPARE;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * 燃料电池高压DC状态码转名称
     * @param code
     * @return 燃料电池高压DC状态名称
     */
    public static String fuelCellDCState(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.HIGHVOLTAGEDCSTATEWORK;
                break;
            case "0x02":
                stateName = NEStateConstant.HIGHVOLTAGEDCSTATEDISCONNECT;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * 发动机状态码砖状态名称
     * @param code
     * @return 发动机状态名称
     */
    public static String motorState(short code){
        String stateCode = hex2String(code);
        String stateName;
        switch (stateCode){
            case "0x01":
                stateName = NEStateConstant.ENGINSTATEON;
                break;
            case "0x02":
                stateName = NEStateConstant.ENGINSTATEOFF;
                break;
            case "0xFE":
                stateName = NEStateConstant.ABNORMAL;
                break;
            case "0xFF":
                stateName = NEStateConstant.INVALID;
                break;
            default:
                stateName = null;
        }
        return stateName;
    }

    /**
     * hex convert hex String
     * @param code
     * @return hex String
     */
    public static String hex2String(short code) {
        return String.format("0x%02X", code);
    }

    public static List<String> gearStates(short gear){
        List<String> gearList = Lists.newArrayList();
        try {
            int gears = gear & 0xF;
            gearList.add(currentGear(gears));
            int brakingForceCode = (gear & 0x10) >>>4 ;
            String brakingForce = brakingForceCode==1? NEStateConstant.BRAKINGFORCEON: NEStateConstant.BRAKINGFORCEOFF;
            gearList.add(brakingForce);
            int drivingForceCode = (gear & 0x20) >>>5;
            String drivingForce = drivingForceCode==1? NEStateConstant.DRIVINGFORCEON: NEStateConstant.DRIVINGFORCEOFF;
            gearList.add(drivingForce);
            //有两位是预留位，后期需要时候再搞

        }catch (Exception ex){
            logger.error("gearStates error:{}",ex);
        }
        return gearList;
    }

    private static String currentGear(int gears) {
        String currentPosition;
        if (gears < 0 || gears > 15) {
            logger.error("currentGear档位不对");
        }
        if (gears == 0) {
            currentPosition = NEStateConstant.NEUTRALGEAR;
        } else if (gears == 13) {
            currentPosition = NEStateConstant.REVERSEGEAR;
        } else if (gears == 14) {
            currentPosition = NEStateConstant.AUTOMATICCATCH_D;
        } else if (gears == 15) {
            currentPosition = NEStateConstant.PARKING;
        } else {
            currentPosition = gears + NEStateConstant.GEAR;
        }
        return currentPosition;
    }

    /**
     * 定位状态Code转定位状态信息
     * @param localtionCode
     * @return
     */
    public static List<String> locationStates(short localtionCode){
        List<String> locationStateList = Lists.newArrayList();
        int isValid = localtionCode & 0x01;
        locationStateList.add(isValid==1?NEStateConstant.LOCATIONINVALID:NEStateConstant.LOCATIONVALID);
        String latitude = (localtionCode & 0x02)>>>1==1?NEStateConstant.SOUTHERNLATITUDE:NEStateConstant.NORTHERNLATITUDE;
        locationStateList.add(latitude);
        String longitude = (localtionCode & 0x04)>>>2==1?NEStateConstant.WESTLONGITUDE:NEStateConstant.EASTLONGITUDE;
        locationStateList.add(longitude);
        return locationStateList;
    }

    /**
     * 新能源通用报警解析
     */
    public static List<String> parseGeneralAlarms(long alarmStatus){
        List<String> alarmStatusList = Lists.newArrayList();
        for (int i = 0; i < 32; i++) {
            if ((alarmStatus & (1 << i)) != 0) {
                Pair<Integer, String> at = doGeneralAlarm(i);
                alarmStatusList.add(at.getValue());
            }
        }
        return alarmStatusList;
    }

    /**
     * 新能源通用报警标志位解析
     */
    private static Pair<Integer, String> doGeneralAlarm(int alarmBit) {
        int statusType;
        String statusDesc = "未知报警";
        switch (alarmBit) {
            case 0:
                statusType = 0;
                statusDesc = "温度差异报警";
                break;
            case 1:
                statusType = 1;
                statusDesc = "电池高温报警";
                break;
            case 2:
                statusType = 2;
                statusDesc = "车载储能装置类型过压报警";
                break;
            case 3:
                statusType = 3;
                statusDesc = "车载储能装置类型欠压报警";
                break;
            case 4:
                statusType = 4;
                statusDesc = "SOC低报警";
                break;
            case 5:
                statusType = 5;
                statusDesc = "单体电池过压报警";
                break;
            case 6:
                statusType = 6;
                statusDesc = "单体电池欠压报警";
                break;
            case 7:
                statusType = 7;
                statusDesc = "SOC过高报警";
                break;
            case 8:
                statusType = 8;
                statusDesc = "SOC跳变报警";
                break;
            case 9:
                statusType = 9;
                statusDesc = "可充电储能系统不匹配报警";
                break;
            case 10:
                statusType = 10;
                statusDesc = "电池单体一致性差报警";
                break;
            case 11:
                statusType = 11;
                statusDesc = "绝缘报警";
                break;
            case 12:
                statusType = 12;
                statusDesc = "DC-DC温度报警";
                break;
            case 13:
                statusType = 13;
                statusDesc = "制动系统报警";
                break;
            case 14:
                statusType = 14;
                statusDesc = "DC-DC状态报警";
                break;
            case 15:
                statusType = 15;
                statusDesc = "驱动电机控制器温度报警";
                break;
            case 16:
                statusType = 16;
                statusDesc = "高压互锁状态报警";
                break;
            case 17:
                statusType = 17;
                statusDesc = "驱动电机温度报警";
                break;
            case 18:
                statusType = 18;
                statusDesc = "车载储能装置类型过充";
                break;
            default:
                statusType = -1;
                statusDesc = "保留";
                break;
        }
        return new ImmutablePair<>(statusType, statusDesc);
    }

    /*public static void main(String[] args) {
        *//*List<String> gears = gearStates((short)0x1F);
        System.out.println(gears);*//*
        String s = "00aaffff";
        //16进制字符串转成ByteBuf
        ByteBuf bb = ByteBufCustomTool.hexStringToByteBuf(s);
        //List<String> locals = locationStates(bb.readUnsignedByte());

        Long tmp = bb.readUnsignedInt();
        List<String> locals = parseGeneralAlarms(tmp);
        System.out.println(locals);
    }*/
}
