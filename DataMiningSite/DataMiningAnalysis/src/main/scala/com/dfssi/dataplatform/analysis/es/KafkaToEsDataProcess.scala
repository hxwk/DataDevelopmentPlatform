package com.dfssi.dataplatform.analysis.es

import com.dfssi.common.SysEnvs
import com.dfssi.dataplatform.analysis.es.convertor.{ConvertorFactory, SimpleConvertor, TerminalDataConvertorTrait}
import com.dfssi.dataplatform.analysis.es.indexs.{IndexEntity, TypeEntity}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/8 15:04 
  */
class KafkaToEsDataProcess(val typeEntitys: Array[TypeEntity]) extends Serializable with Logging{

  def executeProcess(rdd: RDD[java.util.Map[String, Object]],
                     idField: String): RDD[EsRecord] ={

    val typesBC = rdd.sparkContext.broadcast(typeEntitys)

    rdd.flatMap(record =>{
      var types = typesBC.value
      types = types.filter(entity => {
        val identificationValue = record.get(entity.typeHead.identificationFiled)
        if(identificationValue == null){
          false
        }else{
          entity.typeHead.identificationValues.contains(identificationValue.toString)
        }
      })

      var records: Array[EsRecord] = null

      if(types.length > 0){
        val entity = types(0)
        val convertorTrait = getConvertor(entity)
          convertorTrait.setTypeEntity(entity)
        val realRecord = convertorTrait.preConvert(record, idField, entity.typeHead.identificationFiled)

        var time: Long = 0L
        val timeObj = realRecord.get(entity.typeHead.timeField)
        if(timeObj == null)
          time = System.currentTimeMillis()
        else
          time =  entity.typeHead.formatTimeToLong(timeObj.toString)

        if(time > entity.typeHead.timeMin && time < entity.typeHead.timeMax) {
          //realRecord.put(Constants.TABLE_FIELD, entity.name)
          val index = entity.getIndexEntity().getFullIndexName(time)
          //realRecord.put(Constants.INDEX_FIELD, index)

          records = convertorTrait.convert(realRecord, idField, entity.typeHead.identificationFiled, time)
          records.foreach(r => {
            fieldValueCheck(entity, r)
            //检查是否已经添加了index 和 type
            if(!r.record.containsKey(Constants.TABLE_FIELD)){
              r.record.put(Constants.TABLE_FIELD, entity.name)
            }
            if(!r.record.containsKey(Constants.INDEX_FIELD)){
              r.record.put(Constants.INDEX_FIELD, index)
            }
          })
        }else{
          records = Array[EsRecord]()
        }
      }else{
        logError(s"已知记录未找到注册的type：\n\t ${record}")
        records = Array[EsRecord]()
      }
      records
    })
  }

  def getTypeEntityMap: Map[String, TypeEntity] = typeEntitys.map(v => (v.name, v)).toMap

  private def getConvertor(typeEntity: TypeEntity): TerminalDataConvertorTrait ={
    var convertorTrait: TerminalDataConvertorTrait = null
    if(typeEntity.typeHead.convertorID != null){
      convertorTrait = ConvertorFactory.getConvertor(typeEntity.typeHead.convertorID)
    }else if(typeEntity.typeHead.convertorClass != null){
      convertorTrait = ConvertorFactory.getConvertorByClass(typeEntity.typeHead.convertorClass)
    }else{
      convertorTrait = SimpleConvertor.getOrNewInstance()
    }
    convertorTrait
  }

  //字段对应的值类型校验
  private def fieldValueCheck(typeEntity: TypeEntity,
                              record: EsRecord): Unit ={

    typeEntity.getColumns().foreach(column =>{

      val value = record.record.get(column)
      if(value != null) {
        val columnType = typeEntity.getColumnType(column).toLowerCase
        try {
          columnType match {
            case "long" =>
              if (!value.isInstanceOf[java.lang.Long]) {
                record.record.put(column, value.toString.toLong.asInstanceOf[java.lang.Object])
              }
            case "double" =>
              if (!value.isInstanceOf[java.lang.Double]) {
                record.record.put(column, value.toString.toDouble.asInstanceOf[java.lang.Object])
              }
            case "integer" =>
              if (!value.isInstanceOf[java.lang.Integer]) {
                record.record.put(column, value.toString.toInt.asInstanceOf[java.lang.Object])
              }
            case "float" =>
              if (!value.isInstanceOf[java.lang.Float]) {
                record.record.put(column, value.toString.toFloat.asInstanceOf[java.lang.Object])
              }
            case "text" =>
              if (!value.isInstanceOf[java.lang.String]
                && !value.getClass.isArray) {
                record.record.put(column, value.toString.asInstanceOf[java.lang.Object])
              }
            case "string" =>
              if (!value.isInstanceOf[java.lang.String]
                && !value.getClass.isArray && !SysEnvs.isCollection(value)) {
                record.record.put(column, value.toString.asInstanceOf[java.lang.Object])
              }
            case "keyword" =>
              if (!value.isInstanceOf[java.lang.String]
                      && !value.getClass.isArray && !SysEnvs.isCollection(value)) {
                record.record.put(column, value.toString.asInstanceOf[java.lang.Object])
              }
            case _ =>
              ""
          }
        } catch {
          case e =>
            logError(s"类型转换失败：${column}::${columnType} ,\n\t ${record.record}", e)
            record.record.remove(column)
        }
      }else{
        record.record.remove(column)
      }

    })

  }


}

object KafkaToEsDataProcess extends Logging {

  def buildFromXmlElem(processElem: Elem): KafkaToEsDataProcess ={
    val typeEntitys = (processElem \ "es-index").flatMap(node =>{
      val indexElem = node.asInstanceOf[Elem]
      val indexEntity = IndexEntity.buildFromXmlElem(indexElem)
      indexEntity.getTypes().map(_._2)
    }).toArray

    logInfo("es的表配置如下：")
    typeEntitys.foreach(t => logInfo(t.toString))
    new KafkaToEsDataProcess(typeEntitys)
  }


}
