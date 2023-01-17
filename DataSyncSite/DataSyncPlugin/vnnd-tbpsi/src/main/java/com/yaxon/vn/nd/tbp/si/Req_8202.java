package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 15:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 临时位置跟踪控制
 */
public class Req_8202 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8202"; }

    private short timeDistance; //时间间隔
    private int usefulLife; //位置跟踪有效期
    private long sim;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public short getTimeDistance() {
        return timeDistance;
    }

    public void setTimeDistance(short timeDistance) {
        this.timeDistance = timeDistance;
    }

    public int getUsefulLife() {
        return usefulLife;
    }

    public void setUsefulLife(int usefulLife) {
        this.usefulLife = usefulLife;
    }

    @Override
    public String toString() {
        return "Req_8202{" +
                "timeDistance=" + timeDistance +
                ", usefulLife=" + usefulLife +
                ", sim=" + sim +
                '}';
    }
}
