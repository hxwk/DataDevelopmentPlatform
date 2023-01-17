package com.dfssi.dataplatform.analysis.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

//@Configuration
//@MapperScan(basePackages = "com.dfssi.dataplatform.metadata", sqlSessionTemplateRef =
//        "metaDataMybatisSqlSessionTemplate")
//@EnableConfigurationProperties(MetaDataMybatisProps.class)
public class MetaDataMybatisConfig {

    @Autowired
    private MetaDataMybatisProps metaDataMybatisProps;

    @Bean(name = "metaDataMybatisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.metadata")
    @Qualifier("metaDataMybatisDataSource")
    public DataSource mybatisMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "metaDataMybatisSqlSessionFactory")
    @Qualifier("metaDataMybatisSqlSessionFactory")
    public SqlSessionFactory mybatisMasterSqlSessionFactory(@Qualifier("metaDataMybatisDataSource") DataSource
                                                                        dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setVfs(SpringBootVFS.class);
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(metaDataMybatisProps.getTypeAliasesPackage());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(metaDataMybatisProps
                .getMapperLocations()));

//        Properties properties = new Properties();
//        properties.setProperty("reasonable", "false");
//        properties.setProperty("pageSizeZero", "true");
//        PageInterceptor pageInterceptor = new PageInterceptor();
//        pageInterceptor.setProperties(properties);
//
//        bean.setPlugins(new Interceptor[]{pageInterceptor});

        SqlSessionFactory sqlSessionFactory = bean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactory;
    }

    @Bean(name = "metaDataMybatisTransactionManager")
    @Qualifier("metadata")
    public DataSourceTransactionManager mybatisMasterTransactionManager(@Qualifier("metaDataMybatisDataSource")
                                                                                    DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "metaDataMybatisSqlSessionTemplate")
    @Qualifier("metaDataMybatisSqlSessionTemplate")
    public SqlSessionTemplate analysisMybatisSqlSessionTemplate(@Qualifier("metaDataMybatisSqlSessionFactory")
                                                                            SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
