package selfTest

import java.util

import org.apache.spark.sql.DataFrame
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import com.dfssi.dataplatform.analysis.common.ProcessContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.localMllib.clustering.dbscan._
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.sql.{Column, Row}
import org.apache.spark.localMllib.linalg.{Vectors, Vector => MV}
import org.apache.spark.sql.functions._

import scala.collection.mutable
import scala.xml.{Elem, XML}

object dbscan_test {

    def main(args:Array[String]):Unit = {
        val conf = new SparkConf()
                .setAppName("DBSCANTest")
                .setMaster("local")
        val sc = new SparkContext(conf)
        sc.setLogLevel("WARN")

        val processContext:ProcessContext = new ProcessContext()
        processContext.dataFrameMap = mutable.Map[String,DataFrame]()
        processContext.hiveContext = new HiveContext(sc)
        processContext.processType = ProcessContext.PROCESS_TYPE_OFFLINE
        processContext.sparkContext = sc

        val hiveContext = processContext.hiveContext
        hiveContext.sql("use prod_analysis")
        val rawData = hiveContext.sql("select * from kmeans")
        val colNames = rawData.columns
        val data_cols = convertDecimal(colNames)
        val data = rawData.select(data_cols:_*)
        data.show()

        val assembler = new VectorAssembler()
                .setInputCols(colNames)
                .setOutputCol("features")
        assembler.transform(data).select("features").printSchema()

        val inputRDD = assembler.transform(data).select("features").map{
            x=>x.getAs[MV](0)
        }


        val dbscan_model = DBSCAN.train(inputRDD,1,1,1)
        val labelPoints = dbscan_model.labeledPoints
        labelPoints.foreach{point=>
            println(point.toString)
        }


        sc.stop()

    }

    def convertDecimal(colNames:Array[String]):Seq[Column]={
        colNames.map{name=>
            col(name).cast("double")
        }.toSeq
    }
}
