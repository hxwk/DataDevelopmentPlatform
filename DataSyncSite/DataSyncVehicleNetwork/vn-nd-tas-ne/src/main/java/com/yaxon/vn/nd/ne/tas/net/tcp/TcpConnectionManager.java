package com.yaxon.vn.nd.ne.tas.net.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: 程行荣
 * Time: 2014-11-26 14:00
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class TcpConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(TcpConnectionManager.class);

    private ConcurrentMap<Long, TcpConnection> connections = new ConcurrentHashMap<>();

    private ConcurrentMap<String, TcpConnection> vin2Connection = new ConcurrentHashMap<>();


    public void start() throws Exception {

    }

    public void close() {
        for (TcpConnection conn : vin2Connection.values()) {
            conn.close();
        }


    }
    public void checkLoop() {
        for (TcpConnection conn : connections.values()) {
            conn.checkState();
        }
    }

    public void closeConnByvin(String vin) {
        TcpConnection conn = vin2Connection.get(vin);
        logger.warn("[{}]/鉴权未通过，强制断开链路",conn.vin());
        if(conn !=null){
            conn.close();
        }

    }


    public void addConnection(TcpConnection conn) {
        connections.putIfAbsent(conn.id(), conn);
    }


    public void bingVinToConnection(TcpConnection conn) {
        TcpConnection tc = vin2Connection.put(conn.vin(), conn);
        if (tc != null && tc.id() != conn.id()) {
            tc.close();
        }
    }

    public void removeConnection(TcpConnection conn) {
        connections.remove(conn.id());
        TcpConnection connection2 = getConnectionByVin(conn.vin());
        if(connection2 !=null &&  connection2.connId == conn.connId){
            vin2Connection.remove(conn.vin());
        }
    }


    public void removeConnection(long connId, String vin) {
        connections.remove(connId);
        TcpConnection connection2 = getConnectionByVin(vin);
        if(connection2 !=null &&  connection2.connId == connId){
            vin2Connection.remove(vin);
        }
    }

    public TcpConnection getConnectionByConnId(Long connId) {

        if (null == connId) {
            logger.error("connId  为空");

            return null;
        }

        return connections.get(connId);
    }



    public TcpConnection getConnectionByVin(String vin) {

        if (null == vin) {
            logger.error("vin  为空");

            return null;
        }

        return vin2Connection.get(vin);
    }

    public int numOfConnections() {
        return connections.size();
    }


    public int vin2Connection() {
        return vin2Connection.size();
    }

}
