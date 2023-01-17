package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.mapper;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelAnalysisInDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelCountByDaysEntity;
import com.google.common.base.Joiner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * Description:
 *    车辆油耗数据查询统计
 * @author LiXiaoCong
 * @version 2018/5/14 20:54
 */
@Mapper
public interface VehicleFuelMapper {

    @SelectProvider(type = QureySQLProvider.class, method= "countFuelByDaySQL")
    List<VehicleFuelAnalysisInDaysEntity> countFuelByDay(@Param("vid") String vid,
                                                         @Param("startDay") String startDay,
                                                         @Param("endDay") String endDay);

    @SelectProvider(type = QureySQLProvider.class, method= "countFuelAnalysisInDaysSQL")
    List<VehicleFuelAnalysisInDaysEntity> countFuelAnalysisInDays(@Param("vid") String vid,
                                                                  @Param("startDay") String startDay,
                                                                  @Param("endDay") String endDay);

    @SelectProvider(type = QureySQLProvider.class, method= "countFuelAnalysisByDaysSQL")
    List<VehicleFuelCountByDaysEntity> countFuelAnalysisByDays(@Param("vid") String vid,
                                                               @Param("startDay") String startDay,
                                                               @Param("endDay") String endDay);

    class QureySQLProvider{
        public String countFuelByDaySQL(@Param("vid")String vid){
            SQL sql = new  SQL(){
                {
                    SELECT("vid, day, totalmile, totalfuel, totaltime")
                            .FROM("vehicle_workcondition_fuel")
                            .WHERE("day >= #{startDay} and day <= #{endDay}");

                    if(vid != null){
                        String[] vids = vid.split(",");
                        String condition = String.format("(vid ='%s')", Joiner.on("' or vid ='").skipNulls().join(vids));
                        WHERE(condition);
                    }
                    ORDER_BY("day");
                }
            };
            return sql.toString();
        }

        public String countFuelAnalysisInDaysSQL(@Param("vid")String vid){
            SQL sql = new  SQL(){
                {
                    SELECT("vid, day, totalbrakemile, totalidlefuel, totalmile, totalfuel, totaltime, speedpair, rpmpair,accpair,gearpair,gearspeedpair")
                            .FROM("demo_vehicle_workcondition_fuel")
                            .WHERE("day >= #{startDay} and day <= #{endDay}");

                    if(vid != null){
                        String[] vids = vid.split(",");
                        String condition = String.format("(vid ='%s')", Joiner.on("' or vid ='").skipNulls().join(vids));
                        WHERE(condition);
                    }
                }
            };
            return sql.toString();
        }

        public String countFuelAnalysisByDaysSQL(@Param("vid")String vid){
            SQL sql = new  SQL(){
                {
                    SELECT("vid, sum(totalmile) totalmile, sum(totalfuel) totalfuel, sum(totaltime) totaltime")
                            .FROM("demo_vehicle_workcondition_fuel")
                            .WHERE("day >= #{startDay} and day <= #{endDay}");

                    if(vid != null){
                        String[] vids = vid.split(",");
                        String condition = String.format("(vid ='%s')", Joiner.on("' or vid ='").skipNulls().join(vids));
                        WHERE(condition);
                    }

                    GROUP_BY("vid");
                }
            };
            return sql.toString();
        }
    }
}
