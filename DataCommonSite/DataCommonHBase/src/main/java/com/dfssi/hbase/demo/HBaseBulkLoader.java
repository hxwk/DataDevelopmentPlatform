/**
  * Copyright (c) 2016, jechedo All Rights Reserved.
  *
 */
package com.dfssi.hbase.demo;

import com.dfssi.hbase.v2.HContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
public class HBaseBulkLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(HBaseBulkLoader.class);

	private String path;
	private Configuration conf;
	private HTable table;
	private FileSystem fs;
	private LoadIncrementalHFiles bulkLoader;

	public HBaseBulkLoader(String path, Configuration conf, HTable table){
		this.path = path;
		this.conf = conf;
		this.table = table;
		conf.set("hbase.mapreduce.bulkload.max.hfiles.perRegion.perFamily", "128");

		try {
			fs = FileSystem.get(conf);
			bulkLoader = new LoadIncrementalHFiles(conf);
		} catch (Exception e) {
			LOG.info(null, e);
		}
	}


	public void load(boolean extract) throws IOException {
		Path hfilePath = new Path(this.path);
		if(extract){

			FileStatus[] fileStatuses = fs.listStatus(hfilePath);
			for(FileStatus fileStatus : fileStatuses){
				load(fileStatus.getPath());
			}
		}else {
			load(hfilePath);
		}
	}

	private void load(Path hfile) {
		try {

			LOG.info(String.format("开始将HDFS下路径为 %s 的HFile加载到HBase表 %s 中。",
					path, table.getName().getNameAsString()));

			bulkLoader.doBulkLoad(hfile, table);

			LOG.info(String.format("将HDFS下路径为 %s 的HFile加载到HBase表 %s 中完成。",
					path, table.getName().getNameAsString()));

		} catch (Exception e) {
			LOG.error(String.format("将HDFS下路径为 %s 的HFile加载到HBase表 %s 中失败。",
					path, table.getName().getNameAsString()), e);
		}finally{
			try {
				fs.delete(hfile, true);
				LOG.info(String.format("删除目录%s完成。", hfile));
				fs.deleteOnExit(new Path(TotalOrderPartitioner.getPartitionFile(conf)));
			} catch (IOException e) {
				LOG.error(null, e);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		String path = args[0];
		boolean extract = (args.length > 2);

		HContext hContext = HContext.get();
		HTable hTable = hContext.newHTable(args[1]);

		Configuration configuration = hContext.getConfiguration();

		HBaseBulkLoader hBaseBulkLoader = new HBaseBulkLoader(path, configuration, hTable);
		hBaseBulkLoader.load(extract);

	}

}
