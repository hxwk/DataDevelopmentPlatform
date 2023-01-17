package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-01 15:50
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 位置信息汇报
 */
public class Req_0200 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0200"; }

    private String sim;

    private GpsVo gps;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "Req_0200{" + super.toString() +
                ", sim=" + sim +
                ", gps=" + gps +
                '}';
    }
}
