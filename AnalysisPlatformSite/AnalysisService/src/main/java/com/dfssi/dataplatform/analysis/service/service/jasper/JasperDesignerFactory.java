package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.JasperChartType;
import net.sf.jasperreports.engine.JRChart;

/**
 * @Description 创建jasperReport设计器
 * @Author zhangcheng
 * @Date 2018/9/28 15:41
 */
public class JasperDesignerFactory {
    /**
     *  根据类型，创建相应报表设计器
     * @param chartType  报表类型
     * @return            对应的报表设计器
     */
    public static synchronized  AbstractJasperReportDesigner  createJasperDesigner(String chartType){
        AbstractJasperReportDesigner  designer = null;
        Byte type = JasperChartType.findJasperType(chartType);
        switch (type){
            case JRChart.CHART_TYPE_PIE:
            case JRChart.CHART_TYPE_PIE3D:
                designer = new JasperPieDesigner();
                break;
            case JRChart.CHART_TYPE_BAR :
            case JRChart.CHART_TYPE_BAR3D:
            case JRChart.CHART_TYPE_STACKEDBAR:
            case JRChart.CHART_TYPE_STACKEDBAR3D:
                designer = new JasperBarDesigner();
                break;
            case JRChart.CHART_TYPE_XYBAR:
                designer = new JasperXyBarDesigner();
                break;
            case JRChart.CHART_TYPE_LINE:
                designer = new JasperLineDesigner();
                break;
            case JRChart.CHART_TYPE_XYLINE:
                designer = new JasperXyLineDesigner();
                break;
            case JRChart.CHART_TYPE_AREA:
            case JRChart.CHART_TYPE_STACKEDAREA:
                designer = new JasperAreaDesigner();
                break;
            case JRChart.CHART_TYPE_XYAREA:
                designer = new JasperXyAreaDesigner();
                break;
            case JRChart.CHART_TYPE_BUBBLE:
                designer = new JasperBubbleDesigner();
                break;
            case JRChart.CHART_TYPE_TIMESERIES:
                designer = new JasperTimeSeriesDesigner();
                break;
            case JRChart.CHART_TYPE_SCATTER:
                designer = new JasperScatterDesigner();
                break;
            case JRChart.CHART_TYPE_GANTT:
                designer = new JasperGanttDesigner();
                break;
            case JRChart.CHART_TYPE_METER:
                designer = new JasperMeterDesigner();
                break;
        }
        return designer;
    }

}
