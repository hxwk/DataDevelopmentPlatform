package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description  饼图数据指标
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class PieField {
    /**
     * pie chart key
     */
    @ApiModelProperty("饼图的key在数据库中对应的字符串类型字段")
    private String keyField;
    /**
     * pie chart value
     */
    @ApiModelProperty("饼图的value在数据库中对应的数值类型字段")
    private String valueField;

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
