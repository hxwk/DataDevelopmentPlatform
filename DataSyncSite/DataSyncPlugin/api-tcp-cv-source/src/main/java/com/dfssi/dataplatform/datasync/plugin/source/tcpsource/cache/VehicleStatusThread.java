package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.cache;

import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.TaskInfo;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.KafkaUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

/**
 * Created by Hannibal on 2018-02-03.
 */
public class VehicleStatusThread implements Runnable{

    private static Logger logger = Logger.getLogger(VehicleStatusThread.class);

    private boolean isActive=true;

    public void setIsActive(boolean isActive){
        isActive=isActive;
    }

    @Override
    public void run() {

        while (isActive) {
            Jedis jedis = null;
            try {
                try {
                    Thread.sleep(1 * 60 * 1000L);
                } catch (InterruptedException e) {
                    logger.error("获取车辆状态线程睡眠失败");
                }

                logger.info("开始更新车辆状态");
                Long dealBeginTime = System.currentTimeMillis();
                if (!CacheEntities.vidSets.isEmpty()) {
                    Iterator<String> vidIter = CacheEntities.vidSets.iterator();
                    jedis = RedisPoolManager.getJedis();
                    while (vidIter.hasNext()) {
                        String vid = vidIter.next();
                        String redisKey = Constants.GK_VEHICLE_STATE + vid;

                        int status = jedis.exists(redisKey) ? 1 : 0;

                        VnndResMsg res = new VnndResMsg();
                        res.setVid(vid);
                        res.setStatus(status);

                        KafkaUtil.processEvent(res, TaskInfo.getInstance().getTaskId(), "0", Constants.LOGIN_TOPIC, TaskInfo.getInstance().getChannelProcessor());
                    }
                }

                logger.info("此次更新车辆状态成功，耗时：" + (System.currentTimeMillis() - dealBeginTime) / 1000.0 + ". s");

            } catch (Exception e) {
                logger.error(null, e);
            } finally {

                try {
                    if (null != jedis) {
                        RedisPoolManager.returnResource(jedis);
                    }
                } catch (Exception e) {
                    logger.error(null, e);
                }

                try {
                    Thread.sleep(Constants.VEHCILE_STATUS_THREAD_SLEEPTIME * 60 * 1000L);
                } catch (InterruptedException e) {
                    logger.error("获取车辆状态线程睡眠失败");
                }
            }
        }
    }
}
