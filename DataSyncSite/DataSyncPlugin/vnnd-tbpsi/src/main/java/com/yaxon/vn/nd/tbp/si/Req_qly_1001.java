package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 赖贵明
 * Time: 2015-01-27 10:03
 * Copyright (C) 2015 Xiamen Yaxon Networks CO.,LTD.
 * 千里眼协议：短信中心号设置请求
 */
public class Req_qly_1001 extends JtsReqMsg{
    @Override
    public String id() {
        return "jts.qly.1001";
    }
    private long sim;
    private String  tel;//短信中心号码 (22)

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "Req_qly_1001{" +
                "sim=" + sim +
                ", tel='" + tel + '\'' +
                '}';
    }
}
