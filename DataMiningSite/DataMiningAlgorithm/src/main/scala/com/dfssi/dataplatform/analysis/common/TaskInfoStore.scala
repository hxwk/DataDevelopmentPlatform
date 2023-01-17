package com.dfssi.dataplatform.analysis.common

import java.sql.{Connection, DriverManager}

import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.hadoop.fs.Path
import org.apache.spark.{Logging, SparkContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.Elem

trait TaskInfoStore extends Logging with Serializable {
    def getMysqlConnection():Option[Connection] = {
        val url = "jdbc:mysql://172.16.1.241:3306/analysis"
        val user = "root"
        val password = "112233"
        Class.forName("com.mysql.jdbc.Driver").newInstance()
        try{
            var conn = DriverManager.getConnection(url, user, password)
            Some(conn)
        }catch{
            case e:Throwable => println("found a exception "+e)
                None
        }
    }

    def storeAlgorithmMetric(sql:String, conn:Connection):Unit = {
        try{
            val stmt = conn.createStatement()
            stmt.execute(sql)
        }catch{
            case e:Throwable => println("found a exception "+e)
        }finally {
            try{
                conn.close()
            }catch{
                case e:Throwable => println("found a exception "+e)
            }finally {
                println("Connection close success!")
            }
        }
    }

    def getTableName():String = {
        "result_metrics"
    }

}
