package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.access.mvc.entity.DataResourceConfEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDataresourceAccessInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据资源断路器，微服务调用失败或超时时使用
 * Created by yanghs on 2018/7/27.
 */
@Component
public class DataresourceHystrix implements IDataresourceFeign {
   @Override
   public List<DataResourceConfEntity> getAllDataResources() {
       ResponseObj responseObj = ResponseObj.createResponseObj();
       responseObj.setData("");
       responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取数据资源微服务调用失败，请检查微服务是否开启");
       return null;
   }

    @Override
    public List<MetaDataresourceAccessInfoEntity> getResourceAccessInfoByResId(String resId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData("");
        responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取数据资源微服务调用失败，请检查微服务是否开启");
        return null;
    }
}
