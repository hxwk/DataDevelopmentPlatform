package com.dfssi.dataplatform.abs.utils;

/**
 * Description:
 *
 * @author Weijj
 * @version 2018/9/15 8:39
 */
public class AbsConstant {
    /**kafka topic*/
    public final static String KAFKA_TOPIC_NAME = "POSITIONINFORMATION_0200_TOPIC";
    public final static Integer KAFKA_CONSUMER_TIMEOUT_VALUE = 1000;
    public final static String ABS_WARN_INFO = "上传数据的数据格式不正确";
    public final static String ABS_VID_NAME = "vid";
    public final static String ABS_SENSOR_SPEED_NAME = "sensorSpeed";
    public final static String ABS_WHEEL_SPEED_NAME = "wheelSpeed";
    public final static String ABS_BRAKING_NAME = "braking";
    public final static String ABS_GEAR_NAME = "gear";
    public final static String ABS_BRAKE_STATUS_NAME = "brakeStatus";
    public final static String ABS_ABS_STATUS_NAME = "absStatus";
    public final static String ABS_LON_NAME = "highPrecisionLon";
    public final static String ABS_LAT_NAME = "highPrecisionLat";
    public final static String ABS_COLL_TIME_NAME = "absTime";
    public final static String ABS_COLLECTION_TIME_NAME = "collectionTime";
    /**China Standard Time*/
    public final static String CST_TIME_ZONE = "+8";

    public final static String DATE_FORMAT_PARTTERN = "yyyy-MM-dd HH:mm:ss";
    /**Redis key*/
    public final static String ABS_CHECH_REDIS_KEY = "abscheck:dev";


    public static final String ABS_SPEED_NANE = "absSpeed";
}
