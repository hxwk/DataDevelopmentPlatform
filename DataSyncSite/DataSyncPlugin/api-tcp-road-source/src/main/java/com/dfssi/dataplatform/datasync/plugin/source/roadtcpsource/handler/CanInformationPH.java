package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.road.entity.CanAnalyzeBeanItem;
import com.dfssi.dataplatform.datasync.model.road.entity.CanAnalyzeSignal;
import com.dfssi.dataplatform.datasync.model.road.entity.CanBusParamItem;
import com.dfssi.dataplatform.datasync.model.road.entity.Req_0705;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanDBCFormat;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanFileParse;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.FtpApache;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.ProcessKafka;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.constants.FTPServerInfo;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.X0200BitParse;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanConstants.*;
import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common.DbcFileHandle.readValueFromRedis;
import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ByteBufUtil.pad;
import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ProtoUtil.readTimeCAN;

public class  CanInformationPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger("canInformationPH");
    static String dbcFormat = CanDBCFormat.CURRENT.name();
    //static String dbcFormat = CanDBCFormat.OLD.name();
    //private static Properties prop;

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        //CAN ??????????????????
        if (upMsg.msgId == (short)0x0705) {
            do_0705(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("??????????????????????????????msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
    }

    private void do_0705(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.info("??????????????????sim:{}??????0705??????" ,upMsg.sim);
        //1 read object from redis by sim
        List<String> objects;
        String obj = readValueFromRedis(upMsg.sim);
        if (StringUtils.isNotEmpty(obj) && obj.contains(AT)) {
            objects = Arrays.asList(obj.split(AT));
            dbcName = objects.get(1);
            //?????????????????????dbc?????????dbc???????????????????????????,????????????????????????????????????????????????spf-1???spf-2,spf-1
            if (!dbcNameVersion.contains(dbcName)) {
                remotePath = objects.get(2);
                dbcNameVersion.add(dbcName);
                downloadDBCFilebyFtp(dbcName, remotePath);
            } else {
                logger.info("dbc??????????????????,?????????????????????dbc??????????????????!");
            }
        } else {
            logger.warn("sim NOT FOUND! sim???{}" + upMsg.sim);
            return;
        }

        //2 parse dbc file and can message
        CanBusParamItem cpi;
        CanAnalyzeBeanItem analyzeBean;
        List<CanAnalyzeSignal> analyzeSignals = Lists.newArrayList();
        CanFileParse t38CanParse = new CanFileParse(dbcName);
        Req_0705 q = new Req_0705();
        ByteBuf msgData = upMsg.dataBuf;

        /**
         * ???????????????
         * update date:2018-10-12 16:01
         */
        //??????
        int alarm = msgData.readInt();
        q.setAlarm(alarm);
        if (0 != alarm) {
            q.setAlarms(X0200BitParse.parseAlarm(alarm));
        }else{
            q.setAlarms(Lists.newArrayList());
        }

        //????????????
        int vstate = msgData.readInt();
        q.setState(vstate);
        if (0 != vstate) {
            q.setVehicleStatus(X0200BitParse.getStateDesp(vstate));
        }else{
            q.setVehicleStatus(Lists.newArrayList());
        }
        q.setLat(msgData.readInt());
        q.setLon(msgData.readInt());
        q.setAlt(msgData.readShort());
        q.setSpeed(msgData.readShort());
        q.setDir(msgData.readShort());

        try {
            String sim = upMsg.sim;
            q.setSim(String.valueOf(sim));
            q.setVid(upMsg.vid);
            analyzeBean = new CanAnalyzeBeanItem();
            int dataCount = msgData.readShort() & 0xFFFF;
            logger.debug("dataItemCount:{}", dataCount);
            analyzeBean.setItemNum(dataCount);
            Long receiveTime = readTimeCAN(msgData.readBytes(5));
            if(receiveTime == -99999L){
                return;
            }
            analyzeBean.setReceiveTime(receiveTime == null ? NumberUtils.LONG_ZERO : receiveTime);
            q.setCanBusDataReceiveTime(receiveTime == null ? "0L" : String.valueOf(receiveTime));
            logger.debug("receiveTime:{}", DateFormatUtils.format(receiveTime, DATEFORMAT));
            int itemNum = 0;
            while (msgData.readableBytes() >= 12 && itemNum <= dataCount) {
                cpi = new CanBusParamItem();
                String canID = Integer.toHexString(msgData.readInt());
                String canData = pad(Long.toHexString(msgData.readLong()), 16, true);
                logger.debug("canId:{},canData:{}", canID, canData);
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
            /**
             * ??????????????????
             */
            canAnalyzeBeanItem.setAlarm(q.getAlarm());
            canAnalyzeBeanItem.setAlarms(q.getAlarms());
            canAnalyzeBeanItem.setState(q.getState());
            canAnalyzeBeanItem.setVehicleStatus(q.getVehicleStatus());
            canAnalyzeBeanItem.setLon(q.getLon());
            canAnalyzeBeanItem.setLat(q.getLat());
            canAnalyzeBeanItem.setAlt(q.getAlt());
            canAnalyzeBeanItem.setSpeed(q.getSpeed());
            canAnalyzeBeanItem.setDir(q.getDir());

            canAnalyzeBeanItem.setItemNum(null == q.getCanBusParamItems() ? 0 : q.getCanBusParamItems().size());
            canAnalyzeBeanItem.setMsgId(String.format("%04x",upMsg.msgId));
            canAnalyzeBeanItem.setMessageBeanList(q.getCanBusParamItems());
            logger.info("??????0705????????????????????????kafkaChannel???sim???{}" + upMsg.sim);
            ProcessKafka.processEvent(JSON.toJSONString(canAnalyzeBeanItem), taskId, q.id(), Constants.CANINFORMATION_TOPIC, channelProcessor);
        } catch (Exception e) {
            logger.error("upMsg:{} protocol parse error:{}", upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        //updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
    }

    @Override
    public void setup() {
    }

    /**
     * ?????????????????????dbcFormat dbcFile charset
     * ?????????????????????????????????????????????dbc????????????????????????redis??????????????????????????????vid?????????fileId??????Map
     * @return Map
     */
    /*private synchronized Map<String,Object> readProperty(){
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
    }*/
    /**
     * ???dbc?????????FTP?????????????????? user.home /dbcfiles?????????, ??????dbc?????????
     *
     * @param dbcName
     */
    public static void downloadDBCFilebyFtp(String dbcName, String remotePath) {
        if (StringUtils.isNotEmpty(dbcName)) {
            FTPServerInfo.fileName = dbcName;
        }
        if (StringUtils.isNotEmpty(remotePath)) {
            FTPServerInfo.remotePath = remotePath;
        }
        try {
            boolean flag = FtpApache.downFile(FTPServerInfo.url, FTPServerInfo.port, FTPServerInfo.userName, FTPServerInfo.password,
                    FTPServerInfo.remotePath, FTPServerInfo.fileName, FTPServerInfo.localPath);
            logger.info("???dbc?????????FTP??????????????????:{}?????????,?????????:{}.",FTPServerInfo.localPath,FTPServerInfo.fileName);
            if (flag) {
                logger.info("dbc?????????url:{},port:{},remotePath:{},fileName:{}????????????.", FTPServerInfo.url
                        ,FTPServerInfo.port, FTPServerInfo.remotePath, FTPServerInfo.fileName);
            } else {
                logger.warn("download DBC File by Ftp error.");
            }
        } catch (Exception ex) {
            logger.error("CanInformationPH download DBC File error:{}.", ex.getMessage());
        }
    }

}
