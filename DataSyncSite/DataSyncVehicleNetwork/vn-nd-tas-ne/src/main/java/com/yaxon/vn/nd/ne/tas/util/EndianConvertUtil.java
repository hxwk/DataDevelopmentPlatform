package com.yaxon.vn.nd.ne.tas.util;

/**
 * Created by HSF on 2018/2/2.
 * 大端/小端转换工具
 * byte[]与int和long之间的转换
 */
public class EndianConvertUtil {

    /**
     * int to byte[] 支持 1或者 4 个字节
     * @param i
     * @param len
     * @return
     */
    public static byte[] intToByteLittleEndian(int i,int len) {
        byte[] abyte=null;
        if(len==1){
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        }else{
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }

    public  static int bytesToIntLittleEndian(byte[] bytes) {
        int addr=0;
        if(bytes.length==1){
            addr = bytes[0] & 0xFF;
        }else{
            addr = bytes[0] & 0xFF;
            addr |= ((bytes[1] << 8) & 0xFF00);
            addr |= ((bytes[2] << 16) & 0xFF0000);
            addr |= ((bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }

    /**
     * int to byte[] 支持 1或者 4 个字节
     * @param i
     * @param len
     * @return
     */
    public static byte[] intToByteBigEndian(int i,int len) {
        byte[] abyte=null;
        if(len==1){
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        }else{
            abyte = new byte[len];
            abyte[0] = (byte) ((i >>> 24) & 0xff);
            abyte[1] = (byte) ((i >>> 16) & 0xff);
            abyte[2] = (byte) ((i >>> 8) & 0xff);
            abyte[3] = (byte) (i & 0xff);
        }
        return abyte;
    }

    public  static int bytesToIntBigEndian(byte[] bytes) {
        int addr=0;
        if(bytes.length==1){
            addr = bytes[0] & 0xFF;
        }else{
            addr = (int) ((((bytes[3] & 0xff) << 24)
                    | ((bytes[2] & 0xff) << 16)
                    | ((bytes[1] & 0xff) << 8)
                    | ((bytes[0] & 0xff) << 0)));
        }
        return addr;
    }

    /**
     * int to byte[] 支持 1或者 4 个字节
     * @param i
     * @param len
     * @return
     */
    public static byte[] longToByteBigEndian(long i,int len) {
        byte[] abyte=null;
        if(len==1){
            abyte = new byte[len];
            abyte[0] = (byte) (0xffff & i);
        }else{
            abyte = new byte[len];
            abyte[7] = (byte) (i >> 56);
            abyte[6] = (byte) (i >> 48);
            abyte[5] = (byte) (i >> 40);
            abyte[4] = (byte) (i >> 32);
            abyte[3] = (byte) (i >> 24);
            abyte[2] = (byte) (i >> 16);
            abyte[1] = (byte) (i >> 8);
            abyte[0] = (byte) (i >> 0);
        }
        return abyte;
    }


    public  static long bytesToLongBigEndian(byte[] bytes) {
        long addr= 0;
        if(bytes.length==1){
            addr = bytes[0] & 0xFFFF;
        }else{
            addr = bytes[0]&0xff;
            addr = (addr<<8 )| (bytes[1]&0xff);
            addr = (addr<<8 )| (bytes[2]&0xff);
            addr = (addr<<8 )| (bytes[3]&0xff);
            addr = (addr<<8)| (bytes[4]&0xff);
            addr = (addr<<8 )| (bytes[5]&0xff);
            addr = (addr<<8 )| (bytes[6]&0xff);
            addr = (addr<<8 )| (bytes[7]&0xff);
        }
        return addr;
    }


    /**
     * int to byte[] 支持 1或者 4 个字节
     * @param i
     * @param len
     * @return
     */
    public static byte[] longToByteLitterEndian(long i,int len) {
        byte[] abyte=null;
        if(len==1){
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        }else{
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
            abyte[4] = (byte) ((0xff00000000L & i) >> 32);
            abyte[5] = (byte) ((0xff0000000000L & i) >>40);
            abyte[6] = (byte) ((0xff000000000000L & i)>>48);
            abyte[7] = (byte) ((0xff00000000000000L & i)>>56);
        }
        return abyte;
    }


    public  static long bytesToLongLitterEndian(byte[] bytes) {
        long addr= 0;
        if(bytes.length==1){
            addr = bytes[0] & 0xFF;
        }else{
            addr = ((((long) bytes[7] & 0xff) << 56)
                    | (((long) bytes[6] & 0xff) << 48)
                    | (((long) bytes[5] & 0xff) << 40)
                    | (((long) bytes[4] & 0xff) << 32)
                    | (((long) bytes[3] & 0xff) << 24)
                    | (((long) bytes[2] & 0xff) << 16)
                    | (((long) bytes[1] & 0xff) << 8)
                    | (((long) bytes[0] & 0xff) << 0));
        }
        return addr;
    }

    public static void main(String[] args) {
        int i1= 0x10203040;
        byte[] bytesL = intToByteLittleEndian(i1,4);
        int iL = bytesToIntLittleEndian(bytesL);
        System.out.println(Integer.toHexString(iL));

        int i2= 0x10203040;
        byte[] bytesB = intToByteBigEndian(i2,4);
        int iB = bytesToIntBigEndian(bytesB);
        System.out.println(Integer.toHexString(iB));

        long j1 = 0x3135313037423632L;
        byte[] bytesjLittle = longToByteLitterEndian(j1,8);
        long jLittleEndian = bytesToLongLitterEndian(bytesjLittle);
        System.out.println(Long.toHexString(jLittleEndian));

        long j2 = 0x3135313037423632L;
        byte[] bytesj = longToByteBigEndian(j2,8);
        long jBigEndian = bytesToLongBigEndian(bytesj);
        System.out.println(Long.toHexString(jBigEndian));

    }
}
