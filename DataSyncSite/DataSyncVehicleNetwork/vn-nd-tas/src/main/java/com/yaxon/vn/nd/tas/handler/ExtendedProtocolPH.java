package com.yaxon.vn.nd.tas.handler;

/**
 * Author: 程行荣
 * Time: 2014-01-08 15:56
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vn.nd.tas.exception.UnsupportedProtocolException;
import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tbp.si.Req_0102;
import com.yaxon.vndp.dms.DmsContext;
import com.yaxon.vndp.dms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.yaxon.vn.nd.redis.RedisConstants.GK_VEHICLE_STATE;
import static com.yaxon.vn.nd.redis.RedisConstants.IM_T8_0102;
import static com.yaxon.vn.nd.redis.RedisConstants.IM_T8_0102_SIM;


/**
 * 扩展协议，协议号对应0000
 */

@Component
public class ExtendedProtocolPH extends BaseProtoHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedProtocolPH.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{configProperties['dms.nodeId']}")
    private String nodeId;

    @Autowired
    private AvsProtocolPH avsProtocolPH;

    @Override
    protected void doUpMsg(ProtoMsg upMsg) {
        if (upMsg.msgId == 0x0000) {
            short extId = 0;
            try {
                extId = upMsg.dataBuf.readShort();
            } catch (Exception e) {
                throw new UnsupportedProtocolException("解析扩展协议(0x0000)类型失败");
            }

            if (extId == 0x0102) {//终端下线
                do_ex_0102(upMsg);
            } else if (extId == 0x0103) {//超时触发终端下线
                do_ex_trik_0102(upMsg);
            } else {
                throw new UnsupportedProtocolException("未知的扩展协议：extId=" + extId);
            }
        }  else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.msgId);
        }
    }

    @Override
    protected void doDnReq(DmsContext ctx, Message dnReq) {

    }

    private void do_ex_0102(final ProtoMsg upMsg) {
        //负载情况下解决车辆在线信息冲突问题，判断redis保存的前置机节点是否当前节点，为否不做业务处理
        String dmsNodeId = redisTemplate.opsForValue().get(GK_VEHICLE_STATE+upMsg.vid);
        if(dmsNodeId!=null) {
            if (!dmsNodeId.equals(nodeId)) {
                return;
            }
        }
        Req_0102 q = new Req_0102();
        q.setVid(upMsg.vid);
        q.setSim(upMsg.sim);
        q.setLogFlag(Req_0102.LOG_OUT);

        try {
            redisTemplate.delete(GK_VEHICLE_STATE + upMsg.vid);
            redisTemplate.convertAndSend(IM_T8_0102 + upMsg.vid, "0");
            redisTemplate.convertAndSend(IM_T8_0102_SIM + upMsg.sim, "0");
        } catch (Exception e) {
            logger.warn("在Redis中更新车辆状态失败", e);
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);
        //不关心处理结果

        avsProtocolPH.doTerminalLogout(upMsg.sim);
    }

    private void do_ex_trik_0102(final ProtoMsg upMsg) {
        //负载情况下解决车辆在线信息冲突问题，判断redis保存的前置机节点是否当前节点，为否不做业务处理
        String dmsNodeId = redisTemplate.opsForValue().get(GK_VEHICLE_STATE+upMsg.vid);
        if(dmsNodeId!=null) {
            if (!dmsNodeId.equals(nodeId)) {
                return;
            }
        }
        Req_0102 q = new Req_0102();
        q.setVid(upMsg.vid);
        q.setSim(upMsg.sim);
        q.setLogFlag(Req_0102.LOG_OUT_TRIK);

        try {
            redisTemplate.delete(GK_VEHICLE_STATE + upMsg.vid);
            redisTemplate.convertAndSend(IM_T8_0102 + upMsg.vid, "0");
            redisTemplate.convertAndSend(IM_T8_0102_SIM + upMsg.sim, "0");
        } catch (Exception e) {
            logger.warn("在Redis中更新车辆状态失败", e);
        }

        //发送tbp（终端业务处理模块）处理
        ListenableFuture<Message> f = tbp().call(q);
        //不关心处理结果

        avsProtocolPH.doTerminalLogout(upMsg.sim);
    }

}
