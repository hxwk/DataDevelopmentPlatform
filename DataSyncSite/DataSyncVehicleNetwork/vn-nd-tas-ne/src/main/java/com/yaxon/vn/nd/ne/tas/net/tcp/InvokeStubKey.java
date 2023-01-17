package com.yaxon.vn.nd.ne.tas.net.tcp;

import com.yaxon.vndp.common.util.CodecUtils;

/**
 * Author: 程行荣
 * Time: 2013-11-16 22:51
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class InvokeStubKey {
    public String vin;
    public short msgId;

    public InvokeStubKey() {
    }

    public InvokeStubKey(String vin, short msgId) {
        this.vin = vin;
        this.msgId = msgId;
    }

    public String getUniqueKey() {
        return this.vin + "_" + this.msgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvokeStubKey that = (InvokeStubKey) o;

        if (msgId != that.msgId) return false;
        if (vin != that.vin) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (Long.valueOf(vin) ^ (Long.valueOf(vin) >>> 32));
        result = 31 * result + (int) msgId;
        return result;
    }

    @Override
    public String toString() {
        return "InvokeStubKey{" +
                "vin=" + vin +
                ", msgId=0x" + CodecUtils.shortToHex(msgId) +
                ", UniqueKey " + getUniqueKey() +
                '}';
    }
}
