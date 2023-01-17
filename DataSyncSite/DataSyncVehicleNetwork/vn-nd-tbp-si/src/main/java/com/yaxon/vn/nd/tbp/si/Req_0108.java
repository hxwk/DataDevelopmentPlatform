package com.yaxon.vn.nd.tbp.si;

public class Req_0108 extends JtsReqMsg{
    @Override
    public String id() { return "jts.0108"; }

    private String sim;

    private byte updateType;

    private byte updateResult;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getUpdateType() {
        return updateType;
    }

    public void setUpdateType(byte updateType) {
        this.updateType = updateType;
    }

    public byte getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(byte updateResult) {
        this.updateResult = updateResult;
    }

    @Override
    public String toString() {
        return "Req_0108{" +
                "sim='" + sim + '\'' +
                "updateType='" + updateType + '\'' +
                "updateResult='" + updateResult + '\'' +
                '}';
    }

}
