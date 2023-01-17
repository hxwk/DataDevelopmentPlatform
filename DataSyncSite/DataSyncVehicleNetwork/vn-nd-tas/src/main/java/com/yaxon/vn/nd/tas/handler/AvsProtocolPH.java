package com.yaxon.vn.nd.tas.handler;

import com.yaxon.vn.nd.tas.avs.AvsLink;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.ProtoUtil;
import com.yaxon.vndp.common.util.SpringContextUtil;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 * Author: Sun Zhen
 * Time: 2014-01-20 15:54
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
@Component
public class AvsProtocolPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(AvsProtocolPH.class);

    private short[] dnReqIds = new short[] {
            (short)0x9101,(short)0x9102,(short)0x9105,(short)0x9201,(short)0x9202,
            (short)0x9205,(short)0x9206,(short)0x9207,(short)0x9301,(short)0x9302,
            (short)0x9303,(short)0x9304,(short)0x9305,(short)0x9306,(short)0x9401
    };

    private AvsLink avsLink;

    private AvsLink avs() {
        if (avsLink == null) {
            try {
                avsLink = (AvsLink) SpringContextUtil.getBean("avs");
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        return avsLink;
    }

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        try {
            if (upMsg.msgId == 0x0001) {
                ByteBuf dataBuf = upMsg.dataBuf;
                int readerIndex = dataBuf.readerIndex();
                short reqId = dataBuf.getShort(readerIndex + 2);
                for (short dnReqId : dnReqIds) {
                    if (reqId == dnReqId) {
                        sendMessage(upMsg);
                        break;
                    }
                }
            } else {
                sendMessage(upMsg);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage(ProtoMsg msg) {
        try {
            AvsLink avs = avs();
            if (avs != null) {
                avs.sendMessage(msg);
            }
        } catch (Exception e) {
            logger.warn("往视频服务器发送消息失败");
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {
    }

    public void doTerminalLogin(String sim, String authCode) {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = sim;
        msg.msgId = 0x0102;
        msg.dataBuf = Unpooled.buffer();
        ProtoUtil.writeString(msg.dataBuf, authCode);

        sendMessage(msg);
    }

    public void doTerminalLogout(String sim) {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = sim;
        msg.msgId = 0x0003;
        msg.dataBuf = Unpooled.buffer(0);

        sendMessage(msg);
    }
}
