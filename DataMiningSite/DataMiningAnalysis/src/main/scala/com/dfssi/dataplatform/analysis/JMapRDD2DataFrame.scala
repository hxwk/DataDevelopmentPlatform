package com.dfssi.dataplatform.analysis

import com.dfssi.dataplatform.analysis.config.XmlReader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types._

import scala.xml.Elem

/**
  * Description:
  *   将RDD[ java.util.Map[String, Object] ]转成DateFrame
  * @author LiXiaoCong
  * @version 2018/3/28 16:52 
  */
class JMapRDD2DataFrame(columnEntitys: Array[ColumnEntity]) extends Serializable {

    val schema = StructType(columnEntitys.map(structField))

    def transform(hiveContext: HiveContext,
                  jMapRDD: RDD[java.util.Map[String, Object]]): DataFrame ={
        hiveContext.setConf("spark.sql.parquet.mergeSchema", "true")

        val rowRDD = jMapRDD.map(r =>{
            val seq =  columnEntitys.map(c =>{
                valueCheck(c.typeName, r.get(c.dataField))
            })
            Row.fromSeq(seq)
        })

        hiveContext.createDataFrame(rowRDD, schema)
    }


    private def structField(columnEntity: ColumnEntity): StructField ={

        val typeName = columnEntity.typeName.toLowerCase
        typeName match {
            case "string" =>
                StructField(columnEntity.name, StringType)
            case "long" =>
                StructField(columnEntity.name, LongType)
            case "double" =>
                StructField(columnEntity.name, DoubleType)
            case "float" =>
                StructField(columnEntity.name, FloatType)
            case "integer" =>
                StructField(columnEntity.name, IntegerType)
            case "int" =>
                StructField(columnEntity.name, IntegerType)
            case "boolean" =>
                StructField(columnEntity.name, BooleanType)
            case "date" =>
                StructField(columnEntity.name, DateType)
            case "timestamp" =>
                StructField(columnEntity.name, TimestampType)
            case "array[string]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(StringType))
            case "array[long]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(LongType))
            case "array[double]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(DoubleType))
            case "array[integer]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(IntegerType))
            case "array[int]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(IntegerType))
            case "array[float]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(FloatType))
            case "array[boolean]" =>
                StructField(columnEntity.name, DataTypes.createArrayType(BooleanType))
            case _ =>
                StructField(columnEntity.name, StringType)
        }
    }

    //字段对应的值类型校验
    private def valueCheck(typeName: String, value: Object): Any ={
        var res: Any = null
        if(value != null) {
            typeName.toLowerCase match {
                case "long" =>
                    if (!value.isInstanceOf[java.lang.Long]) {
                        res = value.toString.toLong
                    }
                case "double" =>
                    if (!value.isInstanceOf[java.lang.Double]) {
                        res = value.toString.toDouble
                    }
                case "integer" =>
                    if (!value.isInstanceOf[java.lang.Integer]) {
                        res = value.toString.toInt
                    }
                case "int" =>
                    if (!value.isInstanceOf[java.lang.Integer]) {
                        res = value.toString.toInt
                    }
                case "float" =>
                    if (!value.isInstanceOf[java.lang.Float]) {
                        res = value.toString.toFloat
                    }
                case "boolean" =>
                    if (!value.isInstanceOf[java.lang.Boolean]) {
                        res = value.toString.toBoolean
                    }
                case _ =>
                    res = value
            }
        }

        res
    }


}

object JMapRDD2DataFrame{

    def buildFromXmlElem(elem: Elem): JMapRDD2DataFrame ={

        val columnsEl = XmlReader.getNextSingleSubElem(elem, "columns")
        require(columnsEl != null, "不包含columns节点！")

        val columnEntitys = (columnsEl \ "column").map(node =>{
            val columnEl = node.asInstanceOf[Elem]

            val name = XmlReader.getAttr(columnEl, "name")
            require(name != null, s"${columnEl}中name不能为空")

            val typeName = XmlReader.getAttr(columnEl, "type")
            require(typeName != null, s"${columnEl}中type不能为空")

            val dataField = XmlReader.getAttrWithDefault(columnEl, "dataField", name)

            ColumnEntity(name, typeName, dataField)
        }).toArray

        new JMapRDD2DataFrame(columnEntitys)
    }
}

case class ColumnEntity(val name: String,
                        val typeName: String,
                        val dataField: String) extends Serializable
