package com.dfssi.dataplatform.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/8/18 14:59
 */
@Setter
@Getter
@ToString(exclude = {"dataSourceBean"})
@Slf4j
public class DruidXADataSourceConfig {

    private volatile int maxLifetime = 0;
    private volatile int reapTimeout = 0;

    private volatile int maxPoolSize = 8;
    private volatile int minPoolSize = 1;

    private volatile int minIdle;

    private volatile boolean asyncInit;

    private volatile String username;
    private volatile String datasourceName;
    private volatile String password;
    private String driverClassName;
    private String jdbcUrl;

    private volatile AtomikosDataSourceBean dataSourceBean;

    public DataSource getDataSource(){
        if(dataSourceBean == null) {
            synchronized (DruidXADataSourceConfig.class) {
                if(dataSourceBean == null) {
                    DruidXADataSource dataSource = new DruidXADataSource();
                    dataSource.setDriverClassName(driverClassName);
                    dataSource.setUrl(jdbcUrl);
                    dataSource.setUsername(username);
                    dataSource.setPassword(password);
                    dataSource.setMaxActive(maxPoolSize + 1);
                   // dataSource.setAsyncInit(asyncInit);

                    dataSourceBean = new AtomikosDataSourceBean();
                    dataSourceBean.setXaDataSource(dataSource);
                    dataSourceBean.setMinPoolSize(minPoolSize);
                    dataSourceBean.setMaxPoolSize(maxPoolSize);
                    dataSourceBean.setReapTimeout(reapTimeout);
                    dataSourceBean.setMaxLifetime(maxLifetime);
                    dataSourceBean.setUniqueResourceName(datasourceName);

                    log.info(toString());
                }
            }
        }
        return dataSourceBean;
    }
}
