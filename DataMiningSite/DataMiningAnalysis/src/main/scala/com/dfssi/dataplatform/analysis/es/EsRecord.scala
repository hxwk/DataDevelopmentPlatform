package com.dfssi.dataplatform.analysis.es

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/14 15:31 
  */
case class EsRecord(val subject: String,
                    val time: Long,
                    val record: java.util.Map[String, Object]) extends Serializable

