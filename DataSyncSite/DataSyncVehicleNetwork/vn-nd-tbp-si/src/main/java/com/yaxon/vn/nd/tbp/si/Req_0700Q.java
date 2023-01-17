package com.yaxon.vn.nd.tbp.si;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sun Zhen
 * Time: 2014-01-09 10:57
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
public class Req_0700Q extends JtsReqMsg{
    @Override
    public String id() {
        return "jts.0700Q";
    }

    private Byte orderWord; //命令字

    private List<TravellingDataRecorderItem> travellingDataRecorderItems = new ArrayList<TravellingDataRecorderItem>(); //各种记录

    public List<TravellingDataRecorderItem> getTravellingDataRecorderItems() {
        return travellingDataRecorderItems;
    }

    public void setTravellingDataRecorderItems(List<TravellingDataRecorderItem> travellingDataRecorderItems) {
        this.travellingDataRecorderItems = travellingDataRecorderItems;
    }

    public Byte getOrderWord() {
        return orderWord;
    }

    public void setOrderWord(Byte orderWord) {
        this.orderWord = orderWord;
    }

    @Override
    public String toString() {
        return "Req_0700Q{" + super.toString() +
                ", orderWord=" + orderWord +
                ", travellingDataRecorderItems=" + travellingDataRecorderItems +
                '}';
    }
}
