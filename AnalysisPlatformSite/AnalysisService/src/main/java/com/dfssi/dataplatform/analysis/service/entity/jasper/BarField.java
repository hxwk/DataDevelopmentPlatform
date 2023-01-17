package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description  柱状图数据指标
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class BarField {
    /**
     * bar series 引用变量
     */
    @ApiModelProperty(value = "柱形图的series在数据库中对应的字符串类型字段",example = "account")
    private String seriesField;
    /**
     * category 引用变量
     */
    @ApiModelProperty("柱形图的category在数据库中对应的字符串类型字段")
    private String categoryField;
    /**
     * 值 引用变量
     */
    @ApiModelProperty("柱形图的value在数据库中对应的数值类型字段")
    private String valueField;
    /**
     * 标签 引用变量
     */
    @ApiModelProperty("柱形图的标签，可忽略")
    private String labelField;

    public String getSeriesField() {
        return seriesField;
    }

    public void setSeriesField(String seriesField) {
        this.seriesField = seriesField;
    }

    public String getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(String categoryField) {
        this.categoryField = categoryField;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getLabelField() {
        return labelField;
    }

    public void setLabelField(String labelField) {
        this.labelField = labelField;
    }
}
