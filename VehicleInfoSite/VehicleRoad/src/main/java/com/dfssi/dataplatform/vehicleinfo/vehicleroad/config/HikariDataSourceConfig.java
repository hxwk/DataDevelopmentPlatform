package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Setter
@Getter
@ToString(exclude = {"dataSourceBean"})
@Slf4j
public class HikariDataSourceConfig {

    private String driverClassName;
    private String jdbcUrl;
    private volatile String username;
    private volatile String password;
    private volatile String poolName;
    private volatile int minimumIdle = 5;
    private volatile int maximumPoolSize = 15;
    private volatile boolean autoCommit = true;
    private volatile int idleTimeout = 30000;
    private volatile int maxLifetime = 1800000;
    private volatile int connectionTimeout = 30000;
    private volatile String connectionTestQuery = "SELECT 1";


    private volatile HikariDataSource dataSourceBean;

    public DataSource getDataSource(){
        if(dataSourceBean == null) {
            synchronized (HikariDataSourceConfig.class) {
                if(dataSourceBean == null) {

                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setDriverClassName(driverClassName);
                    hikariConfig.setJdbcUrl(jdbcUrl);
                    hikariConfig.setUsername(username);
                    hikariConfig.setPassword(password);
                    hikariConfig.setPoolName(poolName);
                    hikariConfig.setMinimumIdle(minimumIdle);
                    hikariConfig.setMaximumPoolSize(maximumPoolSize);
                    hikariConfig.setAutoCommit(autoCommit);
                    hikariConfig.setIdleTimeout(idleTimeout);
                    hikariConfig.setMaxLifetime(maxLifetime);
                    hikariConfig.setConnectionTimeout(connectionTimeout);
                    hikariConfig.setConnectionTestQuery(connectionTestQuery);

                    dataSourceBean = new HikariDataSource(hikariConfig);

                    log.info(toString());
                }
            }
        }
        return dataSourceBean;
    }



}