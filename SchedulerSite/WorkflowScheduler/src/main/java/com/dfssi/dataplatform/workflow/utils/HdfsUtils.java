package com.dfssi.dataplatform.workflow.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.net.URI;


public class HdfsUtils {
    /**
     * upload the local file to the hds
     * notice that the path is full like /tmp/test.c
     */
    public static void copyLocalFileToHdfs(String nameNode, String localPath, String hdfsPath, String userName)
            throws Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);

        Path srcPath = new Path(localPath);
        Path destPath = new Path(hdfsPath);
        fs.copyFromLocalFile(srcPath, destPath);
        fs.close();
    }

    /**
     * read the hdfs file content
     * notice that the dst is the full path name
     */
    public static byte[] readHdfsFile(String nameNode, String destPath, String userName) throws Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);
        Path path = new Path(destPath);
        if (fs.exists(path)) {
            FSDataInputStream is = fs.open(path);
            // get the file info to create the buffer
            FileStatus stat = fs.getFileStatus(path);
            // create the buffer
            byte[] buffer = new byte[Integer.parseInt(String.valueOf(stat.getLen()))];
            is.readFully(0, buffer);

            is.close();
            fs.close();

            return buffer;
        } else {
            throw new Exception("the file is not found .");
        }
    }

    public static void mkdir(String nameNode, String path, String userName) throws Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);
        fs.mkdirs(new Path(uri));
        fs.close();
    }


    public static void deleteDir(String nameNode, String path, String userName) throws Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);
        fs.delete(new Path(path));
        fs.close();
    }

    public static boolean existPath(String nameNode, String outputStr, String filePath, String userName) throws
            Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);

        Path path = new Path(filePath);
        return fs.exists(path);
    }

    public static void writeHdfsFile(String nameNode, String outputStr, String filePath, String userName) throws
            Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);

        Path path = new Path(filePath);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }

        fs.createNewFile(path);
        FSDataOutputStream os = fs.create(path);
        os.write(outputStr.getBytes("UTF-8"));
        os.flush();
        os.close();
        fs.close();
    }

    /**
     * overide write
     *
     * @param nameNode
     * @param outputStr
     * @param filePath
     * @param userName
     * @throws Exception
     */
    public static void writeHdfsFile(String nameNode, byte[] outputStr, String filePath, String userName) throws
            Exception {
        URI uri = new URI(nameNode);
        FileSystem fs = FileSystem.get(uri, getConf(), userName);

        Path path = new Path(filePath);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }

        fs.createNewFile(path);
        FSDataOutputStream os = fs.create(path);
        os.write(outputStr);
        os.flush();
        os.close();
        fs.close();
    }

    public static FileStatus[] browseCatalog(String nameNode, String libPath, String userName) throws Exception {
        URI uri = new URI(nameNode);
        FileSystem fileSystem = FileSystem.get(uri, getConf(), userName);
        Path path = new Path("/user/hdfs/bigdata/dev/ext");
        FSDataInputStream is = fileSystem.open(path);
        FileStatus[] fileStatuses = fileSystem.listStatus(path);
//        System.out.println("文件根目录: "+fileStatus.getPath());
//        System.out.println("这文件目录为：");
//        for(FileStatus fs : fileSystem.listStatus(path)){
//            System.out.println(fs.getPath());
//        }
        return fileStatuses;
    }

    public static Configuration getConf() {
        Configuration conf = new Configuration();
        return conf;
    }
}
