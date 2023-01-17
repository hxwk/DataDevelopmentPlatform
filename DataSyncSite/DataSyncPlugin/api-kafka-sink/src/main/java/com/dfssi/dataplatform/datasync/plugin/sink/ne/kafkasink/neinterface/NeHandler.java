package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.neinterface;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.model.newen.entity.NEMessage2KafkaBean;
import com.dfssi.dataplatform.datasync.model.newen.entity.Req_02;

import java.util.Map;

/**
 * @author JianKang
 * @date 2018/4/3
 * @description
 */
public interface NeHandler {
    /**
     * 新能源上行消息处理方法
     * @param upMsg 消息报文
     * @return Req_02 回传消息对象
     */
    Req_02 doUpMsg(ProtoMsg upMsg);

    /**
     * 获取解析后新能源消息对象
     * @param req
     * @return
     */
    NEMessage2KafkaBean getNeMsgObject(Req_02 req);

    /**
     * 获取实时数据对象
     * @param req 报文REQ对象
     * @param headers EVENT header
     * @param eventBody EVENT body
     * @return 解析后REQ对象
     */
    Req_02 getRealDataObject(Req_02 req, Map<String, String> headers, byte[] eventBody);
}
