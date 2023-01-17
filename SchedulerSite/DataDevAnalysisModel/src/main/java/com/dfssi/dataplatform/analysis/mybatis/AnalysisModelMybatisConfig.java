package com.dfssi.dataplatform.analysis.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"com.dfssi.dataplatform.analysis", "com.dfssi.dataplatform.metadata"},
        sqlSessionTemplateRef = "analysisMybatisSqlSessionTemplate")
@EnableConfigurationProperties(AnalysisModelMybatisProps.class)
public class AnalysisModelMybatisConfig {

    @Autowired
    private AnalysisModelMybatisProps analysisModelMybatisProps;

    @Bean(name = "analysisMybatisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.analysis")
    @Qualifier("analysisMybatisDataSource")
    public DataSource mybatisMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "analysisMybatisSqlSessionFactory")
    @Qualifier("analysisMybatisSqlSessionFactory")
    public SqlSessionFactory mybatisMasterSqlSessionFactory(@Qualifier("analysisMybatisDataSource") DataSource
                                                                        dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setVfs(SpringBootVFS.class);
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(analysisModelMybatisProps.getTypeAliasesPackage());
        bean.setMapperLocations(buildResources());

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

    private Resource[] buildResources() throws IOException {
        List<Resource> results = new ArrayList<Resource>();
        String mapperLoc = analysisModelMybatisProps.getMapperLocations();
        String[] mappperLocs = mapperLoc.split(";");
        for (String mapper : mappperLocs) {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapper);
            results.addAll(Arrays.asList(resources));
        }

        return results.toArray(new Resource[0]);
    }

    @Bean(name = "analysisMybatisTransactionManager")
    @Qualifier("analysis")
    public DataSourceTransactionManager mybatisMasterTransactionManager(@Qualifier("analysisMybatisDataSource")
                                                                                    DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "analysisMybatisSqlSessionTemplate")
    @Qualifier("analysisMybatisSqlSessionTemplate")
    public SqlSessionTemplate analysisMybatisSqlSessionTemplate(@Qualifier("analysisMybatisSqlSessionFactory")
                                                                            SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
