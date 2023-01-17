package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import lombok.Data;

@Data
public class FtpConfig {
    private boolean enabled = true;
    private String host;
    private int port = 21;
    private String username;
    private String password;
    private volatile int maximumPoolSize = 15;
    private boolean passiveMode = false;
    private String encoding = "UTF-8";
    private int connectTimeout = 30000;
    private int bufferSize = 8096;
    private int transferFileType;
}