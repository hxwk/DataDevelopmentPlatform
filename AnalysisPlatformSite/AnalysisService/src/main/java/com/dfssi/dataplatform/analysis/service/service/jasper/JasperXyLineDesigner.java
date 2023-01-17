package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import com.dfssi.dataplatform.analysis.service.entity.jasper.XyField;
import net.sf.jasperreports.charts.design.JRDesignXyDataset;
import net.sf.jasperreports.charts.design.JRDesignXySeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.apache.commons.lang.StringUtils;

import java.awt.*;

/**
 * @Description 自定义XY折线图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperXyLineDesigner extends AbstractJasperReportDesigner {
    /**
     * 创建XY折线图报表
     *
     * @param voJasperParam XY折线图参数实体
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

        XyField fields = voJasperParam.getXyFields();

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

        JRDesignField xField = new JRDesignField();
        xField.setName(fields.getxValueField());
        xField.setValueClass(Integer.class);
        jasperDesign.addField(xField);

        JRDesignField yField = new JRDesignField();
        yField.setName(fields.getyValueField());
        yField.setValueClass(Double.class);
        jasperDesign.addField(yField);

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
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_XYLINE);
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
        JRDesignXyDataset jrDesignXyDataset = new JRDesignXyDataset(null);
        jrDesignXyDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignXyDataset.setResetType(ResetTypeEnum.REPORT);
        //定义categoryDateset的dataset
        JRDesignXySeries jrDesignXySeries = new JRDesignXySeries();

        JRDesignExpression seriesExpression = new JRDesignExpression();
        seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        jrDesignXySeries.setSeriesExpression(seriesExpression);

        JRDesignExpression xValueExpression = new JRDesignExpression();
        xValueExpression.setText("$F{" + fields.getxValueField() + "}");
        jrDesignXySeries.setXValueExpression(xValueExpression);

        JRDesignExpression yValueExpression = new JRDesignExpression();
        yValueExpression.setText("new Double($F{" + fields.getyValueField() + "})");
        jrDesignXySeries.setYValueExpression(yValueExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getyValueField() + "})");
        jrDesignXySeries.setLabelExpression(labelExpression);

        jrDesignXyDataset.addXySeries(jrDesignXySeries);

        jrDesignChart.setDataset(jrDesignXyDataset);
        //plot设计
        jrDesignBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrDesignBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }

}
