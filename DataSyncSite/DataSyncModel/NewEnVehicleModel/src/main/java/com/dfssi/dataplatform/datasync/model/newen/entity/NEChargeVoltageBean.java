package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/4/10
 * @description 可充电储能装置电压数据
 */
public class NEChargeVoltageBean implements Serializable {
    /**
     * 可充电储能子系统号
     */
    private short storageSubSysNo;

    /**
     * 可充电储能装置电压
     */
    private int storageVoltage;

    /**
     * 可充电储能装置电流
     */
    private int storageCurrent;

    /**
     * 单体电池总数
     */
    private int cellTotal;

    /**
     * 本帧起始电池序号
     */
    private int serailOfFrame;

    /**
     * 本帧单体电池总数
     */
    private short cellNumOfFrame;

    /**
     *单体电池电压
     */
    private List<Integer> cellVoltage;

    public short getStorageSubSysNo() {
        return storageSubSysNo;
    }

    public void setStorageSubSysNo(short storageSubSysNo) {
        this.storageSubSysNo = storageSubSysNo;
    }

    public int getStorageVoltage() {
        return storageVoltage;
    }

    public void setStorageVoltage(int storageVoltage) {
        this.storageVoltage = storageVoltage;
    }

    public int getStorageCurrent() {
        return storageCurrent;
    }

    public void setStorageCurrent(int storageCurrent) {
        this.storageCurrent = storageCurrent;
    }

    public int getCellTotal() {
        return cellTotal;
    }

    public void setCellTotal(int cellTotal) {
        this.cellTotal = cellTotal;
    }

    public int getSerailOfFrame() {
        return serailOfFrame;
    }

    public void setSerailOfFrame(int serailOfFrame) {
        this.serailOfFrame = serailOfFrame;
    }

    public short getCellNumOfFrame() {
        return cellNumOfFrame;
    }

    public void setCellNumOfFrame(short cellNumOfFrame) {
        this.cellNumOfFrame = cellNumOfFrame;
    }

    public List<Integer> getCellVoltage() {
        return cellVoltage;
    }

    public void setCellVoltage(List<Integer> cellVoltage) {
        this.cellVoltage = cellVoltage;
    }

    @Override
    public String toString() {
        return "NEChargeVoltageBean{" +
                "storageSubSysNo=" + storageSubSysNo +
                ", storageVoltage=" + storageVoltage +
                ", storageCurrent=" + storageCurrent +
                ", cellTotal=" + cellTotal +
                ", serailOfFrame=" + serailOfFrame +
                ", cellNumOfFrame=" + cellNumOfFrame +
                ", cellVoltage=" + cellVoltage +
                '}';
    }
}
