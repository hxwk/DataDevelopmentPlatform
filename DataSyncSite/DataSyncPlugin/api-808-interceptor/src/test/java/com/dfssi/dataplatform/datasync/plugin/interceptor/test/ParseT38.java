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
     * ???808?????????????????????809????????????
     * ????????????
     */
    private static Pair<Integer, String> checkAlarmType(int alarmBit, String alarmExtra) {
        int alarmType = 34;
        String alarmDesc = "????????????";
        switch (alarmBit) {
            case 0:
                alarmType = 0;
                alarmDesc = "????????????";
                break;
            case 1:
                alarmType = 1;
                alarmDesc = "????????????";
                break;
            case 2:
                alarmType = 2;
                alarmDesc = "????????????";
                break;
            case 3:
                alarmType = 3;
                alarmDesc = "????????????";
                break;
            case 4:
                alarmType = 4;
                alarmDesc = "GNSS ??????????????????";
                break;
            case 5:
                alarmType = 5;
                alarmDesc = "GNSS ????????????????????????";
                break;
            case 6:
                alarmType = 6;
                alarmDesc = "GNSS ????????????";
                break;
            case 7:
                alarmType = 7;
                alarmDesc = "?????????????????????";
                break;
            case 8:
                alarmType = 8;
                alarmDesc = "?????????????????????";
                break;
            case 9:
                alarmType = 9;
                alarmDesc = "?????? LCD ??????????????????";
                break;
            case 10:
                alarmType = 10;
                alarmDesc = "TTS ???????????? ";
                break;
            case 11:
                alarmType = 11;
                alarmDesc = "???????????????";
                break;
            case 12:
                alarmType = 12;
                alarmDesc = "??????????????? IC ???????????????";
                break;
            case 13:
                alarmType = 13;
                alarmDesc = "????????????";
                break;
            case 14:
                alarmType = 14;
                alarmDesc = "??????????????????";
                break;
            /*case 15:
                alarmType = 15;
                alarmDesc = "??????";
                break;
            case 16:
                alarmType = 16;
                alarmDesc = "??????";
                break;
            case 17:
                alarmType = 17;
                alarmDesc = "??????";
                break;*/
            case 18:
                alarmType = 18;
                alarmDesc = "????????????????????????";
                break;
            case 19:
                alarmType = 19;
                alarmDesc = "????????????";
                break;
            case 20:
                //??????????????????????????????????????????????????????????????????????????????
                alarmType = 20;
                alarmDesc = "????????????";
                break;
            case 21:
                alarmType = 21;
                alarmDesc = "????????????";
                break;
            case 22:
                alarmType = 22;
                alarmDesc = "????????????????????????/??????";
                break;
            case 23:
                alarmType = 23;
                alarmDesc = "??????????????????";
                break;
            case 24:
                alarmType = 24;
                alarmDesc = "?????? VSS ??????";
                break;
            case 25:
                alarmType = 25;
                alarmDesc = "??????????????????";
                break;
            case 26:
                alarmType = 26;
                alarmDesc = "????????????";
                break;
            case 27:
                alarmType = 27;
                alarmDesc = "??????????????????";
                break;
            case 28:
                alarmType = 28;
                alarmDesc = "??????????????????";
                break;
            case 29:
                alarmType = 29;
                alarmDesc = "????????????";
                break;
            case 30:
                alarmType = 30;
                alarmDesc = "????????????";
                break;
            case 31:
                alarmType = 31;
                alarmDesc = "??????????????????";
                break;
            default:
//                alarmType = 34;
//                alarmDesc = "????????????";
                break;
        }
        return new ImmutablePair<>(alarmType, alarmDesc);
    }
}
