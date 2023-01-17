package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.common.BitParser;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.X0200BitParse;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yaxon.vn.nd.redis.RedisConstants.GK_VEHICLE_STATE;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTime;

/**
 * Author: Sun Zhen
 * Time: 2013-11-25 19:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 * @Modifier: JianKang
 * @Modify Time:2018-01-15 10:12
 */
@Component
public class PositionAndAlarmPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(PositionAndAlarmPH.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{configProperties['dms.nodeId']}")
    private String nodeId;
    @Value("#{configProperties['terminal.maxidletimemillis']}")
    private Long terminalMaxIdleTimeMillis;

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0200) {//位置信息汇报
            do_0200(upMsg);
        }else if (upMsg.msgId == 0x0704) { //位置信息批量汇报
            do_0704(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8201) {//位置信息查询
            do_8201(ctx, (Req_8201) dnReq);
        } else if (dnReq instanceof Req_8202) {//临时位置跟踪控制
            do_8202(ctx, (Req_8202) dnReq);
        } else if (dnReq instanceof Req_8203) {//人工确认报警消息
            do_8203(ctx, (Req_8203) dnReq);
        } else {
            throw new UnsupportedProtocolException("未知的下行请求消息类型：" + dnReq.getClass().getName());
        }
    }

    private void do_8203(final DmsContext ctx, final Req_8203 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8203;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeShort(dnReq.getAlarmMsgSerialNo());
            req.dataBuf.writeInt(dnReq.getAlarmType());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        try {
            sendMessage(req);
            res.setVid(req.vid);
            res.setRc(JtsResMsg.RC_OK);
            ctx.reply(res);
        } catch (Exception e) {
            res.setVid(req.vid);
            res.setRc(JtsResMsg.RC_FAIL);
            ctx.reply(res);
        }

       /* ListenableFuture<ProtoMsg> f = tcpChannel().sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("人工确认报警消息失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });*/
    }

    private void do_8202(final DmsContext ctx, final Req_8202 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8202;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeShort(dnReq.getTimeDistance());
            req.dataBuf.writeInt(dnReq.getUsefulLife());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("临时位置跟踪控制失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    private void do_8201(final DmsContext ctx, final Req_8201 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0201 res = new Res_0201();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8201;
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0201);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                res.setRc(JtsResMsg.RC_OK);

                //解析上行应答
                try {
                    ByteBuf reqBuf = result.dataBuf;
                    short sn = reqBuf.readShort(); //流水号

                    GpsVo gps = new GpsVo();
                    gps.setSim(result.sim);
                    gps.setAlarm(reqBuf.readInt());
                    gps.setState(reqBuf.readInt());
                    gps.setLat(reqBuf.readInt());
                    gps.setLon(reqBuf.readInt());
                    gps.setAlt(reqBuf.readShort());
                    gps.setSpeed(reqBuf.readShort());
                    gps.setDir(reqBuf.readShort());
                    gps.setGpsTime(readTime(reqBuf.readBytes(6)));

                    List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
                    while (reqBuf.readableBytes() > 0) {
                        byte extraInfoId = reqBuf.readByte();
                        if (extraInfoId == 0x01) {//总计里程
                            reqBuf.skipBytes(1);
                            gps.setMile(reqBuf.readInt());
                        } else if (extraInfoId == 0x02) {//分钟油耗
                            reqBuf.skipBytes(1);
                            Short shortValue = reqBuf.readShort();
                            gps.setFuel(shortValue / 100.0f);
                        } else if (extraInfoId == 0x03) {//行驶记录仪速度
                            reqBuf.skipBytes(1);
                            gps.setSpeed1(reqBuf.readShort());
                        }
//                        else if (extraInfoId == 0x04) {//人工确认告警
//                            reqBuf.skipBytes(1);
//                            gps.setArtificialConfirmedAlarm(reqBuf.readUnsignedShort());
//                        }
                        else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                            reqBuf.skipBytes(1);
                            gps.setSignalState(reqBuf.readInt());
                        } else if (extraInfoId == 0x2A) {//IO状态位
                            reqBuf.skipBytes(1);
                            gps.setIoState(reqBuf.readShort());
                        }
//                        else if (extraInfoId == 0x2B) {//模拟量,bit0-15,AD0;bit16-31,AD1
//                            reqBuf.skipBytes(1);
//                            gps.setAnalog(reqBuf.readUnsignedInt());
//                        } else if(extraInfoId == 0x30){ //无线通信网络信号强度
//                            reqBuf.skipBytes(1);
//                            gps.setSignalStrength(reqBuf.readUnsignedByte());
//                        }else if(extraInfoId == 0x31){ //GNSS定位卫星数
//                            reqBuf.skipBytes(1);
//                            gps.setPositioningSatelliteNumber(reqBuf.readUnsignedByte());
//                        }
                        else if(extraInfoId ==0xE1){ //主电平电压
                            reqBuf.skipBytes(1);
                            gps.setBatteryVoltage(reqBuf.readShort());
                        }else if(extraInfoId == 0xF1){ //总计油耗
                            reqBuf.skipBytes(1);
                            gps.setTotalFuelConsumption(reqBuf.readUnsignedInt());
                        }else if(extraInfoId == 0xE2){ //累计油耗 （/1000 && /100 单位升）
                            reqBuf.skipBytes(1);
                            gps.setCumulativeOilConsumption(reqBuf.readUnsignedInt());
                        }else if(extraInfoId == 0xF2){ //油箱液位
                            reqBuf.skipBytes(1);
                            gps.setHydraulicTank(reqBuf.readUnsignedByte());
                        }else if(extraInfoId == 0xF3) { //车载重量
                            reqBuf.skipBytes(1);
                            gps.setVehicleWeight(reqBuf.readUnsignedShort());
                        }else if(extraInfoId == 0xF4){ //0位:CAN状态 1位：油量液位状态
                            reqBuf.skipBytes(1);
                            gps.setCanAndHydraulicTankStatus(reqBuf.readUnsignedInt());
                        }
                        else {
                            GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                            item.setId(extraInfoId);
                            int byteLength = reqBuf.readUnsignedByte();
                            byte[] bytes = new byte[byteLength];
                            item.setData(bytes);
                            reqBuf.skipBytes(byteLength);
                            extraInfoItems.add(item);
                        }
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    res.setGps(gps);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}]接收到上行应答消息:{}", result.sim, res);
                    }
                } catch (Exception e) {
                    logger.error("上行解析异常:{}",e.getMessage());
                }
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("位置信息查询失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 位置信息汇报
     * @param upMsg
     * @return
     */
    private void do_0200(final ProtoMsg upMsg) {
        Req_0200 q = new Req_0200();
        byte[] bytes;
        //异常驾驶行为告警
        List<Object> abnormalData ;
        List<String> abnormalDrivingType ;
        Short abnormalDrivingDegree ;

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);
            GpsVo gps = new GpsVo();
            gps.setSim(upMsg.sim);
            //告警
            int alarm = reqBuf.readInt();
            gps.setAlarm(alarm);
            if(0!=alarm){
                gps.setAlarms(X0200BitParse.parseAlarm(alarm));
            }
            //车辆状态
            int vstate = reqBuf.readInt();
            gps.setState(vstate);
            if(0!=vstate){
                gps.setVehicleStatus(X0200BitParse.getStateDesp(vstate));
            }
            gps.setLat(reqBuf.readInt());
            gps.setLon(reqBuf.readInt());
            gps.setAlt(reqBuf.readShort());
            gps.setSpeed(reqBuf.readShort());
            gps.setDir(reqBuf.readShort());
            gps.setGpsTime(readTime(reqBuf.readBytes(6)));

            List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
            while (reqBuf.readableBytes() > 0) {
                short extraInfoId = reqBuf.readUnsignedByte();
                if (extraInfoId == 0x01) {//里程
                    reqBuf.skipBytes(1);
                    gps.setMile(reqBuf.readInt());
                } else if (extraInfoId == 0x02) {//油耗
                    reqBuf.skipBytes(1);
                    int shortValue = reqBuf.readUnsignedShort();
                    gps.setFuel(shortValue / 100.0f);
                } else if (extraInfoId == 0x03) {//行驶记录仪速度
                    reqBuf.skipBytes(1);
                    gps.setSpeed1(reqBuf.readShort());
                } else if (extraInfoId == 0x18){ //异常驾驶行为报警
                    reqBuf.skipBytes(1);
                    if(reqBuf.readableBytes() > 0){
                        ByteBuf tmpBuf = reqBuf.readBytes(3);
                        //异常驾驶行为类型 判空
                        abnormalData = BitParser.parseAbnormalDrivingData(tmpBuf);
                        if(abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData){
                            abnormalDrivingType = (List<String>) abnormalData.get(0);
                            gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                        }
                        //异常驾驶行为程度 判空
                        if(abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData){
                            abnormalDrivingDegree = (Short)abnormalData.get(1);
                            //判断是不是有效值
                            if(abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100){
                                gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                            }else{
                                logger.warn("不是有效异常驾驶行为程度值");
                            }
                        }
                    }
                } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                    reqBuf.skipBytes(1);
                    int signalState = reqBuf.readInt();
                    gps.setSignalState(signalState);
                    if(0!=signalState){
                        gps.setSignalStates(X0200BitParse.parseVehicleStatus(signalState));
                    }
                } else if (extraInfoId == 0x2A) {//IO状态位
                    reqBuf.skipBytes(1);
                    gps.setIoState(reqBuf.readShort());
                } else if(extraInfoId ==0xE1){ //电平电压
                    reqBuf.skipBytes(1);
                    gps.setBatteryVoltage(reqBuf.readShort());
                }else if(extraInfoId == 0xE2) { //累计油耗 0.01
                    reqBuf.skipBytes(1);
                    gps.setCumulativeOilConsumption(reqBuf.readUnsignedInt());
                }else if(extraInfoId == 0xE3){ //累计油耗 0.1 和A司沟通新增E3作为累计油耗
                    reqBuf.skipBytes(1);
                    long cumulativeOilConsumption = reqBuf.readUnsignedInt();
                    if(0!=cumulativeOilConsumption){
                        gps.setCumulativeOilConsumption(cumulativeOilConsumption*100);
                    }
                }else if(extraInfoId == 0xF1){ //总计油耗
                    reqBuf.skipBytes(1);
                    gps.setTotalFuelConsumption(reqBuf.readUnsignedInt());
                }else if(extraInfoId == 0xF2){ //f2报警信息
                    reqBuf.skipBytes(1);
                    long f2AlarmsCode = reqBuf.readUnsignedInt();
                    gps.setAlarmInforCode(f2AlarmsCode);
                    gps.setAlarmLists(X0200BitParse.getVdrAlarmLists(f2AlarmsCode));
                }else if(extraInfoId == 0xF3) { //车载重量
                    reqBuf.skipBytes(1);
                    gps.setVehicleWeight(reqBuf.readUnsignedShort());
                }else if(extraInfoId == 0xF4){ //0位:CAN状态 1位：油量液位状态
                    reqBuf.skipBytes(1);
                    gps.setCanAndHydraulicTankStatus(reqBuf.readUnsignedInt());
                }else {
                    GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                    item.setId(extraInfoId);
                    int byteLength = reqBuf.readUnsignedByte();
                    bytes = new byte[byteLength];
                    item.setData(bytes);
                    reqBuf.skipBytes(byteLength);
                    extraInfoItems.add(item);
                }
            }
            gps.setExtraInfoItems(extraInfoItems);
            q.setGps(gps);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        try {
            ValueOperations<String, String> op = redisTemplate.opsForValue();
            op.set(GK_VEHICLE_STATE + upMsg.vid, nodeId, terminalMaxIdleTimeMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("在Redis中更新车辆状态失败", e);
        }
        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
                // r.setRc((byte)0x04); 注释该代码 by liuzf,怀疑设备收到04时会重传数据，改成0
                r.setRc((byte)0x00);
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
     * 位置信息批量汇报
     *
     * @param upMsg
     * @return
     */
    private void do_0704(final ProtoMsg upMsg) {
        Req_0704 q = new Req_0704();
        //异常驾驶行为告警
        List<Object> abnormalData;
        List<String> abnormalDrivingType;
        Short abnormalDrivingDegree;
        //解析上行请求协议
        try {
            q.setSim(upMsg.sim);
            q.setVid(upMsg.vid);
            List<GpsVo> gpsList = Lists.newArrayList();
            GpsVo gps;
            ByteBuf msgData = upMsg.dataBuf;
            short dataCount = msgData.readShort();
            q.setPositionDataType(msgData.readByte());
            for (int i = 0; (i < dataCount)&&(msgData.readableBytes()>28); i++) {
                short dataLen = msgData.readShort();
                if (dataLen > 0) {
                    gps = new GpsVo();
                    int startReadIndex = msgData.readerIndex();
                    //告警
                    int alarm = msgData.readInt();
                    gps.setAlarm(alarm);
                    if(0!=alarm){
                        gps.setAlarms(X0200BitParse.parseAlarm(alarm));
                    }
                    int cState = msgData.readInt();
                    logger.debug("cState = " + cState);
                    gps.setState(cState);
                    if(0!=cState){
                        gps.setVehicleStatus(X0200BitParse.getStateDesp(cState));
                    }
                    gps.setLat(msgData.readInt());
                    gps.setLon(msgData.readInt());
                    gps.setAlt(msgData.readShort());
                    gps.setSpeed(msgData.readShort());
                    gps.setDir(msgData.readShort());
                    gps.setGpsTime(readTime(msgData.readBytes(6)));
                    gps.setSim(upMsg.sim);
                    List<GpsVo.ExtraInfoItem> extraInfoItems = Lists.newArrayList();
                    while (dataLen > msgData.readerIndex() - startReadIndex) {
                        short extraInfoId = msgData.readUnsignedByte();
                        if (extraInfoId == 0x01) {//里程
                            msgData.skipBytes(1);
                            gps.setMile(msgData.readInt());
                        } else if (extraInfoId == 0x02) {//油耗
                            msgData.skipBytes(1);
                            int shortValue = msgData.readUnsignedShort();
                            gps.setFuel(shortValue / 100.0f);
                        } else if (extraInfoId == 0x03) {//行驶记录仪速度
                            msgData.skipBytes(1);
                            gps.setSpeed1(msgData.readShort());
                        } else if (extraInfoId == 0x18) {//异常驾驶行为报警
                            msgData.skipBytes(1);
                            if(msgData.readableBytes() > 0){
                                ByteBuf tmpBuf = msgData.readBytes(3);
                                //异常驾驶行为类型 判空
                                abnormalData = BitParser.parseAbnormalDrivingData(tmpBuf);
                                if(abnormalData.get(0) instanceof List<?> && null != abnormalData.get(0) && null != abnormalData){
                                    abnormalDrivingType = (List<String>) abnormalData.get(0);
                                    gps.setAbnormalDrivingBehaviorAlarmType(abnormalDrivingType);
                                }
                                //异常驾驶行为程度 判空
                                if(abnormalData.get(1) instanceof Short && null != abnormalData.get(1) && null != abnormalData){
                                    abnormalDrivingDegree = (Short)abnormalData.get(1);
                                    //判断是不是有效值
                                    if(abnormalDrivingDegree >= 0 && abnormalDrivingDegree <= 100){
                                        gps.setAbnormalDrivingBehaviorAlarmDegree(abnormalDrivingDegree);
                                    }else{
                                        logger.warn("不是有效异常驾驶行为程度值");
                                    }
                                }
                            }
                        } else if (extraInfoId == 0x25) {//扩展车辆信号状态位
                            msgData.skipBytes(1);
                            int signalState = msgData.readInt();
                            gps.setSignalState(signalState);
                            if(0!=signalState){
                                gps.setSignalStates(X0200BitParse.parseVehicleStatus(signalState));
                            }
                        } else if (extraInfoId == 0x2A) {//IO状态位
                            msgData.skipBytes(1);
                            gps.setIoState(msgData.readShort());
                        } else if(extraInfoId ==0xE1){ //电平电压
                            msgData.skipBytes(1);
                            gps.setBatteryVoltage(msgData.readShort());
                        }else if(extraInfoId == 0xE2) { //累计油耗
                            msgData.skipBytes(1);
                            gps.setCumulativeOilConsumption(msgData.readUnsignedInt());
                        }else if(extraInfoId == 0xE3){ //累计油耗 0.1 和A司沟通新增E3作为累计油耗
                            msgData.skipBytes(1);
                            long cumulativeOilConsumption = msgData.readUnsignedInt();
                            if(0!=cumulativeOilConsumption){
                                gps.setCumulativeOilConsumption(cumulativeOilConsumption*100);
                            }
                        }else if(extraInfoId == 0xF1){ //总计油耗
                            msgData.skipBytes(1);
                            gps.setTotalFuelConsumption(msgData.readUnsignedInt());
                        }else if(extraInfoId == 0xF2){ //油箱液位
                            msgData.skipBytes(1);
                            gps.setHydraulicTank(msgData.readUnsignedByte());
                        }else if(extraInfoId == 0xF3) { //车载重量
                            msgData.skipBytes(1);
                            gps.setVehicleWeight(msgData.readUnsignedShort());
                        }else if(extraInfoId == 0xF4){ //0位:CAN状态 1位：油量液位状态
                            msgData.skipBytes(1);
                            gps.setCanAndHydraulicTankStatus(msgData.readUnsignedInt());
                        }else {
                            GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                            item.setId(extraInfoId);
                            int byteLength = msgData.readUnsignedByte();
                            byte[] bytes = new byte[byteLength];
                            item.setData(bytes);
                            msgData.skipBytes(byteLength);
                            extraInfoItems.add(item);
                        }
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    gpsList.add(gps);
                }
            }
            q.setGpsVos(gpsList);
        } catch (Exception e) {
            logger.warn("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}]接收到上行请求消息:{},packCount:{},packIndex{}", upMsg.sim, q,upMsg.packCount,upMsg.packIndex);
        }
        try {
            ValueOperations<String, String> op = redisTemplate.opsForValue();
            op.set(GK_VEHICLE_STATE + upMsg.vid, nodeId, terminalMaxIdleTimeMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("在Redis中更新车辆状态失败", e);
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001) result;
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

}
