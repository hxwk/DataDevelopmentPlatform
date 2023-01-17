package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler;

/**
 * Author: 程行荣
 * Time: 2013-11-10 16:09
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;

/**
 * 协议处理器接口
 */
public interface IProtocolHandler {
    /**
     * 处理消息。
     *
     * @param msg 协议消息
     */
    void handle(ProtoMsg msg, String taskId, ChannelProcessor channelProcessor);

    void setup();
}
