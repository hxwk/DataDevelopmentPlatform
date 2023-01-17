package com.dfssi.dataplatform.ide.access.mvc.restful;

import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskCleanTransformInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskCleanTransformMappingEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 清洗转换微服务断路器，微服务调用失败或超时时使用
 * Created by yanghs on 2018/7/27.
 */
@Component
public class CleanTransformHystrix implements ICleanTransformFeign {
   @Override
   public List<TaskCleanTransformMappingEntity> getAllCleanTranses() {
       ResponseObj responseObj = ResponseObj.createResponseObj();
       responseObj.setData("");
       responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取清洗转换微服务调用失败，请检查微服务是否开启");
       return null;
   }

    @Override
    public List<TaskCleanTransformInfoEntity> findListByMappingId(String mappingId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.setData("");
        responseObj.setStatus(ResponseObj.CODE_FAIL_B,"失败！","获取清洗转换微服务调用失败，请检查微服务是否开启");
        return null;
    }


}
