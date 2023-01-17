package com.dfssi.zuul.restful;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
public class RouteLoginHystrix implements IRouteLoginFeign {
    public ResponseObj login(@RequestBody Map usermap) {
        ResponseObj responseObj=ResponseObj.createResponseObj();
        responseObj.setData("");
        responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败","系统登录微服务调用失败，请检查微服务是否开启");
        return responseObj;
    }
}
