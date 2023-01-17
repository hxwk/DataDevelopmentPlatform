package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;


import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;

/**
 * 基类
 */
public class VnndF007ResMsg extends VnndResMsg {

    public String id() {
        return "jts.F007.nd.r";
    }
    private byte rc;//查询结果 0成功；  1失败；  2查询超时



    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }


    @Override
    public String toString() {
        return "Res_F007_nd{" + super.toString() +
                ", rc='" + rc + '\'' +
                '}';
    }
}
