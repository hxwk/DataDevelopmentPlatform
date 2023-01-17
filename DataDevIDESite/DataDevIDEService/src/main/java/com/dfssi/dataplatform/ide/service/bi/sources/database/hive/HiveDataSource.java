package com.dfssi.dataplatform.ide.service.bi.sources.database.hive;

import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/9 13:08
 */
public class HiveDataSource extends DataBaseSource {
    private final Logger logger = LogManager.getLogger(HiveDataSource.class);

    private HiveDataSourceConf hiveDataSourceConf;

    public HiveDataSource(HiveDataSourceConf hiveDataSourceConf) {
        super(hiveDataSourceConf);
        this.hiveDataSourceConf = hiveDataSourceConf;
    }

    public HiveDataSource(Map<String, String> config) {
        this(new HiveDataSourceConf(config));
    }

}
