package com.dfssi.dataplatform.ide.access.mvc.entity;

import com.dfssi.dataplatform.ide.access.mvc.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * 接入任务新增修改左侧树形结构实体类
 * Created by hongs on 2018/5/2.
 */
public class AccessLeftMenuItemsEntity extends ArrayList {
    @JsonIgnore
    private CommonMenuCtrEntity dataResource = null;
    @JsonIgnore
    private CommonMenuCtrEntity channel = null;
    @JsonIgnore
    private CommonMenuCtrEntity process = null;
    @JsonIgnore
    private CommonMenuCtrEntity outputsource = null;

    private List children = new ArrayList();

    public AccessLeftMenuItemsEntity() {
        this.init();
    }

    /**
     * 初始化
     */
    public void init(){
        this.buildDataResourceCtr();
        this.buildChannelCtr();
        this.buildProcessCtr();
        this.buildOutputSourceCtr();
    }

    /**
     * 初始化数据源栏父节点，code为自定义，必须保证1开头
     */
    private void buildDataResourceCtr() {
        dataResource = new CommonMenuCtrEntity(1000, "数据源", "数据源");
        CommonMenuCtrEntity mysqlResourceCtr = new CommonMenuCtrEntity(1100, "MySQL", "MySQL");
        CommonMenuCtrEntity oracleResourceCtr = new CommonMenuCtrEntity(1700, "Oracle", "Oracle");
        CommonMenuCtrEntity sqlserverResourceCtr = new CommonMenuCtrEntity(1800, "SQLServer", "SQLServer");
        CommonMenuCtrEntity db2ResourceCtr = new CommonMenuCtrEntity(1900, "DB2", "DB2");
        CommonMenuCtrEntity kafkaResourceCtr = new CommonMenuCtrEntity(1102, "Kafka", "Kafka");
        CommonMenuCtrEntity hiveResourceCtr = new CommonMenuCtrEntity(1200, "Hive", "Hive");
        CommonMenuCtrEntity hbaseResourceCtr = new CommonMenuCtrEntity(1300, "HBase", "HBase");
        CommonMenuCtrEntity hdfsResourceCtr = new CommonMenuCtrEntity(1101, "Hdfs", "Hdfs");
        CommonMenuCtrEntity tcpResourceCtr = new CommonMenuCtrEntity(1400, "TCP", "TCP");
        CommonMenuCtrEntity udpResourceCtr = new CommonMenuCtrEntity(1500, "UDP", "UDP");
        CommonMenuCtrEntity httpResourceCtr = new CommonMenuCtrEntity(1600, "HTTP", "HTTP");
        dataResource.addChild(mysqlResourceCtr);
        dataResource.addChild(oracleResourceCtr);
        dataResource.addChild(sqlserverResourceCtr);
        dataResource.addChild(db2ResourceCtr);
        dataResource.addChild(kafkaResourceCtr);
        dataResource.addChild(hiveResourceCtr);
        dataResource.addChild(hbaseResourceCtr);
        dataResource.addChild(hdfsResourceCtr);
        dataResource.addChild(tcpResourceCtr);
        dataResource.addChild(udpResourceCtr);
        dataResource.addChild(httpResourceCtr);
        this.add(dataResource);
    }

    /**
     * 通道
     */
    private void buildChannelCtr(){
        channel = new CommonMenuCtrEntity(2000, "通道", "通道");
        this.add(channel);
    }

    private void buildProcessCtr(){
        process = new CommonMenuCtrEntity(3000, "清洗转换", "清洗转换");
        this.add(process);
    }

    /**
     * 初始化数据资源栏父节点，code为自定义
     */
    private void buildOutputSourceCtr(){
        outputsource = new CommonMenuCtrEntity(4000, "数据资源", "数据资源");
        //CommonMenuCtrEntity mysqlResourceCtr = new CommonMenuCtrEntity(4101, "MySQL", "MySQL");
        //CommonMenuCtrEntity oracleResourceCtr = new CommonMenuCtrEntity(4102, "Oracle", "Oracle");
        //CommonMenuCtrEntity sqlserverResourceCtr = new CommonMenuCtrEntity(4103, "SQLServer", "SQLServer");
        CommonMenuCtrEntity hiveResourceCtr = new CommonMenuCtrEntity(4100, "Hive", "Hive");
        CommonMenuCtrEntity kafkaResourceCtr = new CommonMenuCtrEntity(4200, "Kafka", "Kafka");
        CommonMenuCtrEntity hdfsResourceCtr = new CommonMenuCtrEntity(4300, "Hdfs", "Hdfs");
        //outputsource.addChild(mysqlResourceCtr);
        //outputsource.addChild(oracleResourceCtr);
        //outputsource.addChild(sqlserverResourceCtr);
        outputsource.addChild(hiveResourceCtr);
        outputsource.addChild(kafkaResourceCtr);
        outputsource.addChild(hdfsResourceCtr);
        this.add(outputsource);
    }


    /**
     * 数据源栏给父节点添加数据
     * @param dsce
     */
    public void addDataSourceItem(DataSourceConfEntity dsce) {
        //根据数据源类型判断
        String datasourceType = dsce.getDatasourceType();
        if((Constants.STR_TASK_MYSQL).equals(datasourceType)){
            CommonMenuCtrEntity mysqlResource = dataResource.findByName("MySQL");
            mysqlResource.addChild(dsce);
        }else if((Constants.STR_TASK_ORACLE).equals(datasourceType)){
            CommonMenuCtrEntity oracleResource = dataResource.findByName("Oracle");
            oracleResource.addChild(dsce);
        }else if((Constants.STR_TASK_SQLSERVER).equals(datasourceType)){
            CommonMenuCtrEntity sqlserverResource = dataResource.findByName("SQLServer");
            sqlserverResource.addChild(dsce);
        }else if((Constants.STR_TASK_DBTWO).equals(datasourceType)){
            CommonMenuCtrEntity db2Resource = dataResource.findByName("DB2");
            db2Resource.addChild(dsce);
        }else if((Constants.STR_TASK_HDFS).equals(datasourceType)){
            CommonMenuCtrEntity hdfsResource = dataResource.findByName("Hdfs");
            hdfsResource.addChild(dsce);
        }else if((Constants.STR_TASK_KAFKA).equals(datasourceType)){
            CommonMenuCtrEntity kafkaResource = dataResource.findByName("Kafka");
            kafkaResource.addChild(dsce);
        }else if((Constants.STR_TASK_HIVE).equals(datasourceType)){
            CommonMenuCtrEntity hiveResource = dataResource.findByName("Hive");
            hiveResource.addChild(dsce);
        }else if((Constants.STR_TASK_HBASE).equals(datasourceType)){
            CommonMenuCtrEntity hbaseResource = dataResource.findByName("HBase");
            hbaseResource.addChild(dsce);
        }else if((Constants.STR_TASK_TCP).equals(datasourceType)) {
            CommonMenuCtrEntity netcpResource = dataResource.findByName("TCP");
            netcpResource.addChild(dsce);
        }else if((Constants.STR_TASK_UDP).equals(datasourceType)) {
            CommonMenuCtrEntity bctcpResource = dataResource.findByName("UDP");
            bctcpResource.addChild(dsce);
        }else if((Constants.STR_TASK_HTTP).equals(datasourceType)) {
            CommonMenuCtrEntity httpResource = dataResource.findByName("HTTP");
            httpResource.addChild(dsce);
        }
    }

    public void addChannelItem(TaskAccessPluginsEntity tape) {
            this.channel.addChild(tape);
    }

    public void addProcessItem(TaskCleanTransformMappingEntity  tctme) {
        this.process.addChild(tctme);
    }

    /**
     * 数据资源栏给父节点添加数据
     * @param tape
     */
    public void addOutputItem(DataResourceConfEntity tape) {
        String dataresourceType = tape.getDataresourceType();
        if((Constants.STR_TASK_HDFS).equals(dataresourceType)){
            CommonMenuCtrEntity hdfsResource=outputsource.findByName("Hdfs");
            hdfsResource.addChild(tape);
        }else if((Constants.STR_TASK_HIVE).equals(dataresourceType)){
            CommonMenuCtrEntity hiveResource = outputsource.findByName("Hive");
            hiveResource.addChild(tape);
        }else if((Constants.STR_TASK_KAFKA).equals(dataresourceType)){
            CommonMenuCtrEntity kafkaResource = outputsource.findByName("Kafka");
            kafkaResource.addChild(tape);
        }
//         else if((Constants.STR_TASK_MYSQL).equals(dataresourceType)){
//            CommonMenuCtrEntity mysqlResource = outputsource.findByName("MySQL");
//            mysqlResource.addChild(tape);
//        }else if((Constants.STR_TASK_ORACLE).equals(dataresourceType)){
//            CommonMenuCtrEntity oracleResource = outputsource.findByName("Oracle");
//            oracleResource.addChild(tape);
//        }else if((Constants.STR_TASK_SQLSERVER).equals(dataresourceType)){
//            CommonMenuCtrEntity sqlserverResource = outputsource.findByName("SQLServer");
//            sqlserverResource.addChild(tape);
//        }
    }
}
