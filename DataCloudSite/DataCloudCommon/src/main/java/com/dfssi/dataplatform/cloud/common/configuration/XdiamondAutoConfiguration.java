package com.dfssi.dataplatform.cloud.common.configuration;

import io.github.xdiamond.client.XDiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 集成配置中心自动配置
 *
 * @author yanghs
 * @since 2018-4-12 10:10:38
 */
@EnableConfigurationProperties(XdiamondProperties.class)
public class XdiamondAutoConfiguration {

    protected final Logger logger = LoggerFactory.getLogger(XdiamondAutoConfiguration.class);

    private XdiamondProperties xdiamondProperties;


    public XdiamondAutoConfiguration(XdiamondProperties xdiamondProperties) {
        this.xdiamondProperties = xdiamondProperties;
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean factory = new MethodInvokingFactoryBean();
        factory.setTargetObject(xdiamondConfig());
        factory.setTargetMethod("getProperties");
        return factory;
    }

    public XDiamondConfig xdiamondConfig() {
        XDiamondConfig xDiamondConfig = new XDiamondConfig();
        xDiamondConfig.setServerHost(xdiamondProperties.getServerHost());
        xDiamondConfig.setServerPort(xdiamondProperties.getServerPort());
        xDiamondConfig.setGroupId(xdiamondProperties.getGroupId());
        xDiamondConfig.setArtifactId(xdiamondProperties.getArtifactId());
        xDiamondConfig.setVersion(xdiamondProperties.getVersion());
        xDiamondConfig.setProfile(xdiamondProperties.getProfile());
        xDiamondConfig.setSecretKey(xdiamondProperties.getSecretKey());
        xDiamondConfig.setbSyncToSystemProperties(xdiamondProperties.getSyncToSystem());
        xDiamondConfig.addAllKeyListener(configEvent -> logger.debug("key：" + configEvent.getKey() + "配置变动"));
        xDiamondConfig.init();
        return xDiamondConfig;
    }



}
