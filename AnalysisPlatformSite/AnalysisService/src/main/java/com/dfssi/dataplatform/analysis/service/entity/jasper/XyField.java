package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description  折线图、散点图、泡状数据指标
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class XyField {
    @ApiModelProperty("XY类型图的series在数据库中对应的字符串类型字段")
    private String seriesField;
    @ApiModelProperty("XY类型图的xValue在数据库中对应的整数类型字段")
    private String xValueField;
    @ApiModelProperty("XY类型图的yValue在数据库中对应的double类型字段")
    private String yValueField;

    public String getSeriesField() {
        return seriesField;
    }

    public void setSeriesField(String seriesField) {
        this.seriesField = seriesField;
    }

    public String getxValueField() {
        return xValueField;
    }

    public void setxValueField(String xValueField) {
        this.xValueField = xValueField;
    }

    public String getyValueField() {
        return yValueField;
    }

    public void setyValueField(String yValueField) {
        this.yValueField = yValueField;
    }
}
