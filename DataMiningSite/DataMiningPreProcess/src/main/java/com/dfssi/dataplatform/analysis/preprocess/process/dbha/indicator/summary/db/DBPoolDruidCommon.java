package com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.config.XdiamondApplication;
import io.github.xdiamond.client.XDiamondConfig;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBPoolDruidCommon {

    private static Logger LOG = Logger.getLogger(DBPoolDruidCommon.class);

    private static DBPoolDruidCommon instance;

    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    static{
        instance = new DBPoolDruidCommon();
    }

    private DBPoolDruidCommon() {
        try {

            XDiamondConfig commonConfig = XdiamondApplication.getInstance().getxDiamondConfig();
            String[] dataSourceTypes = commonConfig.getProperty("datasource.ids").split(",");

            //通过Map方式设置Druid参数
            for (Object dataSourceType : dataSourceTypes) {
                String sourceType = String.valueOf(dataSourceType);
                Map<String, String> druidMap=new HashMap<>();
                druidMap.put(DruidDataSourceFactory.PROP_USERNAME, commonConfig.getProperty("db.datasource.username." + sourceType));
                druidMap.put(DruidDataSourceFactory.PROP_PASSWORD, commonConfig.getProperty("db.datasource.password." + sourceType));
                druidMap.put(DruidDataSourceFactory.PROP_URL, commonConfig.getProperty("db.datasource.url." + sourceType));
                druidMap.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, commonConfig.getProperty("db.datasource.jdbcdriver." + sourceType));
                druidMap.put(DruidDataSourceFactory.PROP_MAXACTIVE, commonConfig.getProperty("db.datasource.maxpoolsize." + sourceType));
                druidMap.put(DruidDataSourceFactory.PROP_REMOVEABANDONED, "true");
                druidMap.put(DruidDataSourceFactory.PROP_REMOVEABANDONEDTIMEOUT, "180");

                System.out.println( " druidMap = " + druidMap);
                DataSource dataSource = DruidDataSourceFactory.createDataSource(druidMap);
                System.out.println( " 创建数据源完成 druidMap = " + druidMap);
                dataSourceMap.put(sourceType, dataSource);
            }

        } catch (Exception e) {
            LOG.error(null, e);
        }
    }

    public synchronized static DBPoolDruidCommon getInstance(){
        return instance;
    }

    public Connection getConnection(String dataSourceType) throws SQLException {
        return dataSourceMap.get(dataSourceType).getConnection();
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }
}
