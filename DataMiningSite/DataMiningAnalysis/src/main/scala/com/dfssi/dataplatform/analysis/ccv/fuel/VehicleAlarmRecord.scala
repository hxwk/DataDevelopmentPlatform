package com.dfssi.dataplatform.analysis.ccv.fuel

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/23 14:10 
  */
private [fuel]
class VehicleAlarmRecord(val alarm: String,
                         val alarmType: Int,
                         val alarmLabel: Int,
                         val alarmDegree: Int,
                         val alarmCode: Int) extends Serializable {


    var count = 1

    def canEqual(other: Any): Boolean = other.isInstanceOf[VehicleAlarmRecord]

    def getName(): String = s"${alarm}-${alarmType}"

    override def equals(other: Any): Boolean = other match {
        case that: VehicleAlarmRecord =>
            val status = (that canEqual this) &&
                    alarm == that.alarm &&
                    alarmType == that.alarmType &&
                    alarmLabel == that.alarmLabel &&
                    alarmDegree == that.alarmDegree &&
                    alarmCode == that.alarmCode

            if(status){
                this.count += that.count
            }

            status
        case _ => false
    }

    override def hashCode(): Int = {
        val state = Seq(alarm, alarmType, alarmLabel, alarmDegree, alarmCode)
        state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
}
