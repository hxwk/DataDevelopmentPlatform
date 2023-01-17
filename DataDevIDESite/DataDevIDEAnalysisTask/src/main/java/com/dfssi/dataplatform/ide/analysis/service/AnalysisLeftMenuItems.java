package com.dfssi.dataplatform.ide.analysis.service;

import com.dfssi.dataplatform.analysis.entity.AnalysisSourceEntity;
import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import com.dfssi.dataplatform.analysis.entity.DataResourceStepEntity;
import com.dfssi.dataplatform.common.service.CommonMenuCtr;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class AnalysisLeftMenuItems extends ArrayList {

    @JsonIgnore
    private CommonMenuCtr dataSourceCtr = null;
    @JsonIgnore
    private CommonMenuCtr preprocessCtr = null;
    @JsonIgnore
    private CommonMenuCtr algorithmCtr = null;
    @JsonIgnore
    private CommonMenuCtr outputCtr = null;

    public AnalysisLeftMenuItems() {
        this.init();
    }

    private void init() {
        this.buildDataSourceCtr();
        this.buildPreprocessCtr();
        this.buildAlgorithmCtr();
        this.buildOutputCtr();
    }

    private void buildDataSourceCtr() {
        dataSourceCtr = new CommonMenuCtr(1, "数据资源", "数据资源", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr mysqlSourceCtr = new CommonMenuCtr(2, "MySQL", "MySQL", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr oracleSourceCtr = new CommonMenuCtr(3, "Oracle", "Oracle", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr sqlSourceCtr = new CommonMenuCtr(4, "SQL", "SQL", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr db2SourceCtr = new CommonMenuCtr(5, "DB2", "DB2", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hdfsSourceCtr = new CommonMenuCtr(6, "Hdfs", "Hdfs", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hiveSourceCtr = new CommonMenuCtr(7, "Hive", "Hive", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hbaseSourceCtr = new CommonMenuCtr(8, "Hbase", "Hbase", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr impalaSourceCtr = new CommonMenuCtr(9, "Impala", "Impala", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr kafkaSourceCtr = new CommonMenuCtr(10, "Kafka", "Kafka", "./css/zTreeStyle/img/diy/8.png");
//        CommonMenuCtr otherSourceCtr = new CommonMenuCtr(11, "其它", "其它", "./css/zTreeStyle/img/diy/8.png");


        dataSourceCtr.addChild(mysqlSourceCtr);
        dataSourceCtr.addChild(oracleSourceCtr);
        dataSourceCtr.addChild(sqlSourceCtr);
        dataSourceCtr.addChild(db2SourceCtr);
        dataSourceCtr.addChild(hdfsSourceCtr);
        dataSourceCtr.addChild(hiveSourceCtr);
        dataSourceCtr.addChild(hbaseSourceCtr);
        dataSourceCtr.addChild(impalaSourceCtr);
        dataSourceCtr.addChild(kafkaSourceCtr);
//        dataSourceCtr.addChild(otherSourceCtr);

        this.add(dataSourceCtr);
    }

    private void buildPreprocessCtr() {
        preprocessCtr = new CommonMenuCtr(2, "数据预处理", "数据预处理", "./css/zTreeStyle/img/diy/8.png");
        this.add(preprocessCtr);
    }

    private void buildAlgorithmCtr() {
        algorithmCtr = new CommonMenuCtr(3, "算法", "算法", "./css/zTreeStyle/img/diy/8.png");
        this.add(algorithmCtr);
    }

    private void buildOutputCtr() {
        outputCtr = new CommonMenuCtr(4, "输出", "输出", "./css/zTreeStyle/img/diy/8.png");
        this.add(outputCtr);
    }

    public void addDataSourceItem(AnalysisSourceEntity drce) {

        CommonMenuCtr mysqlSourceCtr = dataSourceCtr.findByName("MySQL");
        CommonMenuCtr oracleSourceCtr = dataSourceCtr.findByName("Oracle");
        CommonMenuCtr sqlSourceCtr = dataSourceCtr.findByName("SQL");
        CommonMenuCtr db2SourceCtr = dataSourceCtr.findByName("DB2");
        CommonMenuCtr hdfsSourceCtr = dataSourceCtr.findByName("Hdfs");
        CommonMenuCtr hiveResourceCtr = dataSourceCtr.findByName("Hive");
        CommonMenuCtr hbaseSourceCtr = dataSourceCtr.findByName("Hbase");
        CommonMenuCtr impalaSourceCtr = dataSourceCtr.findByName("Impala");
        CommonMenuCtr kafkaSourceCtr = dataSourceCtr.findByName("Kafka");
//        CommonMenuCtr otherSourceCtr = dataSourceCtr.findByName("其它");

        if (StringUtils.isBlank(drce.getShowName())) {
            drce.setShowName(drce.getDataresourceName());
        }

        String str= (drce.getDataresourceType()) == null ? "其它" : drce.getDataresourceType();
        switch (str.toLowerCase()) {
            case "mysql":
                mysqlSourceCtr.addChild(new DataResourceStepEntity(drce,"InputMysqlTable", "DataSource", ""));
                break;
            case "oracle":
                oracleSourceCtr.addChild(new DataResourceStepEntity(drce, "InputOracleTable", "DataSource", ""));
                break;
            case "sqlserver":
                sqlSourceCtr.addChild(new DataResourceStepEntity(drce, "InputSqlserverTable", "DataSource", ""));
                break;
            case "db2":
                db2SourceCtr.addChild(new DataResourceStepEntity(drce, "InputDb2Table", "DataSource", ""));
                break;
            case "hdfs":
//                hdfsSourceCtr.addChild(new DataResourceStepEntity(drce, "InputHdfs", "DataSource", ""));
                break;
            case "hive":
                hiveResourceCtr.addChild(new DataResourceStepEntity(drce, "InputHiveTable", "DataSource", ""));
                break;
            case "hbase":
//                hbaseSourceCtr.addChild(new DataResourceStepEntity(drce, "InputHbaseTable", "DataSource", ""));
                break;
            case "impala":
                impalaSourceCtr.addChild(new DataResourceStepEntity(drce, "InputImpalaTable", "DataSource", ""));
                break;
            case "kafka":
//                kafkaSourceCtr.addChild(new DataResourceStepEntity(drce, "InputKafkaDirectReceiver", "DataSource", ""));
                break;
            default:
//                otherSourceCtr.addChild(new DataResourceStepEntity(drce, "", "DataSource", ""));
        }

    }

    public void addPreprocessItem(AnalysisStepTypeEntity aste) {
        this.generateShowName(aste);
        this.preprocessCtr.addChild(aste);
    }

    public void addAlgorithmItem(AnalysisStepTypeEntity aste) {
        this.generateShowName(aste);
        this.algorithmCtr.addChild(aste);
    }

    public void addOutputItem(AnalysisStepTypeEntity aste) {
        this.generateShowName(aste);
        this.outputCtr.addChild(aste);
    }

    private void generateShowName(AnalysisStepTypeEntity aste) {
        if (StringUtils.isBlank(aste.getShowName())) {
            aste.setShowName(aste.getName());
        }
    }
}
