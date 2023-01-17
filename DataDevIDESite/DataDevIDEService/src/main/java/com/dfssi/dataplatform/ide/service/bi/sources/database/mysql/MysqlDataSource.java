package com.dfssi.dataplatform.ide.service.bi.sources.database.mysql;

import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 11:35
 */
public class MysqlDataSource extends DataBaseSource {
    private final Logger logger = LogManager.getLogger(MysqlDataSource.class);

    private MysqlConf mysqlConf;

    public MysqlDataSource(Map<String, String> config) {
        this(new MysqlConf(config));
    }

    public MysqlDataSource(MysqlConf conf) {
        super(conf);
        this.mysqlConf = conf;
    }
}
