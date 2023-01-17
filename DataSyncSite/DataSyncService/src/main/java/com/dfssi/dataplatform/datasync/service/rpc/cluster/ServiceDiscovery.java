package com.dfssi.dataplatform.datasync.service.rpc.cluster;

import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.ZK_SESSION_TIMEOUT;

/**
 * 服务发现:连接ZK,添加watch事件
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<>();

    private String registryAddress;

    private CuratorFramework curatorFramework;

    private String parentPath;

    private boolean recurse;

    private int zk_session_timeout = PropertiUtil.getInt("zk_session_timeout");

    private int base_sleep_time_ms = PropertiUtil.getInt("base_sleep_time_ms");

    private int max_retries = PropertiUtil.getInt("max_retries");




    /* public ServiceDiscovery(String registryAddress, String zkNodePath ) {
         this.registryAddress = registryAddress;

         ZooKeeper zk = connectServer();
         if (zk != null) {
         watchNode(zk,zkNodePath);
         }
     }*/
    public ServiceDiscovery(String registryAddress,String parentPath, boolean recurse/*,Watcher watcher*/) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        curatorFramework = builder
                .connectString(registryAddress)
                .connectionTimeoutMs(zk_session_timeout)
                .retryPolicy( new ExponentialBackoffRetry(base_sleep_time_ms, max_retries))
                .build();
        curatorFramework.start();
        this.parentPath = "/"+parentPath;
        this.recurse = recurse;
    }

    private List<String> getChildrenWatched(Watcher watcher){
        List<String> instanceIds = null;
        try{
            LOGGER.info("开始获取zk的parentPath：{}下的节点信息",this.parentPath);
            instanceIds = curatorFramework.getChildren().usingWatcher(watcher).forPath(this.parentPath);
        }
        catch ( KeeperException.NoNodeException e ){
            if ( this.recurse ){
                try{
                    curatorFramework.create().creatingParentsIfNeeded().forPath(this.parentPath);
                }catch ( KeeperException.NodeExistsException ignore ){
                    // ignore
                    LOGGER.error("NoNode but creating  exists Node !",ignore);
                } catch (Exception e1) {
                    LOGGER.error("Commit path error !",e1);
                }
                instanceIds = getChildrenWatched( watcher);
            }else{
                LOGGER.error("Parent Node do not exists ,and will not create Parent !",e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instanceIds;
    }

    public void watcherNode(String zkNodePath)  {
        try {
            dataList = getChildrenWatched(event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    if (event.getState()== Watcher.Event.KeeperState.SyncConnected){
                        watcherNode(zkNodePath);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String discoverIpPort(String serverIpPort) {
        LOGGER.info("开始连接zk并discover节点信息");
        String data = null;
        List<String> childrenNodeList = getChildrenWatched(null);
        int size = childrenNodeList.size();
        if (size > 0) {
            if (size == 1) {
                data = childrenNodeList.get(0);
                try {
                    String datasresult = getChildrenNodeData(data);
                    return datasresult;
//                    if(datasresult.equals(serverIpPort)){
//                        return datasresult;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("discover zk node info get only data error: ", e);
                }
                LOGGER.info("using only data: {}", data);
            } else {
                for(int i=0;i<size;i++){
                    try {
                        String datasresult = getChildrenNodeData(childrenNodeList.get(i));
                        if(datasresult.equals(serverIpPort)){
                            return datasresult;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("discover zk node info get only data error: ", e);
                    }
                }
                data = childrenNodeList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
                try {
                    String datasresultRamdom = getChildrenNodeData(data);
                    return datasresultRamdom;
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("discover zk node info get only data error: ", e);
                }
            }
        }else{
            try {
                LOGGER.info("当前目录path:{}未发现已注册的zk节点信息,{}ms后会继续去zk上查询节点",this.parentPath,30000);
                Thread.sleep(30000);
                return  discoverIpPort(serverIpPort);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    public String discover(String nodeName) {
        //如果有入参nodeName，则需要根据它去zk服务上索引
        //如果可以索引到，返回其对应的nodeValue,否则任取一个nodeValue将其返回，
        LOGGER.info("discover(String nodeName)开始连接zk并获取节点信息,nodeName:"+nodeName);
        String data = null;
        List<String> childrenNodeList = getChildrenWatched(null);
        int size = childrenNodeList.size();
        if (size > 0) {
            //如果发现了一个节点，那么不管是否匹配都返回对应的ip
            if (size == 1) {
                String datasresult = "";
                data = childrenNodeList.get(0);
                if (data.equals(nodeName)){
                    try {
                        datasresult = getChildrenNodeData(data);
                        return datasresult;
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("根据clientId获取zk上节点存储的信息失败 error: ", e);
                        return "";
                    }
                }else{
                    LOGGER.error("无法根据clientId：{}查找到zk上对应的节点！",nodeName);
                    return datasresult;
                }

            } else {
                String datasresult = "";
                for (int i=0;i<size;i++){
                    data = childrenNodeList.get(i);
                    if (data.equals(nodeName)){
                        try {
                            datasresult = getChildrenNodeData(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error("根据nodeName：{}获取zk上节点存储的信息失败 error: ", nodeName,e);
                            return "";
                        }
                        return datasresult;
                    }
                }
                LOGGER.error("无法根据nodeName：{}查找到zk上对应的节点！",nodeName);
                return datasresult;
                /*//如果childrenNodeList中没有一个匹配clientId  那么返回任意一个子节点的ip
                data = childrenNodeList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
                try {
                    datasresult = getChildrenNodeData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("discover zk node info get childrenNode data error: ", e);
                }*/
            }
        }else{
            try {
                LOGGER.debug("no node is discovered ,will discover {} ms later",500);
                LOGGER.info("当前目录path:{}未发现服务端注册的zk节点信息,{}ms后会继续去zk上查询节点",this.parentPath,30000);
                Thread.sleep(30000);
                return  discover(nodeName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private  String getChildrenNodeData(String ChildrenNode) throws Exception{
        String path = this.parentPath+"/"+ChildrenNode;
        Stat stat = curatorFramework.checkExists().forPath(path);

        if (stat != null){
            LOGGER.info("获取到节点状态正常！");
            byte[] data = curatorFramework.getData().forPath(path);
            String datas = "";
            if (data != null){
                datas = new String(data);
                LOGGER.info("节点不为空，获取到节点值:"+datas);
            }else{
                LOGGER.info("获取节点值为空:"+datas);
            }

            return datas;
        }else{
            return "";
        }


    }
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, ZK_SESSION_TIMEOUT, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
                if(event.getState() == Watcher.Event.KeeperState.Disconnected){
                    LOGGER.error("connection loss");
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk,String zkNodePath) {
        String zkPath = "/"+zkNodePath;
        try {
            Stat status = zk.exists(zkPath, false);
            if (status == null) {
                String[] nodes = zkNodePath.split("/");
                StringBuffer parentPath = new StringBuffer("/");
                for (int i = 0; i < nodes.length; i++) {
                    parentPath.append(nodes[i]);
                    if(zk.exists(parentPath.toString(), false)==null){
                        zk.create(parentPath.toString(),new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    parentPath.append("/");
                }
            }

            List<String> nodeList = zk.getChildren(zkPath, event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    if (event.getState()== Watcher.Event.KeeperState.SyncConnected){
                        watchNode(zk,zkNodePath);
                    }
                    System.out.println("ServiceDiscovery.process");
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                /* */ byte[] bytes = zk.getData(zkNodePath + "/" + node, false, null);
                dataList.add(new String(bytes));
                dataList.add(node);
            }
            LOGGER.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
    }
    public List<String> getAllChilrenNode() {
        List<String> childrenNodeList = getChildrenWatched(null);
        return childrenNodeList;
    }

}