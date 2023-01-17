package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import java.sql.Timestamp
import java.util.Properties

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

import scala.xml.Elem


/**
  * 指标分析和打分模块
  * 1. 确定要分析的指标所属的组
  * 2. 指标分布表更新模式
  *
  * 获取所有的指标，对每个指标求其统计量：最大最小，均值，标准差，四分位数等
  *
  * @author lulin
  */
class ProcessIndicatorStat extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: Map[String, String] = extractSimpleParams(defEl).toMap

    val inputIds: Array[String] = getInputs(defEl)

    val groupTypeStr = paramsMap.getOrElse("groupType", "NONE")
    val groupType = groupTypeStr match {
      case "NONE" => 0
      case "FLEET" => 1
      case "MODEL" => 2 // 车型
    }

    // dbha_trip_indicators 表
    val optionTripIndicatorsDF = processContext.dataFrameMap.get(inputIds(0))
    require(optionTripIndicatorsDF.nonEmpty)

    // dbha_indicator_distribution 表
    val optionIndicatorDisDF = processContext.dataFrameMap.get(inputIds(2))
    require(optionIndicatorDisDF.nonEmpty)

    /** 首先对指标进行统计分析，计算每个指标的相关统计量 */
    val relatedGroupIndicatorDisDF = optionIndicatorDisDF.get.where(s"group_type = $groupType")

    val unrelatedGroupIndicatorDisDF = optionIndicatorDisDF.get
      .where(s"group_type <> $groupType").drop(col("id"))

    // 是否基于新增处理的行程信息增量更新指标分布表
    val updatedIndicatorStatTableDF = paramsMap.get("updateMode") match {
      case Some("INCREMENTAL") => // 增量更新
        val simpleTripDetailDF = processContext.dataFrameMap(inputIds(1))

        val tripEndTimeRdd = simpleTripDetailDF.map(_.getAs[Long]("trip_end_time"))
        val minEndTime = new Timestamp(tripEndTimeRdd.min())
        val maxEndTime = new Timestamp(tripEndTimeRdd.max())

        val validTripIndicatorsDF = optionTripIndicatorsDF.get.where(s"trip_end_time >='$minEndTime' AND trip_end_time <='$maxEndTime'")

        val selectSql = Array("indicator_id",
          "indicator_name",
          "group_type",
          "(CASE WHEN count IS NULL THEN num ELSE count+num END) as count",
          "(CASE WHEN sum_value IS NULL THEN sum_val ELSE sum_value+sum_val END) as sum_value",
          "(CASE WHEN sum_square_value IS NULL THEN sum_square_val ELSE sum_square_value+sum_square_val END) as sum_square_value",
          "0.0 as min_value",
//          "(CASE WHEN min_value IS NULL THEN min_val WHEN min_value<min_val THEN min_value ELSE min_val END) AS min_value",
          "(CASE WHEN max_value IS NULL THEN max_val WHEN max_value>max_val THEN max_value ELSE max_val END) AS max_value")

        groupTypeStr match {
          case "NONE" => // 仅基于指标
            val stat = updateIndicatorValueStats(validTripIndicatorsDF, groupType)
            val tmp = stat
              .join(relatedGroupIndicatorDisDF, Seq("indicator_id", "indicator_name","group_type"), joinType="left_outer")
              .selectExpr(selectSql: _*)
            tmp

          case "FLEET" =>
            // 获取每个driver的车队信息
            val vid2fid = simpleTripDetailDF.map{ row =>
              row.getAs[String]("vehicle_id") -> row.getAs[String]("fleet_id")
            }.distinct()
              .collectAsMap()

            val getFleetIdUDF = udf { (vehicleId: Any) =>
              vid2fid(vehicleId.asInstanceOf[String])
            }

            val indicatorsWithFleetIdDF = validTripIndicatorsDF.withColumn("fleet_id", getFleetIdUDF(col("vehicle_id")))
            indicatorsWithFleetIdDF.show(5)
            // 获取统计数据
            val stat = updateIndicatorValueStats(indicatorsWithFleetIdDF, groupType)

            stat.join(relatedGroupIndicatorDisDF, Seq("indicator_id", "group_type"), joinType="left_outer")
              .selectExpr(selectSql: _*)

          case "MODEL" => // 车型维度
            throw new UnsupportedOperationException("暂不支持按车型统计")
        }

      case _ => // 全量更新
        // trip_detail 表中数据

        val selectSql = Array("indicator_id",
          "indicator_name",
          "group_type",
          "num as count",
          "sum_val as sum_value",
          "sum_square_val as sum_square_value",
          "min_val AS min_value",
          "max_val AS max_value")

        groupTypeStr match {
          case "NONE" => // 所有指标
            val stat = updateIndicatorValueStats(optionTripIndicatorsDF.get, groupType)
            stat.selectExpr(selectSql: _*)

          case "FLEET" =>
            val optionTripDetailDF = processContext.dataFrameMap.get(inputIds(1)) // 第三个输入
            require(optionTripDetailDF.nonEmpty)

            // 获取每个driver的车队信息
            val vid2fid = optionTripDetailDF.get.map{ row =>
              row.getAs[String]("vehicle_id") -> row.getAs[String]("fleet_id")
            }.distinct()
              .collectAsMap()

            val getFleetIdUDF = udf { (vehicleId: Any) =>
              vid2fid(vehicleId.asInstanceOf[String])
            }

            val indicatorsDF = optionTripIndicatorsDF.get.withColumn("fleet_id", getFleetIdUDF(col("vehicle_id")))
            // 获取统计数据
            val stat = updateIndicatorValueStats(indicatorsDF, groupType)

            stat.drop(col("fleet_id")).selectExpr(selectSql: _*)

          case "MODEL" => // 车型维度
            throw new UnsupportedOperationException("暂不支持按车型统计")
        }
    }

    val newIndicatorStatTableDF = updatedIndicatorStatTableDF.unionAll(unrelatedGroupIndicatorDisDF)

    processContext.dataFrameMap.put(id, newIndicatorStatTableDF)
  }


  /**
    *
    * @param tripIndicatorsDF 待分析的指标
    * @param groupType         分组类型，默认0
    * @return
    */
  def updateIndicatorValueStats(tripIndicatorsDF: DataFrame, groupType: Int = 0): DataFrame = {

    val groupedDF = groupType match {
      case 0 =>
        tripIndicatorsDF.groupBy("indicator_id", "indicator_name")
      case 1 =>
        tripIndicatorsDF.groupBy("indicator_id", "indicator_name", "fleet_id")
      case _ =>
        throw new UnsupportedOperationException("暂不支持该groupType")
    }

    val tmp = groupedDF.agg(count("indicator_value") as "num",
      sum("indicator_value") as "sum_val",
      sum(pow("indicator_value", 2)) as "sum_square_val",
      //mean("indicator_value") as "mean_val",
      //stddev("indicator_value") as "stddev_val",
//      min("indicator_value") as "min_val",
      max("indicator_value") as "max_val")
      .withColumn("min_val", lit(0))
      .withColumn("group_type", lit(groupType))

    tmp
  }


  def saveStatResult(df: DataFrame, paramsMap: Map[String, String]): Unit = {
    val jdbcHostname = paramsMap.getOrElse("jdbcHostname", "172.16.1.241")
    val jdbcPort = paramsMap.getOrElse("jdbcPort","3306")
    val jdbcDatabase = paramsMap.getOrElse("jdbcDatabase","SSI_MOTORCADE")
    val jdbc_url = s"jdbc:mysql://$jdbcHostname:$jdbcPort/$jdbcDatabase?useSSL=false"

    val user = paramsMap.getOrElse("user", "ssiuser")
    val password = paramsMap.getOrElse("password", "112233")

    val connectionProperties = new Properties()
    connectionProperties.put("driver", "com.mysql.jdbc.Driver")
    connectionProperties.put("user", user)
    connectionProperties.put("password", password)

    val dbtable = paramsMap.getOrElse("dbtable", "dbha_stat_indicators")

    df.write.mode(SaveMode.Overwrite).jdbc(jdbc_url, dbtable, connectionProperties)
  }

}



object ProcessIndicatorStat {

  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_INDICATOR_STAT

  // Test
  def main(args: Array[String]): Unit = {
//    loadIndicators
    val config = new SparkConf().setMaster("local[*]").setAppName("indicator")
    val spark = new SparkContext(config)

    spark.setLogLevel("WARN")

    val hiveContext = new HiveContext(spark)

    val date = "20180205"

    // 从Hive表中读取每段行程的数据进行处理
    val df = hiveContext
      .sql("SELECT DISTINCT a.gps_time, a.speed1/10.0 AS gps_speed1, b.value AS can_speed, a.speed1/10.0-b.value AS speedDiff " +
        "FROM prod_analysis.terminal_0200 a, prod_analysis.terminal_0705 b " +
        s" WHERE a.gps_time=b.receive_time AND b.signal_name like '%车速%' AND a.vid='035baa41db8a4500873ded260a3019ec' AND b.vid='035baa41db8a4500873ded260a3019ec' AND b.part_yearmonthday=$date AND a.part_yearmonthday=$date")

    println(date)
    df.show(20)

    val paramsMap = Map[String, String]()
    val jdbcHostname = paramsMap.getOrElse("jdbcHostname", "172.16.1.241")
    val jdbcPort = paramsMap.getOrElse("jdbcPort","3306")
    val jdbcDatabase = paramsMap.getOrElse("jdbcDatabase","SSI_MOTORCADE")
    val dbtable = paramsMap.getOrElse("dbtable", "speed_improve")
    val jdbc_url = s"jdbc:mysql://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"

    val user = paramsMap.getOrElse("user", "ssiuser")
    val password = paramsMap.getOrElse("password", "112233")

    val connectionProperties = new Properties()
    connectionProperties.put("driver", "com.mysql.jdbc.Driver")
    connectionProperties.put("user", user)
    connectionProperties.put("password", password)

    df.write.mode(SaveMode.Append).jdbc(jdbc_url, dbtable, connectionProperties)

    /*val fleetId = 1
    val motorTypeId = 1

    val indicatorName2Id = IndicatorBean.ID_INDICATORS_MAP.map(_.swap)

    val cols  = Array("id", "fleet_id", "motor_type_id", "group_type", "indicator_id", "indicator_name", "weight")

    val data = Default_Indicator_Weights.map{ case(indicatorName, weight) =>
      val id = indicatorName2Id(indicatorName)
      WeightContainer(UUID.randomUUID().toString, 1, 1, 1, id, indicatorName, weight)
    }.toSeq

    import hiveContext.implicits._
    val df = spark.parallelize(data).toDF(cols: _*)

    val paramsMap = Map[String, String]()
//    val jdbcHostname = paramsMap.getOrElse("jdbcHostname", "172.16.1.221")
    val jdbcHostname = paramsMap.getOrElse("jdbcHostname", "172.16.1.241")
//    val jdbcPort = paramsMap.getOrElse("jdbcPort","5432")
    val jdbcPort = paramsMap.getOrElse("jdbcPort","3306")
//    val jdbcDatabase = paramsMap.getOrElse("jdbcDatabase","analysis")
    val jdbcDatabase = paramsMap.getOrElse("jdbcDatabase","SSI_MOTORCADE") //
    val dbtable = paramsMap.getOrElse("dbtable", "indicators_distribution")

//    val user = paramsMap.getOrElse("user", "analysis")
    val user = paramsMap.getOrElse("user", "ssiuser") //
    val password = paramsMap.getOrElse("password", "112233")

    val connectionProperties = new Properties()
//    connectionProperties.put("driver", "com.pivotal.jdbc.GreenplumDriver") //com.mysql.jdbc.Driver
    connectionProperties.put("driver", "com.mysql.jdbc.Driver") //
    connectionProperties.put("user", user)
    connectionProperties.put("password", password)

//    val jdbc_url = s"jdbc:pivotal:greenplum://${jdbcHostname}:${jdbcPort}"
    val jdbc_url = s"jdbc:mysql://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"

    df.write.mode(SaveMode.Append).jdbc(jdbc_url, dbtable, connectionProperties)*/

  }

  // A 司提供的相关指标的权重
  @Deprecated
  val Default_Indicator_Weights = Map(
  "百公里大油门时长"->	0.2209,
  "百公里刹车里程"->	0.0978,
  "百公里经济速度里程"->	0.0892,
  "档位速度匹配"->	0.0531,
  "百公里高转速起步次数"->	0.0532,
  "百公里高档行驶里程"->	0.0778,
  "百公里超长怠速次数"->	0.0877,
  "百公里停车次数"->	0.0707,
  "百公里急加速次数"->	0.1184,
  "速度波动性"->	0.0594,
  "百公里怠速时长"->	0.0718,
  "百公里急减速次数"->	0.4792,
  "百公里先离合后刹车次数"->	0.2918,
  "百公里空档滑行次数"->	0.2290,
  "百公里刹车时长"->	0.2319,
  "百公里离合时长"->	0.2448,
  "百公里长时刹车次数"->	0.1489,
  "百公里长时离合次数"->	0.3744
  )

}