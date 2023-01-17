package com.dfssi.dataplatform.ide.service.bi.converters.pie;


import com.dfssi.dataplatform.ide.service.bi.converters.DataConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 19:06
 */
public class PieConverter extends DataConverter {
    private final Logger logger = LogManager.getLogger(PieConverter.class);

    public PieConverter(DataBaseConf conf) {
        super(conf);
    }

    @Override
    public Object convet(Iterable<Map<String, Object>> records) {
        return records;
    }
}
