package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: 孙震
 * Time: 2014-02-17 15:09
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 故障码数据项
 */

public class HitchCodeItem implements Serializable {
    private Integer spn; //可疑参数编号
    private Byte fmi; //故障模式标志符
    private Byte oc; //发生次数

    public HitchCodeItem() {

    }

    public HitchCodeItem(Integer spn, Byte fmi, Byte oc) {
        this.spn = spn;
        this.fmi = fmi;
        this.oc = oc;
    }

    public Integer getSpn() {
        return spn;
    }

    public void setSpn(Integer spn) {
        this.spn = spn;
    }

    public Byte getFmi() {
        return fmi;
    }

    public void setFmi(Byte fmi) {
        this.fmi = fmi;
    }

    public Byte getOc() {
        return oc;
    }

    public void setOc(Byte oc) {
        this.oc = oc;
    }

    @Override
    public String toString() {
        return "HitchCodeItem{" +
                "spn=" + spn +
                ", fmi=" + fmi +
                ", oc=" + oc +
                '}';
    }
}
