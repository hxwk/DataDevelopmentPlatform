package com.dfssi.dataplatform.analysis.service.service.jasper;

import com.dfssi.dataplatform.analysis.service.entity.jasper.VoBaseJasper;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import com.dfssi.dataplatform.analysis.service.resource.DBCommon;
import com.dfssi.dataplatform.analysis.service.resource.DBType;
import com.google.common.base.Preconditions;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description jasper report报表基类
 * @Author zhangcheng
 * @Date 2018/9/19 15:19
 **/
public abstract class AbstractJasperReportDesigner {
    /**
     * 报表样式自定义类路径
     */
    @Value("${jasper.customizer}")
    private String customizer;

    /**
     * 获取数据库链接
     *
     * @param voBaseJasper 参数实体
     * @return 数据库链接
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private Connection getConnection(VoBaseJasper voBaseJasper)
            throws SQLException, ClassNotFoundException {
        return DBCommon.getConn(DBType.newDBType(voBaseJasper.getDbType()), voBaseJasper.getHost(), voBaseJasper.getPort(),
                voBaseJasper.getDbName(), voBaseJasper.getUserName(), voBaseJasper.getPassword());
    }

    /**
     * 报表义html字符流输出
     *
     * @param jasperPrint 填充数据后的报表，可导出各种格式
     * @param response    响应
     * @throws Exception
     */
    private void renderHtmlStream(JasperPrint jasperPrint, HttpServletResponse response) throws IOException, JRException {
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput simpleHtmlExporterOutput = new SimpleHtmlExporterOutput(response.getOutputStream());
        simpleHtmlExporterOutput.setImageHandler(new HtmlImageHandler());
        exporter.setExporterOutput(simpleHtmlExporterOutput);
        exporter.exportReport();
    }

    /**
     * 报表以html格式导出
     *
     * @param jasperPrint 填充数据后的报表
     * @param destFile    目标文件路径
     * @throws Exception
     */
    private void renderHtml(JasperPrint jasperPrint, String destFile) throws FileNotFoundException, JRException {
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput simpleHtmlExporterOutput =
                new SimpleHtmlExporterOutput(new FileOutputStream(new File(destFile)));
        simpleHtmlExporterOutput.setImageHandler(new HtmlImageHandler());
        exporter.setExporterOutput(simpleHtmlExporterOutput);
        exporter.exportReport();
    }

    /**
     * 生成报表html字符串的形式
     * @param voJasperParam   填充数据后的报表
     * @return               html字符串
     * @throws JRException
     * @throws IOException
     */
    public String renderHtmlStr(VoJasperParam voJasperParam)
            throws JRException, IOException, SQLException, ClassNotFoundException {
        JasperPrint jasperPrint = renderJasperPrint(voJasperParam);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput simpleHtmlExporterOutput =
                new SimpleHtmlExporterOutput(out);
        simpleHtmlExporterOutput.setImageHandler(new HtmlImageHandler());
        exporter.setExporterOutput(simpleHtmlExporterOutput);
        exporter.exportReport();
        String resultStr = out.toString();
        out.close();
        return resultStr;
    }

    /**
     *  报表设计的实现类
     * @param voJasperParam
     * @return    编译后的jasper对象
     * @throws JRException
     */
    public  abstract JasperReport createJapserReport(VoJasperParam voJasperParam)
            throws JRException;

    /**
     *  产生填充数据后的打印对象（利用该对象可以导出各种格式的可视化报表）
     * @param voJasperParam
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws JRException
     */
    private JasperPrint  renderJasperPrint(VoJasperParam voJasperParam)
            throws SQLException, ClassNotFoundException,JRException {
        JasperReport japserReport = createJapserReport(voJasperParam);
        Map<String, Object> params = createJapserReportParams(voJasperParam);
        Connection connection = getConnection(voJasperParam);
        Preconditions.checkNotNull(connection, String.format("数据库%s的连接为null", voJasperParam.getDbType()));
        JasperPrint jasperPrint = JasperFillManager.fillReport(japserReport, params, connection);
        if (connection != null) {
            connection.close();
        }
        return jasperPrint;
    }

    /**
     *  报表pdf 格式导出
     * @param voJasperParam
     * @param destFile
     */
    public void exportToPdf(VoJasperParam voJasperParam, String destFile) {
        Connection connection = null;
        try {
            JasperExportManager.exportReportToPdfFile(renderJasperPrint(voJasperParam), destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 定义一个JRDesignExpression
     *
     * @param labelExpress 符合jasperReport规范的表达式
     * @return 表达式
     */
    protected JRDesignExpression createJRExpression(String labelExpress) {
        JRDesignExpression labelExpression = new JRDesignExpression();
        labelExpression.setText(labelExpress);
        return labelExpression;
    }

    /**
     * 为报表创建参数变量
     *
     * @param voBaseJasper 报表视图参数实体
     * @return 参数(map格式)
     */
    protected Map<String, Object> createJapserReportParams(VoBaseJasper voBaseJasper) {
        Map<String, Object> params = new HashMap<>();
        params.put("xLabel", voBaseJasper.getxLabel());
        params.put("yLabel", voBaseJasper.getyLabel());
        return params;
    }

    public String getCustomizer() {
        return customizer;
    }

    /**
     * japser report 输出html格式时，图片处理
     */
    public class HtmlImageHandler implements HtmlResourceHandler {
        //存放报表图片
        private Map<String, String> images = new HashMap<>();

        @Override
        public String getResourcePath(String id) {
            return images.get(id);
        }

        @Override
        public void handleResource(String id, byte[] data) {
            images.put(id, "data:image/png;base64," + Base64.encodeBase64String(data));
        }
    }

}
