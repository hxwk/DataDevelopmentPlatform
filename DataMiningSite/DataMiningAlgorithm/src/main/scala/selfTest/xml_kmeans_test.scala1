package selfTest

import org.apache.spark._
import org.apache.spark.sql._
import org.apache.log4j.{Level, Logger}
import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext

import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

object xml_kmeans_test {
    def main(args:Array[String]):Unit = {
        Logger.getLogger("org").setLevel(Level.ERROR)
        val conf = new SparkConf().setAppName("XML_Kmeans_Test").setMaster("local")
        val sc = new SparkContext(conf)
        val sqlcontext = new SQLContext(sc)

        val data = sc.parallelize(List((1,3),(1,2),(1,4),(2,3)),2)
        val result = data.aggregateByKey(100)(seqb,comb).collect()
        result.foreach(println)
        sc.stop()
    }

    def seqb(a:Int,b:Int):Int = {
        println("seq: "+a+"\t"+b)
        a+b
    }

    def comb(a:Int,b:Int):Int = {
        println("comb: "+a+"\t"+b)
        a+b
    }
}
