package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:44
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 定位数据批量上传
 */
public class Req_0704 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0704";
    }
    private String sim;
    private byte positionDataType; //位置数据类型
    private List<GpsVo> gpsVos; //位置汇报数据项列表


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getPositionDataType() {
        return positionDataType;
    }

    public void setPositionDataType(byte positionDataType) {
        this.positionDataType = positionDataType;
    }

    public List<GpsVo> getGpsVos() {
        return gpsVos;
    }

    public void setGpsVos(List<GpsVo> gpsVos) {
        this.gpsVos = gpsVos;
    }

    @Override
    public String toString() {
        return "Req_0704{" + super.toString() +
                "positionDataType=" + positionDataType +
                ", gpsVos=" + gpsVos +
                '}';
    }
}
