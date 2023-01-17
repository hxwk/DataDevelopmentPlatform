package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.util;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.ResponseObj;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Description:
 *   用于封装响应请求的结果
 * @author LiXiaoCong
 * @version 2017/11/22 10:34
 */
public class ResponseUtil {
    private ResponseUtil(){}

    public static ResponseObj success(){
        return successBase();
    }

    public static ResponseObj success(Object data){
        ResponseObj res = successBase();
        if(data == null) data = new Object[0];
        res.setData(data);
        return res;
    }

    public static ResponseObj error(String detail){
        return errorBase(detail);
    }

    public static ResponseObj success(long total, Object record){
        Map<String, Object> data = Maps.newHashMap();
        data.put("total", total);
        data.put("record", record);
        return success(data);
    }

    private static ResponseObj successBase(){
        ResponseObj res = ResponseObj.createResponseObj();
        res.setStatus(ResponseObj.CODE_SUCCESS, "请求成功", null);
        return res;
    }


    private static ResponseObj errorBase(String detail){
        ResponseObj res = ResponseObj.createResponseObj();
        res.setStatus(ResponseObj.CODE_FAIL_B, "请求失败", detail);
        return res;
    }
}
