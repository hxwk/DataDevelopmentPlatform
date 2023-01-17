package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.ftp.FtpTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Configuration
@Order(1)
@Data
@ConfigurationProperties(prefix = "spring.ftp")
public class FtpAutoConfigure {

    private FtpConfig export;

    @Bean
    public FtpTemplate ftpTemplate() throws Exception {
        return new FtpTemplate(export);
    }

}