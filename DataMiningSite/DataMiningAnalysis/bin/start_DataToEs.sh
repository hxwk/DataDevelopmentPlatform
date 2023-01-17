#!/bin/sh
#--namenode  hdfs的namenode
#--configPath  配置文件路径，hdfs上的路径
spark-submit --class com.dfssi.dataplatform.analysis.es.TerminalDataToEsFromKafka --master yarn-cluster --deploy-mode cluster --num-executors 2 --driver-memory 1g --executor-memory 2g --executor-cores 1 --jars $(echo ../target/jars/*.jar | tr ' ' ',') ../target/DataMiningAnalysis-0.1-SNAPSHOT.jar