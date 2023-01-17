package com.dfssi.dataplatform.devmanager.analysistask.util;

/**
 * Created by Hannibal on 2018-01-11.
 */
public class Constants {

    //数据资源从hdfs转换到hive的数据库名
    public static String HIVE_DBNAME = "dsconf";

    //日期类型代码
    public static final Integer FIELDTYPE_DATE = 10;

    public static final String RESOURCETYPE_HDFS= "5";

    public static final String RESOURCETYPE_HIVE = "6";

    public static final String USER_ADMIN = "admin";

    public static final String DATEFORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String[] HIVE_CONF_FIELDS = new String[]{"url", "dbName", "driverclassname", "username", "password", "tableName"};


    public static String CONF_URL = "url";
    public static String CONF_TABLENAME = "tableName";
    public static String CONF_DBNAME = "dbName";
    public static String CONF_USERNAME = "username";
    public static String CONF_PASSWORD = "password";
    public static String CONF_DRIVER = "driverclassname";
    public static String CONF_PATH = "path";
    public static String CONF_FILEPREFIX = "filePrefix";
    public static String CONF_FILESUFFIX = "fileSuffix";
    public static String CONF_DELIMITER = "delimiter";
    public static String CONF_PARTITIONKEY = "partitionKey";

    public static String VALUE_URL = "jdbc:hive2://172.16.1.230:10000";
    public static String VALUE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    public static String KEY_RESULT_STATUS = "status";
    public static String KEY_RESULT_MESSAGE = "message";
    public static String KEY_RESULT_DATA = "data";

    public static final int REQUEST_STATUS_SUCCESS = 0;
    public static final int REQUEST_STATUS_FAIL = 1;

}
