package com.dfssi.dataplatform.datasync.common.platform.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by HSF on 2017/12/4.
 * 数据源定义
 */
public class TaskDataSource {
    private String srcId; //数据源id
    private String type; //数据源类型，FQCN全路径类名——source的全路径类名
    private List<String> columns; //字段名称
    private Map<String,String> config; //配置信息


    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
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
}
