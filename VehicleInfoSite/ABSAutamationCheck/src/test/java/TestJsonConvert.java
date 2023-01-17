import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author Weijj
 * @version 2018/9/14 16:26
 */
public class TestJsonConvert {
    public static void main(String[] args) {
//        String jsonStr = "{\"abnormalDrivingBehaviorAlarmDegree\":0,\"abnormalDrivingBehaviorAlarmType\":[],\"alarm\":-1,\"alarmInforCode\":0,\"alarmLists\":[],\"alarms\":[\"紧急报警\",\"超速报警\",\"疲劳驾驶\",\"危险预警\",\"GNSS 模块发生故障\",\"GNSS 天线未接或被剪断\",\"GNSS 天线短路\",\"终端主电源欠压\",\"终端主电源掉电\",\"终端 LCD 或显示器故障\",\"TTS 模块故障 \",\"摄像头故障\",\"道路运输证 IC 卡模块故障\",\"超速预警\",\"疲劳驾驶预警\",\"未知报警\",\"未知报警\",\"未知报警\",\"当天累计驾驶超时\",\"超时停车\",\"进出区域\",\"进出路线\",\"路段行驶时间不足/过长\",\"路线偏离报警\",\"车辆 VSS 故障\",\"车辆油量异常\",\"车辆被盗\",\"车辆非法点火\",\"车辆非法位移\",\"碰撞预警\",\"侧翻预警\",\"非法开门报警\"],\"alt\":23,\"batteryVoltage\":0,\"canAndHydraulicTankStatus\":0,\"cumulativeOilConsumption\":0.0,\"dir\":34,\"engineSpeed\":0.0,\"extraInfoItems\":[{\"data\":\"AAAAAA==\",\"id\":20},{\"data\":\"AAAAAA==\",\"id\":43},{\"data\":\"AA==\",\"id\":48},{\"data\":\"AA==\",\"id\":49},{\"data\":\"AAAAAAAAAAAAAAAAAAAAAA==\",\"id\":239}],\"fromQly\":0,\"fuel\":0.0,\"gpsTime\":1536910381000,\"hydraulicTank\":0,\"ioState\":0,\"lat\":30506903,\"lon\":114196893,\"mile\":0,\"msgId\":\"0200\",\"receiveMsgTime\":1536910491613,\"signalState\":0,\"signalStates\":[],\"sim\":\"18888888888\",\"speed\":400,\"speed1\":0,\"state\":11,\"throttleOpen\":0,\"torquePercentage\":0,\"totalFuelConsumption\":0,\"vehicleStatus\":[\"ACC 开\",\"定位\",\"北纬\",\"西经\",\"运营状态\",\"经纬度未经保密插件加密\",\"空车\",\"车辆油路正常\",\"车辆电路正常\",\"车门解锁\",\"门1关\",\"门2关\",\"门3关\",\"门4关\",\"门5关\",\"未使用GPS卫星定位\",\"未使用北斗卫星定位\",\"未使用GLONASS卫星定位\",\"未使用Galileo卫星定位\"],\"vehicleWeight\":526344.0,\"vid\":\"695e023e519c4d6ba77858e105c2f888\"}";
//        Gson gson = new Gson();
//        Map<String, Object> map = gson.fromJson(jsonStr, Map.class);
//        System.out.println(map.get("vid"));
        Map<String, Object> map = fomartCollTime("180913110811020");
        System.out.printf("collTime:%d,collectionTime:%s", map.get("collTime"), map.get("collectionTime"));
    }

    private static Map<String, Object> fomartCollTime(String collTime) {
        String yearMonthDay = "20" + collTime.substring(0, 2) + "-" + collTime.substring(2, 4) + "-" + collTime.substring(4, 6) + " " + collTime.substring(6, 8) + ":" + collTime.substring(8, 10) + ":" + collTime.substring(10, 12);
        TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(yearMonthDay);
        LocalDateTime date = LocalDateTime.from(temporalAccessor);
        long millSeconds = date.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        millSeconds += Integer.parseInt(collTime.substring(12, 15));
        HashMap<String, Object> map = new HashMap<>();
        map.put("collTime", millSeconds);
        map.put("collectionTime", yearMonthDay);
        return map;
    }
}
