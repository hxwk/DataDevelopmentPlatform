package com.yaxon.vn.nd.ne.tas.net.tcp;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yaxon.vn.nd.ne.tas.IProtocolHandler;
import org.apache.commons.lang.Validate;

import java.util.Map;
import java.util.Set;

/**
 * Author: 程行荣
 * Time: 2013-11-18 14:37
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 「上行」协议分发器。
 */
public class ProtocolDispatcher {

    private Map<Short, Set<IProtocolHandler>> msgHandlers = Maps.newHashMap();

    /**
     * 注册协议处理器
     *
     * @param msgId 请求消息ID
     * @param handler  协议处理器
     */
    public void registerHandler(short msgId, IProtocolHandler handler) {
        Validate.notNull(handler, "协议处理器不能为空");

        Set<IProtocolHandler> handlers = msgHandlers.get(msgId);
        if (handlers == null) {
            handlers = Sets.newHashSet();
            msgHandlers.put(msgId, handlers);
        }
        handlers.add(handler);
    }

    public Set<IProtocolHandler> getHandlers(short msgId) {
        return msgHandlers.get(msgId);
    }
 }
