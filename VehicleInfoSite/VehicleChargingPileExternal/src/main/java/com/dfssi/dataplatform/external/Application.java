package com.dfssi.dataplatform.external;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 9:45
 */
//@SpringBootApplication
//public class Application extends SpringBootServletInitializer {
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return configureApplication(builder);
//    }
//
//    public static void main(String[] args) {
//        configureApplication(new SpringApplicationBuilder()).run(args);
//    }
//
//    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
//        return builder.sources(Application.class);
//    }
//
//}
@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@MapperScan({"com.dfssi.dataplatform.external.chargingPile.dao"})
//@ComponentScan("com.dfssi")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
      //  SpringApplication.run(Application.class);

        SpringApplication springApplicationBuilder = new SpringApplication(Application.class);
//        Properties properties = getProperties();
//        StandardEnvironment environment = new StandardEnvironment();
//        environment.getPropertySources().addLast(new PropertiesPropertySource("micro-service", properties));
//        springApplicationBuilder.setEnvironment(environment);
        springApplicationBuilder.run(args);

    }

    private static Properties getProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        propertiesFactoryBean.setIgnoreResourceNotFound(true);

        //获取jar所在目录
        String parent = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent();
        int index = parent.indexOf(".jar");
        if(index > 0){
            parent = parent.substring(0, index + 4);
            parent = new File(parent).getParent();
        }

        //加载可能存在的自定义的配置文件
        String property = String.format("%s/config/application.properties", parent);
        LOGGER.info(String.format("读取配置文件：%s", property));

        Resource fileSystemResource = resolver.getResource(property);
        propertiesFactoryBean.setLocations(fileSystemResource);
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
