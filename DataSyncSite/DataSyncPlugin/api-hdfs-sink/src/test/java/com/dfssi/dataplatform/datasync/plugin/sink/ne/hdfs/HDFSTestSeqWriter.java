package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;


import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSSequenceFile;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;

import java.io.IOException;

public class HDFSTestSeqWriter extends HDFSSequenceFile {
    protected volatile boolean closed;
    protected volatile boolean opened;

    private int openCount = 0;

    HDFSTestSeqWriter(int openCount) {
        this.openCount = openCount;
    }

    @Override
    public void open(String filePath, CompressionCodec codeC, CompressionType compType)
            throws IOException {
        super.open(filePath, codeC, compType);
        if (closed) {
            opened = true;
        }
    }

    @Override
    public void append(Event e) throws IOException {

        if (e.getHeaders().containsKey("fault")) {
            throw new IOException("Injected fault");
        } else if (e.getHeaders().containsKey("fault-once")) {
            e.getHeaders().remove("fault-once");
            throw new IOException("Injected fault");
        } else if (e.getHeaders().containsKey("fault-until-reopen")) {
            // opening first time.
            if (openCount == 1) {
                throw new IOException("Injected fault-until-reopen");
            }
        } else if (e.getHeaders().containsKey("slow")) {
            long waitTime = Long.parseLong(e.getHeaders().get("slow"));
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException eT) {
                throw new IOException("append interrupted", eT);
            }
        }

        super.append(e);
    }

    @Override
    public void close() throws IOException {
        closed = true;
        super.close();
    }
}