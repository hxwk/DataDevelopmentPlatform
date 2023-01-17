package com.dfssi.dataplatform.ide.service.bi.converters.heat;


import com.dfssi.dataplatform.ide.service.bi.converters.DataConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 19:07
 */
public class HeatConverter extends DataConverter {
    private final Logger logger = LogManager.getLogger(HeatConverter.class);

    public HeatConverter(DataBaseConf conf) {
        super(conf);
    }

    @Override
    public Object convet(Iterable<Map<String, Object>> records) {
        return records;
    }
}
