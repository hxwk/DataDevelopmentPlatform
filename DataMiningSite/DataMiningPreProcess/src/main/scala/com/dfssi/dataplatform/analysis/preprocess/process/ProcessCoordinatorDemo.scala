package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/6 10:19 
  */
class ProcessCoordinatorDemo extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem) {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val inputIds: Array[String] =  getInputs(defEl);
    val inputDF = processContext.dataFrameMap.get(inputIds(0));
    if (inputDF.isEmpty) {
      return
    }

    logError(s"${inputDF.get.collect()}")
    inputDF.get.show()

  }
}

object ProcessCoordinatorDemo{
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_COORDINATORDEMO
}