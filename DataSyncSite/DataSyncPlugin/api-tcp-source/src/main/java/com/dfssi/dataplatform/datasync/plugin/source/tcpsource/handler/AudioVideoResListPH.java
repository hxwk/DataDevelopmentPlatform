package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile.ByteBufCustomTool;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.X0200BitParse;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;

/**
 * @author JIANKANG
 * 音视频资源列表
 */
public class AudioVideoResListPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(AudioVideoResListPH.class);

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId==(short)0x1205) {//音视频资源列表
            do_1205(upMsg, taskId, channelProcessor);
        }else if(upMsg.msgId==(short)0x1003){//终端上传音视频属性
            do_1003(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId==(short)0x1005){  //终端上传乘客流量
            do_1005(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId==(short)0x0107){ //查询终端属性应答
            do_0107(upMsg, taskId, channelProcessor);
        }else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.info("协议处理器开始处理下行消息:{}", dnReq);
        if (dnReq instanceof Req_9003) {//查询终端音视频属性
            do_9003((Req_9003) dnReq, taskId, channelProcessor);
        }else if(dnReq instanceof Req_8107){ //查询终端属性
            do_8107((Req_8107) dnReq, taskId, channelProcessor);
        }else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    //查询终端音视频属性
    private void do_9003(final Req_9003 dnReq, String taskId, ChannelProcessor channelProcessor) {
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        String str = String.format("平台向终端请求音视频属性");

        rcp.setInstructionDesc(str);
        rcp.setStatus(0);


        logger.info("协议处理器接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.sim = dnReq.getSim();
            req.msgId = (short) 0x9003;
            req.dataBuf = Unpooled.buffer(4);
            logger.info("QueryResourceListPH 根据9003协议向车机终端发送空消息体:{}", ByteBufUtil.toHexString(req.dataBuf.array()));
        }catch (Exception e) {
            logger.error("协议处理器封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }
    }

     /* 查询终端属性*/
    private void do_8107(final Req_8107 dnReq, String taskId, ChannelProcessor channelProcessor) {
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        String str = String.format("平台请求终端属性");

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
        /**
         * 音视频资源列表处理类
         * @param upMsg
         * @return
         */
    private void do_1205(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.warn("do_1205 start...");
        ByteBufCustomTool bbt = new ByteBufCustomTool();
        String word = bbt.bytesToHexString(upMsg.dataBuf);
        logger.warn("current hexstring is  +"+word);
        Req_1205 q = new Req_1205();
        Date dateStart = null;
        Date dateEnd = null;
        SimpleDateFormat formatter=new SimpleDateFormat("yyMMddHHmmss");
        List<AVResourceListItem> avResourceListItemList = Lists.newArrayList();
        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            AVResourceListVo avResourceListVo = new AVResourceListVo();

            AVResourceListItem item ;
            avResourceListVo.setSim(upMsg.sim);
            avResourceListVo.setVid(upMsg.vid);

            avResourceListVo.setFlowNo(reqBuf.readUnsignedShort());
            avResourceListVo.setAvResourceNum(reqBuf.readInt());

            while(reqBuf.readableBytes()>=28){
                item = new AVResourceListItem();
                item.setChannelNo(reqBuf.readUnsignedByte());
                byte[] startTime = new byte[6];
                reqBuf.readBytes(startTime);
                byte[] endTime = new byte[6];
                reqBuf.readBytes(endTime);
                try {
                    dateStart = formatter.parse(ProtoUtil.bcd2Str(startTime));
                    dateEnd = formatter.parse(ProtoUtil.bcd2Str(endTime));
                    logger.info("start date:{},end date:{}",dateStart,dateEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setStartTime(dateStart.getTime());
                logger.info("dateStart:{},getTime:{}",dateStart,dateStart.getTime());
                item.setEndTime(dateEnd.getTime());
                logger.info("dateEnd:{},getTime:{}",dateEnd,dateEnd.getTime());
                byte[] alarms = new byte[8];
                reqBuf.readBytes(alarms);
                item.setAlarmMark(alarms);
                item.setAlarmMarks(X0200BitParse.parseAlarm(X0200BitParse.longFrom8Bytes(alarms, 0, true)));
                item.setAvResourceType(reqBuf.readByte());
                item.setStreamType(reqBuf.readByte());
                item.setStorageType(reqBuf.readByte());
                item.setFileSize(reqBuf.readInt());
                avResourceListItemList.add(item);
            }
            avResourceListVo.setAvResourceListItemList(avResourceListItemList);
            q.setAvResourceList(avResourceListVo);

            //存到音视频资源列表topic
            processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.AVRESOURCELIST_TOPIC, channelProcessor);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
        }
    }

    /*
    终端上传音视频处理类
    */
    private void do_1003(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        Req_1003 q = new Req_1003();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            AVPropertyVo avpropertyvo = new AVPropertyVo();

            AVResourceListItem item ;
            avpropertyvo.setSim(upMsg.sim);
            avpropertyvo.setVid(upMsg.vid);
            //音频编码模式
            String audioencodemode = encodeMode2String(String.valueOf(reqBuf.readByte()));
            avpropertyvo.setAudioEncodeMode(audioencodemode);
            avpropertyvo.setAudioApology(String.valueOf(reqBuf.readByte()));
            String samplrate=samplrate2String(String.valueOf(reqBuf.readByte()));
            avpropertyvo.setSamplRate(samplrate);
            String samplnumber=samplNumber2String(String.valueOf(reqBuf.readByte()));
            avpropertyvo.setSamplNumber(samplnumber);
            avpropertyvo.setAudioFrameLength(reqBuf.readShort());
            avpropertyvo.setIsOutputSupported(String.valueOf(reqBuf.readByte()));
            //视频编码模式
            String videoEncodeMode=encodeMode2String(String.valueOf(reqBuf.readByte()));
            avpropertyvo.setVideoEncodeMode(videoEncodeMode);
            avpropertyvo.setSupportMaxAudioChannelNum(String.valueOf(reqBuf.readByte()));
            avpropertyvo.setSupportMaxVideoChannelNum(String.valueOf(reqBuf.readByte()));

            q.setAvpropertyvo(avpropertyvo);

            processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.AVPROPERTY_TOPIC, channelProcessor);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
        }
    }

    /*终端上传乘客流量*/
    private void do_1005(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        Req_1005 q = new Req_1005();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;

            PassengerFlow passengerflow =new PassengerFlow();
            passengerflow.setStartTime(readTime(reqBuf.readBytes(6)));
            passengerflow.setEndTime(readTime(reqBuf.readBytes(6)));
            passengerflow.setUpNum(reqBuf.readInt());
            passengerflow.setDownNum(reqBuf.readInt());
            q.setPassengerflow(passengerflow);

            processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.AVPASSENGERFLOW_TOPIC, channelProcessor);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
        }
    }

    /*查询终端属性应答*/
    private void do_0107(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        Req_0107 q = new Req_0107();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;

            TerminalProperties terminalproperties =new TerminalProperties();
            short terminalType=reqBuf.readShort();
            List<String> terminalTypeList=parseTerminalType(terminalType);
            terminalproperties.setTerminalType(terminalTypeList);
            byte[] manufacturerID = new byte[5];
            reqBuf.readBytes(manufacturerID);
            terminalproperties.setManufacturerID(String.valueOf(manufacturerID));
            byte[] terminalModel = new byte[20];
            reqBuf.readBytes(terminalModel);
            terminalproperties.setTerminalModel(String.valueOf(terminalModel));
            byte[] terminalID = new byte[7];
            reqBuf.readBytes(terminalID);
            terminalproperties.setTerminalID(String.valueOf(terminalID));
            byte[] ICCID = new byte[10];
            reqBuf.readBytes(ICCID);
            terminalproperties.setICCID(String.valueOf(ICCID));

            int n=reqBuf.readUnsignedByte();
            terminalproperties.setTHVNL(n);

            byte[] TerminalHardwareVersionNumber = new byte[n];
            reqBuf.readBytes(TerminalHardwareVersionNumber);
            terminalproperties.setTerminalHardwareVersionNumber(String.valueOf(TerminalHardwareVersionNumber));

            int m=reqBuf.readUnsignedByte();
            terminalproperties.setTFVNL(m);

            byte[] TerminalFirmwareVersionNumber = new byte[m];
            reqBuf.readBytes(TerminalFirmwareVersionNumber);
            terminalproperties.setTerminalFirmwareVersionNumber(String.valueOf(TerminalFirmwareVersionNumber));
            int gnss=reqBuf.readUnsignedByte();
            List<String> GNSSList=parseGNSS(gnss);
            terminalproperties.setGNSS(GNSSList);
            int CMA=reqBuf.readUnsignedByte();
            List<String> CMAList=parseCMA(CMA);
            terminalproperties.setCommunicationModuleAttribute(CMAList);
            q.setPassengerflow(terminalproperties);

            processEvent(JSON.toJSONString(q), taskId, q.id(), Constants.TERMINALPROPERTEIS_TOPIC, channelProcessor);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
        }
    }



    /*编码模式转化成字符串*/
    private String encodeMode2String(String encode){
        String str = "";
        switch (encode){
            case "0":
                str = "保留";
                break;
            case "1":
                str="G.721";
                break;
            case "2":
                str="G.722";
                break;
            case "3":
                str="G.723";
                break;
            case "4":
                str="G.728";
                break;
            case "5":
                str="G.729";
                break;
            case "6":
                str="G.711A";
                break;
            case "7":
                str="G.711C";
                break;
            case "8":
                str="G.726";
                break;
            case "9":
                str="G.729A";
                break;
            case "10":
                str="DVI4_3";
                break;
            case "11":
                str="DVI4_4";
                break;
            case "12":
                str="DVI4_8K";
                break;
            case "13":
                str="DVI4_16K";
                break;
            case "14":
                str="LPC";
                break;
            case "15":
                str="S16BE_STEREO";
                break;
            case "16":
                str="S16BE_MONO";
                break;
            case "17":
                str="MPTGAUDIO";
                break;
            case "18":
                str="LPCM";
                break;
            case "19":
                str="AAC";
                break;
            case "20":
                str="WMA9STD";
                break;
            case "21":
                str="HEAAC";
                break;
            case "22":
                str="PCM_VOICE";
                break;
            case "23":
                str="PCM_AUDIO";
                break;
            case "24":
                str="AACLC";
                break;
            case "25":
                str="MP3";
                break;
            case "26":
                str="ADPCMA";
                break;
            case "27":
                str="MP4AUDIO";
                break;
            case "28":
                str="AMR";
                break;
            case "91":
                str="透传";
                break;
            case "98":
                str="H.264";
                break;
            case "99":
                str="H.265";
                break;
            case "100":
                str="AVS";
                break;
            case "101":
                str="SVAC";
                break;
        }
        return str;
    }
    /*输入音频采样率转字符串*/
   private String samplrate2String(String samplrate){
       String str = "";
       switch (samplrate){
           case "0":
               str = "8";
               break;
           case "1":
               str="22.05";
               break;
           case "2":
               str="44.1";
               break;
           case "3":
               str="48";
               break;
       }
       return str;
   }
    /*输入音频采样位数转字符串*/
    private String samplNumber2String(String samplNumber){
     String str="";
     switch (samplNumber){
         case "0":
             str="8位";
             break;
         case "1":
             str="16位";
             break;
         case "2":
             str="32位";
             break;
     }
     return str;
    }

    /**
     * 解析终端类型
     */
    private static List<String> parseTerminalType(int termainalType){
        List<String> typeList = Lists.newArrayList();
        if ((termainalType & 1) != 0) {
            typeList.add("适用客运车辆");
        } else {
            typeList.add("不适用客运车辆");
        }
        if ((termainalType & 2) != 0) {
            typeList.add("适用危险品车辆");
        } else {
            typeList.add("不适用危险品车辆");
        }
        if ((termainalType & 3) != 0) {
            typeList.add("适用普通货运车辆");
        } else {
            typeList.add("不适用普通货运车辆");
        }
        if ((termainalType & 4) != 0) {
            typeList.add("适用出租车辆");
        } else {
            typeList.add("不适用出租车辆");
        }
        if ((termainalType & 7) != 0) {
            typeList.add("支持硬盘录像");
        } else {
            typeList.add("不支持硬盘录像");
        }
        if ((termainalType & 8) != 0) {
            typeList.add("分体机");
        } else {
            typeList.add("一体机");
        }

        return typeList;
    }

    /**
     * 解析GNSS模块属性
     */
    private static List<String> parseGNSS(int gnss){
        List<String> GNSSList = Lists.newArrayList();
        if ((gnss & 1) != 0) {
            GNSSList.add("支持 GPS 定位");
        } else {
            GNSSList.add("不支持 GPS 定位");
        }
        if ((gnss & 2) != 0) {
            GNSSList.add("支持北斗定位");
        } else {
            GNSSList.add("不支持北斗定位");
        }
        if ((gnss & 3) != 0) {
            GNSSList.add("支持 GLONASS 定位");
        } else {
            GNSSList.add("不支持 GLONASS 定位");
        }
        if ((gnss & 4) != 0) {
            GNSSList.add("支持 Galileo 定位");
        } else {
            GNSSList.add("不支持 Galileo 定位");
        }
        return GNSSList;
    }

    /**
     * 解析通信模块属性
     */
    private static List<String> parseCMA(int cma){
        List<String> CMAList = Lists.newArrayList();
        if ((cma & 1) != 0) {
            CMAList.add("支持GPRS通信");
        } else {
            CMAList.add("不支持GPRS通信");
        }
        if ((cma & 2) != 0) {
            CMAList.add("支持CDMA通信");
        } else {
            CMAList.add("不支持CDMA通信");
        }
        if ((cma & 3) != 0) {
            CMAList.add("支持TD-SCDMA通信");
        } else {
            CMAList.add("不支持TD-SCDMA通信");
        }
        if ((cma & 4) != 0) {
            CMAList.add("支持WCDMA通信");
        } else {
            CMAList.add("不支持WCDMA通信");
        }
        if ((cma & 5) != 0) {
            CMAList.add("支持CDMA2000通信");
        } else {
            CMAList.add("不支持CDMA2000通信");
        }
        if ((cma & 6) != 0) {
            CMAList.add("支持TD-LTE通信");
        } else {
            CMAList.add("不支持TD-LTE通信");
        }
        if ((cma & 8) != 0) {
            CMAList.add("支持其他通信方式");
        } else {
            CMAList.add("不支持其他通信方式");
        }
        return CMAList;
    }


    @Override
    public void setup() {

    }
}
