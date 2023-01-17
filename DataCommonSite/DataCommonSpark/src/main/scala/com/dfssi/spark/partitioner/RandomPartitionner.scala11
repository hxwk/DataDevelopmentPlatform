package com.dfssi.spark.partitioner

import org.apache.spark.Partitioner

class RandomPartitionner(partitions: Int) extends Partitioner {
  require(partitions >= 0, s"Number of partitions ($partitions) cannot be negative.")

  def numPartitions: Int = partitions

  def getPartition(key: Any): Int =  {
    
       (Math.random() * numPartitions).toInt
  }

  override def equals(other: Any): Boolean = other match {
    case h: RandomPartitionner =>
      h.numPartitions == numPartitions
    case _ =>
      false
  }

  override def hashCode: Int = numPartitions
}
