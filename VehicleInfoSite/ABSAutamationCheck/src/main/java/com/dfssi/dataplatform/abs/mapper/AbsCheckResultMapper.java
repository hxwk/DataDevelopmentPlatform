package com.dfssi.dataplatform.abs.mapper;

import com.dfssi.dataplatform.abs.entity.AbsCheckRecordEntity;
import com.dfssi.dataplatform.abs.entity.AbsCheckResultEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/10/27 9:22
 */
@Mapper
public interface AbsCheckResultMapper {

    void insert(AbsCheckResultEntity acre);

    void update(AbsCheckResultEntity acre);

    AbsCheckResultEntity getResultByVid(String vid);

    List<AbsCheckResultEntity> listCheckResult(Map map);

    void delect(String vid);
}
