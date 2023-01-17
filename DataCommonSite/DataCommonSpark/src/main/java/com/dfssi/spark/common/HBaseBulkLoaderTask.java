/**
  * Copyright (c) 2016, jechedo All Rights Reserved.
  *
 */
package com.dfssi.spark.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description:
 * 
 *  Date    2016-7-22 上午11:11:23   
 *                  
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
public class HBaseBulkLoaderTask implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(HBaseBulkLoaderTask.class);

	private String path;
	private Configuration conf;
	private HTable table;
	private FileSystem fs;
	private Connection connection;

	public HBaseBulkLoaderTask(String path, Configuration conf, HTable table, Connection connection){

		this(path, conf, table);
		this.connection = connection;
	}

	public HBaseBulkLoaderTask(String path, Configuration conf, HTable table){
		this.path = path;
		this.conf = conf;
		this.table = table;

		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			LOG.info(null, e);
		}

		conf.set("hbase.mapreduce.bulkload.max.hfiles.perRegion.perFamily", "128");
	}

	@Override
	public void run() {
		
		LoadIncrementalHFiles bulkLoader;

		Path hfofDir = new Path(path);
		try {
			changePermission(hfofDir, new FsPermission("777"));

			LOG.info(String.format("开始将HDFS下路径为 %s 的HFile加载到HBase表 %s 中。",
					path, table.getName().getNameAsString()));

			bulkLoader = new LoadIncrementalHFiles(conf);
			bulkLoader.doBulkLoad(hfofDir, table);

			LOG.info(String.format("将HDFS下路径为 %s 的HFile加载到HBase表 %s 中完成。",
					path, table.getName().getNameAsString()));
		} catch (Exception e) {
			LOG.error(String.format("将HDFS下路径为 %s 的HFile加载到HBase表 %s 中失败。",
					path, table.getName().getNameAsString()), e);
		}finally{
			try {
				fs.delete(hfofDir, true);
				LOG.info(String.format("删除目录%s完成。", path));

				// clean HFileOutputFormat2 stuff
				fs.deleteOnExit(new Path(TotalOrderPartitioner.getPartitionFile(conf)));
				if(connection != null)connection.close();
			} catch (IOException e) {
				LOG.error(null, e);
			}
		}
	}

	private void changePermission(Path path, FsPermission fsPermission) throws IOException {

		FileStatus status;
		Path tmp;
		FileStatus[] fileStatuses = fs.listStatus(path);
		for(int i = 0; i < fileStatuses.length; i++){
			status = fileStatuses[i];
			tmp = status.getPath();

			fs.setPermission(tmp, fsPermission);
			if(status.isDirectory() && !tmp.getName().equals("_tmp")){
				FileSystem.mkdirs(fs, new Path(tmp, "_tmp"), fsPermission);
				changePermission(tmp, fsPermission);
			}
		}
	}

}
