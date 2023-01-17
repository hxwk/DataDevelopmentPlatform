package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import com.dfssi.dataplatform.analysis.service.entity.jasper.XyField;
import net.sf.jasperreports.charts.design.JRDesignXyzDataset;
import net.sf.jasperreports.charts.design.JRDesignXyzSeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;

import java.awt.*;

/**
 * @Description 自定义泡状图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperBubbleDesigner extends AbstractJasperReportDesigner {

    /**
     * 创建泡状图报表
     *
     * @param voJasperParam 泡状图参数实体
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
        //定义fields
        JRDesignField seriesField = new JRDesignField();
        seriesField.setName(fields.getSeriesField());
        seriesField.setValueClass(String.class);
        jasperDesign.addField(seriesField);

        JRDesignField xValueField = new JRDesignField();
        xValueField.setName(fields.getxValueField());
        xValueField.setValueClass(Integer.class);
        jasperDesign.addField(xValueField);

        JRDesignField yValuefield = new JRDesignField();
        yValuefield.setName(fields.getyValueField());
        yValuefield.setValueClass(Integer.class);
        jasperDesign.addField(yValuefield);

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
        JRDesignChart jrDesignChart = new JRDesignChart(jasperDesign, JRChart.CHART_TYPE_BUBBLE);
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
        JRDesignXyzDataset jrDesignXyzDataset = new JRDesignXyzDataset(null);
        jrDesignXyzDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignXyzDataset.setResetType(ResetTypeEnum.REPORT);
        //定义categoryDateset的dataset
        JRDesignXyzSeries jrDesignXyzSeries = new JRDesignXyzSeries();

        JRDesignExpression seriesExpression = new JRDesignExpression();
        seriesExpression.setText("$F{" + fields.getSeriesField() + "}");
        jrDesignXyzSeries.setSeriesExpression(seriesExpression);

        JRDesignExpression xValueExpression = new JRDesignExpression();
        xValueExpression.setText("$F{" + fields.getxValueField() + "}");
        jrDesignXyzSeries.setXValueExpression(xValueExpression);

        JRDesignExpression yValueExpression = new JRDesignExpression();
        yValueExpression.setText("$F{" + fields.getyValueField() + "}");
        jrDesignXyzSeries.setYValueExpression(yValueExpression);

        JRDesignExpression zValueExpression = new JRDesignExpression();
        zValueExpression.setText("$F{" + fields.getyValueField() + "}/$F{" + fields.getxValueField() + "}");
        jrDesignXyzSeries.setZValueExpression(zValueExpression);

        jrDesignXyzDataset.addXyzSeries(jrDesignXyzSeries);

        jrDesignChart.setDataset(jrDesignXyzDataset);
        jrBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrBand);
        return JasperCompileManager.compileReport(jasperDesign);
    }
}
