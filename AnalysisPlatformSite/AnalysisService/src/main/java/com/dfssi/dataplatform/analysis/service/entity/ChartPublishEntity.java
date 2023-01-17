package com.dfssi.dataplatform.analysis.service.entity;

import java.util.Date;

/**
 * @Description 图表发布实体
 * @Author zhangcheng
 * @Date 2018/9/29 11:15
 */
public class ChartPublishEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 发布的报表参数，json串
     */
    private String reportParam;
    /**
     * 报表内容
     */
    private String reportContent;
    /**
     * 序号
     */
    private Integer serialNum;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportParam() {
        return reportParam;
    }

    public void setReportParam(String reportParam) {
        this.reportParam = reportParam;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public Integer getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(Integer serialNum) {
        this.serialNum = serialNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
