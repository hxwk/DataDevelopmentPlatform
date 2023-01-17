package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.entity.count.*;
import com.dfssi.dataplatform.model.EvsDataCountResultModel;
import com.dfssi.dataplatform.service.EvsDataCountResultService;
import com.dfssi.dataplatform.utils.DateTimeBoundary;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
 * @version 2018/4/21 12:54
 */
@Service
public class EvsDataCountResultServiceImpl implements EvsDataCountResultService {
    private final Logger logger = LoggerFactory.getLogger(EvsDataCountResultServiceImpl.class);

    @Autowired
    private EvsDataCountResultModel evsDataCountResultModel;

    @Override
    public TotalMileAndTime queryTotalMileAndTime(){
        return evsDataCountResultModel.queryTotalMileAndTime();
    }

    @Override
    public TotalRunningMsg queryTotalMileAndTimeByVin(String vin){
        TotalRunningMsg totalMileAndTime = evsDataCountResultModel.queryTotalMileAndTimeByVin(vin);
        if(totalMileAndTime == null){
            totalMileAndTime = new TotalRunningMsg();
            totalMileAndTime.setTotalDuration(0L);
            totalMileAndTime.setTotalMileage(0.0);
            totalMileAndTime.setTotalPowerConsumption(0.0);
            totalMileAndTime.setTotalrun(0);
            totalMileAndTime.setTotalcharge(0);
        }
        return totalMileAndTime;
    }

    @Override
    public Map<String, Double> queryTotalEnergy(){
        Map<String, Double> energy = evsDataCountResultModel.queryTotalEnergy();
        Double totalPowerConsumption = energy.remove("totalpowerconsumption");
        if(totalPowerConsumption == null){
            totalPowerConsumption = 0.0;
        }
        energy.put("totalPowerConsumption", totalPowerConsumption / 1000.0);
        return energy;
    }

    @Override
    public MileDetail queryMileDetail(String vin,
                                      Long startTime,
                                      Long endTime) {

        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 1, DateTimeBoundary.TimeIntervalUnit.WEEKS, "yyyyMMdd");

        return evsDataCountResultModel.queryMileDetail(vin,
                timeBoundary.getLower(), timeBoundary.getUpper());
    }

    @Override
    public List<MaxOnlineAndRun> queryCurrentMonthMaxOnlineAndRunByDay() {

        DateTime.Property property = DateTime.now().dayOfMonth();
        String startDay = property.withMinimumValue().toString("yyyyMMdd");
        String endDay = property.withMaximumValue().toString("yyyyMMdd");

        return evsDataCountResultModel.queryMaxOnlineAndRunByDay(startDay, endDay);
    }

    @Override
    public Map<String, Object> countMonthTotalOnlineAndRun(String month) {

        if(month == null){
            //取上一个月
            month = DateTime.now().minusMonths(1).toString("yyyyMM");
        }

        DateTime time = DateTime.parse(month, DateTimeFormat.forPattern("yyyyMM"));
        DateTime.Property property = time.dayOfMonth();

        String startDay = property.withMinimumValue().toString("yyyyMMdd");
        String endDay = property.withMaximumValue().toString("yyyyMMdd");

        Map<String, Object> totalOnlineAndRun =
                evsDataCountResultModel.countMonthTotalOnlineAndRun(startDay, endDay);
        totalOnlineAndRun.put("month", month);

        return totalOnlineAndRun;
    }

    @Override
    public Map<String, Object> countVehicleMileAndTime(String enterprise,
                                                             String hatchback,
                                                             String vin,
                                                             Long startTime,
                                                             Long endTime,
                                                             int pageNow,
                                                             int pageSize) {

        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 7, DateTimeBoundary.TimeIntervalUnit.DAYS, "yyyyMMdd");

        Page<VehicleMileAndTime> page = PageHelper.startPage(pageNow, pageSize);
        evsDataCountResultModel.countVehicleMileAndTime(enterprise, hatchback, vin,
                timeBoundary.getLower(), timeBoundary.getUpper());

        long total = page.getTotal();

        List<VehicleMileAndTime> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public List<Map<String, Object>> queryWarningCountByDay(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            Long startTime,
                                                            Long endTime) {

        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 7, DateTimeBoundary.TimeIntervalUnit.DAYS, "yyyyMMdd");

        return evsDataCountResultModel.queryWarningCountByDay(enterprise, hatchback, vin,
                timeBoundary.getLower(), timeBoundary.getUpper());
    }

    @Override
    public Map<String, Object> queryWarningCount(String enterprise,
                                                 String hatchback,
                                                 String vin,
                                                 Long startTime,
                                                 Long endTime) {

        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 7, DateTimeBoundary.TimeIntervalUnit.DAYS, "yyyyMMdd");

        return evsDataCountResultModel.queryWarningCount(enterprise, hatchback, vin,
                timeBoundary.getLower(), timeBoundary.getUpper());
    }

    @Override
    public Map<String, Object> queryWarningCountTotal(String enterprise,
                                                      String hatchback,
                                                      String vin) {

        return evsDataCountResultModel.queryWarningCountTotal(enterprise, hatchback, vin);
    }

    @Override
    public Map<String, Object> queryOnlineCountByDay(String enterprise,
                                                          String hatchback,
                                                          Long startTime,
                                                          Long endTime,
                                                          int pageNow,
                                                          int pageSize ) {
        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 7, DateTimeBoundary.TimeIntervalUnit.DAYS, "yyyyMMdd");

        Page<VehicleOnlineCount> page = PageHelper.startPage(pageNow, pageSize);
        evsDataCountResultModel.queryOnlineCountByDay(enterprise, hatchback,
                timeBoundary.getLower(), timeBoundary.getUpper());

        long total = page.getTotal();

        List<VehicleOnlineCount> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryOnlineCountInEnterpriseByDay(String enterprise,
                                                                 String hatchback,
                                                                 Long startTime,
                                                                 Long endTime,
                                                                 int pageNow,
                                                                 int pageSize ) {
        DateTimeBoundary timeBoundary = new DateTimeBoundary(startTime,
                endTime, 7, DateTimeBoundary.TimeIntervalUnit.DAYS, "yyyyMMdd");

        Page<VehicleOnlineCount> page = PageHelper.startPage(pageNow, pageSize);
        evsDataCountResultModel.queryOnlineCountInEnterpriseByDay(enterprise, hatchback,
                timeBoundary.getLower(), timeBoundary.getUpper());

        long total = page.getTotal();

        List<VehicleOnlineCount> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

}
