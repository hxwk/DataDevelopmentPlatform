package com.dfssi.dataplatform.analysis.fuel.stats

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/14 11:18 
  */
case class VehicleWorkConditionRecord(val vid: String,
                                      val totaltime: Long,
                                      val totalmile: Double,
                                      val totalBrakeMile: Double,
                                      val totalfuel: Double,
                                      val totalIdlefuel: Double,
                                      val speedpair: String,
                                      val rpmpair: String,
                                      val accpair: String,
                                      val gearpair: String,
                                      val gearSpeedpair: String,
                                      val day: Long) extends Serializable


