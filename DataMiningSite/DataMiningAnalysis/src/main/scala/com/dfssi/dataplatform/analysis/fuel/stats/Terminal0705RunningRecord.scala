package com.dfssi.dataplatform.analysis.fuel.stats

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/10 8:55 
  */
class Terminal0705RunningRecord (val vid: String,
                                 val name: String,
                                 val value: Double,
                                 val gpstime: Long) extends Comparable[Terminal0705RunningRecord] with Serializable {
    override def compareTo(o: Terminal0705RunningRecord): Int = {
        gpstime.compareTo(o.gpstime)
    }

    override def toString = s"Terminal0705RunningRecord($vid, $name, $value, $gpstime)"

    def canEqual(other: Any): Boolean = other.isInstanceOf[Terminal0705RunningRecord]

    override def equals(other: Any): Boolean = other match {
        case that: Terminal0705RunningRecord =>
            (that canEqual this) &&
                    vid == that.vid &&
                    name == that.name &&
                    value == that.value &&
                    gpstime == that.gpstime
        case _ => false
    }

    override def hashCode(): Int = {
        val state = Seq(vid, name, value, gpstime)
        state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
}

object Terminal0705RunningRecord{

    def apply(vid: String,
              name: String,
              value: Double,
              gpstime: Long): Terminal0705RunningRecord
    = new Terminal0705RunningRecord(vid, name, value, gpstime)

}


