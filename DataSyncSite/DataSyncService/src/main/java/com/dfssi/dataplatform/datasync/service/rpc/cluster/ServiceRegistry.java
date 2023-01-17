package com.dfssi.dataplatform.datasync.service.rpc.cluster;

import com.dfssi.dataplatform.datasync.common.utils.JSONUtil;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 连接ZK注册中心，创建服务注册目录
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private String registryAddress;//zk的集群地址 支持多节点 如：172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181
    private int base_sleep_time_ms = PropertiUtil.getInt("base_sleep_time_ms");
    private int max_retries = PropertiUtil.getInt("max_retries");
    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public CuratorFramework  start(String namespace) {
        LOGGER.info("ServiceRegistry开始实例化curatorFramework,path:{}",namespace);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        if(StringUtils.isNotEmpty( namespace)){
            builder = builder.namespace(namespace);
        }
        CuratorFramework curatorFramework = builder
                .connectString(this.registryAddress)
                .retryPolicy( new ExponentialBackoffRetry(base_sleep_time_ms, max_retries))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }


    private void  stop (CuratorFramework curatorFramework){
        curatorFramework.close();
    }

    @Deprecated
    protected void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create("path", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
            Stat stat = zk.exists(path, true);
            System.out.println("stat = " + stat.getVersion());
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("createNode error:{}", e);
        }
    }

    public void createNode(String node,String nameSpace,byte[] data){
        LOGGER.info("创建永久节点node:{},path:{}",node,nameSpace);
        CuratorFramework curatorFramework = start(nameSpace);
        String path = null;
        try {
            Stat stat = curatorFramework.checkExists().forPath(node);
            if (stat==null){
                path = curatorFramework.create().forPath(node,data);
                LOGGER.info("获得刚创建的节点path:"+path);
            }

        } catch (Exception e) {
            LOGGER.error("创建永久节点node:{},path:{}异常",node,nameSpace,e);
        }
        finally {
            stop(curatorFramework);
        }
        LOGGER.info("创建永久节点node:{},path:{}成功",node,nameSpace);
    }

    public void createTempNode(String node,String nameSpace,byte[] data){
        LOGGER.info("开始在ZK上创建临时节点！"+"path:"+nameSpace+"name:"+node+",value:"+ new String(data));
        CuratorFramework curatorFramework = start(nameSpace);
        LOGGER.debug("为curatorFramework添加监听，当curatorFramework状态异常时自动重新注册临时节点");
        RegtryConnectionStateListener connectionStateListener = new RegtryConnectionStateListener(node, data);
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
        LOGGER.debug("为curatorFramework添加监听结束");
        String actualPath = "";
        String actualvalue = "";
        try {
            //在这里等待一段时间，来消除那种zookeeper客户端已死，但是节点还没有失效的情况
            Stat stat = curatorFramework.checkExists().forPath(node);

            if (stat==null){
                LOGGER.info("curatorFramework检测到path:{}下node:{}状态为空,开始创建临时节点",nameSpace,node);
                PersistentEphemeralNode persistentEphemeralNode = null;
                if (data == null){
                    persistentEphemeralNode  = new PersistentEphemeralNode(curatorFramework, PersistentEphemeralNode.Mode.EPHEMERAL,node, "".getBytes());

                }else{
                    persistentEphemeralNode  = new PersistentEphemeralNode(curatorFramework, PersistentEphemeralNode.Mode.EPHEMERAL,node, data);
                }
                persistentEphemeralNode.start();
                persistentEphemeralNode.waitForInitialCreate(3, TimeUnit.SECONDS);
                //但这是查询节点下的实际值
                actualPath = persistentEphemeralNode.getActualPath();
                actualvalue = new String(curatorFramework.getData().forPath(actualPath));
                //如果actualPath和actualvalue为空  那么临时节点的创建未执行  可能原因：该临时节点已失效，但是未死掉，所以创建失败，这个时候需要等待一段时间，然后重新创建
                if ("".equals(actualPath) && "".equals(actualvalue)){
                    LOGGER.info("创建临时节点失败!可能原因：1、该临时节点已失效，但是未死掉,path="+nameSpace+",actualPath="+actualPath+",actualvalue="+actualvalue);
                }else{
                    LOGGER.info("创建临时节点成功!path="+nameSpace+",actualPath="+actualPath+",actualvalue="+actualvalue);
                }
            }else{
                // 但是如果临时节点处于已失效但是未死掉的状态（状态确实不为null） 如杀掉进程然后马上重启 那么这里没有注册 但是过一段时间该节点就死掉了 所以策略改为先删再注册
                LOGGER.info("检测到path:{}下node:{}不为空,先删除该临时节点再去创建临时节点",nameSpace,node);
                /*//在这里等待一段时间，然后再创建临时节点，如果节点处于将死状态那么死掉就会重新创建，如果过了这段时间还没有死掉，那么则认为该节点正常
                Thread.sleep(3000L);*/
                curatorFramework.delete().deletingChildrenIfNeeded().forPath(node);
                LOGGER.info("删除该临时节点成功，node："+node);
                PersistentEphemeralNode persistentEphemeralNode = null;
                if (data == null){
                    persistentEphemeralNode  = new PersistentEphemeralNode(curatorFramework, PersistentEphemeralNode.Mode.EPHEMERAL,node, "".getBytes());

                }else{
                    persistentEphemeralNode  = new PersistentEphemeralNode(curatorFramework, PersistentEphemeralNode.Mode.EPHEMERAL,node, data);
                }
                persistentEphemeralNode.start();
                persistentEphemeralNode.waitForInitialCreate(3, TimeUnit.SECONDS);
                //但这是查询节点下的实际值
                actualPath = persistentEphemeralNode.getActualPath();
                actualvalue = new String(curatorFramework.getData().forPath(actualPath));
                //如果actualPath和actualvalue为空  那么临时节点的创建未执行  可能原因：该临时节点已失效，但是未死掉，所以创建失败，这个时候需要等待一段时间，然后重新创建
                if ("".equals(actualPath) && "".equals(actualvalue)){
                    LOGGER.info("创建临时节点失败!可能原因：1、该临时节点已失效但未死掉，故创建失败,需等待一段时间(10S)ZK上临时节点死亡，然后重新创建path="+nameSpace+",actualPath="+actualPath+",actualvalue="+actualvalue);
                }else{
                    LOGGER.info("创建临时节点成功!path="+nameSpace+",actualPath="+actualPath+",actualvalue="+actualvalue);
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建临时节点发生异常,e:",e);
        }
        LOGGER.debug("创建临时节点结束!");
    }




    public void deleteNode(String node,String nameSpace){
        //如果是永久节点则需要删除，如果是临时节点那么有可能删除失败，因为临时节点会自动删除
        LOGGER.info("开始在zk上删除节点;node:{};nameSpace:{}",node,nameSpace);
        CuratorFramework curatorFramework = start(nameSpace);
        try {
            Stat stat = curatorFramework.checkExists().forPath("/" + node);
            if(null!=stat){
                curatorFramework.delete().deletingChildrenIfNeeded().forPath("/"+node);
            }else{
                LOGGER.info("删除节点失败，对应ZK目录下不存在该节点！");
            }
        } catch (Exception e) {
            LOGGER.error("删除节点失败，请检查对应ZK目录下是否存在该节点:{}",e);
        }
        finally {
            stop(curatorFramework);
        }
    }
/*
    @Deprecated
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, event -> {
            // 判断是否已连接ZK,连接后计数器递减.
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            });
            // 若计数器不为0,则等待.
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("connectServer error:{}", e);
        }
        return zk;
    }*/




    //注册真实节点
    public void registerRealNode(String node,String nameSpace,byte[] data) {
        if (node != null) {
            createNode(node,nameSpace,data);
        }
    }
    //注册真实节点
    public void registerRealNode(String node,String nameSpace) {
        if (node != null) {
            createNode(node,nameSpace,null);
        }
    }

    //注册临时节点
    public void register(String node,String nameSpace,byte[] data) {
        if (node != null) {
            //createNode(node,nameSpace,data);
            createTempNode(node,nameSpace,data);
        }
    }
    //注册临时节点
    public void register(String node,String nameSpace) {
        if (node != null) {
            //createNode(node,nameSpace,null);
            createTempNode(node,nameSpace,null);
        }
    }

    public byte[] getData(String node,String nameSpace){
        byte[] data = null;
        LOGGER.info("ServiceRegistry.getData;node:{};nameSpace:{}",node,nameSpace);
        CuratorFramework curatorFramework = start(nameSpace);
        try {
            data =curatorFramework.getData().forPath("/"+node);
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("getData error:{}",e);
        }
        finally {
            //改为注册为临时节点后 不应关闭zk客户端 那样临时节点会被删掉
            //stop(curatorFramework);
        }
        return data;
    }


    public <T> T getPOJO(String node,String nameSpace,Class<T> tClass){
        //开始将zk节点中的数据取出来，并转换成指定的class文件
        byte[] data = getData(node, nameSpace);
        LOGGER.info("开始将zk上注册的节点信息取出来并转换成指定的class生成并执行对应的任务，node："+node+",nameSpace:"+nameSpace+",data:"+new String(data));
        T obj = JSONUtil.fromJson(new String(data),tClass);
        return  obj;
    }
    public void setData(String node,String nameSpace,byte[] data){
        //修改临时节点和永久节点的value
        LOGGER.info("ServiceRegistry开始修改节点value,如果节点不存在则创建节点node:{},nameSpace:{},data:{}",node,nameSpace,new String(data));
        CuratorFramework curatorFramework = start(nameSpace);
        try {
            Stat stat = curatorFramework.checkExists().forPath(node);
            if (stat==null){
                curatorFramework.create().forPath(node,data);
            }
            curatorFramework.setData().forPath(node,data);
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("修改节点value发生异常",e);
        }
        finally {
            stop(curatorFramework);
        }
    }


    public void setPOJO(String node,String nameSpace,Object obj){
        LOGGER.info("ServiceRegistry的setPOJO："+new Gson().toJson(obj).toString());
        setData(node,nameSpace,new Gson().toJson(obj).getBytes());
    }
/*
    protected void register(String node) {
        if (node != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
            }
        }
    }
*/

    public void unRegister(String node,String nameSpace) {
        if (node != null) {
            deleteNode(node,nameSpace);
        }
    }
/*
    protected void unRegister() {
            ZooKeeper zk = connectServer();
            if (zk != null) {
            }
    }
*/
}