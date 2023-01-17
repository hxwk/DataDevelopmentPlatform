package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-01 14:42
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;

/**
 * 终端注册应答
 */
public class Res_8100 extends JtsResMsg {
    public static final byte RE_VE_REGISTERED = 0x11;  //车辆已被注册
    public static final byte RE_NO_VE = 0x12; //数据库中无该车辆
    public static final byte RE_TE_REGISTERED = 0x13; //终端已被注册
    public static final byte RE_NO_TE = 0x14; //数据库中无该终端
    public static final byte RE_NO_SIM = 0x15; //SIM不存在
    public static final byte RE_SIM_USED = 0x16; //SIM已经使用

    @Override
    public String id() { return "jts.8100"; }

    private String authCode; //鉴权码

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    
    public String getRcMsg(byte rc) {
        String rcMsg = "";
        switch (rc) {
            case Res_8100.RE_VE_REGISTERED:
                rcMsg = "车辆已被注册";
                break;
            case Res_8100.RE_NO_VE:
                rcMsg = "数据库中无该车辆";
                break;
            case Res_8100.RE_TE_REGISTERED:
                rcMsg = "终端已被注册";
                break;
            case Res_8100.RE_NO_TE:
                rcMsg = "数据库中无该终端";
                break;
            case Res_8100.RE_NO_SIM:
                rcMsg = "SIM不存在";
                break;
            case Res_8100.RE_SIM_USED:
                rcMsg = "SIM已经使用";
                break;
            default:
                rcMsg = "未知异常";
                break;
        }
        return rcMsg;
    }

    @Override
    public String toString() {
        return "Res_8100{" + super.toString() +
                ", authCode='" + authCode + '\'' +
                '}';
    }
}
