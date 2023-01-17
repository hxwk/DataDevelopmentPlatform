package com.dfssi.dataplatform.analysis.dbha.driverbehavior.turnlight

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils.{CollectedDvrItem, collectValidDvrData}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag


case class TurnLightEvent(flag: Int, startItem: CollectedDvrItem, endItem: CollectedDvrItem)

case class TurnAngleEvent(flag: Int, startItem: CollectedDvrItem, endItem: CollectedDvrItem)

// 转向相关事件计数器
class ImproperTurnEventCounter {

  private[this] var _turnWithoutTurnLightCount: Int = 0
  private[this] var _turnWithIncorrectTurnLightCount = 0
  private[this] var _straightForwardWithTurnLightCount = 0
  private[this] var _changeLaneFrequentlyCount = 0  // TODO 暂未实现

  def incrementTurnWithoutTurnLight(): ImproperTurnEventCounter = {
    _turnWithoutTurnLightCount += 1
    this
  }

  def turnWithoutTurnLightCount: Int = _turnWithoutTurnLightCount

  def incrementTurnWithIncorrectTurnLight(): ImproperTurnEventCounter = {
    _turnWithIncorrectTurnLightCount += 1
    this
  }

  def turnWithIncorrectTurnLightCount: Int = _turnWithIncorrectTurnLightCount

  def incrementStraightForwardWithTurnLight(): ImproperTurnEventCounter = {
    _straightForwardWithTurnLightCount += 1
    this
  }

  def straightForwardWithTurnLightCount: Int = _straightForwardWithTurnLightCount

  def incrementChangeLaneFrequently(): ImproperTurnEventCounter = {
    _changeLaneFrequentlyCount += 1
    this
  }

  def changeLaneFrequentlyCount: Int = _changeLaneFrequentlyCount

}

/**
  * 转弯不打转向灯
  *
  */
class ImproperTurnLightBehavior(paramsMap: Map[String, String]) extends Serializable {

  // get params
  private val minTurnDir: Float = paramsMap.getOrElse("minRightTurnDir", "13").toFloat
  private val minStaightForwardWithTurnLightDurationMillis: Float = paramsMap.getOrElse("minStaightForwardWithTurnLightDuration", "5").toFloat * 1000
  private val windowSize: Int = paramsMap.getOrElse("slideWindowSize", "3").toInt
  private val maxEventDurationMillis: Int = 5 * 1000 // 5秒

  // init vars
  private val turnLightEventStack = new mutable.Stack[TurnLightEvent]()
  private val window = new SlideWindow[CollectedDvrItem](windowSize)

  private var turnLightFlag: Int = -1 // 0->右转向灯 1->左转向灯 2->双闪
  private var turnDirFlag: Int = -1   // 0->右转向灯 1->左转向灯

  private var singleTurnLightStartItem: CollectedDvrItem = _  //信号灯指示转弯起点
  private var turnDirStartItem: CollectedDvrItem = _          //角度变化指示转弯起点
  private var previousItem: CollectedDvrItem = _

  val improperTurnEventCounter = new ImproperTurnEventCounter()

  // methods
  private def resetTurnLight(turnLightFlag: Int = -1, singleTurnLightStartItem: CollectedDvrItem = null): Unit = {
    this.turnLightFlag = turnLightFlag
    this.singleTurnLightStartItem = singleTurnLightStartItem
  }

  private def resetTurnDir(turnDirFlag: Int = -1, turnDirStartItem: CollectedDvrItem = null): Unit = {
    this.turnDirFlag = turnDirFlag
    this.turnDirStartItem = turnDirStartItem
  }

  private def checkTurnLight(item: CollectedDvrItem): Unit = {

    val turnLightSignals = item.turnLightSignals

    if( turnLightSignals.contains("左转向灯信号") && turnLightSignals.contains("右转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(2, item)
      } else if(2 != turnLightFlag || item.timestamp - previousItem.timestamp >= maxEventDurationMillis) {
//        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(2, item)
      }
    }
    else if(turnLightSignals.contains("左转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(1, item)
      } else if(1 != turnLightFlag || item.timestamp - previousItem.timestamp >= maxEventDurationMillis) {
        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(1, item)
      }
    }
    else if(turnLightSignals.contains("右转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(0, item)
      } else if(0 != turnLightFlag || item.timestamp - previousItem.timestamp >= maxEventDurationMillis) {
        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(0, item)
      }
    }
    else if(-1 != turnLightFlag || item.timestamp - previousItem.timestamp >= maxEventDurationMillis){
      if(2 != turnLightFlag) turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
      resetTurnLight()
    }
  }

  private def checkTurnDir(item: CollectedDvrItem, previousTimestamp: Long): Unit = {

    val currentDir = item.dir
    val currentSpeed = item.speed


    val headDirAngle = window.first().dir

    // 这里只做转弯不打转向灯判断
    if(item.timestamp - window.first().timestamp <= maxEventDurationMillis) {

      val dirDiff = currentDir - headDirAngle

      // 如果滑动窗口中起始点的角度构成转弯，则判断这段时间是否存在转向灯信号
      if(isTurnRight(dirDiff, headDirAngle, currentDir, minTurnDir)) {
        if( -1 == turnDirFlag) {
          resetTurnDir(0, window.first())
        }
        else if(0 != turnDirFlag) {
          println(s"构成转弯从$turnDirStartItem 到 ${window.first()}")
          if(-1 == turnLightFlag || 2 == turnLightFlag) {
            println("转弯不打转向灯 或者打双闪")
            improperTurnEventCounter.incrementTurnWithoutTurnLight()
          }
          resetTurnDir(0, window.first())
        }
      } else if(isTurnLeft(dirDiff, headDirAngle, currentDir, minTurnDir)) {
        if( -1 == turnDirFlag) {
          resetTurnDir(1, window.first())
        }
        else if(1 != turnDirFlag) {
          println(s"构成转弯从$turnDirStartItem 到 ${window.first()}")
          if(-1 == turnLightFlag || 2 == turnLightFlag) {
            println("转弯不打转向灯 或者打双闪")
            improperTurnEventCounter.incrementTurnWithoutTurnLight()
          }
          resetTurnDir(1, window.first())
        }
      }
      else if(-1 != turnDirFlag) {
        println(s"构成转弯从$turnDirStartItem 到 ${window.first()}")
        if(-1 == turnLightFlag || 2 == turnLightFlag) {
          println("转弯不打转向灯 或者打双闪")
          improperTurnEventCounter.incrementTurnWithoutTurnLight()
        }
        resetTurnDir()
      }
    }
    else if(-1 != turnDirFlag) {
//      println(s"构成转弯从$turnDirStartItem 到 $previousItem")
      if(-1 == turnLightFlag || 2 == turnLightFlag) {
        println("转弯不打转向灯 或者打双闪")
        improperTurnEventCounter.incrementTurnWithoutTurnLight()
      }
      resetTurnDir()
    }
  }


  def parseSingleRecord(item: CollectedDvrItem, previousTimestamp: Long): Unit = {
    // 添加当前item必须在角度检查之后
    window.add(item)

    // 必须先进行角度变化检查
    if(previousItem != null) {
      checkTurnDir(item, previousTimestamp)
    }

    // 然后做转向灯信号检查
    checkTurnLight(item)

    previousItem = item
  }

  def analysisTurnLightEvents(): Unit = {
    while (turnLightEventStack.nonEmpty) {
      val event = turnLightEventStack.pop()
      val headDir = event.startItem.dir
      val lastDir = event.endItem.dir
      val dirDiff = lastDir - headDir

      if(isTurnRight(dirDiff, headDir, lastDir, minTurnDir)) {
        if(0 != event.flag && 2 != event.flag) {
          println(s"右转弯错打左转向灯，从${event.startItem}到${event.endItem}")
          improperTurnEventCounter.incrementTurnWithIncorrectTurnLight()
        }
      } else if(isTurnLeft(dirDiff, headDir, lastDir, minTurnDir)) {
        if(1 != event.flag && 2 != event.flag) {
          println(s"左转弯错打右转向灯，从${event.startItem}到${event.endItem}")
          improperTurnEventCounter.incrementTurnWithIncorrectTurnLight()
        }
      } else if(2 != event.flag){
        val timeDiff = event.endItem.timestamp - event.startItem.timestamp
        if (timeDiff > minStaightForwardWithTurnLightDurationMillis) {
          println(s"直行打转向灯从${event.startItem} 到${event.endItem}")
          improperTurnEventCounter.incrementStraightForwardWithTurnLight()
        }
      }
    }
  }

  private def isTurnRight(dirDiff: Long, headDirAngle: Long, currentDirAngle: Long, minRightTurnDirAngle: Float): Boolean = {
    if(headDirAngle > 300 && currentDirAngle < 50) true
    else if(dirDiff > minRightTurnDirAngle) true
    else false
  }

  private def isTurnLeft(dirDiff: Long, headDirAngle: Long, currentDirAngle: Long, minLeftTurnDirAngle: Float): Boolean = {
    if(headDirAngle < 50 && currentDirAngle > 300) true
    else if(-dirDiff > minLeftTurnDirAngle) true
    else false
  }

}

object ImproperTurnLightBehavior {

  def main(args: Array[String]): Unit = {

    val window = new SlideWindow[Int](4)

    for(i <- 0 until 10) {
      window.add(i)
      println(window.collect().mkString(","))
    }

    val config = new SparkConf().setMaster("local[*]").setAppName("indicator")
    val spark = new SparkContext(config)

    spark.setLogLevel("ERROR")

    val hiveContext = new HiveContext(spark)

    val date = "20180206"
    val endDate = "20180214"

    // 从Hive表中读取每段行程的数据进行处理
    val df = hiveContext
      .sql("SELECT alarms,dir,gps_time,mile,speed,speed1,vehicle_status,signal_states " +
        "FROM prod_analysis.terminal_0200 a " + //, prod_analysis.terminal_0705 b
        s" WHERE vid='7a4c631ef0bc444684d4d406be17668b' " +
        s"AND a.part_yearmonthday>=$date AND a.part_yearmonthday<$endDate ORDER BY gps_time LIMIT 1000")

    // 7a4c631ef0bc444684d4d406be17668b  035baa41db8a4500873ded260a3019ec AND (array_contains(a.signal_states, '左转向灯信号') OR array_contains(a.signal_states, '右转向灯信号'))
//    df.show(200)

    val improperTurnLightBehavior = new ImproperTurnLightBehavior(Map("minRightTurnDir" -> "12"))

    val valid0200Data = collectValidDvrData(df.rdd).sortBy(_.timestamp)

    var previousTs = -1L

    valid0200Data.foreach{ item =>
      improperTurnLightBehavior.parseSingleRecord(item, previousTs)
      previousTs = item.timestamp
    }

    improperTurnLightBehavior.analysisTurnLightEvents()

  }

  val improperTurnBehavior = Map(0 -> "转弯不打转向灯")
}


class SlideWindow[T: ClassTag](windowSize: Int) extends Serializable {

  var head: ItemNode[T] = _
  var tail: ItemNode[T] = _
  var numNode: Int = 0

  def add(item: T): Unit = {
    if(numNode < windowSize) {
      var tmp = head
      if(head == null) {
        head = new ItemNode[T]()
        head.item = item
        tail = head
      } else {
        while(tmp.next != null) {
          tmp = tmp.next
        }
        tmp.next = new ItemNode[T]()
        tmp.next.item = item
        tail = tmp.next
      }
      numNode += 1
      if(numNode == windowSize)
        tmp.next.next = head
    } else {
      head.item = item
      tail = head
      head = head.next
    }
  }

  def collect(): Array[T] = {
    var count = 0
    val list = ArrayBuffer[T]()
    var tmp = head
    while(count < windowSize && tmp != null) {
      list.append(tmp.item)
      tmp = tmp.next
      count += 1
    }
    list.toArray
  }

  def first(): T = {
    require(head != null)
    head.item
  }

  def last(): T = {
    require(tail != null)
    tail.item
  }

}

class ItemNode[T: ClassTag]() {
  var item: T = _
  var next: ItemNode[T] = _
}