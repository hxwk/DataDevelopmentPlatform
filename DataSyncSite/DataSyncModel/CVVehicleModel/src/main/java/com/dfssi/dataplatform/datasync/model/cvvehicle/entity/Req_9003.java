package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 平台向终端请求资源列表
 */
public class Req_9003 extends JtsReqMsg {

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    @Override
    public String id() { return "jts.9003"; }

    @Override
    public String toString() {
        return "Res_9003{Sim:"+sim +"}";
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }


}
