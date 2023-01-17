package com.dfssi.dataplatform.newenergy.kafkatohive

object NewEnergyKafkaToHiveApp {

  def main(args: Array[String]): Unit = {
    val nameNode = args(0)
    val configPath = args(1)
    val newEnergyKafkaToHiveTask: NewEnergyKafkaToHiveTask = new NewEnergyKafkaToHiveTask()

    newEnergyKafkaToHiveTask.execute(nameNode, configPath)
  }
}
