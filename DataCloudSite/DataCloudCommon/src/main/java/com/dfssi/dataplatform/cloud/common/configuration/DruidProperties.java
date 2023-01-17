package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yanghs on 2018/6/5.
 */
@ConfigurationProperties(prefix = DruidProperties.DRUID_PREFIX)
public class DruidProperties {

    public static final String DRUID_PREFIX = "druid";

    private String loginUsername="ssidruid";

    private String loginPassword="ssidruid";

    private String resetEnable="false";

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getResetEnable() {
        return resetEnable;
    }

    public void setResetEnable(String resetEnable) {
        this.resetEnable = resetEnable;
    }
}
