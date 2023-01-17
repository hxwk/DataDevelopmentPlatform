package com.yaxon.vn.nd.tas.net.tcp;

import com.google.common.util.concurrent.SettableFuture;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;

/**
 * Author: 程行荣
 * Time: 2013-11-16 22:52
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class InvokeStubItem {
    public long timestamp;
    public SettableFuture<ProtoMsg> future;

    public InvokeStubItem(long timestamp, SettableFuture<ProtoMsg> future) {
        this.timestamp = timestamp;
        this.future = future;
    }
}
