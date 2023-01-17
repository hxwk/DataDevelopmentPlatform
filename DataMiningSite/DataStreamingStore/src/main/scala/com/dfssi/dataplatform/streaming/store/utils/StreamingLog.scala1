package com.dfssi.dataplatform.streaming.store.utils

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.Logging

object StreamingLog extends Logging {
  def setStreamingLogLevels(): Unit = {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      logInfo(
        "Setting log level to [WARN] for streaming example." + " To override add a custom log4j.properties to the classpath.")
      Logger.getRootLogger.setLevel(Level.WARN)
    }
    val e = LogManager.getLoggerRepository.getCurrentLoggers()
    while (e != null && e.hasMoreElements) {
      val logger = e.nextElement()
      if (logger.isInstanceOf[Logger]) {
        logger.asInstanceOf[Logger].setLevel(Level.WARN)
      }
    }

  }
}
