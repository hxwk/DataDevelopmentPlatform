package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

/**
 * @author JianKang
 * @date 2018/4/3
 * @description
 */
public class Constants {
    public static final String TEST_KEY = "test_key";

    public static String TEST_VALUE = "test_key";

    public static final String GK_VEHICLE_STATE = "ne:vehicle:status:";

    public static Integer VEHCILE_CACHE_TIME = 2;

    public static String NEREALTIMEDATAREPORT_TOPIC = "NEREALTIMEDATAREPORT_TOPIC";

    public static String NESUPPLEMENTDATAREPORT_TOPIC = "NESUPPLEMENTDATAREPORT_TOPIC";

    public static final String GEODE_CONNECT_KEY = "GEODE_CONNECT";

    //geode服务地址
    public static String GEODE_CONNECT = null;

    public static final String REGION_VEHICLEINFO_KEY = "REGION_VEHICLEINFO";
    public static final String REGION_PLATFORMINFO_KEY = "REGION_PLATFORMINFO";

    public static String REGION_VEHICLEINFO = "vehicleBaseInfo";

    public static String REGION_PLATFORMINFO = "platformInfo";

    //新能源数据topic
    public static String NE_VECHILE_DATA_TOPIC = "NE_VECHILE_DATA_TOPIC";

    //新能源车辆登入登出topic
    public static String NE_VECHILE_LOGIN_TOPIC = "NE_VECHILE_LOGIN_TOPIC";

    public static String NE_VECHILE_LOGOUT_TOPIC = "NE_VECHILE_LOGOUT_TOPIC";

    //平台登入登出topic
    public static String NE_PLATFORM_LOGIN_TOPIC = "NE_PLATFORM_LOGIN_TOPIC";

    public static String NE_PLATFORM_LOGOUT_TOPIC = "NE_PLATFORM_LOGOUT_TOPIC";

    //xdiamond配置车辆登入登出的key值
    public static final String PROPERTY_TOPIC_VECHILE_KEY = "key_vhchile_login_topic";

    //xdiamond配置平台登入登出的key值
    public static final String PROPERTY_TOPIC_PLATFORM_KEY = "key_platform_login_topic";

    //xdiamond配置平台登入登出的key值
    public static final String PROPERTY_TOPIC_DATA_KEY = "key_vehicle_data_topic";

    //新能源数据个数
    public static final String NUM_NE_DATA_KEY = "numofnedata";

    public static Integer NUM_NE_DATA_VALUE = 63;

    public static final String _32960_02 = "32960_02";

    public static final String _32960_03 = "32960_03";
}
