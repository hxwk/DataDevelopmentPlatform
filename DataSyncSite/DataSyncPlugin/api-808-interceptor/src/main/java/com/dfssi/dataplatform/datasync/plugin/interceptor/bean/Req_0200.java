package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

/**
 * 位置信息汇报
 * @author jianKang
 * @date 2017/12/15
 */
public class Req_0200 extends JtsReqMsg {
    public String id() { return "jts.0200"; }

    private Long sim;

    private GpsVo gps;

    public Long getSim() {
        return sim;
    }

    public void setSim(Long sim) {
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
        /*return "Req_0200{" + super.toString() +
                ", sim=" + sim +
                ", gps=" + gps +
                '}';*/
        return JSON.toJSONString(this);
    }
}
