package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.D004Constants;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author JianKang
 * @date 2018/5/31
 * @description
 */
public class D004DataParse {
/*    public static void main(String[] args) {
        int code = 85;
        System.out.println(getStatus(code));
    }*/

    public static List<String> getStatus(int code){
        List<String> status = Lists.newArrayList();
        status.add(getNameByParkBrakeCode(code&3));
        status.add(getNameByBrakePadel((code&12)>>>2));
        status.add(getNameByClutchPedal((code&48)>>>4));
        status.add(getNameByCruiseController((code&192)>>>6));
        return status;
    }

    private static String getNameByParkBrakeCode(int code){
        String name = null;
        if(code == 0){
            name = D004Constants.parkbrakeOff;
        }else if(code == 1){
            name = D004Constants.parkbrakeOn;
        }
        return name;
    }

    private static String getNameByBrakePadel(int code){
        String name = null;
        if(code == 0){
            name = D004Constants.brakePadelOff;
        }else if(code ==1){
            name = D004Constants.brakePadelOn;
        }else if(code ==2){
            name = D004Constants.brakePadelErr;
        }else if(code ==3){
            name = D004Constants.brakePadelDisable;
        }
        return name;
    }
    private static String getNameByClutchPedal(int code){
        String name = null;
        if(code == 0){
            name = D004Constants.clutchPedalOff;

        }else if(code ==1){
            name = D004Constants.clutchPedalOn;
        }else if(code ==2){
            name = D004Constants.clutchPedalErr;
        }else if(code ==3){
            name = D004Constants.clutchPedalDisable;
        }
        return name;
    }

    private static String getNameByCruiseController(int code){
        String name = null;
        if(code == 0){
            name = D004Constants.cruiseControllerOff;
        }else if(code == 1){
            name = D004Constants.cruiseControllerOn;
        }
        return name;
    }
}
