package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:26
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 数据上行透传
 */
public class Req_0900 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0900"; }

    private byte transparentTransmitMsgType; //透传消息类型

    private short hitchCodeType; //故障码类型

    private Date hitchCodeTime; //故障码发生时间

    private byte indicatorLight1; //指示灯1

    private byte indicatorLight2; //指示灯2

    private List<HitchCodeItem> hitchCodeItems; //故障码数据项



    //透传消息内容
    private byte[] content;



    public byte getTransparentTransmitMsgType() {
        return transparentTransmitMsgType;
    }

    public void setTransparentTransmitMsgType(byte transparentTransmitMsgType) {
        this.transparentTransmitMsgType = transparentTransmitMsgType;
    }

    public short getHitchCodeType() {
        return hitchCodeType;
    }

    public void setHitchCodeType(short hitchCodeType) {
        this.hitchCodeType = hitchCodeType;
    }

    public Date getHitchCodeTime() {
        return hitchCodeTime;
    }

    public void setHitchCodeTime(Date hitchCodeTime) {
        this.hitchCodeTime = hitchCodeTime;
    }

    public byte getIndicatorLight1() {
        return indicatorLight1;
    }

    public void setIndicatorLight1(byte indicatorLight1) {
        this.indicatorLight1 = indicatorLight1;
    }

    public byte getIndicatorLight2() {
        return indicatorLight2;
    }

    public void setIndicatorLight2(byte indicatorLight2) {
        this.indicatorLight2 = indicatorLight2;
    }

    public List<HitchCodeItem> getHitchCodeItems() {
        return hitchCodeItems;
    }

    public void setHitchCodeItems(List<HitchCodeItem> hitchCodeItems) {
        this.hitchCodeItems = hitchCodeItems;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Req_0900{" +
                "transparentTransmitMsgType=" + transparentTransmitMsgType +
                ", hitchCodeType=" + hitchCodeType +
                ", hitchCodeTime=" + hitchCodeTime +
                ", indicatorLight1=" + indicatorLight1 +
                ", indicatorLight2=" + indicatorLight2 +
                ", hitchCodeItems=" + hitchCodeItems +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
