package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 *查询E003响应入参实体
 */
public class Req_E103_Entity {

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private String vin;

    private String vid;

    private  String  paramId;

    private String paramValue; //升级包文件路径

    private int isWaiting;  //是否等待


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
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

    public int getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(int isWaiting) {
        this.isWaiting = isWaiting;
    }

}
