package com.dfssi.dataplatform.ide.service.bi.sources.database.hive;


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
 *
 * @author LiXiaoCong
 * @version 2018/1/9 13:08
 */
public class HiveDataSourceConf extends DataBaseConf {
    private final Logger logger = LogManager.getLogger(HiveDataSourceConf.class);

    //hive连接
    private DBType hive;

    public HiveDataSourceConf(Map<String, String> config) {
        super(config);
    }

    @Override
    protected void init() {
        super.init();
        this.hive = DBType.HIVE;
        if (!server.contains(":")) {
            server = String.format("%s:%s", server, hive.getDefaultPort());
        }
    }

    @Override
    public DBType getDataBaseType() {
        return DBType.HIVE;
    }

    @Override
    public Connection createConnnection() {
        String url = String.format("jdbc:hive2://%s/%s", server, database);
        ConnectEntity connnection = BIContext.get().createConnnection(url, hive.getDriver(), user, password);
        return connnection.getConnection();
    }

}
