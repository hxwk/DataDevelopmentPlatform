package com.dfssi.dataplatform.ide.cleantransform.mvc.dao;

import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.TaskCleanTransformMapperEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hongs on 2018/5/10.
 */
@Mapper
public interface ITaskCleanTransformMapperDao {
    /**
     * 批量插入清洗转换bean
     * @param TaskCleanTransformInfoEntities
     * @return
     */
    int batchInsert(@Param("list") List<TaskCleanTransformMapperEntity> TaskCleanTransformInfoEntities);

    /**
     * 修改操作
     * @param tctie
     * @return
     */
    int update(TaskCleanTransformMapperEntity tctie);

    /**
     * 查询所有数据列表
     *
     * @return
     */
    List<TaskCleanTransformMapperEntity> findAllList();

    /**
     * 根据外键映射mappingid逻辑删除子表
     * @param id
     * @return
     */
    int deleteByMappingId(String id);

    /**
     * 插入子表list数据
     * @param TaskCleanTransformInfoEntities
     * @return
     */
    int insertMutil(@Param("list") List<TaskCleanTransformMapperEntity> TaskCleanTransformInfoEntities);

    /**
     * 根据mappingId查询子表list
     * @param id
     * @return
     */
    List<TaskCleanTransformMapperEntity> findListByMappingId(String id);
}
