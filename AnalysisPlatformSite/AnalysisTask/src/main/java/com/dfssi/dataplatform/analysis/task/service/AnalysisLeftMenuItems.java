package com.dfssi.dataplatform.analysis.task.service;

import com.dfssi.dataplatform.analysis.common.entity.CommonMenuCtr;
import com.dfssi.dataplatform.analysis.task.entity.StepInfoEntity;
import com.dfssi.dataplatform.analysis.task.entity.DataresourceConfEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        dataSourceCtr.addChild(mysqlSourceCtr);
        dataSourceCtr.addChild(oracleSourceCtr);
        dataSourceCtr.addChild(sqlSourceCtr);
        dataSourceCtr.addChild(db2SourceCtr);
        dataSourceCtr.addChild(hdfsSourceCtr);
        dataSourceCtr.addChild(hiveSourceCtr);
        dataSourceCtr.addChild(hbaseSourceCtr);
        dataSourceCtr.addChild(impalaSourceCtr);
        dataSourceCtr.addChild(kafkaSourceCtr);

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

    public void addDataSourceItem(DataresourceConfEntity drce) {

        CommonMenuCtr mysqlSourceCtr = dataSourceCtr.findByName("MySQL");
        CommonMenuCtr oracleSourceCtr = dataSourceCtr.findByName("Oracle");
        CommonMenuCtr sqlSourceCtr = dataSourceCtr.findByName("SQL");
        CommonMenuCtr db2SourceCtr = dataSourceCtr.findByName("DB2");
        CommonMenuCtr hdfsSourceCtr = dataSourceCtr.findByName("Hdfs");
        CommonMenuCtr hiveResourceCtr = dataSourceCtr.findByName("Hive");
        CommonMenuCtr hbaseSourceCtr = dataSourceCtr.findByName("Hbase");
        CommonMenuCtr impalaSourceCtr = dataSourceCtr.findByName("Impala");
        CommonMenuCtr kafkaSourceCtr = dataSourceCtr.findByName("Kafka");

        String str= (drce.getDataresourceType()) == null ? "其它" : drce.getDataresourceType();
        switch (str.toLowerCase()) {
            case "mysql":
                mysqlSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "oracle":
                oracleSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "sqlserver":
                sqlSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "db2":
                db2SourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "hdfs":
                hdfsSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "hive":
                hiveResourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "hbase":
                hbaseSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "impala":
                impalaSourceCtr.addChild(getDataresourceChild(drce));
                break;
            case "kafka":
                kafkaSourceCtr.addChild(getDataresourceChild(drce));
                break;
            default:
//                otherSourceCtr.addChild(getDataresourceChild(drce));
        }
    }

    public void addPreprocessItem(StepInfoEntity sie) {
        this.preprocessCtr.addChild(getStepChild(sie));
    }

    public void addAlgorithmItem(StepInfoEntity sie) {
        this.algorithmCtr.addChild(getStepChild(sie));
    }

    public void addOutputItem(StepInfoEntity sie) {
        this.outputCtr.addChild(getStepChild(sie));
    }

    private Map<String, String> getDataresourceChild(DataresourceConfEntity drce) {
        Map<String, String> map = new HashMap<>();
        map.put("showName", drce.getDataresourceName());
        map.put("dataresourceId", drce.getDataresourceId());
        map.put("dataresourceName", drce.getDataresourceName());
        map.put("dataresourceDesc", drce.getDataresourceDesc());
        map.put("dataresourceType", drce.getDataresourceType());
        map.put("output", "out_1");
        return map;
    }

    private Map<String, String> getStepChild(StepInfoEntity sie) {
        Map<String, String> map = new HashMap<>();
        map.put("showName", sie.getName());
        map.put("stepId", sie.getStepId() + "");
        map.put("name", sie.getName());
        map.put("stepType", sie.getStepType());
        map.put("className", sie.getClassName());
        map.put("description", sie.getDescription());
        map.put("input", sie.getInput());
        map.put("output", sie.getOutput());
        return map;
    }
}
