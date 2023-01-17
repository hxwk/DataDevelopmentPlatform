package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;

import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.annotations.InterfaceAudience;
import com.dfssi.dataplatform.datasync.flume.agent.annotations.InterfaceStability;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.SequenceFile.CompressionType;

import java.io.IOException;

/**
 * Created by jian on 2017/12/4.
 */
@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface HDFSWriter extends Configurable {

    public void open(String filePath) throws IOException;

    public void open(String filePath, CompressionCodec codec,
                     CompressionType cType) throws IOException;

    public void append(Event e) throws IOException;

    public void sync() throws IOException;

    public void close() throws IOException;

    public boolean isUnderReplicated();

}