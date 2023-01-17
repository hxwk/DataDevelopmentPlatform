package com.dfssi.dataplatform.datasync.plugin.interceptor.decode;

import com.dfssi.dataplatform.datasync.common.utils.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.interceptor.bean.ProtoMsg;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * x0702 protocol message body parse
 *
 * @author jianKang
 * @date 2018/01/11
 */
public class X0702Decoder {
    static final Logger logger = LoggerFactory.getLogger(X0702Decoder.class);
    ByteBufUtils byteBufUtils = new ByteBufUtils();

    public byte[] do_0702(final ProtoMsg protoMsg) {
        if (null == protoMsg || 0 == protoMsg.getDataBuf().length) {
            logger.info("message body can not be null");
            return null;
        }
        try {
            String beforeBody = new String(protoMsg.getDataBuf());
            ByteBuf byteBuf = byteBufUtils.hexStringToByteBuf(beforeBody);
            Byte status = byteBuf.readByte();
            byteBuf.readBytes(6);
            //fixme todo no need
        } catch (Exception ex) {
            logger.error("0702 parse error:{}", ex.getMessage());
        }
        return null;
    }
}
