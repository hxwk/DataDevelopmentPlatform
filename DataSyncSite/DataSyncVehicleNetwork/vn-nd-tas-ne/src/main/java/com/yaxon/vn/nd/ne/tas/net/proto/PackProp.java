package com.yaxon.vn.nd.ne.tas.net.proto;

/**
 * Author: 程行荣
 * Time: 2013-11-07 11:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

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
