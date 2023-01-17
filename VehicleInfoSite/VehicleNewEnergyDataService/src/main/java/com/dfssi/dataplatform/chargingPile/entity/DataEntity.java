package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/6/2 15:41
 */
public class DataEntity<T> {
    private T Data;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
