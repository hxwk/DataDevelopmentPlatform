package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.ide.access.mvc.entity.DataResourceConfEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.MetaDataresourceAccessInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 数据资源微服务调用
 * Created by yanghs on 2018/7/27.
 */
@FeignClient(value = "ide-dataResource",fallback =DataresourceHystrix.class)
public interface IDataresourceFeign {
    /**
     * 微服务调用获取数据源方法
     * @return
     */
    @RequestMapping(value = "dataresource/getAllDataResources",method = RequestMethod.GET)
    @ResponseBody
    List<DataResourceConfEntity> getAllDataResources();

    /**
     *根据dataresource_id查询dv_dataresource_sub表数据，用于新增修改时右侧参数配置信息展示
     * @return
     */
    @RequestMapping(value = "/dataresource/micro/getAccessInfoById",method = RequestMethod.POST)
    @ResponseBody
    List<MetaDataresourceAccessInfoEntity> getResourceAccessInfoByResId(@RequestBody String resId);

}
