package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.bean.MediaMeta;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.db.DBCommon;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.SubPackMissingException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.FastDFSHandler;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.readTime;
import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.writeTime;

/**
 */
public class MultimediaPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(MultimediaPH.class);

    private DB mapdb = null;

    private ConcurrentNavigableMap<Integer, byte[]> packStore;
    private AtomicInteger packIdGenerator = new AtomicInteger(0);
    private ConcurrentMap<MediaPackKey, MediaPackItem> mediaPackCache = Maps.newConcurrentMap();
    private Queue<MediaPack> mpQueue = Queues.newConcurrentLinkedQueue();


    private Thread cleanupThread = new Thread("media-cleanup-thread") {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);

                    for (MediaPackKey mpk : mediaPackCache.keySet()) {
                        MediaPackItem mpi = mediaPackCache.get(mpk);
                        if (mpi != null &&
                                System.currentTimeMillis() - mpi.firstPackTime > 20000 * mpi.totalPacks) {
                            if (mpi.resend > 0) {
                                mpi = mediaPackCache.remove(mpk);
                                if (mpi != null) {
                                    for (Integer v : mpi.receivedPacks.values()) {
                                        packStore.remove(v);
                                    }
                                }
                                logger.info("多媒体数据接收超时,将从缓存清除已接收的分包:vid={},sn={}", mpk.vid, mpk.sn);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Throwable t) {
                    logger.warn("", t);
                }
            }
        }
    };
    private Thread uploadMediaThread = new Thread("upload-media-thread") {
        @Override
        public void run() {
            int errCount = 0;
            int sucCount = 0;
            long lastUpTime = System.currentTimeMillis();
            while (true) {
                try {
                    MediaPack mp = mpQueue.poll();
                    File mf = null;
                    File target = null;
                    MediaPackKey mpk = null;
                    MediaPackItem mpi = null;

                    if (mp != null) {
                        try {
                            MediaMeta mm = mp.mm;
                            mpk = mp.mpk;
                            mpi = mp.mpi;
                            mf = readMediaData(mm, mpk, mpi);
                            uploadFile(mm.getFileKey(), mf, mm);
                            mm.setFileKeyReal(mm.getFileKey());

                            //执行成功后清除缓存
                            mediaPackCache.remove(mpk);
                            for (Integer v : mpi.receivedPacks.values()) {
                                packStore.remove(v);
                            }
                            logger.info("文件路径：" + mm.getFileKey());
                        } catch (SubPackMissingException e) {
                            mediaPackCache.remove(mpk);
                            for (Integer v : mpi.receivedPacks.values()) {
                                packStore.remove(v);
                            }
                            logger.error("缓存在mapdb中的多媒体分包数据丢失，清除相关缓存");
                        } catch (Exception e) {
                            mpQueue.offer(mp);
                            errCount++;
                            logger.error("", e);
                            e.printStackTrace();
                        } finally {
                            if (mf != null) {
                                FileUtils.deleteQuietly(mf);
                            }
                            if(target != null) {
                                FileUtils.deleteQuietly(target);
                            }
                        }
                        /** 队列里有数据睡眠300毫秒3种情况：1、持续3次上传失败 2、持续10次上传成功 3、持续3次上传成功，总时间持续消耗30秒
                         *   防止文件服务器如果挂掉了，文件一直持续不断的上传，从而降低服务器系统资源的消耗
                         */
                        if ((errCount) > 3 || sucCount > 10 || (sucCount > 3 && System.currentTimeMillis() - lastUpTime > 30000)) {
                            errCount = 0;
                            sucCount = 0;
                            lastUpTime = System.currentTimeMillis();
                            logger.debug("多媒体上传开始睡眠300毫秒");
                            Thread.sleep(300);
                        }
                        sucCount++;
                    } else {
                        errCount = 0;
                        sucCount = 0;
                        lastUpTime = System.currentTimeMillis();
                        logger.debug("多媒体上传开始睡眠300毫秒");
                        Thread.sleep(300);
                    }
                } catch (Exception e) {
                    logger.info("出现异常...........");
                    e.printStackTrace();
                    break;
                } catch (Throwable t) {
                    logger.warn("", t);
                }
            }
        }
    };

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8801) {//摄像头立即拍摄命令
            do_8801((Req_8801) dnReq);
        } else if (dnReq instanceof Req_8802) {//存储多媒体数据检索
            do_8802((Req_8802) dnReq);
        } else if (dnReq instanceof Req_8803) {//存储多媒体数据上传命令
            do_8803((Req_8803) dnReq);
        } else if (dnReq instanceof Req_8804) {//录音开始命令
            do_8804((Req_8804) dnReq);
        } else if (dnReq instanceof Req_8805) {//单条存储多媒体数据检索上传命令
            do_8805((Req_8805) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
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

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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

            try {
                MediaPackKey mpk = null;
                MediaPackItem mpi = null;
                synchronized (this) {
                    int sn = (q.getSn() & 0xFFFF);
                    if (q.getPackIndex() > 1) {
                        sn -= (q.getPackIndex() - 1); //按照第一包的序列号作为整包的序列号
                        sn = (sn & 0xFFFF); //大于65535时，从0开始计算
                    }
                    mpk = new MediaPackKey(q.getVid(), sn);
                    mpi = mediaPackCache.get(mpk);
                    if (mpi == null) {
                        mpi = new MediaPackItem(q.getPackCount(), System.currentTimeMillis());
                        MediaPackItem v = mediaPackCache.putIfAbsent(mpk, mpi);
                        if (v != null) {
                            mpi = v;
                        }
                    }

                    Integer oid = packIdGenerator.incrementAndGet();
                    if (mpi.receivedPacks.putIfAbsent(q.getPackIndex(), oid) != null) {
                        logger.info("接收到重复的多媒体数据包:vid={},packIndex={}", q.getVid(), q.getPackIndex());
                    } else {
                        packStore.put(oid, q.getData());
                    }
                }

                ProtoMsg res = null;
                res = new ProtoMsg();
                res.msgId = (short) 0x8800;
                res.sim = upMsg.sim;
                res.vid = upMsg.vid;
                res.dataBuf = Unpooled.buffer(5);

                if (mpi.receivedPacks.size() != mpi.totalPacks) { //只收到部分分包
                    res.dataBuf.writeInt(0);
                    sendMessage(res);
                    logger.debug("只收到部分分包");
                } else { //所有分包都已收到
                    MediaMeta mm = readMediaMeta(mpk, mpi);
                    mm.setVid(q.getVid());
                    mm.setSim(q.getSim());
                    MediaPack mp = new MediaPack(mpk, mpi, mm);

                    res.dataBuf.writeInt(mm.getMediaId());

                    res.dataBuf.writeByte(0);

                    sendMessage(res);

                    mpQueue.offer(mp);
                }

                sendCenterGeneralRes(upMsg, JtsResMsg.RC_OK);
            } catch (Exception e) {
                logger.warn("消息处理异常：{} : {}", q, Throwables.getStackTraceAsString(e));

                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }

        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            return;
        }

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

            sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);
        } catch (Exception e) {
            logger.info("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

        return;
    }

    /**
     * 下行协议：单条存储多媒体数据检索上传命令
     *
     * @param dnReq
     * @return
     */
    private void do_8805(final Req_8805 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：录音开始命令
     *
     * @param dnReq
     * @return
     */
    private void do_8804(final Req_8804 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：存储多媒体数据上传命令
     *
     * @param dnReq
     * @return
     */
    private void do_8803(final Req_8803 dnReq) {
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
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0001);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("存储多媒体数据上传命令失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
            }
        });

    }

    /**
     * 下行协议：存储多媒体数据检索
     *
     * @param dnReq
     * @return
     */
    private void do_8802(final Req_8802 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();

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
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0802);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
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
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("摄像头立即拍摄命令失败", t);
            }
        });

    }


    /**
     * @param dnReq
     * @return
     */
    private void do_8801(final Req_8801 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        //请求参数合法性校验
        try {
            Validate.notNull(dnReq.getChannelId(), "通道ID不能为空");
            Validate.notNull(dnReq.getShotOrder(), "拍摄命令不能为空");
            Validate.notNull(dnReq.getShotSpace(), "拍摄间隔/录像时间不能为空");
            Validate.notNull(dnReq.getSaveFlag(), "保存标志不能为空");
            Validate.notNull(dnReq.getResolution(), "分辨率不能为空");
            Validate.notNull(dnReq.getVideoQuality(), "视频、图片质量不能为空");
            Validate.notNull(dnReq.getBrightness(), "亮度不能为空");
            Validate.notNull(dnReq.getContrast(), "对比度不能为空");
            Validate.notNull(dnReq.getSaturation(), "饱和度不能为空");
            Validate.notNull(dnReq.getChroma(), "色度不能为空");
        } catch (Exception e) {
            logger.error("拍照参数不合法");
            return ;
        }

        ProtoMsg req = new ProtoMsg();

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

            ListenableFuture<ProtoMsg> f = sendRequest(req, (short) 0x0805, (short) 0x0001);
            Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
                @Override
                public void onSuccess(ProtoMsg result) {

                    if (result.msgId == 0x0805) {
                        result.dataBuf.skipBytes(2);
                        byte rc = result.dataBuf.readByte();

                        if (rc == 2) { //2：通道不支持(0x0805)
                            rc = ProtoConstants.RC_NOT_SUPPORT; //3：不支持(0x8001)
                        }

                        sendCenterGeneralRes(result, rc);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    logger.warn("摄像头立即拍摄命令失败", t);
                }
            });

        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            return;
        }

    }

    private MediaMeta readMediaMeta(MediaPackKey mpk, MediaPackItem mpi) throws Exception {
        try {

//            logger.info(" mpi = " + mpi);

            int bytes = 0;
            List<byte[]> packs = Lists.newLinkedList();
            for (int i = 1; i <= mpi.totalPacks; i++) {
                Integer oid = mpi.receivedPacks.get(i);
                byte[] packData = packStore.get(oid);
                if (packData == null) {
                    throw new Exception("缓存在mapdb中的多媒体分包数据丢失");
                }
                bytes += packData.length;
                packs.add(packData);
                if (bytes >= 36) {//固定部分
                    break;
                }
            }

            if (bytes < 36) {//
                throw new RuntimeException("多媒体数据总长度<36字节");
            }

            ByteBuf buf = Unpooled.wrappedBuffer(packs.toArray(new byte[packs.size()][]));
            MediaMeta mm = new MediaMeta();
            mm.setMediaId(buf.readInt());
            mm.setMediaType(buf.readByte());
            mm.setMediaFormat(buf.readByte());
            mm.setEventCode(buf.readByte());
            mm.setChannelId(buf.readByte());

            GpsVo gps = new GpsVo();
            gps.setAlarm(buf.readInt());
            gps.setState(buf.readInt());
            gps.setLat(buf.readInt());
            gps.setLon(buf.readInt());
            gps.setAlt(buf.readShort());
            gps.setSpeed(buf.readShort());
            gps.setDir(buf.readShort());
//            gps.setGpsTime(ProtoUtil.readTime(buf.readBytes(6)));
            gps.setGpsTime(readTime(buf.readBytes(6)));
            mm.setGps(gps);
            return mm;
        } catch (Exception e) {
            throw new Exception("解析多媒体元信息失败", e);
        }
    }

    private File readMediaData(MediaMeta mediaMeta, MediaPackKey mpk, MediaPackItem mpi) throws Exception {
        FileOutputStream fos = null;
        File f = null;
        try {
            String randomSeq = RandomStringUtils.randomAlphanumeric(5);
            String fn = mediaMeta.getSim() + "_" + LocalDateTime.now().toString("yyMMddhhmmss") + randomSeq
                    + getMediaFormat(mediaMeta);
            f = new File(Constants.tmpDir + "/" + fn);
//            logger.info(" fn = " + fn);
            mediaMeta.setFileKey(fn);

            if (mediaMeta.getMediaType().equals(Byte.valueOf(1 + "")) && (!mediaMeta.getMediaFormat().equals(Byte.valueOf(2 + "")))) {
                String fnReal = mediaMeta.getSim() + "_" + LocalDateTime.now().toString("yyMMddhhmmss") + randomSeq
                        + "_r.mp3";
                mediaMeta.setFileKeyReal(fnReal);
            }

            fos = FileUtils.openOutputStream(f);
            int bytes = 0;
            for (int i = 1; i <= mpi.totalPacks; i++) {
                Integer oid = mpi.receivedPacks.get(i);
//                logger.info("接收到的信息oid：{}", oid);
                byte[] packData = packStore.get(oid);
                if (packData == null) {
                    throw new SubPackMissingException("缓存在mapdb中的多媒体分包数据丢失，其接收到的信息oid：{}" + oid);
                }

                if (bytes <= 36) {
                    if (bytes + packData.length > 36) {
                        fos.write(packData, 36 - bytes, bytes + packData.length - 36);
                    }
                    bytes += packData.length;
                } else {
                    fos.write(packData);
                }
            }
            fos.flush();

            return f;
        } catch (SubPackMissingException e) {
            throw e;
        } catch (Exception e) {
            if (fos != null) {
                fos.close();
                fos = null;
            }
            if (f != null) {
                FileUtils.deleteQuietly(f);
            }
            throw new Exception("提取多媒体数据到临时文件失败", e);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    private String getMediaFormat(MediaMeta mediaMeta) {
        String f = null;
        switch (mediaMeta.getMediaFormat()) {
            case MediaMeta.MF_JPEG:
                f = ".jpg";
                break;
            case MediaMeta.MF_TIF:
                f = ".tif";
                break;
            case MediaMeta.MF_MP3:
                f = ".mp3";
                break;
            case MediaMeta.MF_WAV:
                f = ".wav";
                break;
            case MediaMeta.MF_WMV:
                f = ".wmv";
                break;
            default:
                if (mediaMeta.getMediaType() == MediaMeta.MT_IMAGE) {
                    f = ".0";
                } else if (mediaMeta.getMediaType() == MediaMeta.MT_AUDIO) {
                    f = ".1";
                } else if (mediaMeta.getMediaType() == MediaMeta.MT_VIDIO) {
                    f = ".2";
                } else {
                    f = "";
                }
                break;
        }
        return f;
    }

    private void uploadFile(String key, File file, MediaMeta mm) throws IOException {
        try {

            //1 上次文件到fastdfs
            String path = FastDFSHandler.getInstance().write2FastDFSServer(file.getAbsolutePath());

            //2 将文件信息存入数据库
            if (save2Db(path, mm)) {
                file.delete();
            }


        } catch (Exception e) {
            throw new IOException("往文件服务器上传文件异常", e);
        }
    }

    private boolean save2Db(String path, MediaMeta mm) {

        String message = "多媒体信息插入数据库失败";

        Connection conn = null;
        PreparedStatement statement = null;
        int result = 0;
        try {
            StringBuilder buf = new StringBuilder();
            buf.append(" insert SSI_MEDIASTOREINFO (id, vid, sim, gpsTime, path, state, alarm, lat, lon, alt, speed, ");
            buf.append("mediaId, mediaType, mediaFormat, eventCode, ChannelId) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
            buf.append(" ?, ?, ?, ?, ?, ?) ");

            System.out.println("开始获取数据库连接");
//            conn = DBPoolDruidCommon.getInstance().getConnection(Constants.VNND_DATASOURCE_ID);
            Class.forName(Constants.DB_VNND_DRIVER);
            conn = DriverManager.getConnection(Constants.DB_VNND_URL, Constants.DB_VNND_USRENAME, Constants.DB_VNND_PASSWORD);
            statement = conn.prepareStatement(buf.toString());
            statement.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
            statement.setString(2, mm.getVid());
            statement.setString(3, mm.getSim());
            statement.setTimestamp(4, new java.sql.Timestamp(mm.getGps().getGpsTime().getTime()));
            statement.setString(5, path);
            statement.setInt(6, mm.getGps().getState());
            statement.setInt(7, mm.getGps().getAlarm());
            statement.setInt(8, mm.getGps().getLat());
            statement.setInt(9, mm.getGps().getLon());
            statement.setInt(10, mm.getGps().getAlt());
            statement.setInt(11, mm.getGps().getSpeed());
            statement.setInt(12, mm.getMediaId());
            statement.setByte(13, mm.getMediaType());
            statement.setByte(14, mm.getMediaFormat());
            statement.setByte(15, mm.getEventCode());
            statement.setByte(16, mm.getChannelId());

            logger.debug(" statement.toString() = " + statement.toString());
            result = statement.executeUpdate();

            if (result > 0) {
                message = "多媒体信息插入数据库成功";
            }

        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            try {
                DBCommon.close(conn, statement, null);
            } catch (SQLException e) {
                logger.error(null, e);
            }
        }

        logger.info(message);

        return result > 0;
    }

    @Override
    public void setup() {
        File dir = new File(Constants.tmpDir);
        try {
            FileUtils.forceMkdir(dir);
            FileUtils.cleanDirectory(dir);

            mapdb = DBMaker.newFileDB(new File(Constants.tmpDir + "/mapdb-media.db"))
                    .transactionDisable()
                    .asyncWriteFlushDelay(500)
                    .closeOnJvmShutdown()
                    .deleteFilesAfterClose()
                    .make();
            packStore = mapdb.getTreeMap("media");
            cleanupThread.setDaemon(true);
            cleanupThread.start();
            uploadMediaThread.setDaemon(true);
            uploadMediaThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MediaPackKey {
        String vid;
        int sn;

        private MediaPackKey(String vid, int sn) {
            this.vid = vid;
            this.sn = sn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MediaPackKey that = (MediaPackKey) o;

            if (sn != that.sn) return false;
            if (!vid.equals(that.vid)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = vid.hashCode();
            result = 31 * result + sn;
            return result;
        }
    }

    class MediaPackItem {
        int totalPacks = 0; //总包数
        long firstPackTime; //接收到第一包数据的时间
        byte resend = 0; //发起重传次数
        ConcurrentMap<Integer, Integer> receivedPacks = new ConcurrentHashMap<Integer, Integer>();

        private MediaPackItem(int totalPacks, long firstPackTime) {
            this.totalPacks = totalPacks;
            this.firstPackTime = firstPackTime;
        }
    }

    class MediaPack {

        MediaMeta mm;
        MediaPackKey mpk;
        MediaPackItem mpi;
        String cfp;//发送平台

        private MediaPack(MediaPackKey mpk, MediaPackItem mpi, MediaMeta mm) {
            this.mpk = mpk;
            this.mpi = mpi;
            this.mm = mm;
        }

        public String getCfp() {
            return cfp;
        }

        public void setCfp(String cfp) {
            this.cfp = cfp;
        }
    }


}

