package com.dfssi.dataplatform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *   elasticsearch查询依赖
 * @author LiXiaoCong
 * @version 2018/4/25 15:09
 */
@Configuration
public class EvsDataSearchConfig {

    private final Logger logger = LoggerFactory.getLogger(EvsDataSearchConfig.class);

    @Value("${evs.vehicle.data.error.rate}")
    private String errorRate;


    public Double getErrorRate() {
        return Double.parseDouble(errorRate);
    }

}
