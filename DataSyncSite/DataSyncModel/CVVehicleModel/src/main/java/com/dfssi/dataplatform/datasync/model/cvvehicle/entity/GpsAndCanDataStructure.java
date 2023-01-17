package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/5/31
 * @description 实时上报的位置信息和对应的Can 报文
 */
public class GpsAndCanDataStructure implements Serializable {
    private String id;
    private String vid;
    private String sim;
    private String msgId;
    private long receiveTime;
    private short version;
    private int primaryPkgNum;
    private List<GpsAndCanDataPkg> pkgs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getPrimaryPkgNum() {
        return primaryPkgNum;
    }

    public void setPrimaryPkgNum(int primaryPkgNum) {
        this.primaryPkgNum = primaryPkgNum;
    }

    public List<GpsAndCanDataPkg> getPkgs() {
        return pkgs;
    }

    public void setPkgs(List<GpsAndCanDataPkg> pkgs) {
        this.pkgs = pkgs;
    }

    @Override
    public String toString() {
        return "GpsAndCanDataStructure{" +
                "id='" + id + '\'' +
                ", vid='" + vid + '\'' +
                ", sim='" + sim + '\'' +
                ", msgId='" + msgId + '\'' +
                ", receiveTime=" + receiveTime +
                ", version=" + version +
                ", primaryPkgNum=" + primaryPkgNum +
                ", pkgs=" + pkgs +
                '}';
    }
}
