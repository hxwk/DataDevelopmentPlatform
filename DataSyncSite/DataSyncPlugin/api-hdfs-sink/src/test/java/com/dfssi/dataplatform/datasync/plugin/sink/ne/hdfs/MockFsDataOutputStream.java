package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockFsDataOutputStream extends FSDataOutputStream {

    private static final Logger logger =
            LoggerFactory.getLogger(MockFsDataOutputStream.class);

    boolean closeSucceed;

    public MockFsDataOutputStream(FSDataOutputStream wrapMe, boolean closeSucceed)
            throws IOException {
        super(wrapMe.getWrappedStream(), null);
        this.closeSucceed = closeSucceed;
    }

    @Override
    public void close() throws IOException {
        logger.info("Close Succeeded - " + closeSucceed);
        if (closeSucceed) {
            logger.info("closing file");
            super.close();
        } else {
            throw new IOException("MockIOException");
        }
    }
}