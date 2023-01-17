package com.dfssi.dataplatform.analysis.service.entity.jasper;

import net.sf.jasperreports.engine.JRChart;

/**
 * @Description 图表类型
 * @Author zhangcheng
 * @Date 2018/9/25 16:44
 **/
public enum JasperChartType {
    CHART_TYPE_AREA("area",JRChart.CHART_TYPE_AREA),
    CHART_TYPE_BAR3D("bar3D",JRChart.CHART_TYPE_BAR3D),
    CHART_TYPE_BAR("bar",JRChart.CHART_TYPE_BAR),
    CHART_TYPE_BUBBLE("bubble",JRChart.CHART_TYPE_BUBBLE),
    CHART_TYPE_CANDLESTICK("candlestack",JRChart.CHART_TYPE_CANDLESTICK),
    CHART_TYPE_HIGHLOW("highLow",JRChart.CHART_TYPE_HIGHLOW),
    CHART_TYPE_LINE("line",JRChart.CHART_TYPE_LINE),
    CHART_TYPE_PIE3D("pie3D",JRChart.CHART_TYPE_PIE3D),
    CHART_TYPE_PIE("pie",JRChart.CHART_TYPE_PIE),
    CHART_TYPE_SCATTER("scatter",JRChart.CHART_TYPE_SCATTER),
    CHART_TYPE_STACKEDBAR3D("stackedbar3D",JRChart.CHART_TYPE_STACKEDBAR3D),
    CHART_TYPE_STACKEDBAR("stackedbar",JRChart.CHART_TYPE_STACKEDBAR),
    CHART_TYPE_XYAREA("xyarea",JRChart.CHART_TYPE_XYAREA),
    CHART_TYPE_XYBAR("xybar",JRChart.CHART_TYPE_XYBAR),
    CHART_TYPE_XYLINE("xyline",JRChart.CHART_TYPE_XYLINE),
    CHART_TYPE_TIMESERIES("timeseries",JRChart.CHART_TYPE_TIMESERIES),
    CHART_TYPE_METER("meter",JRChart.CHART_TYPE_METER),
    CHART_TYPE_THERMOMETER("thermometer",JRChart.CHART_TYPE_THERMOMETER),
    CHART_TYPE_MULTI_AXIS("multi_axis",JRChart.CHART_TYPE_MULTI_AXIS),
    CHART_TYPE_STACKEDAREA("stacked_area",JRChart.CHART_TYPE_STACKEDAREA),
    CHART_TYPE_GANTT("gantt",JRChart.CHART_TYPE_GANTT),
    ;
    private String type;
    private Byte  jasperType;

    JasperChartType(String type, Byte jasperType) {
        this.type = type;
        this.jasperType = jasperType;
    }

    /**
     * 根据参数，查找其对应的jasper report的图表类型
     * @param type  类型参数
     * @return  jasper report图表类型
     */
    public static Byte  findJasperType(String type){
        for(JasperChartType item : values()){
            if(item.type.equals(type)){
                return item.jasperType;
            }
        }
        return null;
    }
}
