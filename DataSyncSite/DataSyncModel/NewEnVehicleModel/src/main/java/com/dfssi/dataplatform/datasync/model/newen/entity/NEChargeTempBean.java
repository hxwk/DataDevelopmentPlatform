package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author JianKang
 * @date 2018/4/10
 * @description 可充电储能子系统上温度数据格式
 */
public class NEChargeTempBean implements Serializable {
    /**
     * 可充电储能子系统好
     */
    private short storageSubSerial;
    /**
     * 可充电储能温度探针个数
     */
    private int storageTempProbeNum;
    /**
     * 可充电储能子系统各温度探针探测到的温度
     */
    private int[] storageTempAllProbeNums;

    public short getStorageSubSerial() {
        return storageSubSerial;
    }

    public void setStorageSubSerial(short storageSubSerial) {
        this.storageSubSerial = storageSubSerial;
    }

    public int getStorageTempProbeNum() {
        return storageTempProbeNum;
    }

    public void setStorageTempProbeNum(int storageTempProbeNum) {
        this.storageTempProbeNum = storageTempProbeNum;
    }

    public int[] getStorageTempAllProbeNums() {
        return storageTempAllProbeNums;
    }

    public void setStorageTempAllProbeNums(int[] storageTempAllProbeNums) {
        this.storageTempAllProbeNums = storageTempAllProbeNums;
    }

    @Override
    public String toString() {
        return "NEChargeTempBean{" +
                "storageSubSerial=" + storageSubSerial +
                ", storageTempProbeNum=" + storageTempProbeNum +
                ", storageTempAllProbeNums=" + Arrays.toString(storageTempAllProbeNums) +
                '}';
    }
}
