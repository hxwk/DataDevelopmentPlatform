package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 时序图数据指标
 * @Author zhangcheng
 * @Date 2018/9/25 18:49
 **/
public class TimeSeriesField{
    /**
     *  表示时间，相当于x变量
     */
    @ApiModelProperty("TimeSeries图的timePeriod在数据库中对应的Date类型字段")
    private String timePeriodField;
    /**
     * 表示series
     */
    @ApiModelProperty("TimeSeries图的series在数据库中对应的字符串类型字段")
    private String seriesField;
    /**
     * 表示值，相当于y变量
     */
    @ApiModelProperty("TimeSeries图的value在数据库中对应的Double类型字段")
    private String valueField;

    public String getTimePeriodField() {
        return timePeriodField;
    }

    public void setTimePeriodField(String timePeriodField) {
        this.timePeriodField = timePeriodField;
    }

    public String getSeriesField() {
        return seriesField;
    }

    public void setSeriesField(String seriesField) {
        this.seriesField = seriesField;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
