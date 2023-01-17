package com.yaxon.vn.nd.tas.net.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: 程行荣
 * Time: 2014-11-26 14:00
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class UdpConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(UdpConnectionManager.class);
    private UdpConnection udpConnection;
    private ConcurrentMap<String, String> vid2Sim = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> sim2Vid = new ConcurrentHashMap<>();
    private ConcurrentMap<String, InetSocketAddress> sim2Address = new ConcurrentHashMap<>();
    private ConcurrentMap<String, InetSocketAddress> vid2Address = new ConcurrentHashMap<>();


    public void start() throws Exception {

    }

    /*public void close() {
        for (UdpConnection conn : vid2Connection.values()) {
            conn.close();
        }
    }*/

    public void checkLoop() {
      /*  for (UdpConnection conn : connections.values()) {
            conn.checkState();
        }*/
    }

  /*  public void addConnection(UdpConnection conn) {
        connections.putIfAbsent(conn.id(), conn);
    }*/

    public void bingSimToAddress(String sim, InetSocketAddress sender) {
        /// 经常发现终端设备短链后，socket 仍然保持连接，netty 侦测不到
        sim2Address.put(sim, sender);

    }

    public UdpConnection getUdpConnection() {
        return udpConnection;
    }

    public void setUdpConnection(UdpConnection udpConnection) {
        this.udpConnection = udpConnection;
    }

    public void bingVidToAddress(String vid, InetSocketAddress sender) {
        vid2Address.put(vid, sender);
    }

    public void bingVidToSim(String vid, String sim) {
        vid2Sim.putIfAbsent(vid, sim);
        sim2Vid.putIfAbsent(sim,vid);
    }


    public InetSocketAddress getInetSocketAddressBySim(String sim) {

        if (null == sim) {
            logger.error("sim 卡 为空");
            return null;
        }

        return sim2Address.get(sim);
    }

    public String getSimByVid(String vid) {
        return vid2Sim.get(vid);
    }

    public String getVidBySim(String sim){

        if (null == sim) {
            logger.error("sim 卡 为空");
            return null;
        }

        return sim2Vid.get(sim);
    }

    public InetSocketAddress getInetSocketAddressByVid(String vid) {

        if (null == vid) {
            logger.error("vid  为空");

            return null;
        }

        return vid2Address.get(vid);
    }

    public int numOfConnections() {
     /*   for (long vid : vid2Address.keySet()) {
            System.out.println("打印vid->address:"+ vid+":" + vid2Address.get(vid));
        }
        for (long sim : sim2Address.keySet()) {
            System.out.println("打印sim->address:"+sim+":"+sim2Address.get(sim));
        }*/
        return vid2Address.size();
    }

}
