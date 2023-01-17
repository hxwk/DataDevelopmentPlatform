package com.dfssi.zuul.restful;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "mng-system",fallback = RouteLoginHystrix.class)
public interface IRouteLoginFeign {

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    ResponseObj login(@RequestBody Map usermap);

}