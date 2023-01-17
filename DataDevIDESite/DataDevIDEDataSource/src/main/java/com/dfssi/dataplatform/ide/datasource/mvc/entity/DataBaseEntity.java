package com.dfssi.dataplatform.ide.datasource.mvc.entity;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;


/**
 * 数据库字段信息
 * @author dingsl
 * @since 2018/8/21
 */
@Data
@Getter
public class DataBaseEntity implements Serializable {
    public static String IP = "ip";
    public static String PORT = "port";
    public static String DATABASENAME = "databaseName";
    public static String DATABASEUSERNAME = "databaseUsername";
    public static String DATABASEPASSWORD = "databasePassword";
    public static String TABLENAME = "tableName";
    public static String HDFSIP = "hdfsIp";
    public static String HDFSPORT = "hdfsPort";
    public static String PATH = "path";
    public static String ZKADDRESS = "zkAddress";
    public static String KAFKATOPIC = "kafkaTopic";
    public static String KAFKATADDRESS = "kafkaAddress";
    public static String KAFKAREQUESTTIMEOUT = "requestTimeout";
    public static String KAFKASESSIONTIMEOUT = "sessionTimeout";
    public static String KAFKAGROUPID = "groupId";
    public static String TCPIP = "IP";
    public static String TCPPORT = "Port";
    public static String MASTERIP = "masterIP";
    public static String MASTERPORT = "masterPort";
    public static String CLIENTIP = "clientIP";
    public static String CLIENTPORT = "clientPort";
    public static String ESCLUSTERNAME = "esClusterName";
    public static String QUERYDB = "queryDB";
    public static String QUERYBIGDATA = "queryBigdata";
    public static String QUERYINTERFACE = "queryInterface";
    public static String QUERYPRIVATERESOURCES = "queryPrivateResources";
    public static String QUERYSHAREDRESOURCES = "querySharedResources";

    public static String STR_HTTP_IP ="httpIP";
    public static String STR_HTTP_PORT ="httpPort";
    public static String STR_HTTP_PATH ="httpPath";
    public static String STR_HTTP_REQUESTMODEL ="requestModel";
    public static String STR_HTTP_PARAMS ="httpParams";


}