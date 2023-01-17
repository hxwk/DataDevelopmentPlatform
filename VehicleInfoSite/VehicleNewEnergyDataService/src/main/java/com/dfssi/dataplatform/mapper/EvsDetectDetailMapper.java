package com.dfssi.dataplatform.mapper;

import com.dfssi.dataplatform.entity.database.EvsDetectDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 10:48
 */
@Mapper
public interface EvsDetectDetailMapper {

    @Select("select id, detect_name, detect_desc, detect_alarm_type, detect_alarm_level, detect_alarm_content from evs_detect_detail")
    List<EvsDetectDetail> queryAll();


    @Select("SELECT id, detect_name, detect_desc, detect_value, detect_type, null_able, detect_value_unit_name FROM evs_detect_detail where detect_alarm_type = 2")
    List<Map<String, Object>> allBaseDetectDetail();

}
