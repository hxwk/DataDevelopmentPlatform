package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/5/30
 * @description primary data pkg
 * GPS AND CAN data object
 */
public class GpsAndCanDataPkg implements Serializable {
    //唯一标识符
    private String id;
    //纬度
    private Long latitude;
    //经度
    private Long longitute;
    //高程
    private Integer height;
    //方向
    private Integer direction;
    //时间
    private Long dataTime;
    //二级包个数
    private Integer secondPkgNum;
    //二级包
    private List<SecondDataPkg> secondDataPkgList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitute() {
        return longitute;
    }

    public void setLongitute(Long longitute) {
        this.longitute = longitute;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Long getDataTime() {
        return dataTime;
    }

    public void setDataTime(Long dataTime) {
        this.dataTime = dataTime;
    }

    public Integer getSecondPkgNum() {
        return secondPkgNum;
    }

    public void setSecondPkgNum(Integer secondPkgNum) {
        this.secondPkgNum = secondPkgNum;
    }

    public List<SecondDataPkg> getSecondDataPkgList() {
        return secondDataPkgList;
    }

    public void setSecondDataPkgList(List<SecondDataPkg> secondDataPkgList) {
        this.secondDataPkgList = secondDataPkgList;
    }

    @Override
    public String toString() {
        return "GpsAndCanDataPkg{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitute=" + longitute +
                ", height=" + height +
                ", direction=" + direction +
                ", dataTime=" + dataTime +
                ", secondPkgNum=" + secondPkgNum +
                ", secondDataPkgList=" + secondDataPkgList +
                '}';
    }
}
