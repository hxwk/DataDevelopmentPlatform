package com.dfssi.dataplatform.abs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring boot启用类
 * Bean装配默认规则是根据Application类所在的包位置从上往下扫描
 */
@SpringBootApplication
public class AbsCheckApp {
    public static void main(String[] args) {
        SpringApplication.run(AbsCheckApp.class, args);
    }

}
