package com.dfssi.dataplatform.analysis.preprocess.process.spatialindex

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.process.spatialindex.zorder.ZCurve2D
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.functions._
import org.apache.spark.mllib.linalg.Vector

import scala.collection.mutable
import scala.xml.Elem

/**
  * conver 2D (latitude, longitude) into 1D index code, according to
  * 1. zcurve
  *
  * @author lulin
  *         $date 2018/1/13
  */
class ProcessSpatialIndex extends AbstractProcess {

  import ProcessSpatialIndex._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem) {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val inputIds: Array[String] = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds(0))

    require(optionDF.nonEmpty)

    val latColName = getParamValue(paramsMap, "latCol")
    val lonColName = getParamValue(paramsMap, "lonCol")

    val distinceDF = optionDF.get.distinct()

    distinceDF.show(5)

    val df = new VectorAssembler().setInputCols(Array(latColName, lonColName))
      .setOutputCol(defaultPointColName)
      .transform(distinceDF)

    df.show(5)

    val resolution = paramsMap.getOrElse("resolution", "9").toInt

    val zCurve2D = new ZCurve2D(resolution)

    val toHilbertCode = udf { point2D: Vector =>
      zCurve2D.toIndex(point2D(0)/1.0e6, point2D(1)/1.0e6).toHexString
    }

    val resultDF = df.withColumn(defaultIndexColName, toHilbertCode(df.col(defaultPointColName)))

    resultDF.show(5)

    processContext.dataFrameMap.put(id, resultDF)

  }
}

object ProcessSpatialIndex {

  val processType = ProcessFactory.PROCESS_NAME_PREPROCESS_SPATIAL_ENCODE

  val defaultPointColName = "point2D"

  val defaultIndexColName = "geoHashCode"

  def toZCurve2D(lat: Double, lon: Double, resolution: Int = 8) = {
    new ZCurve2D(resolution).toIndex(lat, lon)
  }
}