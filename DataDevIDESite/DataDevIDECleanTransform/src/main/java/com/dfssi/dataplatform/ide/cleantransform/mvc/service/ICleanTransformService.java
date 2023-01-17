package com.dfssi.dataplatform.ide.cleantransform.mvc.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.SerResponseEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformMapperEntity;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformModelEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * Created by hongs on 2018/5/22.
 */
public interface ICleanTransformService {

    int saveCleanTransformModel(List<String> lists);

    /**
     * 根据id删除字段映射主表信息
     * @param id
     * @return
     */
    int deleteMappingById(String id);

    int updateCleanTransformModel(String jsonStr);

    List<TaskCleanTransformMapperEntity> findeEntityList();

    /**
     * 查询字段映射主表信息
     * @param entity
     * @param pageParam
     * @return
     */
    PageResult<TaskCleanTransformEntity> getMappings(TaskCleanTransformEntity entity, PageParam pageParam);

    /**
     * 保存字段映射主表和字表信息
     * @param entity
     * @return
     */
    SerResponseEntity saveModel(TaskCleanTransformModelEntity entity);

    /**
     * 清洗转换字段映射根据id查询主表和子表信息
     * @param id
     * @return
     */
    Object getModel(String id);

    Object deleteSingleByMappingId(String id);

    /**
     * 根据映射ID查询字表list
     * @param id
     * @return
     */
    List<TaskCleanTransformMapperEntity> findListByMappingId(String id);

    List<TaskCleanTransformEntity> getAllCleanTranses();

    /**
     * 参数校验异常捕获判断
     * @param bindingResult
     * @return
     */
    String paramValid(BindingResult bindingResult);

}
