package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;

import java.util.List;

/**
 * 终端查询与设置
 * Created by yanghs on 2018/9/12.
 */
public interface ITerminalSettingService {

    /**
     * 参数查询
     * @param terminalParamQuery
     * @return
     */
    //查询终端参数 消息Id:0x8106  返回终端应答 消息id:0x0104
    List<DataStatisticsEntity> queryparam(TerminalParamQuery terminalParamQuery);

    //设置终端参数 消息Id:0x8103  终端无应答
    //void setparam(TerminalSettingEntity terminalSettingEntity);

    //根据TerminalParamQuery入参类获得向master转发的Req_8103
    Req_8103 getReq_8103(TerminalSettingEntity terminalSettingEntity);

    //根据TerminalParamQuery入参类获得向master转发的Req_8104
    Req_8104 getReq_8104(TerminalParamQuery terminalParamQuery);

    //根据TerminalParamQuery入参类获得向master转发的 Req_8106
    Req_8106 getReq_8106(TerminalParamQuery terminalParamQuery) throws  Exception;

    /*JSON fileQuery(SDFileEntity file);

    JSON readFile(SDFileEntity vehicleNo);

    JSON fileQueryCommand(SDFileEntity file);

    JSON readFileCommand(SDFileEntity file);*/
}
