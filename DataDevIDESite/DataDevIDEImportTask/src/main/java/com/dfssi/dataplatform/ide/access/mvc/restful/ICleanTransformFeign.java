package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskCleanTransformInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskCleanTransformMappingEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 清洗转换微服务调用
 * Created by yanghs on 2018/7/27.
 */
@FeignClient(value = "ide-cleanTransform",fallback =CleanTransformHystrix.class)
public interface ICleanTransformFeign {
    /**
     * 调用清洗转换微服务，获取清洗转换方法
     * @return
     */
    @RequestMapping(value = "ide/task/cleantransform/getAllCleanTranses",method = RequestMethod.GET)
    @ResponseBody
    List<TaskCleanTransformMappingEntity> getAllCleanTranses();

    /**
     * 调用清洗转换微服务，根据mappingid获取子表数据
     */
    @RequestMapping(value = "/ide/task/cleantransform/micro/getList",method = RequestMethod.POST)
    @ResponseBody
    List<TaskCleanTransformInfoEntity> findListByMappingId(@RequestBody String mappingId);



}
