package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 甘特图数据指标
 * @Author zhangcheng
 * @Date 2018/9/27 14:08
 **/
public class GanttField {
    /**
     * 序列
     */
    @ApiModelProperty("甘特图的series在数据库中对应的字符串类型字段")
    private String seriesField;
    /**
     * 开始时间
     */
    @ApiModelProperty(value="甘特图的startDate在数据库中对应的时间类型字段",example = "create_time")
    private String startDateField;
    /**
     * 结束时间
     */
    @ApiModelProperty(value="甘特图的endDate在数据库中对应的时间类型字段",example = "create_time")
    private String endDateField;
    /**
     * 任务
     */
    @ApiModelProperty(value="甘特图的task在数据库中对应的字符串类型字段")
    private String taskField;
    /**
     * 子任务
     */
    @ApiModelProperty(value="甘特图的subTask在数据库中对应的字符串类型字段")
    private String subTaskField;
    /**
     * 百分比
     */
    @ApiModelProperty(value="甘特图的percent在数据库中对应的double类型字段")
    private String percentField;

    public String getSeriesField() {
        return seriesField;
    }

    public void setSeriesField(String seriesField) {
        this.seriesField = seriesField;
    }

    public String getStartDateField() {
        return startDateField;
    }

    public void setStartDateField(String startDateField) {
        this.startDateField = startDateField;
    }

    public String getEndDateField() {
        return endDateField;
    }

    public void setEndDateField(String endDateField) {
        this.endDateField = endDateField;
    }

    public String getTaskField() {
        return taskField;
    }

    public void setTaskField(String taskField) {
        this.taskField = taskField;
    }

    public String getSubTaskField() {
        return subTaskField;
    }

    public void setSubTaskField(String subTaskField) {
        this.subTaskField = subTaskField;
    }

    public String getPercentField() {
        return percentField;
    }

    public void setPercentField(String percentField) {
        this.percentField = percentField;
    }
}
