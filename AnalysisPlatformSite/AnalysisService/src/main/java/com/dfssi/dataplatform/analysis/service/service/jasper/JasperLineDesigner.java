package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.BarField;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.JRCategoryPlot;
import net.sf.jasperreports.charts.design.JRDesignCategoryDataset;
import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;

import java.awt.*;

/**
 * @Description 自定义折线图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperLineDesigner extends AbstractJasperReportDesigner {

    /**
     * 创建折线图报表
     *
     * @param voJasperParam 折线图参数实体
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

        BarField fields = voJasperParam.getBarFields();
        //定义fields
        JRDesignField seriesField = new JRDesignField();
        seriesField.setName(fields.getSeriesField());
        seriesField.setValueClass(String.class);
        jasperDesign.addField(seriesField);

        JRDesignField categoryField = new JRDesignField();
        categoryField.setName(fields.getCategoryField());
        categoryField.setValueClass(Integer.class);
        jasperDesign.addField(categoryField);

        JRDesignField valuefield = new JRDesignField();
        valuefield.setName(fields.getValueField());
        valuefield.setValueClass(Integer.class);
        jasperDesign.addField(valuefield);

        //定义查询语句
        JRDesignQuery jrDesignQuery = new JRDesignQuery();
        jrDesignQuery.setLanguage("sql");
        jrDesignQuery.setText(voJasperParam.getQuerySql());
        jasperDesign.setQuery(jrDesignQuery);

        //定义band
        JRDesignBand jrBand = new JRDesignBand();
        jrBand.setHeight(voJasperParam.getHeight());
        jrBand.setSplitType(SplitTypeEnum.STRETCH);

        //定义bar chart
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_LINE);
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
        JRDesignCategoryDataset jrDesignCategoryDataset = new JRDesignCategoryDataset(null);
        jrDesignCategoryDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignCategoryDataset.setResetType(ResetTypeEnum.REPORT);
        //定义categoryDateset的dataset
        JRDesignCategorySeries jrDesignCategorySeries = new JRDesignCategorySeries();

        JRDesignExpression seriesExpression = new JRDesignExpression();
        seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        jrDesignCategorySeries.setSeriesExpression(seriesExpression);

        JRDesignExpression categoryExpression = new JRDesignExpression();
        categoryExpression.setText("$F{" + fields.getCategoryField() + "}");
        jrDesignCategorySeries.setCategoryExpression(categoryExpression);

        JRDesignExpression valueExpression = new JRDesignExpression();
        valueExpression.setText("$F{" + fields.getValueField() + "}");
        jrDesignCategorySeries.setValueExpression(valueExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getSeriesField() + "})");
        jrDesignCategorySeries.setLabelExpression(labelExpression);

        jrDesignCategoryDataset.addCategorySeries(jrDesignCategorySeries);

        jrDesignChart.setDataset(jrDesignCategoryDataset);
        JRCategoryPlot plot = (JRCategoryPlot) jrDesignChart.getPlot();
        plot.setOrientation(PlotOrientationEnum.VERTICAL);
        jrBand.addElement((JRElement) jrDesignChart);
        return JasperCompileManager.compileReport(jasperDesign);
    }

}
