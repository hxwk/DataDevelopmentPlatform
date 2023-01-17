package com.dfssi.dataplatform.cache.database;

import com.dfssi.dataplatform.cache.AbstractCache;
import com.dfssi.dataplatform.entity.database.EvsDetectDetail;
import com.dfssi.dataplatform.service.EvsDetectDetailService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *  evs_detect_detail 表缓存
 * @author LiXiaoCong
 * @version 2018/4/26 9:34
 */
public class EvsDetectDetailCache extends AbstractCache<String, Object> {

    private final String allEvsDetectDetail = "allEvsDetectDetail";
    private final String baseEvsDetectRule  = "baseEvsDetectRule";

    private final Map<String, String> dataItemNameMap;


    private final String logicItemName = "逻辑校验";
    private final String logicItemId = "10";

    @Autowired
    private EvsDetectDetailService evsDetectDetailService;

    public EvsDetectDetailCache(ExecutorService executorService) {
        super(2, 24, TimeUnit.HOURS, executorService);
        this.dataItemNameMap = Maps.newHashMap();
        initDataItemNameMap();
    }

    private void initDataItemNameMap(){
        this.dataItemNameMap.put("00", "整车数据");
        this.dataItemNameMap.put("01", "驱动电机数据");
        this.dataItemNameMap.put("02", "燃料电池");
        this.dataItemNameMap.put("03", "发动机数据");
        this.dataItemNameMap.put("04", "车辆位置数据");
        this.dataItemNameMap.put("05", "极值数据");
        this.dataItemNameMap.put("06", "报警数据");
        this.dataItemNameMap.put("07", "可充电储能装置温度数据");
        this.dataItemNameMap.put("08", "可充电储能装置电压数据");
        this.dataItemNameMap.put("10", "逻辑校验");
        this.dataItemNameMap.put("90", "报文完整性");
    }


    @Override
    protected Object loadData(String s) {
        switch (s) {
            case allEvsDetectDetail :
                List<EvsDetectDetail> evsDetectDetails = evsDetectDetailService.queryAll();
                Map<String, EvsDetectDetail> evsDetectDetailMap = Maps.newHashMap();
                evsDetectDetails.forEach(evsDetectDetail -> {
                    evsDetectDetailMap.put(evsDetectDetail.getDetectName().toLowerCase(), evsDetectDetail);
                });
                return evsDetectDetailMap;
            case baseEvsDetectRule :
                return evsDetectDetailService.allBaseDetectDetail();

        }
        return null;
    }

    public EvsDetectDetail getEvsDetectDetail(String detectName){
        Map<String, EvsDetectDetail> cache = (Map<String, EvsDetectDetail>)getCache(allEvsDetectDetail);
        return cache.get(detectName.toLowerCase());
    }

    public List<Map<String, Object>> getBaseEvsDetectRules(){
        return (List<Map<String, Object>>)getCache(baseEvsDetectRule);

    }

    public String getItemName(String id){
        return this.dataItemNameMap.get(id);
    }

    public boolean isLogicItem(String id){
        if(id == null) return false;
        return id.startsWith(logicItemId);
    }

    public String getLogicItemName(){
        return logicItemName;
    }
}
