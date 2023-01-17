package com.dfssi.dataplatform.abs.mapper;

import com.dfssi.dataplatform.abs.entity.AbsVehicleStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/10/28 15:06
 */
@Mapper
public interface AbsVehicleStatusMapper {

    void insert(AbsVehicleStatusEntity acre);

    void update(AbsVehicleStatusEntity acre);

    void delect(String vid);

    List<AbsVehicleStatusEntity> listStatus(@Param("vidList")List<String> vidList);
}
