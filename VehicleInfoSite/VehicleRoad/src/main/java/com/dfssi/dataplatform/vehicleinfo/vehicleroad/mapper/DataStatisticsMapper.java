package com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
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
 * @version 2018/10/7 11:09
 */
@Mapper
public interface DataStatisticsMapper {

    /**
     * @param vid
     * @param startDay
     * @param stopDay
     * @return
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryStatisticsByDaySQL")
    List<Map<String, Object>> queryStatisticsByDay(@Param("vid") String vid,
                                                   @Param("startDay") String startDay,
                                                   @Param("stopDay") String stopDay);

    /**
     * @param vid
     * @param startDay
     * @param stopDay
     * @return
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryStatisticsTripSQL")
    List<Map<String, Object>> queryStatisticsTrip(@Param("vid") String vid,
                                                  @Param("startDay") String startDay,
                                                  @Param("stopDay") String stopDay);

    /**
     * @param vid
     * @param startDay
     * @param startDay
     * @return
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryStatisticsByMonthSQL")
    List<Map<String, Object>> queryStatisticsByMonth(@Param("vid") String vid,
                                                     @Param("startDay") String startDay,
                                                     @Param("stopDay") String stopDay);


    class QureySQLProvider {

        public String queryStatisticsTripSQL(@Param("vid") String vid){
            String sql;
            if(!Strings.isNullOrEmpty(vid)){
                Iterable<String> vids = Splitter.on(",").omitEmptyStrings().split(vid);

                List<String> sqls = Lists.newArrayList();
                vids.forEach(id ->{
                    sqls.add(new SQL(){
                        {
                            SELECT("id,vid,start_time,stop_time,avg_fuel,avg_speed,avg_speed,max_speed,mile,gps_mile,driving_time,fuel_consumption,idle_time,day, stop_mile total_mile")
                                    .FROM("road_vehicle_daily_trip")
                                    .WHERE(String.format("vid='%s'", id))
                                    .WHERE("day >= #{startDay} and day <= #{stopDay}")
                                    .ORDER_BY("start_time desc");
                        }
                    }.toString());
                });
               sql = Joiner.on(" UNION ").join(sqls);
            }else{
                sql = new SQL(){
                    { SELECT("id,vid,start_time,stop_time,avg_fuel,avg_speed,avg_speed,max_speed,mile,gps_mile,driving_time,fuel_consumption,idle_time,day, stop_mile total_mile")
                            .FROM("road_vehicle_daily_trip")
                            .WHERE("day >= #{startDay} and day <= #{stopDay} and driving_time > 0").ORDER_BY("start_time desc"); }
                }.toString();
            }
            return sql;
        }

        /**按天统计
         * @param vid
         * @return
         */
        public String queryStatisticsByDaySQL(@Param("vid") String vid){
            String sql;
            if(!Strings.isNullOrEmpty(vid)){
                Iterable<String> vids = Splitter.on(",").omitEmptyStrings().split(vid);

                List<String> sqls = Lists.newArrayList();
                vids.forEach(id ->{
                    sqls.add(new SQL(){
                        {
                            SELECT("vid ,day, avg_fuel, avg_speed, max_speed," +
                                    " mile, gps_mile, driving_time, idle_time, fuel_consumption, stop_mile total_mile")
                                    .FROM("road_vehicle_daily_count")
                                    .WHERE(String.format("vid='%s'", id))
                                    .WHERE("day >= #{startDay} and day <= #{stopDay}")
                                    .ORDER_BY("day desc");
                        }
                    }.toString());
                });
               sql = Joiner.on(" UNION ").join(sqls);
            }else{
                sql = new SQL(){
                    { SELECT("vid ,day, avg_fuel, avg_speed, max_speed," +
                            " mile, gps_mile, driving_time, idle_time, fuel_consumption, stop_mile total_mile")
                            .FROM("road_vehicle_daily_count")
                            .WHERE("day >= #{startDay} and day <= #{stopDay}")
                            .ORDER_BY("day desc"); }
                }.toString();
            }
            return sql;
        }


        /**按月统计
         * @param vid
         * @return
         */
        public String queryStatisticsByMonthSQL(@Param("vid") String vid){
            String sql;
            if(!Strings.isNullOrEmpty(vid)){
                Iterable<String> vids = Splitter.on(",").omitEmptyStrings().split(vid);

                List<String> sqls = Lists.newArrayList();
                vids.forEach(id ->{
                    sqls.add(new SQL(){
                        {
                            SELECT("vid ,month, avg(avg_fuel) avg_fuel, avg(avg_speed) avg_speed, max(max_speed) max_speed, max(stop_mile) total_mile," +
                                            " sum(mile) mile, sum(gps_mile) gps_mile, sum(driving_time) driving_time, sum(idle_time) idle_time, sum(fuel_consumption) fuel_consumption")
                                    .FROM("road_vehicle_daily_count")
                                    .WHERE(String.format("vid='%s'", id))
                                    .WHERE("day >= #{startDay} and day <= #{stopDay}")
                                    .GROUP_BY("month", "vid");
                        }
                    }.toString());
                });
               sql = Joiner.on(" UNION ").join(sqls);
            }else{
                sql = new SQL(){
                    { SELECT("vid ,month, avg(avg_fuel) avg_fuel, avg(avg_speed) avg_speed, max(max_speed) max_speed, max(stop_mile) total_mile," +
                            " sum(mile) mile, sum(gps_mile) gps_mile, sum(driving_time) driving_time, sum(idle_time) idle_time, sum(fuel_consumption) fuel_consumption")
                            .FROM("road_vehicle_daily_count")
                            .WHERE("day >= #{startDay} and day <= #{stopDay}")
                            .GROUP_BY("month", "vid").ORDER_BY("month desc"); }
                }.toString();
            }
            return sql;
        }

    }
}
