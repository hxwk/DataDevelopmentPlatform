package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.client.ThreadClientHandler;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/6 20:24
 */
public class ChannelManager {
    private static Map<String, List<Channel>> clientListHandlerFree = Maps.newConcurrentMap();
    private static Map<SocketAddress, List<Channel>> clientListHandlerUse = Maps.newConcurrentMap();

    //key代表client的Channel的地址，value代表master的IP
    private static Map<SocketAddress, SocketAddress> clientMasterRelation = Maps.newConcurrentMap();

    //key代表master的IP，value代表master的ChannelHandlerContext
    private static Map<SocketAddress, ChannelHandlerContext> masterChannelMap = Maps.newConcurrentMap();

    //将客户端绑定IP，key代表客户端通道的虚拟地址，value代表客户端启动的机器
    private static Map<SocketAddress, String> clientIpMap = Maps.newConcurrentMap();

    public Map<SocketAddress, String> getClientIpMap() {
        return this.clientIpMap;
    }
    public String getClientIpByKey(SocketAddress socketAddress) {
        return this.clientIpMap.get(socketAddress);
    }
    public void putClientIpMap(SocketAddress key,String value) {
        clientIpMap.put(key,value);
    }

    public Map<SocketAddress, SocketAddress> getClientMasterRelation() {
        return this.clientMasterRelation;
    }
    public SocketAddress getClientMasterRelationByKey(SocketAddress socketAddress) {
        return this.clientMasterRelation.get(socketAddress);
    }
    public void putClientMasterRelation(SocketAddress key,SocketAddress value) {
        clientMasterRelation.put(key,value);
    }

    public void putClientListChannelFree(String processIpPort, Channel channel) {
        if (null == clientListHandlerFree.get(processIpPort)) {
            List<Channel> listChannel = new ArrayList<Channel>();
            listChannel.add(channel);
            clientListHandlerFree.put(processIpPort, listChannel);
        } else {
            List<Channel> listChannel = clientListHandlerFree.get(processIpPort);
            listChannel.add(channel);
            clientListHandlerFree.put(processIpPort, listChannel);
        }
    }

    public List<Channel> getClientListHandlerFree(String processIpPort) {
        return this.clientListHandlerFree.get(processIpPort);
    }

    public Map<String, List<Channel>> getClientListHandlerFree() {
        return this.clientListHandlerFree;
    }

    public void removeFreeClient(String processIpPort) {
        clientListHandlerFree.remove(processIpPort);
    }

    public void putClientListChannelUse(SocketAddress operatorIpPort, Channel channel) {
        if (null == clientListHandlerUse.get(operatorIpPort)) {
            List<Channel> listChannel = new ArrayList<Channel>();
            listChannel.add(channel);
            clientListHandlerUse.put(operatorIpPort, listChannel);
        } else {
            List<Channel> listChannel = clientListHandlerUse.get(operatorIpPort);
            listChannel.add(channel);
            clientListHandlerUse.put(operatorIpPort, listChannel);
        }
    }

    public List<Channel> getClientListHandlerUse(SocketAddress processIpPort) {
        return this.clientListHandlerUse.get(processIpPort);
    }

    public Map<SocketAddress, List<Channel>> getClientListHandlerUse() {
        return this.clientListHandlerUse;
    }

    public void removeUseClient(SocketAddress processIpPort) {
        this.clientListHandlerUse.remove(processIpPort);
    }

    public void putMasterChannelMap(SocketAddress socketAddress, ChannelHandlerContext ctx) {
        this.masterChannelMap.put(socketAddress, ctx);
    }

    public void removeMasterChannel(SocketAddress key) {
        this.masterChannelMap.remove(key);
    }

    public ChannelHandlerContext getMasterChannelMapBykey(SocketAddress socketAddress){
        return this.masterChannelMap.get(socketAddress);
    }

//    public Map<Channel, ChannelHandlerContext> getMasterChannelMap() {
//        return this.masterChannelMap;
//    }
//
//    public ChannelHandlerContext getMasterChannelMap(Channel channel) {
//        return this.masterChannelMap.get(channel);
//    }

//    private static Map<String, List<ThreadClientHandler>> clientListHandlerFree = Maps.newConcurrentMap();
//    private static Map<SocketAddress, List<ThreadClientHandler>> clientListHandlerUse = Maps.newConcurrentMap();
////    private static Map<SocketAddress, ChannelHandlerContext> masterChannelMap = Maps.newConcurrentMap();
//
//    public void putClientListChannelFree(String processIpPort, ThreadClientHandler handler) {
//        if (null==clientListHandlerFree.get(processIpPort)) {
//            List<ThreadClientHandler> listChannel = new ArrayList<ThreadClientHandler>();
//            listChannel.add(handler);
//            clientListHandlerFree.put(processIpPort, listChannel);
//        } else {
//            List<ThreadClientHandler> listChannel = clientListHandlerFree.get(processIpPort);
//            listChannel.add(handler);
//            clientListHandlerFree.put(processIpPort, listChannel);
//        }
//
//    }
//
//    public List<ThreadClientHandler> getClientListHandlerFree(String processIpPort) {
//        return this.clientListHandlerFree.get(processIpPort);
//    }
//    public  Map<String, List<ThreadClientHandler>>  getClientListHandlerFree() {
//        return this.clientListHandlerFree;
//    }
//
//    public void removeFreeClient(String processIpPort){
//        clientListHandlerFree.remove(processIpPort);
//    }
//
//    public void putClientListChannelUse(SocketAddress operatorIpPort, ThreadClientHandler handler) {
//        if (null==clientListHandlerUse.get(operatorIpPort)) {
//            List<ThreadClientHandler> listChannel = new ArrayList<ThreadClientHandler>();
//            listChannel.add(handler);
//            clientListHandlerUse.put(operatorIpPort, listChannel);
//        } else {
//            List<ThreadClientHandler> listChannel = clientListHandlerUse.get(operatorIpPort);
//            listChannel.add(handler);
//            clientListHandlerUse.put(operatorIpPort, listChannel);
//        }
//
//    }
//    public List<ThreadClientHandler> getClientListHandlerUse(SocketAddress processIpPort) {
//        return this.clientListHandlerUse.get(processIpPort);
//    }
//    public  Map<SocketAddress, List<ThreadClientHandler>>  getClientListHandlerUse() {
//        return this.clientListHandlerUse;
//    }
//
//    public void removeUseClient(SocketAddress processIpPort) {
//        this.clientListHandlerUse.remove(processIpPort);
//    }
//    public void putMasterChannelMap(SocketAddress key, ChannelHandlerContext channel) {
//        this.masterChannelMap.put(key, channel);
//    }
//
//    public void removeMasterChannel(SocketAddress key){
//        this.masterChannelMap.remove(key);
//    }
//    public Map<SocketAddress, ChannelHandlerContext>  getMasterChannelMap(){
//        return this.masterChannelMap;
//    }
//    public ChannelHandlerContext getMasterChannelMap(SocketAddress master) {
//        return this.masterChannelMap.get(master);
//    }

}
