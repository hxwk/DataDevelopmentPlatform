package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 1005，乘客流量信息实体
 * @author jianKang
 * @date 2018/01/22
 */
public class PassengerFlow implements Serializable {

    private Date startTime;
    private Date endTime;
    private int UpNum;
    private int DownNum;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getUpNum() {
        return UpNum;
    }

    public void setUpNum(int upNum) {
        UpNum = upNum;
    }

    public int getDownNum() {
        return DownNum;
    }

    public void setDownNum(int downNum) {
        DownNum = downNum;
    }

    @Override
    public String toString() {
        return "passengerflow{" +
                "startTime='" + startTime + '\'' +
                "endTime='" + endTime + '\''+
                "UpNum='" + UpNum + '\''+
                "DownNum='" + DownNum + '\''+
                '}';
    }
}
