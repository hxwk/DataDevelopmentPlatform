package com.dfssi.dataplatform.plugin.tcpnesource.util;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Hannibal on 2018-02-01.
 */
public class ByteBufUtil {

    public static final int UNICODE_LEN = 2;

    public static String buf2Str(ByteBuf in) {
        byte[] dst = new byte[in.readableBytes()];
        in.getBytes(0, dst);
        return toHexString(dst);
    }

    public static String toHexString(byte[] bs) {
        return new String(encodeHex(bs));
    }

    private static final char[] DIGITS_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    protected static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 转换字符数组为定长byte[]
     * @param chars              字符数组
     * @return 若指定的定长不足返回null, 否则返回byte数组
     */
    public byte[] Chars2Bytes_LE(char[] chars){
        if(chars == null)
            return null;

        int iCharCount = chars.length;
        byte[] rst = new byte[iCharCount*UNICODE_LEN];
        int i = 0;
        for( i = 0; i < iCharCount; i++){
            rst[i*2] = (byte)(chars[i] & 0xFF);
            rst[i*2 + 1] = (byte)(( chars[i] & 0xFF00 ) >> 8);
        }

        return rst;
    }

    /**
     * 转换byte数组为char（大端）
     * @return
     * @note 数组长度至少为2，按小端方式转换
     */
    public char Bytes2Char_BE(byte[] bytes){
        if(bytes.length < 2)
            return (char)-1;
        int iRst = (bytes[0] << 8) & 0xFF;
        iRst |= bytes[1] & 0xFF;

        return (char)iRst;
    }

    /**
     * 转换String为byte[]
     * @param str
     * @return
     */
    public byte[] String2Bytes_LE(String str) {
        if(str == null){
            return null;
        }
        char[] chars = str.toCharArray();

        byte[] rst = Chars2Bytes_LE(chars);

        return rst;
    }

    /**
     * 16进制前补零
     * @param str
     * @param size
     * @param isprefixed
     * @return
     */
    public static String pad(String str ,int size ,boolean isprefixed) {
        if (str == null)
            str = org.apache.commons.lang3.StringUtils.EMPTY;
        int str_size = str.length();
        int pad_len = size - str_size;
        StringBuffer retvalue = new StringBuffer();
        for (int i = 0; i < pad_len; i++) {
            retvalue.append("0");
        }
        if (isprefixed)
            return retvalue.append(str).toString();
        return retvalue.insert(0, str).toString();
    }

    public static String byte2String(byte b) {
        return StringUtils.leftPad(Integer.toBinaryString(b & 0xff), 8, "0");
    }

}
