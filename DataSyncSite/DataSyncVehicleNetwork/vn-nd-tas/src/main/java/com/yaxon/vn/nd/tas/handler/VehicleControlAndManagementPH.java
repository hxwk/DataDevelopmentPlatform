package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.ProtoUtil;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.*;

/**
 * Author: Sun Zhen
 * Time: 2013-11-27 14:06
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class VehicleControlAndManagementPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(VehicleControlAndManagementPH.class);

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8500) {//车辆控制
            do_8500(ctx, (Req_8500) dnReq);
        } else if (dnReq instanceof Req_8500X) {//车辆锁车
            do_8500X(ctx, (Req_8500X) dnReq);
        } else if (dnReq instanceof Req_8600) {//设置圆形区域
            do_8600(ctx, (Req_8600) dnReq);
        } else if (dnReq instanceof Req_8601) {//刪除圆形区域
            do_8601(ctx, (Req_8601) dnReq);
        } else if (dnReq instanceof Req_8602) {//设置矩形区域
            do_8602(ctx, (Req_8602) dnReq);
        } else if (dnReq instanceof Req_8603) {//刪除矩形区域
            do_8603(ctx, (Req_8603) dnReq);
        } else if (dnReq instanceof Req_8604) {//设置多边形区域
            do_8604(ctx, (Req_8604) dnReq);
        } else if (dnReq instanceof Req_8605) {//刪除多边形区域
            do_8605(ctx, (Req_8605) dnReq);
        } else if (dnReq instanceof Req_8606) {//设置路线
            do_8606(ctx, (Req_8606) dnReq);
        } else if (dnReq instanceof Req_8607) {//删除路线
            do_8607(ctx, (Req_8607) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_8500X(final DmsContext ctx, final Req_8500X dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0500 res = new Res_0500();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8500;
            req.dataBuf = Unpooled.buffer(8);
            req.dataBuf.writeByte(dnReq.getFlag());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0500);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                res.setRc(JtsResMsg.RC_OK);
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("车辆控制失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：删除线路
     *
     * @param dnReq
     * @return
     */
    private void do_8607(final DmsContext ctx, final Req_8607 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8607;
            req.dataBuf = Unpooled.buffer(32);
            int routeIdNum = dnReq.getRouteIds().size();
            Validate.isTrue(routeIdNum <= 255, "单次删除的路线不能超过255个");
            req.dataBuf.writeByte(routeIdNum);

            if (routeIdNum == 0) {

            } else {
                for (long routeId : dnReq.getRouteIds()) {
                    req.dataBuf.writeInt((int) routeId);
                }
            }
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
                logger.info("删除路线成功!");
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("删除路线失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：设置线路
     *
     * @param dnReq
     * @return
     */
    private void do_8606(final DmsContext ctx, final Req_8606 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8606;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeInt(dnReq.getRouteId().intValue());
            req.dataBuf.writeShort(dnReq.getRouteAttr());

            if ((dnReq.getRouteAttr() & 1) != 0) {//路线属性位第0位不为0
                if(dnReq.getBeginTime().substring(0,8).equals("00-00-00")){
                     String[] beginTime =dnReq.getBeginTime().split("-");
                    String changeBeginStyle = beginTime[0]+beginTime[1] +beginTime[2]+beginTime[3]+beginTime[4]+beginTime[5];
                    req.dataBuf.writeBytes(ProtoUtil.str2Bcd(changeBeginStyle));

                    String[] endTime =dnReq.getEndTime().split("-");
                    String changeEndStyle = endTime[0]+endTime[1] +endTime[2]+endTime[3]+endTime[4]+endTime[5];
                    req.dataBuf.writeBytes(ProtoUtil.str2Bcd(changeEndStyle));

                }else{
                    writeTime(req.dataBuf, dnReq.getBeginTime());
                    writeTime(req.dataBuf, dnReq.getEndTime());
                }

            }

            int InflexionNum = dnReq.getInflexionParamItems().size();
            Validate.isTrue(InflexionNum <= 65535, "单次设置线路不能超过65535条");
            req.dataBuf.writeShort(InflexionNum);

            if (dnReq.getInflexionParamItems() != null && InflexionNum > 0) {
                for (InflexionParamItem inflexionParamItem : dnReq.getInflexionParamItems()) {

                    req.dataBuf.writeInt(inflexionParamItem.getInflexionId().intValue());
                    req.dataBuf.writeInt(inflexionParamItem.getSectionId().intValue());
                    req.dataBuf.writeInt(inflexionParamItem.getLat());
                    req.dataBuf.writeInt(inflexionParamItem.getLon());
                    req.dataBuf.writeByte(inflexionParamItem.getSectionWidth());
                    req.dataBuf.writeByte(inflexionParamItem.getSectionAttr());

                    if ((inflexionParamItem.getSectionAttr() & 1) != 0) {//路段属性位第0位不为0
                        req.dataBuf.writeShort(inflexionParamItem.getSectionTooLongLimen());
                        req.dataBuf.writeShort(inflexionParamItem.getSectionLackLimen());
                    }

                    if ((inflexionParamItem.getSectionAttr() & 2) != 0) {//路段属性位第1位不为0
                        req.dataBuf.writeShort(inflexionParamItem.getSectionHighestSpeed());
                        req.dataBuf.writeByte(inflexionParamItem.getSectionOverspeedLastTime());
                    }
                }
            }
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
                logger.info("设置路线成功！");
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("设置线路失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：车辆控制
     *
     * @param dnReq
     * @return
     */
    private void do_8500(final DmsContext ctx, final Req_8500 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0500 res = new Res_0500();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8500;
            req.dataBuf = Unpooled.buffer(8);
            req.dataBuf.writeByte(dnReq.getFlag());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0500);
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
                        GpsVo.ExtraInfoItem item = new GpsVo.ExtraInfoItem();
                        item.setId(reqBuf.readByte());
                        item.setData(readU8Bytes(reqBuf));
                        extraInfoItems.add(item);
                    }
                    gps.setExtraInfoItems(extraInfoItems);
                    res.setGps(gps);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}]接收到上行应答消息:{}", result.sim, res);
                    }
                } catch (Exception e) {
                }

                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("车辆控制失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：刪除多边形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8605(final DmsContext ctx, final Req_8605 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8605;
            req.dataBuf = Unpooled.buffer(32);
            int regionIdNum = dnReq.getRegionIds().size();
            Validate.isTrue(regionIdNum <= 255, "单次删除的多边形区域不能超过255个");
            req.dataBuf.writeByte(regionIdNum);

            if (regionIdNum == 0) {

            } else {
                for (long regionId : dnReq.getRegionIds()) {
                    req.dataBuf.writeInt((int) regionId);
                }
            }
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
                logger.warn("删除多边形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：设置多边形区域
     *
     * @param dnReq
     * @return
     */
    /*private void do_8604(final DmsContext ctx, final Req_8604 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8604;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getAttr());


            if (dnReq.getRegionParamItems() != null) {
                int contactNum = dnReq.getRegionParamItems().size();
                Validate.isTrue(contactNum <= 255, "单次设置的多边形区域不能超过255个");

                req.dataBuf.writeByte(contactNum);
                for (RegionParamItem regionParamItem : dnReq.getRegionParamItems()) {
                    req.dataBuf.writeInt((int) regionParamItem.getAreaId());
                    req.dataBuf.writeShort(regionParamItem.getAreaAttr());


                    if ((regionParamItem.getAreaAttr() & 1) != 0) {//属性位第0位不为0
                        writeTime(req.dataBuf, regionParamItem.getBeginTime());
                        writeTime(req.dataBuf, regionParamItem.getEndTime());
                    }

                    if ((regionParamItem.getAreaAttr() & 2) != 0) {//属性位第1位不为0
                        req.dataBuf.writeShort(regionParamItem.getSpeed());
                        req.dataBuf.writeByte(regionParamItem.getTime());
                    }

                    if (regionParamItem.getPeakParamItems().size() == 0) {
                        req.dataBuf.writeShort(0);
                    } else {
                        req.dataBuf.writeShort(regionParamItem.getPeakParamItems().size());
                        for (PeakParamItem peakParamItem : regionParamItem.getPeakParamItems()) {
                            req.dataBuf.writeInt(peakParamItem.getLat());
                            req.dataBuf.writeInt(peakParamItem.getLon());
                        }
                    }
                }
            } else {
                req.dataBuf.writeByte(0);
            }


        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            res.setDid(dnReq.getDid());
            ctx.reply(res);return;
        }

        final SettableFuture<Message> sf = SettableFuture.create();
        ListenableFuture<ProtoMsg> f = tcpChannel().sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                res.setDid(result.did);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("设置多边形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                res.setDid(dnReq.getDid());
                ctx.reply(res);
            }
        });
        return;
    }*/
    private void do_8604(final DmsContext ctx, final Req_8604 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();
        res.setVid(dnReq.getVid());

        final AtomicInteger resCount = new AtomicInteger(0);
        final AtomicBoolean b = new AtomicBoolean(true);

        //并行异步请求机制
        for (int i = 0; i < dnReq.getRegionParamItems().size(); i++) {
            try {
                req.vid = dnReq.getVid();
                req.msgId = (short) 0x8604;
                req.dataBuf = Unpooled.buffer(32);

                RegionParamItem regionParamItem = dnReq.getRegionParamItems().get(i);
                req.dataBuf.writeInt((int) regionParamItem.getAreaId());
                req.dataBuf.writeShort(regionParamItem.getAreaAttr());

                if ((regionParamItem.getAreaAttr() & 1) != 0) {//属性位第0位不为0
                    writeTime(req.dataBuf, regionParamItem.getBeginTime());
                    writeTime(req.dataBuf, regionParamItem.getEndTime());
                }

                if ((regionParamItem.getAreaAttr() & 2) != 0) {//属性位第1位不为0
                    req.dataBuf.writeShort(regionParamItem.getSpeed());
                    req.dataBuf.writeByte(regionParamItem.getTime());
                }

                if (regionParamItem.getPeakParamItems().size() == 0) {
                    req.dataBuf.writeShort(0);
                } else {
                    req.dataBuf.writeShort(regionParamItem.getPeakParamItems().size());
                    for (PeakParamItem peakParamItem : regionParamItem.getPeakParamItems()) {
                        req.dataBuf.writeInt(peakParamItem.getLat());
                        req.dataBuf.writeInt(peakParamItem.getLon());
                    }
                }
                resCount.incrementAndGet();
                ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);

                Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
                    @Override
                    public void onSuccess(ProtoMsg result) {
                        result.dataBuf.skipBytes(4);
                        handle(result.dataBuf.readByte());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        handle(JtsResMsg.RC_FAIL);
                    }

                    private void handle(byte rc) {
                        if (rc != 0) {
                            b.set(false);
                        }

                        int c = resCount.decrementAndGet();
                        if (c <= 0) {
                            if (b.get()) {
                                res.setRc(JtsResMsg.RC_OK);
                            } else {
                                res.setRc(JtsResMsg.RC_FAIL);
                            }
                            ctx.reply(res);
                        }

                    }
                });
            } catch (Exception e) {
                logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
                res.setRc(JtsResMsg.RC_FAIL);
                ctx.reply(res);
            }
        }
    }

    /**
     * 下行协议：刪除矩形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8603(final DmsContext ctx, final Req_8603 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8603;
            req.dataBuf = Unpooled.buffer(32);
            int regionIdNum = dnReq.getRegionIds().size();
            Validate.isTrue(regionIdNum <= 255, "单次删除的矩形区域不能超过255个");
            req.dataBuf.writeByte(regionIdNum);

            if (regionIdNum == 0) {

            } else {
                for (long regionId : dnReq.getRegionIds()) {
                    req.dataBuf.writeInt((int) regionId);
                }
            }
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
                logger.warn("删除矩形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：设置矩形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8602(final DmsContext ctx, final Req_8602 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8602;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getAttr());


            if (dnReq.getRegionParamItems() != null) {
                int contactNum = dnReq.getRegionParamItems().size();
                Validate.isTrue(contactNum <= 255, "单次设置的矩形区域不能超过255个");

                req.dataBuf.writeByte(contactNum);
                for (RegionParamItem regionParamItem : dnReq.getRegionParamItems()) {
                    req.dataBuf.writeInt((int) regionParamItem.getAreaId());
                    req.dataBuf.writeShort(regionParamItem.getAreaAttr());
                    req.dataBuf.writeInt(regionParamItem.getLeftUpLat());
                    req.dataBuf.writeInt(regionParamItem.getLeftUpLon());
                    req.dataBuf.writeInt(regionParamItem.getRightDownLat());
                    req.dataBuf.writeInt(regionParamItem.getRightDownLon());

                    if ((regionParamItem.getAreaAttr() & 1) != 0) {//属性位第0位不为0
                        writeTime(req.dataBuf, regionParamItem.getBeginTime());
                        writeTime(req.dataBuf, regionParamItem.getEndTime());
                    }

                    if ((regionParamItem.getAreaAttr() & 2) != 0) {//属性位第1位不为0
                        req.dataBuf.writeShort(regionParamItem.getSpeed());
                        req.dataBuf.writeByte(regionParamItem.getTime());
                    }
                }
            } else {
                req.dataBuf.writeByte(0);
            }


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
                logger.warn("设置矩形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：刪除圆形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8601(final DmsContext ctx, final Req_8601 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8601;
            req.dataBuf = Unpooled.buffer(32);
            int regionIdNum = dnReq.getRegionIds().size();
            Validate.isTrue(regionIdNum <= 255, "单次删除的圆形区域不能超过255个");
            req.dataBuf.writeByte(regionIdNum);

            if (regionIdNum == 0) {

            } else {
                for (long regionId : dnReq.getRegionIds()) {
                    req.dataBuf.writeInt((int) regionId);
                }
            }
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
                logger.warn("删除圆形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：设置圆形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8600(final DmsContext ctx, final Req_8600 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8600;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getAttr());


            if (dnReq.getRegionParamItems() != null) {
                int contactNum = dnReq.getRegionParamItems().size();
                Validate.isTrue(contactNum <= 255, "单次设置的圆形区域不能超过255个");

                req.dataBuf.writeByte(contactNum);
                for (RegionParamItem regionParamItem : dnReq.getRegionParamItems()) {
                    req.dataBuf.writeInt((int) regionParamItem.getAreaId());
                    req.dataBuf.writeShort(regionParamItem.getAreaAttr());
                    req.dataBuf.writeInt(regionParamItem.getLat());
                    req.dataBuf.writeInt(regionParamItem.getLon());
                    req.dataBuf.writeInt(regionParamItem.getRad());

                    if ((regionParamItem.getAreaAttr() & 1) != 0) {//属性位第0位不为0

                        if (regionParamItem.getBeginTime().substring(0, 8).equals("00-00-00")) {
                            String[] beginTime = regionParamItem.getBeginTime().split("-");
                            String changeBeginStyle = beginTime[0] + beginTime[1] + beginTime[2] + beginTime[3] + beginTime[4] + beginTime[5];
                            req.dataBuf.writeBytes(ProtoUtil.str2Bcd(changeBeginStyle));

                            String[] endTime = regionParamItem.getEndTime().split("-");
                            String changeEndStyle = endTime[0] + endTime[1] + endTime[2] + endTime[3] + endTime[4] + endTime[5];
                            req.dataBuf.writeBytes(ProtoUtil.str2Bcd(changeEndStyle));
                        } else {
                            writeTime(req.dataBuf, regionParamItem.getBeginTime());
                            writeTime(req.dataBuf, regionParamItem.getEndTime());
                        }

                    }

                    if ((regionParamItem.getAreaAttr() & 2) != 0) {//属性位第1位不为0
                        req.dataBuf.writeShort(regionParamItem.getSpeed());
                        req.dataBuf.writeByte(regionParamItem.getTime());
                    }

                }
            } else {
                req.dataBuf.writeByte(0);
            }


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
                logger.warn("设置圆形区域失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }
}
