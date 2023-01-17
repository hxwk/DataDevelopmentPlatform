package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import java.util.List;

public class TerminalParamResult {
    private short flowNo; //流水号

    private List<Item> items;

    public short getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(short flowNo) {
        this.flowNo = flowNo;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setParamItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "TerminalParamResult：flowNo:" + flowNo +
                ", items=" + items +
                '}';
    }
}
