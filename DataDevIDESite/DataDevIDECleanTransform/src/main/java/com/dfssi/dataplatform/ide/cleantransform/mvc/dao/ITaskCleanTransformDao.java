package com.dfssi.dataplatform.ide.cleantransform.mvc.dao;

import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by hongs on 2018/5/10.
 */
@Mapper
public interface ITaskCleanTransformDao {

    /**
     * 查询清洗转换主表数据
     * @return
     */
    List<TaskCleanTransformEntity> getAllCleanTranses();

    /**
     * 修改操作
     * @return
     */
    int update(TaskCleanTransformEntity taskcleantransformmappingentity);

    /**
     * 新增操作
     * @param taskcleantransformmappingentity
     * @return
     */
    int insert(TaskCleanTransformEntity taskcleantransformmappingentity);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    int delete(String id);

    /**
     * 分页条件查询
     * @param entity
     * @return
     */
    List<TaskCleanTransformEntity> findList(TaskCleanTransformEntity entity);

    /**
     * 清洗转换字段映射根据id查询主表信息
     * @param id
     * @return
     */
    TaskCleanTransformEntity get(String id);

    /**
     * 名称唯一性验证
     * @param
     * @return
     */
    List<TaskCleanTransformEntity> queryRepeatName(TaskCleanTransformEntity taskcleanTransformEntity);

}
