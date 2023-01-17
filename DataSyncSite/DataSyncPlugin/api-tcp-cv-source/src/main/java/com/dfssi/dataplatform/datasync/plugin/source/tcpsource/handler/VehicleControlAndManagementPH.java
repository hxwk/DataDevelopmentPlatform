package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil;
import com.google.common.base.Throwables;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class VehicleControlAndManagementPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(VehicleControlAndManagementPH.class);

    @Override
    public void setup() {

    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8500) {//车辆控制
            do_8500((Req_8500) dnReq);
        } else if (dnReq instanceof Req_8500X) {//车辆锁车
            do_8500X((Req_8500X) dnReq);
        } else if (dnReq instanceof Req_8600) {//设置圆形区域
            do_8600((Req_8600) dnReq);
        } else if (dnReq instanceof Req_8601) {//刪除圆形区域
            do_8601((Req_8601) dnReq);
        } else if (dnReq instanceof Req_8602) {//设置矩形区域
            do_8602((Req_8602) dnReq);
        } else if (dnReq instanceof Req_8603) {//刪除矩形区域
            do_8603((Req_8603) dnReq);
        } else if (dnReq instanceof Req_8604) {//设置多边形区域
            do_8604((Req_8604) dnReq);
        } else if (dnReq instanceof Req_8605) {//刪除多边形区域
            do_8605((Req_8605) dnReq);
        } else if (dnReq instanceof Req_8606) {//设置路线
            do_8606((Req_8606) dnReq);
        } else if (dnReq instanceof Req_8607) {//删除路线
            do_8607((Req_8607) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    private void do_8500X(final Req_8500X dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：删除线路
     *
     * @param dnReq
     * @return
     */
    private void do_8607(final Req_8607 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：设置线路
     *
     * @param dnReq
     * @return
     */
    private void do_8606(final Req_8606 dnReq) {
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
                    ProtoUtil.writeTime(req.dataBuf, dnReq.getBeginTime());
                    ProtoUtil.writeTime(req.dataBuf, dnReq.getEndTime());
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
            return;
        }

    }

    /**
     * 下行协议：车辆控制
     *
     * @param dnReq
     * @return
     */
    private void do_8500(final Req_8500 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：刪除多边形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8605(final Req_8605 dnReq) {
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
            return;
        }

    }

    private void do_8604(final Req_8604 dnReq) {
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
                    ProtoUtil.writeTime(req.dataBuf, regionParamItem.getBeginTime());
                    ProtoUtil.writeTime(req.dataBuf, regionParamItem.getEndTime());
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
            } catch (Exception e) {
                logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
                res.setRc(JtsResMsg.RC_FAIL);
            }
        }
    }

    /**
     * 下行协议：刪除矩形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8603(final Req_8603 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：设置矩形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8602(final Req_8602 dnReq) {
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
                        ProtoUtil.writeTime(req.dataBuf, regionParamItem.getBeginTime());
                        ProtoUtil.writeTime(req.dataBuf, regionParamItem.getEndTime());
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
            return;
        }
    }

    /**
     * 下行协议：刪除圆形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8601(final Req_8601 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：设置圆形区域
     *
     * @param dnReq
     * @return
     */
    private void do_8600(final Req_8600 dnReq) {
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
                            ProtoUtil.writeTime(req.dataBuf, regionParamItem.getBeginTime());
                            ProtoUtil.writeTime(req.dataBuf, regionParamItem.getEndTime());
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
            return;
        }

    }
}
