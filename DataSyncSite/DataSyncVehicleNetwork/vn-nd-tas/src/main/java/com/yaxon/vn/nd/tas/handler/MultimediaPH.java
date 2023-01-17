package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tbp.si.*;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTime;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.writeTime;

/**
 * Author: Sun Zhen
 * Time: 2014-01-08 10:09
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class MultimediaPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(MultimediaPH.class);

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8801) {//摄像头立即拍摄命令
            do_8801(ctx, (Req_8801) dnReq);
        } else if (dnReq instanceof Req_8802) {//存储多媒体数据检索
            do_8802(ctx, (Req_8802) dnReq);
        } else if (dnReq instanceof Req_8803) {//存储多媒体数据上传命令
            do_8803(ctx, (Req_8803) dnReq);
        } else if (dnReq instanceof Req_8804) {//录音开始命令
            do_8804(ctx, (Req_8804) dnReq);
        } else if (dnReq instanceof Req_8805) {//单条存储多媒体数据检索上传命令
            do_8805(ctx, (Req_8805) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0805) {//摄像头立即拍摄应答上传（非部标协议）
            do_0805(upMsg);
        } else if (upMsg.msgId == 0x0800) {//多媒体事件信息上传
            do_0800(upMsg);
        } else if (upMsg.msgId == 0x0801) {//多媒体数据上传
            do_0801(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    /**
     * 摄像头立即拍摄应答上传（非部标协议）
     *
     * @param upMsg
     * @return
     */
    private void do_0805(final ProtoMsg upMsg) {
        Req_0805Q q = new Req_0805Q();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            short sn = reqBuf.readShort(); //流水号

            q.setVid(upMsg.vid);
            q.setResult(reqBuf.readByte());
            //协议中说，结果==0的时候，后面的字段才有效，
            //在南斗调试中发现，当结果不是0的时候，后面的字段就直接没有了，
            //这边不判断的话，直接读取就越界了。
            //奇怪的是，这边就算读取出来，后面也没拿这两个字段做处理
            if(q.getResult() == 0){
                q.setNum(reqBuf.readByte());
                //q.setMediaIds();
            }

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
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
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 多媒体数据上传。分包未合并。
     *
     * @param upMsg
     * @return
     */
    private void do_0801(final ProtoMsg upMsg) {
        Req_0801_P q = new Req_0801_P();

        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);
            q.setSim(upMsg.sim);
            q.setSn(upMsg.sn);
            if (upMsg.packCount == 0) { //不分包
                q.setPackCount(1);
                q.setPackIndex(1);
            } else {
                q.setPackCount(upMsg.packCount);
                q.setPackIndex(upMsg.packIndex);
            }

            ByteBuf reqBuf = upMsg.dataBuf;
            int dataLen = reqBuf.readableBytes();
            if (dataLen <= 0) {
                throw new Exception("分包数据长度异常:" + dataLen);
            }
            byte[] data = new byte[dataLen];
            reqBuf.readBytes(data);
            q.setData(data);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8800_P r = (Res_8800_P) result;

                ProtoMsg res = null;
                res = new ProtoMsg();
                res.msgId = (short) 0x8800;
                res.sim = upMsg.sim;
                res.vid = upMsg.vid;
                res.dataBuf = Unpooled.buffer(5);
                res.dataBuf.writeInt(r.getMediaId());
                if (r.getLastPack() == 1) { //最后一包
                    if (r.getRc() != JtsResMsg.RC_OK) {
                        List<Integer> packIndexes = r.getPackIndexes();
                        if (packIndexes == null || packIndexes.size() == 0) {
                            res.dataBuf.writeByte(0);
                        } else {
                            res.dataBuf.writeByte(packIndexes.size());
                            for (Integer packIndex : packIndexes) {
                                res.dataBuf.writeShort(packIndex);
                            }
                        }
                    } else {
                        res.dataBuf.writeByte(0);
                    }
                    sendMessage(res);
                } else { //非最后分包，返回通用应答
                    res.dataBuf.writeByte(0);
                    //南斗的说，有的设备没有收到8800，就认为平台没有收到数据，就不发了。
                    //说改成8800和通用应答两个都回复
                    sendMessage(res);
                    sendCenterGeneralRes(upMsg, r.getRc());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0801_0(final ProtoMsg upMsg) {
        Req_0801 q = new Req_0801();

        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);

            ByteBuf reqBuf = upMsg.dataBuf;
            q.setMediaDataId(reqBuf.readInt());
            q.setMediaType(reqBuf.readByte());
            q.setMediaFormatCode(reqBuf.readByte());
            q.setIncidentCode(reqBuf.readByte());
            q.setChannelId(reqBuf.readByte());

            /*GpsVo gpsVo = new GpsVo();
            gpsVo.setAlarm(reqBuf.readInt());
            gpsVo.setState(reqBuf.readInt());
            gpsVo.setLat(reqBuf.readInt());
            gpsVo.setLon(reqBuf.readInt());
            gpsVo.setAlt(reqBuf.readShort());
            gpsVo.setSpeed(reqBuf.readShort());
            gpsVo.setDir(reqBuf.readShort());
            gpsVo.setGpsTime(readTime(reqBuf));
            q.setGps(gpsVo);*/

            //多媒体数据包

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8800 r = (Res_8800) result;

                ProtoMsg res = new ProtoMsg();
                res.msgId = (short) 0x8800;
                res.vid = r.getVid();
                res.dataBuf = Unpooled.buffer(5);
                res.dataBuf.writeInt(r.getMediaDataId());

                //如果收到全部数据包则没有后续字段
                //TODO：添加数据包总数n
                res.dataBuf.writeByte(0);
                //res.dataBuf.writeByte(rc);
                sendMessage(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 多媒体事件信息上传
     *
     * @param upMsg
     * @return
     */
    private void do_0800(final ProtoMsg upMsg) {
        Req_0800 q = new Req_0800();

        //解析上行请求协议
        try {
            q.setVid(upMsg.vid);

            ByteBuf reqBuf = upMsg.dataBuf;
            q.setMediaDataId(reqBuf.readInt());
            q.setMediaType(reqBuf.readByte());
            q.setMediaFormatCode(reqBuf.readByte());
            q.setIncidentCode(reqBuf.readByte());
            q.setChannelId(reqBuf.readByte());

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
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
                logger.info("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 下行协议：单条存储多媒体数据检索上传命令
     *
     * @param dnReq
     * @return
     */
    private void do_8805(final DmsContext ctx, final Req_8805 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8805;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeInt(dnReq.getMediaId());
            req.dataBuf.writeByte(dnReq.getDeleteFlag());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001);
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
                logger.warn("单条存储多媒体数据检索上传命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：录音开始命令
     *
     * @param dnReq
     * @return
     */
    private void do_8804(final DmsContext ctx, final Req_8804 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8804;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(dnReq.getRecordOrder());
            req.dataBuf.writeShort(dnReq.getRecordTime());
            req.dataBuf.writeByte(dnReq.getSaveFlag());
            req.dataBuf.writeByte(dnReq.getAudioSample());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001);
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
                logger.warn("录音开始命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：存储多媒体数据上传命令
     *
     * @param dnReq
     * @return
     */
    private void do_8803(final DmsContext ctx, final Req_8803 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8803;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(dnReq.getMediaType());
            req.dataBuf.writeByte(dnReq.getChannelId());
            req.dataBuf.writeByte(dnReq.getIncidentCode());
            writeTime(req.dataBuf, dnReq.getBeginTime());
            writeTime(req.dataBuf, dnReq.getEndTime());
            req.dataBuf.writeByte(dnReq.getDeleteFlag());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001);
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
                logger.warn("存储多媒体数据上传命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：存储多媒体数据检索
     *
     * @param dnReq
     * @return
     */
    private void do_8802(final DmsContext ctx, final Req_8802 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0802 res = new Res_0802();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8802;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(dnReq.getMediaType());
            req.dataBuf.writeByte(dnReq.getChannelId());
            req.dataBuf.writeByte(dnReq.getIncidentCode());

            if (dnReq.getBeginTime() == null) {
                Date d = new SimpleDateFormat("yy-MM-dd-HH-mm-ss").parse("00-00-00-00-00-00");
                writeTime(req.dataBuf, d);
            } else {
                writeTime(req.dataBuf, dnReq.getBeginTime());
            }

            if (dnReq.getEndTime() == null) {
                Date d = new SimpleDateFormat("yy-MM-dd-HH-mm-ss").parse("00-00-00-00-00-00");
                writeTime(req.dataBuf, d);
            } else {
                writeTime(req.dataBuf, dnReq.getEndTime());
            }
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0802);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(2);
                short num = result.dataBuf.readShort();

                List<MediaParamItem> mediaParamItems = new ArrayList<MediaParamItem>();
                for (int i = 0; i < num; i++) {
                    MediaParamItem mediaParamItem = new MediaParamItem();
                    mediaParamItem.setMediaId(result.dataBuf.readInt());
                    mediaParamItem.setMediaType(result.dataBuf.readByte());
                    mediaParamItem.setChannelId(result.dataBuf.readByte());
                    mediaParamItem.setIncidentCode(result.dataBuf.readByte());

                    GpsVo gpsVo = new GpsVo();
                    gpsVo.setAlarm(result.dataBuf.readInt());
                    gpsVo.setState(result.dataBuf.readInt());
                    gpsVo.setLat(result.dataBuf.readInt());
                    gpsVo.setLon(result.dataBuf.readInt());
                    gpsVo.setAlt(result.dataBuf.readShort());
                    gpsVo.setSpeed(result.dataBuf.readShort());
                    gpsVo.setDir(result.dataBuf.readShort());
                    gpsVo.setGpsTime(readTime(result.dataBuf.readBytes(6)));
                    mediaParamItem.setGps(gpsVo);

                    mediaParamItems.add(mediaParamItem);
                }
                res.setRc(JtsResMsg.RC_OK);
                res.setMediaParamItems(mediaParamItems);
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("摄像头立即拍摄命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：摄像头立即拍摄命令
     *
     * @param dnReq
     * @return
     */
    //TODO:新部标应答协议
    /*private void do_8801(final DmsContext ctx, final Req_8801 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0805 res = new Res_0805();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8801;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(dnReq.getChannelId());
            req.dataBuf.writeShort(dnReq.getShotOrder());
            req.dataBuf.writeShort(dnReq.getShotSpace());
            req.dataBuf.writeByte(dnReq.getSaveFlag());
            req.dataBuf.writeByte(dnReq.getResolution());
            req.dataBuf.writeByte(dnReq.getVideoQuality());
            req.dataBuf.writeShort(dnReq.getBrightness());
            req.dataBuf.writeByte(dnReq.getContrast());
            req.dataBuf.writeByte(dnReq.getSaturation());
            req.dataBuf.writeShort(dnReq.getChroma());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            res.setDid(dnReq.getDid());
            ctx.reply(res);return;
        }

        final SettableFuture<Message> sf = SettableFuture.create();

        ListenableFuture<ProtoMsg> f = tcpChannel().sendRequest(req, (short) 0x0805);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                res.setDid(result.did);
                result.dataBuf.skipBytes(2);
                res.setResult(result.dataBuf.readByte());

                if (res.getResult() == Res_0805.PHOTO_OK) {
                    res.setNum(result.dataBuf.readByte());
                    //res.setMediaIds(result.dataBuf.readBytes(4*res1.getNum()));
                }

                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("摄像头立即拍摄命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                res.setDid(dnReq.getDid());
                ctx.reply(res);
            }
        });

        return;
    }*/

    /**
     * @param dnReq
     * @return
     */
    private void do_8801(final DmsContext ctx, final Req_8801 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8801;
            req.dataBuf = Unpooled.buffer(32);

            req.dataBuf.writeByte(dnReq.getChannelId());
            req.dataBuf.writeShort(dnReq.getShotOrder());
            req.dataBuf.writeShort(dnReq.getShotSpace());
            req.dataBuf.writeByte(dnReq.getSaveFlag());
            req.dataBuf.writeByte(dnReq.getResolution());
            req.dataBuf.writeByte(dnReq.getVideoQuality());
            req.dataBuf.writeByte(dnReq.getBrightness());
            req.dataBuf.writeByte(dnReq.getContrast());
            req.dataBuf.writeByte(dnReq.getSaturation());
            req.dataBuf.writeByte(dnReq.getChroma());

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            res.setRc(JtsResMsg.RC_FAIL);
            res.setVid(dnReq.getVid());
            ctx.reply(res);
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0805, (short) 0x0001);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);

                if (result.msgId == 0x0805) {
                    result.dataBuf.skipBytes(2);
                    byte rc = result.dataBuf.readByte();

                    if (rc == 2) { //2：通道不支持(0x0805)
                        rc = ProtoConstants.RC_NOT_SUPPORT; //3：不支持(0x8001)
                    }

                    res.setRc(rc);
                    sendCenterGeneralRes(result, rc);
                } else {
                    result.dataBuf.skipBytes(4);
                    res.setRc(result.dataBuf.readByte());
                }

                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("摄像头立即拍摄命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }


}

