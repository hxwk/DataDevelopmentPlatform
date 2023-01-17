package com.dfssi.dataplatform.analysis.es.convertor

import com.dfssi.dataplatform.analysis.es.EsRecord

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 17:05 
  */
class SimpleConvertor extends TerminalDataConvertorTrait{

  override def convert(record: java.util.Map[String, Object] ,
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] ={
    Array(EsRecord(null, time, record))
  }
}

object SimpleConvertor{
  val ID = "0000"

  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new SimpleConvertor
      }
    }
    convertor
  }

}





