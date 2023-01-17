package com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 10:14
 */

@Mapper
public interface DemoMapper {

    @Select("SELECT * FROM road_vehicle_daily")
    List<Map<String, Object>> findAll();

}
