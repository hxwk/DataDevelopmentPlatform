package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto;


/**
 * 分包属性
 */
public class PackProp {
    public short dataLen = 0;
    public byte cryptFlag = 0; //加密标志:0-不加密；1-RSA加密
    public byte splitFlag = 0; //分包标志:0-不分包；1-分包


    public PackProp() {
    }

    public PackProp(short dataLen, byte cryptFlag, byte splitFlag) {
        this.dataLen = dataLen;
        this.cryptFlag = cryptFlag;
        this.splitFlag = splitFlag;
    }
}
