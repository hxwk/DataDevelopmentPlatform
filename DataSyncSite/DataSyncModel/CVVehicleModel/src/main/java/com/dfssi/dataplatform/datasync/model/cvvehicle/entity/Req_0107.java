package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author HONGSHUAI
 * 查询终端属性应答对象
 */

public class Req_0107 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0107"; }

    private String sim;

    private TerminalProperties terminalproperties;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public TerminalProperties getPassengerflow() {
        return terminalproperties;
    }

    public void setPassengerflow(TerminalProperties terminalproperties) {
        this.terminalproperties = terminalproperties;
    }

    @Override
    public String toString() {
        return "Req_1005{" +super.toString() +
                "sim='" + sim + '\'' +
                ", terminalproperties=" + terminalproperties +
                '}';
    }
}
