package com.dfssi.dataplatform.ide.service.bi.converters.scatter;


import com.dfssi.dataplatform.ide.service.bi.converters.DataConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 19:04
 */
public class ScatterConverter extends DataConverter {
    private final Logger logger = LogManager.getLogger(ScatterConverter.class);

    public ScatterConverter(DataBaseConf conf) {
        super(conf);
    }

    @Override
    public Object convet(Iterable<Map<String, Object>> records) {
        List<Object[]> res = Lists.newArrayList();
        records.forEach(record ->{
            List<String> columns = conf.getColumns();
            int size = columns.size();
            Object[] a = new Object[size];
            for(int i = 0; i < size; i++){
                a[i] = record.get(columns.get(i));
            }
            res.add(a);
        });

        return res;
    }
}
