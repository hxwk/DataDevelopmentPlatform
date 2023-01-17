package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:47
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 多媒体数据上传应答
 */
public class Res_8800 extends JtsResMsg {

    @Override
    public String id() { return "jts.8800"; }

    private int mediaDataId; //多媒体数据ID
    private byte[] reSendPackageIds; //重传包ID列表

    public int getMediaDataId() {
        return mediaDataId;
    }

    public void setMediaDataId(int mediaDataId) {
        this.mediaDataId = mediaDataId;
    }

    public byte[] getReSendPackageIds() {
        return reSendPackageIds;
    }

    public void setReSendPackageIds(byte[] reSendPackageIds) {
        this.reSendPackageIds = reSendPackageIds;
    }

    @Override
    public String toString() {
        return "Res_8800{" + super.toString() +
                ", mediaDataId=" + mediaDataId +
                ", reSendPackageIds=" + Arrays.toString(reSendPackageIds) +
                '}';
    }
}
