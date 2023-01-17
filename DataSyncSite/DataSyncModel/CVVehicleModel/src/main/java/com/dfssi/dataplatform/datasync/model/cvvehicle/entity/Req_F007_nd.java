package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-29 18:03
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗 下发 查询锁车相关状态
 */
public class Req_F007_nd extends JtsReqMsg {

    public static final String _id = "jts.F007.nd";

    @Override
    public String id() {
        return "jts.F007.nd";
    }
    /*车牌号LPN*/
    private String lpn;

    /*开关状态
        * 0：IP2开关关闭
         1：IP2开关打开
        * */
    private String status;

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Req_F007_nd{" + super.toString() +
                ",lpn=" + lpn +
                ",status="+status+
                '}';
    }
}