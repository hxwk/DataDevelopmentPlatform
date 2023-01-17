package com.yaxon.vn.nd.tas.handler;

import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.AccessChannel;
import com.yaxon.vn.nd.tas.IProtocolHandler;
import com.yaxon.vn.nd.tas.exception.TasException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.net.tcp.TcpChannel;
import com.yaxon.vn.nd.tas.net.tcp.TcpChannelFactory;
import com.yaxon.vn.nd.tas.net.tcp.TcpConnection;
import com.yaxon.vn.nd.tas.net.tcp.TcpConnectionManager;
import com.yaxon.vn.nd.tas.net.udp.UdpChannel;
import com.yaxon.vn.nd.tas.net.udp.UdpChannelFactory;
import com.yaxon.vn.nd.tas.net.udp.UdpConnectionManager;
import com.yaxon.vndp.common.util.SpringContextUtil;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import com.yaxon.vndp.dms.MessageHandler;
import com.yaxon.vndp.dms.util.LoadBalancingNodeGroup;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Author: 程行荣
 * Time: 2013-11-22 11:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public abstract class BaseProtoHandler implements IProtocolHandler, MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseProtoHandler.class);

    private LoadBalancingNodeGroup tbpService;
    private TcpConnectionManager tcpConnectionManager;
    private UdpConnectionManager udpConnectionManager;


    private TcpConnection vid2tcpConnection;
    private TcpConnection sim2tcpConnection;
    private InetSocketAddress sim2udpAddress;
    private InetSocketAddress vid2udpAddress;

    public LoadBalancingNodeGroup tbp() {
        if(tbpService == null) {
            tbpService = (LoadBalancingNodeGroup) SpringContextUtil.getBean("tbp");
        }
        return tbpService;
    }

    /**
     * 私有属性为子类提供访问
     * @return
     */
    public TcpConnectionManager getTcpConnectionManager(){
        if(tcpConnectionManager==null)
        {
            tcpConnectionManager=new TcpConnectionManager();
        }
        return  tcpConnectionManager;
    }

    public AccessChannel tcpChannel() {
        TcpChannel tcpChannel = TcpChannelFactory.getTcpChannel();
        this.tcpConnectionManager = tcpChannel.getConnectionManager();
        return tcpChannel;
    }

    public AccessChannel udpChannel() {
        UdpChannel udpChannel =  UdpChannelFactory.getUdpChannel();
        this.udpConnectionManager = udpChannel.getConnectionManager();
        return udpChannel;
    }

    @Override
    public void handle(ProtoMsg msg) {
        try {
            doUpMsg(msg);
        } catch (Exception e) {
            logger.info("协议处理失败:" + msg, e);
        }
    }

    protected abstract void doUpMsg(ProtoMsg upMsg);

    @Override
    public void onReceive(DmsContext ctx, Message req) {
        doDnReq(ctx, req);
    }

    protected abstract void doDnReq(DmsContext ctx, Message dnReq);


    protected void sendCenterGeneralRes(ProtoMsg req, byte rc) throws TasException {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = req.sim;
        msg.vid = req.vid;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.dataBuf = Unpooled.buffer(5);
        msg.dataBuf.writeShort(req.sn);
        msg.dataBuf.writeShort(req.msgId);
        if ((rc <= 4 && rc >= 0) || rc == (byte)0xDF) {//0xDF 南斗自定义的锁车心跳F002 的应答
            msg.dataBuf.writeByte(rc);
        } else {
            msg.dataBuf.writeByte(ProtoConstants.RC_FAIL);
        }
        AccessChannel tcpChannel = tcpChannel();
        AccessChannel udpChannel = udpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);
        sim2udpAddress = udpConnectionManager.getInetSocketAddressBySim(req.sim);
        vid2udpAddress = udpConnectionManager.getInetSocketAddressByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            tcpChannel.sendMessage(msg);
        } else if (sim2udpAddress != null || vid2udpAddress != null) {
            msg.sender = sim2udpAddress != null ? sim2udpAddress : vid2udpAddress;
            udpChannel.sendMessage(msg);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", req.vid, req.sim);
        }
    }

    /**
     * 构建中心通用应答消息
     *
     * @param reqMsgId 终端请求消息ID
     * @param reqSn 终端请求消息流水号
     * @param rc 应答码
     * @return
     */
    protected ProtoMsg buildCenterGeneralResMsg(String vid, short reqMsgId, short reqSn, byte rc) {
        ProtoMsg res = new ProtoMsg();
        res.msgId = ProtoConstants.CENTER_GENERAL_RES;
        res.vid = vid;
        res.dataBuf = Unpooled.buffer(5);
        res.dataBuf.writeShort(reqSn);
        res.dataBuf.writeShort(reqMsgId);
        res.dataBuf.writeByte(rc);
        return res;
    }

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short resId) {
        AccessChannel tcpChannel = tcpChannel();
        AccessChannel udpChannel = udpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);
        sim2udpAddress = udpConnectionManager.getInetSocketAddressBySim(req.sim);
        vid2udpAddress = udpConnectionManager.getInetSocketAddressByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            return tcpChannel.sendRequest(req, resId);
        } else if (sim2udpAddress != null || vid2udpAddress != null) {
            req.sender = sim2udpAddress != null ? sim2udpAddress : vid2udpAddress;
            return udpChannel.sendRequest(req, resId);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", req.vid, req.sim);
            return null;
        }
    }

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short... resIds) {
        AccessChannel tcpChannel = tcpChannel();
        AccessChannel udpChannel = udpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);
        sim2udpAddress = udpConnectionManager.getInetSocketAddressBySim(req.sim);
        vid2udpAddress = udpConnectionManager.getInetSocketAddressByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            return tcpChannel.sendRequest(req, resIds);
        } else if (sim2udpAddress != null || vid2udpAddress != null) {
            req.sender = sim2udpAddress != null ? sim2udpAddress : vid2udpAddress;
            return udpChannel.sendRequest(req, resIds);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", req.vid, req.sim);
            return null;
        }
    }

    protected void sendMessage(ProtoMsg msg) throws TasException {
        AccessChannel tcpChannel = tcpChannel();
        AccessChannel udpChannel = udpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(msg.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(msg.vid);
        sim2udpAddress = udpConnectionManager.getInetSocketAddressBySim(msg.sim);
        vid2udpAddress = udpConnectionManager.getInetSocketAddressByVid(msg.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            tcpChannel.sendMessage(msg);
        } else if (sim2udpAddress != null || vid2udpAddress != null) {
            msg.sender = sim2udpAddress != null ? sim2udpAddress : vid2udpAddress;
            udpChannel.sendMessage(msg);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", msg.vid, msg.sim);
        }
    }

    protected void closeConnBySim(long sim) throws TasException {
        tcpConnectionManager.closeConnBySim(sim);
    }
}
