package com.dfssi.dataplatform.analysis.es.convertor

import com.dfssi.dataplatform.analysis.es.EsRecord
import com.dfssi.dataplatform.analysis.es.indexs.TypeEntity
import org.apache.spark.Logging

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 17:10 
  */
trait TerminalDataConvertorTrait extends Logging{

  protected var typeEntity: TypeEntity = null

  def setTypeEntity(typeEntity: TypeEntity): Unit ={
    if(this.typeEntity == null)
      this.typeEntity = typeEntity
  }

  def convert(record: java.util.Map[String, Object],
              idField: String,
              identificationFiled: String,
              time: Long): Array[EsRecord]

  def preConvert(record: java.util.Map[String, Object],
                 idField: String,
                 identificationFiled: String): java.util.Map[String, Object] = record
}
