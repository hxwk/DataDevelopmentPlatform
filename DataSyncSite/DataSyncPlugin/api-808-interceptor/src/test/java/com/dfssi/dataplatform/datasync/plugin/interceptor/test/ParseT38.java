package com.dfssi.dataplatform.datasync.plugin.interceptor.test;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jian on 2017/12/22.
 */
public class ParseT38 {
    static final Logger logger = LoggerFactory.getLogger(ParseT38.class);
    public static void main(String[] args) {
        String word="(1,-40)";
        //System.out.println(word.split(",")[0].replace("(",""));
        //System.out.println(word.split(",")[1].replace(")",""));

        /*long a = 0x8CFE6CEE;
        long b = 0x4CFE6CEE;
        //System.out.println(a&0xCFFFFFFF);
        //System.out.println(b&0xCFFFFFFF);
        long t = 0x1FFFFFFF;
        long a1 = a & t;
        long b1 = b & t;
        System.out.println("0x8CFE6CEE & 0x1FFFFFFF "+a1);
        System.out.println("0x4CFE6CEE & 0x1FFFFFFF "+b1);

        String n = "null";
        System.out.println(n);
        System.out.println(n=="null");
        System.out.println(n.equals("null"));
        System.out.println(n.equals(null));
        System.out.println(n.length());*/

        /*short a = (short)16; //1000
        System.out.println(Integer.toBinaryString(a&0x10));//00010000
        System.out.println(Integer.toBinaryString(a>>1));//00010000*/

        /*List<String> parses = parseAlarm(786434); //10
        for(String parse:parses){
            logger.info("each bit "+parse);
        }*/
        testBit(15);
    }

    public static void testBit(int alarm){
        for (int i = 0; i < 32; i++) {
            logger.info("(alarm & (1 << i)) " +(alarm & (i>>1)) );
            logger.info("alarm "+alarm);
            logger.info("(1 << i) "+(i>>1));
            if ((alarm & (1<<i)) != 0) {  //0,0,1,1,2,2,3,3,4,4,5,5
                logger.info("alarmBit " + i + " alarmExtra "+(1<<i));
            }
        }
    }


    public void testBit(){
        for (int i = 0; i < 32; i++) {
                logger.info("alarmBit " + i + " alarmExtra "+(1 << i));

        }
    }


    public static List<String> parseAlarm(int alarm){
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
}
