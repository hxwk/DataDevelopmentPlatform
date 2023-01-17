package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2014-01-19 20:46
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 终端控制命令参数项
 */
public class CommandParamItem {
    private Byte connectControl; //连接控制
    private String dialName; //拨号点名称
    private String dialUser; //拨号用户名
    private String dialPwd; //拨号密码
    private String address; //地址
    private Short tcPort; //服务器TCP端口
    private Short udPort; //服务器UDP端口
    private String manufacturerId; //制造商ID，长度为5
    private String authCode; //监管平台鉴权码
    private String hardWareVersion; //硬件版本
    private String firmWareVersion; //固件版本
    private String URLAddress; //URL地址
    private Integer timeLimit; //连接到指定服务器时限（单位：分钟）

    public CommandParamItem() {
    }

    public CommandParamItem(Byte connectControl, String dialName, String dialUser,
                            String dialPwd, String address, Short tcPort, Short udPort,
                            String manufacturerId, String authCode, String hardWareVersion,
                            String firmWareVersion, String URLAddress, Integer timeLimit) {
        this.connectControl = connectControl;
        this.dialName = dialName;
        this.dialUser = dialUser;
        this.dialPwd = dialPwd;
        this.address = address;
        this.tcPort = tcPort;
        this.udPort = udPort;
        this.manufacturerId = manufacturerId;
        this.authCode = authCode;
        this.hardWareVersion = hardWareVersion;
        this.firmWareVersion = firmWareVersion;
        this.URLAddress = URLAddress;
        this.timeLimit = timeLimit;
    }

    public Byte getConnectControl() {
        return connectControl;
    }

    public void setConnectControl(Byte connectControl) {
        this.connectControl = connectControl;
    }

    public String getDialName() {
        return dialName;
    }

    public void setDialName(String dialName) {
        this.dialName = dialName;
    }

    public String getDialUser() {
        return dialUser;
    }

    public void setDialUser(String dialUser) {
        this.dialUser = dialUser;
    }

    public String getDialPwd() {
        return dialPwd;
    }

    public void setDialPwd(String dialPwd) {
        this.dialPwd = dialPwd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Short getTcPort() {
        return tcPort;
    }

    public void setTcPort(Short tcPort) {
        this.tcPort = tcPort;
    }

    public Short getUdPort() {
        return udPort;
    }

    public void setUdPort(Short udPort) {
        this.udPort = udPort;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getHardWareVersion() {
        return hardWareVersion;
    }

    public void setHardWareVersion(String hardWareVersion) {
        this.hardWareVersion = hardWareVersion;
    }

    public String getFirmWareVersion() {
        return firmWareVersion;
    }

    public void setFirmWareVersion(String firmWareVersion) {
        this.firmWareVersion = firmWareVersion;
    }

    public String getURLAddress() {
        return URLAddress;
    }

    public void setURLAddress(String URLAddress) {
        this.URLAddress = URLAddress;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public String toString() {
        return "CommandParamItem{" +
                "connectControl=" + connectControl +
                ", dialName='" + dialName + '\'' +
                ", dialUser='" + dialUser + '\'' +
                ", dialPwd='" + dialPwd + '\'' +
                ", address='" + address + '\'' +
                ", tcPort=" + tcPort +
                ", udPort=" + udPort +
                ", manufacturerId='" + manufacturerId + '\'' +
                ", authCode='" + authCode + '\'' +
                ", hardWareVersion='" + hardWareVersion + '\'' +
                ", firmWareVersion='" + firmWareVersion + '\'' +
                ", URLAddress='" + URLAddress + '\'' +
                ", timeLimit=" + timeLimit +
                '}';
    }
}
