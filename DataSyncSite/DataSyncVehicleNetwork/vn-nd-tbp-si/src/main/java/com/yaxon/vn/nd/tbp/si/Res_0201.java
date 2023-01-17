package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 15:01
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 位置信息查询应答
 */
public class Res_0201 extends JtsResMsg {
    @Override
    public String id() { return "jts.0201"; }

    private GpsVo gps;

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "Res_0201{" + super.toString() +
                "gps=" + gps +
                '}';
    }
}
