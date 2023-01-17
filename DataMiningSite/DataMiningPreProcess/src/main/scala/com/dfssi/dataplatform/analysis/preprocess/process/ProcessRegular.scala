package com.dfssi.dataplatform.analysis.preprocess.process

import java.util

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{DataTypeParser, SparkDefTag, XmlUtils}
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.{DataFrame, Row}
import org.wltea.expression.ExpressionEvaluator
import org.wltea.expression.datameta.Variable

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions
import scala.xml.Elem


/**
  * The supported types are: string, boolean, byte, short, int, long, float, double, decimal, date, timestamp.
  */
class ProcessRegular extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem) {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val inputIds: Array[String] =  getInputs(defEl);
    val inputDF = processContext.dataFrameMap.get(inputIds(0));
    if (inputDF.isEmpty) {
      return
    }

    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    val dataTypeStr = getParamValue(paramsMap, "dataType")

    val df: DataFrame = inputDF.get
    var schema = df.schema
    schema = schema.add(StructField(getParamValue(paramsMap, "destColName"), DataTypeParser.toDataType(dataTypeStr),
      true))

    val expression = getParamValue(paramsMap, "condition")

    val newRDD = df.map(row => {
      val oldSeq: Seq[Any] = row.toSeq;
      val newSeq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
      for (obj <- oldSeq) {
        newSeq += obj
      }

      val variables: util.List[Variable] = new util.ArrayList[Variable]();
      row.schema.map(structField => {
        val fieldVal = SparkStageCodeUtils.getFieldName(row, structField)
        if (fieldVal != null) {
          variables.add(Variable.createVariable(structField.name, fieldVal));
        }
      })

      val result = ExpressionEvaluator.evaluate(expression, variables);
      newSeq += result

      Row.fromSeq(newSeq)
    })

    val newDF = processContext.hiveContext.createDataFrame(newRDD, schema)
    processContext.dataFrameMap.put(id, newDF)
  }
}

object ProcessRegular {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_REGULAR
}


