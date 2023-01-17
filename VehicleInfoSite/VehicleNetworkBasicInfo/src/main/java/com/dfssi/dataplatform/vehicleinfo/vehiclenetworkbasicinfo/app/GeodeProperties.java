package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yanghs on 2018/4/3.
 */
@ConfigurationProperties(prefix = GeodeProperties.GEODE_PREFIX)
public class GeodeProperties {

    public static final String GEODE_PREFIX = "spring.geode";

    // 服务地址
    private String url;
    // 服务端口
    private int port;
    // 默认连接池名称
    private String poolName;
    //超时执行时间
    private int poolTimeout;
    //集群url
    private String urls;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public int getPoolTimeout() {
        return poolTimeout;
    }

    public void setPoolTimeout(int poolTimeout) {
        this.poolTimeout = poolTimeout;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "GeodeProperties{" +
                "url='" + url + '\'' +
                ", port=" + port +
                ", poolName='" + poolName + '\'' +
                ", poolTimeout=" + poolTimeout +
                ", urls='" + urls + '\'' +
                '}';
    }
}
