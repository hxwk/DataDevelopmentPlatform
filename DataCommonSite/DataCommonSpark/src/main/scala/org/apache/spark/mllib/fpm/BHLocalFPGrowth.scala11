package org.apache.spark.mllib.fpm

import org.apache.spark.Logging

import scala.reflect.ClassTag


class BHLocalFPGrowth(private var minCount: Long) extends Logging with Serializable {

  def setMinCount(minCount: Long): this.type = {
    this.minCount = minCount
    this
  }

  def run[Item: ClassTag](data: Iterator[Array[Item]],
                          countMap: Array[(Item, Long)]): Iterator[(Array[Item], Long)] = {

    val freqItems = genFreqItems(countMap)
    genFreqItemsets(data, minCount, freqItems)
  }

  /**
    * Generates frequent items by filtering the input data using minimal support level.
    *
    * @return array of frequent pattern ordered by their frequencies
    */
  private def genFreqItems[Item: ClassTag](countMap: Array[(Item, Long)]): Array[Item] = {
    countMap.filter(_._2 >= minCount).sortBy(-_._2).map(_._1)
  }

  /**
    * Generate frequent itemsets by building FP-Trees, the extraction is done on each partition.
    *
    * @param data      transactions
    * @param minCount  minimum count for frequent itemsets
    * @param freqItems frequent items
    * @return an RDD of (frequent itemset, count)
    */
  private def genFreqItemsets[Item: ClassTag](data: Iterator[Array[Item]],
                                              minCount: Long,
                                              freqItems: Array[Item]): Iterator[(Array[Item], Long)] = {

    val itemToRank = freqItems.zipWithIndex.toMap

    val tree = data.map { transaction =>
      transaction.flatMap(itemToRank.get).sorted
    }.aggregate(new BHFPTree[Int])(
      (tree, transaction) => tree.add(transaction, 1L),
      (tree1, tree2) => tree1.merge(tree2))

   tree.extract(minCount).map { case (ranks, count) =>
      (ranks.map(i => freqItems(i)).toArray, count)
    }
  }
}


