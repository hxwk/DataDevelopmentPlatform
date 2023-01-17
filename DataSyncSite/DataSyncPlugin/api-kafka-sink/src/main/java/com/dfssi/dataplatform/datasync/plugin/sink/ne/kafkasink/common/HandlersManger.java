package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze.BaseProtoHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;

import java.util.Map;
import java.util.Set;

/**
 * Created by Hannibal on 2018-02-28.
 */
public class HandlersManger {

    public static Set<String> SKIPPACKMERGEPROTOS = Sets.newHashSet();

    public static Map<String, BaseProtoHandler> msgHandlers = Maps.newHashMap();

    private static Map<Short, BaseProtoHandler> upMsgHandlers = Maps.newHashMap();

    /**
     * 注册协议处理器
     *
     * @param msgId 请求消息ID
     * @param handler  协议处理器
     */
    public static void registerUpHandler(short msgId, BaseProtoHandler handler) {
        Validate.notNull(handler, "协议处理器不能为空");

        upMsgHandlers.put(msgId, handler);
    }

    public static BaseProtoHandler getUpHandlers(Short msgId) {
        return upMsgHandlers.get(msgId);
    }


    public static Map<Short, BaseProtoHandler> getAllUpMsgHandlers() {
        return upMsgHandlers;
    }
}
