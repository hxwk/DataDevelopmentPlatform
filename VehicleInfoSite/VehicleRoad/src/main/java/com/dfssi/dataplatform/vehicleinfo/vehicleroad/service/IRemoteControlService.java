package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;

import java.util.List;

/**
 * 远程控制管理
 * Created by yanghs on 2018/9/12.
 */
public interface IRemoteControlService {



    Req_8105 getReq_8105(RemoteControlEntity remoteControlEntity) throws Exception;
}
