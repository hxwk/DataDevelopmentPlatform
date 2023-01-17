package com.dfssi.dataplatform.analysis.dbha.initdata

import java.io.File

import com.dfssi.dataplatform.analysis.dbha.bean.IndicatorBean
import com.dfssi.dataplatform.analysis.dbha.bean.IndicatorTable._
import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig
import com.dfssi.dataplatform.analysis.dbha.rdbutils.OutputRdbms
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.{Elem, XML}

object InitializeDbhaData {

  val DBHA_INIT_DEF_FILE_NAME = "IntegrateTaskDef_dbha_initdata.xml"

  val DBHA_RESOURCE_PATH = "com/dfssi/dataplatform/analysis/dbha/config"

  def main(args: Array[String]): Unit = {

    val is = if (args.nonEmpty) {
        val path = args(0) + "/" + DBHA_INIT_DEF_FILE_NAME
      val url = new File(path).toURI
      url.toURL.openStream()
    }
    else {
      this.getClass.getClassLoader.getResourceAsStream(DBHA_RESOURCE_PATH + "/" + DBHA_INIT_DEF_FILE_NAME)
    }

    val sparkDefEl = XML.load(is)

    val outputsEl = XmlUtils.getSingleSubXmlEl(sparkDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUTS)

    if (outputsEl == null || (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT).isEmpty) {
      return
    }

    for (outputNode <- outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT) {
      val outputEl = outputNode.asInstanceOf[Elem]
      val paramsMap: mutable.Map[String, String] = extractSimpleParams(outputEl)

      initIndicatorTable(paramsMap.toMap)
    }

    is.close()
  }

  def extractSimpleParams(processEl: Elem): mutable.Map[String, String] = {
    val paramsNodes = processEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null
    if (paramsNodes.nonEmpty) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem]
    }
    extractParams(paramsEl)
  }

  def extractParams(paramsEl: Elem): mutable.Map[String, String] = {
    val paramsMap: mutable.Map[String, String] = new java.util.LinkedHashMap[String, String]()
    if (paramsEl != null) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        paramsMap.put(XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_NAME),
          XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE))
      }
    }

    paramsMap
  }

  /**
    * 初始化指标表
    * 根据{IndicatorBean.ID_INDICATORS_MAP}中的指标及其id，
    * 以及默认各维度及其对应指标
    * */
  def initIndicatorTable(paramsMap: Map[String, String]): Unit = {
    val name2id = IndicatorBean.EVENT_ID_TO_INDICATORS_MAP.map(_.swap)

    val safetyIndicators = DEFAULT_DIMENSION_INDICATORS(IndicatorsConfig.DBHA_DIMENSION_NAME_SAFETY)
      .map{ name =>
        IndicatorInfo(name2id(name), name, 2, IndicatorsConfig.DBHA_DIMENSION_ID_SAFETY)
      }
      .+:(IndicatorInfo(IndicatorsConfig.DBHA_DIMENSION_ID_SAFETY,
        IndicatorsConfig.DBHA_DIMENSION_NAME_SAFETY,
        1,
        IndicatorsConfig.DBHA_DIMENSION_ID_SAFETY))

    val ecoIndicators = DEFAULT_DIMENSION_INDICATORS(IndicatorsConfig.DBHA_DIMENSION_NAME_ECONOMY)
      .map{ name =>
        IndicatorInfo(name2id(name), name, 2, IndicatorsConfig.DBHA_DIMENSION_ID_ECONOMY)
      }
      .+:(IndicatorInfo(IndicatorsConfig.DBHA_DIMENSION_ID_ECONOMY,
        IndicatorsConfig.DBHA_DIMENSION_NAME_ECONOMY,
        1,
        IndicatorsConfig.DBHA_DIMENSION_ID_ECONOMY))

    val mainIndicators = DEFAULT_DIMENSION_INDICATORS(IndicatorsConfig.DBHA_DIMENSION_NAME_MAINTENANCE)
      .map{ name =>
        IndicatorInfo(name2id(name), name, 2, IndicatorsConfig.DBHA_DIMENSION_ID_MAINTENANCE)
      }
      .+:(IndicatorInfo(IndicatorsConfig.DBHA_DIMENSION_ID_MAINTENANCE,
        IndicatorsConfig.DBHA_DIMENSION_NAME_MAINTENANCE,
        1,
        IndicatorsConfig.DBHA_DIMENSION_ID_MAINTENANCE))

    val civiIndicators = DEFAULT_DIMENSION_INDICATORS(IndicatorsConfig.DBHA_DIMENSION_NAME_CIVILIZATION)
      .map{ name =>
        IndicatorInfo(name2id(name), name, 2, IndicatorsConfig.DBHA_DIMENSION_ID_CIVILIZATION)
      }
      .+:(IndicatorInfo(IndicatorsConfig.DBHA_DIMENSION_ID_CIVILIZATION,
        IndicatorsConfig.DBHA_DIMENSION_NAME_CIVILIZATION,
        1,
        IndicatorsConfig.DBHA_DIMENSION_ID_CIVILIZATION))

    val indicatorSet = new mutable.HashSet[IndicatorInfo]()
    safetyIndicators.foreach(f => indicatorSet.add(f))
    ecoIndicators.foreach(indicatorSet.add)
    mainIndicators.foreach(indicatorSet.add)
    civiIndicators.foreach(indicatorSet.add)

    OutputRdbms.saveDbhaIndicators(paramsMap, indicatorSet.toSet)

  }

}
