package com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity;

/**
 * 车辆基本信息
 *
 * @author yanghs
 * @since 2018年4月2日15:49:57
 */
public class VehicleDTO extends BaseRourse {

    private static final long serialVersionUID = 1L;

    private String vin;//车辆识别代码
    private String plateNo;// 车牌号
    private String iccId;//SIM卡卡号
    private String appId;// 应用id 4：监管平台
    private String userId;// 用户id
    private String vehicleCompany; //车企
    private String vehicleType; //车型
    private String vehicleUse;//车辆用途
    private String isValid;//是否有效 0：无效；1：有效

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getIccId() {
        return iccId;
    }

    public void setIccId(String iccId) {
        this.iccId = iccId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleUse() {
        return vehicleUse;
    }

    public void setVehicleUse(String vehicleUse) {
        this.vehicleUse = vehicleUse;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "VehicleDTO{" +
                "vin='" + vin + '\'' +
                ", plateNo='" + plateNo + '\'' +
                ", iccId='" + iccId + '\'' +
                ", appId='" + appId + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleCompany='" + vehicleCompany + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", vehicle_use=" + vehicleUse +
                ", isValid='" + isValid + '\'' +
                '}';
    }
}
