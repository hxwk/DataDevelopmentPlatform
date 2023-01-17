package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:18
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 终端升级结果通知
 */
public class Res_0108 extends JtsResMsg {
    @Override
    public String id() { return "jts.0108"; }

    private byte updateType; //升级类型
    private byte updateResult; //升级结果

    public byte getUpdateType() {
        return updateType;
    }

    public void setUpdateType(byte updateType) {
        this.updateType = updateType;
    }

    public byte getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(byte updateResult) {
        this.updateResult = updateResult;
    }

    @Override
    public String toString() {
        return "Res_0108{" + super.toString() +
                ", updateType=" + updateType +
                ", updateResult=" + updateResult +
                '}';
    }
}
