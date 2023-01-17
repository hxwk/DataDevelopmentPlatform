package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JIANKANG
 * 终端上传音视频属性对象
 */

public class Req_1003 extends JtsReqMsg {
    @Override
    public String id() { return "jts.1003"; }

    private String sim;

    private AVPropertyVo avpropertyvo;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public AVPropertyVo getAvpropertyvo() {
        return avpropertyvo;
    }

    public void setAvpropertyvo(AVPropertyVo avpropertyvo) {
        this.avpropertyvo = avpropertyvo;
    }

    @Override
    public String toString() {
        return "Req_1003{" +super.toString() +
                "sim='" + sim + '\'' +
                ", avpropertyvo=" + avpropertyvo +
                '}';
    }
}
