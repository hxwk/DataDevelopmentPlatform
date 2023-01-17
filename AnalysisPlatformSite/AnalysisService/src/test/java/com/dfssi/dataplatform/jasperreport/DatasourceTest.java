package com.dfssi.dataplatform.jasperreport;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.analysis.service.ServiceApp;
import com.dfssi.dataplatform.analysis.service.entity.ChartPublishEntity;
import com.dfssi.dataplatform.analysis.service.entity.jasper.*;
import com.dfssi.dataplatform.analysis.service.service.ChartPublishService;
import com.dfssi.dataplatform.analysis.service.service.jasper.AbstractJasperReportDesigner;
import com.dfssi.dataplatform.analysis.service.service.jasper.JasperDesignerFactory;
import net.sf.jasperreports.engine.JRException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Description 数据源测试
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceApp.class)
public class DatasourceTest {


    public static String exportPath = "d:/reports/*.pdf";

    @Test
    public void BarTest() {
        VoJasperParam voJasperBar = getVoJasperBar();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperBar.getChartType());
        jasperDesigner.exportToPdf(getVoJasperBar(),
                exportPath.replace("*", "test_bar"));
    }

    @Test
    public void Bar3DTest() {
        VoJasperParam voJasperBar = getVoJasperBar();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperBar.getChartType());
        jasperDesigner.exportToPdf(getVoJasperBar(),
                exportPath.replace("*", "test_bar3D"));
    }

    @Test
    public void stackBarTest() {
        VoJasperParam voJasperBar = getVoJasperBar();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperBar.getChartType());
        jasperDesigner.exportToPdf(getVoJasperBar(),
                exportPath.replace("*", "test_stackbar3d"));
    }

    @Test
    public void pieTest() {
        VoJasperParam voJasperPie = getVoJasperPie();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperPie.getChartType());
        jasperDesigner.exportToPdf(getVoJasperPie(),
                exportPath.replace("*", "test_pie"));
    }

    @Test
    public void timePeriodTest() {
        VoJasperParam voJasperPie = getVoTimeSeries();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperPie.getChartType());
        jasperDesigner.exportToPdf(voJasperPie,
                exportPath.replace("*", "test_time_period"));
    }


    @Test
    public void xyBarTest() {
        VoJasperParam voJasperXyBar = getVoJasperXyBar();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperXyBar.getChartType());
        jasperDesigner.exportToPdf(voJasperXyBar,
                exportPath.replace("*", "test_xyBar"));
    }
    @Test
    public void xyLineTest() {
        VoJasperParam voJasperXy = getVoJasperXyLine();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperXy.getChartType());
        jasperDesigner.exportToPdf(voJasperXy,
                exportPath.replace("*", "test_xyLine"));
    }

    @Test
    public void ganttTest() {
        VoJasperParam voJasperGantt = getVoJasperGantt();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperGantt.getChartType());
        jasperDesigner.exportToPdf(getVoJasperGantt(), exportPath.replace("*", "test_gantt"));
    }

    @Test
    public void meterTest() {
        VoJasperParam voJasperMeter = getVoJasperMeter();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperMeter.getChartType());
        jasperDesigner.exportToPdf(getVoJasperMeter(), exportPath.replace("*",
                "test_meter"));
    }


    private VoJasperParam getVoJasperPie() {
        VoJasperParam voJasperPie = new VoJasperParam();
        voJasperPie.setDbType("hive");
        voJasperPie.setDbName("dev_analysis");
        voJasperPie.setPageHeight(800);
        voJasperPie.setPageWidth(800);
        voJasperPie.setWidth(600);
        voJasperPie.setHeight(400);
        voJasperPie.setX(0);
        voJasperPie.setY(0);
        voJasperPie.setChartType("pie");
        voJasperPie.setTitle("汽车销售");
        PieField pieField = new PieField();
        pieField.setKeyField("province");
        pieField.setValueField("amount");
        voJasperPie.setPieFields(pieField);
        voJasperPie.setQuerySql("select amount,province,category from vehicle_analysis limit 10");
        voJasperPie.setxLabel("销量");
        voJasperPie.setyLabel("省份");
        return voJasperPie;
    }

    private VoJasperParam getVoJasperBar() {
        VoJasperParam voJasperBar = new VoJasperParam();
        //voJasperBar.setDbType("hive");
        //voJasperBar.setDbName("dev_analysis");
        voJasperBar.setDbType("mysql");
        voJasperBar.setDbName("analysis");
        voJasperBar.setHost("172.16.1.241");
        voJasperBar.setPort("3306");
        voJasperBar.setUserName("ssiuser");
        voJasperBar.setPassword("112233");
        voJasperBar.setPageHeight(800);
        voJasperBar.setPageWidth(800);
        voJasperBar.setChartType("bar");
        //voJasperBar.setChartType("bar3D");
        //voJasperBar.setChartType("stackedbar3D");
//        voJasperBar.setChartType("stackedbar3D");
//        voJasperBar.setChartType("stackedbar3D");
        voJasperBar.setWidth(600);
        voJasperBar.setHeight(400);
        voJasperBar.setX(0);
        voJasperBar.setY(0);
        voJasperBar.setTitle("汽车销售");
        BarField barField = new BarField();
        barField.setSeriesField("account");
        barField.setCategoryField("year");
        barField.setValueField("salary");
        voJasperBar.setBarFields(barField);
        //voJasperBar.setQuerySql("select amount,province,category from vehicle_analysis limit 10");
        voJasperBar.setQuerySql("select * from t_user");
        voJasperBar.setxLabel("省份");
        voJasperBar.setyLabel("销量");
        return voJasperBar;
    }

    private VoJasperParam getVoTimeSeries() {
        VoJasperParam voJasperTimeSeries = new VoJasperParam();
        voJasperTimeSeries.setDbType("mysql");
        voJasperTimeSeries.setDbName("report");
        voJasperTimeSeries.setPageHeight(800);
        voJasperTimeSeries.setPageWidth(800);
        voJasperTimeSeries.setChartType("timeseries");
        voJasperTimeSeries.setWidth(600);
        voJasperTimeSeries.setHeight(400);
        voJasperTimeSeries.setX(0);
        voJasperTimeSeries.setY(0);
        voJasperTimeSeries.setHost("172.16.1.241");
        voJasperTimeSeries.setPort("3306");
        voJasperTimeSeries.setUserName("ssiuser");
        voJasperTimeSeries.setPassword("112233");
        voJasperTimeSeries.setTitle("年薪");
        TimeSeriesField timeSeriesField = new TimeSeriesField();
        timeSeriesField.setSeriesField("customerid");
        timeSeriesField.setTimePeriodField("OrderDate");
        timeSeriesField.setValueField("Freight");
        voJasperTimeSeries.setTimeSeriesFields(timeSeriesField);
        voJasperTimeSeries.setQuerySql("SELECT * FROM orders WHERE OrderID <= 12500  and customerid='quede' or customerid='OCEAN'  ORDER BY ShipCountry");

        voJasperTimeSeries.setxLabel("省份");
        voJasperTimeSeries.setyLabel("销量");
        return voJasperTimeSeries;
    }
    private VoJasperParam getVoJasperXyBar() {
        VoJasperParam voJasperXy = new VoJasperParam();
        voJasperXy.setDbType("mysql");
        voJasperXy.setDbName("report");
        voJasperXy.setPageHeight(800);
        voJasperXy.setPageWidth(800);
        voJasperXy.setChartType("xybar");
        voJasperXy.setWidth(600);
        voJasperXy.setHeight(400);
        voJasperXy.setX(0);
        voJasperXy.setY(0);
        voJasperXy.setHost("172.16.1.241");
        voJasperXy.setPort("3306");
        voJasperXy.setUserName("ssiuser");
        voJasperXy.setPassword("112233");
        voJasperXy.setTitle("汽车销售");
        XyField xyField = new XyField();
        xyField.setSeriesField("account");
        xyField.setxValueField("year");
        xyField.setyValueField("salary");
        voJasperXy.setXyFields(xyField);
        voJasperXy.setQuerySql("select account,`year` as yea,salary from t_user ");
        voJasperXy.setxLabel("年份");
        voJasperXy.setyLabel("年薪");
        return voJasperXy;
    }
    private VoJasperParam getVoJasperXyLine() {
        VoJasperParam voJasperXy = new VoJasperParam();
        voJasperXy.setDbType("mysql");
        voJasperXy.setDbName("report");
        voJasperXy.setPageHeight(800);
        voJasperXy.setPageWidth(800);
        voJasperXy.setChartType("xyline");
        voJasperXy.setWidth(600);
        voJasperXy.setHeight(400);
        voJasperXy.setX(0);
        voJasperXy.setY(0);
        voJasperXy.setHost("172.16.1.241");
        voJasperXy.setPort("3306");
        voJasperXy.setUserName("ssiuser");
        voJasperXy.setPassword("112233");
        voJasperXy.setTitle("汽车销售");
        XyField xyField = new XyField();
       /* xyField.setSeriesField("account");
        xyField.setxValueField("yea");
        xyField.setyValueField("salary");
        voJasperXy.setXyFields(xyField);
        voJasperXy.setQuerySql("select account,`year` as yea,salary from t_user ");*/
        //xy line
        xyField.setSeriesField("customerid");
        xyField.setxValueField("OrderID");
        xyField.setyValueField("Freight");
        voJasperXy.setXyFields(xyField);
        voJasperXy.setQuerySql("SELECT * FROM orders WHERE OrderID <= 12500  and customerid='quede' or customerid='OCEAN'  ORDER BY ShipCountry");
        voJasperXy.setxLabel("订单");
        voJasperXy.setyLabel("销量");
        return voJasperXy;
    }


    private VoJasperParam getVoJasperGantt() {
        VoJasperParam voJasperGantt = new VoJasperParam();
        voJasperGantt.setDbType("mysql");
        voJasperGantt.setDbName("report");
        voJasperGantt.setPageHeight(800);
        voJasperGantt.setPageWidth(800);
        voJasperGantt.setWidth(600);
        voJasperGantt.setHeight(400);
        voJasperGantt.setX(0);
        voJasperGantt.setY(0);
        voJasperGantt.setHost("172.16.1.241");
        voJasperGantt.setPort("3306");
        voJasperGantt.setUserName("ssiuser");
        voJasperGantt.setPassword("112233");
        voJasperGantt.setChartType("gantt");
        voJasperGantt.setTitle("甘特图");
        GanttField ganttField = new GanttField();
        ganttField.setSeriesField("SERIES");
        ganttField.setStartDateField("START_DATE");
        ganttField.setEndDateField("END_DATE");
        ganttField.setTaskField("TASK");
        ganttField.setSubTaskField("SUBTASK");
        ganttField.setPercentField("PERCENT");
        voJasperGantt.setGanttFields(ganttField);
        voJasperGantt.setQuerySql("SELECT * FROM t_gantt");
        return voJasperGantt;
    }

    private VoJasperParam getVoJasperMeter() {
        VoJasperParam voJasperMeter = new VoJasperParam();
        voJasperMeter.setDbType("mysql");
        voJasperMeter.setDbName("analysis");
        voJasperMeter.setPageHeight(800);
        voJasperMeter.setPageWidth(800);
        voJasperMeter.setWidth(600);
        voJasperMeter.setHeight(400);
        voJasperMeter.setX(0);
        voJasperMeter.setY(0);
        voJasperMeter.setChartType("meter");
        voJasperMeter.setHost("172.16.1.241");
        voJasperMeter.setPort("3306");
        voJasperMeter.setUserName("ssiuser");
        voJasperMeter.setPassword("112233");
        voJasperMeter.setTitle("里程表");
        voJasperMeter.setValueField("salary");
        voJasperMeter.setQuerySql("SELECT * FROM t_user limit 1");
        return voJasperMeter;
    }

    @Autowired
    private ChartPublishService chartPublishService;

    @Test
    public void publishTest() throws ClassNotFoundException, JRException, SQLException, IOException {
        VoJasperParam voJasperBar = getVoJasperBar();
        AbstractJasperReportDesigner jasperDesigner =
                JasperDesignerFactory.createJasperDesigner(voJasperBar.getChartType());
        String renderHtmlStr = jasperDesigner.renderHtmlStr(voJasperBar);
        Document doc = Jsoup.parse(renderHtmlStr);
        Elements img = doc.select("img");
        if (img != null) {
            ChartPublishEntity chartPublishEntity = new ChartPublishEntity();
            chartPublishEntity.setReportParam(JSON.toJSONString(voJasperBar));
            chartPublishEntity.setReportContent(img.outerHtml());
            chartPublishEntity.setPublishTime(new java.util.Date());
            chartPublishEntity.setSerialNum(1);
            chartPublishEntity.setStatus(2);
            chartPublishEntity.setId(Long.valueOf(2));
            //int flag = chartPublishService.insert(chartPublishEntity);
            int flag = chartPublishService.update(chartPublishEntity);
            if (flag > 0) {
                System.out.println("++++++++++++发布成功");
            }
        }
    }
}
