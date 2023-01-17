package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/21
 * @description 疲劳驾驶位解析
 */
public class BitParser {
    /*public static void main(String[] args) {
        String s = "00fffa";
        ByteBufCustomTool tool = new ByteBufCustomTool();
        //16进制字符串转成ByteBuf
        ByteBuf bb = tool.hexStringToByteBuf(s);
        List<Object> abnormalData;
        List<String> abnormalDrivingType = null;
        Short abnormalDrivingDegree = null;
        abnormalData = parseAbnormalDrivingData(bb);
        if(abnormalData.get(0) instanceof List<?>){
            abnormalDrivingType = (List<String>) abnormalData.get(0);
        }
        if(abnormalData.get(1) instanceof Short){
            abnormalDrivingDegree = (Short)abnormalData.get(1);
        }
        System.out.println(abnormalDrivingType.toString()+" 疲劳程度: "+abnormalDrivingDegree);
    }*/

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
}
