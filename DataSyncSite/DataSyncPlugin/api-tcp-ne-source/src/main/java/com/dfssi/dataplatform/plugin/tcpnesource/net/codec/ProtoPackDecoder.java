package com.dfssi.dataplatform.plugin.tcpnesource.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.apache.log4j.Logger;

import java.util.List;


public class ProtoPackDecoder extends ByteToMessageDecoder {

    private static Logger logger = Logger.getLogger(ProtoPackDecoder.class);

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object frm = decode(ctx, in);
        if (frm != null) {
//            ReferenceCountUtil.retain(frm);
            in.retain();
            out.add(frm);
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int len = in.readableBytes();
        if (len <= 0) {
            return null;
        }

        ByteBuf ret = Unpooled.buffer(len);
        for (int i = 0; i < len; i++) {
            byte b = in.readByte();
            ret.writeByte(b);
        }

        if (ret != null) {
            if (!checkPack(ret)) { //校验码校验失败
                in.release();
                throw new CorruptedFrameException("协议包CRC校验失败");
            } else {
                //校验码删除
                ret.writerIndex(in.writerIndex() - 1);
            }
        }

        //检查是否以##（及0x23，0x23）开头
        if (!checkStartChar(ret)) {
            logger.error("此报文么有以##（及0x23，0x23）开头");
            return null;
        }

        return ret;
    }

    /**
     * 检查是否以##（及0x23，0x23）开头
     * @param in
     * @return
     */
    private boolean checkStartChar(ByteBuf in) {
        boolean flag = false;

        in.markReaderIndex();

        byte b0 = in.readByte();
        byte b1 = in.readByte();

        if (0x23 == b0 && 0x23 == b1) {
            flag = true;
        }

        in.resetReaderIndex();

        return flag;
    }

    private boolean checkPack(ByteBuf buf) {
        int bytes = buf.readableBytes();
        if (bytes < 2) {
            return false;
        }

        buf.markReaderIndex();

        byte checkSum = 0;
        for (int i = 0; i < bytes - 1; i++) {
            checkSum ^= buf.readByte();
        }

        if (checkSum == buf.readByte()) {
            buf.resetReaderIndex();
            return true;
        }
        return false;
    }
}
