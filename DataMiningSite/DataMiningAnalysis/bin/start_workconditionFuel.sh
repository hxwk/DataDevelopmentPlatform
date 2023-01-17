#!/bin/sh
spark-submit --class com.dfssi.dataplatform.analysis.fuel.stats.ConditionFuelStatisticians
--master yarn
--deploy-mode client
--num-executors 2
--driver-memory 1g
--executor-memory 3g
--executor-cores 2
--jars $(echo ../target/jars/*.jar | tr ' ' ',')
../target/DataMiningAnalysis-0.1-SNAPSHOT.jar