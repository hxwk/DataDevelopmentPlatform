package com.dfssi.dataplatform.datasync.service.util.zkutil;

/**
 * Created by HSF on 2017/12/13.
 */
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jobclient docker改造
 * 注册应用信息至zookeeper
 *
 */
public class ZkService<T> {
    private static final Logger logger = LoggerFactory.getLogger(ZkService.class);

    private CuratorZookeeperClient zkClient;

    private Set<String> zkPathList = new HashSet<String>();

    // 失败重试定时器，定时检查是否有请求失败，如有，无限次重试
    private  ScheduledFuture<?> retryFuture;

    // 定时任务执行器
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("RegistryFailedRetryTimer", true));

    //需要重新注册的数据
    private Set<T> retrySet = new HashSet<T>();

    /**
     * init-method，初始化执行
     * 将本机docker的IP地址 端口都注册到zookeeper中
     */
    public void register2Zookeeper(String zkAddress) {
        try {
            zkClient = CuratorZookeeperClient.getInstance(zkAddress);
           // ClientMetric client = findClientMetric();
            T client = null;//FIXME
            registerClientMetric(client);
            zkClient.addStateListener(new ZkStateListener(){
                @Override
                public void reconnected() {
                   // ClientMetric client = findClientMetric();
                    //将服务添加到重试列表
                    retrySet.add(client);
                }
            });
            //启动线程进行重试，1秒执行一次，因为jobcenter的定时触发时间最短的是1秒
            this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    // 检测并连接注册中心
                    try {
                        retryRegister();
                    } catch (Throwable t) { // 防御性容错
                        logger.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            logger.error("zookeeper write exception",e);
        }
    }

    /**
     * destrory-method,销毁时执行
     */
    public void destroy4Zookeeper() {
        logger.info("zkDockerService destrory4Zookeeper path="+zkPathList);
        try {
            if(retryFuture != null){
                retryFuture.cancel(true);
            }

        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }

        if(zkPathList != null && zkPathList.size() > 0) {
            for(String path : zkPathList) {
                try {
                    zkClient.delete(path);
                } catch (Exception e) {
                    logger.error("zkDockerService destrory4Zookeeper exception",e);
                }
            }
        }
        zkClient.close();
    }


    /** 将值写入zookeeper中 **/
    private void registerClientMetric(T data) throws Exception{
        String centerPath = "/server";
        String content = "";
        String strServer = zkClient.write(centerPath, content);
        if(!StringUtils.isBlank(strServer)) {
            zkPathList.add(strServer);
        }
    }
    /**
     * 重连到zookeeper时，自动重试
     */
    protected synchronized void retryRegister() {
        if(!retrySet.isEmpty()){
            logger.info("jobclient  begin retry register client to zookeeper");
            Set<T> retryClients = new HashSet<T>(retrySet);
            for(T data :retryClients){
                logger.info("retry register="+data);
                try {
                    //registerJobcenterClient(data);
                    retrySet.remove(data);
                } catch (Exception e) {
                    logger.error("registerJobcenterClient failed",e);
                }
            }
        }
    }
}

