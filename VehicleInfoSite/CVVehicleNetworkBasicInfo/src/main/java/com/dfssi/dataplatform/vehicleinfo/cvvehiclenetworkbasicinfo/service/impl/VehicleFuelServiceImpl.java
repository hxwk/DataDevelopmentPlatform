package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelAnalysisInDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelCountByDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelDistributedEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.VehicleFuelModel;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.VehicleFuelService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.elasticsearch.search.SearchHits;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/14 20:51
 */
@Service
public class VehicleFuelServiceImpl implements VehicleFuelService {
    private final Logger logger = LoggerFactory.getLogger(VehicleFuelServiceImpl.class);

    @Autowired
    private VehicleFuelModel vehicleFuelModel;

    @Override
    public Map<String, Object> countFuelByDay(String vid,
                                              long starttime,
                                              long endtime,
                                              int pageNum,
                                              int pageSize) {

       String startDay = new DateTime(starttime).toString("yyyyMMdd");
       String endDay = new DateTime(endtime).toString("yyyyMMdd");

       Page<VehicleFuelAnalysisInDaysEntity> page = PageHelper.startPage(pageNum, pageSize);
       vehicleFuelModel.countFuelByDay(vid, startDay, endDay);

        long total = page.getTotal();

        List<VehicleFuelAnalysisInDaysEntity> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    /**
     *根据指定类型统计油耗 比如： 车型
     */
    @Override
    public Map<String, Object> countFuelByItem(String item,
                                               String vals,
                                               long starttime,
                                               long endtime) {
        String startDay = new DateTime(starttime).toString("yyyyMMdd");
        String endDay = new DateTime(endtime).toString("yyyyMMdd");



        return null;
    }

    @Override
    public Map<String, Object> countFuelAnalysisInDays(String vid, long starttime, long endtime) {

        String startDay = new DateTime(starttime).toString("yyyyMMdd");
        String endDay = new DateTime(endtime).toString("yyyyMMdd");

        List<VehicleFuelAnalysisInDaysEntity> analysisInDaysEntities
                = vehicleFuelModel.countFuelAnalysisInDays(vid, startDay, endDay);

        //油耗分布统计分析计算
        double totalmile = 0.0;
        double totalfuel = 0.0;
        double totalbrakemile = 0.0;
        double totalidlefuel = 0.0;
        double totaltime = 0.0;
        Set<VehicleFuelDistributedEntity> speeds = Sets.newHashSet();
        Set<VehicleFuelDistributedEntity> accs   = Sets.newHashSet();
        Set<VehicleFuelDistributedEntity> rpms   = Sets.newHashSet();
        Set<VehicleFuelDistributedEntity> gears  = Sets.newHashSet();

        Map<Integer, Set<VehicleFuelDistributedEntity>> gearSpeeds = Maps.newHashMap();
        Set<VehicleFuelDistributedEntity> set;
        int gear;
        for(VehicleFuelAnalysisInDaysEntity analysisInDaysEntity : analysisInDaysEntities){

            totalmile += analysisInDaysEntity.getTotalmile();
            totalfuel += analysisInDaysEntity.getTotalfuel();
            totalbrakemile += analysisInDaysEntity.getTotalbrakemile();
            totalidlefuel += analysisInDaysEntity.getTotalidlefuel();
            totaltime += analysisInDaysEntity.getTotaltime();

            String item = analysisInDaysEntity.getSpeedpair();
            speeds.addAll(parseNormalDistributedItems(item, "speed", ",", 10));

            item = analysisInDaysEntity.getAccpair();
            accs.addAll(parseNormalDistributedItems(item, "acc", ",", 5));

            item = analysisInDaysEntity.getRpmpair();
            rpms.addAll(parseNormalDistributedItems(item, "rpm", ",", 300));

            item = analysisInDaysEntity.getGearpair();
            gears.addAll(parseNormalDistributedItems(item, "gear", ",", 1));

            item = analysisInDaysEntity.getGearspeedpair();
            if(item != null) {
                String[] items = item.split("#");
                String[] gearSpeed;
                for (String t : items) {
                    gearSpeed = t.split(":");
                    if (gearSpeed.length == 2) {
                        gear = Integer.parseInt(gearSpeed[0]);
                        set = gearSpeeds.get(gear);
                        if (set == null) {
                            set = Sets.newHashSet();
                            gearSpeeds.put(gear, set);
                        }
                        set.addAll(parseNormalDistributedItems(gearSpeed[1], "speed", ",", 10));
                    }
                }
            }
        }

        Map<String, Object> res = Maps.newLinkedHashMap();
        res.put("totalmile", totalmile);
        res.put("totalfuel", totalfuel);
        res.put("totalbrakemile", totalbrakemile);
        res.put("totalidlefuel", totalidlefuel);
        res.put("totaltime", totaltime);

        res.put("speeds", speeds);
        res.put("accs", accs);
        res.put("rpms", rpms);
        res.put("gears", gears);
        res.put("gearSpeeds", gearSpeeds);

        return res;
    }

    @Override
    public List<VehicleFuelCountByDaysEntity> countFuelAnalysisByDays(String vid, String startDay, String endDay){
        return vehicleFuelModel.countFuelAnalysisByDays(vid, startDay, endDay);
    }


    @Override
    public List<Map<String, Object>> searchLatestStatusByVid(String vid) {
        SearchHits searchHits = vehicleFuelModel.searchLatestStatusByVid(vid);
        List<Map<String, Object>> res;
        if(searchHits.getTotalHits() == 0){
            res = Lists.newArrayList();
        }else{
            res = Arrays.stream(searchHits.getHits()).map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());
        }
        return  res;
    }

    private Set<VehicleFuelDistributedEntity> parseNormalDistributedItems(String item,
                                                                          String type,
                                                                          String seq,
                                                                          int levelLength){
        Set<VehicleFuelDistributedEntity> fuelDistributedEntities = Sets.newHashSet();
        if(item != null){
            String[] pairs = item.split(";");
            VehicleFuelDistributedEntity fuelDistributedEntity;
            for(String pair : pairs){
                fuelDistributedEntity = newFuelDistributedEntity(pair, type,seq, levelLength);
                if(fuelDistributedEntity != null){
                    fuelDistributedEntities.add(fuelDistributedEntity);
                }
            }
        }
        return fuelDistributedEntities;
    }

    private VehicleFuelDistributedEntity newFuelDistributedEntity(String pair,
                                                                  String type,
                                                                  String seq,
                                                                  int levelLength){
        VehicleFuelDistributedEntity fuelDistributedEntity = null;

        //s"${level}${sep}${count}${sep}${time}${sep}${precisionDouble(mile, 1)}${sep}${precisionDouble(fuel, 5)}"
        String[] distributes = pair.split(seq);
        if(distributes.length == 5){
            fuelDistributedEntity = new VehicleFuelDistributedEntity(type,
                    Integer.parseInt(distributes[0]),
                    levelLength,
                    Long.parseLong(distributes[1]),
                    Long.parseLong(distributes[2]),
                    Double.parseDouble(distributes[3]),
                    Double.parseDouble(distributes[4]));
        }

        return fuelDistributedEntity;
    }


}
