package com.yaxon.vn.nd.ne.tas.avs;

import com.yaxon.vn.nd.ne.tas.exception.BadFormattedProtocolException;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoPackHeader;
import com.yaxon.vn.nd.ne.tas.util.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2013-07-29 10:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ProtoDecoder extends ByteToMessageDecoder {
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

        ByteBuf pkg = null;
        outer:
        for (int i = 0; i < len; i++) {
            byte b = in.readByte();
            switch (b) {
                case 0x7e:
                    if (_7e) {
                        _7e = false;
                        _7d = false;
                        pkg = buf;
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

        if (pkg != null) {
            try {
                if (!checkPack(pkg)) { //校验码校验失败
                    throw new CorruptedFrameException("协议包CRC校验失败");
                } else {
                    return parseProtoMsg(pkg);
                }
            } catch (Exception e) {
                throw e;
            } finally {
                pkg.release();
            }
        }

        return null;
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

    private ProtoMsg parseProtoMsg(ByteBuf pack) {
        if (pack.readableBytes() < ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT) {
            throw new BadFormattedProtocolException("协议数据包的长度小于包头长度(" + ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + ")");
        }

        ProtoMsg protoMsg = null;
        try {
            protoMsg = new ProtoMsg();
            protoMsg.msgId = pack.readShort();
            short prop = pack.readShort();
            short dataLen = (short) (prop & 0x03FF);
            byte cryptFlag = (byte) ((prop >> 10) & 0x07);
            byte splitFlag = (byte) ((prop >> 13) & 0x01);
            byte[] bcd = new byte[6];
            pack.readBytes(bcd);
            protoMsg.sim = String.valueOf(ProtoUtil.bcd2Phone(bcd));
            protoMsg.sn = pack.readShort();
            if (splitFlag == 1) {
                protoMsg.packCount = pack.readUnsignedShort();
                protoMsg.packIndex = pack.readUnsignedShort();
                if (protoMsg.packCount <= 0 || protoMsg.packIndex > protoMsg.packCount || protoMsg.packIndex <= 0) {
                    throw new BadFormattedProtocolException("分包索引号异常: packCount=" + protoMsg.packCount
                            + ",index=" + protoMsg.packIndex);
                }
            }
            //判断一下数据包完整性
            if (dataLen != pack.readableBytes()-1) {
                throw new BadFormattedProtocolException("协议分包中的数据长度与消息体的字节数不一致: " + dataLen
                        + "," + pack.readableBytes());
            }

            protoMsg.dataBuf = Unpooled.buffer(dataLen);
            pack.readBytes(protoMsg.dataBuf, dataLen);
        } catch (Exception e) {
            throw new BadFormattedProtocolException("解析协议数据包头失败", e);
        }

        return protoMsg;
    }
}
