package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 11:45
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;

import java.util.List;

/**
 * 存储多媒体数据检索应答
 */
public class Res_0802 extends JtsResMsg {
    @Override
    public String id() { return "jts.0802"; }

    private List<MediaParamItem> mediaParamItems; //多媒体检索项列表

    public List<MediaParamItem> getMediaParamItems() {
        return mediaParamItems;
    }

    public void setMediaParamItems(List<MediaParamItem> mediaParamItems) {
        this.mediaParamItems = mediaParamItems;
    }

    @Override
    public String toString() {
        return "Res_0802{" + super.toString() +
                ", mediaParamItems=" + mediaParamItems +
                '}';
    }
}
