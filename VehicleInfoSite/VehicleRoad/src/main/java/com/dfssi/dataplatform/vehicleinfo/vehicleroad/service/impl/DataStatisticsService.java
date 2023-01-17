package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao.DataStatisticsDao;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.HistoryDataQueryEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IDataStatisticsService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHits;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//import org.elasticsearch.client.transport.TransportClient;

/**
 * Created by yanghs on 2018/9/12.
 */
@Service
public class DataStatisticsService implements IDataStatisticsService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataStatisticsDao dataStatisticsDao;


    @Override
    public  Map<String, Object> queryHistoryData(HistoryDataQueryEntity historyDataQueryEntity) {

        SearchHits hits = dataStatisticsDao.queryHistoryData(historyDataQueryEntity);

        long total = hits.getTotalHits();
        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(hits);

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public Map<String, Object> exportHistoryData(HistoryDataQueryEntity historyDataQueryEntity) {
        return dataStatisticsDao.exportHistoryData(historyDataQueryEntity);
    }

    @Override
    public List<Map<String, Object>>  findAllFields(String label) {
        return dataStatisticsDao.findAllFields(label);
    }

    @Override
    public Map<String, Object> queryStatisticsByDay(String vid,
                                                          Long startTime,
                                                          Long stopTime,
                                                          int pageNow,
                                                          int pageSize) {

        String startDay = new DateTime(startTime).toString("yyyyMMdd");
        String stopDay = new DateTime(stopTime).toString("yyyyMMdd");

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        dataStatisticsDao.queryStatisticsByDay(vid, startDay, stopDay);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryStatisticsTrip(String vid,
                                                   Long startTime,
                                                   Long stopTime,
                                                   int pageNow, int pageSize) {
        String startDay = new DateTime(startTime).toString("yyyyMMdd");
        String stopDay = new DateTime(stopTime).toString("yyyyMMdd");

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        dataStatisticsDao.queryStatisticsTrip(vid, startDay, stopDay);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryStatisticsByMonth(String vid,
                                                      Long startTime,
                                                      Long stopTime,
                                                      int pageNow,
                                                      int pageSize) {

        String startDay = new DateTime(startTime).dayOfMonth().withMinimumValue().toString("yyyyMMdd");
        String stopDay = new DateTime(stopTime).dayOfMonth().withMaximumValue().toString("yyyyMMdd");

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        dataStatisticsDao.queryStatisticsByMonth(vid, startDay, stopDay);

        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }
}
