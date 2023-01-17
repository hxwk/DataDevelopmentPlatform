package com.dfssi.spark.common;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class HDFSCommon {
	 
	 private static final Logger LOG = LoggerFactory.getLogger(HDFSCommon.class);
	 
	 private static FileSystem fs;
     
	 private HDFSCommon(){}
	 
	 public static FileSystem getFileSystem() throws IOException{
		 
		 if(fs == null){
			 synchronized (HDFSCommon.class) {
				 if(fs == null){
					 Configuration conf = new Configuration();
			        fs = FileSystem.get(conf);
				 }
			}
		 }
		 return fs;
	 }
	 
     //创建新文件
     public static void createFile(String dst , byte[] contents) throws IOException{
    	 
          FileSystem fs = getFileSystem();
          Path dstPath = new Path(dst); //目标路径
          //打开一个输出流
          FSDataOutputStream outputStream = fs.create(dstPath);
          outputStream.write(contents);
          outputStream.close();
          fs.close();
          LOG.info(String.format("文件: %s 创建成功！", dst));
      }
      
      //上传本地文件
     public static void uploadFile(String src,String dst) throws IOException{
    	 
          FileSystem fs = getFileSystem();
          Path srcPath = new Path(src); //原路径
          Path dstPath = new Path(dst); //目标路径
          //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
          fs.copyFromLocalFile(false,srcPath, dstPath);
          
          //打印文件路径
          LOG.info(String.format("Upload %s to %s ok", src, dst));
         
          fs.close();
      }
     
     public static void getFromHDFS(String src , String dst) throws IOException{  
    	 
	      FileSystem fs = getFileSystem();
          Path dstPath = new Path(dst) ;  
          fs.copyToLocalFile(false, new Path(src), dstPath);  
          
          LOG.info(String.format("down %s to %s ok", src, dst));
          
          fs.close();
     }  
      
      //文件重命名
      public static void rename(String oldName,String newName) throws IOException{
    	  
          FileSystem fs = getFileSystem();
          Path oldPath = new Path(oldName);
          Path newPath = new Path(newName);
          boolean isok = fs.rename(oldPath, newPath);
          if(isok){
        	  LOG.info(String.format("rename file : %s to %s ok!", oldName, newName));
          }else{
        	  LOG.error(String.format("rename file : %s to %s failure!", oldName, newName));
          }
          fs.close();
      }
      
      //删除文件
      public static void delete(String filePath) throws IOException{
         
          FileSystem fs = getFileSystem();
          Path path = new Path(filePath);
          boolean isok = fs.delete(path, true);
          if(isok){
        	  LOG.info(String.format("delete file : %s ok!", filePath));
          }else{
        	  LOG.error(String.format("delete file : %s failure!", filePath));
          }
         
      }
      
      //创建目录
      public static void mkdir(String path) throws IOException{
         
          FileSystem fs = getFileSystem();
          Path srcPath = new Path(path);
          boolean isok = fs.mkdirs(srcPath);
          if(isok){
        	  LOG.info(String.format("create dir : %s ok!", path));
          }else{
        	  LOG.error(String.format("create dir : %s failure!", path));
          }
          fs.close();
      }
      
     //读取文件的内容
     public static void readFile(String filePath) throws IOException{
    	 
          FileSystem fs = getFileSystem();
          Path srcPath = new Path(filePath);
          InputStream in = null;
         try {
              in = fs.open(srcPath);
             IOUtils.copyBytes(in, System.out, 4096, false); //复制到标准输出流
         } finally {
             IOUtils.closeStream(in);
         }
     }
 
 }