package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 18:44
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class PositionInformationItem implements Serializable {

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    private String lpn;//车牌号
    private Short speed; //每分钟平均速度
    private Position  position; //位置信息
    private  Integer positiondesnumber;// 位置描述个数（by luxin）
    private Date startTime;//开始时间 （by luxin）

    public Integer getPositiondesnumber() {
        return positiondesnumber;
    }

    public void setPositiondesnumber(Integer positiondesnumber) {
        this.positiondesnumber = positiondesnumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Short getSpeed() {
        return speed;

    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "PositionInformationItem{" +
                "speed=" + speed +'\''+
                ", position=" + position+'\'' +
                ",startTime="+startTime+'\''+
                ",lpn="+lpn+'\''+
                ",positiondesnumber="+positiondesnumber+
                '}';
    }
}
