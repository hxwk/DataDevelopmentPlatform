package com.dfssi.dataplatform.datasync.plugin.interceptor.numeric;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/5/2
 * @description numeric interceptor
 * 主要用于数字转换 y=kx+b
 *
 */
public class NumInterceptor implements Interceptor{
    private static final Logger logger = LoggerFactory.getLogger(NumInterceptor.class);
    private final String factor;
    private final String offset;

    public NumInterceptor(String factor, String offset) {
        this.factor = factor;
        this.offset = offset;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        //original 原始值
        Double original = Double.parseDouble(new String(event.getBody()));
        Double destValue = original * Double.parseDouble(factor) + Double.parseDouble(offset);
        event.setBody(String.valueOf(destValue).getBytes(Charset.forName("utf-8")));
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> eventList = Lists.newArrayList();
        for (Event e : events) {
            eventList.add(intercept(e));
        }
        return eventList;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {
        private static final String FACTOR_KEY = "factor";
        private static final String OFFSET_KEY = "offset";

        private String factor;
        private String offset;

        @Override
        public void configure(Context context) {

            factor = context.getString(FACTOR_KEY,"1");
            offset = context.getString(OFFSET_KEY,"0");
        }

        @Override
        public Interceptor build() {
            if(null == factor &&null == offset){
                logger.warn("factor and offset cannot be empty all the time");
            }
            return new NumInterceptor(factor, offset);
        }
    }
}
