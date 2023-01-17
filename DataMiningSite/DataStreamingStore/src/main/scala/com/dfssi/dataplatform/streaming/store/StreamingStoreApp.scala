package com.dfssi.dataplatform.streaming.store

object StreamingStoreApp {

  def main(args: Array[String]): Unit = {
    val nameNode = args(0)
    val configPath = args(1)
    val streamingStoreTask: StreamingStoreTask = new StreamingStoreTask()

    streamingStoreTask.execute(nameNode, configPath)
  }
}
