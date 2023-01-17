package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:47
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 多媒体数据上传应答
 */
public class Res_8800_P extends JtsResMsg {

    @Override
    public String id() { return "jts.8800.p"; }

    private byte lastPack = 0; //0，收到部分分包的应答；1，收到所有分包的应答

    private int mediaId; //多媒体数据ID，针对最后分包应答才有效
    private List<Integer> packIndexes; //待重传分包序号列表，针对最后分包应答才有效

    public byte getLastPack() {
        return lastPack;
    }

    public void setLastPack(byte lastPack) {
        this.lastPack = lastPack;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public List<Integer> getPackIndexes() {
        return packIndexes;
    }

    public void setPackIndexes(List<Integer> packIndexes) {
        this.packIndexes = packIndexes;
    }

    @Override
    public String toString() {
        return "Res_8800_P{" + super.toString() +
                ", lastPack=" + lastPack +
                ", mediaId=" + mediaId +
                ", packIndexes=" + packIndexes +
                '}';
    }
}
