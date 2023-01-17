package com.dfssi.dataplatform.streaming.store.utils

import java.io.{PrintWriter, StringWriter}

object ExceptionUtils {
  def getStackTraceAsString(e: Throwable): String = {
    if (e == null) {
      return ""
    }

    val stringWriter = new StringWriter
    e.printStackTrace(new PrintWriter(stringWriter))

    stringWriter.toString
  }
}
