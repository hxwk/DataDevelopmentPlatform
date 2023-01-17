package com.dfssi.dataplatform.ide.service.bi.converters;

import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/9 13:06
 */
public abstract class DataConverter {
    private final Logger logger = LogManager.getLogger(DataConverter.class);

    protected DataBaseConf conf;

    public DataConverter(DataBaseConf conf){
        this.conf = conf;
    }

    public abstract Object convet(Iterable<Map<String, Object>> records);
}
