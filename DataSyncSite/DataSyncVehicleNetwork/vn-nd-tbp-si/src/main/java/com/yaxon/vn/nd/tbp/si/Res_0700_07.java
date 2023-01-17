package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;
import java.util.Date;

/**
 * 行驶记录数据上传 （2012版）
 * 采集记录仪唯一性编号
 */
public class Res_0700_07 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.07";
    }

    private String ccc; //生产产CCC认证代码
    private String model; //认证产品型号
    private Date createTime; //记录仪的生产日期
    private String sn; //生产流水号
    private String backup; //备用字段

    private byte[] data;  //数据块（用于809协议）

    public String getCcc() {
        return ccc;
    }

    public void setCcc(String ccc) {
        this.ccc = ccc;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_07{" +
                "ccc='" + ccc + '\'' +
                ", model='" + model + '\'' +
                ", createTime=" + createTime +
                ", sn='" + sn + '\'' +
                ", backup='" + backup + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
