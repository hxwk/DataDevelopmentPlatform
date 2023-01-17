package com.dfssi.dataplatform.analysis.es.convertor

import org.apache.spark.Logging


/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/8 16:28 
  */
object ConvertorFactory extends Logging{

  def getConvertor(convertorId: String): TerminalDataConvertorTrait ={
    val convertor = convertorId match {
      case Terminal0705Convertor.ID => Terminal0705Convertor.getOrNewInstance()
      case Terminal0702Convertor.ID => Terminal0702Convertor.getOrNewInstance()
      case Terminal0200Convertor.ID => Terminal0200Convertor.getOrNewInstance()
      case TerminalNewConvertor.ID => TerminalNewConvertor.getOrNewInstance()
      case TerminalD004Convertor.ID => TerminalD004Convertor.getOrNewInstance()
      case id =>
          logWarning(s"未找到id为${id}的转换器, 使用默认转换器SimpleConvertor代替。")
          SimpleConvertor.getOrNewInstance()
    }
    convertor
  }

  def getConvertorByClass(convertorClass: String): TerminalDataConvertorTrait =
   Class.forName(convertorClass).newInstance().asInstanceOf[TerminalDataConvertorTrait]

}
