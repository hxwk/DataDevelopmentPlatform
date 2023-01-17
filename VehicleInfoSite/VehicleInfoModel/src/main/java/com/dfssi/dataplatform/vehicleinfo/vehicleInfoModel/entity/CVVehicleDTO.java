package com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity;

/**
 * 商用车车辆基本信息
 *
 * @author yanghs
 * @since 2018年4月2日15:49:57
 */
public class CVVehicleDTO extends BaseRourse {

    private String vin;//车辆识别代码
    private String sim;//SIM卡卡号
    private String vid;//
    private String did;// 终端编号id
    private String userId;//用户id
    private String vehicleType; //车型
    private String plateNo;// 车牌号
    private String buyTime;// 购车时间
    private String isValid;//是否有效 0：无效；1：有效

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(String buyTime) {
        this.buyTime = buyTime;
    }

    @Override
    public String toString() {
        return "CVVehicleDTO{" +
                "vin='" + vin + '\'' +
                ", sim='" + sim + '\'' +
                ", vid='" + vid + '\'' +
                ", did='" + did + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", plateNo='" + plateNo + '\'' +
                ", buyTime='" + buyTime + '\'' +
                ", isValid='" + isValid + '\'' +
                '}';
    }
}
