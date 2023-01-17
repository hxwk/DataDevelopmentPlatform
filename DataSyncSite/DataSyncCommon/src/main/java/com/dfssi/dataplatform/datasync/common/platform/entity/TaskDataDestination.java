package com.dfssi.dataplatform.datasync.common.platform.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by HSF on 2017/12/4.
 * 数据资源目的地定义
 */
public class TaskDataDestination {
    private String destId; //数据目的地id
    private String type; //数据资源类型，FQCN全路径类名——sink的全路径类名
    private List<String> columns; //字段名称
    private Map<String,String> config; //配置信息
    private Map<Integer,List<Integer>> mapping; //字段映射


    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
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
}