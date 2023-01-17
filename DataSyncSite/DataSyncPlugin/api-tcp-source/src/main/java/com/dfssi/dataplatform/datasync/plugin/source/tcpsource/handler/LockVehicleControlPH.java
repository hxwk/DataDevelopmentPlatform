package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.GeodeTool;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;

public class LockVehicleControlPH extends BaseProtoHandler{
    private static final Logger logger = LoggerFactory.getLogger(LockVehicleControlPH.class);

    @Override
    public void setup() {

    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_F001_nd) {//设置锁车参数
            do_F001_nd( (Req_F001_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F003_nd){//查询锁车各个状态
            do_F003_nd((Req_F003_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F006_nd){ //参数设置
            do_F006_nd((Req_F006_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F007_nd){ //IP2开关状态
            do_F007_nd((Req_F007_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F008_nd){ //SEED设置
            do_F008_nd((Req_F008_nd) dnReq, taskId, channelProcessor);
        }
        else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        //ID超出了Java short型的范围，上报的时候强转，变成了负数，这边也强转才能对应上
        if (upMsg.msgId == (short)0xF101) {//锁车控制的结果上报。
            do_F101(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId == (short) 0xF002){//锁车心跳
            do_F002(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId == (short) 0xF102){//超时上报
            do_F102(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId ==(short) 0xF103){ //控制指令通用应答
            do_F103(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId ==(short) 0xF106){
            do_F106(upMsg, taskId, channelProcessor);
        }else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    private void do_F101(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        final Req_F101_nd req = new Req_F101_nd();
        try{
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            req.setInstruction((int)reqBuf.readByte());
            reqBuf.skipBytes(3);//跳过3个预留字节
            req.setResult((int)reqBuf.readShort());//控制命令执行结果
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, req);

            VnndInstructionResMsg rcp = new VnndInstructionResMsg();
            rcp.setInstruction(req.getInstruction());
            rcp.setVid(req.getVid());
            rcp.setStatus(0 == req.getResult() ? 1 : 0);

            processEvent(rcp, CodecUtils.shortToHex(upMsg.msgId), taskId, Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
            //执行结果存redis
//            Map  map=new HashMap();
//            map.put("vid",req.getVid());
//            map.put("msgId","jts.F001.nd");
//            map.put("instruction",req.getInstruction());
//            map.put("status",req.getResult());
//            map.put("datetime",Calendar.getInstance().getTimeInMillis());
//            RedisUtils.commandStatus2Redis(map);
        }catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    }

    private void do_F002(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.info("接收到锁车心跳:{}", upMsg);
        final Req_F002_nd req = new Req_F002_nd();
        try{
            logger.info("车辆vid:{}", upMsg.vid);
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            byte byte1 = reqBuf.readByte();
            String byte1String = ByteBufUtil.byte2String(byte1);
            logger.info("1 接收到锁车心跳:byte1 = {}, byte1String = {}", byte1, byte1String);
            req.setOnBitchSign(Integer.parseInt(byte1String.substring(7), 2));
            req.setAccBitchSign(Integer.parseInt(byte1String.substring(6, 7), 2));
            req.setOffLineLockVehicleSign(Integer.parseInt(byte1String.substring(5, 6), 2));
            req.setWriteVDR_IDSign(Integer.parseInt(byte1String.substring(4, 5), 2));
            req.setOpenAuthSign(Integer.parseInt(byte1String.substring(3, 4), 2));
            req.setCloseAuthSign(Integer.parseInt(byte1String.substring(2, 3), 2));
            req.setICHeartBeatSign(Integer.parseInt(byte1String.substring(0, 2), 2));
            byte byte2 = reqBuf.readByte();
            String byte2String = ByteBufUtil.byte2String(byte2);
            logger.info("2 接收到锁车心跳:byte2 = {}, byte2String = {}", byte2, byte2String);
            req.setLimpStatusSign(Integer.parseInt(byte2String.substring(4), 2));
            req.setProcessMonitorSign(Integer.parseInt(byte2String.substring(3, 4), 2));
            req.setOfflineMonitorSign(Integer.parseInt(byte2String.substring(2, 3), 2));
            req.setRealLimpStatusSign(Integer.parseInt(byte2String.substring(0, 2), 2));
            byte byte3 = reqBuf.readByte();
            String byte3String = ByteBufUtil.byte2String(byte2);
            logger.info("3 接收到锁车心跳:byte3 = {}, byte3String = {}", byte3, byte3String);
            req.setVehicleBusinessStatus(Integer.parseInt(byte3String.substring(4), 2));
            req.setICBreakSign(Integer.parseInt(byte3String.substring(3, 4), 2));
            req.setEecuSign(Integer.parseInt(byte3String.substring(2, 3), 2));
            req.setLimpCommand(Integer.parseInt(byte3String.substring(1, 2), 2));
            req.setMeterBreakSign(Integer.parseInt(byte3String.substring(0, 1), 2));
            logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, req);

            //跳过5个字节，读取时间
            reqBuf.skipBytes(5);
            Date time=readTime(reqBuf.readBytes(6));
            JSONObject json=new JSONObject();
            json.put("time",time);
            json.put("vid",req.getVid());
            json.put("onBitchSign",req.getOnBitchSign());
            //更新redis并 推送kafka
            updateONStatus2Redis(json,channelProcessor);
            VnndInstructionResMsg res = new VnndInstructionResMsg();
            res.setVid(req.getVid());
            res.setStatus(1);

            if (4 == req.getLimpStatusSign()) {//当识别为不在线跛行时，平台自动下发解跛指令
                Req_F001_nd offLimpReq = new Req_F001_nd();
                offLimpReq.setInstruction(10);
                offLimpReq.setLimpStatus(1);
                offLimpReq.setBusiness(req.getVehicleBusinessStatus());
                offLimpReq.setVid(req.getVid());

                do_F001_nd_ByVId(offLimpReq, taskId, channelProcessor);
                logger.info("不在线跛行，下发解跛指令");
            } else {//更新平台的锁车状况
                if (2 == req.getLimpStatusSign() || 6 == req.getLimpStatusSign() || 8 == req.getLimpStatusSign()) {
                    //发送跛行状态
                    res.setInstruction(0x09);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                } else if (1 == req.getOfflineMonitorSign()) {
                    //发送不在线监控状态
                    res.setInstruction(0x06);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                } else if (1 == req.getProcessMonitorSign()) {
                    //发送过程监控状态
                    res.setInstruction(0x04);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                }
            }

            logger.debug("锁车心跳应答：" );
            sendCenterGeneralRes(upMsg, (byte)0xDF);
        }catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    //F103的应答消息
    private void do_F103(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        final VnndF103ResMsg res = new VnndF103ResMsg();
        try{
        ByteBuf dataBuf = upMsg.dataBuf;
        res.setVid(upMsg.vid);
      /*  dataBuf.readBytes(2);*/   //主动上报流水号为0，应答时为平台流水号
        res.setSerialNumber(String.valueOf(dataBuf.readShort()));    //设置流水号
        res.setInstruction((int)dataBuf.readByte());
        res.setRespTime(readTime(dataBuf.readBytes(6)));
        res.setRc(dataBuf.readByte());
        if(res.getInstruction()==0x01){
            // VDR发送状态标志定义表第一个字节 转为8位的二进制字符串list
            List<String> byte1 = get8bitBinaryStringList(dataBuf.readByte());
            res.setOn(Byte.parseByte(byte1.get(7),2));
            res.setAcc(Byte.parseByte(byte1.get(6),2));
            res.setNotOnlineLock(Byte.parseByte(byte1.get(5),2));
            res.setWriteVdrId(Byte.parseByte(byte1.get(4),2));
            res.setStartupAuthentication(Byte.parseByte(byte1.get(3),2));
            res.setFunctionShutdown(Byte.parseByte(byte1.get(2),2));
            res.setIcHeartbeat(Byte.parseByte(byte1.get(0)+byte1.get(1),2));
            //VDR发送状态标志定义表第二个字节 转为8位的二进制字符串list
            List<String> byte2 = get8bitBinaryStringList(dataBuf.readByte());
            res.setLimpStatus(Byte.parseByte(byte2.get(4)+byte2.get(5)+byte2.get(6)+byte2.get(7),2));  //跛行状态标志
            res.setProcessMonitorSwitch(Byte.parseByte(byte2.get(3),2));    //过程监控总开关标志
            res.setNotOnlineMonitorSwitch(Byte.parseByte(byte2.get(2),2));  //不在线监控总开关标志
            res.setEecuLimpStatus(Byte.parseByte(byte2.get(0)+byte2.get(1),2));//EECU实际跛行状态
            //VDR发送状态标志定义表第三个字节 转为8位的二进制字符串list
            List<String> byte3 = get8bitBinaryStringList(dataBuf.readByte());
            res.setVehicleBusiness(Byte.parseByte(byte3.get(4)+byte3.get(5)+byte3.get(6)+byte3.get(7),2));
            res.setIcFault(Byte.parseByte(byte3.get(3),2));
            res.setVdrEecuMessage(Byte.parseByte(byte3.get(2),2));
            res.setLimp(Byte.parseByte(byte3.get(1),2));
            res.setMeterBusiness(Byte.parseByte(byte3.get(0),2));
            //VDR心跳状态标志结构表的第四字节
            List<String> byte4 = get8bitBinaryStringList(dataBuf.readByte());
            res.setCanStatus(Byte.parseByte(byte4.get(6)+byte4.get(7),2));
            //BIT 3 - BIT 8 预留

            /*第 5-11个字节*/
            //VDR 自身存储 ID(7 个字节)
            byte a[]=new byte[7];
            dataBuf.readBytes(a);
            res.setVdrId(new String(a));

            /*第 12-19个字节*/
            /*VDR 自身存储底盘号(8 个字节)：以底盘号 G8020529 为例
            ，每位当成字符处理，则为：0x47 0x38 0x32 0x32 0x30 0x35 0x32 0x39*/
            byte b[]=new byte[8];
            dataBuf.readBytes(b);
            res.setClassissNumber(new String(b));

        }else if(res.getInstruction()==0x02){
            // IC状态标志定义表第一个字节 转为8位的二进制字符串list
            List<String> byte1 = get8bitBinaryStringList(dataBuf.readByte());
            res.setOn(Byte.parseByte(byte1.get(7),2));
            res.setAcc(Byte.parseByte(byte1.get(6),2));
            res.setNotOnlineLock(Byte.parseByte(byte1.get(5),2));
            res.setWriteVdrId(Byte.parseByte(byte1.get(4),2));
            res.setStartupAuthentication(Byte.parseByte(byte1.get(3),2));
            res.setFunctionShutdown(Byte.parseByte(byte1.get(2),2));
            res.setIcHeartbeat(Byte.parseByte(byte1.get(0)+byte1.get(1),2));
            //IC心跳状态标志定义表第二个字节 转为8位的二进制字符串list
            List<String> byte2 = get8bitBinaryStringList(dataBuf.readByte());
            res.setLimpStatus(Byte.parseByte(byte2.get(4)+byte2.get(5)+byte2.get(6)+byte2.get(7),2));  //跛行状态标志
            res.setProcessMonitorSwitch(Byte.parseByte(byte2.get(3),2));    //过程监控总开关标志
            res.setNotOnlineMonitorSwitch(Byte.parseByte(byte2.get(2),2));  //不在线监控总开关标志
            res.setEecuLimpStatus(Byte.parseByte(byte2.get(0)+byte2.get(1),2));//EECU实际跛行状态
            //IC心跳状态标志定义表第三个字节 转为8位的二进制字符串list
            List<String> byte3 = get8bitBinaryStringList(dataBuf.readByte());
           //0-无效，1-库内监管,2-销贷监管
            res.setVehicleBusiness(Byte.parseByte(byte3.get(4)+byte3.get(5)+byte3.get(6)+byte3.get(7),2));
            res.setIcFault(Byte.parseByte(byte3.get(3),2));
            res.setVdrEecuMessage(Byte.parseByte(byte3.get(2),2));
            res.setLimp(Byte.parseByte(byte3.get(1),2));
            res.setMeterBusiness(Byte.parseByte(byte3.get(0),2));
            //IC心跳状态标志定义表第四个字节
            dataBuf.readByte();

            //IC 存储VDR_ ID(7 个字节)+
            byte a[]=new byte[7];
            dataBuf.readBytes(a);
            res.setVdrId(new String(a));

            /*VDR 自身存储底盘号(8 个字节)：以底盘号 G8020529 为例
            ，每位当成字符处理，则为：0x47 0x38 0x32 0x32 0x30 0x35 0x32 0x39*/
            byte b[]=new byte[8];
            dataBuf.readBytes(b);
            res.setClassissNumber(new String(b));
            //跳过4个字节
            byte c[]=new byte[4];
            dataBuf.readBytes(c);
        }else if(res.getInstruction()==0x03){
            //指令执行记录消息结构表
            res.setTerminalValidRecords((int)dataBuf.readByte());
            //表3-1不存在???????????????????????
            //指令内容(1)  +流水号1(2)   +指令状态(1)  +流水号2(2)  +时间戳(6)
            byte c[]=new byte[res.getTerminalValidRecords()*10-2];
            List<String> IER=new ArrayList<>();
            for(int i=0;i<res.getTerminalValidRecords();i++){
                dataBuf.readBytes(c);
                IER.add(new String(c));
            }
            res.setInstructionExecutionRecord(IER);
        }else if(res.getInstruction()==0x04){
            byte a[]=new byte[5];
            dataBuf.readBytes(a);
            res.setManufacturerId( new String(a));        //注册信息:制造商ID
            byte b[]=new byte[20];
            dataBuf.readBytes(b);
            res.setTerminalModel(new String(b));         //注册信息，终端型号
            res.setMainVersionNumber(dataBuf.readByte());                                  //主版本号
            res.setSubversionNumber(dataBuf.readByte());                                  //子版本号
        }

        processEvent(res, taskId, CodecUtils.shortToHex(upMsg.msgId), Constants.INSTRUCTION_RESULT_QUERY_TOPIC, channelProcessor);
        logger.info("指令下发查询结果0xF103:  "+ res.toString());

        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);

        }catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    private void do_F106(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        final VnndF106ResMsg res = new VnndF106ResMsg();
        List<Item> ItemList = Lists.newArrayList();
        try {
            ByteBuf dataBuf = upMsg.dataBuf;
            res.setVid(upMsg.vid);
            dataBuf.skipBytes(1);
            res.setSerialNumber(String.valueOf(dataBuf.readShort()));    //设置流水号
            res.setParamNum((int)dataBuf.readByte());
            Item item;
            while(dataBuf.readableBytes()>=3){
                item = new Item();
                item.setId(dataBuf.readUnsignedByte());
                item.setLength(dataBuf.readUnsignedByte());
                if(item.getId()==0x14){
                    item.setValue(dataBuf.readShort());
                }else if(item.getId()==0x15){
                    item.setValue(dataBuf.readShort());
                }else if(item.getId()==0x16){
                    item.setValue(dataBuf.readByte());
                }else if(item.getId()==0x17){
                    item.setValue(dataBuf.readByte());
                }
                ItemList.add(item);
            }
            res.setPlist(ItemList);

            processEvent(res, taskId, CodecUtils.shortToHex(upMsg.msgId), Constants.INSTRUCTION_RESULT_QUERY_TOPIC, channelProcessor);
            //暂时没什么业务逻辑，收到南斗定义的超时上报就直接回复通用应答
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
            logger.info("参数设置结果上报0xF106:  "+ res.toString());
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    private void do_F102(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.debug("接收到超时上报:{}", upMsg);
        //暂时没什么业务逻辑，收到南斗定义的超时上报就直接回复通用应答
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    }

    private void do_F001_nd(final Req_F001_nd dnReq, String taskId, ChannelProcessor channelProcessor){

        String str = instruction2String(dnReq.getInstruction());
        if(dnReq.getInstruction() != 0x09){
            str += business2String(dnReq.getBusiness());
        }else {
            str += limpStatus2String(dnReq.getLimpStatus());
        }

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        final Res_0001 resg = new Res_0001();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehicl(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }

        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
         logger.warn("查询车辆为空或VID不一致:{}\n{}");
            return;
            }

        rcp.setVid(dnReq.getVid());
        rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.debug("接收到下行请求:{}", dnReq);
        final ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF001;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getInstruction());
            if(dnReq.getInstruction() != 0x09){
                req.dataBuf.writeByte(dnReq.getBusiness());
                req.dataBuf.writeByte(0);
            }else {
                req.dataBuf.writeByte(dnReq.getTsc1()==null?0xFF:dnReq.getTsc1());
                req.dataBuf.writeByte(dnReq.getLimpStatus());
                //目前只要命令字是0x09，这里就只有0x03一种情况，其他情况的下发什么值还没定义
                //后面南斗定义了其他情况的下发值后，这边在添加
                if(dnReq.getLimpStatus() == 0x03){
                    int speedOfRevolution = dnReq.getSpeedOfRevolution();
                    speedOfRevolution *= 8;
                    req.dataBuf.writeByte(speedOfRevolution&0x00FF);//转速 高字节
                    req.dataBuf.writeByte((speedOfRevolution>>>8)&0x00FF);//转速 低字节

/*                    req.dataBuf.writeByte(dnReq.getSpeedOfRevolution()&0x00FF);
                    req.dataBuf.writeByte((dnReq.getSpeedOfRevolution()&0xFF00)>>>8/8);*/
                    req.dataBuf.writeByte(dnReq.getTorque()+125);//扭矩
                }
                req.dataBuf.writeByte(0xFF);
                req.dataBuf.writeByte(0xFF);
            }
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("锁车设置成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("锁车设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }



//解跛命令特殊处理
    private void do_F001_nd_ByVId(final Req_F001_nd dnReq, String taskId, ChannelProcessor channelProcessor){

        String str = instruction2String(dnReq.getInstruction());
        if(dnReq.getInstruction() != 0x09){
            str += business2String(dnReq.getBusiness());
        }else {
            str += limpStatus2String(dnReq.getLimpStatus());
        }

        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        final Res_0001 resg = new Res_0001();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehiclByVid(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }

        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("查询车辆为空或VID不一致:{}\n{}");
            return;
        }

        rcp.setVid(dnReq.getVid());
        rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.debug("接收到下行请求:{}", dnReq);
        final ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF001;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getInstruction());
            if(dnReq.getInstruction() != 0x09){
                req.dataBuf.writeByte(dnReq.getBusiness());
                req.dataBuf.writeByte(0);
            }else {
                req.dataBuf.writeByte(dnReq.getTsc1()==null?0xFF:dnReq.getTsc1());
                req.dataBuf.writeByte(dnReq.getLimpStatus());
                //目前只要命令字是0x09，这里就只有0x03一种情况，其他情况的下发什么值还没定义
                //后面南斗定义了其他情况的下发值后，这边在添加
                if(dnReq.getLimpStatus() == 0x03){
                    int speedOfRevolution = dnReq.getSpeedOfRevolution();
                    speedOfRevolution *= 8;
                    req.dataBuf.writeByte(speedOfRevolution&0x00FF);//转速 高字节
                    req.dataBuf.writeByte((speedOfRevolution>>>8)&0x00FF);//转速 低字节

/*                    req.dataBuf.writeByte(dnReq.getSpeedOfRevolution()&0x00FF);
                    req.dataBuf.writeByte((dnReq.getSpeedOfRevolution()&0xFF00)>>>8/8);*/
                    req.dataBuf.writeByte(dnReq.getTorque()+125);//扭矩
                }
                req.dataBuf.writeByte(0xFF);
                req.dataBuf.writeByte(0xFF);
            }
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("锁车设置成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("锁车设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }


    private void do_F003_nd(final Req_F003_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setInstruction(dnReq.getInstruction());
        rcp.setStatus(0);

        final VnndF003ResMsg resg = new VnndF003ResMsg();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehicl(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
           return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("查询车辆为空或VID不一致");
            return;
        }
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF003ResMsg res = new VnndF003ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF003;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getInstruction());

            req.dataBuf.writeInt(0xFFFFFFFF);
            req.dataBuf.writeShort(0xFFFF);
            req.dataBuf.writeByte(0xFF);
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询锁车状态成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询锁车状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(res.getRc() == JtsResMsg.NE_RC_FAIL ? 0 : 1);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_STATUS_TOPIC, channelProcessor);
            }
        });
    }

    private void do_F006_nd(final Req_F006_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setStatus(0);

        final VnndF006ResMsg resg = new VnndF006ResMsg();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehicl(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("查询车辆为空或VID不一致");
            return;
        }
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF006ResMsg res = new VnndF006ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0xF006;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getParam().size());
            for(int i=0;i<dnReq.getParam().size();i++){
                req.dataBuf.writeByte(Byte.parseByte(dnReq.getParam().get(i).getId()));
                if(Byte.parseByte(dnReq.getParam().get(i).getId())==0x14){
                    req.dataBuf.writeByte(2);
                    req.dataBuf.writeShort(Short.parseShort(dnReq.getParam().get(i).getValue()));
                }else if(Byte.parseByte(dnReq.getParam().get(i).getId())==0x15){
                    req.dataBuf.writeByte(2);
                    req.dataBuf.writeShort(Short.parseShort(dnReq.getParam().get(i).getValue()));
                }else if(Byte.parseByte(dnReq.getParam().get(i).getId())==0x16){
                    req.dataBuf.writeByte(1);
                    req.dataBuf.writeByte(Byte.parseByte(dnReq.getParam().get(i).getValue()));
                }else if(Byte.parseByte(dnReq.getParam().get(i).getId())==0x17){
                    req.dataBuf.writeByte(1);
                    req.dataBuf.writeByte(Byte.parseByte(dnReq.getParam().get(i).getValue()));
                }
            }

        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询锁车状态成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_PARAM_SET_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询锁车状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(res.getRc() == JtsResMsg.NE_RC_FAIL ? 0 : 1);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_PARAM_SET_TOPIC, channelProcessor);
            }
        });
    }

    private void do_F007_nd(final Req_F007_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setStatus(0);

        final VnndF007ResMsg resg = new VnndF007ResMsg();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehicl(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("查询车辆为空或VID不一致");
            return;
        }
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF007ResMsg res = new VnndF007ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(Byte.parseByte(dnReq.getStatus()));
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询锁车状态成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询锁车状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(res.getRc() == JtsResMsg.NE_RC_FAIL ? 0 : 1);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_STATUS_TOPIC, channelProcessor);
            }
        });
    }

    private void do_F008_nd(final Req_F008_nd dnReq, String taskId, ChannelProcessor channelProcessor){
        VnndInstructionResMsg rcp = new VnndInstructionResMsg();
        rcp.setVid(dnReq.getVid());
        rcp.setStatus(0);

        final VnndF008ResMsg resg = new VnndF008ResMsg();
        CVVehicleDTO cvvehicledto= null;
        try {
            cvvehicledto = getVehicl(dnReq);
        } catch (Exception e) {
            logger.warn("查询车辆请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("查询车辆为空或VID不一致");
            return;
        }
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF003ResMsg res = new VnndF003ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(Byte.parseByte(dnReq.getSeed()));
        }catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询锁车状态成功", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询锁车状态失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(res.getRc() == JtsResMsg.NE_RC_FAIL ? 0 : 1);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_STATUS_TOPIC, channelProcessor);
            }
        });
    }

    /**
     * 根据根据lpn获取车辆信息
     * @param req
     * @return
     */
    private CVVehicleDTO getVehicl(Req_F001_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVin()!=null){
            sqlBuf.append(" where vin = '");
            sqlBuf.append(req.getVin());
        }else if(req.getLpn()!=null){
            sqlBuf.append(" where plateNo = '");
            sqlBuf.append(req.getLpn());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    //根据Vid获取车辆信息
    private CVVehicleDTO getVehiclByVid(Req_F001_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVid()!=null){
            sqlBuf.append(" where vid = '");
            sqlBuf.append(req.getVid());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    private CVVehicleDTO getVehicl(Req_F003_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVin()!=null){
            sqlBuf.append(" where vin = '");
            sqlBuf.append(req.getVin());
        }else if(req.getLpn()!=null){
            sqlBuf.append(" where plateNo = '");
            sqlBuf.append(req.getLpn());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    //参数设置
    private CVVehicleDTO getVehicl(Req_F006_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVin()!=null){
            sqlBuf.append(" where vin = '");
            sqlBuf.append(req.getVin());
        }else if(req.getLpn()!=null){
            sqlBuf.append(" where plateNo = '");
            sqlBuf.append(req.getLpn());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    //IP2开关状态
    private CVVehicleDTO getVehicl(Req_F007_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVin()!=null){
            sqlBuf.append(" where vin = '");
            sqlBuf.append(req.getVin());
        }else if(req.getLpn()!=null){
            sqlBuf.append(" where plateNo = '");
            sqlBuf.append(req.getLpn());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        System.err.println("aaaaaa"+sqlBuf.toString());
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    //SEED
    private CVVehicleDTO getVehicl(Req_F008_nd  req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if(req.getVin()!=null){
            sqlBuf.append(" where vin = '");
            sqlBuf.append(req.getVin());
        }else if(req.getLpn()!=null){
            sqlBuf.append(" where plateNo = '");
            sqlBuf.append(req.getLpn());
        }
        sqlBuf.append("' and isValid = '1' limit 1");
        System.err.println("aaaaaa"+sqlBuf.toString());
        return queryCVVehicleInfo(sqlBuf.toString());
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

    private List<String> get8bitBinaryStringList(byte b){
        List<String> stringList = new ArrayList<>();
        String s =  Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
        char[] charArr = s.toCharArray();
        for(char c:charArr){
            stringList.add(String.valueOf(c));
        }
        return stringList;
    }

    /*控制指令转换*/
    private String instruction2String(Integer instruction){
        String str = "";
        switch (instruction){
            case 0x01:
                str = "开启认证";
                break;
            case 0x02:
                str = "功能关闭";
                break;
            case 0x03:
                str = "预留";
                break;
            case 0x04:
                str = "开启过程监控";
                break;
            case 0x05:
                str = "关闭过程监控";
                break;
            case 0x06:
                str = "开启不在线监控";
                break;
            case 0x07:
                str = "关闭不在线监控";
                break;
            case 0x08:
                str = "预留";
                break;
            case 0x09:
                str = "跛行激活";
                break;
            case 0x0A:
                str = "跛行解除";
                break;
            case 0x0B:
                str = "预留";
                break;
            case 0x0C:
                str = "安全访问SEED";
                break;
            case 0x0D:
                str = "安全访问KEY";
                break;
            case 0x0E:
                str = "心跳连接";
                break;
            case 0x0F:
                str = "写入VDR_ID";
                break;
            case 0x11:
                str = "查询IC存储的VDR_ID";
                break;
            case 0x12:
                str = "查询IC存储的底盘号";
                break;
            case 0x13:
                str = "查询IC状态标志";
                break;
            case 0x14:
                str = "标定VDR";
                break;
        }
        return str;
    }

    private String business2String(Integer business){
        String str = "";
        switch (business){
            case 0x00:
                str = "无效值";
                break;
            case 0x01:
                str = "库内车监管";
                break;
            case 0x02:
                str = "消贷车监管";
                break;
        }
        return str;
    }

    private String limpStatus2String(Integer limpStatus){
        String str = "";
        switch (limpStatus){
            case 0x00:
                str = "取消控制";
                break;
            case 0x01:
                str = "转速控制";//目前也是只有这一项，后面增加了再来改动
                break;
            case 0x02:
                str = "扭矩控制";
                break;
            case 0x03:
                str = "转速/扭矩控制";
                break;

        }
        return str;
    }

}
