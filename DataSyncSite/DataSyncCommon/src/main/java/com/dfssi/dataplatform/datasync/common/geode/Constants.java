package com.dfssi.dataplatform.datasync.common.geode;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hannibal on 2018-03-01.
 */
public class Constants {

    public static final String string_encoding = "GBK";
    public static final String TASK_ID_KEY = "taskId";//任务id
    public static final String MSG_ID_KEY = "msgId";//消息头ID
    public static final String KEY_HEADER = "key";//分区键

    public static final String FIELD_KEY_PARTITION = "vin";//分区字段

    public static final String TEST_KEY = "test_key";

    public static String TEST_VALUE = "test_key";

    //插入最新位置信息到redis的前缀
    public static final String suffixVid = "vid";

    public static final String VNND_DATASOURCE_ID = "vnnd";

    //车辆缓存线程睡眠时间
    public static final Integer VEHCILE_CHAHE_THREAD_SLEEPTIME = 10;

    //更改车辆状态线程睡眠时间
    public static final Integer VEHCILE_STATUS_THREAD_SLEEPTIME = 11;

    //车辆缓存在redis的保存时间
    public static final Integer VEHCILE_CACHE_TIME = 10;

    ///全局缓存对象主键
    public static final String GK_VEHICLE_STATE = "gk:vs:"; //车辆状态
    public static final String GK_USER_TO_VEHICLES= "u2vs:"; //用户与车辆对应关系
    public static final String GK_LATEST_POS = "gk:lp:"; //车辆最新的位置
    public static final String VEHICLE_ISUSE = "visuse:";//车辆启用状态
    public static final String VEHICLE_LATEST_INFO = "vehicle_latest_info:"; //车辆最新信息
    public static final String VEHILCE_INFO_UPDATE = "vehicle_info_update:"; //车辆信息变更

    public static final int ENCODE_SALT = 10;



    public static String DMS_XML_PATH = "config/dms.xml";

    public static String DB_VNND_DRIVER = "com.mysql.jdbc.Driver";

    public static String DB_VNND_URL = "jdbc:mysql://172.16.1.241:3306/SSI_SERVICE_PLATFORM?useUnicode=true&characterEncoding=utf8&useSSL=false&Allow ZeroDatetime=True";

    public static String DB_VNND_USRENAME = "app_user";

    public static String DB_VNND_PASSWORD = "112233";

    public static Integer REDIS_VS_STATUS_DB = 2;

    //新能源数据topic
    public static String NE_VECHILE_DATA_TOPIC = "NE_VECHILE_DATA_TOPIC";

    //新能源车辆登入登出topic
    public static String NE_VECHILE_LOGIN_TOPIC = "NE_VECHILE_LOGIN_TOPIC";

    public static String NE_VECHILE_LOGOUT_TOPIC = "NE_VECHILE_LOGOUT_TOPIC";

    //平台登入登出topic
    public static String NE_PLATFORM_LOGIN_TOPIC = "NE_PLATFORM_LOGIN_TOPIC";

    public static String NE_PLATFORM_LOGOUT_TOPIC = "NE_PLATFORM_LOGOUT_TOPIC";

    public static Map<String, Integer> PARAM_ITEM_8103_MAP = new HashMap<>();

    //上次照片文件存储的临时目录
    public static String tmpDir = "/tmp/vnnd/";


    public static final Charset string_charset = Charset.forName(string_encoding);

    public static final String TOPIC_HEADER = "topic";

    //xdiamond配置车辆登入登出的key值
    public static final String PROPERTY_TOPIC_VECHILE_KEY = "key_vhchile_login_topic";

    //xdiamond配置平台登入登出的key值
    public static final String PROPERTY_TOPIC_PLATFORM_KEY = "key_platform_login_topic";

    //xdiamond配置平台登入登出的key值
    public static final String PROPERTY_TOPIC_DATA_KEY = "key_vehicle_data_topic";

    public static final String GEODE_CONNECT_KEY = "GEODE_CONNECT";

    //新能源数据个数
    public static final String NUM_NE_DATA_KEY = "numofnedata";

    public static Integer NUM_NE_DATA_VALUE = 63;

    //geode服务地址
    public static String GEODE_CONNECT = null;

    public static final String REGION_VEHICLEINFO_KEY = "REGION_VEHICLEINFO";
    public static final String REGION_PLATFORMINFO_KEY = "REGION_PLATFORMINFO";

    public static String REGION_VEHICLEINFO = "vehicleBaseInfo";

    public static String REGION_PLATFORMINFO = "platformInfo";

    public static String REGION_CVVEHICLEBASEINFO =  "cvVehicleBaseInfo";

}
