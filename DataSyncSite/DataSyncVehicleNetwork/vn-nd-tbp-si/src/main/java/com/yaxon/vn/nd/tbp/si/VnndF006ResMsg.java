package com.yaxon.vn.nd.tbp.si;


import java.util.Date;

/**
 * 基类
 */
public class VnndF006ResMsg extends VnndResMsg{

    public String id() {
        return "jts.F006.nd.r";
    }

    public byte rc;


    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }

    @Override
    public String toString() {
        return "Res_F003_nd{" + super.toString() +
                ", rc='" + rc + '\'' +
                '}';
    }
}
