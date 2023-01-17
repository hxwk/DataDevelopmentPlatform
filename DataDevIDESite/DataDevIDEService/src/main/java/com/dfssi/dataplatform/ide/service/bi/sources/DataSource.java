package com.dfssi.dataplatform.ide.service.bi.sources;

import com.dfssi.resources.ConfigDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/9 13:03
 */
public abstract class DataSource {
    private final Logger logger = LogManager.getLogger(DataSource.class);

    protected ConfigDetail configDetail;

    public DataSource(ConfigDetail configDetail){
        this.configDetail = configDetail;
        init();
    }

    public void init(){}

    public abstract Object readData();


}
