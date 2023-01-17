package com.yaxon.vn.nd.tbp.si;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: 孙震
 * Time: 2014-02-25 11:40
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
public class Req_0700Q_2003 extends JtsReqMsg{

    @Override
    public String id() {
        return "jts.0700Q2003";
    }

    private Byte orderWord; //命令字

    private List<TravellingDataRecorder2003Item> travellingDataRecorderItems = new ArrayList<TravellingDataRecorder2003Item>(); //各种记录

    public List<TravellingDataRecorder2003Item> getTravellingDataRecorderItems() {
        return travellingDataRecorderItems;
    }

    public void setTravellingDataRecorderItems(List<TravellingDataRecorder2003Item> travellingDataRecorderItems) {
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
        return "Req_0700Q_2003{" + super.toString() +
                "orderWord=" + orderWord +
                ", travellingDataRecorderItems=" + travellingDataRecorderItems +
                '}';
    }
}
