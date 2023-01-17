package com.dfssi.dataplatform.analysis.process

import java.util.ServiceLoader

import scala.collection.JavaConverters._

object ProcessFactory {

  def getProcess(processType: String): AbstractProcess = {
    val serviceLoader = ServiceLoader.load(classOf[AbstractProcess])
    serviceLoader.asScala.foreach(instance => {
      if (instance.getImplement().equals(processType)) {
        return instance
      }
    })
     throw new ClassNotFoundException("can not find implement class "+processType)
  }
}
