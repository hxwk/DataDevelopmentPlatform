package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2014-01-20 14:44
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 * @modify: JianKang
 * @modifyTime: 2018/03/02
 */
public class Req_0702Q extends JtsReqMsg{
    @Override
    public String id() {
        return "jts.0702Q";
    }

    private String sim;

    private DriverCardInforItem driverCardInforItem;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public DriverCardInforItem getDriverCardInforItem() {
        return driverCardInforItem;
    }

    public void setDriverCardInforItem(DriverCardInforItem driverCardInforItem) {
        this.driverCardInforItem = driverCardInforItem;
    }

    @Override
    public String toString() {
        return "Req_0702Q{" + super.toString() +
                "sim='" + sim +
                ", driverCardInforItem=" + driverCardInforItem +
                '}';
    }
}
