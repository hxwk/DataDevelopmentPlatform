package com.dfssi.spark.filter;

import org.apache.hadoop.fs.FileStatus;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/11/5 17:06
 */
public abstract class HdfsFileFilter {

    public abstract boolean filter(FileStatus status);
}
