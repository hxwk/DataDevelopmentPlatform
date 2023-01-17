package com.dfssi.spark.hdfs;


import com.dfssi.common.Dates;
import com.dfssi.common.file.Files;
import com.dfssi.spark.filter.HdfsFileFilter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/11/5 16:04
 */
public class HdfsAdmin {

    private Logger LOG;

    private static final String HDFS_SITE = "/etc/hadoop/conf.cloudera.hdfs/hdfs-site.xml";
    private static final String CORE_SITE = "/etc/hadoop/conf.cloudera.hdfs/core-site.xml";

    private volatile static HdfsAdmin hdfsAdmin;

    private  FileSystem fs;

    private HdfsAdmin(Configuration conf, Logger logger){
        try {
            if(conf == null) conf = newConf();
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            LOG.error("获取 hdfs的FileSystem出现异常。", e);
        }
        Preconditions.checkNotNull(fs, "没有获取到可用的Hdfs的FileSystem");
        this.LOG = logger;
        if(this.LOG == null)
            this.LOG = Logger.getLogger(HdfsAdmin.class);
    }

    private Configuration newConf(){

        Configuration conf = new Configuration();
/*      conf.set("fs.defaultFS", "hdfs://nameservice1");
        conf.set("dfs.nameservices", "nameservice1");
        conf.set("dfs.ha.namenodes.nameservice1", "namenode216,namenode217");
        conf.set("dfs.namenode.rpc-address.nameservice1.namenode216", "BH-LAN-Virtual-hadoop-1:8020");
        conf.set("dfs.namenode.rpc-address.nameservice1.namenode217", "BH-LAN-Virtual-hadoop-3:8020");
        conf.set("dfs.client.failover.proxy.provider.nameservice1",
         "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");*/
        if(Files.exist(HDFS_SITE)) conf.addResource(HDFS_SITE);
        if(Files.exist(CORE_SITE)) conf.addResource(CORE_SITE);

        return conf;
    }


    public static HdfsAdmin get(){
        return get(null);
    }

    public static HdfsAdmin get(Logger logger){
        if(hdfsAdmin == null){
            synchronized (HdfsAdmin.class){
                if(hdfsAdmin == null) hdfsAdmin = new HdfsAdmin(null, logger);
            }
        }
        return hdfsAdmin;
    }

    public static HdfsAdmin get(Configuration conf, Logger logger){
        if(hdfsAdmin == null){
            synchronized (HdfsAdmin.class){
                if(hdfsAdmin == null) hdfsAdmin = new HdfsAdmin(conf, logger);
            }
        }
        return hdfsAdmin;
    }

    public FileStatus getFileStatus(String dir) {
        FileStatus fileStatus = null;
        try {
            fileStatus = fs.getFileStatus(new Path(dir));
        } catch (IOException e) {
            LOG.error(String.format("获取文件 %s信息失败。", dir), e);
        }
        return fileStatus;
    }

    public void createFile(String dst , byte[] contents){

        Path dstPath = new Path(dst); //目标路径
        //打开一个输出流
        FSDataOutputStream outputStream;
        try {
            outputStream = fs.create(dstPath);
            outputStream.write(contents);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            LOG.error(String.format("创建文件 %s 失败。", dst), e);
        }
        LOG.info(String.format("文件: %s 创建成功！", dst));
    }

    //上传本地文件
    public void uploadFile(String src,String dst){
        Path srcPath = new Path(src); //原路径
        Path dstPath = new Path(dst); //目标路径
        //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
        try {
            fs.copyFromLocalFile(false,srcPath, dstPath);
        } catch (IOException e) {
            LOG.error(String.format("上传文件 %s 到 %s 失败。", src, dst), e);
        }
        //打印文件路径
        LOG.info(String.format("上传文件 %s 到 %s 完成。", src, dst));
    }

    public void downloadFile(String src , String dst){
        Path dstPath = new Path(dst) ;
        try {
            fs.copyToLocalFile(false, new Path(src), dstPath);
        } catch (IOException e) {
            LOG.error(String.format("下载文件 %s 到 %s 失败。", src, dst), e);
        }
        LOG.info(String.format("下载文件 %s 到 %s 完成", src, dst));
    }

    //文件重命名
    public void rename(String oldName,String newName){

        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        boolean isok = false;
        try {
            isok = fs.rename(oldPath, newPath);
        } catch (IOException e) {
            LOG.error(String.format("重命名文件 %s 为 %s 失败。", oldName, newName), e);
        }
        if(isok){
            LOG.info(String.format("重命名文件 %s 为 %s 完成。", oldName, newName));
        }else{
            LOG.error(String.format("重命名文件 %s 为 %s 失败。", oldName, newName));
        }
    }

    public void delete(String path){
        delete(path, true);
    }


    //删除文件
    public void delete(String path, boolean recursive){

        Path deletePath = new Path(path);
        boolean isok = false;
        try {
            isok = fs.delete(deletePath, recursive);
        } catch (IOException e) {
            LOG.error(String.format("删除文件 %s 失败。", path), e);
        }
        if(isok){
            LOG.info(String.format("删除文件 %s 完成。", path));
        }else{
            LOG.error(String.format("删除文件 %s 失败。", path));
        }

    }

    //创建目录
    public void mkdir(String path){

        Path srcPath = new Path(path);
        boolean isok = false;
        try {
            isok = fs.mkdirs(srcPath);
        } catch (IOException e) {
            LOG.error(String.format("创建目录 %s 失败。", path), e);
        }
        if(isok){
            LOG.info(String.format("创建目录 %s 完成。", path));
        }else{
            LOG.error(String.format("创建目录 %s 失败。", path));
        }
    }

    //读取文件的内容
    public InputStream readFile(String filePath){

        Path srcPath = new Path(filePath);
        InputStream in = null;
        try {
           in = fs.open(srcPath);
        } catch (IOException e) {
            LOG.error(String.format("读取文件  %s 失败。", filePath), e);
        }
        return in;
    }

    public List<String> readLines(String filePath){
        return readLines(filePath, "UTF-8");
    }

    public  List<String> readLines(String filePath, String encoding){
        InputStream inputStream = readFile(filePath);
        List<String> lines = null;
        if(inputStream != null) {
            try {
                lines = IOUtils.readLines(inputStream, encoding);
            } catch (IOException e) {
                LOG.error(String.format("按行读取文件 %s 失败。", filePath), e);
            }finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
        return lines;
    }

    public List<FileStatus> findNewFileOrDirInDir(String dir, HdfsFileFilter filter,
                                                final boolean onlyFile, final boolean onlyDir){
       return findNewFileOrDirInDir(dir, filter, onlyFile, onlyDir, false);
    }

    public List<FileStatus> findNewFileOrDirInDir(String dir, HdfsFileFilter filter,
                          final boolean onlyFile, final boolean onlyDir, boolean recursive){

        if(onlyFile && onlyDir){
            FileStatus fileStatus = getFileStatus(dir);
            if(fileStatus == null)return Lists.newArrayList();
            if(isAccepted(fileStatus,filter)){
                return Lists.newArrayList(fileStatus);
            }
            return Lists.newArrayList();
        }

       if(onlyFile){
           return findNewFileInDir(dir, filter, recursive);
       }
       if(onlyDir){
           return findNewDirInDir(dir, filter, recursive);
       }

       return Lists.newArrayList();
    }

    /**
     * 查找一个文件夹中 新建的目录
     * @param dir
     * @param filter
     * @return
     */
    public List<FileStatus> findNewDirInDir(String dir, HdfsFileFilter filter){
        return findNewDirInDir(new Path(dir), filter, false);
    }
    public List<FileStatus> findNewDirInDir(Path path, HdfsFileFilter filter){
        return findNewDirInDir(path, filter, false);
    }

    public List<FileStatus> findNewDirInDir(String dir, HdfsFileFilter filter, boolean recursive){
        return findNewDirInDir(new Path(dir), filter, recursive);
    }

    public List<FileStatus> findNewDirInDir(Path path, HdfsFileFilter filter, boolean recursive){

        FileStatus[] files = null;
        try {
            files = fs.listStatus(path);
        } catch (IOException e) {
            LOG.error(String.format("获取目录 %s下的文件列表失败。", path), e);
        }
        if(files == null)return Lists.newArrayList();

        List<FileStatus> paths = Lists.newArrayList();
        List<String> res = Lists.newArrayList();
        for(FileStatus fileStatus : files){
            if (fileStatus.isDirectory()) {
                if (isAccepted(fileStatus, filter)) {
                    paths.add(fileStatus);
                    res.add(fileStatus.getPath().toString());
                }else if(recursive){
                    paths.addAll(findNewDirInDir(fileStatus.getPath(), filter, recursive));
                }
            }
        }
        LOG.info(String.format("从目录%s 找到满足条件%s 有如下 %s 个文件： %s",
                path, filter,res.size(), res));
        return paths;
    }

    /**
     * 查找一个文件夹中 新建的文件
     * @param dir
     * @param filter
     * @return
     */
    public List<FileStatus> findNewFileInDir(String dir, HdfsFileFilter filter){
        return  findNewFileInDir(new Path(dir), filter, false);
    }

    public List<FileStatus> findNewFileInDir(String dir, HdfsFileFilter filter, boolean recursive){
        return  findNewFileInDir(new Path(dir), filter, recursive);
    }

    public List<FileStatus> findNewFileInDir(Path path, HdfsFileFilter filter){
        return  findNewFileInDir(path, filter, false);
    }

    public List<FileStatus> findNewFileInDir(Path path, HdfsFileFilter filter, boolean recursive){

        FileStatus[] files = null;
        try {
            files = fs.listStatus(path);
        } catch (IOException e) {
            LOG.error(String.format("获取目录 %s下的文件列表失败。", path), e);
        }
        if(files == null)return Lists.newArrayList();

        List<FileStatus> paths = Lists.newArrayList();
        List<String> res = Lists.newArrayList();
        for(FileStatus fileStatus : files){
            if (fileStatus.isFile()) {
                if (isAccepted(fileStatus, filter)) {
                    paths.add(fileStatus);
                    res.add(fileStatus.getPath().toString());
                }
            }else if(recursive){
                paths.addAll(findNewFileInDir(fileStatus.getPath(), filter, recursive));
            }
        }
        LOG.info(String.format("从目录%s 找到满足条件%s 有如下 %s 个文件： %s",
                path, filter,res.size(), res));

        return paths;
    }

    private boolean isAccepted(String file, HdfsFileFilter filter) {
        if(filter == null) return true;
        FileStatus fileStatus = getFileStatus(file);
        if(fileStatus == null)return false;
        return isAccepted(fileStatus, filter);
    }

    private boolean isAccepted(FileStatus fileStatus, HdfsFileFilter filter) {
        return  filter == null ? true : filter.filter(fileStatus);
    }

    public long getModificationTime(Path path){
        try {
            FileStatus status = fs.getFileStatus(path);
            return status.getModificationTime();
        } catch (IOException e) {
            LOG.error(String.format("获取路径 %s信息失败。", path), e);
        }
        return -1L;
    }

    public FileSystem getFs() {
        return fs;
    }

    public static void main(String[] args) throws IOException {


        System.out.println(Dates.long2Str(1472623265383L));
        System.out.println(Dates.long2Str(1446986338000L));

        boolean b = (Long.MAX_VALUE >= 1472623265383L && 1446986338000L <= 1472623265383L);
        System.out.println(b);

        HdfsAdmin hdfsAdmin = HdfsAdmin.get();
        List<FileStatus> status = hdfsAdmin.findNewDirInDir("hdfs://hadoop-1:8020/D406/jieyang65/wifi", null);
        System.out.println("status = " + status.size());
    }
}
