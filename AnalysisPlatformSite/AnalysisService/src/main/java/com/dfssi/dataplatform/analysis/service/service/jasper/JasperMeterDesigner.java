package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.design.JRDesignDataRange;
import net.sf.jasperreports.charts.design.JRDesignMeterPlot;
import net.sf.jasperreports.charts.design.JRDesignValueDataset;
import net.sf.jasperreports.charts.design.JRDesignValueDisplay;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Description 仪表盘接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperMeterDesigner extends AbstractJasperReportDesigner {
    /**
     * 创建仪表盘
     *
     * @param voJasperParam 仪表盘参数实体
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

        String valueField = voJasperParam.getValueField();

        JRDesignField valuefield = new JRDesignField();
        valuefield.setName(valueField);
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
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_METER);
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
        JRDesignValueDataset jrDesignValueDataset = new JRDesignValueDataset(null);
        jrDesignValueDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignValueDataset.setResetType(ResetTypeEnum.REPORT);

        JRDesignExpression valueExpression = new JRDesignExpression();
        valueExpression.setText("$F{" + valueField + "}");
        jrDesignValueDataset.setValueExpression(valueExpression);

        jrDesignChart.setDataset(jrDesignValueDataset);
        JRDesignMeterPlot meterPlot = (JRDesignMeterPlot)jrDesignChart.getPlot();
        meterPlot.setUnits("Freight");
        meterPlot.setTickInterval(new Double(1000.0));
        meterPlot.setTickColor(new Color(0,0,255));
        meterPlot.setNeedleColor(new Color(0,0,66));

        JRDesignValueDisplay jrDesignValueDisplay = new JRDesignValueDisplay(null, null);
        jrDesignValueDisplay.setColor(new Color(0,0,0));
        jrDesignValueDisplay.setFont(new JRBaseFont());
        meterPlot.setValueDisplay(jrDesignValueDisplay);

        //构建刻度范围
        JRDesignDataRange jrDesignDataRange = new JRDesignDataRange(null);
        jrDesignDataRange.setLowExpression(new JRDesignExpression(String.valueOf(new Double(0.0))));
        jrDesignDataRange.setHighExpression(new JRDesignExpression(String.valueOf(new Double(17500.0))));
        meterPlot.setDataRange(jrDesignDataRange);

        //刻度范围分块显示
        meterPlot.setIntervals(buildMeterInterval());

        jrDesignBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrDesignBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private Collection<JRMeterInterval> buildMeterInterval(){
        Collection<JRMeterInterval> intervals = new ArrayList<>();
        JRMeterInterval smallInterval = new JRMeterInterval();
        smallInterval.setLabel("small");
        //smallInterval.setAlpha(0.5);
        smallInterval.setBackgroundColor(new Color(255,6,6));
        JRDesignDataRange samllDataRange = new JRDesignDataRange(null);
        samllDataRange.setLowExpression(new JRDesignExpression(String.valueOf(new Double(0.0))));
        samllDataRange.setHighExpression(new JRDesignExpression(String.valueOf(new Double(2000.0))));
        smallInterval.setDataRange(samllDataRange);
        intervals.add(smallInterval);

        JRMeterInterval medMeterInterval = new JRMeterInterval();
        medMeterInterval.setLabel("med");
        medMeterInterval.setAlpha(new Double(0.4));
        medMeterInterval.setBackgroundColor(new Color(255,255,0));
        JRDesignDataRange medDataRange= new JRDesignDataRange(null);
        medDataRange.setLowExpression(new JRDesignExpression(String.valueOf(new Double(2000.0))));
        medDataRange.setHighExpression(new JRDesignExpression(String.valueOf(new Double(12000.0))));
        medMeterInterval.setDataRange(medDataRange);
        intervals.add(medMeterInterval);

        JRMeterInterval largeMeterInterval = new JRMeterInterval();
        largeMeterInterval.setLabel("large");
        largeMeterInterval.setAlpha(new Double(0.5));
        largeMeterInterval.setBackgroundColor(new Color(6,12,6));
        JRDesignDataRange largeDataRange= new JRDesignDataRange(null);
        largeDataRange.setLowExpression(new JRDesignExpression(String.valueOf(new Double(2000.0))));
        largeDataRange.setHighExpression(new JRDesignExpression(String.valueOf(new Double(12000.0))));
        largeMeterInterval.setDataRange(largeDataRange);
        intervals.add(largeMeterInterval);
        return intervals;
    }

}
