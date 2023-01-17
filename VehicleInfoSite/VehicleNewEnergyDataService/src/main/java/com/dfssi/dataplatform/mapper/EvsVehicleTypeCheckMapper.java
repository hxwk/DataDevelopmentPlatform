package com.dfssi.dataplatform.mapper;

import com.dfssi.dataplatform.entity.database.EvsVehicleTypeDetect;
import com.google.common.base.Joiner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * Description:
 *   车型联调相关的质量检测查询接口
 * @author LiXiaoCong
 * @version 2018/5/29 8:53
 */
@Mapper
public interface EvsVehicleTypeCheckMapper {

    @SelectProvider(type = QureySQLProvider.class, method= "countDectectResultSQL")
    List<EvsVehicleTypeDetect> countDectectResult(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin,
                                                  @Param("startDay") String startDay,
                                                  @Param("endDay") String endDay);

    class QureySQLProvider{

        /**
         * select t1.vin,t1.hatchback,t1.enterprise, t1.errorCount, t2.totalCount from (
         * 	(select vin,hatchback,enterprise,count errorCount from evs_err_day) t1
         * LEFT JOIN
         * 	(select vin,count totalCount from evs_driving_day) t2
         * on t1.vin = t2.vin)
         */
        public String countDectectResultSQL(@Param("enterprise") String enterprise,
                                            @Param("hatchback") String hatchback,
                                            @Param("vin") String vin,
                                            @Param("startDay") String startDay,
                                            @Param("endDay") String endDay){

           SQL errSQL =  new SQL().SELECT("vin,count errorCount, day")
                   .FROM("evs_err_day");
           addCondition(errSQL, enterprise, hatchback, vin, startDay, endDay);

           SQL totalSQL =  new SQL().SELECT("vin,count totalCount, day")
                   .FROM("evs_driving_day");
           addCondition(totalSQL, enterprise, hatchback, vin, startDay, endDay);

            SQL sql = new SQL().SELECT("t1.day,t1.vin, t1.errorCount, t2.totalCount")
                    .FROM(String.format("((%s) t1 LEFT JOIN (%s) t2 on t1.vin = t2.vin and t1.day = t2.day)",
                            errSQL.toString(), totalSQL.toString()))
                    .ORDER_BY("day");

            return sql.toString();

        }

        private void addCondition(SQL sql,
                                  String enterprise,
                                  String hatchback,
                                  String vin,
                                  String startDay,
                                  String endDay){

            if(startDay != null && endDay != null) {
                if (startDay.equals(endDay)) {
                    sql.WHERE("day=#{startDay}");
                } else {
                    sql.WHERE("day >= #{startDay} and day <= #{endDay}");
                }
            }

            if(vin != null){
                String[] vins = vin.split(",");
                String condition = String.format("(vin ='%s')", Joiner.on("' or vin ='").skipNulls().join(vins));
                sql.WHERE(condition);
            }

            if(hatchback != null){
                String[] hatchbacks = hatchback.split(",");
                String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
                sql.WHERE(condition);
            }

            if(enterprise != null){
                String[] enterprises = enterprise.split(",");
                String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
                sql.WHERE(condition);
            }
        }

    }
}
