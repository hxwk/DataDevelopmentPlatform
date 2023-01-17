package com.dfssi.dataplatform.analysis.dbha.behavior

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import org.apache.spark.sql.Row

/**
  * 驾驶行为解析识别接口
  */
trait BehaviorParserBase extends Serializable {

  def setUp(): Unit

  def cleanUp(): Unit

}

trait BehaviorParserOnIndividualRow extends BehaviorParserBase {

  def parseCurrentRow(row: Row): Unit

  def outputAllEvents(): Iterator[BasicEventBean]

//  def outputToHBaseCells(): Iterator[Cell]
}

trait BehaviorParserOnMultipleRows extends BehaviorParserBase {

  def parseMultipleRows2(rows1: Array[Row], rows2: Array[Row]): Unit

  def outputAllEvents(): Iterator[BasicEventBean]

}