package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.entity.DicEntity;

import java.util.List;

public interface DicService {
    /**
     * 显示字典主表
     * @return
     */
    PageResult<DicEntity> listGetDic(PageParam pageParam,DicEntity entity);

    /**
     * 新增/修改主表信息
     * @param dicEntity
     * @return
     */
    String insert(DicEntity dicEntity);

    /**
     * 根据字典类型删除
     * @param dicType
     * @return
     */
    String deleteByType(String dicType);

    /**
     * 更改有效性
     * @param dicEntity
     * @return
     */
    String changeValid(DicEntity dicEntity);

    /**
     * 有效字典信息
     * @return
     */
    List<DicEntity> listGetDicType();

}
