package com.dfssi.dataplatform.manager.monitor.mybatis;

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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"com.dfssi.dataplatform.manager.monitor"}, sqlSessionTemplateRef =
        "monitorMybatisSqlSessionTemplate")
@EnableConfigurationProperties(MonitorMybatisProps.class)
public class MonitorMybatisConfig {

    @Autowired
    private MonitorMybatisProps monitorMybatisProps;

    @Bean(name = "monitorMybatisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.monitor")
    @Qualifier("monitorMybatisDataSource")
    public DataSource mybatisMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "monitorMybatisSqlSessionFactory")
    @Qualifier("monitorMybatisSqlSessionFactory")
    public SqlSessionFactory mybatisMasterSqlSessionFactory(@Qualifier("monitorMybatisDataSource") DataSource
                                                                    dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setVfs(SpringBootVFS.class);
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(monitorMybatisProps.getTypeAliasesPackage());
        bean.setMapperLocations(buildResources());

        SqlSessionFactory sqlSessionFactory = bean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactory;
    }

    private Resource[] buildResources() throws IOException {
        List<Resource> results = new ArrayList<Resource>();
        String mapperLoc = monitorMybatisProps.getMapperLocations();
        String[] mapperLocs = mapperLoc.split(";");
        for (String mapper : mapperLocs) {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapper);
            results.addAll(Arrays.asList(resources));
        }

        return results.toArray(new Resource[0]);
    }

    @Bean(name = "monitorMybatisTransactionManager")
    @Qualifier("monitor")
    public DataSourceTransactionManager mybatisMasterTransactionManager(@Qualifier("monitorMybatisDataSource")
                                                                                    DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "monitorMybatisSqlSessionTemplate")
    @Qualifier("monitorMybatisSqlSessionTemplate")
    public SqlSessionTemplate monitorMybatisSqlSessionTemplate(@Qualifier("monitorMybatisSqlSessionFactory")
                                                                           SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
