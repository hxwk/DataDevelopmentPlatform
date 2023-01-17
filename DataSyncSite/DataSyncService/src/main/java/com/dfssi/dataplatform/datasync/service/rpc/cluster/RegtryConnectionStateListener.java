package com.dfssi.dataplatform.datasync.service.rpc.cluster;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Hannibal on 2018-03-15.
 */
public class RegtryConnectionStateListener implements ConnectionStateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegtryConnectionStateListener.class);

    private String zkRegPath;

    private byte[] regContext;

    public RegtryConnectionStateListener(String zkRegPath, byte[] regContext) {
        this.zkRegPath = zkRegPath;
        this.regContext = regContext;
    }

    @Override
    public void stateChanged(CuratorFramework curator, ConnectionState connectionState) {
        LOGGER.info("curatorFramework的监听器检测到curatorFramework状态发生变化,connectionState:"+connectionState);
        
        if (connectionState == ConnectionState.LOST || connectionState == ConnectionState.RECONNECTED) {
                try {
                    LOGGER.info("因curatorFramework状态发生变化,开始尝试重新注册临时节点,若网络中断则无法重新注册该临时节点,zkRegPath:{},regContext:{}",zkRegPath,new String(regContext));
                    Stat stat = curator.checkExists().forPath(zkRegPath);
                    if (stat==null){
                        //如果临时节点已注册，但是发生了断网情况导致节点死掉状态变为null，那么在重连过程状态变化SUSPENDED--LOST--RECONNECTED  当状态改变到RECONNECTED下会将临时节点重新注册
                        //如果一开始，临时节点没有注册，但是发生了断网情况(节点状态肯定为null)，那么重连过程状态变化SUSPENDED--LOST--RECONNECTED 当状态改变到LOST下会将未注册的临时节点注册，当状态改变到RECONNECTED下就不会再次注册
                        LOGGER.info("zkRegPath:{}下状态为null,故需重新注册临时节点",zkRegPath);
                        PersistentEphemeralNode persistentEphemeralNode = null;
                        if (regContext == null){
                            persistentEphemeralNode  = new PersistentEphemeralNode(curator, PersistentEphemeralNode.Mode.EPHEMERAL,zkRegPath, "".getBytes());

                        }else{
                            persistentEphemeralNode  = new PersistentEphemeralNode(curator, PersistentEphemeralNode.Mode.EPHEMERAL,zkRegPath, regContext);
                        }
                        persistentEphemeralNode.start();
                        persistentEphemeralNode.waitForInitialCreate(3, TimeUnit.SECONDS);
                        //actualPath = persistentEphemeralNode.getActualPath();
                        String actualPath = persistentEphemeralNode.getActualPath();
                        String value = new String(curator.getData().forPath(actualPath));
                        LOGGER.info("重新注册临时节点成功actualPath:"+actualPath+",actualvalue: " + value);
                    }else{
                        //断网重连的情况下，如果进入到这里 那么节点未死掉，那么这里可以不需要重新注册
                        LOGGER.info("zkRegPath:{}下状态不为null,故无需重新注册临时节点",zkRegPath);
                    }
                } catch (Exception e) {
                    LOGGER.error("重新注册临时节点发生异常,e:",e);
                }
        }
    }
}
