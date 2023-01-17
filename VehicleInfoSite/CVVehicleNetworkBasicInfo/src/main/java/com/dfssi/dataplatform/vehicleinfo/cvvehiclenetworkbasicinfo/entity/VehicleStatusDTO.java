package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import java.io.Serializable;

/**
 * 车在线状态信息
 * Created by yanghs on 2018/5/9.
 */
public class VehicleStatusDTO implements Serializable{

    private String vid;

    private String status;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VehicleStatusDTO{" +
                "vid='" + vid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
