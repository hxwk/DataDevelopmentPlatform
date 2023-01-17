package com.yaxon.vn.nd.ne.tas.common;

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
    private static BaseEncoding hex = null;
    static {
        hex = BaseEncoding.base16().withSeparator(StringUtils.SPACE, 2);
    }

    public static String bytesToHexString(ByteBuf buf) {
        buf.markReaderIndex();
        int count = buf.readableBytes();
        byte[] out = new byte[count];
        buf.readBytes(out);
        buf.resetReaderIndex();

        return hex.encode(out);
    }

    public static String bytesToHexString(ByteBuf buf, int count) {
        buf.markReaderIndex();
        byte[] out = new byte[count];
        buf.readBytes(out);
        buf.resetReaderIndex();
        return hex.encode(out);
    }

    /**
     * 缓存中读出来指定长度值放到short数组里
     * @param msgBody
     * @param arrLength
     * @return
     */
    public static short[] bytes2ShortArr(ByteBuf msgBody, int arrLength) {
        short[] arr = new short[arrLength];
        int idx = 0;
        while(idx < arrLength){
            arr[idx++] = msgBody.readUnsignedByte();
        }
        return arr;
    }

    /**
     * HEX String to ByteBuf
     * @param hexString
     * @return ByteBuf
     */
    public static ByteBuf hexStringToByteBuf(String hexString) {
        byte[] bytes = hexStringToBytes(hexString);
        if (null == bytes) {
            return null;
        }
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }

    public static byte[] hexStringToBytes(String hexString) {
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
    public static String bytesToBit(Byte[] byteArr) {
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

    //(16进制)字节数组转整数
    public static int bytes2OctNum(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        String tmp;
        for (byte b : bytes){
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            // 每个字节8为，转为16进制标志，2个16进制位
            if (tmp.length() == 1){
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        int OctNum = Integer.parseInt(sb.toString(),16);
        return OctNum;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

/*    public static void main(String[] args) {
        byte[] bytes = {(byte)0x01,(byte)0x02};
        long sresp = bytes2OctNum(bytes);
        System.out.println("aaa= "+sresp);
    }*/
}
