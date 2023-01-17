package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;
import org.elasticsearch.search.SearchHits;

import java.util.List;
import java.util.Set;

public interface IRemoteUpgradeService {

    //查询0108响应信息
    Object query0108Response(String queryKey);
    //根据Req_8108_Entity入参类获得向master转发的Req_8108
    Req_8108 getReq_8108(Req_8108_Entity entity);

    //根据Req_E103_Entity入参类获得向master转发的Req_E103
    Req_E103 getReq_E103(Req_E103_Entity entity) throws Exception;
    //查询E003响应信息
    Object queryE003Response(String queryKey);


}
