package com.yaxon.vn.nd.tas.net.tcp;

import com.yaxon.vndp.common.util.CodecUtils;

/**
 * Author: 程行荣
 * Time: 2013-11-16 22:51
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class InvokeStubKey {
    public String sim;
    public short msgId;
    public short sn;

    public InvokeStubKey() {
    }

    public InvokeStubKey(String sim, short msgId, short sn) {
        this.sim = sim;
        this.msgId = msgId;
        this.sn = sn;
    }

    public String getUniqueKey() {
        return this.sim + "_" + this.msgId + "_" + this.sn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvokeStubKey that = (InvokeStubKey) o;

        if (msgId != that.msgId) return false;
        if (sim != that.sim) return false;
        if (sn != that.sn) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (Long.valueOf(sim) ^ (Long.valueOf(sim) >>> 32));
        result = 31 * result + (int) msgId;
        result = 31 * result + sn;
        return result;
    }

    @Override
    public String toString() {
        return "InvokeStubKey{" +
                "sim=" + sim +
                ", msgId=0x" + CodecUtils.shortToHex(msgId) +
                ", UniqueKey " + getUniqueKey() +
                ", sn=" + (sn&0xFFFF) +
                '}';
    }
}
