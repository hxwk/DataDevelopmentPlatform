#!/bin/sh
source /etc/profile
spark-submit --class com.dfssi.dataplatform.analysis.fuel.stats.ConditionFuelStatisticians  --master yarn --deploy-mode client --num-executors 2 --driver-memory 1g --executor-memory 3g --executor-cores 2 --jars $(echo /usr/lixc/DataMiningAnalysis-0.1-SNAPSHOT/target/jars/*.jar | tr ' ' ',') /usr/lixc/DataMiningAnalysis-0.1-SNAPSHOT/target/DataMiningAnalysis-0.1-SNAPSHOT.jar --env test
