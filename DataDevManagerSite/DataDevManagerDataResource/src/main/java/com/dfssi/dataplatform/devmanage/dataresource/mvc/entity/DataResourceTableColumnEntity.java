package com.dfssi.dataplatform.devmanage.dataresource.mvc.entity;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseVO;
import org.apache.ibatis.type.Alias;

/**
 */
@Alias("dataResourceTableColumnEntity")
public class DataResourceTableColumnEntity extends BaseVO {
    private String dataresTableColumnId;
    private String dataresTableColumnName;
    private String dataresId;
    private Integer dataresTableColumnType;


    public String getDataresTableColumnId() {
        return dataresTableColumnId;
    }

    public void setDataresTableColumnId(String dataresTableColumnId) {
        this.dataresTableColumnId = dataresTableColumnId;
    }

    public String getDataresTableColumnName() {
        return dataresTableColumnName;
    }

    public void setDataresTableColumnName(String dataresTableColumnName) {
        this.dataresTableColumnName = dataresTableColumnName;
    }

    public Integer getDataresTableColumnType() {
        return dataresTableColumnType;
    }

    public void setDataresTableColumnType(Integer dataresTableColumnType) {
        this.dataresTableColumnType = dataresTableColumnType;
    }

    public String getDataresId() {
        return dataresId;
    }

    public void setDataresId(String dataresId) {
        this.dataresId = dataresId;
    }

    @Override
    public String toString() {
        return "DataResourceTableColumnEntity{" +
                "dataresTableColumnId='" + dataresTableColumnId + '\'' +
                ", dataresTableColumnName='" + dataresTableColumnName + '\'' +
                ", dataresId='" + dataresId + '\'' +
                ", dataresTableColumnType='" + dataresTableColumnType + '\'' +
                '}';
    }
}
