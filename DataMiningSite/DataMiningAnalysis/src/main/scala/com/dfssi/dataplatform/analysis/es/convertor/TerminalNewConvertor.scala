package com.dfssi.dataplatform.analysis.es.convertor

import java.util

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.dfssi.dataplatform.analysis.es.{ConvertorUtils, EsRecord}
import com.dfssi.dataplatform.analysis.util.MathUtil
import com.dfssi.resources.ConfigUtils

/**
  * 新能源汽车数据实时入ES
  * @author pengwk
  * @version 2018-4-12 13:34:46
  */
class TerminalNewConvertor extends TerminalDataConvertorTrait{

  //数据预处理
  override def preConvert(record: util.Map[String, Object],
                          idField: String,
                          identificationFiled: String): util.Map[String, Object] = {
    if (record.get("collectTime") == null) {
      extractCollecTime(record, "neAlarmBean")
      extractCollecTime(record, "neDriverMotor")
      extractCollecTime(record, "neEngineBean")
      extractCollecTime(record, "neExtremumBean")
      extractCollecTime(record, "neFuelCellBean")
      extractCollecTime(record, "neGpsBean")
      extractCollecTime(record, "neVehicleBean")
    }

    record
  }

  //数据格式转换
  override def convert(record: util.Map[String, Object],
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] = {
    try{
      val vin = ConfigUtils.getAsString(record, "vin")
      require(vin != null, "vin = null")

      reviseVehicle(record)
      reviseDriverMotor(record)
      reviseFuelCell(record)
      reviseEngine(record)
      reviseLocation(record)
      reviseExtremum(record)
      reviseAlarm(record)

      reviseStorageData(record)
      reviseStorageTemp(record)

      Array(EsRecord(vin, time, record))
    } catch {
      case e: Exception => {
        logError(s"解析新能源汽车数据 ${record} 失败。", e)
        Array.empty[EsRecord]
      }
    }
  }

  //修正整车数据
  private def reviseVehicle(record: util.Map[String, Object]): Unit = {
    var neVehicleBean = record.remove("neVehicleBean")
    if(neVehicleBean != null && neVehicleBean.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neVehicleBean.asInstanceOf[util.Map[String, Object]])

      //修正车速
      var speed = ConfigUtils.getAsDouble(record, "speed")
      if (speed != null) speed = ConvertorUtils.KeepNDecimal(speed * 0.1, 1)
      record.put("speed", speed)

      //修正累计里程
      var accumulativeMile = ConfigUtils.getAsDouble(record, "accumulativeMile")
      if (accumulativeMile != null) accumulativeMile = ConvertorUtils.KeepNDecimal(accumulativeMile * 0.1, 1)
      record.put("accumulativeMile", accumulativeMile)

      //修正总电压
      var totalVoltage = ConfigUtils.getAsDouble(record, "totalVoltage")
      if (totalVoltage != null) totalVoltage = ConvertorUtils.KeepNDecimal(totalVoltage * 0.1, 1)
      record.put("totalVoltage", totalVoltage)

      //修正总电流
      var totalElectricity = ConfigUtils.getAsDouble(record, "totalElectricity")
      if (totalElectricity != null) totalElectricity = ConvertorUtils.KeepNDecimal(totalElectricity * 0.1 - 1000, 1)
      record.put("totalElectricity", totalElectricity)
    }
  }

  //修正驱动电机数据
  private def reviseDriverMotor(record: util.Map[String, Object]): Unit = {
    var neDriverMotor = record.remove("neDriverMotor")
    if(neDriverMotor != null && neDriverMotor.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neDriverMotor.asInstanceOf[util.Map[String, Object]])

      //取出JSONArray中的JSONObject
      val nEDriverMotorBeansObj = record.remove("nEDriverMotorBeans")
      if(nEDriverMotorBeansObj != null) {
        val nEDriverMotorBeansArr = nEDriverMotorBeansObj.asInstanceOf[JSONArray]
        var nEDriverMotorBeans = new JSONArray()
        if (!nEDriverMotorBeansArr.isEmpty) {
          for (i <- 0 to nEDriverMotorBeansArr.size() - 1) {
            var nEDriverMotorBean = nEDriverMotorBeansArr.getJSONObject(i).asInstanceOf[util.Map[String, Object]]

            //修正驱动电机控制器温度
            var driverMotorControllerTemperature = ConfigUtils.getAsDouble(nEDriverMotorBean, "driverMotorControllerTemperature")
            if (driverMotorControllerTemperature != null) driverMotorControllerTemperature = driverMotorControllerTemperature - 40
            nEDriverMotorBean.put("driverMotorControllerTemperature", driverMotorControllerTemperature)

            //修正驱动电机转速
            var driverMotorRPM = ConfigUtils.getAsDouble(nEDriverMotorBean, "driverMotorRPM")
            if (driverMotorRPM != null) driverMotorRPM = driverMotorRPM - 20000
            nEDriverMotorBean.put("driverMotorRPM", driverMotorRPM)

            //修正驱动电机转矩
            var driverMotorTorque = ConfigUtils.getAsDouble(nEDriverMotorBean, "driverMotorTorque")
            if (driverMotorTorque != null) driverMotorTorque = ConvertorUtils.KeepNDecimal((driverMotorTorque - 20000) * 0.1, 1)
            nEDriverMotorBean.put("driverMotorTorque", driverMotorTorque)

            //修正驱动电机温度
            var driverMotorTemperature = ConfigUtils.getAsDouble(nEDriverMotorBean, "driverMotorTemperature")
            if (driverMotorTemperature != null) driverMotorTemperature = driverMotorTemperature - 40
            nEDriverMotorBean.put("driverMotorTemperature", driverMotorTemperature)

            //修正电机控制器输入电压
            var motorControllerInputVoltage = ConfigUtils.getAsDouble(nEDriverMotorBean, "motorControllerInputVoltage")
            if (motorControllerInputVoltage != null) motorControllerInputVoltage = ConvertorUtils.KeepNDecimal(motorControllerInputVoltage * 0.1, 1)
            nEDriverMotorBean.put("motorControllerInputVoltage", motorControllerInputVoltage)

            //修正电机控制器直流母线电流
            var motorControllerNegativeDCCurrent = ConfigUtils.getAsDouble(nEDriverMotorBean, "motorControllerNegativeDCCurrent")
            if (motorControllerNegativeDCCurrent != null) motorControllerNegativeDCCurrent = ConvertorUtils.KeepNDecimal((motorControllerNegativeDCCurrent - 10000) * 0.1, 1)
            nEDriverMotorBean.put("motorControllerNegativeDCCurrent", motorControllerNegativeDCCurrent)

            nEDriverMotorBeans.add(i, nEDriverMotorBean.asInstanceOf[JSONObject])
          }
        }
        record.put("nEDriverMotorBeans", nEDriverMotorBeans)
      }
    }
  }

  //修正燃料电池数据
  private def reviseFuelCell(record: util.Map[String, Object]): Unit = {
    var neFuelCellBean = record.remove("neFuelCellBean")
    if (neFuelCellBean != null && neFuelCellBean.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neFuelCellBean.asInstanceOf[util.Map[String, Object]])

      //修正燃料电池电压
      var fuelCellVoltage = ConfigUtils.getAsDouble(record, "fuelCellVoltage")
      if (fuelCellVoltage != null) fuelCellVoltage = ConvertorUtils.KeepNDecimal(fuelCellVoltage * 0.1, 1)
      record.put("fuelCellVoltage", fuelCellVoltage)

      //修正燃料电池电流
      var fuelCellCurrent = ConfigUtils.getAsDouble(record, "fuelCellCurrent")
      if (fuelCellCurrent != null) fuelCellCurrent = ConvertorUtils.KeepNDecimal(fuelCellCurrent * 0.1, 1)
      record.put("fuelCellCurrent", fuelCellCurrent)

      //修正燃料消耗率
      var rateOfFuelConsumption = ConfigUtils.getAsDouble(record, "rateOfFuelConsumption")
      if (rateOfFuelConsumption != null) rateOfFuelConsumption = ConvertorUtils.KeepNDecimal(rateOfFuelConsumption * 0.01, 2)
      record.put("rateOfFuelConsumption", rateOfFuelConsumption)

      //修正探针温度值
      var probeTemperaturesObj = record.get("probeTemperatures")
      if(probeTemperaturesObj != null){
        if(probeTemperaturesObj.isInstanceOf[java.util.List[Object]]){
          val objects = probeTemperaturesObj.asInstanceOf[util.List[Object]]
          for(i <- 0 until objects.size()){
            val rv = (objects.remove(i).toString.toInt  - 40).asInstanceOf[Object]
            objects.add(i, rv)
          }
          record.put("probeTemperatures", objects)
        }else{
          record.put("probeTemperatures", (probeTemperaturesObj.toString.toInt - 40).asInstanceOf[Object])
        }
      }

      //修正氢系统中最高温度
      var maxTemperatureInHydrogenSystem = ConfigUtils.getAsDouble(record, "maxTemperatureInHydrogenSystem")
      if (maxTemperatureInHydrogenSystem != null) maxTemperatureInHydrogenSystem = maxTemperatureInHydrogenSystem - 40
      record.put("maxTemperatureInHydrogenSystem", maxTemperatureInHydrogenSystem)

      //修正氢气最高压力
      var maxPressureHydrogen = ConfigUtils.getAsDouble(record, "maxPressureHydrogen")
      if (maxPressureHydrogen != null) maxPressureHydrogen = ConvertorUtils.KeepNDecimal(maxPressureHydrogen * 0.1, 1)
      record.put("maxPressureHydrogen", maxPressureHydrogen)
    }
  }

  //修正发动机数据
  private def reviseEngine(record: util.Map[String, Object]): Unit = {
    var neEngineBean = record.remove("neEngineBean")
    if(neEngineBean != null && neEngineBean.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neEngineBean.asInstanceOf[util.Map[String, Object]])

      //修正燃料消耗率
      var specificFuelConsumption = ConfigUtils.getAsDouble(record, "specificFuelConsumption")
      if (specificFuelConsumption != null) specificFuelConsumption = ConvertorUtils.KeepNDecimal(specificFuelConsumption * 0.01, 2)
      record.put("specificFuelConsumption", specificFuelConsumption)
    }
  }

  //修正位置信息
  private def reviseLocation(record: util.Map[String, Object]): Unit = {
    var neGpsBean = record.remove("neGpsBean")
    if(neGpsBean != null && neGpsBean.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neGpsBean.asInstanceOf[util.Map[String, Object]])

      //修正经度
      var longitude = ConfigUtils.getAsDouble(record, "longitude")
      if(longitude != null) longitude = longitude/Math.pow(10, 6)
      record.put("longitude", longitude)

      //修正纬度
      var latitude = ConfigUtils.getAsDouble(record, "latitude")
      if(latitude != null) latitude = latitude/Math.pow(10, 6)
      record.put("latitude", latitude)

      if(longitude >= -180 && longitude <= 180 && latitude >= -90 && latitude <= 90)
        record.put("location", Array(longitude, latitude))
    }
  }

  //修正极值数据
  private def reviseExtremum(record: util.Map[String, Object]): Unit = {
    var neExtremumBean = record.remove("neExtremumBean")
    if(neExtremumBean != null && neExtremumBean.isInstanceOf[util.Map[String, Object]]) {
      record.putAll(neExtremumBean.asInstanceOf[util.Map[String, Object]])

      //修正电池单体电压最高值
      var maximumBatteryVoltage = ConfigUtils.getAsDouble(record, "maximumBatteryVoltage")
      if (maximumBatteryVoltage != null) maximumBatteryVoltage = ConvertorUtils.KeepNDecimal(maximumBatteryVoltage * 0.001, 3)
      record.put("maximumBatteryVoltage", maximumBatteryVoltage)

      //修正电池单体电压最低值
      var minimumBatteryVoltage = ConfigUtils.getAsDouble(record, "minimumBatteryVoltage")
      if (minimumBatteryVoltage != null) minimumBatteryVoltage = ConvertorUtils.KeepNDecimal(minimumBatteryVoltage * 0.001, 3)
      record.put("minimumBatteryVoltage", minimumBatteryVoltage)

      //修正最高温度值
      var maxTemperatureValue = ConfigUtils.getAsDouble(record, "maxTemperatureValue")
      if (maxTemperatureValue != null) maxTemperatureValue = maxTemperatureValue - 40
      record.put("maxTemperatureValue", maxTemperatureValue)

      //修正最低温度值
      var minTemperatureValue = ConfigUtils.getAsDouble(record, "minTemperatureValue")
      if (maxTemperatureValue != null) maxTemperatureValue = minTemperatureValue - 40
      record.put("minTemperatureValue", minTemperatureValue)
    }
  }

  //修正报警数据
  private def reviseAlarm(record: util.Map[String, Object]): Unit = {
    var neAlarmBean = record.remove("neAlarmBean")
    if(neAlarmBean != null && neAlarmBean.isInstanceOf[util.Map[String, Object]])
      record.putAll(neAlarmBean.asInstanceOf[util.Map[String, Object]])
  }

  //修正可充电储能装置电压数据
  private def reviseStorageData(record: util.Map[String, Object]): Unit = {

    val neChargeVoltage = record.remove("neChargeVoltage")
    if(neChargeVoltage != null && neChargeVoltage.isInstanceOf[util.Map[String, Object]]) {
      val map = neChargeVoltage.asInstanceOf[util.Map[String, Object]]
      val neChargeVoltageBeanList = map.get("neChargeVoltageBeanList")
      if (neChargeVoltageBeanList != null && neChargeVoltageBeanList.isInstanceOf[util.List[util.Map[String, Object]]]) {
        val list = neChargeVoltageBeanList.asInstanceOf[util.List[util.Map[String, Object]]]
        for(i <- 0 until list.size()) {
          val tmap = list.get(i)

          //修正可充电储能装置电压
          var storageVoltage = ConfigUtils.getAsDouble(tmap, "storageVoltage")
          if(storageVoltage != null) storageVoltage = MathUtil.rounding(storageVoltage * 0.1, 1)
          tmap.put("storageVoltage", storageVoltage)

          //修正可充电储能装置电流
          var storageCurrent = ConfigUtils.getAsDouble(tmap, "storageCurrent")
          if(storageCurrent != null) storageCurrent = MathUtil.rounding(storageCurrent * 0.1 - 1000, 1)
          tmap.put("storageCurrent", storageCurrent)

          //修正单体电池电压 数组格式
          val cellVoltageObj = tmap.remove("cellVoltage")
          if(cellVoltageObj != null && cellVoltageObj.isInstanceOf[java.util.List[Object]]){
            val objects = cellVoltageObj.asInstanceOf[util.List[Object]]
            for(i <- 0 until objects.size()){
              val rv = (MathUtil.rounding(objects.remove(i).toString.toInt * 0.001, 3)).asInstanceOf[Object]
              objects.add(i, rv)
            }
            tmap.put("cellVoltage", objects)
          }
        }
      }
      record.putAll(map)
    }

  }

  //修正可充电储能装置温度数据
  private def reviseStorageTemp(record: util.Map[String, Object]): Unit = {

    val neChargeTemp = record.remove("neChargeTemp")
    if(neChargeTemp != null && neChargeTemp.isInstanceOf[util.Map[String, Object]]){
      val map = neChargeTemp.asInstanceOf[util.Map[String, Object]]
      val neChargeTempBeanList = map.get("neChargeTempBeanList")
      if(neChargeTempBeanList != null && neChargeTempBeanList.isInstanceOf[util.List[util.Map[String, Object]]]){
        val list = neChargeTempBeanList.asInstanceOf[util.List[util.Map[String, Object]]]
        for(i <- 0 until list.size()){
          val tmap = list.get(i)
          //修正可充电储能子系统各温度探针检测到的温度值
          val storageTempAllProbeNums = tmap.remove("storageTempAllProbeNums")
          if(storageTempAllProbeNums != null && storageTempAllProbeNums.isInstanceOf[java.util.List[Object]]){
            val objects = storageTempAllProbeNums.asInstanceOf[util.List[Object]]
            for(i <- 0 until objects.size()){
              val rv = (objects.remove(i).toString.toInt  - 40).asInstanceOf[Object]
              objects.add(i, rv)
            }
            tmap.put("storageTempAllProbeNums", objects)
          }
        }
      }
      record.putAll(map)
    }
  }

  //提取collectTime
  private def extractCollecTime(record: util.Map[String, Object], string: String): Unit = {
    var obj = record.get(string)
    if (obj != null) {
      val collectTime = obj.asInstanceOf[util.Map[String, Object]].get("collectTime")
      if (collectTime != null) {
        record.put("collectTime", collectTime)
      }
    }
  }
}

object TerminalNewConvertor{
  val ID = "new_vehicle"
  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new TerminalNewConvertor
      }
    }
    convertor
  }
}

