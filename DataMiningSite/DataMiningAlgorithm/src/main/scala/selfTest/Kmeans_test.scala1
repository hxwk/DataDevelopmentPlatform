package selfTest

import org.apache.spark.localMl.clustering.{KMeans, KMeansModel}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.localMl.feature.MinMaxScaler

object Kmeans_test {
    def main(args:Array[String]):Unit = {
        val conf = new SparkConf().setAppName("KmeansDemo").setMaster("local")
        val sc = new SparkContext(conf)
        val sqlcontext = new SQLContext(sc)

        //从本地加载UCI数据集csv文件
        val data = sc.textFile("hdfs://172.16.1.210//user/chenz/Kmeans_data.csv")
        val headerString = data.first()

        val fields = headerString.split(",")
                .map(fieldName=>
                    StructField(fieldName,DoubleType,nullable = true))

        val schema = StructType(fields)

        //将RDD转换成DataFrame
        val dataRows = data.filter(!_.contains(headerString.split(",").head))
                .map{ line=>
                    val arr = line.split(",").map(_.toDouble)
                    arr
                }.map(Row.fromSeq(_))


        val dataDF = sqlcontext.createDataFrame(dataRows,schema)

        //check dataTypes
        //        dataDF.dtypes.foreach(x=>println(x._1+" dataType is "+x._2))

        val assembler = new VectorAssembler()
                .setInputCols(headerString.split(","))
                .setOutputCol("rawFeatures")
        val rawTrainDF = assembler.transform(dataDF)
        rawTrainDF.show()

        //特征归一化
        val scaler = new MinMaxScaler()
                .setInputCol("rawFeatures")
                .setOutputCol("features")

        val scalerModel = scaler.fit(rawTrainDF)
        val trainDF = scalerModel.transform(rawTrainDF)

        import scala.collection.mutable.ArrayBuffer
        val costModelArray = ArrayBuffer[(Double,KMeansModel)]()

        //训练模型
        val kmeans = new KMeans()
                .setK(3)
                .setFeaturesCol("features")
        kmeans.setMaxIter(27)
        val model = kmeans.fit(trainDF)

        //make predictions
        val predictions = model.transform(trainDF)

        predictions.show()
        println(model.explainParams())
    }
}
