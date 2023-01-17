package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:16
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 单条存储多媒体数据检索上传命令
 */
public class Req_8805 extends JtsReqMsg {

    public static final String _id = "jts.8805";

    @Override
    public String id() { return "jts.8805"; }

    private Integer mediaId; //多媒体ID
    private Byte deleteFlag; //删除标志 0：保留； 1：删除

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public Byte getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Byte deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public String toString() {
        return "Req_8805{" + super.toString() +
                ", mediaId=" + mediaId +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}
