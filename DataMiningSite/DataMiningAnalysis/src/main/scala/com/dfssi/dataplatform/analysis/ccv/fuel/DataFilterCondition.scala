package com.dfssi.dataplatform.analysis.ccv.fuel

import com.dfssi.dataplatform.analysis.config.XmlReader
import org.apache.spark.Logging

import scala.xml.Elem

/**
  * Description:
  *   数据过滤规则
  * @author LiXiaoCong
  * @version 2018/5/23 11:07 
  */
class DataFilterCondition(val field: String,
                          val filterType: String,
                          val nullAble: Boolean,
                          val lowerBound: Double,
                          val upperBound: Double,
                          val enums: List[String] = null) extends Serializable with Logging{


    def isAccept(record: java.util.Map[String, Object]): Boolean ={
        if(record != null){
            return isAcceptValue(record.get(field))
        }
        true
    }

    def isAcceptValue(value: Object): Boolean ={

        if(value == null){
            return nullAble
        }

        try {
            filterType match {
                case "range" =>
                    val r = value.toString.toDouble
                    (r >= lowerBound && r <= upperBound)
                case "enum" =>
                    (enums == null || enums.isEmpty) || enums.contains(value.toString)
            }
        } catch {
            case _:Throwable => false
        }
    }


}

object DataFilterCondition extends Logging {

    // <filter field="gpsTime" type="range" min="1483200000000" max="" nullable="true"/>
    def buildFromXmlElem(filter: Elem): DataFilterCondition ={
        if(filter != null){
            val field = XmlReader.getAttr(filter, "field")
            require(field != null, s"${filter}缺失field的配置。 ")

            val filterType = XmlReader.getAttr(filter, "type")
            require(field != null, s"${filter}缺失type的配置。 ")

            val nullable = XmlReader.getAttrAsBoolean(filter, "nullable", true)

            filterType.toLowerCase match {
                case "range" =>
                    val lowerBound = XmlReader.getAttrAsDouble(filter, "min", Double.MinValue)
                    val upperBound = XmlReader.getAttrAsDouble(filter, "max", Double.MaxValue)
                    return new DataFilterCondition(field, filterType, nullable, lowerBound, upperBound)
                case "enum" =>
                    val vals = XmlReader.getAttr(filter, "vals")
                    var enums: List[String] = null
                    if(vals != null && vals.trim.length > 0){
                        enums = vals.split(",").toList
                    }
                    return new DataFilterCondition(field, filterType, nullable, 0.0, 0.0, enums)
                case _: String =>
                    logError(s"不识别${filter}缺失type类型，取值示例：range、enum")
            }
        }
        null
    }
}
