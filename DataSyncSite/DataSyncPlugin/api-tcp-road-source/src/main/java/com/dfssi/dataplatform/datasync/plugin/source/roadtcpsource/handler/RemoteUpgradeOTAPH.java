package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.road.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.ProcessKafka;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ByteBufUtil;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ProtoUtil;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;


/**
 * @author SHUAIHONG
 * 远程升级
 */
public class RemoteUpgradeOTAPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(RemoteUpgradeOTAPH.class);


    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId==(short)0x0108){ //远程升级指令下发响应
            do_0108(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0xE003) {//文件数据下发
            do_E003(upMsg ,taskId,  channelProcessor);
        } else if (upMsg.msgId==(short)0x0107){ //终端系统信息查询应答
            do_0107(upMsg, taskId, channelProcessor);
        }else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_8108){   //下发终端升级包
            do_8108((Req_8108) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_E103) {
            Req_E103((Req_E103) dnReq,taskId,channelProcessor);
        }else if (dnReq instanceof Req_8107){   //终端系统信息查询
            do_8107((Req_8107) dnReq, taskId, channelProcessor);
        }else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }



    private void do_8108(final Req_8108 dnReq, String taskId, ChannelProcessor channelProcessor) {
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        String str = String.format("下发终端升级包");

        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        Jedis jedis = new RedisPoolManager().getJedis();
        String redis8108Key = "RoadInstruct:8108:"+dnReq.getSim();
        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x8108;
            req.dataBuf = Unpooled.buffer(32);
            if(dnReq.getUpdateType().equals("A0")){
                req.dataBuf.writeByte(0xA0);
            }else if(dnReq.getUpdateType().equals("A1")){
                req.dataBuf.writeByte(0xA1);
            }else if(dnReq.getUpdateType().equals("A2")){
                req.dataBuf.writeByte(0xA2);
            }else if(dnReq.getUpdateType().equals("A3")){
                req.dataBuf.writeByte(0xA3);
            }else{

            }
            byte ManufacturerId[]=new byte[5];
            ManufacturerId=dnReq.getManufacturerId().getBytes();
            if(ManufacturerId.length==5){
                req.dataBuf.writeBytes(ManufacturerId);
            }else{
                byte ManufacturerId1[]={0x00,0x00,0x00,0x00,0x00};
                req.dataBuf.writeBytes(ManufacturerId1);
            }
            //版本号长度
            int n=dnReq.getVersion().getBytes().length;
            req.dataBuf.writeByte(n);
            //版本号
            byte version[]=new byte[n];
            version=dnReq.getVersion().getBytes();
            req.dataBuf.writeBytes(version);


            /*****************修改为下发文件路径*********************************/
//            待下发文件路径长度
            int filepathlength=dnReq.getFilePath().getBytes().length;
            byte[] filepathresult=dnReq.getFilePath().getBytes();
            req.dataBuf.writeInt(filepathlength);
            req.dataBuf.writeBytes(filepathresult);


            logger.info("RemoteUpgradeOTAPH 根据8108协议向车机终端发送消息体:{}", ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            jedis.set(redis8108Key,"exception");
            jedis.close();
            //res.setRc(JtsResMsg.RC_FAIL);
            //res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req,ProtoConstants.TERMINAL_GENERAL_RES,redis8108Key);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.info("远程升级指令下发成功");
                String redis8108Key = "RoadInstruct:8108:"+result.sim;
                ByteBuf reqBuf = result.dataBuf;

              short flowNo = reqBuf.readShort();
                logger.info("获得应答流水号：" + flowNo);
                short resID=reqBuf.readShort();
                logger.info("获得返回应答ID：" + resID);
                byte updateResult=reqBuf.readByte();
                res.setRc(updateResult);
                logger.info("获得返回应答结果：" + updateResult);
                /*Jedis jedis = null;
                jedis = RedisPoolManager.getJedis();
                jedis.set(dnReq.getSim()+"_OTA",JSON.toJSONString(res));
                logger.info("存入redis结果：" + jedis.get(redis8108Key));
                if(null != jedis){
                    RedisPoolManager.returnResource(jedis);
                }*/
                jedis.set(redis8108Key,"success");
                jedis.close();
                return;
                //processEvent(JSON.toJSONString(res), taskId, dnReq.id(), Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("远程升级指令下发失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                jedis.set(redis8108Key,"fail");
                jedis.close();
                return;
//                processEvent(JSON.toJSONString(res),taskId, dnReq.id(), Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }


    /**
     * 下行协议：File数据文件上传命令
     *
     * @param dnReq
     * @return
     */
    private void Req_E103(final Req_E103 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        Jedis jedis = new RedisPoolManager().getJedis();
        String redisE103Key = "RoadInstruct:E103:"+dnReq.getSim()+"_"+dnReq.getParamId();
        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0xE103;
            req.dataBuf = Unpooled.buffer(32);
            if(dnReq.getParamId().equalsIgnoreCase("F00D")){
                req.dataBuf.writeInt(0xF00D);
            }else if(dnReq.getParamId().equalsIgnoreCase("F00E")){
                req.dataBuf.writeInt(0xF00E);
            }else if(dnReq.getParamId().equalsIgnoreCase("F00F")){
                req.dataBuf.writeInt(0xF00F);
            }else if(dnReq.getParamId().equalsIgnoreCase("F003")){
                req.dataBuf.writeInt(0xF003);
            }else if(dnReq.getParamId().equalsIgnoreCase("F004")){
                req.dataBuf.writeInt(0xF004);
            }else if(dnReq.getParamId().equalsIgnoreCase("F006")){
                req.dataBuf.writeInt(0xF006);
            }else if(dnReq.getParamId().equalsIgnoreCase("F007")){
                req.dataBuf.writeInt(0xF007);
            }else if(dnReq.getParamId().equalsIgnoreCase("F008")){
                req.dataBuf.writeInt(0xF008);
            }

            req.dataBuf.writeShort(dnReq.getParamValue().getBytes().length);
            ProtoUtil.writeString(req.dataBuf,dnReq.getParamValue());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            logger.error("File数据文件命令下发请求异常,将结果存入redis，key: " + redisE103Key);
            jedis.set(redisE103Key,"exception");
            jedis.close();
            return;
        }
        //通用应答，异步上传
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001,redisE103Key);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                String redisE103Key = "RoadInstruct:E103:"+result.sim+"_"+dnReq.getParamId();
                logger.warn("File数据文件下发成功,将结果存入redis，key: " + redisE103Key);
                jedis.set(redisE103Key,"success");
                jedis.close();
                return;
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("文件下发命令失败,将结果存入redis，key: " + redisE103Key, t);
                jedis.set(redisE103Key,"fail");
                jedis.close();
                //res.setRc(JtsResMsg.RC_FAIL);
                //res.setVid(dnReq.getVid());
            }
        });

    }

    /* 查询终端属性*/
    private void do_8107(final Req_8107 dnReq, String taskId, ChannelProcessor channelProcessor) {
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        String str = String.format("平台请求终端系统信息");

        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x8107;
            req.dataBuf = Unpooled.buffer(4);
            logger.info("QueryResourceListPH 根据8107协议向车机终端发送空消息体:{}", ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }
    }




/*********************************终端响应监控中心***************************************/

private void do_0108(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
    logger.warn("do_0108 start...");
    Req_0108 q = new Req_0108();

    //解析上行请求协议
    try {
        ByteBuf reqBuf = upMsg.dataBuf;

        q.setSim(upMsg.sim);
        q.setVid(upMsg.vid);

        q.setUpdateType(reqBuf.readByte());
        q.setUpdateResult(reqBuf.readByte());

        //存到远程升级响应topic
        ProcessKafka.processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.REMOTEUPGRADEOTAPH_TOPIC, channelProcessor);

        if (logger.isDebugEnabled()) {
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
        }
        //updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    } catch (Exception e) {
        logger.warn("协议解析失败:" + upMsg, e);
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
    }
}


    /**
     * file数据下发
     *
     * @param upMsg
     * @return
     */
    private void do_E003(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        Req_E003 q = new Req_E003();
        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);
            q.setSn(upMsg.sn);

            ByteBuf reqBuf = upMsg.dataBuf;
           int paramId=reqBuf.readInt();
            q.setParamId(paramId);
           int dataLen = (int)reqBuf.readShort();

            byte[] data = new byte[dataLen];
            reqBuf.readBytes(data);
            q.setResValue(new String(data, Charset.forName("GBK")));

            //从文件路径里面解析出文件名称
            String fName=q.getResValue().trim();
           String fileName= fName.substring(fName.lastIndexOf("/")+1);
            byte rc= reqBuf.readByte();

            String type=null;
            if(paramId==0xF00D){
                type="F00D";
            }else if (paramId==0xF00E){
                type="F00E";
            }else if(paramId==0xF004){
                type="F004";
            }else if(paramId==0xF006){
                type="F006";
            }
            String redisE103key = "RoadInstruct:E103:"+upMsg.sim+"_"+type;
            String redisftpKey="FTPID:SIM"+upMsg.sim;
            int end=q.getResValue().lastIndexOf("/");
            String redisftpValue=upMsg.sim+"@"+fileName+"@"+q.getResValue().substring(0,end+1);

            Jedis jedis = RedisPoolManager.getJedis();

            jedis.del(redisE103key);

           if(rc==0){
               jedis.set(redisE103key,"download success");
               if(fileName.contains("DBC")||fileName.contains("dbc") ){
                   jedis.set(redisftpKey,redisftpValue);
               }
             }else{
               jedis.set(redisE103key,"download exception");
           }

            if(null != jedis){
                RedisPoolManager.returnResource(jedis);
            }

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);

        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            return;
        }

        return;
    }



    private void do_0107(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        Req_0107 q = new Req_0107();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;



            ProcessKafka.processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.TERMINALPROPERTEIS_TOPIC, channelProcessor);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
            //updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
        }
    }

    @Override
    public void setup() {

    }

    public static void main(String[] args) {
        logger.info("vvvvvvvvvvvvvvvvvvvvvvvvvvv");
    }
}

