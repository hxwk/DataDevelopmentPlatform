package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.codec;

import com.dfssi.dataplatform.datasync.service.util.msgutil.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.apache.log4j.Logger;

import java.util.List;


public class ProtoPackDecoder extends ByteToMessageDecoder {

    private static Logger logger = Logger.getLogger(ProtoPackDecoder.class);

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
        int len = in.readableBytes();
        if (len <= 0) {
            return null;
        }

        //打印报文日志
//        logger.info("此次接收报文：" + ByteBufUtil.buf2Str(in));
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
