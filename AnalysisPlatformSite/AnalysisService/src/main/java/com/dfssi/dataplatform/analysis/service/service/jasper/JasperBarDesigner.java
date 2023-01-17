package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.BarField;
import com.dfssi.dataplatform.analysis.service.entity.jasper.JasperChartType;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.design.JRDesignCategoryDataset;
import net.sf.jasperreports.charts.design.JRDesignCategoryPlot;
import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.apache.commons.lang.StringUtils;

import java.awt.*;

/**
 * @Description 自定义柱状图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperBarDesigner extends AbstractJasperReportDesigner {
    /**
     * 创建柱状图报表
     *
     * @param voJasperParam 柱状图参数实体
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
        //分单条柱子和多条柱子的情况，单条柱子时seriesExpress为不变量，多条柱子时，seriesExpress为变量
        if (org.apache.commons.lang.StringUtils.isNotBlank(fields.getSeriesField())) {
            JRDesignField seriesField = new JRDesignField();
            seriesField.setName(fields.getSeriesField());
            seriesField.setValueClass(String.class);
            jasperDesign.addField(seriesField);
        }

        JRDesignField categoryField = new JRDesignField();
        categoryField.setName(fields.getCategoryField());
        categoryField.setValueClass(String.class);
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
        JRDesignBand jrDesignBand = new JRDesignBand();
        jrDesignBand.setHeight(voJasperParam.getHeight());
        jrDesignBand.setSplitType(SplitTypeEnum.STRETCH);

        //定义bar chart
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, getChartType(voJasperParam.getChartType()));
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
        if (org.apache.commons.lang.StringUtils.isNotBlank(fields.getSeriesField())) {
            seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        } else {
            seriesExpression.setText("$P{xLabel}");
        }
        jrDesignCategorySeries.setSeriesExpression(seriesExpression);

        JRDesignExpression categoryExpression = new JRDesignExpression();
        categoryExpression.setText("$F{" + fields.getCategoryField() + "}");
        jrDesignCategorySeries.setCategoryExpression(categoryExpression);

        JRDesignExpression valueExpression = new JRDesignExpression();
        valueExpression.setText("$F{" + fields.getValueField() + "}");
        jrDesignCategorySeries.setValueExpression(valueExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getCategoryField() + "})");
        jrDesignCategorySeries.setLabelExpression(labelExpression);

        jrDesignCategoryDataset.addCategorySeries(jrDesignCategorySeries);

        jrDesignChart.setDataset(jrDesignCategoryDataset);
        JRChartPlot plot = jrDesignChart.getPlot();
        if(plot instanceof JRDesignCategoryPlot){
            JRDesignCategoryPlot categoryPlot = (JRDesignCategoryPlot) plot;
            categoryPlot.setCategoryAxisLabelExpression(createJRExpression("$P{xLabel}"));
            categoryPlot.setValueAxisLabelExpression(createJRExpression("$P{yLabel}"));
        }
        jrDesignBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrDesignBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    /**
     * 获取需要创建图表类型
     * @param chartType   类型参数
     * @return             需要创建的jasper图表类型
     */
    private Byte getChartType(String chartType) {
        if (StringUtils.isNotBlank(chartType)) {
            return JasperChartType.findJasperType(chartType);
        }
        return JRChart.CHART_TYPE_BAR3D;
    }

}
