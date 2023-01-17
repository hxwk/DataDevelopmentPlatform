package com.dfssi.dataplatform.datasync.model.common;

/**
 * （上行/下行）请求消息基类
 */
public abstract class JtsReqMsg implements Message {
    /* 车辆ID */
    protected String vid;

    /* 车辆VIN */
    protected String vin;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "JtsReqMsg{" +
                "vid='" + vid + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
