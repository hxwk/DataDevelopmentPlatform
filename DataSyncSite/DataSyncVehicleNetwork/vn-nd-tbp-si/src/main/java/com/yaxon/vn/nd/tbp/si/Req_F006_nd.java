package com.yaxon.vn.nd.tbp.si;

import java.util.List;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-29 18:03
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗 下发 查询锁车相关状态
 */
public class Req_F006_nd extends JtsReqMsg{

    public static final String _id = "jts.F006.nd";
	


    @Override
    public String id() {
        return "jts.F006.nd";
    }
    /*车牌号LPN*/
    private String lpn;

    /*参数列表*/
    private List<ParmBean> param;

    public List<ParmBean> getParam() {
        return param;
    }

    public void setParam(List<ParmBean> param) {
        this.param = param;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    @Override
    public String toString() {
        return "Req_F006_nd{" + super.toString() +
                ",lpn=" + lpn +
                ",param"+param+
                '}';
    }
}
