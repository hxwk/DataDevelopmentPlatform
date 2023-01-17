package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 19:08
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class AccidentSuspicion2012Item implements Serializable {

    private Date endTime; //行驶结束时间
    private String lic; //驾驶证号码：ASCII码字符
    private List<AccidentSuspicion> asList; //行驶记录仪事故疑点数据项
    private Position position; //位置记录

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    public List<AccidentSuspicion> getAsList() {
        return asList;
    }

    public void setAsList(List<AccidentSuspicion> asList) {
        this.asList = asList;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "AccidentSuspicion2012Item{" +
                "endTime=" + endTime +
                ", lic='" + lic + '\'' +
                ", asList=" + asList +
                ", position=" + position +
                '}';
    }
}
