package com.dfssi.dataplatform.plugin.tcpnesource.handler;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
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
    void handle(ProtoMsg msg, String taskId, ChannelProcessor channelProcessor,String vin);

    void setup();
}
