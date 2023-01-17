package com.dfssi.dataplatform.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.dfssi.dataplatform.aop.DynamicDataSource;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableTransactionManagement
@Order(1)
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfiguration {

	private final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    private DruidXADataSourceConfig mysql;

    private DruidXADataSourceConfig postgresql;

	@Bean(name = "mysqlDatasource")
	public DataSource mysqlDataSource() {
		return mysql.getDataSource();
	}

	@Bean(name = "gpDatasource")
	@Primary
	public DataSource postgresDataSource() {
		return postgresql.getDataSource();
	}


	@Bean(name = "dynamicDS")
	public DataSource dataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		// 默认数据源
		DataSource defaultTargetDataSource = postgresDataSource();
		dynamicDataSource.setDefaultTargetDataSource(defaultTargetDataSource);
		// 配置多数l据源
		Map<Object, Object> dsMap = new HashMap<>(2);
		dsMap.put("mysqlDataSource", mysqlDataSource());
		dsMap.put("gpDatasource", defaultTargetDataSource);

		dynamicDataSource.setTargetDataSources(dsMap);
		return dynamicDataSource;
	}

	// 提供SqlSeesion
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("dynamicDS") DataSource dynamicDS) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dynamicDS);
		sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
		return sqlSessionFactoryBean.getObject();
	}

	// JTA transaction
	@Bean(name = "userTransaction")
	public UserTransaction userTransaction() throws Throwable {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(300);
		return userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() throws Throwable {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(true);
		return userTransactionManager;
	}

	@Bean(name = "transactionManager")
	@DependsOn({ "userTransaction", "atomikosTransactionManager" })
	public JtaTransactionManager transactionManager() throws Throwable {
		JtaTransactionManager manager = new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
		return manager;
	}

}
