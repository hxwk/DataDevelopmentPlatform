package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.client.NettyClient;
import com.dfssi.dataplatform.util.PropertiesUtil;

import java.util.Properties;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/9 9:12
 */
public class ClientBuild {
    Properties properties = PropertiesUtil.getProperties(PropertiesUtil.CONSUMER_SYNC_PROPERTIES);

//    private String masterIp;

//    public ClientBuild(String masterIp) {
//        this.masterIp = masterIp;
//    }

    public void instanceClient() {
        int clientNum = Integer.parseInt(properties.getProperty("clientNum"));
        int clientInstanceCount = Integer.parseInt(properties.getProperty("clientInstanceCount"));
        String[] clientList = properties.getProperty("clientList").split(",");
        //每台机器建立配置的线程数的连接
        for (int j = 0; j < clientNum; j++) {
            String ip = clientList[j].split(":")[0];
            int port = Integer.parseInt(clientList[j].split(":")[1]);
            for (int i = 0; i < clientInstanceCount; i++) {
                NettyClient client = new NettyClient(ip, port);
                client.start();
            }
        }
    }
}
