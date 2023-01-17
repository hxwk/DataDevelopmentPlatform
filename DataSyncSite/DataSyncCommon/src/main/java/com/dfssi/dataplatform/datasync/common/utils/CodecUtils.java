package com.dfssi.dataplatform.datasync.common.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;

/**
 * Created by Hannibal on 2018-02-28.
 */
public class CodecUtils {

    public CodecUtils() {
    }

    public static byte[] hex2Bytes(String s) {
        Validate.notNull(s);
        Validate.isTrue(s.length() % 2 == 0, "字符串的字节数必须为偶数");
        byte[] bytes = new byte[s.length() / 2];

        for(int i = 0; i < s.length() / 2; ++i) {
            bytes[i] = (byte)Short.parseShort(s.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        byte[] arr$ = bytes;
        int len$ = bytes.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte b = arr$[i$];
            sb.append(hexChar((byte)(b >> 4 & 15)));
            sb.append(hexChar((byte)(b & 15)));
        }

        return sb.toString();
    }

    private static char hexChar(byte b) {
        return b < 10?(char)(48 + b):(char)(97 + (b - 10));
    }

    public static short parseUnsignedByte(String s) {
        return parseUnsignedByte(s, 10);
    }

    public static short parseUnsignedByte(String s, int radix) {
        int parse = Integer.parseInt((String) Preconditions.checkNotNull(s), radix);
        if(parse >> 8 == 0) {
            return (short)parse;
        } else {
            throw new NumberFormatException("out of range: " + parse);
        }
    }

    public static int parseUnsignedShort(String s) {
        return parseUnsignedShort(s, 10);
    }

    public static int parseUnsignedShort(String s, int radix) {
        int parse = Integer.parseInt((String)Preconditions.checkNotNull(s), radix);
        if(parse >> 16 == 0) {
            return parse;
        } else {
            throw new NumberFormatException("out of range: " + parse);
        }
    }

    public static long parseUnsignedInt(String s) {
        return parseUnsignedInt(s, 10);
    }

    public static long parseUnsignedInt(String s, int radix) {
        Preconditions.checkNotNull(s);
        long result = Long.parseLong(s, radix);
        if((result & 4294967295L) != result) {
            throw new NumberFormatException("Input " + s + " in base " + radix + " is not in the range of an unsigned integer");
        } else {
            return result;
        }
    }

    public static String shortToHex(short s) {
        return String.format("%04x", new Object[]{Short.valueOf(s)});
    }

    public static void main(String[] args) {
        byte[] bytearray = new byte[]{101, 16, -13, 41};
        int i0 = (new BigInteger(bytearray)).intValue();
        String s = bytes2Hex(bytearray);
        long i = parseUnsignedInt(s, 16);
        System.out.println(i);
    }

}
