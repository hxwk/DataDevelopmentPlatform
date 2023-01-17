package com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/8 11:37
 */
@Mapper
public interface DataFieldMapper {

    @SelectProvider(type = QureySQLProvider.class, method = "findAllSQL")
    List<Map<String, Object>>  findAll(@Param("label") String label);

    class QureySQLProvider {

        /**
         * @param label
         * @return
         */
        public String findAllSQL(@Param("label") String label) {

            if(label != null){
                return new SQL(){
                    {
                        SELECT("id, field_name, field_label, field_unit")
                                .FROM("road_vehicle_fields")
                                .WHERE("field_label like '%" + label + "%'");
                    }
                }.toString();
            }else{
                return new SQL(){
                    {
                        SELECT("id, field_name, field_label, field_unit").FROM("road_vehicle_fields");
                    }
                }.toString();
            }

        }
    }

}
