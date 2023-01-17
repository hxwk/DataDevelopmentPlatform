package com.yaxon.vn.nd.tbp.si.ne;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/4/10
 * @description 可充电储能装置电压数据格式
 */
public class NEChargeVoltage implements Serializable {
    private short chargeVoltageInformationType;
    /**
     * 可充电储能子系统个数
     */
    private short storageVoltageSubSysNum;
    /**
     * 可充电储能子系统电压信息列表
     */
    private List<NEChargeVoltageBean> neChargeVoltageBeanList;

    public short getChargeVoltageInformationType() {
        return chargeVoltageInformationType;
    }

    public void setChargeVoltageInformationType(short chargeVoltageInformationType) {
        this.chargeVoltageInformationType = chargeVoltageInformationType;
    }

    public short getStorageVoltageSubSysNum() {
        return storageVoltageSubSysNum;
    }

    public void setStorageVoltageSubSysNum(short storageVoltageSubSysNum) {
        this.storageVoltageSubSysNum = storageVoltageSubSysNum;
    }

    public List<NEChargeVoltageBean> getNeChargeVoltageBeanList() {
        return neChargeVoltageBeanList;
    }

    public void setNeChargeVoltageBeanList(List<NEChargeVoltageBean> neChargeVoltageBeanList) {
        this.neChargeVoltageBeanList = neChargeVoltageBeanList;
    }

    @Override
    public String toString() {
        return "NEChargeVoltage{" +
                "chargeVoltageInformationType=" + chargeVoltageInformationType +
                ", storageVoltageSubSysNum=" + storageVoltageSubSysNum +
                ", neChargeVoltageBeanList=" + neChargeVoltageBeanList +
                '}';
    }
}
