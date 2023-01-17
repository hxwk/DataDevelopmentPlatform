package com.dfssi.dataplatform.datasync.plugin.interceptor.decode;

import com.dfssi.dataplatform.datasync.plugin.interceptor.common.MsgCodeConstant;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 808 protocol(808) interceptor and parse to event to be comprehensible
 * protocol decoder body by protocolType
 * @author jianKang
 * @date 2017/12/14
 */
public class ProtocolDecoder {
    static final Logger logger = LoggerFactory.getLogger(ProtocolDecoder.class);

    private final static int allocSize = 1024;

    private X0200Decoder x0200Decoder;
    private X0704Decoder x0704Decoder;
    private X0705Decoder x0705Decoder;
    private X0702Decoder x0702Decoder;

    public byte[] interceptBody(String msgID,ProtoMsg upMsg){
        byte[] afterInterceptBody = null;
        x0200Decoder = new X0200Decoder();
        x0704Decoder = new X0704Decoder();
        x0705Decoder = new X0705Decoder();
        x0702Decoder = new X0702Decoder();
        if(msgID.equals(MsgCodeConstant.x0200)){
            afterInterceptBody = x0200Decoder.do_0200(upMsg);
        }else if(msgID.equals(MsgCodeConstant.x0704)){
            afterInterceptBody = x0704Decoder.do_0704(upMsg);
        }else if(msgID.equals(MsgCodeConstant.x0705)){
            afterInterceptBody = x0705Decoder.do_0705(upMsg);
        }else if(msgID.equals(MsgCodeConstant.x0702)) {
            afterInterceptBody = x0702Decoder.do_0702(upMsg);
        }else{
            logger.error("未知的上行请求消息：msgId="+upMsg.msgId);
        }
        return afterInterceptBody;
    }
}
