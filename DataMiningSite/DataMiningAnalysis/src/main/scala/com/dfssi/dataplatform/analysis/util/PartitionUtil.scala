package com.dfssi.dataplatform.analysis.util

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/16 13:55 
  */
object PartitionUtil {

    def getPartition(key: Any, numPartitions: Int): Int = key match {
        case null => 0
        case _ => nonNegativeMod(key.hashCode, numPartitions)
    }

    def nonNegativeMod(x: Int, mod: Int): Int = {
        val rawMod = x % mod
        rawMod + (if (rawMod < 0) mod else 0)
    }
}
