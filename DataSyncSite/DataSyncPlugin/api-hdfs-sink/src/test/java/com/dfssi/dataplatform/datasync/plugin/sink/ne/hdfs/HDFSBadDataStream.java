package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;

import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSDataStream;
import com.dfssi.dataplatform.datasync.plugin.sink.hdfs.HDFSSequenceFile;
import com.dfssi.dataplatform.datasync.flume.agent.Event;

import java.io.IOException;

public class HDFSBadDataStream extends HDFSDataStream {
    public class HDFSBadSeqWriter extends HDFSSequenceFile {
        @Override
        public void append(Event e) throws IOException {

            if (e.getHeaders().containsKey("fault")) {
                throw new IOException("Injected fault");
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

    }

}