package com.yaxon.vn.nd.tbp.si;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-29 18:03
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗 下发 查询锁车相关状态
 */
public class Req_F003_nd extends JtsReqMsg{

    public static final String _id = "jts.F003.nd";

    @Override
    public String id() {
        return "jts.F003.nd";
    }
    private Integer instruction;//命令字
    /*车牌号LPN*/
    private String lpn;

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }
    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    @Override
    public String toString() {
        return "Req_F003_nd{" + super.toString() +
                ",instruction=" + instruction +
                '}';
    }
}
