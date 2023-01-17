package com.dfssi.dataplatform.util.msg;

import com.dfssi.dataplatform.client.NettyClient;
import com.dfssi.dataplatform.util.ByteBufCustomTool;
import com.dfssi.dataplatform.util.byteHexStr;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class msgEncoder  extends MessageToByteEncoder {
    private static Logger logger = LoggerFactory.getLogger(msgEncoder.class);

    private static final char[] DIGITS_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        logger.debug("获得对象："+o.toString()+",并将其16进制的数据转化成byte[]");
        //byte[] data = o.toString().getBytes();
        ByteBufCustomTool byteBufCustomTool = new ByteBufCustomTool();
        byte[] data = byteBufCustomTool.hexStringToBytes(o.toString());
        logger.debug("获得字节数组为：" + Arrays.toString(data));
        //byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        /*String hexStr = toHexString(data);
        logger.info("将获得的byte[]重新转换成16进制的数据核对："+hexStr);*/
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String abc = "33676E6574";
        ByteBufCustomTool byteBufCustomTool = new ByteBufCustomTool();
        byte[] data = byteBufCustomTool.hexStringToBytes(abc);
        String  str = new String(data,"gbk");

        logger.info("获得：" + str);

//        String abcd = "232302FE4C474A453133454130484D38383838383801006912081D0E0F37010103020000000000000000271000010000000000020101010000000000000000000003000000000000000000000100000100000101040200000000050000000000000000000601010001010100010101010101010700000000000000000008000900A6";
//        Integer x = Integer.parseInt(abc,16);
//        Integer x1 = Integer.parseInt(abcd,16);
//        System.out.println(x);
//        System.out.println(x1);
//
//       String baowentem = "232302FE4C474A453133454130484D38383838383801006912081D0E0F37010103020000000000000000271000010000000000020101010000000000000000000003000000000000000000000100000100000101040200000000050000000000000000000601010001010100010101010101010700000000000000000008000900A6";
//
//        String baowen = "2323";
//        logger.info("原始报文："+baowen);
//        byte[] data = baowen.getBytes();
//        /*byteBuf.writeInt(data.length);
//        byteBuf.writeBytes(data);*/
//        String hexStr = byteHexStr.bytesToHexFun1(data);
//        logger.info("将获得的byte[]重新转换成16进制的数据核对："+hexStr);
//        String Str = new String(data);
//        logger.info("将获得的byte[]重新转换字符串："+Str);
//        int x10 = Integer.parseInt(baowen,16);
//        logger.info("将16进制的字符串转换为10进制的字符串："+x10);
//        String str10 = String.valueOf(x10);
//
//        byte[] data10 = str10.getBytes();
//        String str16 = toHexString(data10);
//
//        logger.info("将10进制的字符串重新转换成16进制的字符串："+str16);*/
//
//
//        String a = "232302FE4C474A453133454130484D38383838383801006912081D0E0F37010103020000000000000000271000010000000000020101010000000000000000000003000000000000000000000100000100000101040200000000050000000000000000000601010001010100010101010101010700000000000000000008000900A";
//        byte[] bytes = a.getBytes();
//        System.out.println("字节数组为：" + Arrays.toString(bytes));
//        String b = byteHexStr.bytesToHexFun1(bytes);
//        System.out.println("将byte数组转换为16进制：" +b);
//
//        String str16 = toHexString(bytes);
//        System.out.println("将byte数组转换为16进制：" +str16);

        //如何再将2323这个16进制的字符串转换成byte数组
    }

    protected static char[] encodeHex(byte[] data) {
        //byte[]数组这样转换后变成现在的报文才是正常的
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return out;
    }
    public static String toHexString(byte[] bs) {
        return new String(encodeHex(bs));
    }



}
