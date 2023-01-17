package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.InvalidRequestException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.UnsupportedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil.*;


public class TelAndInfoPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(TelAndInfoPH.class);
    private ConcurrentMap<String, Short> vid2ReqSn = new ConcurrentHashMap();
    private ConcurrentMap<String, Integer> vid2AnswerId = new ConcurrentHashMap();

    @Override
    public void setup() {

    }

    @Override
    public void doUpMsg(ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        if (upMsg.msgId == 0x0301) {//事件报告
            do_0301(upMsg);
        } else if (upMsg.msgId == 0x0302) {//提问应答
            do_0302(upMsg);
        } else if (upMsg.msgId == 0x0303) {//信息点播/取消
            do_0303(upMsg);
        } else if (upMsg.msgId == 0x0701) {//电子运单上报
            do_0701(upMsg);
        } else if (upMsg.msgId == 0x0702) {//驾驶员身份信息采集上报
            do_0702(upMsg, taskId, channelProcessor);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    public void doDnReq(Message dnReq, String taskId, ChannelProcessor channelProcessor) {
        if (dnReq instanceof Req_8400) {//电话回拨
            do_8400((Req_8400) dnReq);
        } else if (dnReq instanceof Req_8401) {//设置电话本
            do_8401((Req_8401) dnReq);
        } else if (dnReq instanceof Req_8300) {//文本信息下发
            do_8300((Req_8300) dnReq);
        } else if (dnReq instanceof Req_8301) {//事件设置
            do_8301((Req_8301) dnReq);
        } else if (dnReq instanceof Req_8302) {//提问下发
            do_8302((Req_8302) dnReq);
        } else if (dnReq instanceof Req_8303) {//信息点播菜单设置
            do_8303((Req_8303) dnReq);
        } else if (dnReq instanceof Req_8304) {//信息服务
            do_8304((Req_8304) dnReq);
        } else {
            throw new RuntimeException("未知的请求消息类型: " + dnReq.getClass().getName());
        }
    }


    private void do_0702(final ProtoMsg upMsg, String taskId, ChannelProcessor channelProcessor) {
        DriverCardInforItem cardInfor = new DriverCardInforItem();
        //解析上行请求协议
        try {
            ByteBuf reqBuf = upMsg.dataBuf;
            logger.debug("[{}]驾驶员信息上报采集可用字节数:{}", upMsg.sim, reqBuf.readableBytes());
            reqBuf.markReaderIndex();
            try {
                //走新808新车台解析协议处理
                cardInfor.setStatus(reqBuf.readByte());
                cardInfor.setWorkDate(ProtoUtil.readTime(reqBuf));
                //驾驶员上班
                if (cardInfor.getStatus() == 1) {
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

                    cardInfor.setMsgId("0702");
                    cardInfor.setPtfReceiveTime(System.currentTimeMillis());
                    cardInfor.setVid(upMsg.vid);
                }

                sendCenterGeneralRes(upMsg, ProtoConstants.RC_OK);

                processEvent(JSON.toJSONString(cardInfor), taskId, CodecUtils.shortToHex(upMsg.msgId), Constants.DRIVERCARDINFORMATION_TOPIC, channelProcessor);
            } catch (Exception e) {

                sendCenterGeneralRes(upMsg, ProtoConstants.RC_FAIL);
                logger.error("解析0x0702驾驶员身份信息上报失败");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("[{}]接收到上行请求消息:{}", upMsg.sim, upMsg);
            }

        } catch (Exception e) {
            logger.error("协议解析失败:" + upMsg, e);
            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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
//            sendCenterGeneralRes(upMsg, ProtoConstants.RC_BAD_REQUEST);
            return;
        }

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

        return;
    }

    /**
     * 下行协议：信息服务
     *
     * @param dnReq
     * @return
     */
    private void do_8304(final Req_8304 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：信息点播菜单设置
     *
     * @param dnReq
     * @return
     */
    private void do_8303(final Req_8303 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：提问下发
     *
     * @param dnReq
     * @return
     */
    private void do_8302(final Req_8302 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：事件设置
     *
     * @param dnReq
     * @return
     */
    private void do_8301(final Req_8301 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：电话回拨
     *
     * @param dnReq
     * @return
     */
    private void do_8400(final Req_8400 dnReq) {
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
            return;
        }

    }

    /**
     * 下行协议：文本信息下发
     *
     * @param dnReq
     * @return
     */
    private void do_8300(final Req_8300 dnReq) {
        if (logger.isDebugEnabled()) {
            logger.debug("接收到下行请求:{}", dnReq);
        }

        //请求参数合法性校验
        try {
            Validate.notNull(dnReq.getFlag(), "文本信息标志位不能为空");
            Validate.notNull(dnReq.getText(), "文本信息不能为空");
        } catch (Exception e) {
            logger.error("文本信息验证失败", e);
        }

        ProtoMsg req = new ProtoMsg();

        try {
            req.vid = dnReq.getVid();
            req.msgId = (short) 0x8300;
            req.dataBuf = Unpooled.buffer(32);
            req.dataBuf.writeByte(dnReq.getFlag());
            writeString(req.dataBuf, dnReq.getText());
        } catch (Exception e) {
            logger.warn("封装下行请求失败:{}\n{}", dnReq, Throwables.getStackTraceAsString(e));
            return;
        }

        ListenableFuture<ProtoMsg> f = sendRequest(req, ProtoConstants.TERMINAL_GENERAL_RES);
        Futures.addCallback(f, new FutureCallback<ProtoMsg>() {
            @Override
            public void onSuccess(ProtoMsg result) {
                result.dataBuf.skipBytes(4);
                logger.info("文本信息下发状态“：" + result.dataBuf.readByte());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("文本信息下发失败", t);
            }
        });

    }

    /**
     * 下行协议：设置电话本
     *
     * @param dnReq
     * @return
     */
    private void do_8401(final Req_8401 dnReq) {
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
            return;
        }
    }
}
