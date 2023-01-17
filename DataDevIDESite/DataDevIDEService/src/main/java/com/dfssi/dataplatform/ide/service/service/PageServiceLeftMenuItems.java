package com.dfssi.dataplatform.ide.service.service;

import com.dfssi.dataplatform.analysis.entity.AnalysisResourceEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import com.dfssi.dataplatform.common.service.CommonMenuCtr;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class PageServiceLeftMenuItems extends ArrayList {

//    @JsonIgnore
//    private CommonMenuCtr dataResourceCtr = null;
//    @JsonIgnore
//    private CommonMenuCtr biComponentCtr = null;
//
//
//    public PageServiceLeftMenuItems() {
//        this.init();
//    }
//
//    private void init() {
//        this.buildDataResourceCtr();
//        this.buildBICompCtr();
//    }
//
//    private void buildDataResourceCtr() {
//        dataResourceCtr = new CommonMenuCtr(1, "数据源", "数据源", "./css/zTreeStyle/img/diy/8.png");
//        CommonMenuCtr hiveResourceCtr = new CommonMenuCtr(12, "Hive", "Hive", "./css/zTreeStyle/img/diy/8.png");
//        dataResourceCtr.addChild(hiveResourceCtr);
//
//        this.add(dataResourceCtr);
//    }
//
//    private void buildBICompCtr() {
//        biComponentCtr = new CommonMenuCtr(2, "组件BI", "组件BI", "./css/zTreeStyle/img/diy/8.png");
//        this.add(biComponentCtr);
//    }
//
//    public void addDataResourceItem(DataResourceConfEntity drce) {
//        CommonMenuCtr hiveResourceCtr = dataResourceCtr.findByName("Hive");
//        if (StringUtils.isBlank(drce.getShowName())) {
//            drce.setShowName(drce.getDataresName());
//        }
//        hiveResourceCtr.addChild(drce);
//    }
//
//    public void addBICompItem(AnalysisStepTypeEntity aste) {
//        this.generateShowName(aste);
//        this.biComponentCtr.addChild(aste);
//    }
//
//    private void generateShowName(AnalysisStepTypeEntity aste) {
//        if (StringUtils.isBlank(aste.getShowName())) {
//            aste.setShowName(aste.getName());
//        }
//    }

    @JsonIgnore
    private CommonMenuCtr dataResourceCtr = null;
    @JsonIgnore
    private CommonMenuCtr biComponentCtr = null;

    public PageServiceLeftMenuItems() {
        this.init();
    }

    private void init() {
        this.buildDataResourceCtr();
        this.buildBICompCtr();
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

    private void buildBICompCtr() {
        biComponentCtr = new CommonMenuCtr(2, "组件BI", "组件BI", "./css/zTreeStyle/img/diy/8.png");
        this.add(biComponentCtr);
    }

    public void addDataResourceItem(AnalysisResourceEntity drce) {

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


        if (StringUtils.isBlank(drce.getShowName())) {
            drce.setShowName(drce.getDataresName());
        }
        switch (drce.getDataresType().toLowerCase()) {
            case "mysql":
                mysqlResourceCtr.addChild(drce);
                break;
            case "oracle":
                oracleResourceCtr.addChild(drce);
                break;
            case "sql":
                sqlResourceCtr.addChild(drce);
                break;
            case "db2":
                db2ResourceCtr.addChild(drce);
                break;
            case "hdfs":
                hdfsResourceCtr.addChild(drce);
                break;
            case "hive":
                hiveResourceCtr.addChild(drce);
                break;
            case "hbase":
                hbaseResourceCtr.addChild(drce);
                break;
            case "impala":
                impalaResourceCtr.addChild(drce);
                break;
            case "kafka":
                kafkaResourceCtr.addChild(drce);
                break;
            case "tcp":
                tcpResourceCtr.addChild(drce);
                break;
            case "udp":
                udpResourceCtr.addChild(drce);
                break;
            default:
                otherSourceCtr.addChild(drce);

        }

    }

    public void addBICompItem(AnalysisStepTypeEntity aste) {
        this.generateShowName(aste);
        this.biComponentCtr.addChild(aste);
    }

    private void generateShowName(AnalysisStepTypeEntity aste) {
        if (StringUtils.isBlank(aste.getShowName())) {
            aste.setShowName(aste.getName());
        }
    }
}
