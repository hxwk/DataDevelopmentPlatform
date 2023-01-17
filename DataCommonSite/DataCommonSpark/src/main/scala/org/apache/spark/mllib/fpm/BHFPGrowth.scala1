package org.apache.spark.mllib.fpm

import java.lang.{Iterable => JavaIterable}
import java.{util => ju}

import org.apache.spark.annotation.Since
import org.apache.spark.mllib.fpm.BHFPGrowth._
import org.apache.spark.mllib.fpm.FPGrowth.FreqItemset
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{HashPartitioner, Logging, Partitioner, SparkException}

import scala.collection.mutable
import scala.reflect.ClassTag


/**
  * Model trained by [[FPGrowth]], which holds frequent itemsets.
  * @param freqItemsets frequent itemset, which is an RDD of [[]]
  * @tparam Item item type
  */
@Since("1.3.0")
class BHFPGrowthModel[Item: ClassTag] @Since("1.3.0") (
                                                      @Since("1.3.0") val freqItemsets: RDD[BHFreqItemset[Item]]) extends Serializable {
  /**
    * Generates association rules for the [[Item]]s in [[freqItemsets]].
    * @param confidence minimal confidence of the rules produced
    */
  @Since("1.5.0")
  def generateAssociationRules(confidence: Double): RDD[AssociationRules.Rule[Item]] = {
    val associationRules = new AssociationRules(confidence)
    val fs = freqItemsets.map(_.asInstanceOf[FreqItemset[Item]])
    associationRules.run(fs)
  }
}

/**
  * A parallel FP-growth algorithm to mine frequent itemsets. The algorithm is described in
  * [[http://dx.doi.org/10.1145/1454008.1454027 Li et al., PFP: Parallel FP-Growth for Query
  *  Recommendation]]. PFP distributes computation in such a way that each worker executes an
  * independent group of mining tasks. The FP-Growth algorithm is described in
  * [[http://dx.doi.org/10.1145/335191.335372 Han et al., Mining frequent patterns without candidate
  *  generation]].
  *
  *                   more than (minSupport * size-of-the-dataset) times will be output
  * @param numPartitions number of partitions used by parallel FP-growth
  *
  * @see [[http://en.wikipedia.org/wiki/Association_rule_learning Association rule learning
  *       (Wikipedia)]]
  *
  */
@Since("1.3.0")
class BHFPGrowth(private var minCount: Long,
                 private var minSize:Int,
                  private var numPartitions: Int) extends Logging with Serializable {

  /**
    * Sets the minimal support level (default: `0.3`).
    *
    */
  def setMinCount(minCount: Long): this.type = {
    this.minCount = minCount
    this
  }

  def setMinSize(minSize: Int): this.type = {
    this.minSize = minSize
    this
  }

  /**
    * Sets the number of partitions used by parallel FP-growth (default: same as input data).
    *
    */
  @Since("1.3.0")
  def setNumPartitions(numPartitions: Int): this.type = {
    this.numPartitions = numPartitions
    this
  }

  /**
    * Computes an FP-Growth model that contains frequent itemsets.
    * @param data input data set, each element contains a transaction
    * @return an [[FPGrowthModel]]
    *
    */
  @Since("1.3.0")
  def run[Item: ClassTag](data: RDD[Array[Item]]): BHFPGrowthModel[Item] = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("Input data is not cached.")
    }
    val numParts = if (numPartitions > 0) numPartitions else data.partitions.length
    val partitioner = new HashPartitioner(numParts)
    val freqItems = genFreqItems(data, minCount, partitioner)
    val freqItemsets = genFreqItemsets(data, minCount, minSize, freqItems, partitioner)
    new BHFPGrowthModel(freqItemsets)
  }

  /**
    * Generates frequent items by filtering the input data using minimal support level.
    * @return array of frequent pattern ordered by their frequencies
    */
  private def genFreqItems[Item: ClassTag](
                                            data: RDD[Array[Item]],
                                            minCount: Long,
                                            partitioner: Partitioner): Array[Item] = {
    data.flatMap { t =>
      val uniq = t.toSet
      if (t.size != uniq.size) {
        throw new SparkException(s"Items in a transaction must be unique but got ${t.toSeq}.")
      }
      t
    }.map(v => (v, 1L))
      .reduceByKey(partitioner, _ + _)
      .filter(_._2 >= minCount)
      .collect()
      .sortBy(-_._2)
      .map(_._1)
  }

  /**
    * Generate frequent itemsets by building FP-Trees, the extraction is done on each partition.
    * @param data transactions
    * @param minCount minimum count for frequent itemsets
    * @param freqItems frequent items
    * @param partitioner partitioner used to distribute transactions
    * @return an RDD of (frequent itemset, count)
    */
  private def genFreqItemsets[Item: ClassTag](data: RDD[Array[Item]],
                                               minCount: Long,
                                               minSize:Int,
                                               freqItems: Array[Item],
                                               partitioner: Partitioner): RDD[BHFreqItemset[Item]] = {
    val itemToRank = freqItems.zipWithIndex.toMap
    val broadcast = data.sparkContext.broadcast(itemToRank)
    data.flatMap { transaction =>
      genCondTransactions(transaction, broadcast.value, partitioner)
    }.aggregateByKey(new BHFPTree[Int], partitioner.numPartitions)(
      (tree, transaction) => tree.add(transaction, 1L),
      (tree1, tree2) => tree1.merge(tree2))
      .flatMap { case (part, tree) =>
        tree.extract(minCount, x => partitioner.getPartition(x) == part).filter(_._1.size >= minSize)
      }.map { case (ranks, count) =>
      new BHFreqItemset(ranks.map(i => freqItems(i)).toArray, count)
    }
  }

  /**
    * Generates conditional transactions.
    * @param transaction a transaction
    * @param itemToRank map from item to their rank
    * @param partitioner partitioner used to distribute transactions
    * @return a map of (target partition, conditional transaction)
    */
  private def genCondTransactions[Item: ClassTag](
                                                   transaction: Array[Item],
                                                   itemToRank: Map[Item, Int],
                                                   partitioner: Partitioner): mutable.Map[Int, Array[Int]] = {
    val output = mutable.Map.empty[Int, Array[Int]]
    // Filter the basket by frequent items pattern and sort their ranks.
    val filtered = transaction.flatMap(itemToRank.get).sorted
    val n = filtered.length
    var i = n - 1
    var item:Int = -1
    var part:Int = -1
    while (i >= 0) {
      item = filtered(i)
      part = partitioner.getPartition(item)
      if (!output.contains(part)) {
        output(part) = filtered.slice(0, i + 1)
      }
      i -= 1
    }
    output
  }
}

@Since("1.3.0")
object BHFPGrowth {


  @Since("1.3.0")
  class BHFreqItemset[Item](items: Array[Item], freq: Long)
    extends FreqItemset(items, freq) with Serializable {}

}