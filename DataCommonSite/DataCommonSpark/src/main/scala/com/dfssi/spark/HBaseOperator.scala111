
/**
	* Copyright (c) 2016, jechedo All Rights Reserved.
	*
	*/
package com.dfssi.spark

import java.util.UUID

import com.dfssi.hbase.v2.HContext
import com.dfssi.spark.common.{HBaseBulkLoaderTask, HKV}
import com.dfssi.spark.partitioner.HFilePartitioner
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat2, TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hbase.{KeyValue, TableName}
import org.apache.hadoop.mapreduce.{Job, OutputFormat}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD


import scala.collection.JavaConversions
import scala.reflect.ClassTag

/**
	* Description:
	*
	*  date:   2016-5-17 上午10:45:33
	*
	* @author  LiXiaoCong
	* @version 1.0
	* @since   JDK 1.7
	*/
class HBaseOperator {

	def storeRDD[T: ClassTag](tableName:String , rdd:RDD[T] , convertor:(T) => (ImmutableBytesWritable, Put)){
		storeRDD(tableName , rdd.map(convertor))
	}

	def storeRDD(tableName:String  , rdd:RDD[(ImmutableBytesWritable, Put)]){

		//指定输出格式和输出表名
		val hConf = HContext.get().getConfiguration
		hConf.set(TableOutputFormat.OUTPUT_TABLE,tableName)
		hConf.setClass("mapreduce.job.outputformat.class", classOf[TableOutputFormat[String]], classOf[OutputFormat[String,Mutation]])

		rdd.saveAsNewAPIHadoopDataset(hConf)
	}

	def storeRDDWithBulkLoad(rdd: RDD[HKV], conf: Configuration, tableNameStr:
	String, cf:Array[Byte], numFilesPerRegionPerFamily: Int = 1) =
		HBaseBulkLoader.storeRDDWithBulkLoad(rdd, conf, tableNameStr, cf, numFilesPerRegionPerFamily)


	def readRDD(tableName:String , scan:Scan , sc:SparkContext):RDD[Result] = {
		val conf =  HContext.get().getConfiguration
		conf.set(TableInputFormat.INPUT_TABLE, tableName)

		conf.set(TableInputFormat.SCAN, createScanString(scan) )
		val rdd = sc.newAPIHadoopRDD(conf,
			classOf[TableInputFormat],
			classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
			classOf[org.apache.hadoop.hbase.client.Result])
		rdd.filter(!_._2.isEmpty()).map(_._2)
	}

	def readRDDMap(tableName:String , scan:Scan , sc:SparkContext):RDD[(String,Map[String,String])] = {

		val convertor = {result:Result =>
			val row = Bytes.toString(result.getRow())
			val record = JavaConversions.collectionAsScalaIterable(result.listCells())
				.map(cell =>{
					( Bytes.toString(cell.getQualifierArray() , cell.getQualifierOffset() , cell.getQualifierLength()) ,
						Bytes.toString(cell.getValueArray() , cell.getValueOffset() , cell.getValueLength()))
				}).toMap
			(row , record)
		}

		readRDD(tableName , scan , sc , convertor)
	}

	def readRDD[T: ClassTag](tableName:String , scan:Scan ,
													 sc:SparkContext , convertor:(Result) => T):RDD[T] = readRDD(tableName , scan , sc).map(convertor)

	private def createScanString(scan:Scan) : String = Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray())

}

object HBaseOperator{

	def apply() = new HBaseOperator

	def newInstance = new HBaseOperator
}


object HBaseBulkLoader{

	def storeRDDWithBulkLoad(rdd: RDD[HKV], conf: Configuration, tableNameStr:
	String, cf:Array[Byte], numFilesPerRegionPerFamily: Int){

		require(numFilesPerRegionPerFamily > 0)

		val tableName = TableName.valueOf(tableNameStr)
		val connection = ConnectionFactory.createConnection(conf)
		val regionLocator = connection.getRegionLocator(tableName)
		val table = connection.getTable(tableName)

		val bulkData = getHFilePartitiondRdd(rdd.map(_.toPair), cf,
			HFilePartitioner(conf, regionLocator.getStartKeys, numFilesPerRegionPerFamily))

		conf.set(TableOutputFormat.OUTPUT_TABLE, tableNameStr)
		saveAsHFile(bulkData, conf, table.asInstanceOf[HTable], regionLocator, connection)

	}

	private def saveAsHFile(rdd: RDD[(ImmutableBytesWritable, KeyValue)], conf: Configuration,
													table: HTable, regionLocator: RegionLocator, connection: Connection) = {

		val job = Job.getInstance(conf, this.getClass.getName.split('$')(0))
		job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
		job.setMapOutputValueClass(classOf[KeyValue])

		HFileOutputFormat2.configureIncrementalLoad(job, table, regionLocator)

		val path = "/tmp/" + table.getName.getQualifierAsString + "_" + UUID.randomUUID()

		rdd.saveAsNewAPIHadoopFile(path, classOf[ImmutableBytesWritable],
			classOf[KeyValue], classOf[HFileOutputFormat2], job.getConfiguration)

		new HBaseBulkLoaderTask(path, conf, table, connection).run();
	}

	private	def getHFilePartitiondRdd[C: ClassTag, A: ClassTag](rdd: RDD[(C, A)],
																															 family: Array[Byte], partitioner: HFilePartitioner)(implicit ord: Ordering[C]) = {

		rdd.repartitionAndSortWithinPartitions(partitioner)
			.map { case (cell: ((Array[Byte], Array[Byte]), Long), value: Array[Byte]) =>
				(new ImmutableBytesWritable(cell._1._1), new KeyValue(cell._1._1, family, cell._1._2, cell._2, value)) }
	}

	implicit val ordForBytes = new math.Ordering[Array[Byte]] {
		def compare(a: Array[Byte], b: Array[Byte]): Int = {
			Bytes.compareTo(a, b)
		}
	}

	implicit val ordForLong = new math.Ordering[Long] {
		def compare(a: Long, b: Long): Int = {
			if (a < b) {
				1
			} else if (a > b) {
				-1
			} else {
				0
			}
		}
	}


}

