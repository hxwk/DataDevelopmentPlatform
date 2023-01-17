package com.yaxon.vn.nd.tbp.si.ne;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/4/10
 * @description 可充电储能装置温度数据格式
 */
public class NEChargeTemp implements Serializable {
    private short chargeTempInformationType;

    private short storageTempSubSysNum;

    private List<NEChargeTempBean> neChargeTempBeanList;

    public short getChargeTempInformationType() {
        return chargeTempInformationType;
    }

    public void setChargeTempInformationType(short chargeTempInformationType) {
        this.chargeTempInformationType = chargeTempInformationType;
    }

    public short getStorageTempSubSysNum() {
        return storageTempSubSysNum;
    }

    public void setStorageTempSubSysNum(short storageTempSubSysNum) {
        this.storageTempSubSysNum = storageTempSubSysNum;
    }

    public List<NEChargeTempBean> getNeChargeTempBeanList() {
        return neChargeTempBeanList;
    }

    public void setNeChargeTempBeanList(List<NEChargeTempBean> neChargeTempBeanList) {
        this.neChargeTempBeanList = neChargeTempBeanList;
    }

    @Override
    public String toString() {
        return "NEChargeTemp{" +
                "chargeInformationType=" + chargeTempInformationType +
                ", storageSubSysNum=" + storageTempSubSysNum +
                ", neChargeTempBeanList=" + neChargeTempBeanList +
                '}';
    }
}
