package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.codec;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


public class ProtoPackDecoder extends ByteToMessageDecoder {
    //logger是为了收集原始报文，将其存储在message目录下
    private static final Logger logger = LoggerFactory.getLogger("protolog");
    //loggerSeocnd是将roadSource的正常日志收集下来
    private static final Logger loggerSeocnd = LoggerFactory.getLogger(ProtoPackDecoder.class);

    private static final int BUF_INIT_SIZE = 32;

    private ByteBuf buf;
    private boolean _7e = false;
    private boolean _7d = false;

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        releaseBuf();
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        loggerSeocnd.trace("解码第二步:ProtoPackDecoder的decode1对进来的字节流进行解码成对象");
      /*  if (in.readableBytes()<4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(dataLength<0){
            ctx.close();
        }
        if(in.readableBytes()<dataLength){
            in.resetReaderIndex();
            return;
        }*/
       /* byte[] data = new byte[dataLength];
        in.readBytes(data);
        T obj = SerializationUtil.deserialize(data,clazz);
        out.add(obj);*/

        Object frm = decode(ctx, in);
        if (frm != null) {
            out.add(frm);
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        loggerSeocnd.trace("解码第三步:ProtoPackDecoder的decode2对进来的字节流进行解码成对象");
        int len = in.readableBytes();
        if (len <= 0) {
            return null;
        }
        //打印报文日志
        logger.info("tcp recvOri：" + ByteBufUtil.buf2Str(in));
//        in.resetReaderIndex();
        ByteBuf ret = null;
        outer:
        for (int i = 0; i < len; i++) {
            byte b = in.readByte();
            switch (b) {
                case 0x7e:
                    if (_7e) {
                        _7e = false;
                        _7d = false;
                        ret = buf;
                        buf = null;
                        break outer;//成功解析到完整分包
                    } else {
                        _7e = true;
                        _7d = false;
                        releaseBuf();
                        //buf = ctx.alloc().buffer(BUF_INIT_SIZE);
                        buf = Unpooled.buffer(BUF_INIT_SIZE);
                    }
                    break;
                case 0x7d:
                    if (_7e) {
                        if (_7d) {
                            _7d = false;
                            _7e = false;
                            releaseBuf();
                        } else {
                            _7d = true;
                        }
                    }
                    break;
                case 0x01:
                    if (_7e) {
                        if (_7d) {
                            buf.writeByte(0x7d);
                            _7d = false;
                        } else {
                            buf.writeByte(0x01);
                        }
                    }
                    break;
                case 0x02:
                    if (_7e) {
                        if (_7d) {
                            buf.writeByte(0x7e);
                            _7d = false;
                        } else {
                            buf.writeByte(0x02);
                        }
                    }
                    break;
                default:
                    if (_7e) {
                        if (_7d) {
                            _7d = false;
                            _7e = false;
                            releaseBuf();
                        } else {
                            buf.writeByte(b);
                        }
                    }
                    break;
            }
        }

        if (ret != null) {
            if (!checkPack(ret)) { //校验码校验失败
                ret.release();
                throw new CorruptedFrameException("协议包CRC校验失败");
            } else {
                //校验码删除
                ret.writerIndex(ret.writerIndex() - 1);
            }
        }

        //打印报文日志
//        logger.info("此次接收报文：" + ByteBufUtil.buf2Str(ret));
//        ret.resetReaderIndex();

        return ret;
    }

    private void releaseBuf() {
        if (buf != null) {
            buf.release();
            buf = null;
        }
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
