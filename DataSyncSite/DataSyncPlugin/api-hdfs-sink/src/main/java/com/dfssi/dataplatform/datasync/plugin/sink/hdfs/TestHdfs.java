package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;

import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleException;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by jian on 2017/12/8.
 */
public class TestHdfs {
    private static SSIHDFSEventSink sink;
    private static String testPath = StringUtils.EMPTY;
    private static final Logger logger = LoggerFactory
            .getLogger(SSIHDFSEventSink.class);

    static {
        System.setProperty("java.security.krb5.realm", "flume");
        System.setProperty("java.security.krb5.kdc", "blah");
        System.setProperty("HADOOP_USER_NAME","kangj");
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("file.separator"));
        testPath = "hdfs://172.16.1.210:8020/tmp/tcp_flume."
                + Calendar.getInstance().getTimeInMillis() + "."
                + Thread.currentThread().getId();

        sink = new SSIHDFSEventSink();
        sink.setName("SSIHDFSEventSink-" + UUID.randomUUID().toString());
        dirCleanup();
        try {

            testSimpleAppend();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (LifecycleException e) {
            e.printStackTrace();
        } catch (EventDeliveryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dirCleanup() {
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            Path dirPath = new Path(testPath);
            if (fs.exists(dirPath)) {
                fs.delete(dirPath, true);
            }
        } catch (IOException eIO) {
            logger.warn("IO Error in cleanup", eIO);
        }
    }

    public static void testSimpleAppend() throws InterruptedException,
            LifecycleException, EventDeliveryException, IOException {

        logger.debug("Starting...");
        final String fileName = "tcp-sink-hdfs";
        final long rollCount = 100;
        final long batchSize = 100;
        final int rollSize =10240;
        final int rollInterval=60;
        final String callTimeout="1000000";
        String newPath = testPath;
        int totalEvents = 0;
        int i = 1, j = 1;

        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);

        Context context = new Context();

        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.rollSize",String.valueOf(rollSize));
        context.put("hdfs.rollInterval",String.valueOf(rollInterval));
        context.put("hdfs.callTimeout",callTimeout);
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType","DataStream");
        context.put("hdfs.writeFormat","Text");


        Configurables.configure(sink, context);

        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);

        sink.setChannel(channel);
        sink.start();

        List<String> bodies = Lists.newArrayList();

        Transaction txn = channel.getTransaction();
        txn.begin();
        Event event1 = new SimpleEvent();
        event1.getHeaders().put("testTCPHeader","test");
        String body1 = "hello world";

        event1.setBody(body1.getBytes());

        bodies.add(body1);
        channel.put(event1);
        txn.commit();
        txn.close();

        sink.process();
        sink.stop();
    }
}
