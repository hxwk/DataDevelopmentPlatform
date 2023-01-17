package com.dfssi.dataplatform.streaming.store.merge

object MergeApp {
  def main(args: Array[String]): Unit = {
    val nameNode = args(0)
    val configPath = args(1)
    val tableName = args(2)
    val firstLevelPartitionValue = args(3)
    val secondLevelPartitionValue = args(4)

    val mergeTask: MergeTask = new MergeTask()
    mergeTask.execute(nameNode,
                      configPath,
                      tableName,
                      firstLevelPartitionValue,
                      secondLevelPartitionValue)
  }
}
