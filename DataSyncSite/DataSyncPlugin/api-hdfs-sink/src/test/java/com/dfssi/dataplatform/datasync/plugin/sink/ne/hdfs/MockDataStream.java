package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;

import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSDataStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

class MockDataStream extends HDFSDataStream {
    private final FileSystem fs;

    MockDataStream(FileSystem fs) {
        this.fs = fs;
    }

    @Override
    protected FileSystem getDfs(Configuration conf, Path dstPath) throws IOException {
        return fs;
    }

}