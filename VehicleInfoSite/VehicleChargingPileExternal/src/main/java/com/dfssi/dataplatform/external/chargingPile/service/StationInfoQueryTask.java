package com.dfssi.dataplatform.external.chargingPile.service;

import com.dfssi.dataplatform.external.common.PropertiesUtil;
import com.dfssi.dataplatform.external.common.PubGlobal;
import com.dfssi.dataplatform.external.common.shedule.ITask;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/12 19:33
 */
public class StationInfoQueryTask implements ITask {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RestTemplate restTemplate;

    public StationInfoQueryTask(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int exec() {
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator = PubGlobal.secretMap.entrySet().iterator();
        Properties properties = PropertiesUtil.getProperties("application.properties");
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, String>> entry = iterator.next();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", UUID.randomUUID().toString());
            MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
            Date date = new Date();//取时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, Integer.parseInt(properties.getProperty("shedule.stationInfo.beforeday")));//把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); //这个时间就是日期往后推一天的结果
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(date);
            System.out.println(dateString);
            postParameters.add("lastQueryTime", dateString + " 00:00:00");
            postParameters.add("operatorId", entry.getKey());
            String pageSize=properties.getProperty("shedule.stationInfo.pageSize");
            postParameters.add("pageSize",pageSize);
            String url = properties.getProperty("shedule.stationInfo.url");
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, headers);
            Boolean sendFlag = true;
            int pageNo = 1;
            while (sendFlag) {
                postParameters.remove("pageNo");
                postParameters.add("pageNo", pageNo + "");
                HashMap<String, String> map = restTemplate.postForObject(url, requestEntity, HashMap.class);
                logger.info("充电站信息定时任务查询结果:" + map);
                if (!map.get("Ret").equals("0") || (map.get("Ret").equals("0") && Integer.parseInt(map.get("Size")) < Integer.parseInt(pageSize))) {
                    sendFlag = false;
                }
                pageNo++;
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }
}