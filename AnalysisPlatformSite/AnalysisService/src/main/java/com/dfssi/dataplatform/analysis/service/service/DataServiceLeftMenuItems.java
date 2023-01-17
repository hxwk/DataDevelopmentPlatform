package com.dfssi.dataplatform.analysis.service.service;

import com.dfssi.dataplatform.analysis.common.entity.CommonMenuCtr;
import com.dfssi.dataplatform.analysis.service.entity.ResourceEntity;
import com.dfssi.dataplatform.analysis.service.entity.ServiceStepEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataServiceLeftMenuItems extends ArrayList {

    @JsonIgnore
    private CommonMenuCtr dataResourceCtr = null;
    @JsonIgnore
    private CommonMenuCtr shareCtr = null;

    public DataServiceLeftMenuItems() {
        this.init();
    }

    private void init() {
        this.buildDataResourceCtr();
        this.buildShareCtr();
    }

    private void buildDataResourceCtr() {
        dataResourceCtr = new CommonMenuCtr(1, "数据资源", "数据资源", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr mysqlResourceCtr = new CommonMenuCtr(2, "MySQL", "MySQL", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr oracleResourceCtr = new CommonMenuCtr(3, "Oracle", "Oracle", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr sqlResourceCtr = new CommonMenuCtr(4, "SQL", "SQL", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr db2ResourceCtr = new CommonMenuCtr(5, "DB2", "DB2", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hdfsResourceCtr = new CommonMenuCtr(6, "Hdfs", "Hdfs", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hiveResourceCtr = new CommonMenuCtr(7, "Hive", "Hive", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hbaseResourceCtr = new CommonMenuCtr(8, "Hbase", "Hbase", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr impalaResourceCtr = new CommonMenuCtr(9, "Impala", "Impala", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr kafkaResourceCtr = new CommonMenuCtr(10, "Kafka", "Kafka", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr tcpResourceCtr = new CommonMenuCtr(11, "TCP", "TCP", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr udpResourceCtr = new CommonMenuCtr(12, "UDP", "UDP", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr otherSourceCtr = new CommonMenuCtr(13, "其它", "其它", "./css/zTreeStyle/img/diy/8.png");


        dataResourceCtr.addChild(mysqlResourceCtr);
        dataResourceCtr.addChild(oracleResourceCtr);
        dataResourceCtr.addChild(sqlResourceCtr);
        dataResourceCtr.addChild(db2ResourceCtr);
        dataResourceCtr.addChild(hdfsResourceCtr);
        dataResourceCtr.addChild(hiveResourceCtr);
        dataResourceCtr.addChild(hbaseResourceCtr);
        dataResourceCtr.addChild(impalaResourceCtr);
        dataResourceCtr.addChild(kafkaResourceCtr);
        dataResourceCtr.addChild(tcpResourceCtr);
        dataResourceCtr.addChild(udpResourceCtr);
        dataResourceCtr.addChild(otherSourceCtr);

        this.add(dataResourceCtr);
    }

    private void buildShareCtr() {
        shareCtr = new CommonMenuCtr(2, "共享", "共享", "./css/zTreeStyle/img/diy/8.png");
        this.add(shareCtr);
    }

    public void addDataResourceItem(ResourceEntity rce) {

        CommonMenuCtr mysqlResourceCtr = dataResourceCtr.findByName("MySQL");
        CommonMenuCtr oracleResourceCtr = dataResourceCtr.findByName("Oracle");
        CommonMenuCtr sqlResourceCtr = dataResourceCtr.findByName("SQL");
        CommonMenuCtr db2ResourceCtr = dataResourceCtr.findByName("DB2");
        CommonMenuCtr hdfsResourceCtr = dataResourceCtr.findByName("Hdfs");
        CommonMenuCtr hiveResourceCtr = dataResourceCtr.findByName("Hive");
        CommonMenuCtr hbaseResourceCtr = dataResourceCtr.findByName("Hbase");
        CommonMenuCtr impalaResourceCtr = dataResourceCtr.findByName("Impala");
        CommonMenuCtr kafkaResourceCtr = dataResourceCtr.findByName("Kafka");
        CommonMenuCtr tcpResourceCtr = dataResourceCtr.findByName("TCP");
        CommonMenuCtr udpResourceCtr = dataResourceCtr.findByName("UDP");
        CommonMenuCtr otherSourceCtr = dataResourceCtr.findByName("其它");

        switch (rce.getDataresType().toLowerCase()) {
            case "mysql":
                mysqlResourceCtr.addChild(getResourceChild(rce));
                break;
            case "oracle":
                oracleResourceCtr.addChild(getResourceChild(rce));
                break;
            case "sql":
                sqlResourceCtr.addChild(getResourceChild(rce));
                break;
            case "db2":
                db2ResourceCtr.addChild(getResourceChild(rce));
                break;
            case "hdfs":
                hdfsResourceCtr.addChild(getResourceChild(rce));
                break;
            case "hive":
                hiveResourceCtr.addChild(getResourceChild(rce));
                break;
            case "hbase":
                hbaseResourceCtr.addChild(getResourceChild(rce));
                break;
            case "impala":
                impalaResourceCtr.addChild(getResourceChild(rce));
                break;
            case "kafka":
                kafkaResourceCtr.addChild(getResourceChild(rce));
                break;
            case "tcp":
                tcpResourceCtr.addChild(getResourceChild(rce));
                break;
            case "udp":
                udpResourceCtr.addChild(getResourceChild(rce));
                break;
            default:
                otherSourceCtr.addChild(getResourceChild(rce));
        }
    }

    public void addBICompItem(ServiceStepEntity sse) {
        this.shareCtr.addChild(getStepChild(sse));
    }

    private Map<String, String> getResourceChild(ResourceEntity rce) {
        Map<String, String> map = new HashMap<>();
        map.put("showName", rce.getDataresName());
        map.put("resourceId", rce.getResourceId());
        map.put("modelId", rce.getModelId());
        map.put("modelStepId", rce.getModelStepId());
        map.put("dataresName", rce.getDataresName());
        map.put("dataresType", rce.getDataresType());
        return map;
    }

    private Map<String, String> getStepChild(ServiceStepEntity sse) {
        Map<String, String> map = new HashMap<>();
        map.put("showName", sse.getName());
        map.put("stepId", sse.getStepId() + "");
        map.put("name", sse.getName());
        map.put("stepType", sse.getStepType());
        map.put("description", sse.getDescription());
        return map;
    }
}
