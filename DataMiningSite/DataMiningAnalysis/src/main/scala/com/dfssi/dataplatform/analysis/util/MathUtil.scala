package com.dfssi.dataplatform.analysis.util

import java.math.RoundingMode


/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/16 14:08 
  */
object MathUtil {

    def rounding(value: Double, p: Int): Double ={
        val b = new java.math.BigDecimal(value)
        b.setScale(p, RoundingMode.HALF_UP).doubleValue
    }

}
