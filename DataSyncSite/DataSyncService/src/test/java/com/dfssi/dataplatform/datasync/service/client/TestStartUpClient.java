package com.dfssi.dataplatform.datasync.service.client;

import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment.PluginManager;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceDiscovery;
import com.sun.org.apache.bcel.internal.util.ClassLoader;

import java.io.File;
import java.io.IOException;

/**
 * Created by HSF on 2018/3/7.
 */
public class TestStartUpClient {

    public static void main(String[] args) throws InterruptedException {
        try{
            String conf = ClassLoader.getSystemResource("config/client.properties").getFile().toString();
            File configurationFile = new File(conf);
            System.setProperty("conf", configurationFile.getCanonicalPath());
            String property = System.getProperty("java.class.path");
            System.out.println("path = " + property);

            PluginManager pluginManager = PluginManager.getInstance();
            pluginManager.start();

            int serverport = PropertiUtil.getInt("serverport");
            String zkAddress = PropertiUtil.getStr("zk_address");
            ServiceDiscovery discovery = new ServiceDiscovery(zkAddress, Constant.ZK_SERVER_REGISTRY_PATH,true);
            String serverhost=discovery.discoverIpPort("").split(":")[0];
            String port=discovery.discoverIpPort("").split(":")[1];
            System.out.println("serverhost = " + serverhost);
            System.out.println("port = " + port);
            RpcClient client = new RpcClient(serverhost, Integer.parseInt(port));
            try {
                client.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
