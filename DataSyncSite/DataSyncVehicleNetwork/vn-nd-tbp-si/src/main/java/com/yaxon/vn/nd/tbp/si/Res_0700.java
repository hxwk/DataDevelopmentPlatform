package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 15:02
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 行驶记录数据上传
 */
public class Res_0700 extends JtsResMsg {
    @Override
    public String id() {
        return "jts.0700";
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
        return "Res_0700{" + super.toString() +
                ", orderWord=" + orderWord +
                ", travellingDataRecorderItems=" + travellingDataRecorderItems +
                '}';
    }
}
