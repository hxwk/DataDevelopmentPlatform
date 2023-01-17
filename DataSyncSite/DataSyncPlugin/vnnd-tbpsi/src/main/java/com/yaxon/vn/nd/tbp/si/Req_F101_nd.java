package com.yaxon.vn.nd.tbp.si;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-26 15:33
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗定义的锁车控制结果的异步应答
 */
public class Req_F101_nd extends JtsReqMsg{

    public static final String _id = "jts.F101.nd";

    @Override
    public String id() {return "jts.F101.nd";}
    private Integer instruction;//命令字
    private Integer result;//控制结果

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Req_F101_nd{" + super.toString() +
                ",instruction=" + instruction +
                ", result=" + result +
                '}';
    }
}
