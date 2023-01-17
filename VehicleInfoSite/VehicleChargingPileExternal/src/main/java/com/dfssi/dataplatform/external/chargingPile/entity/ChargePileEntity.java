package com.dfssi.dataplatform.external.chargingPile.entity;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/06/19 15:48
 */
public class ChargePileEntity {
    private int status;
    private long total;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}