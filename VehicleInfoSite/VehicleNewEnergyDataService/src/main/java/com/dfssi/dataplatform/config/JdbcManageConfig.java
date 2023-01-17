package com.dfssi.dataplatform.config;

import com.dfssi.dataplatform.service.JdbcManage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/8/16 19:10
 */
@Configuration
public  class JdbcManageConfig {

    @Value("${spring.datasource.mysql.jdbc-url}")
    private String url;

    @Value("${spring.datasource.mysql.username}")
    private String username;

    @Value("${spring.datasource.mysql.password}")
    private String password;

    @Value("${spring.datasource.mysql.driver-class-name}")
    private String driver;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }


    @Bean(name = "initJdbcManageConfig")
    public JdbcManage initJdbcManageConfig(){
        String url = this.url;
        JdbcManage.setParam(driver,url,username,password);
        return null;
    }
}
