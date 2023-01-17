package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 行驶记录数据上传 （2012版）
 * 采集当前驾驶人信息
 */
public class Res_0700_01 extends JtsResMsg  {

    @Override
    public String id() {
        return "jts.0700.01";
    }

    private String lic; //机动车驾驶证号码

    private byte[] data;  //数据块（用于809协议）

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_01{" +
                "lic='" + lic + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
