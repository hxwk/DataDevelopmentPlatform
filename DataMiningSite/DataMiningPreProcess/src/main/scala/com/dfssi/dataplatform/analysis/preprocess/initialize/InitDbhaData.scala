package com.dfssi.dataplatform.analysis.preprocess.initialize

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.dbha.initdata.InitializeDbhaData
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import org.apache.spark.SparkContext

import scala.xml.Elem

class InitDbhaData extends AbstractInitProcess {


  override def isOver: Boolean = false

  override def getSparkContext(defEl: Elem,
                               sparkTaskDefEl: Elem): SparkContext = null

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val paramsMap: Map[String, String] = extractSimpleParams(defEl).toMap

    paramsMap.get("InitOption") match {

      case Some("InitDbhaIndicators") =>
        InitializeDbhaData.initIndicatorTable(paramsMap)

      case _ =>
        throw new UnsupportedOperationException("暂不支持的驾驶行为初始化选项")
    }
  }
}

object InitDbhaData {
  val processType: String = ProcessFactory.PROCESS_NAME_INIT_DBHA_DATA
}

