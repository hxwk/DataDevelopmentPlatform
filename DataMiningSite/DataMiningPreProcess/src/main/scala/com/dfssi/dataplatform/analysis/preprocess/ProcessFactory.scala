package com.dfssi.dataplatform.analysis.preprocess

import java.util

import com.dfssi.dataplatform.analysis.algorithm.kmeans.ProcessKMeans
import com.dfssi.dataplatform.analysis.algorithm.linearregression.ProcessLinearRegression
import com.dfssi.dataplatform.analysis.algorithm.logisticregression.ProcessLogisticRegression
import com.dfssi.dataplatform.analysis.algorithm.randomforest.ProcessRF
import com.dfssi.dataplatform.analysis.common.AbstractProcess
import com.dfssi.dataplatform.analysis.preprocess.initialize.{InitDbhaData, InitHiveContext}
import com.dfssi.dataplatform.analysis.preprocess.input.local.InputCsvFile
import com.dfssi.dataplatform.analysis.preprocess.input.streaming.InputKafkaReceiver
import com.dfssi.dataplatform.analysis.preprocess.input.{InputGreenplumTable, InputHiveSql, InputHiveTable, InputMysqlTable}
import com.dfssi.dataplatform.analysis.preprocess.output.{OutputHiveTable, OutputJDBC}
import com.dfssi.dataplatform.analysis.preprocess.process._
import com.dfssi.dataplatform.analysis.preprocess.process.elasticsearch.{ProcessKafka0200ToEs, ProcessKafka0705ToEs, ProcessKafkaDataToEs}
import com.dfssi.dataplatform.analysis.preprocess.process.fuel.{ProcessKafkaToFuel, ProcessWorkConditionFuel}
import com.dfssi.dataplatform.analysis.preprocess.process.rateindicator.{PreprocessTrips, PreprocessTripsV2, ProcessIndicatorScore, ProcessIndicatorStat}
import com.dfssi.dataplatform.analysis.preprocess.process.spatialindex.ProcessSpatialIndex

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable

object ProcessFactory {

  // added by lulin  @since 2018/01/05
  val PROCESS_NAME_INIT_HIVE_CONTEXT = "InitHiveContext"
  val PROCESS_NAME_INIT_DBHA_DATA = "InitDbhaData"

  val PROCESS_NAME_PREPROCESS_SPATIAL_ENCODE = "PreprocessToIndex"
  val PROCESS_NAME_ALGORITHM_LINEAR_REGRESSION = "AlgorithmLinearRegression"
  val PROCESS_NAME_ALGORITHM_LOGISTIC_REGRESSION = "AlgorithmLogisticRegression"

  val PROCESS_NAME_INPUT_HIVE_TABLE = "InputHiveTable"
  val PROCESS_NAME_INPUT_HIVE_SQL = "InputHiveSql"
  val PROCESS_NAME_INPUT_CSV_FILE = "InputCsvFile"
  val PROCESS_NAME_INPUT_MYSQL_TABLE = "InputMysqlTable"
  val PROCESS_NAME_INPUT_GREENPLUM_TABLE = "InputGreenplumTable"
  val PROCESS_NAME_INPUT_KAFKA_RECEIVER = "StreamingInputKafkaReceiver"
  val PROCESS_NAME_INPUT_KAFKA_DIRECT_RECEIVER = "StreamingInputKafkaDirectReceiver"

  val PROCESS_NAME_PREPROCESS_JOIN = "PreprocessJoin"
  val PROCESS_NAME_PREPROCESS_MAP = "PreprocessMap"
  val PROCESS_NAME_PREPROCESS_SELECT = "PreprocessSelect"
  val PROCESS_NAME_PREPROCESS_CONVERT = "PreprocessConvert"
  val PROCESS_NAME_PREPROCESS_REGULAR = "PreprocessRegular"
  val PROCESS_NAME_PREPROCESS_EMPTY_VALUE = "PreprocessEmptyValue"
  val PROCESS_NAME_PREPROCESS_TRIPS = "PreprocessTrips"
  val PROCESS_NAME_PREPROCESS_TRIPS_V2 = "PreprocessTripsV2"
  val PROCESS_NAME_PREPROCESS_INDICATOR_STAT = "PreprocessIndicatorStat"
  val PROCESS_NAME_PREPROCESS_KAFKATOHIVE = "KafkaToHive"
  val PROCESS_NAME_PREPROCESS_INDICATOR_SCORE = "PreprocessIndicatorScore"


  val PROCESS_NAME_PREPROCESS_KAFKA0200TOES = "Kafka0200ToEs"
  val PROCESS_NAME_PREPROCESS_KAFKA0705TOES = "Kafka0705ToEs"
  val PROCESS_NAME_PREPROCESS_KAFKADATATOES = "KafkaDataToEs"
  val PROCESS_NAME_PREPROCESS_KAFKATOFUEL = "KafkaToFuel"
  val PROCESS_NAME_PREPROCESS_WORKCONDITIONFUEL = "WorkConditionFuel"

  val PROCESS_NAME_ALGORITHM_KMEANS = "AlgorithmKMeans"

  val PROCESS_NAME_OUTPUT_HIVE_TABLE = "OutputHiveTable"
  val PROCESS_NAME_OUTPUT_JDBC = "OutputJDBC"
  val PROCESS_NAME_OUTPUT_ES = "OutputElasticSearch"

  //coordinator测试
  val PROCESS_NAME_PREPROCESS_COORDINATORDEMO = "CoordinatorDemo"

  val processMap: mutable.Map[String, Class[_]] = new util.LinkedHashMap[String, Class[_]]()

  def getProcess(processType: String): AbstractProcess = {
    if (processMap.get(processType).isDefined) {
      val classz: Class[_] = processMap(processType)
      return classz.newInstance().asInstanceOf[AbstractProcess]
    }
    null
  }

  def registerProcess(processName: String, processClass: Class[_]): Unit = {
    processMap.put(processName, processClass)
  }

  // added by lulin  @since 2018/01/05
  registerProcess(InitHiveContext.processType, new InitHiveContext().getClass)
  registerProcess(InitDbhaData.processType, new InitDbhaData().getClass)

  registerProcess(InputCsvFile.processType, new InputCsvFile().getClass)
  registerProcess(ProcessSpatialIndex.processType, new ProcessSpatialIndex().getClass)
  registerProcess(ProcessLinearRegression.processType, new ProcessLinearRegression().getClass)
  registerProcess(ProcessLogisticRegression.processType, new ProcessLogisticRegression().getClass)
  registerProcess(ProcessRF.processType, new ProcessRF().getClass)

  registerProcess(InputHiveTable.processType, new InputHiveTable().getClass)
  registerProcess(InputHiveSql.processType, new InputHiveSql().getClass)
  registerProcess(InputKafkaReceiver.processType, new InputKafkaReceiver().getClass)
  registerProcess(InputGreenplumTable.processType, new InputGreenplumTable().getClass)
  registerProcess(InputMysqlTable.processType, new InputMysqlTable().getClass)

  registerProcess(ProcessJoin.processType, new ProcessJoin().getClass)
  registerProcess(ProcessMap.processType, new ProcessMap().getClass)
  registerProcess(ProcessSelect.processType, new ProcessSelect().getClass)
  registerProcess(ProcessConvert.processType, new ProcessConvert().getClass)
  registerProcess(ProcessRegular.processType, new ProcessRegular().getClass)
  // special processes
  registerProcess(PreprocessTrips.processType, new PreprocessTrips().getClass)
  registerProcess(PreprocessTripsV2.processType, new PreprocessTripsV2().getClass)

  registerProcess(ProcessKafkaToHive.processType, new ProcessKafkaToHive().getClass)
  registerProcess(ProcessIndicatorStat.processType, new ProcessIndicatorStat().getClass)
  registerProcess(ProcessIndicatorScore.processType, new ProcessIndicatorScore().getClass)

  registerProcess(ProcessEmptyValue.processType, new ProcessEmptyValue().getClass)
  registerProcess(ProcessKMeans.processType, new ProcessKMeans().getClass)

  registerProcess(ProcessKafka0200ToEs.processType, classOf[ProcessKafka0200ToEs])
  registerProcess(ProcessKafka0705ToEs.processType, classOf[ProcessKafka0705ToEs])
  registerProcess(ProcessKafkaDataToEs.processType, classOf[ProcessKafkaDataToEs])
  registerProcess(ProcessKafkaToFuel.processType, classOf[ProcessKafkaToFuel])
  registerProcess(ProcessWorkConditionFuel.processType, classOf[ProcessWorkConditionFuel])

  registerProcess(OutputHiveTable.processType, new OutputHiveTable().getClass)
  registerProcess(OutputJDBC.processType, new OutputJDBC().getClass)

  registerProcess(ProcessCoordinatorDemo.processType, classOf[ProcessCoordinatorDemo])

}
