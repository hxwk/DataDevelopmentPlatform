package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:30
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 平台RSA公钥
 */
public class Req_8A00 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8A00"; }

    private Long e; //公钥e
    private String n; //公钥n

    public Long getE() {
        return e;
    }

    public void setE(Long e) {
        this.e = e;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Req_8A00{" + super.toString() +
                ", e=" + e +
                ", n='" + n + '\'' +
                '}';
    }
}
