package com.dfssi.dataplatform.analysis.preprocess.initialize

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.offline.OfflineAnalysisService.applicationExist
import com.dfssi.dataplatform.analysis.utils.XmlUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.xml.Elem

class InitHiveContext extends AbstractInitProcess {

  private var over:Boolean = false

  override def isOver: Boolean = over

  override def getSparkContext(defEl: Elem,
                               sparkTaskDefEl: Elem): SparkContext = {

    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val sparkMaster = paramsMap.getOrElse("sparkMaster", "local")
    val appName = getSparkAppName(sparkTaskDefEl).getOrElse("NULL")
    val config = new SparkConf().setAppName(getSparkAppName(sparkTaskDefEl).getOrElse("NULL"))
    config.setMaster(sparkMaster);

    val sc = new SparkContext(config)

    if(applicationExist(appName, sc)){
      logError(s"任务${appName}已在Yarn的运行列表中...")
      this.over = true
      sc.stop()
      return null
    }
    sc.setLogLevel("ERROR")
    sc
  }

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)

    val initElem = paramsMap.get("BroadcastConfigLoader") match {
      case Some("IndicatorsConfig") =>
        IndicatorsConfig.loadConfig()
      case _ =>
        null
//        throw new UnsupportedOperationException("暂不支持的广播配置加载方法")
    }



    if(initElem != null)
      processContext.broadcastConfig = processContext.sparkContext.broadcast(initElem)

  }

  def getSparkAppName(sparkDefEl: Elem): Option[String] = {
    val name = XmlUtils.getAttrValue(sparkDefEl, "name")
    if (name == null) {
      return None
    }

    Some(name)
  }
}

object InitHiveContext {
  val processType: String = ProcessFactory.PROCESS_NAME_INIT_HIVE_CONTEXT
}
