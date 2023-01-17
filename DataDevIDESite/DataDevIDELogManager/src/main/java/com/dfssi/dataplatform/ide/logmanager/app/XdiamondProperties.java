package com.dfssi.dataplatform.ide.logmanager.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置中心自动配置属性
 *
 * @author yanghs
 * @since 2018-4-12 10:10:38
 */
@ConfigurationProperties(prefix = XdiamondProperties.XDIAMOND_PREFIX)
public class XdiamondProperties {

    public static final String XDIAMOND_PREFIX = "xdiamond";

    private String serverHost;//配置中心服务地址
    private int serverPort;//配置中心服务端口
    private String profile;//配置中心生效环境
    private String secretKey;//配置中心分配的密码
    private String groupId;//系统groupId
    private String artifactId;//系统artifactId
    private String version;//系统version
    private boolean syncToSystem;//配置中心配置数据是否存入系统

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean getSyncToSystem() {
        return syncToSystem;
    }

    public void setSyncToSystem(boolean syncToSystem) {
        this.syncToSystem = syncToSystem;
    }
}
