//package com.dfssi.dataplatform.external.config;
//
//import com.dfssi.dataplatform.external.common.GpJdbcManger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Description
// *
// * @author bin.Y
// * @version 2018/8/16 19:10
// */
//@Configuration
//public  class GpManageConfig {
//
//    @Value("${spring.datasource.postgresql.url}")
//    private String url;
//
//    @Value("${spring.datasource.postgresql.username}")
//    private String username;
//
//    @Value("${spring.datasource.postgresql.password}")
//    private String password;
//
//    @Value("${spring.datasource.postgresql.driver-class-name}")
//    private String driver;
//
//    public String getUrl() {
//        return url;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getDriver() {
//        return driver;
//    }
//
//
//    @Bean(name = "initGpManageConfig")
//    public GpJdbcManger initGpManageConfig(){
//        String url = this.url;
//        GpJdbcManger manger=new GpJdbcManger();
//        manger.setParam(driver,url,username,password);
//        return null;
//    }
//}
