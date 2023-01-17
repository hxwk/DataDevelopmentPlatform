package com.dfssi.dataplatform.analysis.algorithm.pearsoncorrelation

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.stat.{Correlation => MLCorrelation}

import scala.collection.mutable
import scala.xml.Elem

class ProcessCorrelation extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

    val inputIds = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds.head)
    require(optionDF.nonEmpty)

    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    //    val action = getParamValue(paramsMap, "action")
    //    val saveTo: String = getParamValue(paramsMap, "saveTo")

    val df = optionDF.get
    // select the dependent variable column （因变量）
    val dependentColName = getParamValue(paramsMap, "dependentCol")
    val dependentVariable = df.select(dependentColName).map(_.getDouble(0))

    // select the independent variables column （自变量）
    val independentColNames = paramsMap.get("independentCols") match {
      case Some(v) => v.split(",")
      case None =>
        df.columns.diff(dependentColName)
    }

    // compute the pearson correlation
    val arr = independentColNames.map { colName =>
      val independentVariable = df.select(colName) map (_.getDouble(0))
      val corrValue = MLCorrelation.corr(dependentVariable, independentVariable, "pearson")
      (dependentColName, colName, corrValue.toFloat)
    }

    val sqlContext = processContext.hiveContext
    import sqlContext.implicits._
    val sc = processContext.sparkContext
    val resultDF = sc.parallelize(arr).toDF("dependentVarName", "independentVarName", "corrValue")

    resultDF.show()

    processContext.dataFrameMap.put(id, resultDF)

  }

}
