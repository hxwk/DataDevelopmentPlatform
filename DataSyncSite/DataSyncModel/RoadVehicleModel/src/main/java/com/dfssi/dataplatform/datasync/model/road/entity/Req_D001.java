package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author bin.Y 
 * Description:
 * Date:  2018/9/12 14:32
 */
public class Req_D001 extends JtsReqMsg {
    public static final String _id = "jts.D001";
    @Override
    public String id() {
        return _id;
    }

    private String sim;

    private FailureCodeBean failureCodeBean;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public FailureCodeBean getFailureCodeBean() {
        return failureCodeBean;
    }

    public void setFailureCodeBean(FailureCodeBean failureCodeBean) {
        this.failureCodeBean = failureCodeBean;
    }

    @Override
    public String toString() {
        return "Req_D001{" +
                "sim='" + sim + '\'' +
                ", failureCodeBean=" + failureCodeBean +
                '}';
    }
}
