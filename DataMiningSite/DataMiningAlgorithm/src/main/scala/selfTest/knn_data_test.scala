package selfTest

import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.localMl.classification.{KNNClassificationModel,KNNClassifier}
import org.apache.spark.localMllib.util.MLUtils
//import com.dfssi.dataplatform.analysis.common.ProcessContext
import org.apache.log4j.{Level,Logger}
import org.apache.spark.localMl.feature.{LabeledPoint=>newLabelPoint}
import org.apache.spark.localMllib.regression.{LabeledPoint=>oldLabelPoint}

object knn_data_test {
    def main(args:Array[String]):Unit ={
        Logger.getLogger("org").setLevel(Level.ERROR)
        val conf = new SparkConf().setAppName("KNN_data_test").setMaster("local[*]")
        val sc = new SparkContext(conf)
        val sqlcontext= new SQLContext(sc)
        import sqlcontext.implicits._


        val train = MLUtils.loadLibSVMFile(sc,
            "hdfs://172.16.1.210//user/chenz/mnist").map{
            case oldLabelPoint(label,features)=>newLabelPoint(label,features)
        }.toDF()

        println(train.schema)
        println("topTreeSize:  ",train.count().toInt)

        val knn = new KNNClassifier()
                .setTopTreeSize(train.count().toInt/500)
                .setK(10)
        val knnModel = knn.fit(train)
        val predicted = knnModel.transform(train)
        val incorrCount = predicted.where($"label"!==$"prediction").count()
        println("Not correct rate---> "+incorrCount)
        predicted.where($"label"!==$"prediction").show(100)

        sc.stop()

    }
}

