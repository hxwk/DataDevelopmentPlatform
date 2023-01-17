package com.dfssi.dataplatform.datasync.plugin.http;

import com.dfssi.dataplatform.datasync.common.utils.StringUtil;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangeListener;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangedEvent;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.EventBuilder;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HSF on 2018/6/21.
 */
public class HttpSourceEventProcessor implements ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSourceEventProcessor.class);
    private static volatile ChannelProcessor channelProcessor;
    private static String taskId;
    private static HttpSourceEventProcessor instance = null;


    public static ChannelProcessor getChannelProcessor() {
        return channelProcessor;
    }

    public static void setChannelProcessor(ChannelProcessor channelProcessor) {
        HttpSourceEventProcessor.channelProcessor = channelProcessor;
    }

    public static String getTaskId() {
        return taskId;
    }

    public static void setTaskId(String taskId) {
        HttpSourceEventProcessor.taskId = taskId;
    }

    private HttpSourceEventProcessor(){
    }

   public static HttpSourceEventProcessor getInstance(){
        if (null==instance){
            instance = new HttpSourceEventProcessor();
        }
        return instance;
    }

    @Override
    public void performed(ValueChangedEvent e) {
        LOG.info("HTTPSource->performed e = {}",e.getValue());
        processEvent(e.getValue().toString());
    }

    public void processEvent(String eventbody){
        Preconditions.checkNotNull(eventbody,"eventBody can not be null!!!");
        Event event = EventBuilder.withBody(eventbody.getBytes());
        event.getHeaders().put(HTTPSourceConfigurationConstants.TASK_ID,taskId);
        channelProcessor.processEvent(event);
        LOG.info("HttpSource->processEvent successfully , event = {}",event);
    }
}
