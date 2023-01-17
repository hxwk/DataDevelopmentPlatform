package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 15:30
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Date;

/**
 * 行驶记录参数下传命令
 */
public class Req_8701 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701";
    }

    private Short orderWord; //命令字
    private Date realTime; //记录仪实时时间
    private Integer characterCoefficient; //车辆特征系数
    private String vin; //车辆vin号：ASCII码字符
    private String lpn; //车牌号：ASCII码字符
    private String lpnType; //车牌分类
    private Integer driverCode; //驾驶员代码
    private String lic; //驾驶证号码：ASCII码字符

    private Date firstTime; //记录仪初次安装时间
    private Integer initialMileage; //初始里程
    private Integer sumMileage; //累计行驶里程

    public Short getOrderWord() {
        return orderWord;
    }

    public void setOrderWord(Short orderWord) {
        this.orderWord = orderWord;
    }

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Integer getInitialMileage() {
        return initialMileage;
    }

    public void setInitialMileage(Integer initialMileage) {
        this.initialMileage = initialMileage;
    }

    public Integer getSumMileage() {
        return sumMileage;
    }

    public void setSumMileage(Integer sumMileage) {
        this.sumMileage = sumMileage;
    }

    public Integer getCharacterCoefficient() {
        return characterCoefficient;
    }

    public void setCharacterCoefficient(Integer characterCoefficient) {
        this.characterCoefficient = characterCoefficient;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getLpnType() {
        return lpnType;
    }

    public void setLpnType(String lpnType) {
        this.lpnType = lpnType;
    }

    public Integer getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(Integer driverCode) {
        this.driverCode = driverCode;
    }

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    @Override
    public String toString() {
        return "Req_8701{" + super.toString() +
                "orderWord=" + orderWord +
                ", realTime=" + realTime +
                ", characterCoefficient=" + characterCoefficient +
                ", vin='" + vin + '\'' +
                ", lpn='" + lpn + '\'' +
                ", lpnType='" + lpnType + '\'' +
                ", driverCode=" + driverCode +
                ", lic='" + lic + '\'' +
                ", firstTime=" + firstTime +
                ", initialMileage=" + initialMileage +
                ", sumMileage=" + sumMileage +
                '}';
    }
}
