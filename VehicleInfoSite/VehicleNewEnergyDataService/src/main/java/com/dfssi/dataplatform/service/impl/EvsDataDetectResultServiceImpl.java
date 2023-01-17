package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.cache.database.EvsDetectDetailCache;
import com.dfssi.dataplatform.entity.database.EvsDetectDetail;
import com.dfssi.dataplatform.model.EvsDataDetectResultModel;
import com.dfssi.dataplatform.service.EvsDataDetectResultService;
import com.dfssi.dataplatform.utils.DateUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 13:20
 */
@Service
public class EvsDataDetectResultServiceImpl implements EvsDataDetectResultService {
    private final Logger logger = LoggerFactory.getLogger(EvsDataDetectResultServiceImpl.class);

    @Autowired
    private EvsDataDetectResultModel evsDataDetectResultModel;

    @Autowired
    private EvsDetectDetailCache evsDetectDetailCache;

    @Override
    public Map<String, Object> queryDataQualityInDay(String vin, String day) {

        Map<String, Object> res = Maps.newHashMap();

        Map<String, Object> dataQualityInDay = evsDataDetectResultModel.queryDataQualityInDay(vin, day);
        if (dataQualityInDay != null && !dataQualityInDay.isEmpty()) {

            res = aggByItem(dataQualityInDay);
            res.put("vin", vin);
        } else {
            logger.warn(String.format("未查询vin:%s在%s当天的数据质量检测统计信息。", vin, day));
        }
        return res;
    }


    public Map<String, Object> queryItemDataQuality(String item, String enterprise, String hatchback, String vin) {

        Map<String, Object> map;
        switch (item) {
            case "00": // 整车数据
                map = evsDataDetectResultModel.queryVehicleDataQuality(enterprise, hatchback, vin);
                break;
            case "01": // 驱动电机数据
                map = evsDataDetectResultModel.queryDriverMotorDataQuality(enterprise, hatchback, vin);
                break;
            case "02": // 燃料电池
                map = evsDataDetectResultModel.queryFuelCellDataQuality(enterprise, hatchback, vin);
                break;
            case "03": // 发动机数据
                map = evsDataDetectResultModel.queryEngineDataQuality(enterprise, hatchback, vin);
                break;
            case "04": // 车辆位置数据
                map = evsDataDetectResultModel.queryGpsDataQuality(enterprise, hatchback, vin);
                break;
            case "05": // 极值数据
                map = evsDataDetectResultModel.queryExtremumDataQuality(enterprise, hatchback, vin);
                break;
            case "06": // 报警数据
                map = evsDataDetectResultModel.queryAlarmDataQuality(enterprise, hatchback, vin);
                break;
            default:
                throw new IllegalArgumentException(String.format("无法识别的数据项：%s", item));

        }
        return putBaseInfo(map, enterprise, hatchback, vin);
    }

    public Map<String, Object> queryItemDataQualityInDay(String item,
                                                         String enterprise,
                                                         String hatchback,
                                                         String vin,
                                                         Long startTime,
                                                         Long endTime) {

        String[] dateBound = getDateBound(startTime, endTime);

        Map<String, Object> map;
        switch (item) {
            case "00": // 整车数据
                map = evsDataDetectResultModel.queryVehicleDataQualityInDay(enterprise, hatchback, vin, dateBound[0],
                        dateBound[1]);
                break;
            case "01": // 驱动电机数据
                map = evsDataDetectResultModel.queryDriverMotorDataQualityInDay(enterprise, hatchback, vin,
                        dateBound[0], dateBound[1]);
                break;
            case "02": // 燃料电池
                map = evsDataDetectResultModel.queryFuelCellDataQualityInDay(enterprise, hatchback, vin,
                        dateBound[0], dateBound[1]);
                break;
            case "03": // 发动机数据
                map = evsDataDetectResultModel.queryEngineDataQualityInDay(enterprise, hatchback, vin, dateBound[0],
                        dateBound[1]);
                break;
            case "04": // 车辆位置数据
                map = evsDataDetectResultModel.queryGpsDataQualityInDay(enterprise, hatchback, vin, dateBound[0],
                        dateBound[1]);
                break;
            case "05": // 极值数据
                map = evsDataDetectResultModel.queryExtremumDataQualityInDay(enterprise, hatchback, vin,
                        dateBound[0], dateBound[1]);
                break;
            case "06": // 报警数据
                map = evsDataDetectResultModel.queryAlarmDataQualityInDay(enterprise, hatchback, vin, dateBound[0],
                        dateBound[1]);
                break;
            default:
                throw new IllegalArgumentException(String.format("无法识别的数据项：%s", item));

        }
        return putBaseInfo(map, enterprise, hatchback, vin);
    }

    @Override
    public Map<String, Object> queryDataLogicCheck(String enterprise, String hatchback, String vin) {
        return evsDataDetectResultModel.queryDataLogicCheck(enterprise, hatchback, vin);
    }

    @Override
    public Map<String, Object> queryDataLogicCheckInDay(String enterprise,
                                                        String hatchback,
                                                        String vin,
                                                        Long startTime,
                                                        Long endTime) {
        String[] dateBound = getDateBound(startTime, endTime);
        return evsDataDetectResultModel.queryDataLogicCheckInDay(enterprise, hatchback, vin, dateBound[0],
                dateBound[1]);
    }

    @Override
    public List<Map<String, Object>> queryDetectDataQualityByDay(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 Long startTime,
                                                                 Long endTime) {
        String[] dateBound = getDateBound(startTime, endTime);
        List<Map<String, Object>> maps = evsDataDetectResultModel.queryDetectDataQualityByDay(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);

        /*
        List<Map<String, Object>> res = Lists.newLinkedList();
        if (maps != null) {
            int size = maps.size();
            Map<String, Object> map;
            Object day;
            for (int i = 0; i < size; i++) {
                map = maps.get(i);
                day = map.remove("day");
                map =  aggByItem(map);
                map.put("day", day);
                res.add(map);
            }
        }
        return res; */
        return maps;
    }
    @Override
    public Map<String, Object> queryDetectQualityCountByDay(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            Long startTime,
                                                            Long endTime,
                                                            int pageNow,
                                                            int pageSize) {
        String[] dateBound = getDateBound(startTime, endTime);

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        evsDataDetectResultModel.queryDetectQualityCountByDay(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryDetectQualityCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       Long startTime,
                                                       Long endTime,
                                                       int pageNow,
                                                       int pageSize) {

        String[] dateBound = getDateBound(startTime, endTime);

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        evsDataDetectResultModel.queryDetectQualityCount(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryVinDetectQualityCount(String enterprise,
                                                          String hatchback,
                                                          String vin,
                                                          Long startTime,
                                                          Long endTime,
                                                          int pageNow,
                                                          int pageSize) {

        String[] dateBound = getDateBound(startTime, endTime);

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        evsDataDetectResultModel.queryVinDetectQualityCount(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryDetectQualityAllCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       Long startTime,
                                                       Long endTime) {
        String[] dateBound = getDateBound(startTime, endTime);
        return evsDataDetectResultModel.queryDetectQualityAllCount(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);
    }

    @Override
    public List<Map<String, Object>> queryDetectDataLogicQualityByDay(String enterprise,
                                                                      String hatchback,
                                                                      String vin,
                                                                      Long startTime,
                                                                      Long endTime) {
        String[] dateBound = getDateBound(startTime, endTime);
        List<Map<String, Object>> maps = evsDataDetectResultModel.queryDetectDataLogicQualityByDay(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);
        return maps;
    }

    @Override
    public List<Map<String, Object>> queryDetectMessageQualityByDay(String enterprise,
                                                                    String hatchback,
                                                                    String vin,
                                                                    Long startTime,
                                                                    Long endTime) {
        String[] dateBound = getDateBound(startTime, endTime);
        List<Map<String, Object>> maps = evsDataDetectResultModel.queryDetectMessageQualityByDay(enterprise,
                hatchback, vin, dateBound[0], dateBound[1]);
        return maps;
    }

    private String[] getDateBound(Long startTime, Long endTime) {
        String startDay = null;
        String endDay = null;
        if (startTime == null && endTime == null) {
            startDay = getTotay();
            endDay = startDay;
        }

        if (startTime == null && endTime != null) {
            endDay = getDateStr(endTime);
            startDay = endDay;
        }

        if (startTime != null && endTime == null) {
            startDay = getDateStr(startTime);
            endDay = getTotay();
        }

        if (startTime != null && endTime != null) {
            startDay = getDateStr(startTime);
            endDay = getDateStr(endTime);
        }

        return new String[]{startDay, endDay};
    }

    private Map<String, Object> putBaseInfo(Map<String, Object> res, String enterprise, String hatchback, String vin) {
        if (res == null) res = Maps.newHashMap();
        res.put("vin", vin);
        res.put("hatchback", hatchback);
        res.put("enterprise", enterprise);

        return res;
    }

    private String getTotay() {
        return DateUtil.getNow("yyyyMMdd");
    }

    private String getDateStr(long timestamp) {
        return DateUtil.getDateStr(timestamp, "yyyyMMdd");
    }


    private Map<String, Object> aggByItem(Map<String, Object> record){
        Map<String, Map<String, Object>> tmp = Maps.newHashMap();
        record.forEach((key, value) -> {
            EvsDetectDetail evsDetectDetail = evsDetectDetailCache.getEvsDetectDetail(key);
            if (evsDetectDetail != null && !evsDetectDetailCache.isLogicItem(evsDetectDetail.getId())) {
                String item = evsDetectDetail.getId().substring(0, 2);

                Map<String, Object> map = tmp.get(item);
                if(map == null){
                    map = Maps.newHashMap();
                    tmp.put(item, map);
                }
                map.put(key, value);
            }
        });

        return Maps.newHashMap(tmp);

    }
}
