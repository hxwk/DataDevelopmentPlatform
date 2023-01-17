package com.dfssi.dataplatform.mapper;

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
 *   数据质量检测统计结果查询
 * @author LiXiaoCong
 * @version 2018/4/26 11:47
 */
@Mapper
public interface EvsDataDetectResultMapper {

    /**
     * 查询 指定日期 指定车辆 的数据质量检测统计信息
     */
    @Select("select * from evs_err_day where vin=#{vin} and day=${day}")
    Map<String, Object> queryDataQualityInDay(@Param("vin") String vin,
                                              @Param("day")String day);


    /**
     *查询 整车数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryVehicleDataQualitySQL")
    Map<String, Object>  queryVehicleDataQuality(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin);

    /**
     * 查询 指定日期范围 整车数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryVehicleDataQualityInDaySQL")
    Map<String, Object>  queryVehicleDataQualityInDay(@Param("enterprise") String enterprise,
                                                      @Param("hatchback") String hatchback,
                                                      @Param("vin") String vin,
                                                      @Param("startDay")String startDay,
                                                      @Param("endDay")String endDay);
    /**
     *查询 驱动电机数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryDriverMotorDataQualitySQL")
    Map<String, Object>  queryDriverMotorDataQuality(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin);

    /**
     * 查询 指定日期范围 驱动电机数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryDriverMotorDataQualityInDaySQL")
    Map<String, Object>  queryDriverMotorDataQualityInDay(@Param("enterprise") String enterprise,
                                                          @Param("hatchback") String hatchback,
                                                          @Param("vin") String vin,
                                                          @Param("startDay")String startDay,
                                                          @Param("endDay")String endDay);

    /**
     *查询 燃料电池 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryFuelCellDataQualitySQL")
    Map<String, Object>  queryFuelCellDataQuality(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin);

    /**
     * 查询 指定日期范围 燃料电池数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryFuelCellDataQualityInDaySQL")
    Map<String, Object>  queryFuelCellDataQualityInDay(@Param("enterprise") String enterprise,
                                                       @Param("hatchback") String hatchback,
                                                       @Param("vin") String vin,
                                                       @Param("startDay")String startDay,
                                                       @Param("endDay")String endDay);

    /**
     *查询 发动机 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryEngineDataQualitySQL")
    Map<String, Object>  queryEngineDataQuality(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin);

    /**
     * 查询 指定日期范围 发动机 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryEngineDataQualityInDaySQL")
    Map<String, Object>  queryEngineDataQualityInDay(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin,
                                                     @Param("startDay")String startDay,
                                                     @Param("endDay")String endDay);

    /**
     *查询 位置数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryGpsDataQualitySQL")
    Map<String, Object>  queryGpsDataQuality(@Param("enterprise") String enterprise,
                                             @Param("hatchback") String hatchback,
                                             @Param("vin") String vin);

    /**
     * 查询 指定日期范围 位置数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryGpsDataQualityInDaySQL")
    Map<String, Object>  queryGpsDataQualityInDay(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin,
                                                  @Param("startDay")String startDay,
                                                  @Param("endDay")String endDay);

    /**
     *查询 极值数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryExtremumDataQualitySQL")
    Map<String, Object>  queryExtremumDataQuality(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin);

    /**
     * 查询 极值数据 位置数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryExtremumDataQualityInDaySQL")
    Map<String, Object>  queryExtremumDataQualityInDay(@Param("enterprise") String enterprise,
                                                       @Param("hatchback") String hatchback,
                                                       @Param("vin") String vin,
                                                       @Param("startDay")String startDay,
                                                       @Param("endDay")String endDay);

    /**
     *查询 告警数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryAlarmDataQualitySQL")
    Map<String, Object>  queryAlarmDataQuality(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback,
                                               @Param("vin") String vin);

    /**
     * 查询 告警数据 位置数据 质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method= "queryAlarmDataQualityInDaySQL")
    Map<String, Object>  queryAlarmDataQualityInDay(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("startDay")String startDay,
                                                    @Param("endDay")String endDay);

    /**
     * 查询 数据质量 逻辑检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDataLogicCheckSQL")
    Map<String, Object> queryDataLogicCheck(@Param("enterprise") String enterprise,
                                            @Param("hatchback") String hatchback,
                                            @Param("vin") String vin);

    /**
     * 查询 数据质量 逻辑检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDataLogicCheckInDaySQL")
    Map<String, Object> queryDataLogicCheckInDay(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin,
                                                 @Param("startDay") String startDay,
                                                 @Param("endDay") String endDay);
    /**
     * 按天聚合 指定条件下的数据质量检测信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectDataQualityByDaySQL")
    List<Map<String, Object>> queryDetectDataQualityByDay(@Param("enterprise") String enterprise,
                                                          @Param("hatchback") String hatchback,
                                                          @Param("vin") String vin,
                                                          @Param("startDay") String startDay,
                                                          @Param("endDay") String endDay);

    /**
     *  按天聚合 指定条件下的数据质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectQualityCountByDaySQL")
    List<Map<String, Object>> queryDetectQualityCountByDay(@Param("enterprise") String enterprise,
                                                           @Param("hatchback") String hatchback,
                                                           @Param("vin") String vin,
                                                           @Param("startDay") String startDay,
                                                           @Param("endDay") String endDay);

    /**
     *  固定时间范围下 指定条件下的数据质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectQualityCountSQL")
    List<Map<String, Object>> queryDetectQualityCount(@Param("enterprise") String enterprise,
                                                      @Param("hatchback") String hatchback,
                                                      @Param("vin") String vin,
                                                      @Param("startDay") String startDay,
                                                      @Param("endDay") String endDay);

    /**
     *  固定时间范围下 指定条件下的数据质量检测统计信息 按vin分组
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryVinDetectQualityCountSQL")
    List<Map<String, Object>> queryVinDetectQualityCount(@Param("enterprise") String enterprise,
                                                         @Param("hatchback") String hatchback,
                                                         @Param("vin") String vin,
                                                         @Param("startDay") String startDay,
                                                         @Param("endDay") String endDay);


    /**
     *  固定时间范围下 汇总数据质量检测统计信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectQualityAllCountSQL")
    Map<String, Object> queryDetectQualityAllCount(@Param("enterprise") String enterprise,
                                                         @Param("hatchback") String hatchback,
                                                         @Param("vin") String vin,
                                                         @Param("startDay") String startDay,
                                                         @Param("endDay") String endDay);

    /**
     * 按天聚合 指定条件下的数据逻辑质量检测信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectDataLogicQualityByDaySQL")
    List<Map<String, Object>> queryDetectDataLogicQualityByDay(@Param("enterprise") String enterprise,
                                                               @Param("hatchback") String hatchback,
                                                               @Param("vin") String vin,
                                                               @Param("startDay") String startDay,
                                                               @Param("endDay") String endDay);

    /**
     * 按天聚合 指定条件下的报文质量检测信息
     */
    @SelectProvider(type = QureySQLProvider.class, method = "queryDetectMessageQualityByDaySQL")
    List<Map<String, Object>> queryDetectMessageQualityByDay(@Param("enterprise") String enterprise,
                                                             @Param("hatchback") String hatchback,
                                                             @Param("vin") String vin,
                                                             @Param("startDay") String startDay,
                                                             @Param("endDay") String endDay);


    class QureySQLProvider {

        /**
         * 整车数据 质量检测统计信息查询生sql 生成
         */
        public String queryVehicleDataQualitySQL(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(vehicleStatusCode) vehicleStatusCode",
                            "sum(chargingStatusCode) chargingStatusCode",
                            "sum(runModeCode) runModeCode",
                            "sum(speed) speed",
                            "sum(accumulativeMile) accumulativeMile",
                            "sum(totalVoltage) totalVoltage",
                            "sum(totalElectricity) totalElectricity",
                            "sum(soc) soc",
                            "sum(dcStatusCode) dcStatusCode",
                            "sum(gearCode) gearCode",
                            "sum(insulationResistance) insulationResistance");
                    FROM("evs_err_total");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryVehicleDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                      @Param("hatchback") String hatchback,
                                                      @Param("vin") String vin,
                                                      @Param("startDay")String startDay,
                                                      @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(vehicleStatusCode) vehicleStatusCode",
                            "sum(chargingStatusCode) chargingStatusCode",
                            "sum(runModeCode) runModeCode",
                            "sum(speed) speed",
                            "sum(accumulativeMile) accumulativeMile",
                            "sum(totalVoltage) totalVoltage",
                            "sum(totalElectricity) totalElectricity",
                            "sum(soc) soc",
                            "sum(dcStatusCode) dcStatusCode",
                            "sum(gearCode) gearCode",
                            "sum(insulationResistance) insulationResistance");
                    FROM("evs_err_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return sql.toString();
        }


        /**
         * 驱动电机 质量检测统计信息查询生sql 生成
         */
        public String queryDriverMotorDataQualitySQL(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(driverMotorNumber) driverMotorNumber",
                            "sum(driverMotorSerial) driverMotorSerial",
                            "sum(driverMotorStateCode) driverMotorStateCode",
                            "sum(driverMotorControllerTemperature) driverMotorControllerTemperature",
                            "sum(driverMotorRPM) driverMotorRPM",
                            "sum(driverMotorTorque) driverMotorTorque",
                            "sum(driverMotorTemperature) driverMotorTemperature",
                            "sum(motorControllerInputVoltage) motorControllerInputVoltage",
                            "sum(motorControllerNegativeDCCurrent) motorControllerNegativeDCCurrent");
                    FROM("evs_err_total");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryDriverMotorDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                          @Param("hatchback") String hatchback,
                                                          @Param("vin") String vin,
                                                          @Param("startDay")String startDay,
                                                          @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(driverMotorNumber) driverMotorNumber",
                            "sum(driverMotorSerial) driverMotorSerial",
                            "sum(driverMotorStateCode) driverMotorStateCode",
                            "sum(driverMotorControllerTemperature) driverMotorControllerTemperature",
                            "sum(driverMotorRPM) driverMotorRPM",
                            "sum(driverMotorTorque) driverMotorTorque",
                            "sum(driverMotorTemperature) driverMotorTemperature",
                            "sum(motorControllerInputVoltage) motorControllerInputVoltage",
                            "sum(motorControllerNegativeDCCurrent) motorControllerNegativeDCCurrent");
                    FROM("evs_err_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);
            return sql.toString();
        }

        /**
         * 燃料电池 质量检测统计信息查询生sql 生成
         */
        public String queryFuelCellDataQualitySQL(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(fuelCellVoltage) fuelCellVoltage",
                            "sum(fuelCellCurrent) fuelCellCurrent",
                            "sum(rateOfFuelConsumption) rateOfFuelConsumption",
                            "sum(fuelCellProbeNumber) fuelCellProbeNumber",
                            "sum(probeTemperatures) probeTemperatures",
                            "sum(maxTemperatureInHydrogenSystem) maxTemperatureInHydrogenSystem",
                            "sum(maxTemperatureProbeSerial) maxTemperatureProbeSerial",
                            "sum(maxHydrogenConcentration) maxHydrogenConcentration",
                            "sum(maxHydrogenConcentrationProbeSerial) maxHydrogenConcentrationProbeSerial",
                            "sum(maxPressureHydrogen) maxPressureHydrogen",
                            "sum(maxPressureHydrogenProbeSerial) maxPressureHydrogenProbeSerial",
                            "sum(highPressDCStateCode) highPressDCStateCode");
                    FROM("evs_err_total");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryFuelCellDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                       @Param("hatchback") String hatchback,
                                                       @Param("vin") String vin,
                                                       @Param("startDay")String startDay,
                                                       @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(fuelCellVoltage) fuelCellVoltage",
                            "sum(fuelCellCurrent) fuelCellCurrent",
                            "sum(rateOfFuelConsumption) rateOfFuelConsumption",
                            "sum(fuelCellProbeNumber) fuelCellProbeNumber",
                            "sum(probeTemperatures) probeTemperatures",
                            "sum(maxTemperatureInHydrogenSystem) maxTemperatureInHydrogenSystem",
                            "sum(maxTemperatureProbeSerial) maxTemperatureProbeSerial",
                            "sum(maxHydrogenConcentration) maxHydrogenConcentration",
                            "sum(maxHydrogenConcentrationProbeSerial) maxHydrogenConcentrationProbeSerial",
                            "sum(maxPressureHydrogen) maxPressureHydrogen",
                            "sum(maxPressureHydrogenProbeSerial) maxPressureHydrogenProbeSerial",
                            "sum(highPressDCStateCode) highPressDCStateCode");
                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }

        /**
         * 发动机 质量检测统计信息查询生sql 生成
         */
        public String queryEngineDataQualitySQL(@Param("enterprise") String enterprise,
                                                @Param("hatchback") String hatchback,
                                                @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(engineStateCode) engineStateCode",
                            "sum(speedOfCrankshaft) speedOfCrankshaft",
                            "sum(specificFuelConsumption) specificFuelConsumption");
                    FROM("evs_err_total");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryEngineDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin,
                                                     @Param("startDay")String startDay,
                                                     @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(engineStateCode) engineStateCode",
                            "sum(speedOfCrankshaft) speedOfCrankshaft",
                            "sum(specificFuelConsumption) specificFuelConsumption");
                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }

        /**
         * 位置数据 质量检测统计信息查询生sql 生成
         */
        public String queryGpsDataQualitySQL(@Param("enterprise") String enterprise,
                                             @Param("hatchback") String hatchback,
                                             @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(locationCode) locationCode",
                            "sum(longitude) longitude",
                            "sum(latitude) latitude");
                    FROM("evs_err_total");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryGpsDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin,
                                                  @Param("startDay")String startDay,
                                                  @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(locationCode) locationCode",
                            "sum(longitude) longitude",
                            "sum(latitude) latitude");
                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }


        /**
         * 极值数据 质量检测统计信息查询生sql 生成
         */
        public String queryExtremumDataQualitySQL(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(highVBatterySubNum) highVBatterySubNum",
                            "sum(highVBatteryCellCode) highVBatteryCellCode",
                            "sum(maximumBatteryVoltage) maximumBatteryVoltage",
                            "sum(lowVBatterySubNum) lowVBatterySubNum",
                            "sum(lowVBatteryCellCode) lowVBatteryCellCode",
                            "sum(minimumBatteryVoltage) minimumBatteryVoltage",
                            "sum(highTemperatureSubNum) highTemperatureSubNum",
                            "sum(highTemperatureProbeSerial) highTemperatureProbeSerial",
                            "sum(maxTemperatureValue) maxTemperatureValue",
                            "sum(lowTemperatureSubNum) lowTemperatureSubNum",
                            "sum(lowTemperatureProbeSerial) lowTemperatureProbeSerial",
                            "sum(minTemperatureValue) minTemperatureValue");
                    FROM("evs_err_total");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryExtremumDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                       @Param("hatchback") String hatchback,
                                                       @Param("vin") String vin,
                                                       @Param("startDay")String startDay,
                                                       @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(highVBatterySubNum) highVBatterySubNum",
                            "sum(highVBatteryCellCode) highVBatteryCellCode",
                            "sum(maximumBatteryVoltage) maximumBatteryVoltage",
                            "sum(lowVBatterySubNum) lowVBatterySubNum",
                            "sum(lowVBatteryCellCode) lowVBatteryCellCode",
                            "sum(minimumBatteryVoltage) minimumBatteryVoltage",
                            "sum(highTemperatureSubNum) highTemperatureSubNum",
                            "sum(highTemperatureProbeSerial) highTemperatureProbeSerial",
                            "sum(maxTemperatureValue) maxTemperatureValue",
                            "sum(lowTemperatureSubNum) lowTemperatureSubNum",
                            "sum(lowTemperatureProbeSerial) lowTemperatureProbeSerial",
                            "sum(minTemperatureValue) minTemperatureValue");
                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }

        /**
         * 告警数据 质量检测统计信息查询生sql 生成
         */
        public String queryAlarmDataQualitySQL(@Param("enterprise") String enterprise,
                                               @Param("hatchback") String hatchback,
                                               @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(maxAlarmRating) maxAlarmRating",
                            "sum(rechargeableStorageDeviceN1) rechargeableStorageDeviceN1",
                            "sum(driverMotorFailureN2) driverMotorFailureN2",
                            "sum(engineFailureN3) engineFailureN3",
                            "sum(otherFailureN4) otherFailureN4");
                    FROM("evs_err_total");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, null, null);

            return sql.toString();

        }

        public String queryAlarmDataQualityInDaySQL(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("startDay")String startDay,
                                                    @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(maxAlarmRating) maxAlarmRating",
                            "sum(rechargeableStorageDeviceN1) rechargeableStorageDeviceN1",
                            "sum(driverMotorFailureN2) driverMotorFailureN2",
                            "sum(engineFailureN3) engineFailureN3",
                            "sum(otherFailureN4) otherFailureN4");
                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }

        public String queryDataLogicCheckSQL(@Param("enterprise") String enterprise,
                                             @Param("hatchback") String hatchback,
                                             @Param("vin") String vin){
            SQL sql = new SQL() {
                {
                    SELECT("sum(logic_vehiclestatus) logic_vehiclestatus",
                            "sum(logic_runmode) logic_runmode",
                            "sum(logic_chargingStatus) logic_chargingStatus",
                            "sum(logic_drivermotornumber) logic_drivermotornumber",
                            "sum(logic_fuelbattery) logic_fuelbattery",
                            "sum(logic_alarmidentification) logic_alarmidentification",
                            "sum(logic_soc) logic_soc");
                    FROM("evs_err_total");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, null, null);

            return  sql.toString();
        }

        public String queryDataLogicCheckInDaySQL(@Param("enterprise") String enterprise,
                                                  @Param("hatchback") String hatchback,
                                                  @Param("vin") String vin,
                                                  @Param("startDay")String startDay,
                                                  @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("sum(logic_vehiclestatus) logic_vehiclestatus",
                            "sum(logic_runmode) logic_runmode",
                            "sum(logic_chargingStatus) logic_chargingStatus",
                            "sum(logic_drivermotornumber) logic_drivermotornumber",
                            "sum(logic_fuelbattery) logic_fuelbattery",
                            "sum(logic_alarmidentification) logic_alarmidentification",
                            "sum(logic_soc) logic_soc");
                    FROM("evs_err_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);

            return  sql.toString();
        }


        public String queryDetectDataQualityByDaySQL(@Param("enterprise") String enterprise,
                                                     @Param("hatchback") String hatchback,
                                                     @Param("vin") String vin,
                                                     @Param("startDay")String startDay,
                                                     @Param("endDay")String endDay){

            SQL sql = new SQL() {
                {
                    SELECT("day, sum(vehicleStatusCode) vehicleStatusCode",
                            "sum(chargingStatusCode) chargingStatusCode",
                            "sum(runModeCode) runModeCode",
                            "sum(speed) speed",
                            "sum(accumulativeMile) accumulativeMile",
                            "sum(totalVoltage) totalVoltage",
                            "sum(totalElectricity) totalElectricity",
                            "sum(soc) soc",
                            "sum(dcStatusCode) dcStatusCode",
                            "sum(gearCode) gearCode",
                            "sum(insulationResistance) insulationResistance");

                    SELECT("sum(driverMotorNumber) driverMotorNumber",
                            "sum(driverMotorSerial) driverMotorSerial",
                            "sum(driverMotorStateCode) driverMotorStateCode",
                            "sum(driverMotorControllerTemperature) driverMotorControllerTemperature",
                            "sum(driverMotorRPM) driverMotorRPM",
                            "sum(driverMotorTorque) driverMotorTorque",
                            "sum(driverMotorTemperature) driverMotorTemperature",
                            "sum(motorControllerInputVoltage) motorControllerInputVoltage",
                            "sum(motorControllerNegativeDCCurrent) motorControllerNegativeDCCurrent");

                    SELECT("sum(fuelCellVoltage) fuelCellVoltage",
                            "sum(fuelCellCurrent) fuelCellCurrent",
                            "sum(rateOfFuelConsumption) rateOfFuelConsumption",
                            "sum(fuelCellProbeNumber) fuelCellProbeNumber",
                            "sum(probeTemperatures) probeTemperatures",
                            "sum(maxTemperatureInHydrogenSystem) maxTemperatureInHydrogenSystem",
                            "sum(maxTemperatureProbeSerial) maxTemperatureProbeSerial",
                            "sum(maxHydrogenConcentration) maxHydrogenConcentration",
                            "sum(maxHydrogenConcentrationProbeSerial) maxHydrogenConcentrationProbeSerial",
                            "sum(maxPressureHydrogen) maxPressureHydrogen",
                            "sum(maxPressureHydrogenProbeSerial) maxPressureHydrogenProbeSerial",
                            "sum(highPressDCStateCode) highPressDCStateCode");

                    SELECT("sum(engineStateCode) engineStateCode",
                            "sum(speedOfCrankshaft) speedOfCrankshaft",
                            "sum(specificFuelConsumption) specificFuelConsumption");

                    SELECT("sum(locationCode) locationCode",
                            "sum(longitude) longitude",
                            "sum(latitude) latitude");

                    SELECT("sum(highVBatterySubNum) highVBatterySubNum",
                            "sum(highVBatteryCellCode) highVBatteryCellCode",
                            "sum(maximumBatteryVoltage) maximumBatteryVoltage",
                            "sum(lowVBatterySubNum) lowVBatterySubNum",
                            "sum(lowVBatteryCellCode) lowVBatteryCellCode",
                            "sum(minimumBatteryVoltage) minimumBatteryVoltage",
                            "sum(highTemperatureSubNum) highTemperatureSubNum",
                            "sum(highTemperatureProbeSerial) highTemperatureProbeSerial",
                            "sum(maxTemperatureValue) maxTemperatureValue",
                            "sum(lowTemperatureSubNum) lowTemperatureSubNum",
                            "sum(lowTemperatureProbeSerial) lowTemperatureProbeSerial",
                            "sum(minTemperatureValue) minTemperatureValue");

                    SELECT("sum(maxAlarmRating) maxAlarmRating",
                            "sum(rechargeableStorageDeviceN1) rechargeableStorageDeviceN1",
                            "sum(driverMotorFailureN2) driverMotorFailureN2",
                            "sum(engineFailureN3) engineFailureN3",
                            "sum(otherFailureN4) otherFailureN4");


                    FROM("evs_err_day");
                }
            };

            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);
            sql.GROUP_BY("day").ORDER_BY("day");

            return sql.toString();

        }

        /**
         * 按天聚合 指定条件下的数据质量检测统计信息
         */
        public String queryDetectQualityCountByDaySQL(@Param("enterprise") String enterprise,
                                                      @Param("hatchback") String hatchback,
                                                      @Param("vin") String vin,
                                                      @Param("startDay")String startDay,
                                                      @Param("endDay")String endDay){
            SQL error = new SQL() {
                {
                    SELECT("enterprise, hatchback, day, sum(detect_count) detect, sum(logic_count) logic, sum(intact) intact");
                    FROM("evs_err_day");
                }
            };
            addCondition(error, enterprise, hatchback, vin, startDay, endDay);
            error.GROUP_BY("enterprise, hatchback, day");

            SQL total = new SQL() {
                {
                    SELECT("enterprise, hatchback, day, sum(count) total");
                    FROM("evs_driving_day");
                }
            };
            addCondition(total, enterprise, hatchback, vin, startDay, endDay);
            total.GROUP_BY("enterprise, hatchback, day");

            SQL sql = new SQL() {
                {
                    SELECT("t1.enterprise, t1.hatchback, t1.day, t1.detect, t1.logic, t1.intact, t2.total");
                    FROM(String.format("((%s) t1 left join (%s) t2 on (t1.day = t2.day and t1.enterprise = t2.enterprise and t1.hatchback = t2.hatchback))",
                            error.toString(), total.toString()));
                    ORDER_BY("day, total");
                }
            };

            return  sql.toString();
        }

        /**
         * 固定时间范围下 指定条件下的数据质量检测统计信息
         */
        public String queryDetectQualityCountSQL(@Param("enterprise") String enterprise,
                                                 @Param("hatchback") String hatchback,
                                                 @Param("vin") String vin,
                                                 @Param("startDay")String startDay,
                                                 @Param("endDay")String endDay){
            SQL error = new SQL() {
                {
                    SELECT("enterprise, hatchback, sum(detect_count) detect, sum(logic_count) logic, sum(intact) intact");
                    FROM("evs_err_day");
                }
            };
            addCondition(error, enterprise, hatchback, vin, startDay, endDay);
            error.GROUP_BY("enterprise, hatchback");

            SQL total = new SQL() {
                {
                    SELECT("enterprise, hatchback, sum(count) total");
                    FROM("evs_driving_day");
                }
            };
            addCondition(total, enterprise, hatchback, vin, startDay, endDay);
            total.GROUP_BY("enterprise, hatchback");

            SQL sql = new SQL() {
                {
                    SELECT("t1.enterprise, t1.hatchback, t1.detect, t1.logic, t1.intact, t2.total");
                    FROM(String.format("((%s) t1 left join (%s) t2 on (t1.enterprise = t2.enterprise and t1.hatchback = t2.hatchback))",
                            error.toString(), total.toString()));
                    ORDER_BY("total");
                }
            };

            return  sql.toString();
        }

        /**
         * 固定时间范围下 指定条件下的数据质量检测统计信息 按车辆vin分组
         */
        public String queryVinDetectQualityCountSQL(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("startDay")String startDay,
                                                    @Param("endDay")String endDay){
            SQL error = new SQL() {
                {
                    SELECT("min(enterprise) enterprise, max(hatchback) hatchback, vin, sum(detect_count) detect, sum(logic_count) logic, sum(intact) intact");
                    FROM("evs_err_day");
                }
            };
            addCondition(error, enterprise, hatchback, vin, startDay, endDay);
            error.GROUP_BY("vin");

            SQL total = new SQL() {
                {
                    SELECT("vin, sum(count) total");
                    FROM("evs_driving_day");
                }
            };
            addCondition(total, enterprise, hatchback, vin, startDay, endDay);
            total.GROUP_BY("vin");

            SQL sql = new SQL() {
                {
                    SELECT("t1.enterprise, t1.hatchback, t1.vin, t1.detect, t1.logic, t1.intact, t2.total");
                    FROM(String.format("((%s) t1 left join (%s) t2 on (t1.vin = t2.vin))",
                            error.toString(), total.toString()));
                    ORDER_BY("total");
                }
            };

            return  sql.toString();
        }

        /**
         * 固定时间范围下 汇总数据质量检测统计信息
         */
        public String queryDetectQualityAllCountSQL(@Param("enterprise") String enterprise,
                                                    @Param("hatchback") String hatchback,
                                                    @Param("vin") String vin,
                                                    @Param("startDay")String startDay,
                                                    @Param("endDay")String endDay){
            SQL error = new SQL() {
                {
                    SELECT("sum(detect_count) detect, sum(logic_count) logic, sum(intact) intact, 1 id");
                    FROM("evs_err_day");
                }
            };
            addCondition(error, enterprise, hatchback, vin, startDay, endDay);

            SQL total = new SQL() {
                {
                    SELECT("sum(count) total, 1 id");
                    FROM("evs_driving_day");
                }
            };
            addCondition(total, enterprise, hatchback, vin, startDay, endDay);

            SQL sql = new SQL() {
                {
                    SELECT("t1.detect, t1.logic, t1.intact, t2.total");
                    FROM(String.format("((%s) t1 left join (%s) t2 on (t1.id = t2.id))",
                            error.toString(), total.toString()));
                }
            };

            return  sql.toString();
        }

        /**
         * 报文检测统计  完整
         */
        public String queryDetectMessageQualityByDaySQL(@Param("enterprise") String enterprise,
                                                        @Param("hatchback") String hatchback,
                                                        @Param("vin") String vin,
                                                        @Param("startDay")String startDay,
                                                        @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("day, sum(intact) intact");
                    FROM("evs_err_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);
            sql.GROUP_BY("day").ORDER_BY("day");

            return  sql.toString();
        }

        public String queryDetectDataLogicQualityByDaySQL(@Param("enterprise") String enterprise,
                                                          @Param("hatchback") String hatchback,
                                                          @Param("vin") String vin,
                                                          @Param("startDay")String startDay,
                                                          @Param("endDay")String endDay){
            SQL sql = new SQL() {
                {
                    SELECT("day, sum(logic_vehiclestatus) logic_vehiclestatus",
                            "sum(logic_runmode) logic_runmode",
                            "sum(logic_chargingStatus) logic_chargingStatus",
                            "sum(logic_drivermotornumber) logic_drivermotornumber",
                            "sum(logic_fuelbattery) logic_fuelbattery",
                            "sum(logic_alarmidentification) logic_alarmidentification",
                            "sum(logic_soc) logic_soc");
                    FROM("evs_err_day");
                }
            };
            addCondition(sql, enterprise, hatchback, vin, startDay, endDay);
            sql.GROUP_BY("day").ORDER_BY("day");

            return  sql.toString();
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
