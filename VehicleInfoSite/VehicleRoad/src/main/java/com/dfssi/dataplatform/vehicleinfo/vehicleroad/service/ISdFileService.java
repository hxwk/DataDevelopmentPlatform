package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.SDFileEntity;



public interface ISdFileService {

    Object fileQuery(SDFileEntity file);

    Object readFile(SDFileEntity vehicleNo);

    ResponseObj fileQueryCommand(SDFileEntity file);

    ResponseObj readFileCommand(SDFileEntity file);
}
