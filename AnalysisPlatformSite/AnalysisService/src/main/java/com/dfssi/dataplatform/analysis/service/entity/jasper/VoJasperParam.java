package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 报表参数
 * @Author zhangcheng
 * @Date 2018/9/28 13:26
 */
public class VoJasperParam extends VoBaseJasper{
    /**
     * 柱形图数据指标
     */
    @ApiModelProperty("柱形图数据指标，当chartType=bar*时，需要填充该对象")
    private BarField barFields;
    /**
     * 饼图数据指标
     */
    @ApiModelProperty("饼图数据指标，当chartType为pie*时，需要填充该对象")
    private PieField pieFields;
    /**
     *  XY类型图数据指标
     */
    @ApiModelProperty("XY图数据指标，当chartType为xy*类型时，需要填充该对象")
    private XyField xyFields;
    /**
     * gantt数据指标
     */
    @ApiModelProperty("gantt数据指标，当chartType为gantt类型时，需要填充该对象")
    private GanttField ganttFields;
    /**
     * 时间序列数据指标
     */
    @ApiModelProperty("时间序列数据指标，当chartType为timeseries类型时，需要填充该对象")
    private TimeSeriesField timeSeriesFields;
    /**
     * 里程表的数据指标
     */
    @ApiModelProperty("里程表中的value在数据库中对应的double类型字段")
    private String valueField;

    public BarField getBarFields() {
        return barFields;
    }

    public void setBarFields(BarField barFields) {
        this.barFields = barFields;
    }

    public PieField getPieFields() {
        return pieFields;
    }

    public void setPieFields(PieField pieFields) {
        this.pieFields = pieFields;
    }

    public XyField getXyFields() {
        return xyFields;
    }

    public void setXyFields(XyField xyFields) {
        this.xyFields = xyFields;
    }

    public GanttField getGanttFields() {
        return ganttFields;
    }

    public void setGanttFields(GanttField ganttFields) {
        this.ganttFields = ganttFields;
    }

    public TimeSeriesField getTimeSeriesFields() {
        return timeSeriesFields;
    }

    public void setTimeSeriesFields(TimeSeriesField timeSeriesFields) {
        this.timeSeriesFields = timeSeriesFields;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
