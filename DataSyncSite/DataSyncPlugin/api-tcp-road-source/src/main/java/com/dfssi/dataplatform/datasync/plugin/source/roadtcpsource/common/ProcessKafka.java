package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.model.road.entity.VnndF003ResMsg;
import com.dfssi.dataplatform.datasync.model.road.entity.VnndInstructionResMsg;
import com.dfssi.dataplatform.datasync.model.road.entity.VnndLoginResMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessKafka {
    private static final Logger logger = LoggerFactory.getLogger("processKafka");

    public static void processEvent(VnndResMsg res, String taskId, String msgid, String topic, ChannelProcessor channelProcessor){
        logger.info("封装VnndResMsgEvent推送至kafkachannel,taskId:" + taskId + ",msgid:" + msgid+",topic:" + topic+",vnndResMsg:"+res);
        try {
            SimpleEvent event = new SimpleEvent();
            String jonStr = JSON.toJSONString(res);
            if (res instanceof VnndInstructionResMsg) {
                VnndInstructionResMsg viRes = (VnndInstructionResMsg)res;
                jonStr = JSON.toJSONString(viRes);
            } else if (res instanceof VnndLoginResMsg) {
                VnndLoginResMsg vlRes = (VnndLoginResMsg)res;
                jonStr = JSON.toJSONString(vlRes);
            } else if (res instanceof VnndF003ResMsg) {
                VnndF003ResMsg vfRes = (VnndF003ResMsg)res;
                jonStr = JSON.toJSONString(vfRes);
            }
            KafkaHeader header = new KafkaHeader();
            header.setTaskId(taskId);
            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(jonStr.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            channelProcessor.processEvent(event);
            logger.info("发送VnndResMsgEvent到kafkachannel成功,event="+event);
        } catch (Exception e) {
            logger.error("发送VnndResMsgEvent到kafkachannel异常",e);
        }
    }

    public static void processEvent(String json, String taskId, String msgid,String topic, ChannelProcessor channelProcessor){
        //key是event中header的Map的key
        logger.info("封装jsonEvent推送至kafkachannel,taskId:" + taskId + ",msgid:" + msgid+",topic:" + topic+",json:"+json);
        try {
            SimpleEvent event = new SimpleEvent();
            KafkaHeader header = new KafkaHeader();
            header.setMsgid(taskId);
            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(json.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            channelProcessor.processEvent(event);
            logger.info("发送jsonEvent到channel成功,event="+event);
        } catch (Exception e) {
            logger.error("发送jsonEvent到kafkachannel异常",e);
        }
    }

    public static void pushToKafka(String json, String topic, ChannelProcessor channelProcessor){
        logger.info("封装simpleJsonEvent推送至kafkachannel,topic:" + topic+",json:"+json);
        try {
            SimpleEvent event = new SimpleEvent();
            KafkaHeader header = new KafkaHeader();
//            header.setMsgid(taskId);
//            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(json.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            //logger.info("key:{}",Constants.KEY_HEADER);
            channelProcessor.processEvent(event);
            logger.info("发送simpleJsonEvent到kafkachannel成功,event="+event);
        } catch (Exception e) {
            logger.error("发送simpleJsonEvent到kafkachannel异常",e);
        }
    }
}
