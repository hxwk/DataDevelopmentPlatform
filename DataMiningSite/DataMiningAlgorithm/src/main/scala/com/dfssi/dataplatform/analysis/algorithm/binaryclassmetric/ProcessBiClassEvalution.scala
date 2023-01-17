package com.dfssi.dataplatform.analysis.algorithm.binaryclassmetric

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, TaskInfoStore, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.evaluation.BinaryClassificationEvaluator
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.sql.functions.udf


import scala.xml.Elem

class ProcessBiClassEvalution extends AbstractProcess with TaskInfoStore {
    import ProcessBiClassEvalution._

    override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem):Unit = {
        val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
        val task_id = XmlUtils.getAttrValue(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)


        val inputIds = getInputs(defEl)
        val optionDF = processContext.dataFrameMap.get(inputIds.head)
        require(optionDF.nonEmpty)
        val data = optionDF.get

        val paramsMap = extractSimpleParams(defEl)

        val labelCol = getParamValue(paramsMap, "labelCol")
        val rawPredictCol = "Prediction"


        val evaluator = new BinaryClassificationEvaluator()
                .setLabelCol(labelCol)
                .setRawPredictionCol(rawPredictCol)

        val getFilled = udf((x: Double)=>1-x)
        val data_df = data.withColumn("tmpCol", getFilled(data.col("Prediction")))
        val assembler = new VectorAssembler()
                .setInputCols(Array("tmpCol","Prediction"))
                .setOutputCol("predicVector")

        val final_df = assembler.transform(data_df)
        val areaUnderROC = evaluator.evaluate(final_df)

        println("AreaUnderROC is ----->"+areaUnderROC)

        val table_name = "result_metrics"
        val sql = s"insert into ${table_name} (taskId, areaUnderROC) values (${task_id},${areaUnderROC}) " +
                s"on duplicate key update areaUnderROC=${areaUnderROC}"
        val conn = getMysqlConnection()
        storeAlgorithmMetric(sql, conn.get)

    }

}

object ProcessBiClassEvalution {
    val processType = "AlgorithmBiClassEvalution"

}
