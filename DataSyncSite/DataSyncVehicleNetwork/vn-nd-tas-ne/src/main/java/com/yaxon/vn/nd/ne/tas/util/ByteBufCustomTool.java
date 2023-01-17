package com.yaxon.vn.nd.ne.tas.util;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ByteBuf util tools class
 * @author jianKang
 * @date 2017/12/22
 */
public class ByteBufCustomTool {
    private static final Logger logger = LoggerFactory.getLogger(ByteBufCustomTool.class);
    private BaseEncoding hex = null;

    public ByteBufCustomTool() {
        hex = BaseEncoding.base16().withSeparator(StringUtils.SPACE, 2);
    }

    public String bytesToHexString(ByteBuf buf) {
        buf.markReaderIndex();
        int count = buf.readableBytes();
        byte[] out = new byte[count];
        buf.readBytes(out);
        buf.resetReaderIndex();

        return hex.encode(out);
    }

    public String bytesToHexString(ByteBuf buf, int count) {
        buf.markReaderIndex();
        byte[] out = new byte[count];
        buf.readBytes(out);
        buf.resetReaderIndex();
        return hex.encode(out);
    }

    /**
     * HEX String to ByteBuf
     * @param hexString
     * @return ByteBuf
     */
    public ByteBuf hexStringToByteBuf(String hexString) {
        byte[] bytes = hexStringToBytes(hexString);
        if (null == bytes) {
            return null;
        }
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals(StringUtils.EMPTY)) {
            return null;
        }

        if(hexString.contains(StringUtils.SPACE)){
            hexString = hexString.replace(StringUtils.SPACE, StringUtils.EMPTY);
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        byte[] d = new byte[length];
        try {
            char[] hexChars = hexString.toCharArray();
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
        }catch (Exception ex){
            logger.error("hex string to bytes error,{}",ex.getMessage());
        }
        return d;
    }

    /**
     * 把byte转为字符串的bit
     */
    public String bytesToBit(Byte[] byteArr) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String bits = StringUtils.EMPTY;
            for (byte b : byteArr) {
                stringBuilder.append(bits + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                        + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                        + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                        + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1));
            }
        }catch (Exception ex){
            logger.error("byte arrays change to String error,{}",ex.getMessage());
        }
        return stringBuilder.toString();
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
