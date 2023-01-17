/**
 * Copyright (c) 2016, jechedo All Rights Reserved.
 *
 */
package com.dfssi.spark

/**
 * Description:
 *
 *  Date    2016-7-23 上午9:24:24
 *
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
class KV(val rowKey:String,
         val cl:String, 
         val value:String,
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
     if(!that.isInstanceOf[KV]) return false
     
     val tmp = that.asInstanceOf[KV]
     if(rowKey.equals(tmp.rowKey) && cl.equals(tmp.cl) && value.equals(tmp.value) && version.equals(version)){
       return true
     }
     
     false
   }
    
  
}

object KV{
  
  def apply(rowKey:String,
            cl:String, 
            value:String,
            version:Long = System.currentTimeMillis()) = new KV(rowKey, cl,value, version)
}