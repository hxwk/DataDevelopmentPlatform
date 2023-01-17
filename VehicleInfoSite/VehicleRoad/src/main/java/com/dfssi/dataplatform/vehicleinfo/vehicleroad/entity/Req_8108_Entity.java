package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 *查询0108响应入参实体
 */
public class Req_8108_Entity {

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private String vin;

    private String vid;

    private String updateType; //升级类型    0:终端  12:道路运输证 IC 卡读卡器    52: 北斗卫星定位模块

    private String manufacturerId; //制造商ID

    private String version; //版本号

    private String filePath; //升级包文件路径


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }


}
