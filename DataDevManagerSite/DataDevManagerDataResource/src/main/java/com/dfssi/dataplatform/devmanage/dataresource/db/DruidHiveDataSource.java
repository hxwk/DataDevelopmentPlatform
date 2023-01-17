package com.dfssi.dataplatform.devmanage.dataresource.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.dfssi.dataplatform.devmanage.dataresource.Config.HiveConfig;
import com.dfssi.dataplatform.devmanage.dataresource.util.Constants;

/**
 * Created by Hannibal on 2018-01-11.
 */
public class DruidHiveDataSource {

    private DruidDataSource dataSource;

    private static DruidHiveDataSource instance = null;

    public DruidHiveDataSource(HiveConfig hiveConfig) {
        init(hiveConfig);
    }

    private void init(HiveConfig hiveConfig) {
        dataSource = new DruidDataSource();
        dataSource.setUrl(hiveConfig.getUrl());
        dataSource.setDriverClassName(hiveConfig.getDriverclassname());
        dataSource.setUsername(hiveConfig.getUsername());
        dataSource.setPassword(hiveConfig.getPassword());
//        dataSource.setMaxActive(Integer.parseInt(env.getProperty("hive.maxactive")));
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(180);
        Constants.HIVE_DBNAME = hiveConfig.getDbname();
        Constants.VALUE_DRIVER = hiveConfig.getDriverclassname();
        Constants.VALUE_URL = hiveConfig.getUrl();
    }

    public synchronized static DruidHiveDataSource getInstance(HiveConfig hiveConfig){
        if (null == instance) {
            instance = new DruidHiveDataSource(hiveConfig);
        }

        return instance;
    }

    public DruidDataSource getDataSource() {
        return dataSource;
    }
}
