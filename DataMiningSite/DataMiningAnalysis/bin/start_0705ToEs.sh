#!/bin/sh
#--topics  指定消费的topic名称  可根据环境情况修改
#--brokerList  指定kafka的broker的列表  可更加环境情况进行修改
#--interval    指定任务的批处理时间间隔  单位为秒 ， 可自行修改
spark-submit --class com.dfssi.dataplatform.analysis.es.Terminal0705ToEsFromKafka --master yarn-cluster --deploy-mode cluster --num-executors 2 --driver-memory 1g --executor-memory 3g --executor-cores 2 --jars $(echo ../target/jars/*.jar | tr ' ' ',') ../target/DataMiningAnalysis-0.1-SNAPSHOT.jar --topic CANINFORMATION_0705_TOPIC --brokerList 172.16.1.121:9092,172.16.1.122:9092,172.16.1.121:9092 --esNodes 172.16.1.221,172.16.1.222,172.16.1.223 --esClusterName elk --interval 60