package com.dfssi.dataplatform.ide.service.bi.converters.bar;


import com.dfssi.dataplatform.ide.service.bi.converters.DataConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import com.google.common.collect.ArrayListMultimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 19:05
 */
public class BarConverter extends DataConverter {
    private final Logger logger = LogManager.getLogger(BarConverter.class);

    public BarConverter(DataBaseConf conf) {
        super(conf);
    }

    @Override
    public Object convet(Iterable<Map<String, Object>> records) {
        ArrayListMultimap<String, Object> res = ArrayListMultimap.create();
        records.forEach(record ->{
            record.forEach((key, value) ->{
                res.put(key, value);
            });
        });

        return res.asMap();
    }
}
