package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JIANKANG
 * 终端上传乘客流量对象
 */

public class Req_1005 extends JtsReqMsg {
    @Override
    public String id() { return "jts.1005"; }

    private String sim;

    private PassengerFlow passengerflow;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public PassengerFlow getPassengerflow() {
        return passengerflow;
    }

    public void setPassengerflow(PassengerFlow passengerflow) {
        this.passengerflow = passengerflow;
    }

    @Override
    public String toString() {
        return "Req_1005{" +super.toString() +
                "sim='" + sim + '\'' +
                ", passengerflow=" + passengerflow +
                '}';
    }
}
