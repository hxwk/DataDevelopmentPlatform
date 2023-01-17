package com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity;

/**
 * 平台信息
 *
 * @author yanghs
 * @since 2018-4-11 15:03:36
 */
public class PlatformDTO extends BaseRourse {

    private static final long serialVersionUID = 1L;

    private String userName;//用户名
    private String password;//密码
    private String vehicleCompany; //车企
    private String userId;//用户id

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PlatformDTO{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", vehicleCompany='" + vehicleCompany + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
