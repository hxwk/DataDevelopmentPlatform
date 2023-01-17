package com.dfssi.dataplatform.datasync.plugin.interceptor;

import com.dfssi.dataplatform.datasync.plugin.interceptor.common.MsgCodeConstant;
import com.dfssi.dataplatform.datasync.plugin.interceptor.decode.ProtocolDecoder;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 808 protocol(808) interceptor and parse to event to be comprehensible
 * @author jianKang
 * @date 2017/12/13
 */
public class ProtocolInterceptor implements Interceptor {
    static final Logger logger = LoggerFactory.getLogger(ProtocolInterceptor.class);
    ProtocolDecoder protocolDecoder ;
    @Override
    public void initialize() {
        //no-op
    }

    @Override
    public Event intercept(Event event) {
        ProtoMsg protoMsg = new ProtoMsg();
        protocolDecoder = new ProtocolDecoder();
        Map<String,String> headers = event.getHeaders();
        byte[] body = event.getBody();
        String key = headers.get("key");
        String[] headerMap = key.split(MsgCodeConstant.SPLITECHAR);
        String msgID = headerMap[1];
        logger.info("current protocol type is "+msgID);
        logger.info("current message is " + new String(body, Charsets.UTF_8));
        protoMsg.setMsgId(msgID);
        protoMsg.setDataBuf(body);
        byte[] afterDecodeBody = protocolDecoder.interceptBody(msgID,protoMsg);
        event.setBody(afterDecodeBody);
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for(Event event: events){
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {
        //no-op
    }

    public static class Builder implements Interceptor.Builder{

        @Override
        public void configure(Context context) {

        }

        @Override
        public Interceptor build() {
            return new ProtocolInterceptor();
        }
    }
}
