package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.turnlight

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.ParseBehaviorEventBy0200
import org.apache.spark.sql.Row

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag


case class TurnLightEvent(flag: Int, startItem: Row, endItem: Row)

/**
  * 急转弯
  * 转弯不打转向灯
  * 转弯打错转向灯
  *
  */
final class TurnLightSwitchBehavior(paramsMap: Map[String, String]) extends ParseBehaviorEventBy0200 {

  // get params
  private val minTurnDir: Float = paramsMap.getOrElse("minRightTurnDir", "13").toFloat
  private val minStraightForwardWithTurnLightDurationMillis = paramsMap.getOrElse("minStraightForwardWithTurnLightDuration", "5").toFloat * 1000
  private val windowSize: Int = paramsMap.getOrElse("slideWindowSize", "3").toInt
  private val maxEventDurationMillis: Int = 5 * 1000 // 5秒
  private val minTurnV: Double = paramsMap.getOrElse("minTurnSpeed", "30").toFloat * 10 // 30* 10km/h
  private val minTurnRate = paramsMap.getOrElse("minTurnRate", "37.5").toFloat

  // init vars
  private val turnLightEventStack = mutable.Stack[TurnLightEvent]()
  private val window = new SlideWindow[Row](windowSize)

  private var turnLightFlag: Int = -1 // 0->右转向灯 1->左转向灯 2->双闪
  private var turnDirFlag: Int = -1   // 0->右转向灯 1->左转向灯

  private var singleTurnLightStartItem: Row = _  //信号灯指示转弯起点
  private var turnDirStartItem: Row = _          //角度变化指示转弯起点
  private var previousItem: Row = _

  private val eventContainer = ListBuffer[BasicEventBean]()

  import TurnLightSwitchBehavior._

  // methods
  override def setUp(): Unit = {}

  override def parseCurrentRow(item: Row): Unit = {
    // 添加当前item必须在角度检查之后
    window.add(item)

    // 必须先进行角度变化检查
    if(previousItem != null) {
      checkTurnDir(item)
      // 然后做转向灯信号检查
      checkTurnLight(item)
    }

    previousItem = item
  }

  private def resetTurnLight(turnLightFlag: Int = -1, singleTurnLightStartItem: Row = null): Unit = {
    this.turnLightFlag = turnLightFlag
    this.singleTurnLightStartItem = singleTurnLightStartItem
  }

  private def resetTurnDir(turnDirFlag: Int = -1, turnDirStartItem: Row = null): Unit = {
    this.turnDirFlag = turnDirFlag
    this.turnDirStartItem = turnDirStartItem
  }

  private def checkTurnLight(item: Row): Unit = {
    val turnLightSignals = getSignalStates(item)

    if( turnLightSignals.contains("左转向灯信号") && turnLightSignals.contains("右转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(DoubleFlashFlag, item)
      } else if(DoubleFlashFlag != turnLightFlag || getGPSTime(item) - getGPSTime(previousItem) >= maxEventDurationMillis) {
//        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(DoubleFlashFlag, item)
      }
    }
    else if(turnLightSignals.contains("左转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(LeftTurnLightFlag, item)
      } else if(LeftTurnLightFlag != turnLightFlag || getGPSTime(item) - getGPSTime(previousItem) >= maxEventDurationMillis) {
        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(LeftTurnLightFlag, item)
      }
    }
    else if(turnLightSignals.contains("右转向灯信号")) {
      if(-1 == turnLightFlag) {
        resetTurnLight(RightTurnLightFlag, item)
      } else if(RightTurnLightFlag != turnLightFlag || getGPSTime(item) - getGPSTime(previousItem) >= maxEventDurationMillis) {
        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
        resetTurnLight(RightTurnLightFlag, item)
      }
    }
    else if(-1 != turnLightFlag || getGPSTime(item) - getGPSTime(previousItem) >= maxEventDurationMillis){
      if(DoubleFlashFlag != turnLightFlag)
        turnLightEventStack.push(TurnLightEvent(turnLightFlag, singleTurnLightStartItem, previousItem))
      resetTurnLight()
    }
  }

  private def checkTurnDir(item: Row): Unit = {

    val currentDir = getDir(item) //.getAs[Long]("dir")
    val currentTime = getGPSTime(item) //.getAs[Long]("speed")

    val headDirAngle = getDir(window.first()) //.getAs[Long]("dir")

    val prevD = getDir(previousItem)
    val prevT = getGPSTime(previousItem)
    val prevV = get0200Speed(previousItem)

    // 前后两条记录是否构成急转弯
    if (isSuddenTurn(currentDir, currentTime, prevD, prevT, prevV)) {
      addTurnEvent(SuddenTurnEventType, previousItem, item)
    }

    // 这里只做转弯不打转向灯判断
    if(currentTime - getGPSTime(window.first()) <= maxEventDurationMillis) {
      val dirDiff = currentDir - headDirAngle

      // 如果滑动窗口中起始点的角度构成转弯，则判断这段时间是否存在转向灯信号
      if(isTurnRight(dirDiff, headDirAngle, currentDir, minTurnDir)) {
        if( -1 == turnDirFlag) {
          resetTurnDir(0, window.first())
        }
        else if(0 != turnDirFlag) {
          if(-1 == turnLightFlag || 2 == turnLightFlag) {
            println(s"构成转弯从$turnDirStartItem 到 ${window.first()}\n")
            println("转弯不打转向灯 或者打双闪\n")
            addTurnEvent(TurnWithoutTurnLightEventType, turnDirStartItem, window.first())
          }
          resetTurnDir(0, window.first())
        }
      } else if(isTurnLeft(dirDiff, headDirAngle, currentDir, minTurnDir)) {
        if( -1 == turnDirFlag) {
          resetTurnDir(1, window.first())
        }
        else if(1 != turnDirFlag) {
          if(-1 == turnLightFlag || 2 == turnLightFlag) {
            println(s"构成转弯从$turnDirStartItem 到 ${window.first()}\n")
            println("转弯不打转向灯 或者打双闪\n")
            addTurnEvent(TurnWithoutTurnLightEventType, turnDirStartItem, window.first())
          }
          resetTurnDir(1, window.first())
        }
      }
      else if(-1 != turnDirFlag) {
        if(-1 == turnLightFlag || 2 == turnLightFlag) {
          println(s"构成转弯从$turnDirStartItem 到 ${window.first()}\n")
          println("转弯不打转向灯 或者打双闪\n")
          addTurnEvent(TurnWithoutTurnLightEventType, turnDirStartItem, window.first())
        }
        resetTurnDir()
      }
    }
    else if(-1 != turnDirFlag) {
      if(-1 == turnLightFlag || 2 == turnLightFlag) {
              println(s"构成转弯从$turnDirStartItem 到 $previousItem\n")
        println("转弯不打转向灯 或者打双闪\n")
        addTurnEvent(TurnWithoutTurnLightEventType, turnDirStartItem, window.first())
      }
      resetTurnDir()
    }
  }


  override def cleanUp(): Unit = {}

  def analysisTurnLightEvents(): Unit = {
    while (turnLightEventStack.nonEmpty) {
      val event = turnLightEventStack.pop()
      val headDir = getDir(event.startItem) //.getAs[Long]("dir")
      val lastDir = getDir(event.endItem) //.getAs[Long]("dir")
      val dirDiff = lastDir - headDir

      if(isTurnRight(dirDiff, headDir, lastDir, minTurnDir)) {
        if(0 != event.flag && 2 != event.flag) {
          println(s"右转弯错打左转向灯，从${event.startItem}到${event.endItem}\n")
          addTurnEvent(SwitchWrongTurnLightEventType, event.startItem, event.endItem)
        }
      } else if(isTurnLeft(dirDiff, headDir, lastDir, minTurnDir)) {
        if(1 != event.flag && 2 != event.flag) {
          println(s"左转弯错打右转向灯，从${event.startItem}到${event.endItem}\n")
          addTurnEvent(SwitchWrongTurnLightEventType, event.startItem, event.endItem)
        }
      } else if(2 != event.flag){
        val timeDiff = event.endItem.getAs[Long]("gps_time") - event.startItem.getAs[Long]("gps_time")
        if (timeDiff > minStraightForwardWithTurnLightDurationMillis) {
          println(s"直行打转向灯从${event.startItem} 到${event.endItem}\n")
          addTurnEvent(GoStraightWithTurnLightEventType, event.startItem, event.endItem)
        }
      }
    }
  }

  private def isSuddenTurn(dir: Long, ts: Long, prevD: Long, prevT: Long, prevV: Long): Boolean = {
    val deltaT = (ts - prevT) / 1000.0 + 1e-2 //秒
    // 先判断是否急转弯，因为急转弯通常伴随着急减速 TODO 解决角度突然从354 到 1 被判断为急转弯的问题
    val turnRate = math.abs(dir - prevD) / deltaT
    turnRate >= minTurnRate && prevV >= minTurnV
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

  private def addTurnEvent(eventType: Int, startItem: Row, endItem: Row): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    val startTime = getGPSTime(startItem)
    val endTime = getGPSTime(endItem)

    val eventName = eventType match {
      case SuddenTurnEventType =>
        EVENT_NAME_NUM_HARSHTURN
      case SwitchWrongTurnLightEventType =>
        EVENT_NAME_NUM_SWITCHWRONGDIRECTIONSIGNALLIGHT
      case TurnWithoutTurnLightEventType =>
        EVENT_NAME_NUM_TURNCORNERWITHOUTDIRECTIONSIGNALLIGHT
      case GoStraightWithTurnLightEventType =>
        EVENT_NAME_NUM_GOSTRAIGHTWITHTURNLIGHT
    }

    val be = BasicEventBean(eventName, startTime, endTime)
    eventContainer.append(be)
  }

  override def outputAllEvents(): Iterator[BasicEventBean] = eventContainer.toIterator
}

object TurnLightSwitchBehavior {

  val RightTurnLightFlag = 0
  val LeftTurnLightFlag = 1
  val DoubleFlashFlag = 2

  val SuddenTurnEventType = 0
  val SwitchWrongTurnLightEventType = 1
  val TurnWithoutTurnLightEventType = 2
  val GoStraightWithTurnLightEventType = 3

}


private[dbha] class SlideWindow[T: ClassTag](windowSize: Int) extends Serializable {

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

private[dbha] class ItemNode[T: ClassTag]() {
  var item: T = _
  var next: ItemNode[T] = _
}