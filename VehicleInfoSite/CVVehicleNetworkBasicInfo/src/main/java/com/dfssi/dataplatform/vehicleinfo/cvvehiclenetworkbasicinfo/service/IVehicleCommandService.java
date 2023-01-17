package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import java.util.List;

/**
 * Created by yanghs on 2018/5/24.
 */
public interface IVehicleCommandService {

    /**
     * 指令下发执行结果
     * @param vid
     * @param msgId
     * @return
     */
    List findCommandInfo(String vid, String msgId);
}
