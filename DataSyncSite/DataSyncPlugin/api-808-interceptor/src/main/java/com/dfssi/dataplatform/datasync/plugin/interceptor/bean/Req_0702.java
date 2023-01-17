package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

/**
 * 驾驶员身份信息采集上报 0702 entity
 * @author jianKang
 * @date 2018/01/11
 */
public class Req_0702 extends JtsReqMsg{
    public String id(){
        return "jts.0702";
    }

    private byte status;
    private String time;
    private byte icReadResult;
    private byte driverNameLength;
    private String driverName;
    private String qualificationNo;
    private byte licenseAgencyLength;
    private String licenseAgencyName;
    private String licenseValid;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte getIcReadResult() {
        return icReadResult;
    }

    public void setIcReadResult(byte icReadResult) {
        this.icReadResult = icReadResult;
    }

    public byte getDriverNameLength() {
        return driverNameLength;
    }

    public void setDriverNameLength(byte driverNameLength) {
        this.driverNameLength = driverNameLength;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getQualificationNo() {
        return qualificationNo;
    }

    public void setQualificationNo(String qualificationNo) {
        this.qualificationNo = qualificationNo;
    }

    public byte getLicenseAgencyLength() {
        return licenseAgencyLength;
    }

    public void setLicenseAgencyLength(byte licenseAgencyLength) {
        this.licenseAgencyLength = licenseAgencyLength;
    }

    public String getLicenseAgencyName() {
        return licenseAgencyName;
    }

    public void setLicenseAgencyName(String licenseAgencyName) {
        this.licenseAgencyName = licenseAgencyName;
    }

    public String getLicenseValid() {
        return licenseValid;
    }

    public void setLicenseValid(String licenseValid) {
        this.licenseValid = licenseValid;
    }

    @Override
    public String toString() {
        /*return "Req_0702{" +
                "status=" + status +
                ", time='" + time + '\'' +
                ", icReadResult=" + icReadResult +
                ", driverNameLength=" + driverNameLength +
                ", driverName='" + driverName + '\'' +
                ", qualificationNo='" + qualificationNo + '\'' +
                ", licenseAgencyLength=" + licenseAgencyLength +
                ", licenseAgencyName='" + licenseAgencyName + '\'' +
                ", licenseValid='" + licenseValid + '\'' +
                '}';*/
        return JSON.toJSONString(this);

    }
}
