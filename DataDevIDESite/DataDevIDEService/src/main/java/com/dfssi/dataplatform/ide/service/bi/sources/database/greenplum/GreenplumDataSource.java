package com.dfssi.dataplatform.ide.service.bi.sources.database.greenplum;

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
public class GreenplumDataSource extends DataBaseSource {
    private final Logger logger = LogManager.getLogger(GreenplumDataSource.class);

    private GreenplumConf greenplumConf;

    public GreenplumDataSource(Map<String, String> config) {
        this(new GreenplumConf(config));
    }

    public GreenplumDataSource(GreenplumConf conf) {
        super(conf);
        this.greenplumConf = conf;
    }
}
