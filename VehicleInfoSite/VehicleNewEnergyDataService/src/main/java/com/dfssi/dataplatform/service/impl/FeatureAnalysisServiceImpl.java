package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.entity.FeatureAnalysisBasicInformation;
import com.dfssi.dataplatform.model.FeatureAnalysisModel;
import com.dfssi.dataplatform.service.FeatureAnalysisService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/7/24 13:11
 */
@Service
public class FeatureAnalysisServiceImpl implements FeatureAnalysisService {
    private final Logger logger = LoggerFactory.getLogger(FeatureAnalysisServiceImpl.class);

    @Autowired
    private FeatureAnalysisModel featureAnalysisModel;

    @Override
    public Map<String, FeatureAnalysisBasicInformation> basicInformation(String enterprise,
                                                                         String hatchback,
                                                                         String vin,
                                                                         String beginDay,
                                                                         String endDay) {

        //计算三个月的
        String[] dayBounds = getDayBounds(beginDay, endDay, 90);

        Map<String, FeatureAnalysisBasicInformation> res = Maps.newHashMap();
        res.put("1", new FeatureAnalysisBasicInformation());
        res.put("2", new FeatureAnalysisBasicInformation());
        FeatureAnalysisBasicInformation basicInformation;
        String model;

        //单日平均行驶里程
        List<Map<String, Object>> travelAvgMileageByDay
                = featureAnalysisModel.travelAvgMileageByDay(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);
       if(travelAvgMileageByDay != null){
           for(Map<String, Object> map : travelAvgMileageByDay){
               model = map.get("model").toString();
               basicInformation = res.get(model);
               basicInformation.setDayMile((Double) map.getOrDefault("dayavgmile", 0.0));

               BigDecimal dayavgtime = (BigDecimal) map.getOrDefault("dayavgtime", 0.0);
               basicInformation.setDayDrivingTime(dayavgtime.doubleValue());

               double totalmile = (Double) map.getOrDefault("totalmile", 0.0);
               double totalpower = (Double) map.getOrDefault("totalpower", 0.0);
               basicInformation.setDayEnergyConsumption(totalpower * 0.1 / totalmile);

           }
       }

        //单次出行平均行驶里程
        List<Map<String, Object>> travelTripAvgMileageByDay
                = featureAnalysisModel.travelTripAvgMileageByDay(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);
       if(travelTripAvgMileageByDay != null){
           for(Map<String, Object> map : travelTripAvgMileageByDay){
               model = map.get("model").toString();
               basicInformation = res.get(model);
               basicInformation.setTripMile((Double) map.getOrDefault("dayavgtripmile", 0.0));

               BigDecimal dayavgtriptime = (BigDecimal) map.getOrDefault("dayavgtriptime", 0.0);
               basicInformation.setTripDrivingTime(dayavgtriptime.doubleValue());
           }
       }

        //单日出行次数
        List<Map<String, Object>> avgTripsByDay
                = featureAnalysisModel.avgTripsByDay(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);
        if(avgTripsByDay != null){
            for(Map<String, Object> map : avgTripsByDay){
                model = map.get("model").toString();
                basicInformation = res.get(model);

                BigDecimal avgtrips = (BigDecimal) map.getOrDefault("avgtrips", 0.0);
                basicInformation.setDayTrips(avgtrips.doubleValue());
            }
        }

        return res;
    }

    @Override
    public  Map<String, Map<Integer, Long>> tripTimeLevelCount(String enterprise,
                                                               String hatchback,
                                                               String vin,
                                                               String beginDay,
                                                               String endDay) {

        Map<String, Map<Integer, Long>> res = Maps.newHashMap();
        //设置初始值
        Map<Integer, Long> levelCountMap = Maps.newHashMap();
        for(int  i = 0; i < 13; i++){
            levelCountMap.put(i, 0L);
        }
        res.put("1", levelCountMap);
        res.put("2", Maps.newHashMap(levelCountMap));

        String[] dayBounds = getDayBounds(beginDay, endDay, 90);
        List<Map<String, Object>> timeLevelCount =
                featureAnalysisModel.tripTimeLevelCount(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);

        String model;
        int level;
        Long  count;
        if(timeLevelCount != null){
            for(Map<String, Object> map : timeLevelCount){
                model = map.get("model").toString();
                levelCountMap = res.get(model);

                level = (int)map.get("time_level");
                if(level >= 12) level = 12;
                count = levelCountMap.get(level) + (long )map.get("vehicles");

                levelCountMap.put(level, count);
            }
        }

        return res;
    }

    @Override
    public Map<String, Map<Integer, Long>> dayTimeLevelCount(String enterprise,
                                                             String hatchback,
                                                             String vin,
                                                             String beginDay,
                                                             String endDay) {

        Map<String, Map<Integer, Long>> res = Maps.newHashMap();
        //设置初始值
        Map<Integer, Long> levelCountMap = Maps.newHashMap();
        for(int  i = 0; i < 13; i++){
            levelCountMap.put(i, 0L);
        }
        res.put("1", levelCountMap);
        res.put("2", Maps.newHashMap(levelCountMap));

        String[] dayBounds = getDayBounds(beginDay, endDay, 90);
        List<Map<String, Object>> timeLevelCount =
                featureAnalysisModel.dayTimeLevelCount(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);

        String model;
        int level;
        Long  count;
        if(timeLevelCount != null){
            for(Map<String, Object> map : timeLevelCount){
                model = map.get("model").toString();
                levelCountMap = res.get(model);

                level = (int)map.get("totaltimelevel");
                if(level >= 12) level = 12;
                count = levelCountMap.get(level) + (long )map.get("vehicles");

                levelCountMap.put(level, count);
            }
        }

        return res;
    }

    @Override
    public Map<String, Map<Integer, Long>> tripStartSocCount(String enterprise,
                                                             String hatchback,
                                                             String vin,
                                                             String beginDay,
                                                             String endDay) {

        Map<String, Map<Integer, Long>> res = Maps.newHashMap();
        //设置初始值
        Map<Integer, Long> levelCountMap = Maps.newHashMap();
        for(int  i = 0; i < 10; i++){
            levelCountMap.put(i, 0L);
        }
        res.put("1", levelCountMap);
        res.put("2", Maps.newHashMap(levelCountMap));

        String[] dayBounds = getDayBounds(beginDay, endDay, 90);
        List<Map<String, Object>> startSocCount =
                featureAnalysisModel.tripStartSocCount(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);

        String model;
        int level;
        Long  count;
        if(startSocCount != null){
            for(Map<String, Object> map : startSocCount){
                model = map.get("model").toString();
                levelCountMap = res.get(model);

                level = (int)map.get("start_soc");
                if(level >= 9) level = 9;
                count = levelCountMap.get(level) + (long )map.get("vehicles");

                levelCountMap.put(level, count);
            }
        }


        return res;
    }

    @Override
    public Map<String, Map<Integer, Long>> tripStopSocCount(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            String beginDay,
                                                            String endDay) {

        Map<String, Map<Integer, Long>> res = Maps.newHashMap();
        //设置初始值
        Map<Integer, Long> levelCountMap = Maps.newHashMap();
        for(int  i = 0; i < 10; i++){
            levelCountMap.put(i, 0L);
        }
        res.put("1", levelCountMap);
        res.put("2", Maps.newHashMap(levelCountMap));

        String[] dayBounds = getDayBounds(beginDay, endDay, 90);
        List<Map<String, Object>> startSocCount =
                featureAnalysisModel.tripStopSocCount(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);

        String model;
        int level;
        Long  count;
        if(startSocCount != null){
            for(Map<String, Object> map : startSocCount){
                model = map.get("model").toString();
                levelCountMap = res.get(model);

                level = (int)map.get("stop_soc");
                if(level >= 9) level = 9;
                count = levelCountMap.get(level) + (long )map.get("vehicles");

                levelCountMap.put(level, count);
            }
        }


        return res;
    }


    @Override
    public Map<String, Map<Integer, Long>> drivingTimeDistributeCount(String enterprise,
                                                                      String hatchback,
                                                                      String vin,
                                                                      String beginDay,
                                                                      String endDay) {

        Map<String, Map<Integer, Long>> res = Maps.newHashMap();
        //设置初始值
        Map<Integer, Long> levelCountMap = Maps.newHashMap();
        for(int  i = 0; i < 8; i++){
            levelCountMap.put(i, 0L);
        }
        res.put("1", levelCountMap);
        res.put("2", Maps.newHashMap(levelCountMap));

        String[] dayBounds = getDayBounds(beginDay, endDay, 90);
        List<Map<String, Object>> distributeCount =
                featureAnalysisModel.drivingTimeDistributeCount(enterprise, hatchback, vin, dayBounds[0], dayBounds[1]);

        String model;
        long count;
        if(distributeCount != null){
            for(Map<String, Object> map : distributeCount){
                model = map.get("model").toString();
                levelCountMap = res.get(model);

                count = (long)map.get("timelevel0");
                levelCountMap.put(0, count);

                count = (long)map.get("timelevel1");
                levelCountMap.put(1, count);

                count = (long)map.get("timelevel2");
                levelCountMap.put(2, count);

                count = (long)map.get("timelevel3");
                levelCountMap.put(3, count);

                count = (long)map.get("timelevel4");
                levelCountMap.put(4, count);

                count = (long)map.get("timelevel5");
                levelCountMap.put(5, count);

                count = (long)map.get("timelevel6");
                levelCountMap.put(6, count);

                count = (long)map.get("timelevel7");
                levelCountMap.put(7, count);
            }
        }

        return res;
    }


    @Override
    public Map<String, Object> outBoundsVehicles(Long timePoint,
                                                 int pageNow,
                                                 int pageSize) {
        if(timePoint == null)timePoint = System.currentTimeMillis() - 5 * 60 * 1000L;

        DateTime dateTime = new DateTime(timePoint);
        String day = dateTime.toString("yyyyMMdd");

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        featureAnalysisModel.outBoundsVehicles(timePoint, day);
        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    private String[] getDayBounds(String beginDay,
                                  String endDay,
                                  int defaultGapDays){
        if(beginDay == null && endDay == null){
            DateTime now = DateTime.now();
            endDay = now.toString("yyyyMMdd");
            beginDay = now.minusDays(defaultGapDays).toString("yyyyMMdd");
        }else if(beginDay != null && endDay == null){
            endDay = DateTime.now().toString("yyyyMMdd");
        }else if(beginDay == null && endDay != null){
            DateTime now = DateTime.parse(endDay, DateTimeFormat.forPattern("yyyyMMdd"));
            beginDay = now.minusDays(defaultGapDays).toString("yyyyMMdd");
        }
        return new String[]{beginDay, endDay};
    }
}
