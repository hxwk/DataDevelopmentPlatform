package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 19:47
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class ParmBean implements Serializable {
    //参数key
    private String id;
   //参数value
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ParmBean{" + super.toString() +
                ",id=" + id +
                ",value="+value+
                '}';
    }
}
