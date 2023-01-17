package com.dfssi.dataplatform.mapper;

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

    @Select("SELECT * FROM SSI_VEHICLE")
    List<Map<String, Object>> findAll();


    @Select("SELECT id, detect_name, detect_desc, detect_value, detect_type, null_able FROM evs_detect_detail")
    List<Map<String, Object>> listAll();

}
