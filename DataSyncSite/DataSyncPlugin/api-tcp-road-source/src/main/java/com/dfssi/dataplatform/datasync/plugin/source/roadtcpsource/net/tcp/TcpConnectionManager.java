package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TcpConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(TcpConnectionManager.class);


    private ConcurrentMap<Long, TcpConnection> connections = new ConcurrentHashMap<>();

    private ConcurrentMap<String, TcpConnection> sim2Connection = new ConcurrentHashMap<>();
    private ConcurrentMap<String, TcpConnection> vid2Connection = new ConcurrentHashMap<>();


    public void start() throws Exception {

    }

    public void close() {
        for (TcpConnection conn : vid2Connection.values()) {
            conn.close();
        }


    }

    public void checkLoop() {
        for (TcpConnection conn : connections.values()) {
            conn.checkState();
        }
    }

    public void closeConnBySim(String sim) {
        TcpConnection conn = sim2Connection.get(sim);
        logger.warn("[{}]/鉴权未通过，强制断开链路",conn.sim());
        if(conn !=null){
            conn.close();
        }

    }

    public void addConnection(TcpConnection conn) {
        connections.putIfAbsent(conn.id(), conn);
    }

    public void bingSimToConnection(TcpConnection conn) {
        //sim2Connection.putIfAbsent(conn.sim(), conn);
        /// 经常发现终端设备短链后，socket 仍然保持连接，netty 侦测不到
        logger.info("绑定sim跟conn sim:" + conn.sim() + ",conn:" + conn.id());
        TcpConnection tc = sim2Connection.put(conn.sim(), conn);
        if (tc != null && tc.id() != conn.id()) {
            tc.close();
        }
    }

    public void bingVidToConnection(TcpConnection conn) {
        //vid2Connection.putIfAbsent(conn.vid(), conn);
        TcpConnection tc = vid2Connection.put(conn.vid(), conn);

        logger.info("绑定vid跟conn vid:" + conn.vid() + ", conn:" + conn.id());
        if (tc != null && tc.id() != conn.id()) {
            tc.close();
        }
    }

    public void removeConnection(TcpConnection conn) {
        connections.remove(conn.id());
        TcpConnection connection1 = getConnectionBySim(conn.sim());
        if(connection1 !=null && connection1.connId == conn.connId){
            sim2Connection.remove(conn.sim());
        }
        TcpConnection connection2 = getConnectionByVid(conn.vid());
        if(connection2 !=null &&  connection2.connId == conn.connId){
            vid2Connection.remove(conn.vid());
        }
    }


    public void removeConnection(long connId, String sim, String vid) {
        connections.remove(connId);
        TcpConnection connection1 = getConnectionBySim(sim);
        if(connection1 !=null &&  connection1.connId == connId){
            sim2Connection.remove(sim);
        }
        TcpConnection connection2 = getConnectionByVid(vid);
        if(connection2 !=null &&  connection2.connId == connId){
            vid2Connection.remove(vid);
        }
    }


    public TcpConnection getConnectionBySim(String sim) {
        if (null == sim) {
            logger.error("sim为空");
            return null;
        }
        logger.debug("sim2Connection:"+sim2Connection.toString());
        return sim2Connection.get(sim);
    }

    public TcpConnection getConnectionByConnId(Long connId) {

        if (null == connId) {
            logger.error("connId为空");

            return null;
        }

        return connections.get(connId);
    }



    public TcpConnection getConnectionByVid(String vid) {

        if (null == vid) {
            logger.error("vid为空");

            return null;
        }
        logger.debug("vid2Connection:"+vid2Connection.toString());
        return vid2Connection.get(vid);
    }

    public int numOfConnections() {
        return connections.size();
    }

    public int simOfConnections() {
        return sim2Connection.size();
    }

    public int vid2Connection() {
        return vid2Connection.size();
    }

    public String getConnOfConnections() {
        return connections.keySet().toString();
    }

    public String getSimOfConnections() {
        return sim2Connection.keySet().toString();
    }

    public String getVidOfConnection() {
        return vid2Connection.keySet().toString();
    }

}
