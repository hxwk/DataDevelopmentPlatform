package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.ide.access.mvc.entity.DataSourceConfEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDatasourceAccessInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 数据源微服务调用
 * Created by yanghs on 2018/7/27.
 */
@FeignClient(value = "ide-dataSource",fallback =DatasourceHystrix.class)
public interface IDatasourceFeign {

    /**
     * 微服务调用获取数据源方法
     * @return
     */
    @RequestMapping(value = "datasource/getAllDataSources",method = RequestMethod.GET)
    @ResponseBody
    List<DataSourceConfEntity> getAllDataSources();

    /**
     *微服务调用
     */
    @RequestMapping(value = "/datasource/micro/getAccessInfoById",method = RequestMethod.POST)
    @ResponseBody
    List<MetaDatasourceAccessInfoEntity> getSourceAccessInfoBySrcId(@RequestBody String srcId);


}
