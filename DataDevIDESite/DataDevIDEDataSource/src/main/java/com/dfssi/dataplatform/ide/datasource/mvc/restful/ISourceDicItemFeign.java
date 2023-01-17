package com.dfssi.dataplatform.ide.datasource.mvc.restful;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DicItemEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "mng-system" ,fallback = SourceDicItemHystrix.class)
public interface ISourceDicItemFeign {
    @RequestMapping(value = "dicItem/taskdatasourcedicItem",method = RequestMethod.POST)
    List<DicItemEntity> listGetValidSource();
}
