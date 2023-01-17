package org.apache.spark.streaming.kafka

import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.Decoder

import org.apache.spark.{Logging, SparkException}
import org.apache.spark.streaming.{StreamingContext, Time}
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.kafka.KafkaCluster.LeaderOffset
import org.apache.spark.streaming.scheduler.{RateController, StreamInputInfo}
import org.apache.spark.streaming.scheduler.rate.RateEstimator

private[streaming] class DirectKafkaInputDStream2[
K: ClassTag, V: ClassTag,
U <: Decoder[K]: ClassTag,
T <: Decoder[V]: ClassTag,
R: ClassTag](ssc_ : StreamingContext,
             val kafkaParams: Map[String, String],
             val fromOffsets: Map[TopicAndPartition, Long],
             messageHandler: MessageAndMetadata[K, V] => R
            ) extends InputDStream[R](ssc_) with Logging {

    val maxRetries = context.sparkContext.getConf.getInt("spark.streaming.kafka.maxRetries", 1)
    val groupId = kafkaParams.get("group.id").getOrElse("default")

    // Keep this consistent with how other streams are named (e.g. "Flume polling stream [2]")
    private[streaming] override def name: String = s"Kafka direct stream [$id]"

    protected[streaming] override val checkpointData = new DirectKafkaInputDStreamCheckpointData


    /**
      * Asynchronously maintains & sends new rate limits to the receiver through the receiver tracker.
      */
    override protected[streaming] val rateController: Option[RateController] = {
        if (RateController.isBackPressureEnabled(ssc.conf)) {
            Some(new DirectKafkaRateController(id,
                RateEstimator.create(ssc.conf, context.graph.batchDuration)))
        } else {
            None
        }
    }

    protected val kc = new KafkaCluster(kafkaParams)

    private val maxRateLimitPerPartition: Int = context.sparkContext.getConf.getInt(
        "spark.streaming.kafka.maxRatePerPartition", 0)
    protected def maxMessagesPerPartition: Option[Long] = {
        val estimatedRateLimit = rateController.map(_.getLatestRate().toInt)
        val numPartitions = currentOffsets.keys.size

        val effectiveRateLimitPerPartition = estimatedRateLimit
          .filter(_ > 0)
          .map { limit =>
              if (maxRateLimitPerPartition > 0) {
                  Math.min(maxRateLimitPerPartition, (limit / numPartitions))
              } else {
                  limit / numPartitions
              }
          }.getOrElse(maxRateLimitPerPartition)

        if (effectiveRateLimitPerPartition > 0) {
            val secsPerBatch = context.graph.batchDuration.milliseconds.toDouble / 1000
            Some((secsPerBatch * effectiveRateLimitPerPartition).toLong)
        } else {
            None
        }
    }

    protected var currentOffsets = fromOffsets

    @tailrec
    protected final def latestLeaderOffsets(retries: Int): Map[TopicAndPartition, LeaderOffset] = {
        val o = kc.getLatestLeaderOffsets(currentOffsets.keySet)
        // Either.fold would confuse @tailrec, do it manually
        if (o.isLeft) {
            val err = o.left.get.toString
            if (retries <= 0) {
                throw new SparkException(err)
            } else {
                log.error(err)
                Thread.sleep(kc.config.refreshLeaderBackoffMs)
                latestLeaderOffsets(retries - 1)
            }
        } else {
            o.right.get
        }
    }

    // limits the maximum number of messages per partition
    protected def clamp(leaderOffsets: Map[TopicAndPartition, LeaderOffset]): Map[TopicAndPartition, LeaderOffset] = {
        maxMessagesPerPartition.map { mmp =>
            leaderOffsets.map { case (tp, lo) =>
                tp -> lo.copy(offset = Math.min(currentOffsets(tp) + mmp, lo.offset))
            }
        }.getOrElse(leaderOffsets)
    }

    override def compute(validTime: Time): Option[KafkaRDD2[K, V, U, T, R]] = {

        val untilOffsets = clamp(latestLeaderOffsets(maxRetries))

        val rdd = KafkaRDD2[K, V, U, T, R](
            context.sparkContext, kafkaParams, currentOffsets, untilOffsets, messageHandler)

        // Report the record number and metadata of this batch interval to InputInfoTracker.
        val offsetRanges = currentOffsets.map { case (tp, fo) =>
            val uo = untilOffsets(tp)
            OffsetRange(tp.topic, tp.partition, fo, uo.offset)
        }
        val description = offsetRanges.filter { offsetRange =>
            // Don't display empty ranges.
            offsetRange.fromOffset != offsetRange.untilOffset
        }.map { offsetRange =>
            s"topic: ${offsetRange.topic}\tpartition: ${offsetRange.partition}\t" +
              s"offsets: ${offsetRange.fromOffset} to ${offsetRange.untilOffset}"
        }.mkString("\n")
        // Copy offsetRanges to immutable.List to prevent from being modified by the user
        val metadata = Map(
            "offsets" -> offsetRanges.toList,
            StreamInputInfo.METADATA_KEY_DESCRIPTION -> description)
        val inputInfo = StreamInputInfo(id, rdd.count, metadata)
        ssc.scheduler.inputInfoTracker.reportInfo(validTime, inputInfo)

        currentOffsets = untilOffsets.map(kv => kv._1 -> kv._2.offset)
        Some(rdd)
    }

    override def start(): Unit = {}

    def stop(): Unit = {}

    /*
    private def checkOffsets(offsets:Map[TopicAndPartition, LeaderOffset]): Map[TopicAndPartition, LeaderOffset] ={

        val res = mutable.HashMap.empty[TopicAndPartition, LeaderOffset]

        offsets.foreach(kv =>{

            var lo = kv._2
            val consumer = kv._2.offset

            val earOffsets = kc.getEarliestLeaderOffsets(Set(kv._1))
            if(earOffsets.isRight){
                var maybeOffset = earOffsets.right.get
                val ear = maybeOffset(kv._1).offset
                if(consumer < ear){
                    lo =  KafkaCluster.LeaderOffset(kv._2.host, kv._2.port, ear)
                    logWarning(s"调整了分区${kv} 的offset为 ${ear} ")
                }else{
                    val latestOffsets = kc.getLatestLeaderOffsets(Set(kv._1))
                    if(latestOffsets.isRight){
                        maybeOffset = latestOffsets.right.get
                        val latest = maybeOffset(kv._1).offset
                        if(consumer > latest){
                            lo =  KafkaCluster.LeaderOffset(kv._2.host, kv._2.port, latest)
                            logWarning(s"调整了分区${kv} 的offset为 ${latest} ")
                        }
                    }
                }
            }
            res += ((kv._1, lo))
        })

        res.toMap
    }*/

    private[streaming] class DirectKafkaInputDStreamCheckpointData extends DStreamCheckpointData(this) {

        def batchForTime: mutable.HashMap[Time, Array[(String, Int, Long, Long)]] = {
            data.asInstanceOf[mutable.HashMap[Time, Array[OffsetRange.OffsetRangeTuple]]]
        }

        override def update(time: Time) {
            batchForTime.clear()
            generatedRDDs.foreach { kv =>
                val a = kv._2.asInstanceOf[KafkaRDD2[K, V, U, T, R]].offsetRanges.map(_.toTuple).toArray
                batchForTime += kv._1 -> a
            }
        }

        override def cleanup(time: Time) { }

        override def restore() {
            // this is assuming that the topics don't change during execution, which is true currently
            val topics = fromOffsets.keySet
            val leaders = KafkaCluster.checkErrors(kc.findLeaders(topics))

            batchForTime.toSeq.sortBy(_._1)(Time.ordering).foreach { case (t, b) =>
                logInfo(s"Restoring KafkaRDD for time $t ${b.mkString("[", ", ", "]")}")
                generatedRDDs += t -> new KafkaRDD2[K, V, U, T, R](
                    context.sparkContext, kafkaParams, b.map(OffsetRange(_)), leaders, messageHandler, true)
            }
        }
    }

    /**
      * A RateController to retrieve the rate from RateEstimator.
      */
    private[streaming] class DirectKafkaRateController(id: Int, estimator: RateEstimator)
      extends RateController(id, estimator) {
        override def publish(rate: Long): Unit = ()
    }
}
