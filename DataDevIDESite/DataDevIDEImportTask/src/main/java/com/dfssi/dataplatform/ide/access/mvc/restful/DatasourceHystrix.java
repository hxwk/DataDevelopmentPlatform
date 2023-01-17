package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.access.mvc.entity.DataSourceConfEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDatasourceAccessInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据源断路器，微服务调用失败或超时时使用
 * Created by yanghs on 2018/7/27.
 */
@Component
public class DatasourceHystrix implements IDatasourceFeign {

   @Override
    public List<DataSourceConfEntity> getAllDataSources(){
       ResponseObj responseObj = ResponseObj.createResponseObj();
       responseObj.setData("");
       responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取数据源微服务调用失败，请检查微服务是否开启");
       return null;
    }

    @Override
    public List<MetaDatasourceAccessInfoEntity> getSourceAccessInfoBySrcId(String srcId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData("");
        responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取数据源微服务调用失败，请检查微服务是否开启");
        return null;
    }

}
