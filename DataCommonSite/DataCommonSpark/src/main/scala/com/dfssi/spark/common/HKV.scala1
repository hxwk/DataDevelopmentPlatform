package com.dfssi.spark.common

import org.apache.hadoop.hbase.util.Bytes

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 1.0
  */
class HKV(val rowKey:Array[Byte],
          val cl:Array[Byte],
          val value:Array[Byte],
          val version:Long = System.currentTimeMillis()) extends Product {

  require(rowKey != null)
  require(cl != null)
  require(value != null)

  def productElement(n: Int): Any = {

    n match {
      case 0 => return rowKey
      case 1 => return cl
      case 2 => return value
      case 3 => return version
      case _ => throw new IndexOutOfBoundsException(s"不存在下标 $n")
    }
  }

  def productArity: Int = 4

  def canEqual(that: Any): Boolean = {
    if(!that.isInstanceOf[HKV]) return false

    val tmp = that.asInstanceOf[HKV]
    if(Bytes.equals(rowKey, tmp.rowKey) && Bytes.equals(cl, tmp.cl)
             && Bytes.equals(value, tmp.value) && version == tmp.version){
      return true
    }
    false
  }

  def toPair:(((Array[Byte], Array[Byte]), Long), Array[Byte]) =
    (((rowKey, cl), version), value)

}

object HKV{

  def apply(rowKey:Array[Byte],
              cl:Array[Byte],
            value:Array[Byte],
            version:Long = System.currentTimeMillis()) = new HKV(rowKey, cl,value, version)
}