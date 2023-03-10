package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile.CanConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile.CanFileParse;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.FastDFSHandler;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yaxon.vn.nd.tbp.si.CanAnalyzeBeanItem;
import com.yaxon.vn.nd.tbp.si.CanAnalyzeSignal;
import com.yaxon.vn.nd.tbp.si.CanBusParamItem;
import com.yaxon.vn.nd.tbp.si.Req_0705;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile.CanConstants.*;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil.pad;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTimeCAN;


public class  CanInformationPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(CanInformationPH.class);
//    static String dbcFormat = CanDBCFormat.OLD.name();
    private static Properties prop;
    private static String dbcFastDFSFileId ;
    private ConcurrentHashMap<String, String> vid2fileid = new ConcurrentHashMap();


    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        //CAN ??????????????????
        if (upMsg.msgId == 0x0705) {
            do_0705(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("??????????????????????????????msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {

    }

    /**
     * ??????vid??????????????????,??????fileId
     * read String from redis by vid
     * note: ??????????????? SaveLatestGpsVo2Redis ?????????value
     * String: vid@dbcFileName@dbcFastDFSFileId
     * @param vid
     * @return vid@dbcFileName@dbcFastDFSFileId
     */
    public String readObjectFromRedis(String vid){
        String value = null;
        if (null != vid) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolManager.getJedis();
                logger.debug("??????redis?????? jedis = " + jedis);
                //FASTID:VID:7c4c631ef0bc444684d4d406be17668e
                value = jedis.get(VID2FASTDFSFID + vid);
                vid2fileid.put(vid, value);
                logger.debug("????????????vid???fastFileId??????????????????, value = " + value);
            } catch (Exception e) {
                value = vid2fileid.get(vid);
                logger.debug("??????????????????fastFileId = " + value, e);
            } finally {
                if (null != jedis) {
                    RedisPoolManager.returnResource(jedis);
                }
            }
        }
        return value;
    }

    /**
     * ???dbc??????????????? user.home ?????????,??????dbcFastDFSFileId
     * @param dbcFastDFSFileId
     */
    private void downloadDBCFile(String dbcFastDFSFileId){
        try {
            FastDFSHandler.download2LocalWithFileId(dbcFastDFSFileId);
        }catch (Exception ex){
            logger.error("CanInformationPH download DBC File error:{}",ex.getMessage());
        }
    }

    private void do_0705(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        /**
         *1 read object from redis by vid
         */
        List<String> objs;
        String obj = readObjectFromRedis(upMsg.vid);
        if(obj!=null&&obj.contains(AT)){
            objs = Arrays.asList(obj.split(AT));
            dbcFastDFSFileId = objs.get(2);
            CanConstants.dbcFastDFSFileId = dbcFastDFSFileId;
        }else{
            logger.warn("???redis?????????dbcFast????????????vid NOT FOUND! vid???{}"+upMsg.vid);
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
//        Map<String,Object> props = readProperty();
//        dbcFormat = String.valueOf(props.get(DBCFORMAT));

        Req_0705 q = new Req_0705();
        CanBusParamItem cpi;
        CanAnalyzeBeanItem analyzeBean;
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
            logger.debug("receiveTime:{}", DateFormatUtils.format(receiveTime,DATEFORMAT));
            int itemNum = 0;
            while(msgData.readableBytes()>=12 && itemNum<=dataCount){
                cpi = new CanBusParamItem();
                String canID = Integer.toHexString(msgData.readInt());
                String canData = pad(Long.toHexString(msgData.readLong()),16,true);
                logger.debug("canId:{},canData:{}",canID,canData);
                cpi.setCanId(canID);
                cpi.setCanData(canData);
                analyzeSignals.addAll(t38CanParse.getAnalyzeSignalList(cpi, dbcFormat));
                itemNum++;
            }

            analyzeBean.setMsgId(String.valueOf(upMsg.msgId));
            analyzeBean.setMessageBeanList(analyzeSignals);
            q.setCanBusParamItems(analyzeSignals);

            //??????????????????kafka
            CanAnalyzeBeanItem canAnalyzeBeanItem = new CanAnalyzeBeanItem();
            canAnalyzeBeanItem.setSim(upMsg.sim);
            canAnalyzeBeanItem.setReceiveTime(receiveTime);
            canAnalyzeBeanItem.setMessageBeanList(q.getCanBusParamItems());
            canAnalyzeBeanItem.setDbcType(q.getDbcType());
            canAnalyzeBeanItem.setId(UUID.randomUUID().toString());
            canAnalyzeBeanItem.setVid(q.getVid());
            canAnalyzeBeanItem.setItemNum(null==q.getCanBusParamItems()?0:q.getCanBusParamItems().size());
            canAnalyzeBeanItem.setMsgId("0705");
            canAnalyzeBeanItem.setMessageBeanList(q.getCanBusParamItems());

            processEvent(JSON.toJSONString(canAnalyzeBeanItem), taskId, q.id(), Constants.CANINFORMATION_TOPIC, channelProcessor);

        }catch (Exception e){
            logger.error("upMsg:{} protocol parse error:{}",upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
    }

    /**
     * ?????????????????????dbcFormat dbcFile charset
     * ?????????????????????????????????????????????dbc????????????????????????redis??????????????????????????????vid?????????fileId??????Map
     * @return Map
     */
    private synchronized Map<String,Object> readProperty(){
        Map<String,Object> props = Maps.newConcurrentMap();
        String fileIdName;
        prop = new Properties();
        try {
            //load dbc.properties filess
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

    @Override
    public void setup() {

    }
}
