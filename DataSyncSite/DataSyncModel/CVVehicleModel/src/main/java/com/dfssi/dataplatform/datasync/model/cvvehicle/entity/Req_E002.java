package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * @author chenf
 * @date 2018-09-21
 * @description sd 文件上传
 *
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * SD数据上传，分包(Subpackage)
 */
public class Req_E002 extends JtsReqMsg {

    @Override
    public String id() { return "jts.E002"; }

    private short Sn; //流水号

    private String sim;

    private int paramId;

    private String resValue;

    public short getSn() {
        return Sn;
    }

    public void setSn(short sn) {
        Sn = sn;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public String getResValue() {
        return resValue;
    }

    public void setResValue(String resValue) {
        this.resValue = resValue;
    }
}
