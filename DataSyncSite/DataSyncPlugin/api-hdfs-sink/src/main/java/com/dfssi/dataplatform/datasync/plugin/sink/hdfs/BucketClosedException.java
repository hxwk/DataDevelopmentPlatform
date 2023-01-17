package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;

import com.dfssi.dataplatform.datasync.flume.agent.FlumeException;

/**
 * Created by jian on 2017/12/4.
 */
public class BucketClosedException extends FlumeException {

    private static final long serialVersionUID = -4216667125119540357L;

    public BucketClosedException(String msg) {
        super(msg);
    }
}
