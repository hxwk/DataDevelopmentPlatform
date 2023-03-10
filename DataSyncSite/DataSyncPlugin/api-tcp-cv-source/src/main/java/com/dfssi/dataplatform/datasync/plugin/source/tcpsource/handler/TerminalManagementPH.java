package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.utils.EncodeUtil;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.bean.Vehicle;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.cache.CacheEntities;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp.TcpConnection;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp.TcpConnectionManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
import com.google.common.base.Throwables;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils.hex2Bytes;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.*;


public class TerminalManagementPH extends BaseProtoHandler {

    @Override
    public void setup() {

    }

    private static final Logger logger = LoggerFactory.getLogger(TerminalManagementPH.class);

    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);
    //?????????8103?????????ID???????????????
    String road8103="1#4,2#4,3#4,4#4,5#4,6#4,7#4,16#1,17#1,18#1,19#1,20#1,21#1,22#1,23#1,24#4,25#4,26#1,27#4,28#4,29#1,32#4,33#4,34#4,39#4,40#4,41#4,42#4,43#4,44#4,45#4,46#4,47#4,48#4,49#3,64#1,65#1,66#1,67#1,68#1,69#4,70#4,71#4,72#1,73#1,80#4,81#4,82#4,83#4,84#4,85#4,86#4,87#4,88#4,89#4,90#4,91#3,92#3,93#3,94#3,100#4,101#4,112#4,113#4,114#4,115#4,116#4,128#4,129#3,130#3,131#1,132#2,144#2,145#2,146#2,147#4,148#2,149#4,256#4,257#3,258#4,259#3,117#5,118#5,119#5,121#2,61446#5,272#5,273#5,274#5,275#5,276#5,277#5,278#5,279#5,280#5,281#5,282#5,283#5,284#5";


    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8103) {//??????????????????
            do_8103((Req_8103) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8104) {//??????????????????
            do_8104((Req_8104) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8105) {//????????????
            do_8105((Req_8105) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8106) {//???????????????????????????
            do_8106((Req_8106) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_DisconnectVehiceFromTas) {//?????????????????????????????????
            do_disconnectVehiceFromTas((Req_DisconnectVehiceFromTas) dnReq);
        } else if (dnReq instanceof Req_8106_nd) {//?????????????????????  ??????IP????????????
            do_8106_nd((Req_8106_nd) dnReq, taskId, channelProcessor);
        } else {
            throw new RuntimeException("???????????????????????????: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        if (upMsg.msgId == 0x0002) {//????????????
            do_0002(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0100) {//????????????
            do_0100(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0102) {//????????????
            do_0102(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0104) {//????????????????????????
        //do_0102(upMsg, taskId, channelProcessor);
            logger.info("???????????????????????????????????????????????????:{}", upMsg.toString());
        } else if (upMsg.msgId == 0x0003) {//????????????
            do_0003(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("??????????????????????????????msgId=" + upMsg.msgId);
        }
    }

    /**
     * ????????????
     *
     * @param upMsg
     * @return
     */
    private void do_0002(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
//        updateVehicleStatus2Redis(upMsg.vid, taskId);
        updateVehicleStatus2Redis(upMsg, taskId, channelProcessor);
    }

    /**
     * ????????????
     *
     * @param upMsg
     * @return
     */
    private void do_0100(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        VnndLoginResMsg res = new VnndLoginResMsg();
        final Req_0100 req = new Req_0100();

        ProtoMsg protoMsg = new ProtoMsg();
        protoMsg.sim = upMsg.sim;
        protoMsg.msgId = ProtoConstants.TERMINAL_REGISTER_RES;
        protoMsg.vid = upMsg.sim;
        protoMsg.dataBuf = Unpooled.buffer(16);
        protoMsg.dataBuf.writeShort(upMsg.sn);

        //????????????????????????
        try {
            req.setVid(upMsg.sim); //??????????????????vid????????????sim???????????????????????????
            req.setSim(upMsg.sim);
            ByteBuf reqBuf = upMsg.dataBuf;
            req.setProvId(reqBuf.readShort());
            req.setCityId(reqBuf.readShort());
            req.setManufacturerId(readString(reqBuf, 5));
            req.setModel(readString(reqBuf, 20));
            req.setDeviceNo(readString(reqBuf, 7));
            req.setCol(reqBuf.readByte());//????????????
            req.setLpn(readString(reqBuf, reqBuf.readableBytes()));//?????????
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]???????????????????????????:{}", upMsg.sim, req);
            }
            try {
                Validate.notEmpty(req.getDeviceNo(), "????????????????????????");
                Validate.notNull(req.getSim(), "?????????sim????????????");
            } catch (Exception e) {
                res.setStatus(ProtoConstants.PROCESS_FAIL);
                res.setVid(req.getVid());
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
                logger.error(null, e);
                return;
            }

            try {  ///??????????????????
                CVVehicleDTO vehicle = getVehicleInfo(req);
                if (vehicle != null) {//????????????
                    final String vid = vehicle.getVid();
                    protoMsg.vid = vid;
                    res.setVid(vid);
                    String authCode = EncodeUtil.encode(String.valueOf(req.getSim()), Constants.ENCODE_SALT); //?????????
                    res.setStatus(ProtoConstants.PROCESS_SUCCESS);
                    protoMsg.dataBuf.writeByte(Res_8100.RC_OK);
                    writeString(protoMsg.dataBuf, authCode);
                    sendMessage(protoMsg);
                    processEvent(res, taskId, req.id(), Constants.LOGIN_TOPIC, channelProcessor);

                    Vehicle ve = new Vehicle();
                    ve.setId(vehicle.getVid());
                    CacheEntities.sim2VehicleMap.put(req.getSim(), ve);
                    logger.info("??????????????? authCode = {}", authCode);
                    logger.info("??????????????????:{}", req);
                    return;

                }
                vehicle = getVehiclByLpn(req);
                if (vehicle == null) {
                    logger.warn("??????????????????:????????????????????????,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_2);
                    sendMessage(protoMsg);
                    return;
                }
                if (!vehicle.getSim().equals(req.getSim())) {
                    logger.warn("??????????????????:??????????????????,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_1);
                    sendMessage(protoMsg);
                    return;
                }
                vehicle = getVehiclByDeviceNo(req.getDeviceNo());
                if (vehicle != null) {
                    if (!vehicle.getSim().equals(req.getSim())) {
                        logger.warn("??????????????????:??????????????????,{}", req);
                        protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_3);
                        sendMessage(protoMsg);
                        return;
                    }
                } else {
                    logger.warn("??????????????????:????????????????????????,?????????geode???did????????????,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_4);
                    sendMessage(protoMsg);
                    return;
                }
            } catch (Exception e) {
                res.setStatus(ProtoConstants.PROCESS_FAIL);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
                logger.warn("?????????????????????{}\n{}", req, Throwables.getStackTraceAsString(e));
            }

        } catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            res.setStatus(ProtoConstants.PROCESS_FAIL);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

    /**
     * ????????????(0102->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0102(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        final Req_0102 q = new Req_0102();

        VnndResMsg res = new VnndResMsg();


        //????????????????????????
        try {
            q.setVid(upMsg.sim); //??????????????????vid????????????sim???????????????????????????
            q.setSim(upMsg.sim);
            q.setLogFlag(Req_0102.LOG_IN);
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setAuthCode(readString(reqBuf, reqBuf.readableBytes()));
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]???????????????????????????:{}", upMsg.sim, q);
            }

            String vid = null != CacheEntities.sim2VehicleMap.get(q.getSim()) ? CacheEntities.sim2VehicleMap.get(q.getSim()).getId() : getVidBySimNo(q.getSim());

            res.setVid(vid);
            upMsg.vid = vid;

            if (!StringUtils.isEmpty(q.getAuthCode()) && q.getAuthCode().equals(EncodeUtil.encode(String.valueOf(q.getSim()), Constants.ENCODE_SALT))) {
                sendCenterGeneralRes(upMsg, JtsResMsg.RC_OK);
//                doTerminalLogin(upMsg.sim, q.getAuthCode());

                res.setStatus(ProtoConstants.PROCESS_SUCCESS);
            } else {
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
                res.setStatus(ProtoConstants.PROCESS_FAIL);
            }

            processEvent(res, taskId, q.id(), Constants.LOGIN_TOPIC, channelProcessor);

//            updateVehicleStatus2Redis(upMsg, taskId,channelProcessor);
        } catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            closeConnBySim(upMsg.sim);
            return;
        }

        return;
    }

//    public void doTerminalLogin(String sim, String authCode) {
//        ProtoMsg msg = new ProtoMsg();
//        msg.sim = sim;
//        msg.msgId = 0x0102;
//        msg.dataBuf = Unpooled.buffer();
////        ProtoUtil.writeString(msg.dataBuf, authCode);
//
//        sendMessage(msg);
//    }

    /**
     * ???????????????????????????(0003->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0003(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        Req_0003 q = new Req_0003();

        VnndResMsg res = new VnndResMsg();
        res.setVid(q.getVid());
        res.setStatus(ProtoConstants.PROCESS_FAIL);
        //????????????????????????
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]???????????????????????????:{}", upMsg.sim, q);
            }

            processEvent(res, taskId, Constants.LOGIN_TOPIC, q.id(), channelProcessor);

            // ???reids????????????????????????
            Jedis jedis = null;
            try {
                jedis = RedisPoolManager.getJedis();
                jedis.del(Constants.GK_VEHICLE_STATE + upMsg.vid);
            } catch (Exception e) {
                logger.warn("???Redis???????????????????????????", e);
            } finally {
                if (null != jedis) {
                    RedisPoolManager.returnResource(jedis);
                }
            }
        } catch (Exception e) {
            logger.warn("??????????????????:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        return;
    }

    /**
     * ?????????????????????????????????
     *
     * @param dnReq
     * @return
     */
    private void do_8103(final Req_8103 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.info("?????????????????????????????????:{}", dnReq);
        }

        ///?????????????????????????????????????????????????????????????????????????????????
        try {
            //Validate.notNull(dnReq.getVid(), "vid ??????????????????");
            Validate.notNull(dnReq.getParamItems(), "paramItems ??????????????????");
        } catch (Exception e) {
            logger.error(null, e);
            return;
        }
        List<Object> paramItemList =  Arrays.asList(road8103.split(","));
        if (null != paramItemList && paramItemList.size() > 0) {
            Constants.PARAM_ITEM_8103_MAP.clear();
            for (Object paramItem : paramItemList) {
                String[] paramPair = String.valueOf(paramItem).split("#");
                Constants.PARAM_ITEM_8103_MAP.put(paramPair[0], Integer.parseInt(paramPair[1]));
            }
        }
      /*  for (int i = 0; i < dnReq.getParamItems().size(); i++) {
            if (Constants.PARAM_ITEM_8103_MAP.containsKey(String.valueOf(dnReq.getParamItems().get(i).getParamId()))) {
                int paramId = Constants.PARAM_ITEM_8103_MAP.get(String.valueOf(dnReq.getParamItems().get(i).getParamId()));
                dnReq.getParamItems().get(i).setParamType((byte) paramId);
            } else {
                dnReq.getParamItems().get(i).setParamType((byte) 5);
            }
        }*/

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.sim = String.valueOf(dnReq.getSim());
            req.msgId = (short) 0x8103;
            req.dataBuf = Unpooled.buffer(32);
            int paramNum = dnReq.getParamItems().size();
            Validate.isTrue(paramNum <= 255, "???????????????????????????");

            req.dataBuf.writeByte(paramNum);
            for (ParamItem paramItem : dnReq.getParamItems()) {
                req.dataBuf.writeInt(paramItem.getParamId());
                switch (paramItem.getParamType()) {
                    case ParamItem.PT_UINT8:
                        req.dataBuf.writeByte(1);
                        req.dataBuf.writeByte(CodecUtils.parseUnsignedByte(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_UINT16:
                        req.dataBuf.writeByte(2);
                        req.dataBuf.writeShort(CodecUtils.parseUnsignedShort(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_UINT32:
                        req.dataBuf.writeByte(4);
                        req.dataBuf.writeInt((int) CodecUtils.parseUnsignedInt(paramItem.getParamVal()));
                        break;
                    case ParamItem.PT_STR:
                        writeU8String(req.dataBuf, paramItem.getParamVal());
                        break;
                    case ParamItem.PT_BYTES:
                        writeU8Bytes(req.dataBuf, hex2Bytes(paramItem.getParamVal()));
                        break;
                }
            }
            //???????????????dataBuf?????????????????????
        } catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????: " + result.vid);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
            }
        });

    }

    /**
     * ?????????????????????????????????
     *
     * @param dnReq
     * @return
     */
    private void do_8104(final Req_8104 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("?????????????????????:{}", dnReq);
        }
        logger.info("??????????????????:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0104 res = new Res_0104();
        try {
            req.vid = dnReq.getVid();
            req.sim = String.valueOf(dnReq.getSim());
            req.msgId = (short) 0x8104;
            req.dataBuf = Unpooled.EMPTY_BUFFER; //???????????????
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("?????????????????????????????????????????????: " + result);
                ByteBuf reqBuf = result.dataBuf;
                reqBuf.markReaderIndex();

                int count = reqBuf.readableBytes();
                byte[] out1 = new byte[count];
                reqBuf.readBytes(out1);
                reqBuf.resetReaderIndex();
                String byteString = ByteBufUtil.hexDump(out1).toUpperCase();
                logger.info("??????0104???????????????(???????????????):"+ byteString);

                List<Object> paramItemList =  Arrays.asList(road8103.split(","));
                if (null != paramItemList && paramItemList.size() > 0) {
                    Constants.PARAM_ITEM_8103_MAP.clear();
                    for (Object paramItem : paramItemList) {
                        String[] paramPair = String.valueOf(paramItem).split("#");
                        Constants.PARAM_ITEM_8103_MAP.put(paramPair[0], Integer.parseInt(paramPair[1]));
                    }
                }
                short flowNo = reqBuf.readShort();
                logger.info("??????0104?????????????????????"+flowNo);
                res.setFlowNo(flowNo);

                int parametersNum = reqBuf.readByte();
                logger.info("??????0104??????????????????"+parametersNum);
                List<ParamItem> paramItems = new ArrayList<>();
                for(int i=0 ;i<parametersNum;i++){
                    int parameterId = reqBuf.readInt();
                    logger.info("??????0104?????????ID???"+parameterId);
                    //??????id??????????????????
                    int parameterValueLength = reqBuf.readByte();
                    logger.info("??????0104?????????????????????"+parameterValueLength);
                    //????????????????????????????????????????????????????????????

                    int paramType = 5; //?????????5  ?????????????????????????????????ID-?????????????????????????????????
                    if (Constants.PARAM_ITEM_8103_MAP.containsKey(String.valueOf(parameterId))) {
                        paramType = Constants.PARAM_ITEM_8103_MAP.get(String.valueOf(parameterId));
                    }
                    String parameterValue = "";
                    switch (paramType) {
                        case 1:
                            byte[] out = new byte[parameterValueLength];
                            reqBuf.readBytes(out);
                            parameterValue= new String(out);
                            logger.info("??????0104???????????????"+parameterValue);
                            break;
                        case 2:
                            int byteValue = reqBuf.readByte();
                            parameterValue = String.valueOf(byteValue);
                            logger.info("??????0104???????????????"+parameterValue);
                            break;
                        case 3:
                            int shortValue = reqBuf.readShort();
                            parameterValue = String.valueOf(shortValue);
                            logger.info("??????0104???????????????"+parameterValue);
                            break;
                        case 4:
                            int intValue = reqBuf.readInt();
                            parameterValue = String.valueOf(intValue);
                            logger.info("??????0104???????????????"+parameterValue);
                            break;
                        case 5:
                            reqBuf.readBytes(parameterValueLength);
                            parameterValue = "???????????????";
                            logger.info("??????0104?????????????????????????????????????????????ID"+parameterId);
                            break;
                    }
                    ParamItem item = new ParamItem(parameterId,parameterValue);
                    paramItems.add(item);
                }
                res.setParamItems(paramItems);
                logger.info("?????????????????????"+JSON.toJSONString(res));
                processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.TERMINALPARAM_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
            }
        });
    }

    /**
     * ????????????
     *
     * @param dnReq
     * @return
     */
    private void do_8105(final Req_8105 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("?????????????????????:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

//        try {
//            req.vid = dnReq.getVid();
//            req.msgId = (short) 0x8105;
//            req.dataBuf = Unpooled.buffer(32);
//
//            byte commandWord = dnReq.getCommandWord();
//            req.dataBuf.writeByte(commandWord);
//
//            if (commandWord == 1 || commandWord == 2) {
//                writeString(req.dataBuf, dnReq.getCommandParam());
//            }
//        } catch (Exception e) {
//            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
//            res.setRc(JtsResMsg.RC_FAIL);
//            res.setVid(dnReq.getVid());
//            ctx.reply(res);
//            return;
//        }

    }

    private void do_8106_nd(final Req_8106_nd dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.debug("?????????????????????:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_8106_nd res = new Res_8106_nd();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8106;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(1);//????????????
            req.dataBuf.writeInt(0xF006);
            req.dataBuf.writeZero(16);
        } catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("????????????????????????: " + result.vid);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param dnReq
     * @return
     */
    private void do_8106(final Req_8106 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("?????????????????????:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0104 res = new Res_0104();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8106;
            req.dataBuf = Unpooled.buffer(32);
            int idNum = dnReq.getParamIds().length;
            req.dataBuf.writeByte(idNum);
            for (int paramId : dnReq.getParamIds()) {
                req.dataBuf.writeInt(paramId);
            }
        } catch (Exception e) {
            logger.warn("????????????????????????:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("?????????????????????????????????????????????: " + result);
                ByteBuf reqBuf = result.dataBuf;

                short flowNo = reqBuf.readUnsignedByte();
                logger.info("??????0104?????????????????????" + flowNo);
                res.setFlowNo(flowNo);

                int parametersNum = reqBuf.readInt();
                logger.info("??????0104??????????????????" + parametersNum);
                List<ParamItem> paramItems = new ArrayList<>();
                for (int i = 0; i < parametersNum; i++) {
                    int parameterId = reqBuf.readInt();
                    logger.info("??????0104?????????ID???" + parameterId);
                    //??????id??????????????????
                    int parameterValueLength = reqBuf.readByte();
                    logger.info("??????0104?????????????????????" + parameterValueLength);
                    byte[] out = new byte[parameterValueLength];
                    reqBuf.readBytes(out);
                    String parameterValue = new String(out);
                    logger.info("??????0104???????????????" + parameterValue);
                    ParamItem item = new ParamItem(parameterId, parameterValue);
                    paramItems.add(item);
                }
                res.setParamItems(paramItems);
                processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.TERMINALPARAM_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("????????????????????????", t);
            }
        });
    }

    /**
     * ??????????????????????????????
     *
     * @param dnReq
     */
    private void do_disconnectVehiceFromTas(final Req_DisconnectVehiceFromTas dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("?????????????????????????????????????????????:{}", dnReq);
        }
        try {
            TcpConnectionManager tcpConnectionManager = getTcpConnectionManager();
            TcpConnection vid2Connection = tcpConnectionManager.getConnectionByVid(dnReq.getVid());
            if (vid2Connection != null) {
                vid2Connection.close();
                logger.debug("??????????????????????????????????????????:{}", dnReq);
            } else {
                logger.debug("???????????????:{}", dnReq);
            }
        } catch (Exception e) {
            logger.debug("??????????????????????????????????????????:{}", e.getMessage());
        }
    }


    /**
     * ?????????sim???did?????????????????????sim???did???vin?????????????????????
     *
     * @param req
     */
    public CVVehicleDTO getVehicleInfo(Req_0100 req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        sqlBuf.append(" where sim = '");
        sqlBuf.append(req.getSim());
        sqlBuf.append("' and did = '");
        sqlBuf.append(req.getDeviceNo());
        if (req.getCol() == 0) {//??????????????? 0 ??????????????? VIN
            sqlBuf.append("' and vin = '");
        } else {
            sqlBuf.append("' and plateNo = '");
        }
        sqlBuf.append(req.getLpn());
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    /**
     * ??????sim?????????vid
     *
     * @param simNo
     * @return
     */
    private String getVidBySimNo(String simNo) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        sqlBuf.append(" where sim = '");
        sqlBuf.append(simNo);
        sqlBuf.append("' and isValid = '1' limit 1");
        CVVehicleDTO vehicle = queryCVVehicleInfo(sqlBuf.toString());
        return vehicle == null ? null : vehicle.getVid();
    }

    /**
     * ??????deviceNo??????????????????
     *
     * @param deviceNo
     * @return
     */
    private CVVehicleDTO getVehiclByDeviceNo(String deviceNo) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        sqlBuf.append(" where did = '");
        sqlBuf.append(deviceNo);
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    /**
     * ????????????lpn??????????????????
     *
     * @param req
     * @return
     */
    private CVVehicleDTO getVehiclByLpn(Req_0100 req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if (req.getCol() == 0) {//??????????????? 0 ??????????????? VIN
            sqlBuf.append(" where vin = '");
        } else {
            sqlBuf.append(" where plateNo = '");
        }
        sqlBuf.append(req.getLpn());
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }


}
