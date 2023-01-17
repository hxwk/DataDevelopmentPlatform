package com.dfssi.dataplatform.ide.dataresource.mvc.restful;

import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DicItemEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "mng-system" ,fallback = ResourceDicItemHystrix.class)
public interface IResourceDicItemFeign {
    @RequestMapping(value = "dicItem/taskdataresourcedicItem",method = RequestMethod.POST)
    List<DicItemEntity> listGetValidResource();
}
