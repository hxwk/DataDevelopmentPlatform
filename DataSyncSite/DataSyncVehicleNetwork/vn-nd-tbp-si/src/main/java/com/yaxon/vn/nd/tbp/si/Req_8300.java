package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 15:41
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Date;

/**
 * 文本信息下发
 */
public class Req_8300 extends JtsReqMsg {

    public static final String _id = "jts.8300";

    @Override
    public String id() { return "jts.8300"; }

    private Byte flag; //文本信息标志位
    private String text; //文本信息
    private Date ct; //创建时间
    private Long cuid; //创建人
    private Long tid; //企业id
    private String fromIp;//前台用户ip
    private Long sim;
    private String loginName; //用户登录名
    private String lpn; //车牌号
    private Byte userType;
    private String userName;//姓名（昵称）
    private Boolean saveIntoDbFlag=true;

    public Long getSim() {
        return sim;
    }

    public void setSim(Long sim) {
        this.sim = sim;
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }

    public Long getCuid() {
        return cuid;
    }

    public void setCuid(Long cuid) {
        this.cuid = cuid;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public Boolean getSaveIntoDbFlag() {
        return saveIntoDbFlag;
    }

    public void setSaveIntoDbFlag(Boolean saveIntoDbFlag) {
        this.saveIntoDbFlag = saveIntoDbFlag;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Byte getUserType() {
        return userType;
    }

    public void setUserType(Byte userType) {
        this.userType = userType;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    @Override
    public String toString() {
        return "Req_8300{" +
                "flag=" + flag +
                ", text='" + text + '\'' +
                ", ct=" + ct +
                ", cuid=" + cuid +
                ", tid=" + tid +
                ", fromIp='" + fromIp + '\'' +
                ", sim=" + sim +
                ", loginName='" + loginName + '\'' +
                ", lpn='" + lpn + '\'' +
                ", userType=" + userType +
                ", userName='" + userName + '\'' +
                ", saveIntoDbFlag=" + saveIntoDbFlag +
                '}';
    }
}
