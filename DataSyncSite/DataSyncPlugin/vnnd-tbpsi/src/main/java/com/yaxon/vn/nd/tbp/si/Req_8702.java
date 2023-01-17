package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:34
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 上报驾驶员身份信息请求
 */
public class Req_8702 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8702";}

    //消息体为空

    @Override
    public String toString() {
        return "Req_8702{" + super.toString() + "}";
    }
}
