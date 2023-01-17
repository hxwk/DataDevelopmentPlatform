package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.DataFrame

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem

class ProcessEmptyValue extends AbstractProcess {

  val dropParams: mutable.ArrayBuffer[EmptyParamBean] = new mutable.ArrayBuffer[EmptyParamBean]();
  val filterParams: mutable.ArrayBuffer[EmptyParamBean] = new mutable.ArrayBuffer[EmptyParamBean]();
  val fillParams: mutable.ArrayBuffer[EmptyParamBean] = new mutable.ArrayBuffer[EmptyParamBean]();

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val inputIds: Array[String] = getInputs(defEl);
    val inputDF = processContext.dataFrameMap.get(inputIds(0));
    if (inputDF.isEmpty) {
      return
    }
    extractProcessParams(defEl)

    var df: DataFrame = inputDF.get
    df = processDrop(df);
    df = processFilter(df);
    df = processFill(df);

    processContext.dataFrameMap.put(id, df);
  }

  def processDrop(df: DataFrame): DataFrame = {
    if (dropParams.size <= 0) return df;

    val cols: ArrayBuffer[String] = new ArrayBuffer[String]();
    for (bean: EmptyParamBean <- dropParams) {
      cols += bean.colName
    }

    return df.na.drop(cols);
  }

  def processFilter(df: DataFrame): DataFrame = {
    if (filterParams.size <= 0) return df;
    val cols: ArrayBuffer[String] = new ArrayBuffer[String]();
    var newDf = df;
    for (bean: EmptyParamBean <- filterParams) {
      newDf = newDf.filter(bean.exp)
    }

    return newDf;
  }

  def processFill(df: DataFrame): DataFrame = {
    if (fillParams.size <= 0) return df;

    var valueMap: Map[String, Any] = Map[String, Any]();
    for (bean: EmptyParamBean <- fillParams) {
      valueMap += (bean.colName -> bean.toValue)
    }

    return df.na.fill(valueMap);
  }

  def extractProcessParams(defEl: Elem): Unit = {
    val paramsNodes = defEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null;
    if (paramsNodes.size > 0) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem];
    }
    if (paramsEl != null) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM;
      for (paramNode <- paramNodes) {
        var paramEl = paramNode.asInstanceOf[Elem];
        val bean = buildBeanByEl(paramEl);
        if ("drop".equalsIgnoreCase(bean.operator)) {
          dropParams += bean;
        } else if ("filter".equalsIgnoreCase(bean.operator)) {
          filterParams += bean;
        } else if ("fill".equalsIgnoreCase(bean.operator)) {
          fillParams += bean;
        }
      }
    }
  }

  def buildBeanByEl(paramEl: Elem): EmptyParamBean = {
    val bean: EmptyParamBean = new EmptyParamBean();
    bean.operator = XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_OPERATOR)
    bean.colName = XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_COLNAME)
    bean.exp = XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_EXP)
    bean.toValue = XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TOVALUE)

    return bean;
  }
}

object ProcessEmptyValue {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_EMPTY_VALUE
}
