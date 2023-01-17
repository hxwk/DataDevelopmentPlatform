package com.dfssi.dataplatform.analysis.dbha.db

import java.sql.Connection

import org.apache.spark.Logging

object DbReader extends Logging {

  def getVehiclePlateNo(conn: Connection, vid: String): (String,String) = {

    import com.dfssi.dataplatform.analysis.dbha.bean.VehicleInfoBean._

    val querySql = s"SELECT $vehicleIdField, $vehiclePlateField, $vehicleFleetIdField " +
      s" FROM $tableNameField WHERE $vehicleIdField='$vid'"

    try {
      val stmt = conn.createStatement()
      stmt.setMaxRows(1)

      val rs = stmt.executeQuery(querySql)

      if(rs != null && rs.next()) {
        return (rs.getString(s"$vehiclePlateField"), rs.getString(s"$vehicleFleetIdField"))
      }

    } catch {
      case exception: Exception =>
        logError( "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
          .printStackTrace())
    }

    logError(s"无法从SSI_VEHICLE表中获取$vid 的车队信息")
    ("", "")
  }
}
