package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.DicEntity;

import java.util.List;

public interface DicService {
    /**
     * 显示字典主表
     * @return
     */
    PageResult<DicEntity> listGetDic(PageParam pageParam);

    /**
     * 新增/修改主表信息
     * @param dicEntity
     * @return
     */
    String insert(DicEntity dicEntity);

    /**
     * 根据主键删除
     * @param dicType
     * @return
     */
    String deleteById(String id);

    /**
     * 更改有效性
     * @param dicEntity
     * @return
     */
    String changeValid(DicEntity dicEntity);
}
