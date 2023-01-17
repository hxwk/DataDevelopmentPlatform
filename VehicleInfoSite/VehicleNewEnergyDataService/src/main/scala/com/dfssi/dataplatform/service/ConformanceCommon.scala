package com.dfssi.dataplatform.service

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import scala.util.control.Breaks

/**
  * Description
  *
  * @author bin.Y
  * @version 2018/4/2 17:29
  */
object ConformanceCommon extends Serializable {

  def abs(n: Int): Int = if (n > 0) n else -n

  def dateSubtract(date1: String, date2: String): Int = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val b = sdf.parse(date2).getTime - sdf.parse(date1).getTime
    val num = b / 1000
    num.toInt
  }


  def nowDateStr(format: String = "yyyyMMddHHmmss"): String = {
    val now: Date = new Date()
    val dateFormat = new SimpleDateFormat(format)
    val dateStr = dateFormat.format(now)
    return dateStr
  }

  def loadProperties(propertiesFile: String) = {
    val properties = new Properties()
    val stream = Thread.currentThread().getContextClassLoader.getResourceAsStream(propertiesFile)
    if (stream == null) throw new Exception(propertiesFile + " doesn't exist!");
    properties.load(stream);
    properties;
  }

  /**
    * @author bin.Y
    *         Description:判断连续性
    *         Date:  2018/4/11 9:17
    */
  def judgeContinued(collTimeList: List[String], continuedTime: Int, maxMissNum: Int, maxIntervalTime: Int): Int = {
    var intervalList = getintervalList(collTimeList)
    var listEmpty: List[List[Int]] = List()
    var subList = this.subList(intervalList, listEmpty, maxIntervalTime)
    var result = 0
    for (list <- subList) {
      if (list.size >= continuedTime - maxMissNum) {
        result = 1
      }
    }
    result
  }

  /**
    * @author bin.Y
    *         Description:获取数据间隔
    *         Date:  2018/4/11 9:18
    */
  def getintervalList(list: List[String]): List[Int] = {
    var intervalList: List[Int] = List()
    for (i <- 0 to list.size - 2) {
      val interval = this.dateSubtract(list(i), list(i + 1))
      intervalList = intervalList :+ interval
    }
    intervalList
  }

  /**
    * @author bin.Y
    *         Description:递归将集合切片
    *         Date:  2018/4/10 19:03
    */
  def subList(intervalList: List[Int], returnList: List[List[Int]], maxIntervalTime: Int): List[List[Int]] = {
    var list: List[List[Int]] = List()
    if (intervalList.size == 0) {
      return returnList
    }
    val loop = new Breaks
    loop.breakable {
      for (i <- 0 to intervalList.size - 1) {
        if (intervalList(i) > maxIntervalTime) {
          var beforelist: List[Int] = List()
          for (j <- 0 to i) {
            beforelist = beforelist :+ intervalList(j)
            list = returnList :+ beforelist
          }
          var afterlist: List[Int] = List()
          for (j <- i + 1 to intervalList.size - 1) {
            afterlist = afterlist :+ intervalList(j)
          }
          return subList(afterlist, list, maxIntervalTime)
        }
      }
    }
    if (returnList.size == 0) {
      list :+ intervalList
    } else {
      returnList
    }
  }

  /**
    * @author bin.Y
    *         Description:递归将集合切片
    *         Date:  2018/4/11 13:41
    */
  def subListMany(intervalList: List[(Int, String, String)], returnList: List[List[(Int, String, String)]], maxIntervalTime: Int): List[List[(Int, String, String)]] = {
    var list: List[List[(Int, String, String)]] = List()
    if (intervalList.size == 0) {
      return returnList
    }
    val loop = new Breaks
    loop.breakable {
      for (i <- 0 to intervalList.size - 1) {
        if (intervalList(i)._1 > maxIntervalTime) {
          var beforelist: List[(Int, String, String)] = List()
          for (j <- 0 to i) {
            beforelist = beforelist :+ intervalList(j)
            list = returnList :+ beforelist
          }
          var afterlist: List[(Int, String, String)] = List()
          for (j <- i + 1 to intervalList.size - 1) {
            afterlist = afterlist :+ intervalList(j)
          }
          return subListMany(afterlist, list, maxIntervalTime)
        }
      }
    }
    if (returnList.size == 0) {
      list :+ intervalList
    } else {
      returnList
    }
  }
}
