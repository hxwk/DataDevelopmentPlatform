/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.rdd

import java.io.{EOFException, IOException}
import java.text.SimpleDateFormat
import java.util.Date

import com.dfssi.spark.common.FileInputFormatUtil
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path, PathFilter}
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred._
import org.apache.hadoop.mapred.lib.CombineFileSplit
import org.apache.hadoop.mapreduce.security.TokenCache
import org.apache.hadoop.util.ReflectionUtils
import org.apache.spark._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.deploy.SparkHadoopUtil
import org.apache.spark.executor.DataReadMethod
import org.apache.spark.scheduler.{HDFSCacheTaskLocation, HostTaskLocation}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.util.{NextIterator, SerializableConfiguration, ShutdownHookManager, Utils}

import scala.collection.immutable.Map
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  *
  */
private[spark] class NewDirPartition(rddId: Int, idx: Int, s: InputSplit)
  extends Partition {

  val inputSplit = new SerializableWritable[InputSplit](s)

  override def hashCode(): Int = 41 * (41 + rddId) + idx

  override val index: Int = idx

  /**
    * Get any environment variables that should be added to the users environment when running pipes
    * @return a Map with the environment variables and corresponding values, it could be empty
    */
  def getPipeEnvVars(): Map[String, String] = {
    val envVars: Map[String, String] = if (inputSplit.value.isInstanceOf[FileSplit]) {
      val is: FileSplit = inputSplit.value.asInstanceOf[FileSplit]
      // map_input_file is deprecated in favor of mapreduce_map_input_file but set both
      // since its not removed yet
      Map("map_input_file" -> is.getPath().toString(),
        "mapreduce_map_input_file" -> is.getPath().toString())
    } else {
      Map()
    }
    envVars
  }
}

/**
  *  hadoopFile(path, classOf[TextInputFormat], classOf[LongWritable], classOf[Text],
      minPartitions).map(pair => pair._2.toString).setName(path)
  * @param sc
  * @param broadcastedConf
  * @param initLocalJobConfFuncOpt
  * @param minPartitions
  */
class NewDirRDD private(sc: SparkContext,
                            broadcastedConf: Broadcast[SerializableConfiguration],
                            initLocalJobConfFuncOpt: Option[JobConf => Unit],
                            splitFilterFuncOpt: Option[(Path,JobConf) => Boolean],
                            keyCreaterFuncOpt: Option[(Path) => String],
                            minPartitions: Int,
                            startDate:Long = 0)
  extends RDD[(String, String)](sc, Nil) with Logging {

  if (initLocalJobConfFuncOpt.isDefined) {
    sparkContext.clean(initLocalJobConfFuncOpt.get)
  }

  if (splitFilterFuncOpt.isDefined) {
    sparkContext.clean(keyCreaterFuncOpt.get)
  }

  if (keyCreaterFuncOpt.isDefined) {
    sparkContext.clean(keyCreaterFuncOpt.get)
  }

  def this(sc: SparkContext, path:String,
           filterFilter:(Path,JobConf) => Boolean,
           keyCreater:(Path)=>String,
           minPartitions: Int, startDate:Long = 0) = {
    this(
      sc,
      sc.broadcast(new SerializableConfiguration(sc.hadoopConfiguration))
        .asInstanceOf[Broadcast[SerializableConfiguration]],
      Some((jobConf: JobConf) => FileInputFormat.setInputPaths(jobConf, path)),
      Some(filterFilter),
      Some(keyCreater),
      minPartitions,
      startDate)
    setName(path)
  }

  protected val jobConfCacheKey = "rdd_%d_job_conf".format(id)

  protected val inputFormatCacheKey = "rdd_%d_input_format".format(id)

  // used to build JobTracker ID
  private val createTime = new Date()

  private val shouldCloneJobConf = sparkContext.conf.getBoolean("spark.hadoop.cloneConf", false)

  // Returns a JobConf that will be used on slaves to obtain input splits for Hadoop reads.
  protected def getJobConf(): JobConf = {
    val conf: Configuration = broadcastedConf.value.value
    if (shouldCloneJobConf) {
      // Hadoop Configuration objects are not thread-safe, which may lead to various problems if
      // one job modifies a configuration while another reads it (SPARK-2546).  This problem occurs
      // somewhat rarely because most jobs treat the configuration as though it's immutable.  One
      // solution, implemented here, is to clone the Configuration object.  Unfortunately, this
      // clone can be very expensive.  To avoid unexpected performance regressions for workloads and
      // Hadoop versions that do not suffer from these thread-safety issues, this cloning is
      // disabled by default.
      NewDirRDD.CONFIGURATION_INSTANTIATION_LOCK.synchronized {
        logDebug("Cloning Hadoop Configuration")
        val newJobConf = new JobConf(conf)
        if (!conf.isInstanceOf[JobConf]) {
          initLocalJobConfFuncOpt.map(f => f(newJobConf))
        }
        newJobConf
      }
    } else {
      if (conf.isInstanceOf[JobConf]) {
        logDebug("Re-using user-broadcasted JobConf")
        conf.asInstanceOf[JobConf]
      } else if (NewDirRDD.containsCachedMetadata(jobConfCacheKey)) {
        logDebug("Re-using cached JobConf")
        NewDirRDD.getCachedMetadata(jobConfCacheKey).asInstanceOf[JobConf]
      } else {
        // Create a JobConf that will be cached and used across this RDD's getJobConf() calls in the
        // local process. The local cache is accessed through HadoopRDD.putCachedMetadata().
        // The caching helps minimize GC, since a JobConf can contain ~10KB of temporary objects.
        // Synchronize to prevent ConcurrentModificationException (SPARK-1097, HADOOP-10456).
        NewDirRDD.CONFIGURATION_INSTANTIATION_LOCK.synchronized {
          logDebug("Creating new JobConf and caching it for later re-use")
          val newJobConf = new JobConf(conf)
          initLocalJobConfFuncOpt.map(f => f(newJobConf))
          NewDirRDD.putCachedMetadata(jobConfCacheKey, newJobConf)
          newJobConf
        }
      }
    }
  }

  protected def getInputFormat(conf: JobConf): TextInputFormat = {
    ReflectionUtils.newInstance(classOf[TextInputFormat], conf)
  }

  override def getPartitions: Array[Partition] = {
    val jobConf = getJobConf()

    SparkHadoopUtil.get.addCredentials(jobConf)

    val acceptFiles = getAcceptFiles(jobConf)
    logInfo("???????????????????????????????????????")
    acceptFiles.foreach(f => logInfo(s"${f.getPath}"))

    val inputSplits = FileInputFormatUtil.getSplits(jobConf, acceptFiles, minPartitions)

    //val inputFormat = getInputFormat(jobConf)
    //val inputSplits = inputFormat.getSplits(jobConf, minPartitions)

    val array = new Array[Partition](inputSplits.size)
    for (i <- 0 until inputSplits.size) {
      array(i) = new NewDirPartition(id, i, inputSplits(i))
    }
    array
  }

  private def getAcceptFiles(jobConf:JobConf) ={

    val fs = FileSystem.get(jobConf)
    val pathFilter = getFilter(jobConf)

    val paths = FileInputFormatUtil.getInputPaths(jobConf)
    TokenCache.obtainTokensForNamenodes(jobConf.getCredentials, paths, jobConf)

    val errors = new ArrayBuffer[Any]
    val accept = paths.flatMap(path =>{
      findNewFileStatus(fs, path, pathFilter, errors)
    })
    errors.foreach(err => logError(err.toString))

    accept
  }

  private def findNewFileStatus(fs:FileSystem,
                                path:Path,
                                pathFilter:PathFilter,
                                errors:ArrayBuffer[Any]): Array[FileStatus] ={

    val res = new ArrayBuffer[FileStatus]()
    val fileStatus = fs.listStatus(path, pathFilter)
    if(fileStatus == null){
      errors += (new IOException("Input path does not exist: " + path))
    }else if(fileStatus.length > 0){
      //val acceptFiles = fileStatus.filter(status => status.getModificationTime.compareTo(startDate) > 0)
      if(fileStatus.length > 0){
        fileStatus.foreach(status =>{
          if(status.isDirectory){
            res ++= findNewFileStatus(fs, status.getPath, pathFilter, errors)
          }else{
            if(status.getModificationTime.compareTo(startDate) > 0)
            res += (status)
          }
        })
      }
    }
    res.toArray
  }

  private def getFilter(jobConf:JobConf): PathFilter ={

    val hiddenFileFilter: PathFilter = new PathFilter() {
      def accept(p: Path): Boolean = {
        val name = p.getName
        !name.startsWith("_") && !name.startsWith(".")
      }
    }
    val filters = new java.util.ArrayList[PathFilter]
    filters.add(hiddenFileFilter)

    val jobFilter = FileInputFormatUtil.getInputPathFilter(jobConf)
    if (jobFilter != null) filters.add(jobFilter)

    if(splitFilterFuncOpt.isDefined){
      val customFileFilter: PathFilter = new PathFilter() {
        def accept(p: Path): Boolean = {
          splitFilterFuncOpt.get(p, jobConf)
        }
      }
      filters.add(customFileFilter)
    }

    val inputFilter = new MultiPathFilter(filters)
    inputFilter
  }


  override def compute(theSplit: Partition, context: TaskContext): InterruptibleIterator[(String, String)] = {
    val iter = new NextIterator[(String, String)] {

      val split = theSplit.asInstanceOf[NewDirPartition]
      logInfo("Input split: " + split.inputSplit)
      val jobConf = getJobConf()

      val fs = split.inputSplit.value.asInstanceOf[FileSplit]
      SqlNewHadoopRDDState.setInputFileName(fs.getPath.toString)

      val inputMetrics = context.taskMetrics.getInputMetricsForReadMethod(DataReadMethod.Hadoop)
      val bytesReadCallback = inputMetrics.bytesReadCallback.orElse(SparkHadoopUtil.get.getFSBytesReadOnThreadCallback())
      inputMetrics.setBytesReadCallback(bytesReadCallback)

      var reader: RecordReader[LongWritable, Text] = null
      val inputFormat = getInputFormat(jobConf)
      NewDirRDD.addLocalConfiguration(new SimpleDateFormat("yyyyMMddHHmm").format(createTime),
        context.stageId, theSplit.index, context.attemptNumber, jobConf)
      reader = inputFormat.getRecordReader(split.inputSplit.value, jobConf, Reporter.NULL)

      // Register an on-task-completion callback to close the input stream.
      context.addTaskCompletionListener{ context => closeIfNeeded() }
      val key: LongWritable = reader.createKey()
      val value: Text = reader.createValue()

      override def getNext(): (String, String) = {
        try {
          finished = !reader.next(key, value)
        } catch {
          case eof: EOFException =>
            finished = true
        }
        if (!finished) {
          inputMetrics.incRecordsRead(1)
        }

        (if(keyCreaterFuncOpt.isDefined)keyCreaterFuncOpt.get(fs.getPath) else key.get().toString,
          value.toString)
      }

      override def close() {
        if (reader != null) {
          SqlNewHadoopRDDState.unsetInputFileName()
          // Close the reader and release it. Note: it's very important that we don't close the
          // reader more than once, since that exposes us to MAPREDUCE-5918 when running against
          // Hadoop 1.x and older Hadoop 2.x releases. That bug can lead to non-deterministic
          // corruption issues when reading compressed input.
          try {
            reader.close()
          } catch {
            case e: Exception =>
              if (!ShutdownHookManager.inShutdown()) {
                logWarning("Exception in RecordReader.close()", e)
              }
          } finally {
            reader = null
          }
          if (bytesReadCallback.isDefined) {
            inputMetrics.updateBytesRead()
          } else if (split.inputSplit.value.isInstanceOf[FileSplit] ||
            split.inputSplit.value.isInstanceOf[CombineFileSplit]) {
            // If we can't get the bytes read from the FS stats, fall back to the split size,
            // which may be inaccurate.
            try {
              inputMetrics.incBytesRead(split.inputSplit.value.getLength)
            } catch {
              case e: java.io.IOException =>
                logWarning("Unable to get input size to set InputMetrics for task", e)
            }
          }
        }
      }
    }
    new InterruptibleIterator[(String, String)](context, iter)
  }

  override def getPreferredLocations(split: Partition): Seq[String] = {
    val hsplit = split.asInstanceOf[NewDirPartition].inputSplit.value
    val locs: Option[Seq[String]] = NewDirRDD.SPLIT_INFO_REFLECTIONS match {
      case Some(c) =>
        try {
          val lsplit = c.inputSplitWithLocationInfo.cast(hsplit)
          val infos = c.getLocationInfo.invoke(lsplit).asInstanceOf[Array[AnyRef]]
          Some(NewDirRDD.convertSplitLocationInfo(infos))
        } catch {
          case e: Exception =>
            logDebug("Failed to use InputSplitWithLocations.", e)
            None
        }
      case None => None
    }
    locs.getOrElse(hsplit.getLocations.filter(_ != "localhost"))
  }

  override def checkpoint() {
    // Do nothing. Hadoop RDD should not be checkpointed.
  }

  override def persist(storageLevel: StorageLevel): this.type = {
    if (storageLevel.deserialized) {
      logWarning("Caching NewHadoopRDDs as deserialized objects usually leads to undesired" +
        " behavior because Hadoop's RecordReader reuses the same Writable object for all records." +
        " Use a map transformation to make copies of the records.")
    }
    super.persist(storageLevel)
  }

  def getConf: Configuration = getJobConf()
}

private[spark] object NewDirRDD extends Logging {
  /**
    * Configuration's constructor is not threadsafe (see SPARK-1097 and HADOOP-10456).
    * Therefore, we synchronize on this lock before calling new JobConf() or new Configuration().
    */
  val CONFIGURATION_INSTANTIATION_LOCK = new Object()

  /** Update the input bytes read metric each time this number of records has been read */
  val RECORDS_BETWEEN_BYTES_READ_METRIC_UPDATES = 256

  /**
    * The three methods below are helpers for accessing the local map, a property of the SparkEnv of
    * the local process.
    */
  def getCachedMetadata(key: String): Any = SparkEnv.get.hadoopJobMetadata.get(key)

  def containsCachedMetadata(key: String): Boolean = SparkEnv.get.hadoopJobMetadata.containsKey(key)

  private def putCachedMetadata(key: String, value: Any): Unit =
    SparkEnv.get.hadoopJobMetadata.put(key, value)

  /** Add Hadoop configuration specific to a single partition and attempt. */
  def addLocalConfiguration(jobTrackerId: String, jobId: Int, splitId: Int, attemptId: Int,
                            conf: JobConf) {
    val jobID = new JobID(jobTrackerId, jobId)
    val taId = new TaskAttemptID(new TaskID(jobID, true, splitId), attemptId)

    conf.set("mapred.tip.id", taId.getTaskID.toString)
    conf.set("mapred.task.id", taId.toString)
    conf.setBoolean("mapred.task.is.map", true)
    conf.setInt("mapred.task.partition", splitId)
    conf.set("mapred.job.id", jobID.toString)
  }

  private[spark] class SplitInfoReflections {
    val inputSplitWithLocationInfo =
      Utils.classForName("org.apache.hadoop.mapred.InputSplitWithLocationInfo")
    val getLocationInfo = inputSplitWithLocationInfo.getMethod("getLocationInfo")
    val newInputSplit = Utils.classForName("org.apache.hadoop.mapreduce.InputSplit")
    val newGetLocationInfo = newInputSplit.getMethod("getLocationInfo")
    val splitLocationInfo = Utils.classForName("org.apache.hadoop.mapred.SplitLocationInfo")
    val isInMemory = splitLocationInfo.getMethod("isInMemory")
    val getLocation = splitLocationInfo.getMethod("getLocation")
  }

  private[spark] val SPLIT_INFO_REFLECTIONS: Option[SplitInfoReflections] = try {
    Some(new SplitInfoReflections)
  } catch {
    case e: Exception =>
      logDebug("SplitLocationInfo and other new Hadoop classes are " +
        "unavailable. Using the older Hadoop location info code.", e)
      None
  }

  private[spark] def convertSplitLocationInfo(infos: Array[AnyRef]): Seq[String] = {
    val out = ListBuffer[String]()
    infos.foreach { loc => {
      val locationStr = NewDirRDD.SPLIT_INFO_REFLECTIONS.get.
        getLocation.invoke(loc).asInstanceOf[String]
      if (locationStr != "localhost") {
        if (NewDirRDD.SPLIT_INFO_REFLECTIONS.get.isInMemory.
          invoke(loc).asInstanceOf[Boolean]) {
          logDebug("Partition " + locationStr + " is cached by Hadoop.")
          out += new HDFSCacheTaskLocation(locationStr).toString
        } else {
          out += new HostTaskLocation(locationStr).toString
        }
      }
    }}
    out.seq
  }
}

private class MultiPathFilter(var filters: java.util.List[PathFilter]) extends PathFilter {
  def accept(path: Path): Boolean = {
    val i$ = this.filters.iterator
    var filter:PathFilter = null
    do {
      if (!i$.hasNext) return true
      filter = i$.next
    } while (filter.accept(path))
    false
  }
}
