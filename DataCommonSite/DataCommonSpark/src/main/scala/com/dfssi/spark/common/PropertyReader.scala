/**
  * Copyright (c)  2016,  jechedo All Rights Reserved.
  *
 */
package com.dfssi.spark.common

import scala.io.Source
import scala.collection.mutable.HashMap
import scala.reflect.ClassTag

class PropertyReader(val filePath: String, val encoding: String = "UTF-8") extends Cloneable{
  
  //构建线程同步的HashMap
  //private val props = new HashMap[String, String] with SynchronizedMap[String, String]
  private val props = new HashMap[String, String]
  load(filePath, encoding)
  
  private[this] def load(filePath:String, encoding: String){
		  Source.fromFile(this.getClass.getResource(filePath).getPath, encoding).getLines.filterNot(_.startsWith("#"))
		  .map(splitKV).filter(_.length == 2).foreach(kv => props.put(kv(0), kv(1)))
  }

  private[this] def splitKV(kv : String) : Array[String] = {
    val index = kv.indexOf("=")
    var res:Array[String] = null
    if(index > 0 ) res = Array(kv.substring(0, index), kv.substring(index + 1)) else  res = Array(kv)
    res
  }

  def getOption(key: String): Option[String] =  props.get(key)

  def get(key: String): String = {
    getOption(key).getOrElse(null)
  }

  def getInt(key: String, defaultValue: Int): Int = {
    getOption(key).map(_.toInt).getOrElse(defaultValue)
  }

  def getLong(key: String, defaultValue: Long): Long = {
    getOption(key).map(_.toLong).getOrElse(defaultValue)
  }

  
  def getDouble(key: String, defaultValue: Double): Double = {
    getOption(key).map(_.toDouble).getOrElse(defaultValue)
  }

  def getBoolean(key: String, defaultValue: Boolean): Boolean = {
    getOption(key).map(_.toBoolean).getOrElse(defaultValue)
  }

  def getValue[T: ClassTag](key: String, parser:(String) => T) : T = {
    parser(getOption(key).getOrElse(""))
  }
  
  def getAll: Array[(String, String)] = props.toArray
  
  def setAll(props: Traversable[(String, String)]) = {
    this.props ++= props
    this
  }
  
  
  def contains(key: String): Boolean = props.contains(key)

  def remove(key: String) = props.remove(key)

  override def clone: PropertyReader =  new PropertyReader(filePath, encoding)
}


object PropertyReader {
  
  def apply(filePath: String, encoding: String = "UTF-8") = new PropertyReader(filePath, encoding)
  
  def main(args: Array[String]) = {
    var propertyReader =  PropertyReader("/relation/relation-config.properties")
    println(propertyReader get "tables")
  }
}