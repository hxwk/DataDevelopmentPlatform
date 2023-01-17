package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util;

import com.alibaba.fastjson.JSON;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.yaxon.vn.nd.tbp.si.VnndF003ResMsg;
import com.yaxon.vn.nd.tbp.si.VnndInstructionResMsg;
import com.yaxon.vn.nd.tbp.si.VnndLoginResMsg;
import com.yaxon.vn.nd.tbp.si.VnndResMsg;
import org.apache.log4j.Logger;

/**
 * Created by Hannibal on 2018-02-03.
 */
public class KafkaUtil {

    private static Logger logger = Logger.getLogger(KafkaUtil.class);

    public static void processEvent(VnndResMsg res, String taskId, String msgid,
                             String topic, ChannelProcessor channelProcessor) {
        logger.info("  开始封装event " + taskId + ", msgid = " + msgid);
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
            event.setBody(jonStr.getBytes());
            event.getHeaders().put(Constants.TASK_ID_KEY, taskId);
            event.getHeaders().put(Constants.MSG_ID_KEY, msgid);
            event.getHeaders().put(Constants.TOPIC_HEADER, topic);

            channelProcessor.processEvent(event);
            logger.info("发送event到chennel成功 " + taskId + ", msgid = " + msgid);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

}
