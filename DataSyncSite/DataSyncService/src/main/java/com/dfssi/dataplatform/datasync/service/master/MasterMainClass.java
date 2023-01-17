package com.dfssi.dataplatform.datasync.service.master;

import com.dfssi.dataplatform.datasync.common.utils.StringUtil;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceRegistry;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.REST_PACKAGE;
import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.REST_SERVICE_BASE_PORT;

/**
 * Created by cxq on 2017/12/11.
 */
public class MasterMainClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterMainClass.class);

    public static void main(String[] args) {
        LOGGER.info("开始启动master客户端");
        try {
            Options options = new Options();
            Option option = new Option("f", "conf", true,
                    "specify a config file (required if -z missing)");
            option.setRequired(false);
            options.addOption(option);
            CommandLineParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                new HelpFormatter().printHelp("MasterMainClass", options, true);
                return;
            }
            String conf = commandLine.getOptionValue("conf");
            File configurationFile = new File(conf);
            System.setProperty("conf", configurationFile.getCanonicalPath());
            String serverAddress = PropertiUtil.getStr("server_address");
            LOGGER.info("master客户端根据配置文件得到:serverIP = " + serverAddress);
            URI uri = UriBuilder.fromUri("http://0.0.0.0").port(REST_SERVICE_BASE_PORT).build();
            ResourceConfig rc = new PackagesResourceConfig(REST_PACKAGE);
            rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,true);
            //Set<Class<?>> classes = rc.getRootResourceClasses();
            //String property = System.getProperty("java.class.path");
            //LOGGER.info("master客户端得到环境变量:path = " + property);
            //LOGGER.info("StringUtil.class.getClassLoader() = " + StringUtil.class.getClassLoader());
            //System.out.println("TaskEntity.class.getClassLoader() = " + TaskEntity.class.getClassLoader());
            //classes.forEach(aClass -> System.out.println("aClass = " + aClass.getClassLoader()));
            registryMasterTemNode();
            LOGGER.info("master客户端开放restful接口,若接下来日志报错，请检查端口是否被占用，port:" + REST_SERVICE_BASE_PORT);
            HttpServer server = GrizzlyServerFactory.createHttpServer(uri, rc);
            server.start();
            LOGGER.info("master客户端开放restful接口成功，port:" + REST_SERVICE_BASE_PORT);
        } catch (Exception e) {
            LOGGER.error("启动master客户端异常",e);
        }
//        try {
//            Thread.sleep(1000*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        /*Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    String registryAddress = PropertiUtil.getStr("zk_address");
                    String serverAddress = PropertiUtil.getStr("server_address");
                    ServiceRegistry serviceRegistry = new ServiceRegistry(registryAddress);
//                    String serverIP = InetAddress.getLocalHost().getHostAddress();
                    //serviceRegistry.unRegister(serverAddress, Constant.ZK_SERVER_REGISTRY_PATH);
                } catch (Exception e) {
                    LOGGER.error("stop RpcServer error! message:{}",e);
                }
            }
        });*/
    }

    public static int registryMasterTemNode(){
        // 将master的ip信息注册临时节点到zk上，不再去RpcServer的静态块中注册
        try {
            String registryAddress = PropertiUtil.getStr("zk_address");
            String masterName = PropertiUtil.getStr("master_name");
            String serverAddress = PropertiUtil.getStr("server_address");//相当于master_address
            String zkServerRegistryPath = PropertiUtil.getStr("registry_server");
            if ("".equals(masterName) || masterName == null){
                LOGGER.error("启动master客户端指定的配置文件中缺少属性master_name,该属性指定了web端路由到接入的master端名称");
            }
            if ("".equals(serverAddress) || serverAddress == null){
                LOGGER.error("启动master客户端指定的配置文件中缺少属性server_address,该属性映射master端名称对应的ip地址");
            }
            if ("".equals(zkServerRegistryPath) || zkServerRegistryPath == null){
                LOGGER.error("启动master客户端指定的配置文件中缺少属性registry_server,该属性用于注册master信息");
            }
            LOGGER.info("master客户端开始在zk上path:{}上创建临时节点node,name:{}，value:{}", zkServerRegistryPath, masterName, serverAddress);
            ServiceRegistry serviceRegistry = new ServiceRegistry(registryAddress);
            serviceRegistry.register(masterName, zkServerRegistryPath, serverAddress.getBytes());
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("master端开始在zk上创建临时节点失败，30s后重新尝试创建！");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return registryMasterTemNode();
        }
    }

}
