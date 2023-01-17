package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-01 11:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * 终端控制
 */
public class Req_8105 extends JtsReqMsg {

    @Override
    public String id() { return "jts.8105"; }
    private String id;
    private String sim;
    private Byte commandType; //命令字
    private String commandParam; //命令参数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public Byte getCommandType() {
        return commandType;
    }

    public void setCommandType(Byte commandType) {
        this.commandType = commandType;
    }

    public String getCommandParam() {
        return commandParam;
    }

    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }

    @Override
    public String toString() {
        return "Req_8105{" + super.toString() +
                ", commandWord=" + commandType +
                ", sim=" + sim +
                ", commandParam=" + commandParam  +
                '}';
    }
}
