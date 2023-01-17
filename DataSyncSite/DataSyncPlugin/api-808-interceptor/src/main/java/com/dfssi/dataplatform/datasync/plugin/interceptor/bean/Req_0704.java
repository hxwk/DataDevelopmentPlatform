package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 定位数据批量上传
 * @author jianKang
 * @date 2017/12/14
 */
public class Req_0704 extends JtsReqMsg {
    public String id(){
        return "jts.0704";
    }

    private Long sim;
    private byte positionDataType; //位置数据类型
    private List<GpsVo> gpsVos; //位置汇报数据项列表

    public Long getSim() {
        return sim;
    }

    public void setSim(Long sim) {
        this.sim = sim;
    }

    public byte getPositionDataType() {
        return positionDataType;
    }

    public void setPositionDataType(byte positionDataType) {
        this.positionDataType = positionDataType;
    }

    public List<GpsVo> getGpsVos() {
        return gpsVos;
    }

    public void setGpsVos(List<GpsVo> gpsVos) {
        this.gpsVos = gpsVos;
    }

    @Override
    public String toString() {
        /*return "Req_0704{" + super.toString() +
                "positionDataType=" + positionDataType +
                ", gpsVos=" + gpsVos +
                '}';*/
        return JSON.toJSONString(this);

    }
}
