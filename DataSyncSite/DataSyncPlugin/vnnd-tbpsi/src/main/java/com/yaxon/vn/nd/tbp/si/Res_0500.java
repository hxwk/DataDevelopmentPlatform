package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:12
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 车辆控制应答——位置信息汇报消息体
 */
public class Res_0500 extends JtsResMsg {
    @Override
    public String id() { return "jts.0500"; }

    private GpsVo gps;

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "Res_0500{" + super.toString() +
                "gps=" + gps +
                '}';
    }
}
