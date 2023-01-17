package com.dfssi.dataplatform.analysis.es.convertor

import java.util
import java.util.UUID

import com.dfssi.dataplatform.analysis.es.EsRecord
import com.dfssi.resources.ConfigUtils

import scala.collection.mutable.ArrayBuffer

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 17:05 
  */
class Terminal0705Convertor extends TerminalDataConvertorTrait{

  override def convert(record: java.util.Map[String, Object],
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] ={

    var records: ArrayBuffer[EsRecord] = new ArrayBuffer[EsRecord]()

    val beanlist = record.remove("messageBeanList")
    record.remove("itemNum")
    record.remove(idField)

    //解析数据
    if(beanlist != null){
      val beans = beanlist.asInstanceOf[util.List[util.Map[String, Object]]]
      val n = beans.size()
      for(i <- 0 until n){
        val map = beans.get(i)

        val id = ConfigUtils.getAsStringWithDefault(map, idField, UUID.randomUUID().toString)
        map.put(idField, id)

        map.putAll(record)
        records += EsRecord(null, time, map)
      }
    }
    records.toArray
  }
}

object Terminal0705Convertor{
  val ID = "0705"

  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new Terminal0705Convertor
      }
    }
    convertor
  }
}
