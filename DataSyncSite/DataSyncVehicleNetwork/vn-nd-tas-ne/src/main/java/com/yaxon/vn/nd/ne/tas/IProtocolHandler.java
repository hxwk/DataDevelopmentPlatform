package com.yaxon.vn.nd.ne.tas;

import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;

/**
 * Author: 程行荣
 * Time: 2013-11-10 16:09
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 协议处理器接口
 */
public interface IProtocolHandler {
    /**
     * 处理消息。
     *
     * @param msg 协议消息
     */
    void handle(ProtoMsg msg);
}
