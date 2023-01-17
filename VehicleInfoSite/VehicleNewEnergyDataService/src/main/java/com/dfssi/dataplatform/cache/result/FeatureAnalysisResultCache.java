package com.dfssi.dataplatform.cache.result;

import com.dfssi.dataplatform.cache.AbstractCache;
import com.dfssi.dataplatform.service.FeatureAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *   特征分析子专题缓存
 *       缓存过期时间为1小时， 无效时间为1天
 * @author LiXiaoCong
 * @version 2018/7/24 9:16
 */
public class FeatureAnalysisResultCache  extends AbstractCache<String, Object> {
    private final Logger logger = LoggerFactory.getLogger(FeatureAnalysisResultCache.class);

    @Autowired
    private FeatureAnalysisService featureAnalysisService;

    public FeatureAnalysisResultCache(ExecutorService executorService) {
        super(1, 24, TimeUnit.HOURS, executorService);
    }

    @Override
    protected Object loadData(String key) {
        String[] items = key.split(",");
        switch (items[0]){
            case "basicInformation":
                logger.info(String.format("加载 电动汽车用户出行基本信息 数据，并缓存"));
                return  featureAnalysisService.basicInformation(null, null, null, null, null);

            case "tripTimeLevelCount":
                logger.info(String.format("加载 单次行驶时长统计 数据，并缓存"));
                return  featureAnalysisService.tripTimeLevelCount(null, null, null, null, null);
            case "dayTimeLevelCount":
                logger.info(String.format("加载 单日行驶时长统计 数据，并缓存"));
                return  featureAnalysisService.dayTimeLevelCount(null, null, null, null, null);

            case "tripStartSocCount":
                logger.info(String.format("加载 驾驶初始soc统计 数据，并缓存"));
                return  featureAnalysisService.tripStartSocCount(null, null, null, null, null);

            case "tripStopSocCount":
                logger.info(String.format("加载 驾驶结束soc统计 数据，并缓存"));
                return  featureAnalysisService.tripStopSocCount(null, null, null, null, null);

                case "drivingTimeDistributeCount":
                logger.info(String.format("加载 全天行驶时长分布统计 数据，并缓存"));
                return  featureAnalysisService.drivingTimeDistributeCount(null, null, null, null, null);
        }
        return null;
    }




    public Object basicInformation(){
        return getCache("basicInformation");
    }

    public Object tripTimeLevelCount(){
        return getCache("tripTimeLevelCount");
    }

    public Object dayTimeLevelCount(){
        return getCache("dayTimeLevelCount");
    }

    public Object tripStartSocCount(){
        return getCache("tripStartSocCount");
    }

    public Object tripStopSocCount(){
        return getCache("tripStopSocCount");
    }

    public Object drivingTimeDistributeCount(){
        return getCache("drivingTimeDistributeCount");
    }

}
