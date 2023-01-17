package com.dfssi.dataplatform.analysis.preprocess.initialize

import com.dfssi.dataplatform.analysis.common.AbstractProcess
import org.apache.spark.SparkContext

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/14 10:40 
  */
trait AbstractInitProcess extends AbstractProcess{
  def getSparkContext(defEl: Elem, sparkTaskDefEl: Elem): SparkContext

  def isOver:Boolean
}
