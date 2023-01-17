package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by jian on 2017/12/4.
 */
public class test  {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(test.class);
        System.out.println("current time"+String.valueOf(System.currentTimeMillis()));
        logger.info("test calendar is "+Calendar.getInstance().getTimeInMillis());

        Configuration conf = new Configuration();
        FileSystem fs = null;
        conf.addResource(TestHDFSEventSink.class.getClassLoader().getResource("core-site.xml"));
        conf.addResource(TestHDFSEventSink.class.getClassLoader().getResource("hdfs-site.xml"));
        conf.set("fs.defaultFS", "hdfs://172.16.1.210:8020/");
        conf.set("hadoop.job.ugi", "cloudera");
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            logger.error("hdfs filesystem get error, please modify it......");
        }
        Path source = new Path("d:/test/data/ds-oil-2017113017974.dat");
        Path dst = new Path("/tmp/kangj/test/");
        try {
           // fs.mkdirs(dst);

        } catch (Exception e) {
            logger.error("hdfs make directory error, please modify it......");
        }
        try {
            //fs.copyFromLocalFile(source,dst);
        } catch (Exception e) {
            logger.error("hdfs filesystem copy from local file error, please modify it......");
        }
    }
}
