package com.dfssi.dataplatform.analysis.fuel.stats

import java.math.BigDecimal

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/10 15:41 
  */
class DistributedRecord(val level: Int,
                        var count: Long,
                        var time: Long,
                        var mile: Double,
                        var fuel: Double) {

    def add(count: Long, time: Long, mile: Double, fuel: Double): Unit ={
        this.count += count
        this.time += time
        this.mile += mile
        this.fuel += fuel
    }

    def mkString(sep: String = ","): String = s"${level}${sep}${count}${sep}${time}${sep}${precisionDouble(mile, 1)}${sep}${precisionDouble(fuel, 5)}"

    override def toString = s"DistributedRecord($level, $count, $time, $mile, $fuel)"

    private def precisionDouble(value: Double, precision: Int): Double = {
        val bg = new BigDecimal(value)
        bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue
    }
}
