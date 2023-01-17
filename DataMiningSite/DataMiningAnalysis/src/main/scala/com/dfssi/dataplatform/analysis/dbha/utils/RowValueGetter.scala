package com.dfssi.dataplatform.analysis.dbha.utils

import org.apache.spark.sql.Row

import scala.collection.mutable

trait RowValueGetter {

}

trait _0200RowValueGetter extends RowValueGetter {

  import com.dfssi.dataplatform.analysis.dbha.config.Terminal0200FieldConstants._

  def getVDRSpeed(row: Row): Long = row.getAs[Long](FIELD_VDR_SPEED)

  def getGPSSpeed(row: Row): Long = row.getAs[Long](FIELD_GPS_SPEED)

  def getGPSTime(row: Row): Long = row.getAs[Long](FIELD_GPS_TIME)

  def getDir(row: Row): Long = row.getAs[Long](FIELD_DIR)

  def getMile(row: Row): Long = row.getAs[Long](FIELD_MILE)

  def getSignalStates(row: Row): mutable.WrappedArray[String] = row.getAs[mutable.WrappedArray[String]](FIELD_VEHICLE_SIGNAL)
}


trait _0705RowValueGetter extends RowValueGetter{

  import com.dfssi.dataplatform.analysis.dbha.config.Terminal0705FieldConstants._

  def getCaptureTime(row: Row): Long = row.getAs[Long](FIELD_RECEIVE_TIME)

  def getSignalName(row: Row): String = row.getAs[String](FIELD_NAME_SIGNAL)

  def getSignalValue(row: Row): Double = row.getAs[Double](FIELD_VALUE_SIGNAL)

}