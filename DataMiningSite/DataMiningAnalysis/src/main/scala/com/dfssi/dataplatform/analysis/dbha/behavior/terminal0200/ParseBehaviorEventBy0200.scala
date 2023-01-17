package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200

import com.dfssi.dataplatform.analysis.dbha.behavior.BehaviorParserOnIndividualRow
import com.dfssi.dataplatform.analysis.dbha.utils._0200RowValueGetter
import org.apache.spark.Logging
import org.apache.spark.sql.Row

abstract class ParseBehaviorEventBy0200 extends BehaviorParserOnIndividualRow
  with _0200RowValueGetter with Logging {


  def get0200Speed(row: Row): Long = if (getVDRSpeed(row) > 0)
    getVDRSpeed(row)
  else getGPSSpeed(row)

}
