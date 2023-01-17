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
    //初始化8103的参数ID和参数类型
    String road8103="1#4,2#4,3#4,4#4,5#4,6#4,7#4,16#1,17#1,18#1,19#1,20#1,21#1,22#1,23#1,24#4,25#4,26#1,27#4,28#4,29#1,32#4,33#4,34#4,39#4,40#4,41#4,42#4,43#4,44#4,45#4,46#4,47#4,48#4,49#3,64#1,65#1,66#1,67#1,68#1,69#4,70#4,71#4,72#1,73#1,80#4,81#4,82#4,83#4,84#4,85#4,86#4,87#4,88#4,89#4,90#4,91#3,92#3,93#3,94#3,100#4,101#4,112#4,113#4,114#4,115#4,116#4,128#4,129#3,130#3,131#1,132#2,144#2,145#2,146#2,147#4,148#2,149#4,256#4,257#3,258#4,259#3,117#5,118#5,119#5,121#2,61446#5,272#5,273#5,274#5,275#5,276#5,277#5,278#5,279#5,280#5,281#5,282#5,283#5,284#5";


    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8103) {//设置终端参数
            do_8103((Req_8103) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8104) {//查询终端参数
            do_8104((Req_8104) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8105) {//终端控制
            do_8105((Req_8105) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_8106) {//查询终端指定的参数
            do_8106((Req_8106) dnReq, taskId, channelProcessor);
        } else if (dnReq instanceof Req_DisconnectVehiceFromTas) {//断开终端与前置机的连接
            do_disconnectVehiceFromTas((Req_DisconnectVehiceFromTas) dnReq);
        } else if (dnReq instanceof Req_8106_nd) {//南斗自定义查询  查询IP锁定状态
            do_8106_nd((Req_8106_nd) dnReq, taskId, channelProcessor);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        if (upMsg.msgId == 0x0002) {//终端心跳
            do_0002(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0100) {//终端注册
            do_0100(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0102) {//终端鉴权
            do_0102(upMsg, taskId, channelProcessor);
        } else if (upMsg.msgId == 0x0104) {//查询终端参数应答
        //do_0102(upMsg, taskId, channelProcessor);
            logger.info("接收到查询终端参数应答上行请求消息:{}", upMsg.toString());
        } else if (upMsg.msgId == 0x0003) {//终端注销
            do_0003(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    /**
     * 终端心跳
     *
     * @param upMsg
     * @return
     */
    private void do_0002(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
//        updateVehicleStatus2Redis(upMsg.vid, taskId);
        updateVehicleStatus2Redis(upMsg, taskId, channelProcessor);
    }

    /**
     * 终端注册
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

        //解析上行请求协议
        try {
            req.setVid(upMsg.sim); //注册时还没有vid，临时用sim代替，便于负载均衡
            req.setSim(upMsg.sim);
            ByteBuf reqBuf = upMsg.dataBuf;
            req.setProvId(reqBuf.readShort());
            req.setCityId(reqBuf.readShort());
            req.setManufacturerId(readString(reqBuf, 5));
            req.setModel(readString(reqBuf, 20));
            req.setDeviceNo(readString(reqBuf, 7));
            req.setCol(reqBuf.readByte());//车牌颜色
            req.setLpn(readString(reqBuf, reqBuf.readableBytes()));//车牌号
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, req);
            }
            try {
                Validate.notEmpty(req.getDeviceNo(), "设备编号不能为空");
                Validate.notNull(req.getSim(), "手机号sim不能为空");
            } catch (Exception e) {
                res.setStatus(ProtoConstants.PROCESS_FAIL);
                res.setVid(req.getVid());
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
                logger.error(null, e);
                return;
            }

            try {  ///处理业务逻辑
                CVVehicleDTO vehicle = getVehicleInfo(req);
                if (vehicle != null) {//注册成功
                    final String vid = vehicle.getVid();
                    protoMsg.vid = vid;
                    res.setVid(vid);
                    String authCode = EncodeUtil.encode(String.valueOf(req.getSim()), Constants.ENCODE_SALT); //鉴权码
                    res.setStatus(ProtoConstants.PROCESS_SUCCESS);
                    protoMsg.dataBuf.writeByte(Res_8100.RC_OK);
                    writeString(protoMsg.dataBuf, authCode);
                    sendMessage(protoMsg);
                    processEvent(res, taskId, req.id(), Constants.LOGIN_TOPIC, channelProcessor);

                    Vehicle ve = new Vehicle();
                    ve.setId(vehicle.getVid());
                    CacheEntities.sim2VehicleMap.put(req.getSim(), ve);
                    logger.info("生成鉴权码 authCode = {}", authCode);
                    logger.info("终端注册成功:{}", req);
                    return;

                }
                vehicle = getVehiclByLpn(req);
                if (vehicle == null) {
                    logger.warn("终端注册失败:数据库中无该车辆,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_2);
                    sendMessage(protoMsg);
                    return;
                }
                if (!vehicle.getSim().equals(req.getSim())) {
                    logger.warn("终端注册失败:车辆已被注册,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_1);
                    sendMessage(protoMsg);
                    return;
                }
                vehicle = getVehiclByDeviceNo(req.getDeviceNo());
                if (vehicle != null) {
                    if (!vehicle.getSim().equals(req.getSim())) {
                        logger.warn("终端注册失败:终端已被注册,{}", req);
                        protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_3);
                        sendMessage(protoMsg);
                        return;
                    }
                } else {
                    logger.warn("终端注册失败:数据库中无该终端,请检查geode中did是否正确,{}", req);
                    protoMsg.dataBuf.writeByte(ProtoConstants.LOGIN_FAIL_4);
                    sendMessage(protoMsg);
                    return;
                }
            } catch (Exception e) {
                res.setStatus(ProtoConstants.PROCESS_FAIL);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
                logger.warn("消息处理异常：{}\n{}", req, Throwables.getStackTraceAsString(e));
            }

        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            res.setStatus(ProtoConstants.PROCESS_FAIL);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

    /**
     * 终端鉴权(0102->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0102(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {

        final Req_0102 q = new Req_0102();

        VnndResMsg res = new VnndResMsg();


        //解析上行请求协议
        try {
            q.setVid(upMsg.sim); //鉴权时还没有vid，临时用sim代替，便于负载均衡
            q.setSim(upMsg.sim);
            q.setLogFlag(Req_0102.LOG_IN);
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setAuthCode(readString(reqBuf, reqBuf.readableBytes()));
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
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
            logger.warn("协议解析失败:" + upMsg, e);
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
     * 上行协议：终端注销(0003->8001)
     *
     * @param upMsg
     * @return
     */
    private void do_0003(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        Req_0003 q = new Req_0003();

        VnndResMsg res = new VnndResMsg();
        res.setVid(q.getVid());
        res.setStatus(ProtoConstants.PROCESS_FAIL);
        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }

            processEvent(res, taskId, Constants.LOGIN_TOPIC, q.id(), channelProcessor);

            // 从reids删除这台车的缓存
            Jedis jedis = null;
            try {
                jedis = RedisPoolManager.getJedis();
                jedis.del(Constants.GK_VEHICLE_STATE + upMsg.vid);
            } catch (Exception e) {
                logger.warn("在Redis中更新车辆状态失败", e);
            } finally {
                if (null != jedis) {
                    RedisPoolManager.returnResource(jedis);
                }
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        return;
    }

    /**
     * 下行协议：设置终端参数
     *
     * @param dnReq
     * @return
     */
    private void do_8103(final Req_8103 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.info("接收到设置终端参数请求:{}", dnReq);
        }

        ///请求参数合法性校验，先检查终端参数在数据库是否已经定义
        try {
            //Validate.notNull(dnReq.getVid(), "vid 参数不能为空");
            Validate.notNull(dnReq.getParamItems(), "paramItems 参数不能为空");
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
            Validate.isTrue(paramNum <= 255, "单次设置的参数太多");

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
            //最后检验下dataBuf里数据是否正确
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("设置终端参数成功: " + result.vid);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("设置终端参数失败", t);
            }
        });

    }

    /**
     * 下行协议：查询终端参数
     *
     * @param dnReq
     * @return
     */
    private void do_8104(final Req_8104 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }
        logger.info("查询终端参数:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_0104 res = new Res_0104();
        try {
            req.vid = dnReq.getVid();
            req.sim = String.valueOf(dnReq.getSim());
            req.msgId = (short) 0x8104;
            req.dataBuf = Unpooled.EMPTY_BUFFER; //消息体为空
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询终端参数成功，终端返回参数: " + result);
                ByteBuf reqBuf = result.dataBuf;
                reqBuf.markReaderIndex();

                int count = reqBuf.readableBytes();
                byte[] out1 = new byte[count];
                reqBuf.readBytes(out1);
                reqBuf.resetReaderIndex();
                String byteString = ByteBufUtil.hexDump(out1).toUpperCase();
                logger.info("获得0104返回消息体(已合并分包):"+ byteString);

                List<Object> paramItemList =  Arrays.asList(road8103.split(","));
                if (null != paramItemList && paramItemList.size() > 0) {
                    Constants.PARAM_ITEM_8103_MAP.clear();
                    for (Object paramItem : paramItemList) {
                        String[] paramPair = String.valueOf(paramItem).split("#");
                        Constants.PARAM_ITEM_8103_MAP.put(paramPair[0], Integer.parseInt(paramPair[1]));
                    }
                }
                short flowNo = reqBuf.readShort();
                logger.info("获得0104的返回流水号："+flowNo);
                res.setFlowNo(flowNo);

                int parametersNum = reqBuf.readByte();
                logger.info("获得0104的参数个数："+parametersNum);
                List<ParamItem> paramItems = new ArrayList<>();
                for(int i=0 ;i<parametersNum;i++){
                    int parameterId = reqBuf.readInt();
                    logger.info("获得0104的参数ID："+parameterId);
                    //根据id判断值的类型
                    int parameterValueLength = reqBuf.readByte();
                    logger.info("获得0104的参数值长度："+parameterValueLength);
                    //根据参数类型和参数长度取出来参数实际的值

                    int paramType = 5; //默认为5  如果在上面的初始化参数ID-类型列表中没有找到的话
                    if (Constants.PARAM_ITEM_8103_MAP.containsKey(String.valueOf(parameterId))) {
                        paramType = Constants.PARAM_ITEM_8103_MAP.get(String.valueOf(parameterId));
                    }
                    String parameterValue = "";
                    switch (paramType) {
                        case 1:
                            byte[] out = new byte[parameterValueLength];
                            reqBuf.readBytes(out);
                            parameterValue= new String(out);
                            logger.info("获得0104的参数值："+parameterValue);
                            break;
                        case 2:
                            int byteValue = reqBuf.readByte();
                            parameterValue = String.valueOf(byteValue);
                            logger.info("获得0104的参数值："+parameterValue);
                            break;
                        case 3:
                            int shortValue = reqBuf.readShort();
                            parameterValue = String.valueOf(shortValue);
                            logger.info("获得0104的参数值："+parameterValue);
                            break;
                        case 4:
                            int intValue = reqBuf.readInt();
                            parameterValue = String.valueOf(intValue);
                            logger.info("获得0104的参数值："+parameterValue);
                            break;
                        case 5:
                            reqBuf.readBytes(parameterValueLength);
                            parameterValue = "未知参数值";
                            logger.info("获得0104的参数值：未知参数值或未知参数ID"+parameterId);
                            break;
                    }
                    ParamItem item = new ParamItem(parameterId,parameterValue);
                    paramItems.add(item);
                }
                res.setParamItems(paramItems);
                logger.info("开始发送数据："+JSON.toJSONString(res));
                processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.TERMINALPARAM_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询终端参数失败", t);
            }
        });
    }

    /**
     * 终端控制
     *
     * @param dnReq
     * @return
     */
    private void do_8105(final Req_8105 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
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
//            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
//            res.setRc(JtsResMsg.RC_FAIL);
//            res.setVid(dnReq.getVid());
//            ctx.reply(res);
//            return;
//        }

    }

    private void do_8106_nd(final Req_8106_nd dnReq, String taskId, ChannelProcessor channelProcessor) {
        logger.debug("接收到下行请求:{}", dnReq);
        ProtoMsg req = new ProtoMsg();
        final Res_8106_nd res = new Res_8106_nd();
        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8106;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(1);//参数个数
            req.dataBuf.writeInt(0xF006);
            req.dataBuf.writeZero(16);
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询终端参数成功: " + result.vid);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询终端参数失败", t);
            }
        });
    }

    /**
     * 查询终端的指定参数
     *
     * @param dnReq
     * @return
     */
    private void do_8106(final Req_8106 dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
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
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            return;
        }
        ListenableFuture<ProtoMsg> f = sendRequest(req, (short)0x0104);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                logger.warn("查询终端参数成功，终端返回参数: " + result);
                ByteBuf reqBuf = result.dataBuf;

                short flowNo = reqBuf.readUnsignedByte();
                logger.info("获得0104的返回流水号：" + flowNo);
                res.setFlowNo(flowNo);

                int parametersNum = reqBuf.readInt();
                logger.info("获得0104的参数个数：" + parametersNum);
                List<ParamItem> paramItems = new ArrayList<>();
                for (int i = 0; i < parametersNum; i++) {
                    int parameterId = reqBuf.readInt();
                    logger.info("获得0104的参数ID：" + parameterId);
                    //根据id判断值的类型
                    int parameterValueLength = reqBuf.readByte();
                    logger.info("获得0104的参数值长度：" + parameterValueLength);
                    byte[] out = new byte[parameterValueLength];
                    reqBuf.readBytes(out);
                    String parameterValue = new String(out);
                    logger.info("获得0104的参数值：" + parameterValue);
                    ParamItem item = new ParamItem(parameterId, parameterValue);
                    paramItems.add(item);
                }
                res.setParamItems(paramItems);
                processEvent(JSON.toJSONString(res), taskId, res.id(), Constants.TERMINALPARAM_TOPIC, channelProcessor);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("查询终端参数失败", t);
            }
        });
    }

    /**
     * 断开终端与前置机连接
     *
     * @param dnReq
     */
    private void do_disconnectVehiceFromTas(final Req_DisconnectVehiceFromTas dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到断开终端与前置机连接请求:{}", dnReq);
        }
        try {
            TcpConnectionManager tcpConnectionManager = getTcpConnectionManager();
            TcpConnection vid2Connection = tcpConnectionManager.getConnectionByVid(dnReq.getVid());
            if (vid2Connection != null) {
                vid2Connection.close();
                logger.debug("断开终端与前置机连接请求成功:{}", dnReq);
            } else {
                logger.debug("车辆未登录:{}", dnReq);
            }
        } catch (Exception e) {
            logger.debug("断开终端与前置机连接请求异常:{}", e.getMessage());
        }
    }


    /**
     * 根据（sim、did、车牌号）或（sim、did、vin）获取车辆信息
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
        if (req.getCol() == 0) {//车牌颜色为 0 时，取车辆 VIN
            sqlBuf.append("' and vin = '");
        } else {
            sqlBuf.append("' and plateNo = '");
        }
        sqlBuf.append(req.getLpn());
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }

    /**
     * 根据sim卡查询vid
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
     * 根据deviceNo获取车辆信息
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
     * 根据根据lpn获取车辆信息
     *
     * @param req
     * @return
     */
    private CVVehicleDTO getVehiclByLpn(Req_0100 req) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("select * from /");
        sqlBuf.append(Constants.REGION_CVVEHICLEINFO);
        if (req.getCol() == 0) {//车牌颜色为 0 时，取车辆 VIN
            sqlBuf.append(" where vin = '");
        } else {
            sqlBuf.append(" where plateNo = '");
        }
        sqlBuf.append(req.getLpn());
        sqlBuf.append("' and isValid = '1' limit 1");
        return queryCVVehicleInfo(sqlBuf.toString());
    }


}
