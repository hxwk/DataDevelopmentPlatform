package com.dfssi.dataplatform.analysis.es.convertor

import com.dfssi.dataplatform.analysis.es.EsRecord

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 17:05 
  */
class Terminal0702Convertor extends TerminalDataConvertorTrait{

  override def convert(record: java.util.Map[String, Object],
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] ={
    Array(EsRecord(null, time, record))
  }

}

object Terminal0702Convertor{
  val ID = "0702"
  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new Terminal0702Convertor
      }
    }
    convertor
  }

}
