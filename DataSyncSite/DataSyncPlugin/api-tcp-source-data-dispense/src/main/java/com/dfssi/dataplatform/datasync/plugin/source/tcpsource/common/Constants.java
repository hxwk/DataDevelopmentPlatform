package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common;

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

    public static final String FIELD_KEY_PARTITION = "vid";//分区字段

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
    public static final String VEHILCE_ON_UPDATE = "vehicle_on_update:"; //车辆ON档状态信息变更

    public static final int ENCODE_SALT = 10;

    //上下线topic
    public static String LOGIN_TOPIC = "VNND_LOGININFO_STATUS";
    //指令发送状态topic
    public static String INSTRUCTION_SEND_TOPIC = "VNND_INSTRUCTION_SEND_STATUS";
    //指令执行状态topic
    public static String INSTRUCTION_RESULT_TOPIC = "VNND_INSTRUCTION_RESULT_STATUS";
    //查询指令执行结果topic
    public static String INSTRUCTION_RESULT_QUERY_TOPIC = "VNND_INSTRUCTION_RESULT_LIST";
    //通用指令执行状态
    public static String INSTRUCTION_COMMAN_RESULT_TOPIC = "VNND_COMMAN_INSTRUCTION_RESULT_STATUS";
    //锁车状态topic
    public static String LOCKVEHICLE_STATUS_TOPIC = "VNND_LOCKVEHICLE_STATUS_STATUS";
    //锁车参数设置状态topic
    public static String LOCKVEHICLE_PARAM_SET_TOPIC = "LOCKVEHICLE_PARAM_SET_STATUS";

    public static String DMS_XML_PATH = "config/dms.xml";

    public static String DB_VNND_DRIVER = "com.mysql.jdbc.Driver";

    public static String DB_VNND_URL = "jdbc:mysql://172.16.1.241:3306/SSI_SERVICE_PLATFORM?useUnicode=true&characterEncoding=utf8&useSSL=false&Allow ZeroDatetime=True";

    public static String DB_VNND_USRENAME = "app_user";

    public static String DB_VNND_PASSWORD = "112233";

    public static Integer REDIS_VS_STATUS_DB = 2;

    public static final String POSITIONINFORMATION_TOPIC_KEY = "POSITIONINFORMATION_TOPIC_KEY";

    public static String POSITIONINFORMATION_TOPIC = "POSITIONINFORMATION_0200_TOPIC";

    public static final String POSITIONINFORMATIONS_TOPIC_KEY = "POSITIONINFORMATIONS_TOPIC_KEY";

    public static String POSITIONINFORMATIONS_TOPIC = "POSITIONINFORMATION_0704_TOPIC";

    public static final String CANINFORMATION_TOPIC_KEY = "CANINFORMATION_TOPIC_KEY";

    public static String CANINFORMATION_TOPIC = "CANINFORMATION_0705_TOPIC";

    public static final String DRIVERCARDINFORMATION_TOPIC_KEY = "DRIVERCARDINFORMATION_TOPIC_KEY";

    public static String DRIVERCARDINFORMATION_TOPIC = "DRIVERCARDINFORMATION_TOPIC";

    public static String GPSANDCANINFORMATIC_TOPIC ="GPSANDCANINFORMATIC_TOPIC";

    public static String AVRESOURCELIST_TOPIC = "AVRESOURCELIST_TOPIC";

    public static String AVPROPERTY_TOPIC = "AVPROPERTY_1003_TOPIC";

    public static String AVPASSENGERFLOW_TOPIC = "AVPASSENGERFLOW_1005_TOPIC";

    public static String TERMINALPROPERTEIS_TOPIC = "TERMINALPROPERTEIS_0107_TOPIC";

    public static Map<String, Integer> PARAM_ITEM_8103_MAP = new HashMap<>();

    //上次照片文件存储的临时目录
    public static String tmpDir = "/tmp/vnnd/";


    public static final Charset string_charset = Charset.forName(string_encoding);

    public static final String TOPIC_HEADER = "topic";

    // 标识位
    public static final int pkg_delimiter = 0x7e;
    // 客户端发呆15分钟后,服务器主动断开连接
    public static int tcp_client_idle_minutes = 30;
    //终端上传
    // 终端通用应答
    public static final int msg_id_terminal_common_resp = 0x0001;
    // 终端心跳
    public static final int msg_id_terminal_heart_beat = 0x0002;
    // 终端注册
    public static final int msg_id_terminal_register = 0x0100;
    // 终端注销
    public static final int msg_id_terminal_log_out = 0x0003;
    // 终端鉴权
    public static final int msg_id_terminal_authentication = 0x0102;
    // 位置信息汇报
    public static final int msg_id_terminal_location_info_upload = 0x0200;
    // 胎压数据透传
    public static final int msg_id_terminal_transmission_tyre_pressure = 0x0600;
    // 查询终端参数应答
    public static final int msg_id_terminal_param_query_resp = 0x0104;

    //查询终端属性
    public static final int msg_id_terminal_attri_query = 0x8107; // new add
    //补传分包请求
    public static final int msg_id_terminal_resend_pack_request = 0x8003; //new add

    //事件报告
    public static final int msg_id_terminal_event_report = 0x0301;
    //提问应答
    public static final int msg_id_terminal_question_response = 0x0302;
    //信息单播
    public static final int msg_id_terminal_information_unicast = 0x0303;
    //行驶记录数据上传
    public static final int msg_id_terminal_travel_record = 0x0700;
    //电子订单上报
    public static final int msg_id_terminal_eorder_report = 0x0701;
    //驾驶员身份信息采集上报
    public static final int msg_id_terminal_driver_information = 0x0702;
    //定位数据批量上传
    public static final int msg_id_terminal_location_informations = 0x0704;
    //CAN 总线数据上传
    public static final int msg_id_terminal_candata_report = 0x0705;
    //多媒体事件信息上传
    public static final int msg_id_terminal_media_event_report = 0x0800;
    //多媒体数据上传
    public static final int msg_id_terminal_media_data_report = 0x0801;
    //摄像头立即拍摄命令应答
    public static final int msg_id_terminal_camera_shot_response = 0x0805;
    //数据上行透传
    public static final int msg_id_terminal_data_uplink = 0x0900;
    //数据压缩上报
    public static final int msg_id_terminal_data_compression_report = 0x0901;
    //终端 RSA 公钥
    public static final int msg_id_terminal_RSA_publickey = 0x0A00;



    //平台下发
    // 平台通用应答
    public static final int cmd_common_resp = 0x8001;
    // 终端注册应答
    public static final int cmd_terminal_register_resp = 0x8100;
    // 设置终端参数
    public static final int cmd_terminal_param_settings = 0X8103;
    // 查询终端参数
    public static final int cmd_terminal_param_query = 0x8104;

    //终端控制
    public static final int cmd_terminal_ctrl = 0x8105; // new add

    //查询终端属性应答
    public static final int cmd_terminal_attr_resp = 0x0107; // new add

    //xdaimond 配置key GEODE_CONNECT geode连接地址
    public static final String GEODE_CONNECT_KEY = "GEODE_CONNECT";
    //xdaimond 配置key REGION_CVVEHICLEINFO 表名
    public static final String REGION_CVVEHICLEINFO_KEY = "REGION_CVVEHICLEINFO";

    //geode服务地址
    public static String GEODE_CONNECT = null;

    public static String REGION_CVVEHICLEINFO = "cvVehicleBaseInfo";//车辆基本信息表



}
