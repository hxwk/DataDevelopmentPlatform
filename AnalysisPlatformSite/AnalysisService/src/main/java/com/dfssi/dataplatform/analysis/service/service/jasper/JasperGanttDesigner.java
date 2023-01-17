package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.GanttField;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.design.JRDesignGanttDataset;
import net.sf.jasperreports.charts.design.JRDesignGanttSeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.Date;

/**
 * @Description 甘特图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperGanttDesigner extends AbstractJasperReportDesigner {
    /**
     * 甘特图报表
     *
     * @param voJasperParam 甘特图参数实体
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

        GanttField fields = voJasperParam.getGanttFields();

        //定义fields
        //分单条柱子和多条柱子的情况，单条柱子时seriesExpress为不变量，多条柱子时，seriesExpress为变量
        if (StringUtils.isNotBlank(fields.getSeriesField())) {
            JRDesignField seriesField = new JRDesignField();
            seriesField.setName(fields.getSeriesField());
            seriesField.setValueClass(String.class);
            jasperDesign.addField(seriesField);
        }

        JRDesignField startDateField = new JRDesignField();
        startDateField.setName(fields.getStartDateField());
        startDateField.setValueClass(Date.class);
        jasperDesign.addField(startDateField);

        JRDesignField endDateField = new JRDesignField();
        endDateField.setName(fields.getEndDateField());
        endDateField.setValueClass(Date.class);
        jasperDesign.addField(endDateField);

        JRDesignField taskField = new JRDesignField();
        taskField.setName(fields.getTaskField());
        taskField.setValueClass(String.class);
        jasperDesign.addField(taskField);

        JRDesignField subTaskField = new JRDesignField();
        subTaskField.setName(fields.getSubTaskField());
        subTaskField.setValueClass(String.class);
        jasperDesign.addField(subTaskField);

        JRDesignField percentField = new JRDesignField();
        percentField.setName(fields.getPercentField());
        percentField.setValueClass(Double.class);
        jasperDesign.addField(percentField);

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
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_GANTT);
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
        //jrDesignChart.setCustomizerClass(jasperDesignCommonService.getCustomizer());

        JRDesignGanttDataset jrDesignGanttDataset = new JRDesignGanttDataset(null);
        jrDesignGanttDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignGanttDataset.setResetType(ResetTypeEnum.REPORT);

        JRDesignGanttSeries jrDesignGanttSeries = new JRDesignGanttSeries();

        JRDesignExpression seriesExpression = new JRDesignExpression();
        seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        jrDesignGanttSeries.setSeriesExpression(seriesExpression);

        JRDesignExpression startDateExpression = new JRDesignExpression();
        startDateExpression.setText("$F{" + fields.getStartDateField() + "}");
        jrDesignGanttSeries.setStartDateExpression(startDateExpression);

        JRDesignExpression endDateExpression = new JRDesignExpression();
        endDateExpression.setText("$F{" + fields.getEndDateField() + "}");
        jrDesignGanttSeries.setEndDateExpression(endDateExpression);

        JRDesignExpression taskExpression = new JRDesignExpression();
        taskExpression.setText("$F{" + fields.getTaskField() + "}");
        jrDesignGanttSeries.setTaskExpression(taskExpression);

        JRDesignExpression subTaskExpression = new JRDesignExpression();
        subTaskExpression.setText("$F{" + fields.getSubTaskField() + "}");
        jrDesignGanttSeries.setSubtaskExpression(subTaskExpression);

        JRDesignExpression percentExpression = new JRDesignExpression();
        percentExpression.setText("$F{" + fields.getPercentField() + "}");
        jrDesignGanttSeries.setPercentExpression(percentExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getTaskField() + "})");
        jrDesignGanttSeries.setLabelExpression(labelExpression);

        jrDesignGanttDataset.addGanttSeries(jrDesignGanttSeries);
        jrDesignChart.setDataset(jrDesignGanttDataset);

        jrDesignBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrDesignBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }
}
