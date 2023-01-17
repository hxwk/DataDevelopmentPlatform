package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.util;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProtoUtil {
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

    public static String readTimeString(ByteBuf buf){
        try {
            byte[] data = new byte[6];
            buf.readBytes(data);
            String t = bcd2Str(data);
            return t.substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("解析BCD时间异常", e);
        }
    }

    public static String readTimeString(long longTime, String pattern){
        return DateFormatUtils.format(longTime, pattern);
    }

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
}
