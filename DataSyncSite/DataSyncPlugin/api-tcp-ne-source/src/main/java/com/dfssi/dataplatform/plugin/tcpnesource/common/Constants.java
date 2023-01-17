package com.dfssi.dataplatform.plugin.tcpnesource.common;

import java.nio.charset.Charset;

/**
 * 公共静态常量配置
 * Created by Hannibal on 2018-03-01.
 */
public class Constants {

    public static final String string_encoding = "GBK";
    public static final String TASK_ID_KEY = "taskId";//任务id
    public static final String MSG_ID_KEY = "msgId";//消息头ID
    public static final String KEY_HEADER = "key";//分区键

    public static final String FIELD_KEY_PARTITION = "vin";//分区字段

    //新能源数据topic
    public static String NE_VECHILE_DATA_TOPIC = "NE_VECHILE_DATA_TOPIC";

    //新能源车辆登入登出topic
    public static String NE_VECHILE_LOGIN_TOPIC = "NE_VECHILE_LOGIN_TOPIC";

    //平台登入登出topic
    public static String NE_PLATFORM_LOGIN_TOPIC = "NE_PLATFORM_LOGIN_TOPIC";


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
    public static String PREFIX_32960 = "32960_0";
}
