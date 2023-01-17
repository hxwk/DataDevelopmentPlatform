package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 *查询E003响应入参实体
 */
public class Req_E103 {

    private String id;

    private String timestamp;

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private String vin;

    private String vid;

    private String paramId;

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    private String paramValue; //升级包文件路径

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
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

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    @Override
    public String toString() {
        return "Req_E103{sim="+sim
                +",timestamp="+timestamp
                +",vin=" +vin
                +",vid=" +vid
                +",paramId=" +paramId
                +'}';
    }

}
