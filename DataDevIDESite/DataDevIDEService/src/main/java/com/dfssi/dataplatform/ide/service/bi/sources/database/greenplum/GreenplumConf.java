package com.dfssi.dataplatform.ide.service.bi.sources.database.greenplum;


import com.dfssi.common.databases.DBType;
import com.dfssi.dataplatform.ide.service.bi.BIContext;
import com.dfssi.dataplatform.ide.service.bi.ConnectEntity;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Map;

/**
 * Description:
 *    greenplum的获取连接相关的配置
 * @author LiXiaoCong
 * @version 2018/1/11 11:38
 */
public class GreenplumConf extends DataBaseConf {
    private final Logger logger = LogManager.getLogger(GreenplumConf.class);

    private DBType postgresql;

    public GreenplumConf(Map<String, String> config) {
        super(config);
    }

    @Override
    protected void init() {
        super.init();
        this.postgresql = DBType.POSTGRESQL;
        if(!server.contains(":")){
            server = String.format("%s:%s", server, postgresql.getDefaultPort());
        }
    }

    @Override
    public Connection createConnnection() {
        String url = String.format("jdbc:postgresql://%s/%s", server, database);
        ConnectEntity connnection = BIContext.get().createConnnection(url, postgresql.getDriver(), user, password);
        return connnection.getConnection();
    }

    @Override
    public DBType getDataBaseType() {
        return postgresql;
    }
}
