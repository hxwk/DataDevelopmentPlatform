package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * oracle
 * Created by jian on 2017/12/11.
 * @author JianKang
 */
@Deprecated
public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static ComboPooledDataSource comboPoolDs = null;
    private static ReentrantLock lock = new ReentrantLock();

    public static void build(String jdbcUrl, String username, String password, int poolSize, int maxPoolSize, int maxIdleTime){
        initializePool(jdbcUrl,username, password, poolSize, maxPoolSize, maxIdleTime);
    }

    private static void initializePool(String jdbcUrl, String username, String password, int poolSize,
                                       int maxPoolSize, int maxIdleTime){
        try {
            lock.lock();
            if(comboPoolDs != null){
                return;
            }
            comboPoolDs = new ComboPooledDataSource();
            comboPoolDs.setUser(username);
            comboPoolDs.setPassword(password);
            comboPoolDs.setInitialPoolSize(poolSize);
            comboPoolDs.setMaxPoolSize(maxPoolSize);
            comboPoolDs.setMaxIdleTime(maxIdleTime);
            comboPoolDs.setJdbcUrl(jdbcUrl);
        }catch (Exception e){
            logger.error("initialize oracle connection pool exception, {}",e.getMessage());
        }finally {
            lock.unlock();
        }
    }

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = comboPoolDs.getConnection();
        }catch (Exception e){
            logger.error("get oracle connection exception,{}", e);
        }
        return connection;
    }

    public static void close(){
        comboPoolDs.close();
    }
}