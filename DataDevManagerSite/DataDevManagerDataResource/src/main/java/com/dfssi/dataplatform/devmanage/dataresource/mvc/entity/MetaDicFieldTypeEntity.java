package com.dfssi.dataplatform.devmanage.dataresource.mvc.entity;

import org.apache.ibatis.type.Alias;

/**
 * Created by Hannibal on 2018-01-11.
 */
@Alias("metaDicFieldTypeEntity")
public class MetaDicFieldTypeEntity {

    private Integer fieldType;

    private String fieldName;

    private String descritpion;

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    @Override
    public String toString() {
        return "MetaDicFieldTypeEntity{" +
                "fieldtype=" + fieldType +
                ", fieldname='" + fieldName + '\'' +
                ", descritpion='" + descritpion + '\'' +
                '}';
    }
}
