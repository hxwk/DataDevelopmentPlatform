package com.dfssi.dataplatform.datasync.common.platform.entity;


import java.util.List;
import java.util.Map;

/**
 * Created by HSF on 2017/12/4.
 * 数据清洗转换
 * update by JianKang on 2018/05/04
 *
 * "cleanTranRule":{
 "0":{
 "numberhandle":{
 "factor":"0.1",
 "offset":"1.0"
 }
 },
 "1":{
 "searchreplace":{
 "searchPattern":"^prefix",
 "replaceString":""
 }
 },
 "2":{
 "numberhandle":{
 "factor":1,
 "offset":-40
 }
 },
 "3":{
 "maskhandler":{
 "maskStart":"3",
 "maskEnd":"8",
 "insertString":"hello"
 }
 }
 }
 *
 **/
public class TaskDataCleanTransform {
    private String cleanTransId;  //清洗转换id
    private String type; //清洗转换类型，FQCN全路径类名——interceptor全路径类名
    private List<String> columns; //字段名称
    private Map<String,String> config; //配置信息
    private Map<Integer,List<Integer>> mapping; //字段映射
    private Map<String/*字段顺序*/,Map<String/*清洗转换名称*/,MapperRule/*映射规则*/>> cleanTranRule;
    //字段映射规则 LinkedHashMap,保证插入顺序

    public String getCleanTransId() {
        return cleanTransId;
    }

    public void setCleanTransId(String cleanTransId) {
        this.cleanTransId = cleanTransId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public Map<Integer, List<Integer>> getMapping() {
        return mapping;
    }

    public void setMapping(Map<Integer, List<Integer>> mapping) {
        this.mapping = mapping;
    }

    public Map<String, Map<String, MapperRule>> getCleanTranRule() {
        return cleanTranRule;
    }

    public void setCleanTranRule(Map<String, Map<String, MapperRule>> cleanTranRule) {
        this.cleanTranRule = cleanTranRule;
    }
}
