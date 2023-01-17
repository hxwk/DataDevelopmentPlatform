package com.dfssi.spark.hdfs;

import com.dfssi.spark.filter.HdfsFileFilter;
import org.apache.hadoop.fs.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/11/5 17:08
 */
public class HdfsFileCreatTimeRangeFilter extends HdfsFileFilter {

    private static final Logger LOG = LoggerFactory.getLogger(HdfsFileCreatTimeRangeFilter.class);

    private long createTimeLower;
    private long createTimeUpper;

    public HdfsFileCreatTimeRangeFilter(long createTimeLower){
       this(createTimeLower, Long.MAX_VALUE);
    }

    public HdfsFileCreatTimeRangeFilter(long createTimeLower,
                                        long createTimeUpper){
        this.createTimeLower = createTimeLower;
        if(createTimeUpper <= 0) createTimeUpper = Long.MAX_VALUE;
        this.createTimeUpper = createTimeUpper;
    }

    public boolean filter(FileStatus status) {
        long time = status.getModificationTime();
        return (createTimeUpper >= time && createTimeLower <= time);
    }
}
