package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;


import com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.aop.DynamicDataSource;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableTransactionManagement
@Order(1)
@Data
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class DataSourceConfiguration {

	private final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    private HikariDataSourceConfig mysql;

    private HikariDataSourceConfig postgresql;

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

}
