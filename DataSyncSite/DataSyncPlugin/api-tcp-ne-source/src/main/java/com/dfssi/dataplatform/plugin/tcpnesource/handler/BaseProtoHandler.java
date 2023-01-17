package com.dfssi.dataplatform.plugin.tcpnesource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.model.common.BaseMessage;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.model.newen.entity.*;
import com.dfssi.dataplatform.plugin.tcpnesource.common.Constants;
import com.dfssi.dataplatform.plugin.tcpnesource.common.GeodeTool;
import com.dfssi.dataplatform.plugin.tcpnesource.common.KafkaHeader;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.TcpException;
import com.dfssi.dataplatform.plugin.tcpnesource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.plugin.tcpnesource.net.tcp.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.Unpooled;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Iterator;

import static com.dfssi.dataplatform.plugin.tcpnesource.common.Constants.PREFIX_32960;

public abstract class BaseProtoHandler implements IProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseProtoHandler.class);

    private TcpConnectionManager tcpConnectionManager;


    private TcpConnection vin2tcpConnection;
    private String vin;

    @Override
    public void handle(ProtoMsg msg, String taskId, ChannelProcessor channelProcessor,String vin) {
        try {
            this.vin = vin;
//            logger.info("wx 开始发送返回数据，收到数据为：{}",msg);
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
        logger.debug("hehe");
        return tcpChannel;
    }

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short resId) {
        AccessChannel tcpChannel = tcpChannel();
        vin2tcpConnection = tcpConnectionManager.getConnectionByVin(req.vin);

        if (vin2tcpConnection != null ) {
            return tcpChannel.sendRequest(req, resId);
        } else {
            logger.warn("终端未登录,vid:{} ", req.vin);
            return null;
        }
    }

    protected ListenableFuture<ProtoMsg> sendRequest(ProtoMsg req, short... resIds) {
        AccessChannel tcpChannel = tcpChannel();
        vin2tcpConnection = tcpConnectionManager.getConnectionByVin(req.vin);

        if (vin2tcpConnection != null ) {
            return tcpChannel.sendRequest(req, resIds);
        }  else {
            logger.warn("终端未登录,vid:{} ", req.vin);
            return null;
        }
    }

    protected void sendMessage(ProtoMsg req) throws TcpException {
        AccessChannel tcpChannel = tcpChannel();
        vin2tcpConnection = tcpConnectionManager.getConnectionByVin(req.vin);

        if (vin2tcpConnection != null ) {
            tcpChannel.sendRequest(req);
        } else {
            logger.warn("终端未登录,vid:{} ", req.vin);
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
    protected ProtoMsg buildCenterGeneralResMsg(String vin, short reqMsgId, short reqSn, byte rc) {
        ProtoMsg res = new ProtoMsg();
        res.msgId = ProtoConstants.CENTER_GENERAL_RES;
        res.vin = vin;
        res.dataBuf = Unpooled.buffer(5);
        res.dataBuf.writeShort(reqSn);
        res.dataBuf.writeShort(reqMsgId);
        res.dataBuf.writeByte(rc);
        return res;
    }

    protected void sendCenterGeneralRes(ProtoMsg req, byte rc) {
        ProtoMsg msg = new ProtoMsg();
        msg.vin = req.vin;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.answerSign = rc;
        msg.commandSign = req.commandSign;

        msg.dataBuf = Unpooled.buffer(8);
        msg.dataBuf.writeShort(6);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        msg.dataBuf.writeByte(Integer.parseInt(String.valueOf(year).substring(2,4)));
        msg.dataBuf.writeByte(calendar.get(Calendar.MONTH) + 1);
        msg.dataBuf.writeByte(calendar.get(Calendar.DAY_OF_MONTH));
        msg.dataBuf.writeByte(calendar.get(Calendar.HOUR_OF_DAY));
        msg.dataBuf.writeByte(calendar.get(Calendar.MINUTE));
        msg.dataBuf.writeByte(calendar.get(Calendar.SECOND));
//        msg.dataBuf.writeShort(req.sn);
//        msg.dataBuf.writeShort(req.msgId);
//        if ((rc <= 4 && rc >= 0) || rc == (byte)0xDF) {//0xDF 南斗自定义的锁车心跳F002 的应答
//            msg.dataBuf.writeByte(rc);
//        } else {
//            msg.dataBuf.writeByte(ProtoConstants.RC_FAIL);
//        }
        AccessChannel tcpChannel = tcpChannel();
        vin2tcpConnection = tcpConnectionManager.getConnectionByVin(this.vin);
//        logger.info("wx 获取vin连接{}",this.vin);
        if (vin2tcpConnection != null) {
            logger.debug("发送报文msg："+msg.toString());
            tcpChannel.sendMessage(msg);
            logger.debug("报文发送完成");
        } else {
            logger.warn("终端未登录,vid:{}", req.vin);
        }
    }

    public void buildEvent(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor, String topic) {
        if (null != upMsg.bytes && upMsg.bytes.length > 0) {

            processEvent(upMsg.bytes, taskId, upMsg.commandSign,
                    topic, upMsg.vin, channelProcessor);
        }

//        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    }

    public void buildEvent(final BaseMessage message, String taskId, ChannelProcessor channelProcessor,
                           String vin, short commandSign, String topic) {

        try {
            SimpleEvent event = new SimpleEvent();
            String jonStr = JSON.toJSONString(message);

            if (message instanceof Req_05) {
                Req_05 req_05 = (Req_05)message;
                jonStr = JSON.toJSONString(req_05);
            } else if (message instanceof Req_06) {
                Req_06 req_06 = (Req_06)message;
                jonStr = JSON.toJSONString(req_06);
            } else if (message instanceof Req_01) {
                Req_01 req_01 = (Req_01)message;
                jonStr = JSON.toJSONString(req_01);
            } else if (message instanceof Req_04) {
                Req_04 req_04 = (Req_04)message;
                jonStr = JSON.toJSONString(req_04);
            }

            KafkaHeader header = new KafkaHeader();
            header.setMsgId(taskId);
            header.setMsgId(String.valueOf(commandSign));
            header.setTopic(topic);
            header.setVin(vin);
            event.getHeaders().put(Constants.TOPIC_HEADER, topic);
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));
            event.setBody(jonStr.getBytes());
            //event.getHeaders().put(Constants.KEY_HEADER, vin + EventHeader.HEADER_SPLIT_CHAR + commandSign + EventHeader.HEADER_SPLIT_CHAR + topic);

            channelProcessor.processEvent(event);
            logger.debug("发送event到chennel成功 " + taskId + ", msgid = " + message.id());
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void processEvent(VnndResMsg res, String taskId, String msgid,
                             String topic, ChannelProcessor channelProcessor){
        logger.debug("  开始封装event " + taskId + ", msgid = " + msgid);
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
            event.setBody(jonStr.getBytes());
            event.getHeaders().put(Constants.TASK_ID_KEY, taskId);
            event.getHeaders().put(Constants.MSG_ID_KEY, msgid);
            event.getHeaders().put(Constants.TOPIC_HEADER, topic);
            event.getHeaders().put(Constants.KEY_HEADER, Constants.FIELD_KEY_PARTITION);

            channelProcessor.processEvent(event);
            logger.debug("发送event到chennel成功 " + taskId + ", msgid = " + msgid);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void processEvent(String json, String taskId, String msgid,
                             String topic, ChannelProcessor channelProcessor){
        logger.debug("  开始封装event " + taskId + ", msgid = " + msgid);
        try {
            SimpleEvent event = new SimpleEvent();
            event.setBody(json.getBytes());
            event.getHeaders().put(Constants.TASK_ID_KEY, taskId);
            event.getHeaders().put(Constants.MSG_ID_KEY, msgid);
            event.getHeaders().put(Constants.TOPIC_HEADER, topic);
            event.getHeaders().put(Constants.KEY_HEADER, Constants.FIELD_KEY_PARTITION);

            channelProcessor.processEvent(event);
            logger.debug("发送event到chennel成功 " + taskId + ", msgid = " + msgid);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void processEvent(byte[] dataBytes, String taskId, short commandSign,
                             String topic, String vin, ChannelProcessor channelProcessor){
        logger.debug("  开始封装event " + taskId + ", commandSign = " + commandSign);
        try {
            SimpleEvent event = new SimpleEvent();
            event.setBody(dataBytes);
            KafkaHeader header = new KafkaHeader();
            header.setMsgId(PREFIX_32960 + String.valueOf(commandSign));
            header.setTopic(topic);
            header.setVin(vin);
            event.getHeaders().put(Constants.KEY_HEADER, JSONObject.toJSONString(header));

            channelProcessor.processEvent(event);
            logger.debug("发送event到chennel成功 " + taskId + ", commandSign = " + commandSign);
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

//    public void updateVehicleStatus2Redis(String vid, String taskId) {
//        Jedis jedis = null;
//        try {
//            jedis = RedisPoolManager.getJedis();
//            jedis.set(Constants.GK_VEHICLE_STATE + vid, taskId);
//            jedis.expire(Constants.GK_VEHICLE_STATE + vid, Constants.VEHCILE_CACHE_TIME * 60);
//        } catch (Exception e) {
//            logger.error("在Redis中更新车辆状态失败", e);
//        } finally {
//            if (null != jedis) {
//                RedisPoolManager.returnResource(jedis);
//            }
//        }
//    }
//
//    public void updateVehicleStatus2Redis(String vin) {
//        Jedis jedis = null;
//        try {
//            jedis = new com.dfssi.dataplatform.plugin.tcpnesource.anacommon.RedisPoolManager().getJedis();
//            jedis.set(com.dfssi.dataplatform.plugin.tcpnesource.anacommon.Constants.GK_VEHICLE_STATE + vin, String.valueOf(System.currentTimeMillis()));
//            jedis.expire(com.dfssi.dataplatform.plugin.tcpnesource.anacommon.Constants.GK_VEHICLE_STATE + vin, com.dfssi.dataplatform.plugin.tcpnesource.anacommon.Constants.VEHCILE_CACHE_TIME * 60);
//        } catch (Exception e) {
//            logger.error("在Redis中更新车辆状态失败", e);
//        } finally {
//            if (null != jedis) {
//                jedis.close();
//            }
//        }
//    }

    /**
     * 根据vin获取添加车辆信息
     * @param upMsg
     */
    public VehicleDTO getVehicleInfo(ProtoMsg upMsg) {
        VehicleDTO vehicle = null;

        //根据vin获取车辆信息
        Region region = null;

        Object objList = null;
        try {
            //根据vin拿到车企信息

            region = GeodeTool.getRegeion(Constants.REGION_VEHICLEINFO);

            StringBuilder sqlBuf = new StringBuilder();
            sqlBuf.append("select * from /");
            sqlBuf.append(Constants.REGION_VEHICLEINFO);
            sqlBuf.append(" where vin = '");
            sqlBuf.append(upMsg.vin);
            sqlBuf.append("' and isValid = '1' limit 1");

            logger.debug(" REGION_VEHICLEINFO region sql: " + sqlBuf.toString());
            objList = region.query(sqlBuf.toString());
//            objList = GeodeTool.getInstance().getQueryService().newQuery(sqlBuf.toString()).execute();

            if (objList instanceof ResultsBag) {
                Iterator iter = ((ResultsBag)objList).iterator();
                while (iter.hasNext()) {
                    vehicle = (VehicleDTO) iter.next();

                    break;
                }
            }
        } catch (Exception e) {
            logger.error("查询geode出错", e);
        }
        if (vehicle==null){
            vehicle = new VehicleDTO();
            vehicle.setVin(upMsg.vin);
            vehicle.setVehicleCompany("东风性能测试");
        }
        return vehicle;
    }

    protected void closeConnByVin(String vin) throws TcpException {
        tcpConnectionManager.closeConnByvin(vin);
    }


}
