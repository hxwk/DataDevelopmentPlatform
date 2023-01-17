package com.dfssi.dataplatform.cache.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dfssi.dataplatform.cache.AbstractCache;
import com.dfssi.dataplatform.entity.count.TotalRunningMsg;
import com.dfssi.dataplatform.service.EvsDataCountResultService;
import com.dfssi.dataplatform.service.EvsMileageVerifyService;
import com.google.common.base.Splitter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/8 15:51
 */
public class EvsDataCountResultCache extends AbstractCache<String, Object> {
    private final Logger logger = LoggerFactory.getLogger(EvsDataCountResultCache.class);

    @Autowired
    private EvsDataCountResultService evsDataCountResultService;

    @Autowired
    private EvsMileageVerifyService evsMileageVerifyService;

    public EvsDataCountResultCache(ExecutorService executorService) {
        super(3, 24, TimeUnit.HOURS, executorService);
    }

    @Override
    protected Object loadData(String key) {
        String[] items = key.split(",");
        switch (items[0]){
            case "monthTotalOnlineAndRun" :
                return evsDataCountResultService.countMonthTotalOnlineAndRun(items[1]);
            case "totalRunningMsg" :
                TotalRunningMsg totalMileAndTime =
                        evsDataCountResultService.queryTotalMileAndTimeByVin(items[1]);
                if(totalMileAndTime != null) {
                    Map<String, Object> map = evsMileageVerifyService.countVerifyMileage(items[1]);
                    map.put("totalMileage", totalMileAndTime.getTotalMileage());
                    map.put("totalDuration", totalMileAndTime.getTotalDuration());
                    map.put("totalPowerConsumption", totalMileAndTime.getTotalPowerConsumption());
                    map.put("totalCharge", totalMileAndTime.getTotalcharge());
                    map.put("totalRun", totalMileAndTime.getTotalrun());
                    return map;
                }
        }

        return null;
    }

    public List<Object> getMonthTotalOnlineAndRunCountResult(String months){
        if(months == null){
            months = DateTime.now().minusMonths(1).toString("yyyyMM");
        }
        List<String> monthList = Splitter.on(",").omitEmptyStrings().splitToList(months);
        List<Object> res =  monthList.stream().parallel()
                .map(month -> getCache(String.format("monthTotalOnlineAndRun,%s", month)))
                .collect(Collectors.toList());
        return res;
    }

    public Object gettotalRunningMsg(String vin){
        Object res = getCache(String.format("totalRunningMsg,%s", vin));
        return res;
    }

    private static class EvsDataCountResultCacheKey{
        private String label;
        private Map<String, Object> paramMap;

        private EvsDataCountResultCacheKey(String label,
                                           Map<String, Object> paramMap){

            this.label = label;
            this.paramMap = paramMap;
        }

        private <T> T getParamValue(String key){
            return (T)paramMap.get(key);
        }

        @Override
        public String toString() {
            return createKey(label, paramMap);
        }

        private <T> T getParamValue(String key, T defaultVal){
            Object o = paramMap.get(key);
            T t;
            if(o == null){
                t = defaultVal;
            }else {
                t = (T)paramMap.get(key);
            }
            return t;
        }

        private static EvsDataCountResultCacheKey get(String key){
            String[] items = key.split(",");

            String label = items[0];
            Map<String, Object> paramMap = null;
            if(items.length == 2 && items[1].length() > 1){
                paramMap = JSON.parseObject(items[1],
                        new TypeReference<Map<String, Object>>() {});
            }
            return new EvsDataCountResultCacheKey(label, paramMap);
        }

        private static String createKey(String label, Map<String, Object> paramMap){
            String params = "";
            if(paramMap == null){
                params = JSON.toJSONString(paramMap);
            }
            return String.format("%s,%s", label, params);
        }
    }
}
