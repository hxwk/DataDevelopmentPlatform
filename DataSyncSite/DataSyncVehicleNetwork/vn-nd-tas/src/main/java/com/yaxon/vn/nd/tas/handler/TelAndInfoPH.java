package com.yaxon.vn.nd.tas.handler;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.yaxon.vn.nd.tas.exception.InvalidRequestException;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static com.yaxon.vn.nd.tas.util.ProtoUtil.*;

/**
 * Author: Sun Zhen
 * Time: 2013-11-27 14:06
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 * @modify: JianKang
 * @modifyTime: 2018/03/02
 */
@Component
public class TelAndInfoPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TelAndInfoPH.class);
    private ConcurrentMap<String, Short> vid2ReqSn = new ConcurrentHashMap();
    private ConcurrentMap<String, Integer> vid2AnswerId = new ConcurrentHashMap();

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0301) {//事件报告
            do_0301(upMsg);
        } else if (upMsg.msgId == 0x0302) {//提问应答
            do_0302(upMsg);
        } else if (upMsg.msgId == 0x0303) {//信息点播/取消
            do_0303(upMsg);
        } else if (upMsg.msgId == 0x0701) {//电子运单上报
            do_0701(upMsg);
        } else if (upMsg.msgId == 0x0702) {//驾驶员身份信息采集上报
            do_0702(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
        if (dnReq instanceof Req_8400) {//电话回拨
            do_8400(ctx, (Req_8400) dnReq);
        } else if (dnReq instanceof Req_8401) {//设置电话本
            do_8401(ctx, (Req_8401) dnReq);
        } else if (dnReq instanceof Req_8300) {//文本信息下发
            do_8300(ctx, (Req_8300) dnReq);
        } else if (dnReq instanceof Req_8301) {//事件设置
            do_8301(ctx, (Req_8301) dnReq);
        } else if (dnReq instanceof Req_8302) {//提问下发
            do_8302(ctx, (Req_8302) dnReq);
        } else if (dnReq instanceof Req_8303) {//信息点播菜单设置
            do_8303(ctx, (Req_8303) dnReq);
        } else if (dnReq instanceof Req_8304) {//信息服务
            do_8304(ctx, (Req_8304) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }


    private void do_0702(final ProtoMsg upMsg) {
        logger.info("原始数据包进入>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Req_0702Q q = new Req_0702Q();
        DriverCardInforItem cardInfor = new DriverCardInforItem();
        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            logger.debug("[{}]驾驶员信息上报采集可用字节数:{}", upMsg.sim, reqBuf.readableBytes());
            try {
                //走新808新车台解析协议处理
                q.setVid(upMsg.vid);
                q.setSim(upMsg.sim);
                cardInfor.setId(UUID.randomUUID().toString());
                cardInfor.setVid(upMsg.vid);
                cardInfor.setMsgId("0702");
                //IC卡插入或拔出状态
                byte status = reqBuf.readByte();
                cardInfor.setStatus(status);
                logger.debug(" IC work status:{} ",status);
                ByteBuf time = reqBuf.readBytes(6);
                cardInfor.setWorkDate(ProtoUtil.readTime(time));
                //驾驶员上班
                if (1 == status) {
                    byte icResultCode = reqBuf.readByte();
                    cardInfor.setIcResult(icResultCode);
                    switch (icResultCode){
                        case 1:
                            cardInfor.setIcResultReason("读卡失败,卡片密钥认证未通过");
                            break;
                        case 2:
                            cardInfor.setIcResultReason("读卡失败,卡片已被锁定");
                            break;
                        case 3:
                            cardInfor.setIcResultReason("读卡失败,卡片被拔出");
                            break;
                        case 4:
                            cardInfor.setIcResultReason("读卡失败,数据校验错误");
                            break;
                        case 0:
                            cardInfor.setIcResultReason("IC卡读卡成功");
                            break;
                        default:
                            cardInfor.setIcResultReason("未知的IC结果");
                            break;
                    }
                    if(0 == icResultCode) {
                        //姓名长度
                        byte nameLength = reqBuf.readByte();
                        cardInfor.setName(readString(reqBuf, nameLength));
                        cardInfor.setPractitionerIdCard(readString(reqBuf, 20));
                        cardInfor.setIDCard(cardInfor.getPractitionerIdCard());
                        byte institutionLength = reqBuf.readByte();
                        cardInfor.setPractitionerIdCardInstitution(readString(reqBuf, institutionLength));
                        cardInfor.setValidPeriod(ProtoUtil.readTime2(reqBuf));
                    }
                }
            } catch (Exception e) {
                logger.error("解析0x0702驾驶员身份信息上报失败");
            }
            q.setDriverCardInforItem(cardInfor);
            logger.debug(" 驾驶员身份信息:{} ",q.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.debug("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0701(final ProtoMsg upMsg) {
        Req_0701 q = new Req_0701();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;

            q.setVid(upMsg.vid);
            Integer dataLen = reqBuf.readInt();
            q.setContent(readString(reqBuf, dataLen));
            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.debug("协议解析失败:" + upMsg, e);
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
                logger.debug("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0303(final ProtoMsg upMsg) {
        Req_0303 q = new Req_0303();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setInfoType(reqBuf.readByte());
            q.setFlag(reqBuf.readByte());
            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.error("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    private void do_0302(final ProtoMsg upReq) {
        Req_0302 q = new Req_0302();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upReq.dataBuf;
            q.setSn(reqBuf.readShort());

            if (!q.getSn().equals(vid2ReqSn.get(upReq.vid))) {
                q.setFlag((byte)1);
            }else{
                q.setFlag((byte)0);
            }
            q.setId(reqBuf.readByte());
            q.setVid(upReq.vid);
            q.setQuestionId(vid2AnswerId.get(upReq.vid));

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upReq.sim, q);
            }
        } catch (Exception e) {
            throw new InvalidRequestException("上行请求解析失败", e);
        }

        final SettableFuture<ProtoMsg> sf = SettableFuture.create();


        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);

        //异步回调处理
        Futures.addCallback(f, new FutureCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                Res_8001 r = (Res_8001)result;
                //ProtoMsg res = buildCenterGeneralResMsg(r.getVid(),upReq.msgId, upReq.sn, r.getRc());
                sendCenterGeneralRes(upReq, r.getRc());
                //sf.set(res);
            }

            @Override
            public void onFailure(Throwable t) {
                sf.setException(t);
            }
        });
        return;
    }

    private void do_0301(final ProtoMsg upMsg) {
        Req_0301 q = new Req_0301();

        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            q.setId(reqBuf.readUnsignedByte());
            q.setVid(upMsg.vid);

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, q);
            }
        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
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
                logger.error("协议处理失败:" + upMsg, t);
                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
            }
        });
        return;
    }

    /**
     * 下行协议：信息服务
     *
     * @param dnReq
     * @return
     */
    private void do_8304(final DmsContext ctx, final Req_8304 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8304;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getType());
            writeU16String(req.dataBuf, dnReq.getContent());
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
                logger.warn("信息服务失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：信息点播菜单设置
     *
     * @param dnReq
     * @return
     */
    private void do_8303(final DmsContext ctx, final Req_8303 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8303;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getType());

            if(dnReq.getType() == 0){   //0 表示选择的设置类型为删除所有

            }else{
                int infoNum = dnReq.getParamItems().size();
                Validate.isTrue(infoNum <= 255, "单次设置的信息项不能超过255个");

                req.dataBuf.writeByte(infoNum);

                for (int i = 0; i < infoNum; i++) {
                    req.dataBuf.writeByte(dnReq.getParamItems().get(i).getParamType());
                    String[] infos = dnReq.getParamItems().get(i).getParamVal().split("&,&");
                    String name = infos[0]; //点播名称
                    writeU16String(req.dataBuf, name);
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
                logger.warn("信息点播菜单设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：提问下发
     *
     * @param dnReq
     * @return
     */
    private void do_8302(final DmsContext ctx, final Req_8302 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8302;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getFlag());
            writeU8String(req.dataBuf, dnReq.getQuestion());

            for (int i = 0; i < dnReq.getParamItems().size(); i++) {
                req.dataBuf.writeByte(dnReq.getParamItems().get(i).getParamType());
                writeU16String(req.dataBuf, dnReq.getParamItems().get(i).getParamVal());
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
                Short reqSn = result.dataBuf.readShort();
                vid2ReqSn.put(result.vid, reqSn);
                vid2AnswerId.put(result.vid, dnReq.getQuestionId());
                result.dataBuf.skipBytes(2);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("提问下发失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：事件设置
     *
     * @param dnReq
     * @return
     */
    private void do_8301(final DmsContext ctx, final Req_8301 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8301;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getSettingType());

            if (dnReq.getSettingType() == 0) {

            } else {
                int incidentNum = dnReq.getParamItems().size();
                Validate.isTrue(incidentNum <= 255, "单次设置的事件项不能超过255个");

                req.dataBuf.writeByte(dnReq.getParamItems().size());

                for (int i = 0; i < incidentNum; i++) {
                    req.dataBuf.writeByte(dnReq.getParamItems().get(i).getParamType());
                    if (dnReq.getSettingType() == 4) {
                        req.dataBuf.writeByte(0x0);
                    } else {
                        writeU8String(req.dataBuf, dnReq.getParamItems().get(i).getParamVal());
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
                res.setVid(result.vid);
                result.dataBuf.skipBytes(4);
                res.setRc(result.dataBuf.readByte());
                ctx.reply(res);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("事件设置失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：电话回拨
     *
     * @param dnReq
     * @return
     */
    private void do_8400(final DmsContext ctx, final Req_8400 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8400;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getFlag());
            writeString(req.dataBuf, dnReq.getTel());
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
                logger.warn("电话回拨下发失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：文本信息下发
     *
     * @param dnReq
     * @return
     */
    private void do_8300(final DmsContext ctx, final Req_8300 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8300;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getFlag());
            writeString(req.dataBuf, dnReq.getText());
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
                logger.warn("文本信息下发失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                res.setErrMsg(t.getCause().getMessage());
                ctx.reply(res);
            }
        });
    }

    /**
     * 下行协议：设置电话本
     *
     * @param dnReq
     * @return
     */
    private void do_8401(final DmsContext ctx, final Req_8401 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        ProtoMsg req = new ProtoMsg();
        final Res_0001 res = new Res_0001();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8401;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getType());

            if (dnReq.getContactParamItems() != null) {
                int contactNum = dnReq.getContactParamItems().size();
                Validate.isTrue(contactNum <= 255, "单次设置的联系人太多");

                req.dataBuf.writeByte(contactNum);
                for (ContactParamItem contactParamItem : dnReq.getContactParamItems()) {
                    req.dataBuf.writeByte(contactParamItem.getFlag());
                    writeU8String(req.dataBuf, contactParamItem.getTel());
                    writeU8String(req.dataBuf, contactParamItem.getContact());
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
                logger.warn("设置电话本失败", t);
                res.setRc(JtsResMsg.RC_FAIL);
                res.setVid(dnReq.getVid());
                ctx.reply(res);
            }
        });
    }
}
