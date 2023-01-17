package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.TimeSeriesField;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.design.JRDesignTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesPlot;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.jfree.chart.plot.XYPlot;

import java.awt.*;

/**
 * @Description 时间序列图接口实现
 * @Author zhangcheng
 * @Date 2018/9/25 18:43
 **/
public class JasperTimeSeriesDesigner extends AbstractJasperReportDesigner {
    /**
     * 创建曲线面积图
     *
     * @param voJasperParam 时间序列图参数实体
     * @return jasperreport(编译后的报表)
     * @throws Exception 异常交由上层处理
     */
    @Override
    public JasperReport createJapserReport(VoJasperParam voJasperParam) throws JRException {
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName(voJasperParam.getTitle());
        jasperDesign.setPageWidth(voJasperParam.getPageWidth());
        jasperDesign.setPageHeight(voJasperParam.getPageHeight());
        jasperDesign.setLanguage("java");

        TimeSeriesField fields = voJasperParam.getTimeSeriesFields();

        //定义x轴标题引用
        JRDesignParameter xLabel = new JRDesignParameter();
        xLabel.setName("xLabel");
        xLabel.setValueClass(String.class);
        jasperDesign.addParameter(xLabel);

        //定义y轴标题引用
        JRDesignParameter yLabel = new JRDesignParameter();
        yLabel.setName("yLabel");
        yLabel.setValueClass(String.class);
        jasperDesign.addParameter(yLabel);

        //定义fields
        JRDesignField seriesField = new JRDesignField();
        seriesField.setName(fields.getSeriesField());
        seriesField.setValueClass(String.class);
        jasperDesign.addField(seriesField);

        JRDesignField timePeriodField = new JRDesignField();
        timePeriodField.setName(fields.getTimePeriodField());
        timePeriodField.setValueClass(java.sql.Date.class);
        jasperDesign.addField(timePeriodField);

        JRDesignField valuefield = new JRDesignField();
        valuefield.setName(fields.getValueField());
        valuefield.setValueClass(Double.class);
        jasperDesign.addField(valuefield);

        //定义查询语句
        JRDesignQuery jrDesignQuery = new JRDesignQuery();
        jrDesignQuery.setLanguage("sql");
        jrDesignQuery.setText(voJasperParam.getQuerySql());
        jasperDesign.setQuery(jrDesignQuery);

        //定义band
        JRDesignBand jrDesignBand = new JRDesignBand();
        jrDesignBand.setHeight(voJasperParam.getHeight());
        jrDesignBand.setSplitType(SplitTypeEnum.STRETCH);

        //定义bar chart
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_TIMESERIES);
        JRDesignExpression titleExpression = new JRDesignExpression();
        titleExpression.setText("String.valueOf(\"" + voJasperParam.getTitle() + "\")");
        jrDesignChart.setTitleExpression(titleExpression);
        jrDesignChart.setWidth(voJasperParam.getWidth());
        jrDesignChart.setHeight(voJasperParam.getHeight());
        jrDesignChart.setX(voJasperParam.getX());
        jrDesignChart.setY(voJasperParam.getY());
        jrDesignChart.setMode(ModeEnum.OPAQUE);
        jrDesignChart.setLegendPosition(EdgeEnum.getByName(voJasperParam.getLegendPosition()));
        jrDesignChart.setLegendColor(Color.BLACK);
        jrDesignChart.setShowLegend(true);
        jrDesignChart.setEvaluationTime(EvaluationTimeEnum.REPORT);
        jrDesignChart.setCustomizerClass(getCustomizer());
        //定义categoryDateset
        JRDesignTimeSeriesDataset jrDesignTimeSeriesDataset = new JRDesignTimeSeriesDataset(null);
        jrDesignTimeSeriesDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignTimeSeriesDataset.setResetType(ResetTypeEnum.REPORT);
        jrDesignTimeSeriesDataset.setTimePeriod(org.jfree.data.time.Quarter.class);


        //定义categoryDateset的dataset
        JRDesignTimeSeries jrDesignTimeSeries = new JRDesignTimeSeries();

        JRDesignExpression seriesExpression = new JRDesignExpression();
        if (org.apache.commons.lang.StringUtils.isNotBlank(fields.getSeriesField())) {
            seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        } else {
            seriesExpression.setText("$P{xLabel}");
        }
        jrDesignTimeSeries.setSeriesExpression(seriesExpression);

        JRDesignExpression timePeriodExpression = new JRDesignExpression();
        timePeriodExpression.setText("$F{"+fields.getTimePeriodField()+"}");
        jrDesignTimeSeries.setTimePeriodExpression(timePeriodExpression);

        JRDesignExpression valueExpression = new JRDesignExpression();
        valueExpression.setText("$F{" + fields.getValueField() + "}");
        jrDesignTimeSeries.setValueExpression(valueExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getValueField() + "})");
        jrDesignTimeSeries.setLabelExpression(labelExpression);

        jrDesignTimeSeriesDataset.addTimeSeries(jrDesignTimeSeries);

        jrDesignChart.setDataset(jrDesignTimeSeriesDataset);
        JRDesignTimeSeriesPlot jrDesignTimeSeriesPlot = (JRDesignTimeSeriesPlot) jrDesignChart.getPlot();

        jrDesignTimeSeriesPlot.setTimeAxisLabelExpression(createJRExpression("$P{xLabel}"));
        jrDesignTimeSeriesPlot.setValueAxisLabelExpression(createJRExpression("$P{yLabel}"));

        jrDesignBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrDesignBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }
}
