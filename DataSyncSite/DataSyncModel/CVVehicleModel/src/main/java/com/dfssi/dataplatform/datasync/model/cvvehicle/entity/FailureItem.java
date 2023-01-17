package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;

/**
 * Author: JIANKANG
 * Time: 2018-09-26 19:29
 */
public class FailureItem implements Serializable {
    private short sa;//字节源地址

    private short failureNum;//故障个数

    private int spn;

    private int fmi;

    private int spnCm;

    private int oc;

    //private String failureName;//故障名称

    private String sysCategory;//系统类别

    private String model;//名称及型号

    private String desc;//中文描述

    private String diagnosticCode;//诊断仪代码

    private int failureGrade;//故障等级

    private String failureUnit;//故障部件

    public short getSa() {
        return sa;
    }

    public void setSa(short sa) {
        this.sa = sa;
    }

    public short getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(short failureNum) {
        this.failureNum = failureNum;
    }

    public int getSpn() {
        return spn;
    }

    public void setSpn(int spn) {
        this.spn = spn;
    }

    public int getFmi() {
        return fmi;
    }

    public void setFmi(int fmi) {
        this.fmi = fmi;
    }

    public int getSpnCm() {
        return spnCm;
    }

    public void setSpnCm(int spnCm) {
        this.spnCm = spnCm;
    }

    public int getOc() {
        return oc;
    }

    public void setOc(int oc) {
        this.oc = oc;
    }

    public String getSysCategory() {
        return sysCategory;
    }

    public void setSysCategory(String sysCategory) {
        this.sysCategory = sysCategory;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDiagnosticCode() {
        return diagnosticCode;
    }

    public void setDiagnosticCode(String diagnosticCode) {
        this.diagnosticCode = diagnosticCode;
    }

    public int getFailureGrade() {
        return failureGrade;
    }

    public void setFailureGrade(int failureGrade) {
        this.failureGrade = failureGrade;
    }

    public String getFailureUnit() {
        return failureUnit;
    }

    public void setFailureUnit(String failureUnit) {
        this.failureUnit = failureUnit;
    }

    @Override
    public String toString() {
        return "FailureItem{" +
                "sa=" + sa +
                ", failureNum=" + failureNum +
                ", spn=" + spn +
                ", fmi=" + fmi +
                ", spnCm=" + spnCm +
                ", oc=" + oc +
                ", sysCategory='" + sysCategory + '\'' +
                ", model='" + model + '\'' +
                ", desc='" + desc + '\'' +
                ", diagnosticCode='" + diagnosticCode + '\'' +
                ", failureGrade=" + failureGrade +
                ", failureUnit='" + failureUnit + '\'' +
                '}';
    }
}
