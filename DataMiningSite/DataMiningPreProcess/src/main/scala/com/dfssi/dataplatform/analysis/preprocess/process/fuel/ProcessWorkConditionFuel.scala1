package com.dfssi.dataplatform.analysis.preprocess.process.fuel

import java.text.SimpleDateFormat
import java.util.Date

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.fuel.FuelConfig
import com.dfssi.dataplatform.analysis.fuel.stats.ConditionFuelStatisticians
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory

import scala.collection.{JavaConversions, mutable}
import scala.xml.Elem

/**
  * Description:
  *     实时油耗
  * @author LiXiaoCong
  * @version 2018/3/1 19:05 
  */
class ProcessWorkConditionFuel extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    //读取数据处理相关的参数
    val processMap: mutable.Map[String, String] = extractSimpleParams(defEl)

    var day = processMap.getOrElse("daytime", null)
    val format = new SimpleDateFormat("yyyyMMdd")
    //测试日期格式
    if(day != null){
      format.parse(day)
    }else{
      day = format.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L))
      logWarning(s"参数daytime为空，取系统时间的前一天：${day}")
    }

    val partition = processMap.getOrElse("partition", "8").toInt

    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		daytime     ：  $day ")
    logInfo(s" 	  partitions  ：  $partition ")

    val config = JavaConversions.mapAsJavaMap(processMap.filter(_._1.startsWith("fuel.")))

    val fuelConf = new FuelConfig(config, false)
    new ConditionFuelStatisticians(fuelConf).start(day, partition)
  }
}

object ProcessWorkConditionFuel {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_WORKCONDITIONFUEL
}




