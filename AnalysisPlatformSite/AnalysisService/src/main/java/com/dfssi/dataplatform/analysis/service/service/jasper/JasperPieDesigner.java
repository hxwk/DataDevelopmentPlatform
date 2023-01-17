package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.JasperChartType;
import com.dfssi.dataplatform.analysis.service.entity.jasper.PieField;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import net.sf.jasperreports.charts.base.JRBasePiePlot;
import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.apache.commons.lang.StringUtils;

import java.awt.*;


/**
 * @Description 自定义饼状图接口实现
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class JasperPieDesigner extends AbstractJasperReportDesigner {
    /**
     * 创建饼图报表
     *
     * @param voJasperParam 饼图参数实体
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

        PieField fields = voJasperParam.getPieFields();
        //定义fields
        JRDesignField keyField = new JRDesignField();
        keyField.setName(fields.getKeyField());
        keyField.setValueClass(String.class);
        jasperDesign.addField(keyField);

        JRDesignField valueField = new JRDesignField();
        valueField.setName(fields.getValueField());
        valueField.setValueClass(Integer.class);
        jasperDesign.addField(valueField);

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
        //定义pie Dateset
        JRDesignPieDataset jrDesignPieDataset = new JRDesignPieDataset(null);
        jrDesignPieDataset.setIncrementType(IncrementTypeEnum.NONE);
        jrDesignPieDataset.setResetType(ResetTypeEnum.REPORT);
        //定义categoryDateset的dataset

        JRDesignPieSeries jrDesignPieSeries = new JRDesignPieSeries();
        JRDesignExpression keyExpression = new JRDesignExpression();
        keyExpression.setText("$F{" + fields.getKeyField() + "}");
        //keyExpression.setValueClass(String.class);
        jrDesignPieSeries.setKeyExpression(keyExpression);

        JRDesignExpression valueExpression = new JRDesignExpression();
        valueExpression.setText("$F{" + fields.getValueField() + "}");
        //valueExpression.setValueClass(Integer.class);
        jrDesignPieSeries.setValueExpression(valueExpression);

        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText("String.valueOf($F{" + fields.getValueField() + "})");
        //labelExpression.setValueClass(String.class);
        jrDesignPieSeries.setLabelExpression(labelExpression);

        jrDesignPieDataset.addPieSeries(jrDesignPieSeries);

        jrDesignChart.setDataset(jrDesignPieDataset);
        //jrDesignChart.setBackcolor(Color.RED);
        JRBasePiePlot plot = (JRBasePiePlot) jrDesignChart.getPlot();
        plot.setShowLabels(true);
        plot.setOrientation(PlotOrientationEnum.VERTICAL);
        jrBand.addElement((JRElement) jrDesignChart);
        jasperDesign.setSummary(jrBand);
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
        return JRChart.CHART_TYPE_PIE;
    }
}
