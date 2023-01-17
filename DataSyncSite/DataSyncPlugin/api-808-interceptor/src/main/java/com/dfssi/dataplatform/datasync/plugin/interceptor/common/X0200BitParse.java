package com.dfssi.dataplatform.datasync.plugin.interceptor.common;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 808 protocol(808) interceptor and parse to event to be comprehensible
 * @author jianKang
 * @date 2017/12/29
 */
public class X0200BitParse {
    static final Logger logger = LoggerFactory.getLogger(X0200BitParse.class);
    /**
     * alarm type by per bit
     * @param alarm
     * @return status list
     */
    public static List<String> parseAlarm(long alarm){
        List<String> alarmList = Lists.newArrayList();
        for (int i = 0; i < 32; i++) {
            if ((alarm & (1 << i)) != 0) {
                //Pair<Integer, String> at = checkAlarmType(i, gps.getAlarmExtra());
                Pair<Integer, String> at = checkAlarmType(i,"0");
                alarmList.add(at.getValue());
                logger.info("alarmBit " + i + " alarmExtra " + at.getValue());
                //doWarm(gpsPo, at,true);
            }
        }
        return alarmList;
    }

    /**
     * vehicle status type by per bit
     * @param vehicleStatus
     * @return status list
     */
    public static List<String> parseVehicleStatus(long vehicleStatus){
        List<String> vehicleStatusList = Lists.newArrayList();
        for (int i = 0; i < 32; i++) {
            if ((vehicleStatus & (1 << i)) != 0) {
                Pair<Integer, String> at = extraVehicleType(i);
                //logger.info("车辆状态key：{},value：{}",at.getKey(),at.getValue());
                vehicleStatusList.add(at.getValue());
            }
        }
        return vehicleStatusList;
    }

    /**
     * 将808的报警位映射成809报警类型
     * 报警类型
     */
    private static Pair<Integer, String> checkAlarmType(int alarmBit, String alarmExtra) {
        int alarmType = 34;
        String alarmDesc = "未知报警";
        switch (alarmBit) {
            case 0:
                alarmType = 0;
                alarmDesc = "紧急报警";
                break;
            case 1:
                alarmType = 1;
                alarmDesc = "超速报警";
                break;
            case 2:
                alarmType = 2;
                alarmDesc = "疲劳驾驶";
                break;
            case 3:
                alarmType = 3;
                alarmDesc = "危险预警";
                break;
            case 4:
                alarmType = 4;
                alarmDesc = "GNSS 模块发生故障";
                break;
            case 5:
                alarmType = 5;
                alarmDesc = "GNSS 天线未接或被剪断";
                break;
            case 6:
                alarmType = 6;
                alarmDesc = "GNSS 天线短路";
                break;
            case 7:
                alarmType = 7;
                alarmDesc = "终端主电源欠压";
                break;
            case 8:
                alarmType = 8;
                alarmDesc = "终端主电源掉电";
                break;
            case 9:
                alarmType = 9;
                alarmDesc = "终端 LCD 或显示器故障";
                break;
            case 10:
                alarmType = 10;
                alarmDesc = "TTS 模块故障 ";
                break;
            case 11:
                alarmType = 11;
                alarmDesc = "摄像头故障";
                break;
            case 12:
                alarmType = 12;
                alarmDesc = "道路运输证 IC 卡模块故障";
                break;
            case 13:
                alarmType = 13;
                alarmDesc = "超速预警";
                break;
            case 14:
                alarmType = 14;
                alarmDesc = "疲劳驾驶预警";
                break;
            /*case 15:
                alarmType = 15;
                alarmDesc = "保留";
                break;
            case 16:
                alarmType = 16;
                alarmDesc = "保留";
                break;
            case 17:
                alarmType = 17;
                alarmDesc = "保留";
                break;*/
            case 18:
                alarmType = 18;
                alarmDesc = "当天累计驾驶超时";
                break;
            case 19:
                alarmType = 19;
                alarmDesc = "超时停车";
                break;
            case 20:
                //此处做特别处理，在其他地方并没有实际应用，所以屏蔽掉
                alarmType = 20;
                alarmDesc = "进出区域";
                break;
            case 21:
                alarmType = 21;
                alarmDesc = "进出路线";
                break;
            case 22:
                alarmType = 22;
                alarmDesc = "路段行驶时间不足/过长";
                break;
            case 23:
                alarmType = 23;
                alarmDesc = "路线偏离报警";
                break;
            case 24:
                alarmType = 24;
                alarmDesc = "车辆 VSS 故障";
                break;
            case 25:
                alarmType = 25;
                alarmDesc = "车辆油量异常";
                break;
            case 26:
                alarmType = 26;
                alarmDesc = "车辆被盗";
                break;
            case 27:
                alarmType = 27;
                alarmDesc = "车辆非法点火";
                break;
            case 28:
                alarmType = 28;
                alarmDesc = "车辆非法位移";
                break;
            case 29:
                alarmType = 29;
                alarmDesc = "碰撞预警";
                break;
            case 30:
                alarmType = 30;
                alarmDesc = "侧翻预警";
                break;
            case 31:
                alarmType = 31;
                alarmDesc = "非法开门报警";
                break;
            default:
//                alarmType = 34;
//                alarmDesc = "未知报警";
                break;
        }
        return new ImmutablePair<>(alarmType, alarmDesc);
    }

    /**
     * 扩展车辆信号状态位定义解析
     */
    private static Pair<Integer, String> extraVehicleType(int alarmBit) {
        int statusType ;
        String statusDesc = "未知车辆信号";
        switch (alarmBit) {
            case 0:
                statusType = 0;
                statusDesc = "近光灯信号";
                break;
            case 1:
                statusType = 1;
                statusDesc = "远光灯信号";
                break;
            case 2:
                statusType = 2;
                statusDesc = "右转向灯信号";
                break;
            case 3:
                statusType = 3;
                statusDesc = "左转向灯信号";
                break;
            case 4:
                statusType = 4;
                statusDesc = "制动信号";
                break;
            case 5:
                statusType = 5;
                statusDesc = "倒档信号";
                break;
            case 6:
                statusType = 6;
                statusDesc = "雾灯信号";
                break;
            case 7:
                statusType = 7;
                statusDesc = "示廓灯";
                break;
            case 8:
                statusType = 8;
                statusDesc = "喇叭信号";
                break;
            case 9:
                statusType = 9;
                statusDesc = "空调状态";
                break;
            case 10:
                statusType = 10;
                statusDesc = "空挡信号";
                break;
            case 11:
                statusType = 11;
                statusDesc = "缓速器工作";
                break;
            case 12:
                statusType = 12;
                statusDesc = "ABS 工作";
                break;
            case 13:
                statusType = 13;
                statusDesc = "加热器工作";
                break;
            case 14:
                statusType = 14;
                statusDesc = "离合器状态";
                break;
            default:
                statusType = -1;
                statusDesc = "保留";
                break;
        }
        return new ImmutablePair<>(statusType, statusDesc);
    }

    /**
     * get 0200 状态各标志位信息  解析到14位
     *
     * @return
     */
    public static List<String> getStateDesp(long status) {
        List<String> stateDespList = Lists.newArrayList();
        if ((status & 1) != 0) {
            stateDespList.add(StatusConstants.STA_ACCON);
        } else {
            stateDespList.add(StatusConstants.STA_ACCOFF);
        }

        if ((status & 2) != 0) {
            stateDespList.add(StatusConstants.STA_LOC);
        } else {
            stateDespList.add(StatusConstants.STA_UNLOC);
        }

        if ((status & 4) != 0) {
            stateDespList.add(StatusConstants.STA_SL);
        } else {
            stateDespList.add(StatusConstants.STA_NL);
        }
        if ((status & 8) != 0) {
            stateDespList.add(StatusConstants.STA_W);
        } else {
            stateDespList.add(StatusConstants.STA_E);
        }
        if ((status & 16) != 0) {
            stateDespList.add(StatusConstants.STA_STOP);
        } else {
            stateDespList.add(StatusConstants.STA_RUN);
        }
        if ((status & 32) != 0) {
            stateDespList.add(StatusConstants.STA_ENCRYPT);
        } else {
            stateDespList.add(StatusConstants.STA_UNENCRYPT);
        }
        if (rightMoveBit(status, 8) == 0) {//00：空车；01：半载；10：保留；11：满载
            stateDespList.add(StatusConstants.STA_EMPTY);
        } else if (rightMoveBit(status, 8) == 1) {
            stateDespList.add(StatusConstants.STA_HALFLOAD);
        } else if (rightMoveBit(status, 8) == 2) {
            stateDespList.add(StatusConstants.STA_RESERVE);
        } else if (rightMoveBit(status, 8) == 3) {
            stateDespList.add(StatusConstants.STA_FULL);
        }
        if ((status & 1024) != 0) {
            stateDespList.add(StatusConstants.STA_FUELCUTOFF);
        } else {
            stateDespList.add(StatusConstants.STA_FUELOK);
        }

        if ((status & 2048) != 0) {
            stateDespList.add(StatusConstants.STA_CIRCUITOFF);
        } else {
            stateDespList.add(StatusConstants.STA_CIRCUITOK);
        }
        if ((status & 4096) != 0) {
            stateDespList.add(StatusConstants.STA_LOCK);
        } else {
            stateDespList.add(StatusConstants.STA_UNLOCK);
        }
        if ((status & 8192) != 0) {
            stateDespList.add(StatusConstants.STA_DOORONEOPEN);
        } else {
            stateDespList.add(StatusConstants.STA_DOORONECLOSE);
        }
        if ((status & 16384) != 0) {
            stateDespList.add(StatusConstants.STA_DOORTWOOPEN);
        } else {
            stateDespList.add(StatusConstants.STA_DOORTWOCLOSE);
        }
        if ((status & 32768) != 0) {
            stateDespList.add(StatusConstants.STA_DOORTHREEOPEN);
        } else {
            stateDespList.add(StatusConstants.STA_DOORTHREECLOSE);
        }
        if ((status & 65536) != 0) {
            stateDespList.add(StatusConstants.STA_DOORFOUROPEN);
        } else {
            stateDespList.add(StatusConstants.STA_DOORFOURCLOSE);
        }
        if ((status & 131072) != 0) {
            stateDespList.add(StatusConstants.STA_DOORFIVEOPEN);
        } else {
            stateDespList.add(StatusConstants.STA_DOORFIVECLOSE);
        }
        if ((status & 262144) != 0) {
            stateDespList.add(StatusConstants.STA_USEGPS);
        } else {
            stateDespList.add(StatusConstants.STA_NOUSEGPS);
        }
        if ((status & 524288) != 0) {
            stateDespList.add(StatusConstants.STA_USEDIPPER);
        } else {
            stateDespList.add(StatusConstants.STA_NOUSEDIPPER);
        }
        if ((status & 1048576) != 0) {
            stateDespList.add(StatusConstants.STA_USEGLONASS);
        } else {
            stateDespList.add(StatusConstants.STA_NOUSEGLONASS);
        }
        if ((status & 2097152) != 0) {
            stateDespList.add(StatusConstants.STA_USEGALILEO);
        } else {
            stateDespList.add(StatusConstants.STA_NOUSEGALILEO);
        }
        return stateDespList;
    }

    /**
     * 解析异常驾驶行为分析数据
     */
    public static List<Object> parseAbnormalDrivingData(ByteBuf messages){
        List<Object> abnormalDrivingBehaviors = Lists.newArrayList();
        //疲劳驾驶行为类型列表
        abnormalDrivingBehaviors.add(parseAbnormalDrivingType(messages.readUnsignedShort()));
        //疲劳驾驶行为程度
        abnormalDrivingBehaviors.add(messages.readUnsignedByte());
        return abnormalDrivingBehaviors;
    }

    /**
     * 疲劳驾驶行为类型列表
     */
    private static List<String> parseAbnormalDrivingType(int messages){
        int abnormalBehaviorStatus = messages;
        List<String> vehicleStatusList = Lists.newArrayList();
        for (int i = 0; i < 16; i++) {
            if ((abnormalBehaviorStatus & (1 << i)) != 0) {
                Pair<Integer, String> at = abnormalDrivingType(i);
                vehicleStatusList.add(at.getValue());
            }
        }
        return vehicleStatusList;
    }

    /**
     * 疲劳驾驶行为类型
     */
    private static Pair<Integer, String> abnormalDrivingType(int drivingBehaviorTypeBit) {
        int statusType ;
        String statusDesc = "未知异常驾驶行为类型";
        switch (drivingBehaviorTypeBit) {
            case 0:
                statusType = 0;
                statusDesc = "疲劳";
                break;
            case 1:
                statusType = 1;
                statusDesc = "打电话";
                break;
            case 2:
                statusType = 2;
                statusDesc = "抽烟";
                break;
            case 3:
                statusType = 3;
                statusDesc = "未系安全带";
                break;
            default:
                statusType = -1;
                statusDesc = "保留位";
                break;
        }
        return new ImmutablePair<>(statusType, statusDesc);
    }

    private static long rightMoveBit(long source, int n) {
        return (source & (3 << n)) >>> n;
    }
}
