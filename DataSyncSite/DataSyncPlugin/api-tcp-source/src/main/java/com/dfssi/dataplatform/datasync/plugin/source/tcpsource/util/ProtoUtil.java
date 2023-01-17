package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util;

import com.google.common.math.LongMath;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Time: 2013-11-12 14:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ProtoUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProtoUtil.class);

    private static final Charset CHARSET_GBK = Charset.forName("GBK");

    public static long bcd2Phone(byte[] bcd) {
        Validate.isTrue(bcd != null && bcd.length == 6, "手机BCD码数组长度必须为6字节");
        long phone = 0;
        for (int i = 0; i < 6; i++) {
            byte b = bcd[5 - i];
            phone += (b & 0x0F) * LongMath.pow(10, 2 * i);
            phone += ((b & 0xF0) >> 4) * LongMath.pow(10, 2 * i + 1);
        }
        return phone;
    }

    public static byte[] phone2Bcd(long phone) {
        byte[] bcd = new byte[6];
        long l = phone;
        for (int i = 0; i < 6; i++) {
            byte lo = (byte) (l % 10);
            l = l / 10;
            byte hi = (byte) (l % 10);
            l = l / 10;
            bcd[5 - i] = (byte) ((hi << 4) | lo);
        }
        Validate.isTrue(l == 0, "无效的电话号码: %d", phone);
        return bcd;
    }

    public static byte[] int2Bcd(int phone) {
        byte[] bcd = new byte[4];
        long l = phone;
        for (int i = 0; i < 4; i++) {
            byte lo = (byte) (l % 10);
            l = l / 10;
            byte hi = (byte) (l % 10);
            l = l / 10;
            bcd[3 - i] = (byte) ((hi << 4) | lo);
        }
        return bcd;
    }

/*    public static String bcd2Str(byte[] bcd) {
        int len = bcd.length;
        StringBuilder sb = new StringBuilder();
        for (int i = len - 1; i >= 0; i--) {
            byte b = bcd[i];
            sb.append(((b & 0xF0) >> 4) + '0'); //高字节
            sb.append((b & 0x0F)+'0'); //低字节
        }
        return sb.toString();
    }*/

    public static String bcd2Str(byte[] bcd) {
        int len = bcd.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = bcd[i];
            int hi = (b & 0xF0) >> 4;
            if (hi > 9) throw new IllegalArgumentException("BCD数组中存在>9的元素");
            sb.append(hi); //高字节
            int lo = b & 0x0F;
            if (lo > 9) throw new IllegalArgumentException("BCD数组中存在>9的元素");
            sb.append(lo); //低字节
        }
        return sb.toString();
    }

/*    public static byte[] str2Bcd(String str) {
        if (str == null || "".equals(str)) {
            return new byte[0];
        }
        int len = str.length();
        if (len % 2 != 0) {
            str = str+'0';
            len++;
        }
        byte[] bcd = new byte[len/2];
        int j = len/2-1;
        for (int i = 0; i < len; i++) {
            byte b = 0;
            char c = str.charAt(i);
            b = (byte)(((c - '0') & 0x0F) << 4);
            c = str.charAt(++i);
            b = (byte)(b ^ ((c - '0') & 0x0F));
            bcd[j--] = b;
        }
        return bcd;
    }*/

    public static byte[] str2Bcd(String str) {
        if (str == null || "".equals(str)) {
            return new byte[0];
        }
        int len = str.length();
        if (len % 2 != 0) {
            str = str + '0';
            len++;
        }
        byte[] bcd = new byte[len / 2];
        int j = 0;
        for (int i = 0; i < len; i++) {
            byte b = 0;
            char c = str.charAt(i);
            if (c < '0' || c > '9') throw new IllegalArgumentException("BCD字符串存在非数字字符");
            b = (byte) (((c - '0') & 0x0F) << 4);
            c = str.charAt(++i);
            if (c < '0' || c > '9') throw new IllegalArgumentException("BCD字符串存在非数字字符");
            b = (byte) (b ^ ((c - '0') & 0x0F));
            bcd[j++] = b;
        }
        return bcd;
    }


    /**
     * 从ByteBuf中获取读取字符串，默认GBK编码.
     *
     * @param buf
     * @param dataLen 字符串长度
     * @return
     */

    public static String readString(ByteBuf buf, int dataLen) {
        byte[] data = new byte[dataLen];
        buf.readBytes(data);

        //剔除字节数组首部与尾部的'\0'字符
        int end = 0;
        for (int i = 0; i < dataLen; i++) {
            if (data[i] == 0) break;
            else ++end;
        }

        return new String(data, 0, end, CHARSET_GBK);
    }


/*    public static String readString(ByteBuf buf, int dataLen) {
        return readString(buf, dataLen, CHARSET_GBK);
    }*/

    /**
     * 从ByteBuf中获取读取字符串
     *
     * @param buf
     * @param dataLen 字符串长度
     * @param charset
     * @return
     */
    public static String readString(ByteBuf buf, int dataLen, Charset charset) {
        byte[] data = new byte[dataLen];
        buf.readBytes(data);
        return new String(data, charset);
    }

    /**
     * 从ByteBuf中获取读取字符串（GBK编码）。其中，第一个字节（无符号）代表字符串长度。
     *
     * @param buf
     * @return
     */
    public static String readU8String(ByteBuf buf) {
        int dataLen = buf.readUnsignedByte();
        return readString(buf, dataLen);
    }

    /**
     * 从ByteBuf中获取读取字符串。其中，第一个字节（无符号）代表字符串长度。
     *
     * @param buf
     * @param charset
     * @return
     */
    public static String readU8String(ByteBuf buf, Charset charset) {
        int dataLen = buf.readUnsignedByte();
        return readString(buf, dataLen, charset);
    }

    /**
     * 从ByteBuf中获取读取字符串（GBK编码）。其中，前2字节（无符号）代表字符串长度。
     *
     * @param buf
     * @return
     */
    public static String readU16String(ByteBuf buf) {
        int dataLen = buf.readUnsignedShort();
        return readString(buf, dataLen);
    }

    /**
     * 从ByteBuf中获取读取字符串。其中，前2字节（无符号）代表字符串长度。
     *
     * @param buf
     * @param charset
     * @return
     */
    public static String readU16String(ByteBuf buf, Charset charset) {
        int dataLen = buf.readUnsignedShort();
        return readString(buf, dataLen, charset);
    }

    /**
     * 往 ByteBuf 中写入字符串。默认采用GBK编码。
     *
     * @param buf
     * @param src
     */
/*    public static void writeString(ByteBuf buf, String src) {
        writeString(buf, src, CHARSET_GBK);
    }*/
    public static void writeString(ByteBuf buf, String src) {
        if (src == null) {
            return;
        }

        byte[] data = src.getBytes(CHARSET_GBK);
        buf.writeBytes(data);
    }

    public static void stringToAscii(ByteBuf buf, String value) {
        if (value == null) {
            return;
        }

        char[] chars = value.toCharArray();
        byte[] asciis = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            asciis[i] = (byte) chars[i];
        }
        buf.writeBytes(asciis);
    }

    /**
     * 往 ByteBuf 中写入字符串。
     *
     * @param buf
     * @param src
     * @param charset
     */
    public static void writeString(ByteBuf buf, String src, Charset charset) {
        if (src == null) {
            return;
        }

        byte[] data = src.getBytes(charset);
        buf.writeBytes(data);
    }

    /**
     * 往 ByteBuf 中写入字符串（GBK编码）。其中，第一个字节（无符号）表示字符串长度。
     *
     * @param buf
     * @param src
     */
    public static void writeU8String(ByteBuf buf, String src) {
        if (src != null) {
            byte[] data = src.getBytes(CHARSET_GBK);
            buf.writeByte(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeByte(0);
        }
    }


    /**
     * 往 ByteBuf 中写入字符串。其中，第一个字节（无符号）表示字符串长度。
     *
     * @param buf
     * @param src
     * @param charset
     */
    public static void writeU8String(ByteBuf buf, String src, Charset charset) {
        if (src != null) {
            byte[] data = src.getBytes(charset);
            buf.writeByte(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeByte(0);
        }
    }

    /**
     * 往 ByteBuf 中写入字符串（GBK编码）。其中，前2字节（无符号）表示字符串长度。
     *
     * @param buf
     * @param src
     */
    public static void writeU16String(ByteBuf buf, String src) {
        if (src != null) {
            byte[] data = src.getBytes(CHARSET_GBK);
            buf.writeShort(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeShort(0);
        }
    }


    /**
     * 往 ByteBuf 中写入字符串。其中，前2字节（无符号）表示字符串长度。
     *
     * @param buf
     * @param src
     * @param charset
     */
    public static void writeU16String(ByteBuf buf, String src, Charset charset) {
        if (src != null) {
            byte[] data = src.getBytes(charset);
            buf.writeShort(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeShort(0);
        }
    }


    /**
     * 从ByteBuf中获取读取字节数组
     *
     * @param buf
     * @param dataLen 字节数组长度
     * @return
     */
    public static byte[] readBytes(ByteBuf buf, int dataLen) {
        byte[] data = new byte[dataLen];
        buf.readBytes(data);
        return data;
    }

    /**
     * 从ByteBuf中获取读取字节数组。其中，第一个字节（无符号）代表字节数组长度。
     *
     * @param buf
     * @return
     */
    public static byte[] readU8Bytes(ByteBuf buf) {
        int dataLen = buf.readUnsignedByte();
        return readBytes(buf, dataLen);
    }

    /**
     * 从ByteBuf中获取读取字节数组。其中，前2字节（无符号）代表字节数组长度。
     *
     * @param buf
     * @return
     */
    public static byte[] readU16Bytes(ByteBuf buf) {
        int dataLen = buf.readUnsignedShort();
        return readBytes(buf, dataLen);
    }


    /**
     * 从ByteBuf中获取读取字节数组。其中，前4字节（有符号）代表字节数组长度。
     *
     * @param buf
     * @return
     */
    public static byte[] readI32Bytes(ByteBuf buf) {
        int dataLen = buf.readInt();
        return readBytes(buf, dataLen);
    }

    /**
     * 往 ByteBuf 中写入字节数组。其中，第1字节（无符号）代表字节数组长度。
     */
    public static void writeU8Bytes(ByteBuf buf, byte[] data) {
        if (data != null) {
            buf.writeByte(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeByte(0);
        }
    }


    /**
     * 往 ByteBuf 中写入字节数组。其中，前2字节（无符号）代表字节数组长度。
     */
    public static void writeU16Bytes(ByteBuf buf, byte[] data) {
        if (data != null) {
            buf.writeShort(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeShort(0);
        }
    }

    /**
     * 往 ByteBuf 中写入字节数组。其中，前4字节（有符号）代表字节数组长度。
     */
    public static void writeI32Bytes(ByteBuf buf, byte[] data) {
        if (data != null) {
            buf.writeInt(data.length);
            buf.writeBytes(data);
        } else {
            buf.writeInt(0);
        }
    }

    /**
     * 读取bcd码时间，格式:yy-MM-dd-HH-mm-ss
     *
     * @param buf
     * @return
     */
    public static Date readTime(ByteBuf buf) {
        try {
            byte[] data = new byte[6];
            buf.readBytes(data);
            String t = bcd2Str(data);
            return new SimpleDateFormat("yyMMddHHmmss").parse(t);
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }

    public static Long readTimeCAN(ByteBuf buf) {
        try {
            byte[] data = new byte[5];
            buf.readBytes(data);
            String t = bcd2Str(data);
            logger.info("readTimeCAN t = " + t);
            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            Calendar canDate = Calendar.getInstance();
            canDate.setTimeInMillis(new SimpleDateFormat("HHmmssSSS").parse(t).getTime());
            if (nowHour < canDate.get(Calendar.HOUR_OF_DAY)) {//此时是凌晨的时候可能存在跨天的情况
                now.add(Calendar.DATE, -1);
            }

            now.set(Calendar.HOUR_OF_DAY, canDate.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, canDate.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, canDate.get(Calendar.SECOND));
            now.set(Calendar.MILLISECOND, canDate.get(Calendar.MILLISECOND));

            return now.getTimeInMillis();
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }

    /**
     * 读取bcd码时间，格式:yy-MM-dd-HH-mm
     *
     * @param buf
     * @return
     */
    public static Date readTime1(ByteBuf buf) {
        try {
            byte[] data = new byte[5];
            buf.readBytes(data);
            String t = bcd2Str(data);
            return new SimpleDateFormat("yyMMddHHmm").parse(t);
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }

    /**
     * 读取bcd码时间，格式:yy-MM-dd-HH-mm
     *
     * @param buf
     * @return
     */
    public static Date readTime2(ByteBuf buf) {
        try {
            byte[] data = new byte[4];
            buf.readBytes(data);
            String t = bcd2Str(data);
            return new SimpleDateFormat("yyMMddHH").parse(t);
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }

    public static void writeTime(ByteBuf buf, Date time) {
        String t = new SimpleDateFormat("yyMMddHHmmss").format(time);
        byte[] bcd = str2Bcd(t);
        buf.writeBytes(bcd);
    }

    /**
     * @param buf
     * @param time 格式：yy-MM-dd-HH-mm-ss
     */
    public static void writeTime(ByteBuf buf, String time) throws Exception {
        Date d = new SimpleDateFormat("yy-MM-dd-HH-mm-ss").parse(time);
        writeTime(buf, d);
    }


    /*public static void main(String[] args) {
        String s = "1312031544";
        byte[] bcd = str2Bcd(s);
        String s2 = bcd2Str(bcd);
        System.out.println(bcd);
    }*/

}
