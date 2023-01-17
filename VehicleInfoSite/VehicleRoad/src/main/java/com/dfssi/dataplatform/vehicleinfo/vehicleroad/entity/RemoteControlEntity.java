package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 远程控制管理
 * Created by yinli on 2018/9/29.
 */
@Data
public class RemoteControlEntity{

    private String sim;
    /* 车辆ID */
    private String vid;
    private  byte  commandType;
    private  String commandParam;


    public byte getCommandType() {
        return commandType;
    }

    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }


    public String getCommandParam() {
        return commandParam;
    }

    public void setCommandType(byte commandType) {
        this.commandType = commandType;
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

    @Override
    public String toString() {
        return "{sim:"+sim+",vid:"+vid+",commandType:"+commandType+"}";
    }
}
