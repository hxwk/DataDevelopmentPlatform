package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * @author chenf
 * @date 2018-09-21
 * @description sd 文件上传
 *
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * SD数据上传，分包(Subpackage)
 */
public class Req_E002_P extends JtsReqMsg {
    @Override
    public String id() {return "jts.E002.p";}

    private String sim; //手机号
    private short sn = 0; //流水号
    private int packCount = 0; //分包总数，>0
    private int packIndex = 0; //分包索引，从1开始算
    private byte[] data; //分包数据

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPackIndex() {
        return packIndex;
    }

    public void setPackIndex(int packIndex) {
        this.packIndex = packIndex;
    }

    public int getPackCount() {
        return packCount;
    }

    public void setPackCount(int packCount) {
        this.packCount = packCount;
    }

    public short getSn() {
        return sn;
    }

    public void setSn(short sn) {
        this.sn = sn;
    }

}
