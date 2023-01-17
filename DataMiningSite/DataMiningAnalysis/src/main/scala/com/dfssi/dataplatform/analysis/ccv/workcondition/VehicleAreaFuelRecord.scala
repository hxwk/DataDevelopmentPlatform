package com.dfssi.dataplatform.analysis.ccv.workcondition


/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/14 11:18
  */
case class VehicleAreaFuelRecord(var totaltime: Long,
                                 var totalmile: Double,
                                 var totalfuel: Double,
                                 val province: String,
                                 val city: String,
                                 val district: String,
                                 val day: Long) extends Serializable
