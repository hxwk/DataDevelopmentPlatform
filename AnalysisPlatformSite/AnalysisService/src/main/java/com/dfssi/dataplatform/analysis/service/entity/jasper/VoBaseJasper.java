package com.dfssi.dataplatform.analysis.service.entity.jasper;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

/**
 * @Description  报表视图参数实体基类
 * @Author zhangcheng
 * @Date 2018/9/18 15:25
 **/
public class VoBaseJasper {
    /**
     * 报表页宽
     */
    @ApiModelProperty(value = "报表页面宽度，默认800")
    private Integer pageWidth;
    /**
     * 报表页高
     */
    @ApiModelProperty(value = "报表页面高度，默认800")
    private Integer pageHeight;
    /**
     * 报表宽度
     */
    @ApiModelProperty(value = "报表区域宽度，默认600")
    private Integer width;
    /**
     * 报表高度
     */
    @ApiModelProperty(value = "报表区域高度，默认400")
    private Integer height;
    /**
     * x轴坐标
     */
    @ApiModelProperty(value = "报表x轴偏移量，默认0")
    private Integer x;
    /**
     * y轴坐标
     */
    @ApiModelProperty(value = "报表y轴偏移量，默认0")
    private Integer y;
    /**
     * 报表标题
     */
    @ApiModelProperty(value = "图表标题")
    private String  title;
    /**
     * 数据库类型
     */
    @ApiModelProperty(value = "数据库类型",example = "hive",required = true)
    private String dbType;
    /**
     * 数据库名称
     */
    @ApiModelProperty(value = "数据库表名称",example = "ssi_analysis",required = true)
    private String dbName;
    /**
     * 数据库所在主机
     */
    @ApiModelProperty(value = "数据库所在服务器")
    private String host;
    /**
     *  数据库链接端口
     */
    @ApiModelProperty(value = "数据库所在服务器端口")
    private String port;
    /**
     *  数据库登陆用户名
     */
    @ApiModelProperty(value = "数据库用户名")
    private String userName;
    /**
     * 数据库登陆密码
     */
    @ApiModelProperty(value = "数据库用户密码")
    private String password;
    /**
     * sql语句
     */
    @ApiModelProperty(value = "数据查询语句",required = true)
    private String querySql;
    /**
     *  legend 的位置
     */
    @ApiModelProperty(value = "legend位置,默认为Bottom",example = "Top")
    private String  legendPosition;
    /**
     * x轴标题
     */
    @ApiModelProperty(value = "x轴标签")
    private String  xLabel;
    /**
     * y轴标题
     */
    @ApiModelProperty(value = "y轴标签")
    private String  yLabel;
    /**
     * 图表类型
     */
    @ApiModelProperty(value = "图表类型",example = "bar",required = true)
    private String chartType;

    public Integer getPageWidth() {
        return (pageWidth != null) ? pageWidth : 800;
    }

    public void setPageWidth(Integer pageWidth) {
        this.pageWidth = pageWidth;
    }

    public Integer getPageHeight() {
        return (pageHeight != null)? pageHeight : 800;
    }

    public void setPageHeight(Integer pageHeight) {
        this.pageHeight = pageHeight;
    }

    public Integer getWidth() {
        return (width != null)? width : 600;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return (height != null)? height : 400;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getX() {
        return (x != null)? x : 0;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return (y != null)? y : 0;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public String getLegendPosition() {
        return StringUtils.isNotBlank(legendPosition)? legendPosition : "Bottom";
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
