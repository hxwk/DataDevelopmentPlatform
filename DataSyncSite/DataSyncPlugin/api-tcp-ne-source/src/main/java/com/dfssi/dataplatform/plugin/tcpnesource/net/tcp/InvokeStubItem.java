package com.dfssi.dataplatform.plugin.tcpnesource.net.tcp;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.google.common.util.concurrent.SettableFuture;


public class InvokeStubItem {
    public long timestamp;
    public SettableFuture<ProtoMsg> future;

    public InvokeStubItem(long timestamp, SettableFuture<ProtoMsg> future) {
        this.timestamp = timestamp;
        this.future = future;
    }
}
