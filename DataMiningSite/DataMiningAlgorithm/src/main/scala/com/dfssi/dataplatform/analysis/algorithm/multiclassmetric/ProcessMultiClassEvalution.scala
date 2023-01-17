package com.dfssi.dataplatform.analysis.algorithm.multiclassmetric

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext, TaskInfoStore}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.evaluation.MulticlassClassificationEvaluator

import scala.xml.Elem

class ProcessMultiClassEvalution extends AbstractProcess with TaskInfoStore {
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

        val evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol(labelCol)
                .setPredictionCol(rawPredictCol)
        val indexName = Array("f1","accuracy","weightedRecall","weightedPrecision")

        val all_score = evaluator.allEvaluate(data)

        val table_name = "result_metrics"
        val sql = s"insert into ${table_name} (taskId, f1, weighted_precision, weighted_recall,accuracy) " +
                s"values (${task_id},${all_score.fMeasure}," +
                s"${all_score.weightedPrecision},${all_score.weightedRecall},${all_score.accuracy}) " +
                s"on duplicate key update f1=${all_score.fMeasure},weighted_precision=${all_score.weightedPrecision}," +
                s"weighted_recall=${all_score.weightedRecall},accuracy=${all_score.accuracy}"
        val conn = getMysqlConnection()
        storeAlgorithmMetric(sql, conn.get)


    }
}

object ProcessMultiClassEvalution {
    val processType = "AlgorithmMultiClassEvalution"
}
