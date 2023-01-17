package com.dfssi.dataplatform.analysis.preprocess.input.local

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}

import scala.collection.mutable
import scala.xml.Elem

class InputCsvFile extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)

    val filePath = getParamValue(paramsMap, "filePath")
    val header = if (paramsMap.contains("header") && getParamValue(paramsMap, "header") == "false") false
    else true

    val tmp = processContext.sparkContext.textFile(filePath)

    val headerString = tmp.take(1).head.split(",")

    val (fields, rdd) = if (header) {
      (headerString.map(fieldName => StructField(fieldName.replace("\"", ""), StringType, nullable = true)),
        tmp.filter(!_.contains(headerString.head)))
    } else {
      (headerString.indices.map(i => StructField("_" + (i+1), StringType, nullable = true)).toArray,
        tmp)
    }

    val rowRDD = rdd.map { line =>
      var arr = line.split("\"").filter(s => StringUtils.isNotEmpty(s) && s != ",") //
      if(arr.length == 1)
        arr = arr.head.split(",")
      arr }
      // 下面的过滤以后要去掉
//      .filter(_(3).length > 10)
      .map(Row.fromSeq(_))

    val sqlContext: SQLContext = processContext.hiveContext
    val schema = StructType(fields)
    val inputDF = sqlContext.createDataFrame(rowRDD, schema)

    processContext.dataFrameMap.put(id, inputDF)
  }
}

object InputCsvFile {
  val processType: String = ProcessFactory.PROCESS_NAME_INPUT_CSV_FILE
}