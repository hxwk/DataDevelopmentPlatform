package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

/**
 * （上行/下行）请求消息基类
 * @author jianKang
 * @date 2017/12/14
 */
public abstract class JtsReqMsg {
    /**
     *  车辆ID
     */
    protected long vid;

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    @Override
    public String toString() {
        return "vid=" + vid;
    }
}
