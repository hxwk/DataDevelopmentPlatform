package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JianKang
 * @date 2018/5/29
 * @description
 */
public class Req_D004 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.D004";
    }

    private String sim;

    private GpsAndCanDataStructure gpsAndCan;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public GpsAndCanDataStructure getGpsAndCan() {
        return gpsAndCan;
    }

    public void setGpsAndCan(GpsAndCanDataStructure gpsAndCan) {
        this.gpsAndCan = gpsAndCan;
    }

    @Override
    public String toString() {
        return "Req_D004{" +
                "sim='" + sim + '\'' +
                ", gpsAndCan=" + gpsAndCan +
                '}';
    }
}
