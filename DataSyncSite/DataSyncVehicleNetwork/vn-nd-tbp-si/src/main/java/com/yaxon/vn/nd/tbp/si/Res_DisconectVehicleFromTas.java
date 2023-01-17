package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 李松
 * Time: 2013-10-30 10:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 断开车辆与前置机连接应答
 */
public class Res_DisconectVehicleFromTas extends JtsResMsg {

    public static final byte RE_VE_REGISTERED = 0x11;  //车辆未与前置机连接
    @Override
    public String id() { return "disconnectVehiceFromTas.x"; }

    @Override
    public String toString() {
        return "Res_DisconectVehicleFromTas{" + super.toString() + "}";
    }
}
