package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 08:56
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 查询终端属性
 */
public class Req_8107 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8107";}

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }
    /// 消息体为空
    @Override
    public String toString() {
        return "Req_8107{" + super.toString() +
                "sim='" + sim + '\'' +"}";
    }

}
