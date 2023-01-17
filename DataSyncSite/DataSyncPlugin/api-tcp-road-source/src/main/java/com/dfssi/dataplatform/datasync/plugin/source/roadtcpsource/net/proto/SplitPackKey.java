package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto;

public class SplitPackKey {
    public String sim;
    public short msgId;
    public int sn;

    public SplitPackKey(String sim, short msgId, int sn) {
        this.sim = sim;
        this.msgId = msgId;
        this.sn = sn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        SplitPackKey that = (SplitPackKey) o;

        if (msgId != that.msgId) {return false;}
        if (!sim.equals(that.sim) ) {return false;}
        if (sn != that.sn){ return false;}

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (Long.valueOf(sim) ^ (Long.valueOf(sim) >>> 32));
        result = 31 * result + (int) msgId;
        result = 31 * result + sn;
        return result;
    }
}
