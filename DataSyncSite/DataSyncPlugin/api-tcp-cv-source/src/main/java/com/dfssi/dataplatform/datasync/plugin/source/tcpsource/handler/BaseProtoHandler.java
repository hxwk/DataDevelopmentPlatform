package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndF003ResMsg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndInstructionResMsg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.VnndLoginResMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.GeodeTool;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.KafkaHeader;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.TcpException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.Unpooled;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Author: 程行荣
 * Time: 2013-11-22 11:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public abstract class BaseProtoHandler implements IProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseProtoHandler.class);

    private TcpConnectionManager tcpConnectionManager;


    private TcpConnection vid2tcpConnection;
    private TcpConnection sim2tcpConnection;

    @Override
    public void handle(ProtoMsg msg, String taskId, ChannelProcessor channelProcessor) {
        try {
            doUpMsg(msg, taskId, channelProcessor);
        } catch (Exception e) {
            logger.info("协议处理失败:" + msg, e);
        }
    }

    public abstract void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor);


    public abstract void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor);

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

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short resId) {
        AccessChannel tcpChannel = tcpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            return tcpChannel.sendRequest(req, resId);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ,请检查车辆是否和平台建立连接", req.vid, req.sim);
            return null;
        }
    }

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short... resIds) {
        AccessChannel tcpChannel = tcpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            return tcpChannel.sendRequest(req, resIds);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", req.vid, req.sim);
            return null;
        }
    }

    protected void sendMessage(ProtoMsg msg) throws TcpException {
        AccessChannel tcpChannel = tcpChannel();
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(msg.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(msg.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            tcpChannel.sendMessage(msg);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", msg.vid, msg.sim);
        }
    }

    protected void sendCenterGeneralRes(ProtoMsg req, byte rc) {
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
        vid2tcpConnection = tcpConnectionManager.getConnectionBySim(req.sim);
        sim2tcpConnection = tcpConnectionManager.getConnectionByVid(req.vid);

        if (vid2tcpConnection != null || sim2tcpConnection != null) {
            tcpChannel.sendMessage(msg);
        } else {
            logger.warn("终端未登录,vid:{},sim:{} ", req.vid, req.sim);
        }
    }

    public void processEvent(VnndResMsg res, String taskId, String msgid,
                             String topic, ChannelProcessor channelProcessor){
        logger.info("  开始封装event " + taskId + ", msgid = " + msgid);
        try {
            SimpleEvent event = new SimpleEvent();
            String jonStr = JSON.toJSONString(res);
            if (res instanceof VnndInstructionResMsg) {
                VnndInstructionResMsg viRes = (VnndInstructionResMsg)res;
                jonStr = JSON.toJSONString(viRes);
            } else if (res instanceof VnndLoginResMsg) {
                VnndLoginResMsg vlRes = (VnndLoginResMsg)res;
                jonStr = JSON.toJSONString(vlRes);
            } else if (res instanceof VnndF003ResMsg) {
                VnndF003ResMsg vfRes = (VnndF003ResMsg)res;
                jonStr = JSON.toJSONString(vfRes);
            }
            KafkaHeader header = new KafkaHeader();
            header.setTaskId(taskId);
            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(jonStr.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));

            channelProcessor.processEvent(event);
            logger.info("发送event到chennel成功 " + taskId + ", msgid = " + msgid,", event="+event);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void processEvent(String json, String taskId, String msgid,
                             String topic, ChannelProcessor channelProcessor){
        logger.info("  开始封装event " + taskId + ", msgid = " + msgid);
        try {
            SimpleEvent event = new SimpleEvent();
            KafkaHeader header = new KafkaHeader();
            header.setMsgid(taskId);
            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(json.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            logger.info("key:{}",Constants.KEY_HEADER);
            channelProcessor.processEvent(event);
            logger.info("发送event到chennel成功 " + taskId + ", msgid = " + msgid);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void pushToKafka(String json, String topic, ChannelProcessor channelProcessor){
        try {
            SimpleEvent event = new SimpleEvent();
            KafkaHeader header = new KafkaHeader();
//            header.setMsgid(taskId);
//            header.setMsgid(msgid);
            header.setTopic(topic);
            event.setBody(json.getBytes());
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            logger.info("key:{}",Constants.KEY_HEADER);
            channelProcessor.processEvent(event);
//            logger.info("发送event到chennel成功 " + taskId + ", msgid = " + msgid);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    Map<String,Long> map=new HashMap<String,Long>();
    /*车辆信息进内存*/
    public void saveToMap(String vid){
      long t=System.currentTimeMillis();
        map.put(vid,t);
    }

    //redis存储上线下线状态，1表示上线，0表示下线
   public void updateVehicleStatus2Redis(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
       saveToMap(upMsg.vid);

       Jedis jedis = null;
           jedis = RedisPoolManager.getJedis();

           long currentTime=System.currentTimeMillis();
           for (String vid : map.keySet()){
               if((currentTime-map.get(vid))/(60*1000)<10){
                   String flag="1";
                   if(!jedis.exists(vid)|| !jedis.get(Constants.GK_VEHICLE_STATE + vid).equals(flag)){
                       //更新redis
                       jedis.set(Constants.GK_VEHICLE_STATE + vid,flag);
                       JSONObject json=new JSONObject();
                       json.put("vid",vid);
                       json.put("status",flag);
                       //推送kafka
                       pushToKafka(json.toString(),"VEHICLE_STATE_TOPIC",channelProcessor);
                   }
               }else if((currentTime-map.get(vid))/(60*1000)>10){
                   String flag="0";
                   if(!jedis.exists(vid)|| !jedis.get(Constants.GK_VEHICLE_STATE + vid).equals(flag)){
                       //更新redis
                       jedis.set(Constants.GK_VEHICLE_STATE + vid, flag);
                       JSONObject json=new JSONObject();
                       json.put("vid",vid);
                       json.put("status",flag);
                       //推送kafka
                       pushToKafka(json.toString(),"VEHICLE_STATE_TOPIC",channelProcessor);
               }
           }
       }
       if (null != jedis) {
           RedisPoolManager.returnResource(jedis);
       }
   }



    //更新ON档状态到redis，档状态有变更时推送kafka
    public void updateONStatus2Redis(JSONObject json, ChannelProcessor channelProcessor) {
        logger.info("待下发的ON档状态信息:{}", json);
        Jedis jedis = null;
        jedis = RedisPoolManager.getJedis();
         String vid=json.get("vid").toString();
         String OnStatus=json.get("onBitchSign").toString();

        try {
            if(!jedis.exists(Constants.VEHILCE_ON_UPDATE + vid)|| !jedis.get(Constants.VEHILCE_ON_UPDATE + vid).equals(OnStatus)){
                //更新redis
                jedis.set(Constants.VEHILCE_ON_UPDATE + vid,OnStatus);
                //推送kafka
                pushToKafka(json.toString(),"VEHICLE_ON_STATE_TOPIC",channelProcessor);
            }
          } catch (Exception e) {
            logger.error("存redis数据：key:"+Constants.VEHILCE_ON_UPDATE + vid+" value:"+OnStatus);
            logger.error("在Redis中更新车辆状态失败", e);
        } finally {
            if (null != jedis) {
                RedisPoolManager.returnResource(jedis);
            }
        }
    }


    /**
     * 查询车辆信息
     * @param queryStr
     * @return
     */
    public CVVehicleDTO queryCVVehicleInfo(String queryStr){
        logger.debug(" REGION_VEHICLEINFO region sql: " + queryStr);
        CVVehicleDTO vehicle = null;
        Region region = null;
        try {
            region = GeodeTool.getRegeion(Constants.REGION_CVVEHICLEINFO);
            Object objList = region.query(queryStr);
            if (objList instanceof ResultsBag) {
                Iterator iter = ((ResultsBag)objList).iterator();
                while (iter.hasNext()) {
                    vehicle = (CVVehicleDTO) iter.next();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("查询geode出错", e);
        }
        return vehicle;
    }

    public void closeConnBySim(String sim) {
        tcpConnectionManager.closeConnBySim(sim);
    }


}
