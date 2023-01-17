package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/26 9:36
 */
@Configuration
@Getter
public class RoadVehicleConfig {

    @Value("${vehicle.online.minutes.threshold}")
    private int onlineThreshold;

    private Map<String, String> canFieldNameMap = canFieldNameMap();
    private Map<String, String> canFieldIdNameMap = canFieldIdNameMap();

    private static Pattern pattern = Pattern.compile("[^0-9]");

    public Map<String, String>  canFieldNameMap(){

        Map<String, String> fieldNameMap = Maps.newHashMap();
        fieldNameMap.put("1082◎发动机负荷率_W", "engineLoadRate");
        fieldNameMap.put("664◎油门踏板开度_W", "throttleOpening");
        fieldNameMap.put("1034◎VECU伪油门_W", "pseudoThrottle");
        fieldNameMap.put("453◎发动机转速_W", "engineSpeed");
        fieldNameMap.put("1039◎发动机扭矩_W", "engineTorque");
        fieldNameMap.put("1038◎驾驶员请求扭矩_W", "requestTorque");
        fieldNameMap.put("666◎发动机扭矩模式_W", "engineTorqueModel");
        fieldNameMap.put("1000005◎变速箱输出轴转速_W", "gearboxOutputShaftSpeed");
        fieldNameMap.put("1000008◎RlativeSpeedFrontAxleRightWheel_W", "rlativeSpeedFrontAxleRightWheel");
        fieldNameMap.put("1000006◎RlativeSpeedRearAxle1RightWheel_W", "rlativeSpeedRearAxle1RightWheel");
        fieldNameMap.put("1000009◎RelativeSpeedFrontAxleLeftWheel_W", "relativeSpeedFrontAxleLeftWheel");
        fieldNameMap.put("1000007◎RelativeSpeedRearAxle1LeftWheel_W", "relativeSpeedRearAxle1LefttWheel");
        fieldNameMap.put("1097◎前轴速度_W", "frontAxleSpeed");
        fieldNameMap.put("1059◎巡航控制状态_W", "cruiseControlStatus");
        fieldNameMap.put("1000010◎巡航设置速度_W", "cruisingSpeed");
        fieldNameMap.put("1000011◎巡航R开关_W", "cruiseRswitch");
        fieldNameMap.put("1000012◎巡航按键开关_W", "cruiseSwitch");
        fieldNameMap.put("1000013◎巡航S-开关_W", "cruiseSswitch");
        fieldNameMap.put("711◎离合器开关_W", "clutchSwitch");
        fieldNameMap.put("1053◎脚刹_W", "footBrake");
        fieldNameMap.put("1000014◎巡航进入状态_W", "cruiseEntryStatus");
        fieldNameMap.put("1052◎手刹_W", "handbrake");
        fieldNameMap.put("1046◎空调AC开关_W", "acSwitch");
        fieldNameMap.put("1158◎空档开关_W", "airConditioningSwitch");
        fieldNameMap.put("1047◎名义摩擦扭矩_W", "nominalFrictionTorque");
        fieldNameMap.put("669◎发动机进气温度_W", "engineIntakeAirTemperature");
        fieldNameMap.put("1000023◎发动机进气压力_W", "engineIntakePressure");
        fieldNameMap.put("767◎发动机水温_W", "engineWaterTemperature");
        fieldNameMap.put("657◎发动机机油压力_W", "engineOilPressure");
        fieldNameMap.put("1108◎风扇转速_W", "fanSpeed");
        fieldNameMap.put("1107◎风扇状态_W", "fanStatus");
        fieldNameMap.put("443◎大气压力_W", "atmosphericPressure");
        fieldNameMap.put("670◎环境温度_W", "ambientTemperature");
        fieldNameMap.put("1114◎尿素液位_W", "ireaLevel");
        fieldNameMap.put("1084◎防抱死激活_W", "AbsActivation");
        fieldNameMap.put("1000024◎发动机制动_W", "engineBrake");
        fieldNameMap.put("1118◎SCR出口温度_W", "SCROutletTemperature");
        fieldNameMap.put("1117◎SCR进口温度_W", "SCRInletTemperature");
        //fieldNameMap.put("1000025◎当前档位1_W", "gear");
        fieldNameMap.put("1160◎前桥气压_W", "frontAxlePressure");
        fieldNameMap.put("1161◎后桥气压", "rearAxlePressure");
        fieldNameMap.put("1128◎小计里程_W", "subtotalMileage");
        fieldNameMap.put("1159◎总计里程_W", "totalMileage");
        fieldNameMap.put("450◎车速_W", "speed1");
        fieldNameMap.put("614◎瞬时油耗_W", "fuel");

        return fieldNameMap;
    }

    public Map<String, String>  canFieldIdNameMap(){

        Map<String, String> fieldNameMap = Maps.newHashMap();
        fieldNameMap.put("1082", "engineLoadRate");
        fieldNameMap.put("664", "throttleOpening");
        fieldNameMap.put("1034", "pseudoThrottle");
        fieldNameMap.put("453", "engineSpeed");
        fieldNameMap.put("1039", "engineTorque");
        fieldNameMap.put("1038", "requestTorque");
        fieldNameMap.put("666", "engineTorqueModel");
        fieldNameMap.put("1000005", "gearboxOutputShaftSpeed");
        fieldNameMap.put("1000008", "rlativeSpeedFrontAxleRightWheel");
        fieldNameMap.put("1000006", "rlativeSpeedRearAxle1RightWheel");
        fieldNameMap.put("1000009", "relativeSpeedFrontAxleLeftWheel");
        fieldNameMap.put("1000007", "relativeSpeedRearAxle1LefttWheel");
        fieldNameMap.put("1097", "frontAxleSpeed");
        fieldNameMap.put("1059", "cruiseControlStatus");
        fieldNameMap.put("1000010", "cruisingSpeed");
        fieldNameMap.put("1000011", "cruiseRswitch");
        fieldNameMap.put("1000012", "cruiseSwitch");
        fieldNameMap.put("1000013", "cruiseSswitch");
        fieldNameMap.put("711", "clutchSwitch");
        fieldNameMap.put("1053", "footBrake");
        fieldNameMap.put("1000014", "cruiseEntryStatus");
        fieldNameMap.put("1052", "handbrake");
        fieldNameMap.put("1046", "acSwitch");
        fieldNameMap.put("1158", "airConditioningSwitch");
        fieldNameMap.put("1047", "nominalFrictionTorque");
        fieldNameMap.put("669", "engineIntakeAirTemperature");
        fieldNameMap.put("1000023", "engineIntakePressure");
        fieldNameMap.put("767", "engineWaterTemperature");
        fieldNameMap.put("657", "engineOilPressure");
        fieldNameMap.put("1108", "fanSpeed");
        fieldNameMap.put("1107", "fanStatus");
        fieldNameMap.put("443", "atmosphericPressure");
        fieldNameMap.put("670", "ambientTemperature");
        fieldNameMap.put("1114", "ireaLevel");
        fieldNameMap.put("1084", "AbsActivation");
        fieldNameMap.put("1000024", "engineBrake");
        fieldNameMap.put("1118", "SCROutletTemperature");
        fieldNameMap.put("1117", "SCRInletTemperature");
        //fieldNameMap.put("1000025", "gear");
        fieldNameMap.put("1160", "frontAxlePressure");
        fieldNameMap.put("1161", "rearAxlePressure");
        fieldNameMap.put("1128", "subtotalMileage");
        fieldNameMap.put("1159", "totalMileage");
        fieldNameMap.put("450", "speed1");
        fieldNameMap.put("614", "fuel");

        return fieldNameMap;
    }


    public String getCanFieldName(String canField){
        String canFieldId = null;
        try {
            Matcher matcher = pattern.matcher(canField);
            canFieldId = matcher.replaceAll("");
        } catch (Exception e) { }

        return canFieldIdNameMap.get(canFieldId);
    }


}
