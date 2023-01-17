package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JIANKANG
 * 音视频资源列表对象
 */

public class Req_1205 extends JtsReqMsg {
    @Override
    public String id() { return "jts.1205"; }

    private String sim;

    private AVResourceListVo avResourceList;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public AVResourceListVo getAvResourceList() {
        return avResourceList;
    }

    public void setAvResourceList(AVResourceListVo avResourceList) {
        this.avResourceList = avResourceList;
    }

    @Override
    public String toString() {
        return "Req_1205{" +
                "sim='" + sim + '\'' +
                ", avResourceList=" + avResourceList +
                '}';
    }
}
