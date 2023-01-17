package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;


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
        System.out.println( Math.round(21.5));
    }
    public static boolean test(Object obj){
        if(obj instanceof  Integer){
            return true;
        }
        return false;
    }
}
