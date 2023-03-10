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
        if (dnReq instanceof Req_F001_nd) {//??????????????????
            do_F001_nd( (Req_F001_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F003_nd){//????????????????????????
            do_F003_nd((Req_F003_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F006_nd){ //????????????
            do_F006_nd((Req_F006_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F007_nd){ //IP2????????????
            do_F007_nd((Req_F007_nd) dnReq, taskId, channelProcessor);
        } else if(dnReq instanceof Req_F008_nd){ //SEED??????
            do_F008_nd((Req_F008_nd) dnReq, taskId, channelProcessor);
        }
        else {
            throw new RuntimeException("???????????????????????????: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        //ID?????????Java short???????????????????????????????????????????????????????????????????????????????????????
        if (upMsg.msgId == (short)0xF101) {//??????????????????????????????
            do_F101(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId == (short) 0xF002){//????????????
            do_F002(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId == (short) 0xF102){//????????????
            do_F102(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId ==(short) 0xF103){ //????????????????????????
            do_F103(upMsg, taskId, channelProcessor);
        } else if(upMsg.msgId ==(short) 0xF106){
            do_F106(upMsg, taskId, channelProcessor);
        }else {
            throw new UnsupportedProtocolException("??????????????????????????????msgId=" + upMsg.msgId);
        }
    }

    private void do_F101(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        final Req_F101_nd req = new Req_F101_nd();
        try{
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            req.setInstruction((int)reqBuf.readByte());
            reqBuf.skipBytes(3);//??????3???????????????
            req.setResult((int)reqBuf.readShort());//????????????????????????
            logger.debug("[{}]???????????????????????????:{}", upMsg.sim, req);

            VnndInstructionResMsg rcp = new VnndInstructionResMsg();
            rcp.setInstruction(req.getInstruction());
            rcp.setVid(req.getVid());
            rcp.setStatus(0 == req.getResult() ? 1 : 0);

            processEvent(rcp, CodecUtils.shortToHex(upMsg.msgId), taskId, Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
            //???????????????redis
//            Map  map=new HashMap();
//            map.put("vid",req.getVid());
//            map.put("msgId","jts.F001.nd");
//            map.put("instruction",req.getInstruction());
//            map.put("status",req.getResult());
//            map.put("datetime",Calendar.getInstance().getTimeInMillis());
//            RedisUtils.commandStatus2Redis(map);
        }catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
    }

    private void do_F002(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.info("?????????????????????:{}", upMsg);
        final Req_F002_nd req = new Req_F002_nd();
        try{
            logger.info("??????vid:{}", upMsg.vid);
            req.setVid(upMsg.vid);
            ByteBuf reqBuf = upMsg.dataBuf;
            byte byte1 = reqBuf.readByte();
            String byte1String = ByteBufUtil.byte2String(byte1);
            logger.info("1 ?????????????????????:byte1 = {}, byte1String = {}", byte1, byte1String);
            req.setOnBitchSign(Integer.parseInt(byte1String.substring(7), 2));
            req.setAccBitchSign(Integer.parseInt(byte1String.substring(6, 7), 2));
            req.setOffLineLockVehicleSign(Integer.parseInt(byte1String.substring(5, 6), 2));
            req.setWriteVDR_IDSign(Integer.parseInt(byte1String.substring(4, 5), 2));
            req.setOpenAuthSign(Integer.parseInt(byte1String.substring(3, 4), 2));
            req.setCloseAuthSign(Integer.parseInt(byte1String.substring(2, 3), 2));
            req.setICHeartBeatSign(Integer.parseInt(byte1String.substring(0, 2), 2));
            byte byte2 = reqBuf.readByte();
            String byte2String = ByteBufUtil.byte2String(byte2);
            logger.info("2 ?????????????????????:byte2 = {}, byte2String = {}", byte2, byte2String);
            req.setLimpStatusSign(Integer.parseInt(byte2String.substring(4), 2));
            req.setProcessMonitorSign(Integer.parseInt(byte2String.substring(3, 4), 2));
            req.setOfflineMonitorSign(Integer.parseInt(byte2String.substring(2, 3), 2));
            req.setRealLimpStatusSign(Integer.parseInt(byte2String.substring(0, 2), 2));
            byte byte3 = reqBuf.readByte();
            String byte3String = ByteBufUtil.byte2String(byte2);
            logger.info("3 ?????????????????????:byte3 = {}, byte3String = {}", byte3, byte3String);
            req.setVehicleBusinessStatus(Integer.parseInt(byte3String.substring(4), 2));
            req.setICBreakSign(Integer.parseInt(byte3String.substring(3, 4), 2));
            req.setEecuSign(Integer.parseInt(byte3String.substring(2, 3), 2));
            req.setLimpCommand(Integer.parseInt(byte3String.substring(1, 2), 2));
            req.setMeterBreakSign(Integer.parseInt(byte3String.substring(0, 1), 2));
            logger.debug("[{}]???????????????????????????:{}", upMsg.sim, req);

            //??????5????????????????????????
            reqBuf.skipBytes(5);
            Date time=readTime(reqBuf.readBytes(6));
            JSONObject json=new JSONObject();
            json.put("time",time);
            json.put("vid",req.getVid());
            json.put("onBitchSign",req.getOnBitchSign());
            //??????redis??? ??????kafka
            updateONStatus2Redis(json,channelProcessor);
            VnndInstructionResMsg res = new VnndInstructionResMsg();
            res.setVid(req.getVid());
            res.setStatus(1);

            if (4 == req.getLimpStatusSign()) {//???????????????????????????????????????????????????????????????
                Req_F001_nd offLimpReq = new Req_F001_nd();
                offLimpReq.setInstruction(10);
                offLimpReq.setLimpStatus(1);
                offLimpReq.setBusiness(req.getVehicleBusinessStatus());
                offLimpReq.setVid(req.getVid());

                do_F001_nd_ByVId(offLimpReq, taskId, channelProcessor);
                logger.info("????????????????????????????????????");
            } else {//???????????????????????????
                if (2 == req.getLimpStatusSign() || 6 == req.getLimpStatusSign() || 8 == req.getLimpStatusSign()) {
                    //??????????????????
                    res.setInstruction(0x09);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                } else if (1 == req.getOfflineMonitorSign()) {
                    //???????????????????????????
                    res.setInstruction(0x06);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                } else if (1 == req.getProcessMonitorSign()) {
                    //????????????????????????
                    res.setInstruction(0x04);
                    processEvent(res, taskId, req.id(), Constants.INSTRUCTION_RESULT_TOPIC, channelProcessor);
                }
            }

            logger.debug("?????????????????????" );
            sendCenterGeneralRes(upMsg, (byte)0xDF);
        }catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    //F103???????????????
    private void do_F103(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor){
        final VnndF103ResMsg res = new VnndF103ResMsg();
        try{
        ByteBuf dataBuf = upMsg.dataBuf;
        res.setVid(upMsg.vid);
      /*  dataBuf.readBytes(2);*/   //????????????????????????0??????????????????????????????
        res.setSerialNumber(String.valueOf(dataBuf.readShort()));    //???????????????
        res.setInstruction((int)dataBuf.readByte());
        res.setRespTime(readTime(dataBuf.readBytes(6)));
        res.setRc(dataBuf.readByte());
        if(res.getInstruction()==0x01){
            // VDR?????????????????????????????????????????? ??????8????????????????????????list
            List<String> byte1 = get8bitBinaryStringList(dataBuf.readByte());
            res.setOn(Byte.parseByte(byte1.get(7),2));
            res.setAcc(Byte.parseByte(byte1.get(6),2));
            res.setNotOnlineLock(Byte.parseByte(byte1.get(5),2));
            res.setWriteVdrId(Byte.parseByte(byte1.get(4),2));
            res.setStartupAuthentication(Byte.parseByte(byte1.get(3),2));
            res.setFunctionShutdown(Byte.parseByte(byte1.get(2),2));
            res.setIcHeartbeat(Byte.parseByte(byte1.get(0)+byte1.get(1),2));
            //VDR?????????????????????????????????????????? ??????8????????????????????????list
            List<String> byte2 = get8bitBinaryStringList(dataBuf.readByte());
            res.setLimpStatus(Byte.parseByte(byte2.get(4)+byte2.get(5)+byte2.get(6)+byte2.get(7),2));  //??????????????????
            res.setProcessMonitorSwitch(Byte.parseByte(byte2.get(3),2));    //???????????????????????????
            res.setNotOnlineMonitorSwitch(Byte.parseByte(byte2.get(2),2));  //??????????????????????????????
            res.setEecuLimpStatus(Byte.parseByte(byte2.get(0)+byte2.get(1),2));//EECU??????????????????
            //VDR?????????????????????????????????????????? ??????8????????????????????????list
            List<String> byte3 = get8bitBinaryStringList(dataBuf.readByte());
            res.setVehicleBusiness(Byte.parseByte(byte3.get(4)+byte3.get(5)+byte3.get(6)+byte3.get(7),2));
            res.setIcFault(Byte.parseByte(byte3.get(3),2));
            res.setVdrEecuMessage(Byte.parseByte(byte3.get(2),2));
            res.setLimp(Byte.parseByte(byte3.get(1),2));
            res.setMeterBusiness(Byte.parseByte(byte3.get(0),2));
            //VDR??????????????????????????????????????????
            List<String> byte4 = get8bitBinaryStringList(dataBuf.readByte());
            res.setCanStatus(Byte.parseByte(byte4.get(6)+byte4.get(7),2));
            //BIT 3 - BIT 8 ??????

            /*??? 5-11?????????*/
            //VDR ???????????? ID(7 ?????????)
            byte a[]=new byte[7];
            dataBuf.readBytes(a);
            res.setVdrId(new String(a));

            /*??? 12-19?????????*/
            /*VDR ?????????????????????(8 ?????????)??????????????? G8020529 ??????
            ???????????????????????????????????????0x47 0x38 0x32 0x32 0x30 0x35 0x32 0x39*/
            byte b[]=new byte[8];
            dataBuf.readBytes(b);
            res.setClassissNumber(new String(b));

        }else if(res.getInstruction()==0x02){
            // IC???????????????????????????????????? ??????8????????????????????????list
            List<String> byte1 = get8bitBinaryStringList(dataBuf.readByte());
            res.setOn(Byte.parseByte(byte1.get(7),2));
            res.setAcc(Byte.parseByte(byte1.get(6),2));
            res.setNotOnlineLock(Byte.parseByte(byte1.get(5),2));
            res.setWriteVdrId(Byte.parseByte(byte1.get(4),2));
            res.setStartupAuthentication(Byte.parseByte(byte1.get(3),2));
            res.setFunctionShutdown(Byte.parseByte(byte1.get(2),2));
            res.setIcHeartbeat(Byte.parseByte(byte1.get(0)+byte1.get(1),2));
            //IC?????????????????????????????????????????? ??????8????????????????????????list
            List<String> byte2 = get8bitBinaryStringList(dataBuf.readByte());
            res.setLimpStatus(Byte.parseByte(byte2.get(4)+byte2.get(5)+byte2.get(6)+byte2.get(7),2));  //??????????????????
            res.setProcessMonitorSwitch(Byte.parseByte(byte2.get(3),2));    //???????????????????????????
            res.setNotOnlineMonitorSwitch(Byte.parseByte(byte2.get(2),2));  //??????????????????????????????
            res.setEecuLimpStatus(Byte.parseByte(byte2.get(0)+byte2.get(1),2));//EECU??????????????????
            //IC?????????????????????????????????????????? ??????8????????????????????????list
            List<String> byte3 = get8bitBinaryStringList(dataBuf.readByte());
           //0-?????????1-????????????,2-????????????
            res.setVehicleBusiness(Byte.parseByte(byte3.get(4)+byte3.get(5)+byte3.get(6)+byte3.get(7),2));
            res.setIcFault(Byte.parseByte(byte3.get(3),2));
            res.setVdrEecuMessage(Byte.parseByte(byte3.get(2),2));
            res.setLimp(Byte.parseByte(byte3.get(1),2));
            res.setMeterBusiness(Byte.parseByte(byte3.get(0),2));
            //IC??????????????????????????????????????????
            dataBuf.readByte();

            //IC ??????VDR_ ID(7 ?????????)+
            byte a[]=new byte[7];
            dataBuf.readBytes(a);
            res.setVdrId(new String(a));

            /*VDR ?????????????????????(8 ?????????)??????????????? G8020529 ??????
            ???????????????????????????????????????0x47 0x38 0x32 0x32 0x30 0x35 0x32 0x39*/
            byte b[]=new byte[8];
            dataBuf.readBytes(b);
            res.setClassissNumber(new String(b));
            //??????4?????????
            byte c[]=new byte[4];
            dataBuf.readBytes(c);
        }else if(res.getInstruction()==0x03){
            //?????????????????????????????????
            res.setTerminalValidRecords((int)dataBuf.readByte());
            //???3-1????????????????????????????????
            //????????????(1)  +?????????1(2)   +????????????(1)  +?????????2(2)  +?????????(6)
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
            res.setManufacturerId( new String(a));        //????????????:?????????ID
            byte b[]=new byte[20];
            dataBuf.readBytes(b);
            res.setTerminalModel(new String(b));         //???????????????????????????
            res.setMainVersionNumber(dataBuf.readByte());                                  //????????????
            res.setSubversionNumber(dataBuf.readByte());                                  //????????????
        }

        processEvent(res, taskId, CodecUtils.shortToHex(upMsg.msgId), Constants.INSTRUCTION_RESULT_QUERY_TOPIC, channelProcessor);
        logger.info("????????????????????????0xF103:  "+ res.toString());

        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);

        }catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
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
            res.setSerialNumber(String.valueOf(dataBuf.readShort()));    //???????????????
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
            //??????????????????????????????????????????????????????????????????????????????????????????
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
            logger.info("????????????????????????0xF106:  "+ res.toString());
        } catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
    }

    private void do_F102(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        logger.debug("?????????????????????:{}", upMsg);
        //??????????????????????????????????????????????????????????????????????????????????????????
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }

        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
         logger.warn("?????????????????????VID?????????:{}\n{}");
            return;
            }

        rcp.setVid(dnReq.getVid());
        rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.debug("?????????????????????:{}", dnReq);
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
                //????????????????????????0x09??????????????????0x03?????????????????????????????????????????????????????????
                //??????????????????????????????????????????????????????????????????
                if(dnReq.getLimpStatus() == 0x03){
                    int speedOfRevolution = dnReq.getSpeedOfRevolution();
                    speedOfRevolution *= 8;
                    req.dataBuf.writeByte(speedOfRevolution&0x00FF);//?????? ?????????
                    req.dataBuf.writeByte((speedOfRevolution>>>8)&0x00FF);//?????? ?????????

/*                    req.dataBuf.writeByte(dnReq.getSpeedOfRevolution()&0x00FF);
                    req.dataBuf.writeByte((dnReq.getSpeedOfRevolution()&0xFF00)>>>8/8);*/
                    req.dataBuf.writeByte(dnReq.getTorque()+125);//??????
                }
                req.dataBuf.writeByte(0xFF);
                req.dataBuf.writeByte(0xFF);
            }
        }catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("??????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("??????????????????", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }
        });
    }



//????????????????????????
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }

        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("?????????????????????VID?????????:{}\n{}");
            return;
        }

        rcp.setVid(dnReq.getVid());
        rcp.setInstruction(dnReq.getInstruction());
        rcp.setInstructionDesc(str);
        rcp.setStatus(0);

        logger.debug("?????????????????????:{}", dnReq);
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
                //????????????????????????0x09??????????????????0x03?????????????????????????????????????????????????????????
                //??????????????????????????????????????????????????????????????????
                if(dnReq.getLimpStatus() == 0x03){
                    int speedOfRevolution = dnReq.getSpeedOfRevolution();
                    speedOfRevolution *= 8;
                    req.dataBuf.writeByte(speedOfRevolution&0x00FF);//?????? ?????????
                    req.dataBuf.writeByte((speedOfRevolution>>>8)&0x00FF);//?????? ?????????

/*                    req.dataBuf.writeByte(dnReq.getSpeedOfRevolution()&0x00FF);
                    req.dataBuf.writeByte((dnReq.getSpeedOfRevolution()&0xFF00)>>>8/8);*/
                    req.dataBuf.writeByte(dnReq.getTorque()+125);//??????
                }
                req.dataBuf.writeByte(0xFF);
                req.dataBuf.writeByte(0xFF);
            }
        }catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("??????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("??????????????????", t);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
           return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("?????????????????????VID?????????");
            return;
        }
        logger.debug("?????????????????????:{}", dnReq);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("?????????????????????VID?????????");
            return;
        }
        logger.debug("?????????????????????:{}", dnReq);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_PARAM_SET_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("?????????????????????VID?????????");
            return;
        }
        logger.debug("?????????????????????:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF007ResMsg res = new VnndF007ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(Byte.parseByte(dnReq.getStatus()));
        }catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
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
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            resg.setRc(JtsResMsg.RC_FAIL);
            return;
        }
        if(cvvehicledto==null || !dnReq.getVid().equals(cvvehicledto.getVid())){
            logger.warn("?????????????????????VID?????????");
            return;
        }
        logger.debug("?????????????????????:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final VnndF003ResMsg res = new VnndF003ResMsg();
        try {
            req.sim=cvvehicledto.getSim();
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(Byte.parseByte(dnReq.getSeed()));
        }catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????", result);
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                rcp.setStatus(res.getRc() == JtsResMsg.RC_OK ? 1 : 0);
                processEvent(rcp, dnReq.id(), taskId, Constants.INSTRUCTION_SEND_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                rcp.setStatus(res.getRc() == JtsResMsg.NE_RC_FAIL ? 0 : 1);
                processEvent(rcp, dnReq.id(), taskId, Constants.LOCKVEHICLE_STATUS_TOPIC, channelProcessor);
            }
        });
    }

    /**
     * ????????????lpn??????????????????
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

    //??????Vid??????????????????
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

    //????????????
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

    //IP2????????????
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
     * ??????????????????
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
            logger.error("??????geode??????", e);
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

    /*??????????????????*/
    private String instruction2String(Integer instruction){
        String str = "";
        switch (instruction){
            case 0x01:
                str = "????????????";
                break;
            case 0x02:
                str = "????????????";
                break;
            case 0x03:
                str = "??????";
                break;
            case 0x04:
                str = "??????????????????";
                break;
            case 0x05:
                str = "??????????????????";
                break;
            case 0x06:
                str = "?????????????????????";
                break;
            case 0x07:
                str = "?????????????????????";
                break;
            case 0x08:
                str = "??????";
                break;
            case 0x09:
                str = "????????????";
                break;
            case 0x0A:
                str = "????????????";
                break;
            case 0x0B:
                str = "??????";
                break;
            case 0x0C:
                str = "????????????SEED";
                break;
            case 0x0D:
                str = "????????????KEY";
                break;
            case 0x0E:
                str = "????????????";
                break;
            case 0x0F:
                str = "??????VDR_ID";
                break;
            case 0x11:
                str = "??????IC?????????VDR_ID";
                break;
            case 0x12:
                str = "??????IC??????????????????";
                break;
            case 0x13:
                str = "??????IC????????????";
                break;
            case 0x14:
                str = "??????VDR";
                break;
        }
        return str;
    }

    private String business2String(Integer business){
        String str = "";
        switch (business){
            case 0x00:
                str = "?????????";
                break;
            case 0x01:
                str = "???????????????";
                break;
            case 0x02:
                str = "???????????????";
                break;
        }
        return str;
    }

    private String limpStatus2String(Integer limpStatus){
        String str = "";
        switch (limpStatus){
            case 0x00:
                str = "????????????";
                break;
            case 0x01:
                str = "????????????";//?????????????????????????????????????????????????????????
                break;
            case 0x02:
                str = "????????????";
                break;
            case 0x03:
                str = "??????/????????????";
                break;

        }
        return str;
    }

}
