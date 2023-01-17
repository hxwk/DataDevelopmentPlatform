package com.dfssi.dataplatform.datasync.model.newen.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description 车辆位置信息数据
 */
public class NEGpsBean implements Serializable {
    private short gpsInformationType;
    /**
     * 定位状态Code
     */
    private short locationCode;
    /**
     * 定位状态
     */
    private List<String> locations;
    /**
     * 经度
     */
    private long longitude;
    /**
     * 纬度
     */
    private long latitude;

    public short getGpsInformationType() {
        return gpsInformationType;
    }

    public void setGpsInformationType(short gpsInformationType) {
        this.gpsInformationType = gpsInformationType;
    }

    public short getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(short locationCode) {
        this.locationCode = locationCode;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        locations = ObjectUtils.defaultIfNull(locations, Lists.<String>newArrayList());
        String body = "NEGpsBean{" +
                "gpsInformationType="+gpsInformationType+
                ", locationCode=" + locationCode +
                ", locations=" + locations +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
        return body;
    }
}
