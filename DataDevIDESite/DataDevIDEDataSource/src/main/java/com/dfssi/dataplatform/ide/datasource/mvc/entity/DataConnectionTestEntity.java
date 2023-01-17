package com.dfssi.dataplatform.ide.datasource.mvc.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *数据库连接测试字段信息
 * @author dingsl
 * @since 2018/8/22
 */
@Data
public class DataConnectionTestEntity implements Serializable {
    private String ip;
    private String port;
    private String databaseName;
    private String databaseUsername;
    private String databasePassword;
    private String tableName;

    private String hdfsPath;

    private String zkAddress;
    private String kafkaAddress;
    private String kafkaTopic;
    private String kafkaRequestTimeOut;
    private String kafkaSessionTimeOut;
    private String kafkaGroupId;

    private String esClusterName;

    private String geodeRegionName;

    private String path;
    private String requestModel;
    private String requestParams;

    private String masterIP;
    private String masterPort;
    private String clientIP;
    private String clientPort;
}
