package com.yaxon.vn.nd.ne.tas.util;


import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;


public class ByteBufReader {
    private Charset charset = Charset.forName("GBK");
    private final ByteBuf buf;

    public ByteBufReader(ByteBuf buf) {
        this.buf = buf;
    }

    public ByteBufReader(ByteBuf buf, Charset charset) {
        this.buf = buf;
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }


    public byte readByte() {
        return buf.readByte();
    }

    public byte readInt8() {
        return buf.readByte();
    }

    public short readUint8() {
        return buf.readUnsignedByte();
    }

    public short readInt16() {
        return buf.readShort();
    }

    public int readUint16() {
        return buf.readUnsignedShort();
    }

    public int readInt32() {
        return buf.readInt();
    }

    public long readUint32() {
        return buf.readUnsignedInt();
    }

    public long readInt64() {
        return buf.readLong();
    }

    public String readString(int length) {
        return new String(readBytes(length), charset);
    }

    public String readU8Str() {
        return readString(readUint8());
    }

    public String readU16Str() {
        return readString(readUint16());
    }

    public String readI32Str() {
        return readString(readInt32());
    }

    public byte[] readBytes(int length) {
        byte[] bs = new byte[length];
        readBytes(bs);
        return bs;
    }

    public void readBytes(byte[] b) {
        readBytes(b, 0, b.length);
    }

    public void readBytes(byte[] b, int offset, int length) {
        buf.readBytes(b, offset, length);
    }

    public byte[] readU8Bytes() {
        int len = readUint8();
        return readBytes(len);
    }

    public byte[] readU16Bytes() {
        int len = readUint16();
        return readBytes(len);
    }

    public byte[] readI32Bytes() {
        int len = readInt32();
        return readBytes(len);
    }

    public int getReaderIndex() {
        return buf.readerIndex();
    }

    public void setReaderIndex(int index) {
        buf.readerIndex(index);
    }

    public void skip(int length) {
        buf.skipBytes(length);
    }

    public void markReaderIndex() {
        buf.markReaderIndex();
    }

    public void resetReaderIndex() {
        buf.resetReaderIndex();
    }
}
