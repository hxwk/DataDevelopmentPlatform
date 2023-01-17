package com.dfssi.dataplatform.mapper;

import com.dfssi.dataplatform.entity.count.*;
import com.google.common.base.Joiner;
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
 * @version 2018/4/21 12:55
 */
@Mapper
public interface EvsDataCountResultMapper {

    /*查询车辆总行驶里程、车辆总行驶时长
    @SelectProvider(type = QureySQLProvider.class, method = "queryTotalMileAndTimeSQL")
    Object queryTotalMileAndTime(@Param("vin") String vin); */

    //查询所有车辆总行驶里程、车辆总行驶时长
    @Select("select sum(totalmile) totalMileage, sum(totaltime) totalDuration , sum(totalpower) totalPowerConsumption from evs_driving_total")
    TotalMileAndTime queryTotalMileAndTime();

    //查询单一车辆的VIN
    @Select("select totalmile totalMileage, totaltime totalDuration , totalpower totalPowerConsumption, totalcharge, totalrun from evs_driving_total where vin=#{vin}")
    TotalRunningMsg queryTotalMileAndTimeByVin(@Param("vin") String vin);

    //查询汇总所有车辆耗电量
    @Select("select sum(totalpower) totalPowerConsumption from evs_driving_total")
    Map<String, Double> queryTotalEnergy();

    //查询车辆的仪表盘里程 和 gps里程 用于里程核算
    @Select("select sum(totalmile) totalMile, sum(totalgpsmile) totalGpsMile from evs_driving_day where vin=#{vin} and day >= #{startDay} and day <= #{endDay}")
    MileDetail queryMileDetail(@Param("vin") String vin,
                               @Param("startDay") String startDay,
                               @Param("endDay") String endDay);

    //按天统计每天在线车辆和运行车辆
    @SelectProvider(type = QureySQLProvider.class, method = "queryMaxOnlineAndRunByDaySQL")
    List<MaxOnlineAndRun> queryMaxOnlineAndRunByDay(@Param("startDay") String startDay,
                                                    @Param("endDay") String endDay);

    //统计某月的在线车辆和运行车辆
    @SelectProvider(type = QureySQLProvider.class, method = "countMonthTotalOnlineAndRun")
    Map<String, Object> countMonthTotalOnlineAndRun(@Param("startDay") String startDay,
                                                      @Param("endDay") String endDay);

    //查询车辆里程统计信息
    @SelectProvider(type = QureySQLProvider.class, method = "countVehicleMileAndTimeSQL")
    List<VehicleMileAndTime> countVehicleMileAndTime(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin")String vin,
                                                     @Param("startDay") String startDay,
                                                     @Param("endDay") String endDay);

    //查询告警级别统计信息
    @SelectProvider(type = QureySQLProvider.class, method = "queryWarningCountByDaySQL")
    List<Map<String, Object>> queryWarningCountByDay(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin,
                                                     @Param("startDay") String startDay,
                                                     @Param("endDay") String endDay);

    //查询告警级别统计信息
    @SelectProvider(type = QureySQLProvider.class, method = "queryWarningCountSQL")
    Map<String, Object> queryWarningCount(@Param("enterprise") String enterprise,
                                          @Param("hatchback") String hatchback,
                                          @Param("vin") String vin,
                                          @Param("startDay") String startDay,
                                          @Param("endDay") String endDay);

    //查询告警级别统计信息
    @SelectProvider(type = QureySQLProvider.class, method = "queryWarningCountTotalSQL")
    Map<String, Object> queryWarningCountTotal(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback,
                                               @Param("vin") String vin);

    //统计车辆在线数量信息
    @SelectProvider(type = QureySQLProvider.class, method = "queryOnlineCountByDaySQL")
    List<VehicleOnlineCount> queryOnlineCountByDay(@Param("enterprise") String enterprise,
                                                   @Param("hatchback") String hatchback,
                                                   @Param("startDay") String startDay,
                                                   @Param("endDay") String endDay);

    //统计车辆在线数量信息 只按车企分组
    @SelectProvider(type = QureySQLProvider.class, method = "queryOnlineCountInEnterpriseByDaySQL")
    List<VehicleOnlineCount> queryOnlineCountInEnterpriseByDay(@Param("enterprise") String enterprise,
                                                               @Param("hatchback") String hatchback,
                                                               @Param("startDay") String startDay,
                                                               @Param("endDay") String endDay);

    class QureySQLProvider {

        public String queryMaxOnlineAndRunByDaySQL(@Param("startDay") String startDay,
                                                   @Param("endDay") String endDay) {
            return new SQL() {
                {
                    String onlineSQL = queryMaxOnlineSQL(startDay, endDay);
                    String runningSQL = queryMaxRunningSQL(startDay, endDay);

                    String table = String.format("((%s) online left outer join (%s) running on running.runningDay = online.onlineDay) t",
                            onlineSQL, runningSQL);

                    SELECT("onlineday date, onlineCount maxOnline, runningCount maxRun")
                            .FROM(table).ORDER_BY("onlineday");
                }
            }.toString();
        }

        public String queryMaxOnlineSQL(@Param("startDay") String startDay,
                                        @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("day onlineDay, count(vin) onlineCount")
                            .FROM("evs_driving_day")
                            .WHERE(String.format("day >= %s AND day <= %s", startDay, endDay))
                            .GROUP_BY("day");
                }
            }.toString();
        }

        public String queryMaxRunningSQL(@Param("startDay") String startDay,
                                         @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("day runningDay, count(vin) runningCount")
                            .FROM("evs_driving_day")
                            .WHERE(String.format("totalmile >= 1 and day >= %s AND day <= %s", startDay, endDay))
                            .GROUP_BY("day");
                }
            }.toString();
        }

        //统计某月的在线车辆已经行驶车辆
        public String countMonthTotalOnlineAndRun(@Param("startDay") String startDay,
                                                  @Param("endDay") String endDay) {
            return new SQL() {
                {
                    String onlineSQL = queryOnlineSQL(startDay, endDay);
                    String runningSQL = queryRunningSQL(startDay, endDay);

                    String table = String.format("(%s) online , (%s) running ",
                            onlineSQL, runningSQL);

                    SELECT("online.totalOnline, running.totalRunning")
                            .FROM(table)
                            .WHERE("online.res = running.res");
                }
            }.toString();
        }


        public String queryOnlineSQL(@Param("startDay") String startDay,
                                     @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("count(DISTINCT vin) totalOnline, '1' res")
                            .FROM("evs_driving_day")
                            .WHERE(String.format("day >= %s AND day <= %s", startDay, endDay));
                }
            }.toString();
        }

        public String queryRunningSQL(@Param("startDay") String startDay,
                                      @Param("endDay") String endDay){
            return new SQL(){
                {
                    SELECT("count(DISTINCT vin) totalRunning, '1' res")
                            .FROM("evs_driving_day")
                            .WHERE(String.format("totalmile >= 1 and day >= %s AND day <= %s", startDay, endDay));
                }
            }.toString();
        }



        public String queryWarningCountByDaySQL(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin){
            return new SQL() {
                {
                   SELECT("sum(alarm1) alarm1Count, sum(alarm2) alarm2Count, sum(alarm3) alarm3Count, day date")
                           .FROM("evs_driving_day");

                    if(enterprise != null){
                        String[] enterprises = enterprise.split(",");
                        String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
                        WHERE(condition);
                    }

                    if(hatchback != null){
                        String[] hatchbacks = hatchback.split(",");
                        String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
                        WHERE(condition);
                    }

                    if(vin != null && !vin.isEmpty()){
                        String[] vins = vin.split(",");
                        String condition = String.format("(vin ='%s')", Joiner.on("' or vin ='").skipNulls().join(vins));
                        WHERE(condition);
                    }

                   WHERE("day >= #{startDay} AND day <= #{endDay}")
                           .GROUP_BY("day")
                           .ORDER_BY("day");
                }
            }.toString();

        }

        public String queryWarningCountSQL(@Param("enterprise") String enterprise,
                                           @Param("hatchback") String hatchback,
                                           @Param("vin") String vin,
                                           @Param("startDay") String startDay,
                                           @Param("endDay") String endDay){
            SQL sql = new SQL() {
                {
                   SELECT("sum(alarm1) alarm1Count, sum(alarm2) alarm2Count, sum(alarm3) alarm3Count")
                           .FROM("evs_driving_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return sql.toString();

        }

        public String queryWarningCountTotalSQL(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(alarm1) alarm1Count, sum(alarm2) alarm2Count, sum(alarm3) alarm3Count")
                            .FROM("evs_driving_total");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();
        }


        public String countVehicleMileAndTimeSQL(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin){
            return new SQL() {
                {
                    SELECT("enterprise, hatchback, vin, sum(totalmile) totalMileage, sum(totaltime) totalDuration")
                            .FROM("evs_driving_day");
                    if(enterprise != null){
                        String[] enterprises = enterprise.split(",");
                        String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
                        WHERE(condition);
                    }

                    if(hatchback != null){
                        String[] hatchbacks = hatchback.split(",");
                        String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
                        WHERE(condition);
                    }

                    if(vin != null && !vin.isEmpty()){
                        String[] vins = vin.split(",");
                        String condition = String.format("(vin ='%s')", Joiner.on("' or vin ='").skipNulls().join(vins));
                        WHERE(condition);
                    }

                    WHERE("day >= #{startDay} AND day <= #{endDay}")
                            .GROUP_BY("enterprise, hatchback, vin")
                            .ORDER_BY("totalMileage desc");
                }
            }.toString();

        }


        public String queryOnlineCountByDaySQL(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback){
            return new SQL() {
                {
                    SELECT("enterprise, hatchback, count(DISTINCT vin) totalVehicle, sum(totaltime) totalDuration")
                            .FROM("evs_driving_day");

                    if(enterprise != null){
                        String[] enterprises = enterprise.split(",");
                        String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
                        WHERE(condition);
                    }

                    if(hatchback != null){
                        String[] hatchbacks = hatchback.split(",");
                        String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
                        WHERE(condition);
                    }


                    WHERE("day >= #{startDay} AND day <= #{endDay}")
                            .GROUP_BY("enterprise, hatchback");
                }
            }.toString();

        }

        public String queryOnlineCountInEnterpriseByDaySQL(@Param("enterprise") String enterprise,
                                                           @Param("hatchback") String hatchback){
            return new SQL() {
                {
                    SELECT("enterprise, count(DISTINCT vin) totalVehicle, sum(totaltime) totalDuration")
                            .FROM("evs_driving_day");

                    if(enterprise != null){
                        String[] enterprises = enterprise.split(",");
                        String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
                        WHERE(condition);
                    }

                    if(hatchback != null){
                        String[] hatchbacks = hatchback.split(",");
                        String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
                        WHERE(condition);
                    }


                    WHERE("day >= #{startDay} AND day <= #{endDay}")
                            .GROUP_BY("enterprise");
                }
            }.toString();

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
