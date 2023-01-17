package com.dfssi.dataplatform.external.annotation;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.dfssi.dataplatform.external.model.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
public class DataSourceConfiguration {

	@Value("${spring.datasource.postgresql.driver-class-name}")
	private String gpDriverClassName;

	@Value("${spring.datasource.postgresql.url}")
	private String gpUrl;

	@Value("${spring.datasource.postgresql.username}")
	private String gpUsername;

	@Value("${spring.datasource.postgresql.password}")
	private String gpPassword;

	private AtomikosDataSourceBean gpDatasource;

	@Bean(name = "gpDatasource")
	public DataSource postgresDataSource() {
		if(gpDatasource == null) {
			DruidXADataSource dataSource = new DruidXADataSource();
			dataSource.setDriverClassName(gpDriverClassName);
			dataSource.setUrl(gpUrl);
			dataSource.setUsername(gpUsername);
			dataSource.setPassword(gpPassword);
			dataSource.setMaxActive(11);

			gpDatasource = new AtomikosDataSourceBean();
			gpDatasource.setXaDataSource(dataSource);
			gpDatasource.setMaxPoolSize(40);
			gpDatasource.setUniqueResourceName("gpDatasource");
		}
		return gpDatasource;
	}


	@Bean(name = "dynamicDS")
	public DataSource dataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		// 默认数据源
		dynamicDataSource.setDefaultTargetDataSource(postgresDataSource());
		// 配置多数l据源
		Map<Object, Object> dsMap = new HashMap<Object, Object>();
		dsMap.put("gpDatasource", postgresDataSource());

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
