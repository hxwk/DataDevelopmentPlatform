package com.dfssi.dataplatform.analysis.es

import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat

import org.apache.spark.localMl.feature.{MaxAbsScaler, StandardScaler}
import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.types.{StructField, _}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Random
import org.apache.spark.ml._
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.hive.HiveContext


object ProcessTest extends Logging{

  def main (args: Array[String ]) {
    val sparkConf = new SparkConf()

    val sparkContext = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sparkContext)
    val hiveContext = new HiveContext(sparkContext)

    val schema1 = StructType(List(
      StructField("id", StringType, nullable = false),
      StructField("name", StringType, nullable = true),
      StructField("sex", StringType, nullable = true),
      StructField("age", IntegerType, nullable = true),
      StructField("high", DoubleType, nullable = true)
    ))

    val schema2 = StructType(List(
      StructField("vin", StringType, nullable = false),
      StructField("weight", DoubleType, nullable = true)
    ))

    val rdd1 = sparkContext.parallelize(Seq(
      Row("1", "zhao", "男", 20, 1.72),
      Row("2", "qian", "女", 21, 1.65),
      Row("3", "sun", "男", 22, 1.59),
      Row("4", "li", "男", 23, 1.78),
      Row("5", "wang", "女", 24, 1.64),
      Row("6", "zhang", "男", 25, 1.66),
      Row("7", "liu", "男", 26, 1.82),
      Row("8", "peng", "女", 27, 1.60),
      Row("9", "yu", "男", 28, 1.73),
      Row("10", "", null, 29, 0.0)
    ))

    val rdd2 = sparkContext.parallelize(Seq(
      Row("1", 66.5),
      Row("2", 55.5),
      Row("3", 60.4),
      Row("4", 70.2),
      Row("5", 50.2),
      Row("6", 58.3),
      Row("7", 80.2),
      Row("8", 44.3),
      Row("9", 64.4),
      Row("10", 48.8)
    ))

    val df1 = sqlContext.createDataFrame(rdd1, schema1)
    val df2 = sqlContext.createDataFrame(rdd2, schema2)
    val df3: DataFrame = hiveContext.sql("select * from " + "dev_analysis.titanic");

    var inputDF1 = df1
    var inputDF2 = df2
    var inputDF3 = df3

    var resultDF1: DataFrame = null
    var resultDF2: DataFrame = null
    var resultDF3: DataFrame = inputDF3



//    //数据转换
//    val colNames = "pclass;sex".split(";")
//    val fromlValues = "1,2,3;female,male".split(";")
//    val tolValues = "4,5,6;1,0".split(";")
//
//    val tuples: Array[(String, (String, String))] = colNames.zip(fromlValues.zip(tolValues))
//
//    for ((x, (y,z)) <- tuples) {
//      val dataType: DataType = getColType(inputDF3, x)
//      val map: Map[String, String] = y.split(",").zip(z.split(",")).toMap
//      val map1: Map[Any, Any] = for ((k,v) <- map) yield(typeConvert(k,dataType), typeConvert(v, dataType))
//      resultDF3 = resultDF3.na.replace(x, map1)
//    }
//    resultDF3.show(100)
//    resultDF3.write.mode("overwrite").saveAsTable("dev_analysis.titanic_test")

//    //过滤
//    val sql: String = "age > 25"
//    df1.filter(sql).show(10)

//    //分层抽样
//    val n = (df1.count() * 0.5).toInt
//    val array = df1.collect()
//    resultDF1 = sqlContext.createDataFrame(reservoirSampleAndCount(array.iterator, n).toList, schema1).orderBy("sex")
//    resultDF1.show(10)
//    val rows: Array[Row] = reservoirSampleAndCount(array.toIterator, 5)
//    resultDF2 = sqlContext.createDataFrame(rows.toList, schema1).orderBy("sex")
//    resultDF2.show(10)

//    //join,如果左右表join列名称不同，右表该列名称将重命名为左表名称
//    val colNames1 = ("id,name,sex,age" + "," + "vin,weight").split(",")
//    var colNames2 = new ArrayBuffer[String]()
//    colNames2 ++= colNames1
//    val term1 = Array("id")
//    val term2 = Array("vin")
//    val map = term1.zip(term2).toMap
//    for ((x, y) <- map) {
//      if (colNames2.contains(y)) {
//        colNames2 -= y
//        colNames2 += x
//      }
//    }
//    colNames2 = colNames2.distinct
//    for (i <- 0 to term1.length-1) {
//      df2 = df2.withColumnRenamed(term2(i), term1(i))
//    }
//    resultDF1 = df1.join(df2, Seq("id")).selectExpr(colNames2: _*)
//    resultDF1.show(10)

//        //归一化,待解决
//        val scaler = new MaxAbsScaler().setInputCol("high").setOutputCol("normalized_high")
//        val scalerModel = scaler.fit(df1)
//        scalerModel.transform(df1).show(10)


    //缺失值填充
    var colNames = "age;fare".split(";")
    var fromValues = " ;".split(";")
    var toValues = "0;0.0".split(";")
    var ss = ";".split(";")

    var resultDF: DataFrame = df3
    resultDF.show(20)
    val tuples: Array[(String, (String, String))] = colNames.zip(fromValues.zip(toValues))
    for ((x, (y, z)) <- tuples) {
      val dataType = getColType(df3, x)
      if (y.equalsIgnoreCase("null")) {
        z match {
          case "max" => resultDF = resultDF.na
            .fill(Map(x -> resultDF.agg(x -> "max").first().apply(0)))
          case "min" => resultDF = resultDF.na
            .fill(Map(x -> resultDF.agg(x -> "min").first().apply(0)))
          case "avg" => resultDF = resultDF.na
            .fill(Map(x -> resultDF.agg(x -> "avg").first().apply(0)))
          case _ => resultDF = resultDF.na
            .fill(Map(x -> typeConvert(z, dataType)))
        }
      }else {
        var value = typeConvert("", dataType)
        if (y.trim() != "") {
          value = typeConvert(y, dataType)
        }
        z match {
          case "max" => resultDF = resultDF.na
            .replace(x, Map(value -> resultDF.agg(x -> "max").first().apply(0)))
          case "min" => resultDF = resultDF.na
            .replace(x, Map(value -> resultDF.agg(x -> "min").first().apply(0)))
          case "avg" => resultDF = resultDF.na
            .replace(x, Map(value -> resultDF.agg(x -> "avg").first().apply(0)))
          case _ => resultDF = resultDF.na
            .replace(x, Map(value -> typeConvert(z, dataType)))
        }
      }
    }

    resultDF.write.mode("overwrite").saveAsTable("dev_analysis.titanic_test")
    resultDF.show(100)

//    //拆分
//    val frames: Array[DataFrame] = df1.randomSplit(Array(0.4, 0.6))
//    for (df <- frames) {
//      df.show(10)
//    }
//
//    //sql
//    df1.registerTempTable("df1")
//    sqlContext.sql("SELECT * FROM df1 WHERE df1.age > 25").show(10)

//        //标准化,待解决
//        val scaler1 = new StandardScaler().setInputCol("high").setOutputCol("stdized_high")
//        val scalerModel1 = scaler1.fit(df1)
//        scalerModel1.transform(df1).show(10)

//    //类型转换
//    val colNames = "age;fare".split(";")
//    val dataTypes = "int;double".split(";")
//    val defalutValues = "0;0.0".split(";")
//
//    val map = colNames.zip(dataTypes.zip(defalutValues))
//
//    var resultDF: DataFrame = df3
//    for ((x, (y, z)) <- map) {
//      if ("timestamp" == y) {
//        resultDF = resultDF.withColumn(x, unix_timestamp(resultDF.col(x), "yyyy/MM/dd HH:mm:ss")
//          .cast("timestamp").alias(x))
//      }else{
//        resultDF = resultDF.withColumn(x, resultDF.col(x)
//          .cast(y).alias(x))
//      }
//
//      val toValue = typeConvert(z, y)
//      resultDF = resultDF.na.fill(Map(x -> toValue))
//    }
//
//    resultDF.show(10)
  }


  //返回一个长度为n，元素类型为int，范围为[1,m]且不重复的List
  def RandomListWithDistinct(n: Int, m: Int): List[Int] = {
    var outList: List[Int] = Nil
    var index = 0
    var flag = true
    for (i <- 0 to n-1 ) {
      flag = true
      while (flag) {
        index = (new Random).nextInt(m)
        if (!outList.contains(index)) {
          outList :+ index
          flag = false
        }
      }
    }
    return outList
  }

  //按比例抽样
  //蓄水池抽样
  def reservoirSampleAndCount[T: ClassTag](
                                            input: Iterator[T],
                                            k: Int)
  : Array[T] = {
    val reservoir = new Array[T](k)
    var i = 0
    while (i < k && input.hasNext) {
      val item = input.next()
      reservoir(i) = item
      i += 1
    }

    if (i < k) {
      val trimReservoir = new Array[T](i)
      System.arraycopy(reservoir, 0, trimReservoir, 0, i)
      trimReservoir
    } else {
      var l = i.toLong
      val rand = Random.nextDouble()
      while (input.hasNext) {
        val item = input.next()
        val replacementIndex = (rand * l).toLong
        if (replacementIndex < k) {
          reservoir(replacementIndex.toInt) = item
        }
        l += 1
      }
      reservoir
    }
  }

  //类型转换
  def typeConvert(value: Any, dataType: DataType): Any = {
    dataType match {
      case StringType =>  convertToString(value)
      case BooleanType => convertToBoolean(value)
      case ByteType => convertToByte(value)
      case ShortType => convertToShort(value)
      case IntegerType => convertToInt(value)
      case LongType => convertToLong(value)
      case FloatType => convertToFloat(value)
      case DoubleType => convertToDouble(value)
//      case DataTypes => convertToDecimal(value)
//      case DateType => convertToDate(value) //java.sql.Date
//      case TimestampType => convertToTimestamp(value) //java.sql.Timestamp
      case _ => return null
    }
  }
  def typeConvert(value: Any, dataType: String): Any = {
    dataType match {
      case "string" =>  convertToString(value)
      case "boolean" => convertToBoolean(value)
      case "byte" => convertToByte(value)
      case "short" => convertToShort(value)
      case "int" => convertToInt(value)
      case "long" => convertToLong(value)
      case "float" => convertToFloat(value)
      case "double" => convertToDouble(value)
      //      case "data" => convertToDecimal(value)
      //      case "date" => convertToDate(value) //java.sql.Date
      //      case "timestamp" => convertToTimestamp(value) //java.sql.Timestamp
      case _ => null
    }
  }
  def convertToString(v: Any): String = v match {
    case v: String => v
    case v: Boolean => v.toString
    case v: Byte => v.toString
    case v: Short => v.toString
    case v: Int => v.toString
    case v: Long => v.toString
    case v: Float => v.toString
    case v: Double => v.toString
    case v: Decimal => v.toString
    case v: Date => v.toString
    case v: Timestamp => v.toString
    case v: Any => v.toString
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToBoolean(v: Any): Boolean = v match {
    case v: String => v.toBoolean
    case v: Boolean => v
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToByte(v: Any): Byte = v match {
    case v: String => v.toByte
    case v: Byte => v
    case v: Short => v.toByte
    case v: Int => v.toByte
    case v: Long => v.toByte
    case v: Float => v.toByte
    case v: Double => v.toByte
    case v: Decimal => v.toByte
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToShort(v: Any): Short = v match {
    case v: String => v.toShort
    case v: Byte => v.toShort
    case v: Short => v.toShort
    case v: Int => v.toShort
    case v: Long => v.toShort
    case v: Float => v.toShort
    case v: Double => v.toShort
    case v: Decimal => v.toShort
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToInt(v: Any): Int = v match {
    case v: String => v.toInt
    case v: Byte => v.toInt
    case v: Short => v.toInt
    case v: Int => v.toInt
    case v: Long => v.toInt
    case v: Float => v.toInt
    case v: Double => v.toInt
    case v: Decimal => v.toInt
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToLong(v: Any): Long = v match {
    case v: String => v.toLong
    case v: Byte => v.toLong
    case v: Short => v.toLong
    case v: Int => v.toLong
    case v: Long => v.toLong
    case v: Float => v.toLong
    case v: Double => v.toLong
    case v: Decimal => v.toLong
    case v: Timestamp => v.toString.toLong
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToFloat(v: Any): Float = v match {
    case v: String => v.toFloat
    case v: Byte => v.toFloat
    case v: Short => v.toFloat
    case v: Int => v.toFloat
    case v: Long => v.toFloat
    case v: Float => v.toFloat
    case v: Double => v.toFloat
    case v: Decimal => v.toFloat
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
  def convertToDouble(v: Any): Double = v match {
    case v: String => v.toDouble
    case v: Byte => v.toDouble
    case v: Short => v.toDouble
    case v: Int => v.toDouble
    case v: Long => v.toDouble
    case v: Float => v.toDouble
    case v: Double => v.toDouble
    case v: Decimal => v.toDouble
    case v => throw new IllegalArgumentException(
      s"Unsupported value type ${v.getClass.getName} ($v).")
  }
//  def convertToDecimal(v: Any): Decimal = v match {
//    case v: String => v.asInstanceOf[Decimal]
//    case v: Byte => v.toD
//    case v: Short => v.toString
//    case v: Int => v.toString
//    case v: Long => v.toString
//    case v: Float => v.toString
//    case v: Double => v.toString
//    case v: Decimal => v.toString
//    case v: Date => v.toString
//    case v: Timestamp => v.toString
//    case v => throw new IllegalArgumentException(
//      s"Unsupported value type ${v.getClass.getName} ($v).")
//  }
//  def convertToDate(v: Any): Date = v match {
//    case v: String => new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v)
//    case v: Boolean => v.toString
//    case v: Byte => v.toString
//    case v: Short => v.toString
//    case v: Int => v.toString
//    case v: Long => v.toString
//    case v: Float => v.toString
//    case v: Double => v.toString
//    case v: Decimal => v.toString
//    case v: Date => v.toString
//    case v: Timestamp => v.toString
//    case v => throw new IllegalArgumentException(
//      s"Unsupported value type ${v.getClass.getName} ($v).")
//  }
//  def convertToTimestamp(v: Any): Timestamp = v match {
//    case v: String => v
//    case v: Boolean => v.toString
//    case v: Byte => v.toString
//    case v: Short => v.toString
//    case v: Int => v.toString
//    case v: Long => v.toString
//    case v: Float => v.toString
//    case v: Double => v.toString
//    case v: Decimal => v.toString
//    case v: Date => v.toString
//    case v: Timestamp => v.toString
//    case v => throw new IllegalArgumentException(
//      s"Unsupported value type ${v.getClass.getName} ($v).")
//  }
  def getColType(df: DataFrame, col: String): DataType = {
    df.schema.fields.map(x =>
      if(x.name.equalsIgnoreCase(col)){
        return x.dataType
      }
    )
    return null
  }
  def getValue(df: DataFrame, name: String): Any = {
    df.first().toSeq(0)
  }
}

