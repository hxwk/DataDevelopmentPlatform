package com.yaxon.vn.nd.tas.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.canFileParse.CanDBCFormat;
import com.yaxon.vn.nd.tas.canFileParse.CanFileParse;
import com.yaxon.vn.nd.tas.common.CanConstants;
import com.yaxon.vn.nd.tas.common.RedisPoolManager;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.FastDFSHandler;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.yaxon.vn.nd.redis.RedisConstants.GK_VEHICLE_STATE;
import static com.yaxon.vn.nd.tas.canFileParse.CanConstants.*;
import static com.yaxon.vn.nd.tas.common.CanConstants.VID2FASTDFSFID;
import static com.yaxon.vn.nd.tas.util.ByteBufUtil.pad;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTimeCAN;

/**
 * @author: JianKang
 * @Time: 2018-01-18 10:31
 * Can information handle
 */
@Component
public class CanInformationPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(CanInformationPH.class);
    static String dbcFormat = CanDBCFormat.OLD.name();
    private static Properties prop;
    private static String dbcFastDFSFileId ;
    private ConcurrentHashMap<String, String> vid2fileid = new ConcurrentHashMap();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{configProperties['dms.nodeId']}")
    private String nodeId;

    @Value("#{configProperties['terminal.maxidletimemillis']}")
    private Long terminalMaxIdleTimeMillis;

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        //CAN 总线数据上传
        if (upMsg.msgId == 0x0705) {
            do_0705(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {

    }

    /**
     * 通过vid获取映射关系,获取fileId
     * read String from redis by vid
     * note: 可能会参考 SaveLatestGpsVo2Redis 序列化value
     * String: vid@dbcFileName@dbcFastDFSFileId
     * @param vid
     * @return vid@dbcFileName@dbcFastDFSFileId
     */
    public String readObjectFromRedis(String vid){
        String value = null;
        if (null != vid) {
            Jedis jedis = null;
            try {
                jedis = new RedisPoolManager().getJedis();
                logger.info("获取redis状态 jedis = " + jedis);
                //FASTID:VID:7c4c631ef0bc444684d4d406be17668e
                value = jedis.get(VID2FASTDFSFID + vid);
                vid2fileid.put(vid, value);
                logger.info("将获取的vid：fastFileId映射放入内存, value = " + value);
            } catch (Exception e) {
                value = vid2fileid.get(vid);
                logger.debug("从内存在获取fastFileId = " + value, e);
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
        }
        return value;
    }

    /**
     * 将dbc文件下载到 user.home 目录下,通过dbcFastDFSFileId
     * @param dbcFastDFSFileId
     */
    private void downloadDBCFile(String dbcFastDFSFileId){
        try {
            FastDFSHandler.download2LocalWithFileId(dbcFastDFSFileId);
        }catch (Exception ex){
            logger.error("CanInformationPH download DBC File error:{}",ex.getMessage());
        }
    }

    private void do_0705(final ProtoMsg upMsg) {
        /**
         *1 read object from redis by vid
         */
        List<String> objs;
        String obj = readObjectFromRedis(upMsg.vid);
        if(obj.contains(AT)){
            objs = Arrays.asList(obj.split(AT));
            dbcFastDFSFileId = objs.get(2);
            CanConstants.dbcFastDFSFileId = dbcFastDFSFileId;
        }else{
            logger.warn("vid NOT FOUND From Redis!");
            return;
        }

        /**
         * 2 download DBC File
         * dbcFastDFSFileId: group1/M00/00/A2/rBAByVppifeAY964AAAGMXIlLc4291.dbc
         * c:\User\Jian\rBAByVppifeAY964AAAGMXIlLc4291.dbc
         */
        downloadDBCFile(dbcFastDFSFileId);

        /**
         * 3 get dbcfile charset
         * fileIdName: rBAByVppifeAY964AAAGMXIlLc4291.dbc
         */
        Map<String,Object> props = readProperty();
        dbcFormat = String.valueOf(props.get(DBCFORMAT));

        Req_0705 q = new Req_0705();
        CanBusParamItem cpi;
        CanAnalyzeBeanItem analyzeBean =null;
        List<CanAnalyzeSignal> analyzeSignals = Lists.newArrayList();
        /**
         * CanFileParse: Can file parse
         * CanConstants have values
         */
        CanFileParse t38CanParse = new CanFileParse();
        try{
            String sim = upMsg.sim;
            q.setSim(String.valueOf(sim));
            q.setVid(upMsg.vid);
            analyzeBean = new CanAnalyzeBeanItem();
            ByteBuf msgData = upMsg.dataBuf;
            int dataCount = msgData.readShort()& 0xFFFF;
            logger.debug("dataItemCount:{}",dataCount);
            analyzeBean.setItemNum(dataCount);
            Long receiveTime = readTimeCAN(msgData.readBytes(5));
            analyzeBean.setReceiveTime(receiveTime==null?0L:receiveTime);
            q.setCanBusDataReceiveTime(receiveTime==null?"0L":String.valueOf(receiveTime));
            logger.debug("receiveTime:{}",DateFormatUtils.format(receiveTime,DATEFORMAT));
            int itemNum = 0;
            while(msgData.readableBytes()>=12 && itemNum<=dataCount){
                cpi = new CanBusParamItem();
                String canID = Integer.toHexString(msgData.readInt());
                cpi.setCanId(canID);
                String canData = pad(Long.toHexString(msgData.readLong()),16,true);
                cpi.setCanData(canData);
                logger.debug("canId:{},canData:{}",canID,canData);
                analyzeSignals.addAll(t38CanParse.getAnalyzeSignalList(cpi, dbcFormat));
                itemNum++;
            }
        }catch (Exception e){
            logger.error("upMsg:{} protocol parse error:{}",upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }finally {
            analyzeBean.setMsgId(String.valueOf(upMsg.msgId));
            analyzeBean.setMessageBeanList(analyzeSignals);
            q.setCanBusParamItems(analyzeSignals);

        }
        try {
            ValueOperations<String, String> op = redisTemplate.opsForValue();
            op.set(GK_VEHICLE_STATE + upMsg.vid, nodeId, terminalMaxIdleTimeMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("在Redis中更新车辆状态失败", e);
        }
        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
                // r.setRc((byte)0x04); 注释该代码 by liuzf,怀疑设备收到04时会重传数据，改成0
                r.setRc(ProtoConstants.RC_OK);
                sendCenterGeneralRes(upMsg, r.getRc());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 从配置文件读取dbcFormat dbcFile charset
     * 之后不从配置文件中读取该解析的dbc文件类型，要读取redis中配置文件对象，匹配vid对应的fileId放入Map
     * @return Map
     */
    private synchronized Map<String,Object> readProperty(){
        Map<String,Object> props = Maps.newConcurrentMap();
        String fileIdName;
        prop = new Properties();
        try {
            //load dbc.properties file
            String classRootPath = CanInformationPH.class.getClassLoader().getResource("").getFile();
            prop.load(new FileInputStream(new File(classRootPath, "dbc.properties")));
        } catch (IOException e) {
            logger.error("readProperty error:{}",e);
        }

        if(null!=dbcFastDFSFileId&& !StringUtils.EMPTY.equals(dbcFastDFSFileId)){
            fileIdName =dbcFastDFSFileId.substring(dbcFastDFSFileId.lastIndexOf("/")+1);
            props.put(DBCFORMAT,prop.getProperty(DBCFORMAT));
            props.put(CHARSET,prop.getProperty(CHARSET));
            props.put(DBCFILE,fileIdName);
            CanConstants.dbcFormat = prop.getProperty(DBCFORMAT);
            CanConstants.charSet = prop.getProperty(CHARSET);
            CanConstants.fileIdName = fileIdName;
        }
        return props;
    }
}
