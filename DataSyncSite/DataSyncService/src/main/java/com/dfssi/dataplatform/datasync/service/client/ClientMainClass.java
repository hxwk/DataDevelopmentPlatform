package com.dfssi.dataplatform.datasync.service.client;

import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment.PluginManager;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceDiscovery;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceRegistry;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by cxq on 2017/12/11.
 */
public class ClientMainClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMainClass.class);
    public static void main(String[] args) throws Exception{
        LOGGER.info("开始启动client客户端");
        try {
            Options options = new Options();
            Option option = new Option("f", "conf", true,"specify a config file (required if -z missing)");
            option.setRequired(true);
            options.addOption(option);
            CommandLineParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption('h')) {
                new HelpFormatter().printHelp("ClientMainClass", options, true);
                return;
            }
            String conf = commandLine.getOptionValue("conf");
            File configurationFile = new File(conf);
            System.setProperty("conf", configurationFile.getCanonicalPath());
        }  catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String property = System.getProperty("java.class.path");
        //LOGGER.info("client客户端加载path = " + property);
        LOGGER.info("加载插件，需出现加载JAR包的日志，若无日志，请检查client中配置文件的plugin.lib.path配置项是否正确");
        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.start();
        registryClientTemNode();

        String zkAddress ;
        String zk_server_registry_path ;
        String serverAddress ;
        try{
            zkAddress = PropertiUtil.getStr("zk_address");
        }catch(Exception e){
            LOGGER.error("PropertiUtil获取配置文件中的zk_address属性失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由key获取配置文件中的值失败");
        }
        try{
            zk_server_registry_path =PropertiUtil.getStr("registry_server");
        }catch(Exception e){
            LOGGER.error("PropertiUtil获取配置文件中的registry_server属性失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由key获取配置文件中的值失败");
        }

        try{
            serverAddress = PropertiUtil.getStr("server_address");
        }catch(Exception e){
            LOGGER.error("PropertiUtil获取配置文件中的server_address属性失败,异常信息:{}",e);
            throw new Exception("PropertiUtil由key获取配置文件中的值失败");
        }

        LOGGER.info("client客户端开始根据配置文件的registry_server去ZK集群中获取master端信息,获取策略：根据配置文件下的server_address属性去ZK上寻找改master是否存在，zkAddress："+zkAddress+",zk_server_registry_path:"+zk_server_registry_path+",server_address:"+serverAddress);
        ServiceDiscovery discovery = new ServiceDiscovery(zkAddress, zk_server_registry_path,true);
        //从zookeeper那里获得一个ip和端口 并根据这个两个参数实例化rpc服务
        String zkNodeInfo = discovery.discoverIpPort(serverAddress);
        LOGGER.info("client客户端从ZK集群中获取master端信息："+zkNodeInfo);
        String serverhost;
        int port;
        try{
            serverhost=zkNodeInfo.split(":")[0];
            port=Integer.parseInt(zkNodeInfo.split(":")[1]);
        }catch(Exception e){
            LOGGER.error("client客户端解析master端信息发生异常,请检查master端信息在zk上注册的信息:{}",e);
            throw new Exception("client客户端解析master端信息发生异常");
        }
        RpcClient client = new RpcClient(serverhost, port);

        client.start();
        final RpcClient client_ = client;
        Runtime.getRuntime().addShutdownHook(new Thread("client-shutdown-hook"){
            @Override
            public void run() {
                client_.close();
                pluginManager.stop();
            }
        });
    }

    public static int registryClientTemNode() throws InterruptedException {
        // 将ip信息注册临时节点到zk上，不再去master的channelActive()中注册
        try {
            String registryAddress = PropertiUtil.getStr("zk_address");
            String clientName = PropertiUtil.getStr("client_name");
            String clientAddress = PropertiUtil.getStr("client_address");
            String zkClientRegistryPath = PropertiUtil.getStr("registry_client");

            if ("".equals(clientName) || clientName == null){
                LOGGER.error("启动client端指定的配置文件中缺少属性client_name,该属性指定了master端路由到接入的client端名称");
            }
            if ("".equals(clientAddress) || clientAddress == null){
                LOGGER.error("启动client端指定的配置文件中缺少属性clientAddress,该属性映射client端名称对应的ip地址");
            }
            if ("".equals(zkClientRegistryPath) || zkClientRegistryPath == null){
                LOGGER.error("启动client端指定的配置文件中缺少属性registry_client,该属性用于注册client信息");
            }
            LOGGER.info("client客户端已启动，开始在zk上注册client自身信息，path:{}，name:{}，value:{}", zkClientRegistryPath, clientName, clientAddress);
            ServiceRegistry registor = new ServiceRegistry(registryAddress);
            registor.register(clientName, zkClientRegistryPath, clientAddress.getBytes());
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("客户端开始在zk上注册临时节点失败，30s后重新尝试注册注册！");
            Thread.sleep(30000);
            return registryClientTemNode();
        }
    }
}
