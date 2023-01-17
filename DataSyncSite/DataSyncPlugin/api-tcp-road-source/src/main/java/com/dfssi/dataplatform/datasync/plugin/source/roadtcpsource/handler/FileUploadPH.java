package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.road.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ProtoUtil;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @descriiption 文件上传
 * @autho chenf
 *
 */
public class FileUploadPH extends BaseProtoHandler {

    Logger logger  = LoggerFactory.getLogger(FileUploadPH.class);



    /**
     * 文件上传，上传完成，通知
     * @param upMsg
     * @param taskId
     * @param channelProcessor
     */
    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId == (short)0xE002) {//文件数据上传
            do_E002(upMsg ,taskId,  channelProcessor);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    /**
     * 请求指令下发
     * @param dnReq
     * @param taskId
     * @param channelProcessor
     */
    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_E101) {//SD卡文件索引目录查询
            Req_E101((Req_E101) dnReq,  taskId,  channelProcessor);
        }
        else if (dnReq instanceof Req_E102) {//SD卡文件查询
            Req_E102((Req_E102) dnReq,taskId,channelProcessor);
        }
        else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    /**
     * SD file数据上传。分包未合并。
     *
     * @param upMsg
     * @return
     */
    private void do_E002(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.info("[{}]接收到上行请求消息:{}", upMsg.sim, upMsg);
        Req_E002 q = new Req_E002();
        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);
            q.setSn(upMsg.sn);

            ByteBuf reqBuf = upMsg.dataBuf;
            int paramId =(int)reqBuf.readUnsignedInt();

            int dataLen = reqBuf.readShort();

            byte[] data = new byte[dataLen];
            reqBuf.readBytes(data);
            byte rc = reqBuf.readByte();
            String redisE102key = "RoadInstruct:E102:" + upMsg.sim;

            q.setResValue(new String(data, Charset.forName("GBK")));

            String redisE002key = "RoadInstruct:E102:" + upMsg.sim;

            logger.info("[{}]接收到上行请求消息:{}", upMsg.sim, q);

            String path  = q.getResValue().split(";")[0];
            //1 成功 -1失败 -2异常 4上传ftp成功 5 上传ftp失败
            Jedis jedis = RedisPoolManager.getJedis();

            if(0==rc) {

                String ftp = q.getResValue().split(";")[1];

                logger.info("文件上传成功Rediskey:" + redisE002key + ";field:"+path+"value:" + JSON.toJSONString(ftp));

                jedis.hset(redisE002key, path, ftp);

                jedis.hset(redisE102key,path,"4");

                sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
            }else{

                jedis.hset(redisE102key,path,"5");
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);

            }
            if (null != jedis) {
                jedis.close();
            }

        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            return;
        }

        return;
    }

    /**
     * SD卡文件索引目录查询
     *
     * @param dnReq
     * @return
     */
    private void Req_E101(final Req_E101 dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("接收到SD卡文件索引目录查询下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_E001 res = new Res_E001();
        String redisE101key = "RoadInstruct:E101:"+String.valueOf(dnReq.getSim());
        Jedis jedis = RedisPoolManager.getJedis();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xE101;
            req.sim = String.valueOf(dnReq.getSim());
            int idNum = dnReq.getParamIds().length;
            //int cap = 8+4*8*idNum;
            //支持自动扩容
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(idNum);
            //req.dataBuf.writeInt(idNum);
            for(int paramId:dnReq.getParamIds()){
                req.dataBuf.writeInt(paramId);
            }
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            jedis.set(redisE101key,"exception");
            jedis.close();
            return;
        }


        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0xE001,redisE101key);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询SD卡目录成功，终端返回参数: " + result);
                ByteBuf reqBuf = result.dataBuf;
                short flowNo = reqBuf.readShort();
                logger.info("获得E001的返回流水号：" + flowNo);
                res.setFlowNo(flowNo);

                int parametersNum =reqBuf.readByte(); //reqBuf.readInt();
                logger.info("获得E001的参数个数：" + parametersNum);

                List<ParamItem> paramItems = new ArrayList<>();
                for (int i = 0; i < parametersNum; i++) {
                    int parameterId = (int)reqBuf.readUnsignedInt();
                    logger.info("获得E001的参数ID：" + parameterId);
                    //根据id判断值的类型
                    int parameterValueLength = reqBuf.readShort();
                    logger.info("获得E001的参数值长度：" + parameterValueLength);
                    byte[] out = new byte[parameterValueLength];
                    reqBuf.readBytes(out);
                    String parameterValue = new String(out);
                    logger.info("获得E001的参数值：" + parameterValue);
                    ParamItem item = new ParamItem(parameterId, parameterValue);
                    paramItems.add(item);
                }
                res.setParamItems(paramItems);
                logger.info("返回的目录结构" + JSON.toJSONString(res));
                String redisE101key = "RoadInstruct:E101:"+result.sim;
                jedis.set(redisE101key,JSON.toJSONString(res));
                jedis.close();
                //processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.SD_PARAMS_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询SD目录失败", t);
                jedis.set(redisE101key,"fail");
                jedis.close();
            }
        });

    }

    /**
     * 下行协议：File数据文件上传命令
     *
     * @param dnReq
     * @return
     */
    private void Req_E102(final Req_E102 dnReq, String taskId, ChannelProcessor channelProcessor) {

        logger.info("接收到File数据文件上传命令请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        String redisE102key = "RoadInstruct:E102:"+dnReq.getSim();
        Jedis jedis = RedisPoolManager.getJedis();
        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0xE102;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeInt(dnReq.getParamId());
            //req.dataBuf.writeShort(dnReq.getParamLength());
            if(StringUtils.isBlank(dnReq.getParamValue())) {
                jedis.hset(redisE102key,dnReq.getParamValue(),"-2");
                jedis.close();
                return;
            }
            int length = dnReq.getParamValue().getBytes(Charset.forName("GBK")).length;
            req.dataBuf.writeShort(length);
            ProtoUtil.writeString(req.dataBuf,dnReq.getParamValue());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            logger.info("SD文件上传命令下发终端异常");
            //1 成功 -1失败 -2异常 4上传ftp成功 5 上传ftp失败
            jedis.hset(redisE102key,dnReq.getParamValue(),"-2");
            jedis.close();
            //res.setRc(JtsResMsg.RC_FAIL);
            //res.setVid(dnReq.getVid());
            return;
        }

        //通用应答，异步上传
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001,redisE102key,dnReq.getParamValue());
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {

                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                String redisE102key = "E102_"+result.sim;
                //1 成功 -1失败 -2异常
                jedis.hset(redisE102key,dnReq.getParamValue(),"1");
                jedis.close();
                logger.info("SD文件上传命令下发终端成功:{}_{}",redisE102key,dnReq.getParamValue());
            }

            @Override
            public void onFailure(Throwable t) {
                //1 成功 -1失败 -2异常
                logger.warn("SD文件上传命令下发终端失败", t);
                jedis.hset(redisE102key,dnReq.getParamValue(),"-1");
                jedis.close();
                return;
            }
        });

    }

    @Override
    public void setup() {

    }



}
