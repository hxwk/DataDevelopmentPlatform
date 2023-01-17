package com.dfssi.dataplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/7/24 13:17
 */
@Mapper
public interface FeatureAnalysisMapper {

    //日平均出行里程
    @SelectProvider(type = QureySQLProvider.class, method= "travelAvgMileageByDaySQL")
    List<Map<String, Object>> travelAvgMileageByDay(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("beginDay") String beginDay,
                                                    @Param("endDay") String endDay);

    //单次平均出行里程
    @SelectProvider(type = QureySQLProvider.class, method= "travelTripAvgMileageByDaySQL")
    List<Map<String, Object>> travelTripAvgMileageByDay(@Param("enterprise") String enterprise,
                                                        @Param("hatchback") String hatchback,
                                                        @Param("vin") String vin,
                                                        @Param("beginDay") String beginDay,
                                                        @Param("endDay") String endDay);

    //单次平均出行里程
    @SelectProvider(type = QureySQLProvider.class, method= "avgTripsByDaySQL")
    List<Map<String, Object>> avgTripsByDay(@Param("enterprise") String enterprise,
                                            @Param("hatchback") String hatchback,
                                            @Param("vin") String vin,
                                            @Param("beginDay") String beginDay,
                                            @Param("endDay") String endDay);

    //单次行驶时长统计
    @SelectProvider(type = QureySQLProvider.class, method= "tripTimeLevelCountSQL")
    List<Map<String, Object>> tripTimeLevelCount(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin,
                                                 @Param("beginDay") String beginDay,
                                                 @Param("endDay") String endDay);

    //单日行驶时长统计
    @SelectProvider(type = QureySQLProvider.class, method= "dayTimeLevelCountSQL")
    List<Map<String, Object>> dayTimeLevelCount(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin,
                                                @Param("beginDay") String beginDay,
                                                @Param("endDay") String endDay);

    //驾驶初始soc
    @SelectProvider(type = QureySQLProvider.class, method= "tripStartSocCountSQL")
    List<Map<String, Object>> tripStartSocCount(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin,
                                                @Param("beginDay") String beginDay,
                                                @Param("endDay") String endDay);

    //驾驶结束soc
    @SelectProvider(type = QureySQLProvider.class, method= "tripStopSocCountSQL")
    List<Map<String, Object>> tripStopSocCount(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback,
                                               @Param("vin") String vin,
                                               @Param("beginDay") String beginDay,
                                               @Param("endDay") String endDay);

    //驾驶时间全天分布
    @SelectProvider(type = QureySQLProvider.class, method= "drivingTimeDistributeCountSQL")
    List<Map<String, Object>> drivingTimeDistributeCount(@Param("enterprise") String enterprise,
                                                         @Param("hatchback") String hatchback,
                                                         @Param("vin") String vin,
                                                         @Param("beginDay") String beginDay,
                                                         @Param("endDay") String endDay);

    //车辆出省/市情况监控
    @Select("select id, vin, enterprise, hatchback, alarm_type, timestamp from evs_vehicle_outbounts where timestamp > #{timePoint} and day >= #{day}  order by timestamp desc")
    List<Map<String, Object>> outBoundsVehicles(@Param("timePoint") long timePoint,  @Param("day") String day);


    class QureySQLProvider extends AbstractQureySQLProvider{

        public String travelAvgMileageByDaySQL(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback,
                                               @Param("vin") String vin,
                                               @Param("beginDay") String beginDay,
                                               @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("model, avg(totalmile) dayAvgMile, avg(totaltime) dayAvgTime, sum(totalmile) totalmile, sum(totalpower) totalpower").FROM("evs_driving_day");
                    WHERE("totalmile > 0.0 and (model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "day");
                    GROUP_BY("model");
                }
            }.toString();
        }

        public String travelTripAvgMileageByDaySQL(@Param("enterprise") String enterprise,
                                                   @Param("hatchback") String hatchback,
                                                   @Param("vin") String vin,
                                                   @Param("beginDay") String beginDay,
                                                   @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("model, avg(mile) dayAvgTripMile, avg(time) dayAvgTripTime").FROM("evs_driving_trip");
                    WHERE("mile > 0.0 and (model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "start_day");
                    GROUP_BY("model");
                }
            }.toString();
        }

        public String avgTripsByDaySQL(@Param("enterprise") String enterprise,
                                       @Param("hatchback") String hatchback,
                                       @Param("vin") String vin,
                                       @Param("beginDay") String beginDay,
                                       @Param("endDay") String endDay){

            //每天平均出行次数
            String dayTrips = new SQL() {
                {
                    SELECT("model, count(1)/count(distinct vin) avgTrips").FROM("evs_driving_trip");
                    WHERE("mile > 0.0 and (model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "start_day");
                    GROUP_BY("model, start_day");
                }
            }.toString();

            //算每天平均值
            return new SQL(){
                {
                    SELECT("model, avg(avgTrips) avgTrips")
                            .FROM(String.format("(%s) t", dayTrips)).GROUP_BY("model");
                }
            }.toString();
        }

        public String tripTimeLevelCountSQL(@Param("enterprise") String enterprise,
                                            @Param("hatchback") String hatchback,
                                            @Param("vin") String vin,
                                            @Param("beginDay") String beginDay,
                                            @Param("endDay") String endDay){

            //每次出行时间级别统计
           return new SQL() {
                {
                    SELECT("model, time_level, count(distinct vin) vehicles").FROM("evs_driving_trip");
                    WHERE("(model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "start_day");
                    GROUP_BY("model, time_level");
                }
            }.toString();

        }

        public String dayTimeLevelCountSQL(@Param("enterprise") String enterprise,
                                           @Param("hatchback") String hatchback,
                                           @Param("vin") String vin,
                                           @Param("beginDay") String beginDay,
                                           @Param("endDay") String endDay){

            //每日出行时间级别统计
           return new SQL() {
                {
                    SELECT("model, totaltimelevel, count(distinct vin) vehicles").FROM("evs_driving_day");
                    WHERE("(model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "day");
                    GROUP_BY("model, totaltimelevel");
                }
            }.toString();

        }

        public String tripStartSocCountSQL(@Param("enterprise") String enterprise,
                                           @Param("hatchback") String hatchback,
                                           @Param("vin") String vin,
                                           @Param("beginDay") String beginDay,
                                           @Param("endDay") String endDay){

            //驾驶开始soc统计
           return new SQL() {
                {
                    SELECT("model, start_soc, count(distinct vin) vehicles").FROM("evs_driving_trip");
                    WHERE("(model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "start_day");
                    GROUP_BY("model, start_soc");
                }
            }.toString();

        }

        public String tripStopSocCountSQL(@Param("enterprise") String enterprise,
                                          @Param("hatchback") String hatchback,
                                          @Param("vin") String vin,
                                          @Param("beginDay") String beginDay,
                                          @Param("endDay") String endDay){

            //驾驶结束soc统计
           return new SQL() {
                {
                    SELECT("model, stop_soc, count(distinct vin) vehicles").FROM("evs_driving_trip");
                    WHERE("(model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "start_day");
                    GROUP_BY("model, stop_soc");
                }
            }.toString();

        }

        public String drivingTimeDistributeCountSQL(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("beginDay") String beginDay,
                                                    @Param("endDay") String endDay){

            //全天行驶时长分布统计
           return new SQL() {
                {
                   String colums = "sum(timelevel0) timelevel0, sum(timelevel1) timelevel1, sum(timelevel2) timelevel2, " +
                           "sum(timelevel3) timelevel3, sum(timelevel4) timelevel4, sum(timelevel5) timelevel5, " +
                           "sum(timelevel6) timelevel6, sum(timelevel7) timelevel7";

                    SELECT("model, " + colums).FROM("evs_driving_day");
                    WHERE("(model < 3)");
                    addCondition(this, enterprise, hatchback, vin, beginDay, endDay, "day");
                    GROUP_BY("model");
                }
            }.toString();

        }




    }
}
