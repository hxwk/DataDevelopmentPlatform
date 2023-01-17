package com.dfssi.dataplatform.devmanager.analysistask.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.dfssi.dataplatform.devmanager.analysistask.mvc.dao", sqlSessionTemplateRef =
        "mybatisSqlSessionTemplate")
@EnableConfigurationProperties(MybatisProps.class)
public class MybatisConfig {

    @Autowired
    private MybatisProps mybatisProps;

    @Bean(name = "mybatisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mybatisMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mybatisSqlSessionFactory")
    public SqlSessionFactory mybatisMasterSqlSessionFactory(@Qualifier("mybatisDataSource") DataSource
                                                                        dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setVfs(SpringBootVFS.class);
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(mybatisProps.getTypeAliasesPackage());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mybatisProps
                .getMapperLocations()));

        SqlSessionFactory sqlSessionFactory = bean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactory;
    }

    @Bean(name = "mybatisTransactionManager")
    public DataSourceTransactionManager mybatisMasterTransactionManager(@Qualifier("mybatisDataSource")
                                                                                    DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mybatisSqlSessionTemplate")
    public SqlSessionTemplate mybatisSqlSessionTemplate(@Qualifier("mybatisSqlSessionFactory")
                                                                            SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
