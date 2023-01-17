package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * CAN总线数据参数项，can protocol parameter item 对象
 * @author jianKang
 * @date 2017/12/19
 */
public class CanBusParamItem implements Serializable {
    /**
     * CAN ID
     */
    private String canId;
    /**
     * CAN DATA
     */
    private String canData;

    public CanBusParamItem() {
    }

    public CanBusParamItem(String canId, String canData) {
        this.canId = canId;
        this.canData = canData;
    }

    public String getCanId() {
        return canId;
    }

    public void setCanId(String canId) {
        this.canId = canId;
    }

    public String getCanData() {
        return canData;
    }

    public void setCanData(String canData) {
        this.canData = canData;
    }

    @Override
    public String toString() {
        /*return "CANBUS items[" +
                "canId=" + canId +
                ", canData=" + canData +
                ']';*/
        return JSON.toJSONString(this);
    }
}
