package com.dfssi.dataplatform.analysis.algorithm.stationRec

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.geodistance.DistanceUtils
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

import scala.collection.mutable
import scala.xml.Elem

/**
  * 服务站推荐
  * Solution 1: 获取所有的服务站数据，然后入es进行索引，在通过es推荐特定点最近的服务站
  * Solution 2：或区域所有的服务站数据，对经纬度进行GeoHash编码，然后入库或者缓存，
  * 给定一个点，先进行编码找到共同前缀最多的前k个服务站，在计算这些服务站的距离进行排序
  */
class ProcessStationRec extends AbstractProcess {

  import ProcessStationRec.{lenOfCommonPrefix, StationInfo}

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val action: String = getParamValue(paramsMap, "action")

    val inputIds: Array[String] = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds(0))

    require(optionDF.nonEmpty)

    val latColName = getParamValue(paramsMap, "latCol")
    val lonColName = getParamValue(paramsMap, "lonCol")

    val df = new VectorAssembler().setInputCols(Array(latColName, lonColName))
      .setOutputCol("point2D")
      .transform(optionDF.get)

  }

  def getKClosetStations(df: DataFrame, lat1: Double, lon1: Double, k: Int) = {
    val distance = udf { point2D: Vector[Double] =>
      DistanceUtils.distVincentyRAD(lat1, lon1, point2D(0), point2D(1))
    }

    df.withColumn("distance", df.col("point2D"))
      .sort("distance", "star")
  }


}

object ProcessStationRec {

  case class StationInfo(name: String, lat: Double, lng: Double, ghCode: String, starLevel: Int)

  def lenOfCommonPrefix(ghCode1: String, ghCode2: String): Int = {
    require(ghCode1.length == ghCode2.length, s"$ghCode1 and $ghCode2 should have same encode length!")

    for(i <- ghCode1.length-1 to 0)
      if(ghCode1.charAt(i) == ghCode2.charAt(i))
        return ghCode1.length - i
    0
  }


  def getKCloseStations(stations: Seq[StationInfo], lat1: Double, lon1: Double, k: Int) = {
    stations.map{ case StationInfo(name, lat2, lon2, _, starLevel) =>
      (name, DistanceUtils.distVincentyRAD(lat1, lon1, lat2, lon2), starLevel)
    }
      .sortBy(-_._2)
      .take(k)
  }

  implicit val MyOrdering: Ordering[(Int, StationInfo)] =
    Ordering.by {
      case (fst, _) => fst
    }

  def getKCloseStations(stations: Seq[StationInfo], point: (Double, Double, String), k: Int) = {

    val queue = mutable.PriorityQueue()

    stations.foreach{ station =>
      val len = lenOfCommonPrefix(station.ghCode, point._3)
      queue.enqueue((len, station))
    }

    queue.dequeueAll.take(k).map{ case (_, station) =>
      (DistanceUtils.distVincentyRAD(point._1, point._2, station.lat, station.lng), station)
    }.sortBy(_._1)
      .map(_._2)
  }

}