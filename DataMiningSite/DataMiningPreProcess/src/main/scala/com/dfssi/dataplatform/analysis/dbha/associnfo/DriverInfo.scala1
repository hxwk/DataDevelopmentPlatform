package com.dfssi.dataplatform.analysis.dbha.associnfo

import java.util.Calendar

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object DriverInfo {

  val terminal0702Database = "prod_analysis"
  val terminal0702TableName = "terminal_0702"

  val Field_Name_VehicleId = "vid"
  val Field_Name_Idcard = "idcard"
  val Field_Name_CYZGZH = "practitioner_idcard"
  val Field_Name_Driver = "name"
  val Field_Name_CardInsertTime = "work_date"
  val Field_Name_PartYearMonth = "part_yearmonth"

  val selectedFields = s"$Field_Name_VehicleId,$Field_Name_CYZGZH,$Field_Name_Driver,$Field_Name_CardInsertTime"

  case class DriverRange(idcard: String, driverName: String, startTime: Long, endTime: Long)

  lazy val calendar: Calendar = Calendar.getInstance()


  // SSI_DRIVER.CSYE_CAR_NB = terminal_0702.idcard = practitioner_idcard
  def collectVidDriverInfo(startTime: Long, endTime: Long, hiveContext: HiveContext): collection.Map[String, List[DriverRange]] = {

    calendar.setTimeInMillis(startTime)
    val startYearMonth = calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH)

    calendar.setTimeInMillis(endTime)
    val endYearMonth = calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH)

    val sql = s"SELECT $selectedFields FROM $terminal0702Database.$terminal0702TableName " +
      s"WHERE $Field_Name_CYZGZH IS NOT NULL AND $Field_Name_CYZGZH <> '' " +
    s" AND $Field_Name_Driver IS NOT NULL AND $Field_Name_Driver <> '' " +
    s" AND $Field_Name_CardInsertTime >= $startTime AND $Field_Name_CardInsertTime <= $endTime " +
    s" AND $Field_Name_PartYearMonth >= $startYearMonth AND $Field_Name_PartYearMonth <= $endYearMonth"

    val df = hiveContext.sql(sql)//.sort(Field_Name_CardInsertTime)

    val vid2DriverRangesMap = df.rdd.groupBy(_.getAs[String](Field_Name_VehicleId))
        .mapValues{ iter =>
          val ranges = ArrayBuffer[DriverRange]()

          var startIdx = -1
          val cardRecords = iter.map{ row =>
            (row.getAs[String](Field_Name_CYZGZH), row.getAs[String](Field_Name_Driver), row.getAs[Long](Field_Name_CardInsertTime))
          }.toList.sortBy(_._3)

          if(cardRecords.lengthCompare(1) == 0)
            ranges.append(DriverRange(cardRecords.head._1, cardRecords.last._2, cardRecords.last._3, cardRecords.last._3))
          else {
            cardRecords.indices.foreach{ idx =>
              if (-1 == startIdx) {
                startIdx = idx
              }
              else if(cardRecords(startIdx)._1 != cardRecords(idx)._1) {
                ranges.append(DriverRange(cardRecords(startIdx)._1, cardRecords(startIdx)._2, cardRecords(startIdx)._3, cardRecords(idx)._3))
                startIdx = idx
              }
            }
            if(startIdx < cardRecords.length)
              ranges.append(DriverRange(cardRecords(startIdx)._1, cardRecords(startIdx)._2, startTime, cardRecords.last._3))
          }

          ranges.toList
    }.collectAsMap()

    vid2DriverRangesMap
  }


  // Test
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("test driver info"))
    sc.setLogLevel("ERROR")
    val hiveContext = new HiveContext(sc)

    println(collectVidDriverInfo(1420390693000L, 1820390693000L, hiveContext))


  }

}
