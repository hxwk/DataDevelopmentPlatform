package com.dfssi.dataplatform.ide.video.mvc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.ide.video.mvc.dao.VideoMonitorListDao;
import com.dfssi.dataplatform.ide.video.mvc.entity.VideoMonitorListEntity;
import com.dfssi.dataplatform.ide.video.mvc.service.IVideoTaskAccessService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * kafka生产与消费
 * Created by yanghs on 2018/8/23.
 */
@Component
public class KafkaService {
    public static Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private VideoMonitorListDao videoMonitorListDao;

    @Autowired
    private IVideoTaskAccessService videoTaskAccessService;

    /**
     * 生产kafka数据
     * @param topic
     * @param key
     * @param data
     * @return
     */
    public String sendKafka(String topic, String key, String data) {
        kafkaTemplate.send(topic, key, data);
        return "success";
    }

    /**
     * 消费数据
     * @param cr
     * @throws Exception
     */
    @KafkaListener( topics = "test1")
    public void videolist(ConsumerRecord<?, ?> cr) throws Exception {
        logger.info("{} - {} : {}", cr.topic(), cr.key(), cr.value());
    }

    /**
     * 消费数据，监控kafka视频资源，存入mysql数据库
     * @param cr
     * @throws Exception
     */
    @KafkaListener(id="videoList", topics = "AVRESOURCELIST_TOPIC")
    public void myListen(ConsumerRecord<String, String> cr) throws Exception {
        //logger.info("{} - {} : {}", cr.topic(), cr.key(), cr.value());
        String str = cr.value();
        try {
            VideoMonitorListEntity videoMonitorListEntity = new VideoMonitorListEntity();
            JSONObject jsonObject = JSON.parseObject(str);
            JSONObject body = jsonObject.getJSONObject("avResourceList");
            int avResourcesSum = body.getInteger("avResourceNum");
            int flowId = body.getInteger("flowNo");
            String sim = body.getString("sim");
            String vid = body.getString("vid");
            videoMonitorListEntity.setAvResourcesSum(avResourcesSum);
            videoMonitorListEntity.setFlowId(flowId);
            videoMonitorListEntity.setSim(sim);
            videoMonitorListEntity.setVid(vid);
            JSONArray jsonArray = body.getJSONArray("avResourceListItemList");
            for(int i=0; i<jsonArray.size(); i++){
                String uid = UUID.randomUUID().toString().replaceAll("-", "");
                JSONObject jobj = jsonArray.getJSONObject(i);
                String ag = jobj.getString("alarmMarks") == null?"":jobj.getString("alarmMarks");
                String alarmFlag =ag.substring(1,ag.length()-1);
                int avResourceType = jobj.getInteger("avResourceType");
                int channelNo = jobj.getInteger("channelNo");
                Long eTime = jobj.getLong("endTime");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String endDate = null;
                if(null != eTime){
                    endDate = dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(eTime), ZoneId.systemDefault()));
                }
                Long fSize = jobj.getLong("fileSize");
                int fileSize = 0;
                if(null != fSize){
                    fileSize = Math.round(fSize/1024/1024);
                }
                Long sTime = jobj.getLong("startTime");
                String startDate = null;
                if(null != sTime){
                    startDate = dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(sTime), ZoneId.systemDefault()));
                }
                int memoryType = jobj.getInteger("storageType");
                int codeStreamType = jobj.getInteger("streamType");
                videoMonitorListEntity.setId(uid);
                videoMonitorListEntity.setIsDeleted("0");
                videoMonitorListEntity.setAlarmFlag(alarmFlag);
                videoMonitorListEntity.setAvResourceType(avResourceType);
                videoMonitorListEntity.setChannelNo(channelNo);
                videoMonitorListEntity.setEndDate(endDate);
                videoMonitorListEntity.setFileSize(fileSize);
                videoMonitorListEntity.setStartDate(startDate);
                videoMonitorListEntity.setMemoryType(memoryType);
                videoMonitorListEntity.setCodeStreamType(codeStreamType);
                videoTaskAccessService.deleteList(videoMonitorListEntity);
                videoMonitorListDao.insert(videoMonitorListEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
