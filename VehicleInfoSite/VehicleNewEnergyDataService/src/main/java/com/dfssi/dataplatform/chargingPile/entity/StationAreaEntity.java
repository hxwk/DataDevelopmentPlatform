package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/07/10 10:43
 */
public class StationAreaEntity {
    private String areaName;
    private int stationCount;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getStationCount() {
        return stationCount;
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }
}
