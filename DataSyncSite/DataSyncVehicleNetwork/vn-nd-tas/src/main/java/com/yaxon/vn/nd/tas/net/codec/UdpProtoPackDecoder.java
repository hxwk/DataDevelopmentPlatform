package com.yaxon.vn.nd.tas.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Author: 程行荣
 * Time: 2013-07-29 10:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class UdpProtoPackDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final int BUF_INIT_SIZE = 32;

    private ByteBuf ret;
    private InetSocketAddress sender;
    private ByteBuf buf;
    private boolean _7e = false;
    private boolean _7d = false;

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        try {
            Object frm = decode(ctx, msg);
            if (frm != null) {
                out.add(frm);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private Object decode(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf in = msg.content();
        int len = in.readableBytes();
        if (len <= 0) {
            return null;
        }

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
        DatagramPacket packet = new DatagramPacket(ret,msg.sender());
        return packet;
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
