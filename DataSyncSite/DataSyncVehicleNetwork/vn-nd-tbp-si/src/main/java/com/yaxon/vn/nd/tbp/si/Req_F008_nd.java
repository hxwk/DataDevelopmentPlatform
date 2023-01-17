package com.yaxon.vn.nd.tbp.si;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-29 18:03
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗 下发 查询锁车相关状态
 */
public class Req_F008_nd extends JtsReqMsg{
    public static final String _id = "jts.F008.nd";

    @Override
    public String id() {
        return "jts.F008.nd";
    }
    /*车牌号LPN*/
    private String lpn;

    /*SEED*/
    private String seed;

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }


    @Override
    public String toString() {
        return "Req_F008_nd{" + super.toString() +
                ",lpn=" + lpn +
                ",seed="+seed+
                '}';
    }
}