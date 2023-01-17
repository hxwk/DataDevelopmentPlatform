package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class hdfs {
    static final Logger logger = LoggerFactory.getLogger(hdfs.class);
    /**
     * @author dcx by 2015.11.19
     * 新建文件
     * @param dst
     * @param conf
     * @return
     */
    public static boolean CreatDir(String dst , Configuration conf){
        Path dstPath = new Path(dst) ;
        try{
            FileSystem dhfs = FileSystem.get(conf);
            dhfs.mkdirs(dstPath);
        }
        catch(IOException ie){
            logger.error("hdfs filesystem get error, please modify it......");
            return false ;
        }
        return true ;
    }

    /**
     * @author dcx by 2015.11.19
     * 文件上传
     * @param src
     * @param dst
     * @param conf
     * @return
     */
    public static boolean putToHDFS(String src , String dst , Configuration conf){
        Path dstPath = new Path(dst) ;
        try{
            FileSystem hdfs = dstPath.getFileSystem(conf) ;
            hdfs.copyFromLocalFile(false, new Path(src), dstPath) ;
        }
        catch(IOException ie){
            logger.error("hdfs put to hdfs error, please modify it......");
            return false ;
        }
        return true ;
    }

    /**
     *  @author dcx by 2015.11.19
     * 文件下载
     * @param src
     * @param dst
     * @param conf
     * @return
     */
    public static boolean getFromHDFS(String src , String dst , Configuration conf){
        Path dstPath = new Path(dst) ;
        try{
            FileSystem dhfs = dstPath.getFileSystem(conf) ;
            dhfs.copyToLocalFile(false, new Path(src), dstPath) ;
        }catch(IOException ie){
            logger.error("hdfs copy to local file get error, please modify it......");
            return false ;
        }
        return true ;
    }

    /**
     * @author dcx by 2015.11.19
     * 文件删除
     * @param path
     * @param conf
     * @return
     */
    public static boolean checkAndDel(final String path , Configuration conf){
        Path dstPath = new Path(path) ;
        try{
            FileSystem dhfs = dstPath.getFileSystem(conf) ;
            if(dhfs.exists(dstPath)){
                dhfs.delete(dstPath, true) ;
            }else{
                return false ;
            }
        }catch(IOException ie ){
            logger.error("hdfs delete get error, please modify it......");
            return false ;
        }
        return true ;
    }

    /**
     * 主函数测试
     * @param args
     *
     */
    public static void main(String[] args) {

        boolean status = false ;
        String dst1 = "hdfs://172.16.1.210:8020/tmp/ssi/oilprice2.txt" ;
        Configuration conf = new Configuration() ;

        //java.lang.IllegalArgumentException:
        // Wrong FS: hdfs://172.16.1.210:9000/EBLearn_data/hello.txt, expected: file:///
        //解决这个错误的两个方案：
        //方案1：下面这条命令必须加上，否则出现上面这个错误
        conf.set("fs.default.name", "hdfs://172.16.1.210:8020"); // "hdfs://master:9000"
        //方案2： 将core-site.xml 和hdfs-site.xml放入当前工程中
        status = CreatDir(dst1, conf) ;
        System.out.println("status="+status) ;

        String dst = "hdfs://172.16.1.210:8020/tmp/ssi/oilprice2.txt" ;
        String src = "D:/test/data/ds-oil-2017113046481.dat" ;

        status = putToHDFS(src, dst, conf) ;
        System.out.println("status="+status) ;

/*        src = "hdfs://172.16.1.210:9000/EBLearn_data/hello.txt" ;
        dst = "I:/hadoop_need/" ;
        status = getFromHDFS( src ,  dst ,  conf) ;
        System.out.println("status="+status) ;*/

/*        dst = "hdfs://172.16.1.210:9000/EBLearn_data/hello.txt" ;
        status = checkAndDel( dst ,  conf) ;
        System.out.println("status="+status) ;*/
    }
}