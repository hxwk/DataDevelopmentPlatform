package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:22
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 人工确认报警消息
 */
public class Req_8203 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8203";}

    private Short alarmMsgSerialNo; //报警消息流水号
    private Integer alarmType; //人工确认报警类型

    public Short getAlarmMsgSerialNo() {
        return alarmMsgSerialNo;
    }

    public void setAlarmMsgSerialNo(Short alarmMsgSerialNo) {
        this.alarmMsgSerialNo = alarmMsgSerialNo;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    @Override
    public String toString() {
        return "Req_8203{" + super.toString() +
                ", alarmMsgSerialNo=" + alarmMsgSerialNo +
                ", alarmType=" + alarmType +
                '}';
    }
}

