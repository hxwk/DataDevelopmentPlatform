package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;


import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSWriter;
import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSWriterFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class HDFSTestWriterFactory extends HDFSWriterFactory {
    static final String TestSequenceFileType = "SequenceFile";
    static final String BadDataStreamType = "DataStream";

    // so we can get a handle to this one in our test.
    AtomicInteger openCount = new AtomicInteger(0);

    @Override
    public HDFSWriter getWriter(String fileType) throws IOException {
        if (fileType == TestSequenceFileType) {
            return new HDFSTestSeqWriter(openCount.incrementAndGet());
        } else if (fileType == BadDataStreamType) {
            return new HDFSBadDataStream();
        } else {
            throw new IOException("File type " + fileType + " not supported");
        }
    }
}
