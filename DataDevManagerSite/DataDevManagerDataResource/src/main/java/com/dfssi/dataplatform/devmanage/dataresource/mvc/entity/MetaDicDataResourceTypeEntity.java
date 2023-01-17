package com.dfssi.dataplatform.devmanage.dataresource.mvc.entity;

import org.apache.ibatis.type.Alias;

/**
 * Created by Hannibal on 2018-01-11.
 */
@Alias("metaDicDataResourceTypeEntity")
public class MetaDicDataResourceTypeEntity {

    private Integer dbType;

    private String dbName;

    private String descritpion;

    public Integer getDbType() {
        return dbType;
    }

    public void setDbType(Integer dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    @Override
    public String toString() {
        return "MetaDicDataResourceTypeEntity{" +
                "dbType=" + dbType +
                ", dbName='" + dbName + '\'' +
                ", descritpion='" + descritpion + '\'' +
                '}';
    }
}
