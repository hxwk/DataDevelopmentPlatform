package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp;


import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.TcpException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 终端接入通道接口。具体实现为TCP或UDP通道。
 */
public interface AccessChannel {

    /**
     * 开启通道
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 关闭通道
     */
    void stop();

    /**
     * 向终端发送请求消息，期望得到应答消息
     *
     * @param req 下行请求协议消息。该参数一旦由底层负责释放，不能重复使用。
     * @param resId 应答消息ID
     * @return 应答消息Future
     */
    ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short resId);

    /**
     * 向终端发送请求消息，期望得到应答消息
     *
     * @param req 下行请求协议消息。该参数一旦由底层负责释放，不能重复使用。
     * @param resIds 多个应答消息ID
     * @return 应答消息Future
     */
    ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short... resIds);

    /**
     * 向终端发送消息，不需要应答
     *
     * @param msg 待发送的消息
     * @return 发送结果Future
     */
    void sendMessage(ProtoMsg msg) throws TcpException;

    Object getConnectionManager();

}
