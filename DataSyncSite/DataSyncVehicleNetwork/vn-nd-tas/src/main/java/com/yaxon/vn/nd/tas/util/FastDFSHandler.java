package com.yaxon.vn.nd.tas.util;

import com.yaxon.vn.nd.tas.util.csource.common.MyException;
import com.yaxon.vn.nd.tas.util.csource.common.NameValuePair;
import com.yaxon.vn.nd.tas.util.csource.fastdfs.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * FastDFS server Handler upload download
 * @author jianKang
 * @date 2018/01/26
 */
public class FastDFSHandler {
    final static Logger logger = LoggerFactory.getLogger(FastDFSHandler.class);
    //static String conf_filename = "F:\\fastdfs-client.properties";
    static String conf_filename ;//= "F:\\fdfs_client.conf";
    static String localFilePath = System.getProperties().getProperty("user.home")+File.separatorChar+"dbcfiles";
    static final String AT = "@";

    private static  StorageClient1 client =null;
    private static TrackerServer trackerServer;
    static {
            try {
                String classRootPath = FastDFSHandler.class.getClassLoader().getResource("").getFile();
                conf_filename = classRootPath+"fastdfs-client.properties";
                ClientGlobal.init(conf_filename);
                System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
                System.out.println("charset=" + ClientGlobal.g_charset);

                TrackerClient tracker = new TrackerClient();
                trackerServer = tracker.getConnection();
                StorageServer storageServer = null;
                client = new StorageClient1(trackerServer, storageServer);

            } catch (Exception ex) {
                logger.error("StorageClient1 error:{}",ex);
                /*try {
                    trackerServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
    }

    private StorageClient1 getConnFastDFSClient() {
        return client;
    }

    /*public static void main(String[] args) {
        String localFilePath = "D:\\t38.dbc";
        String path = write2FastDFSServer(localFilePath);
        logger.info("path:{}",path);
        String fileId = "group1/M00/00/A2/rBAByVqP0DmAazCPAAAY9ktMiAQ359.dbc";
        download2LocalWithFileId(fileId);
    }*/

    /**
     * download to local user.home by FastDFS fileId
     * @param fileId with FastDFS server
     */
    public static void download2Local(String fileId){
        File file;
        try {
            NameValuePair[] nameValuePairs = client.get_metadata1(fileId);
            logger.info("name "+nameValuePairs[0].getName()+" value " + getFileNameWithSuffix(nameValuePairs[0].getValue()));
            String fileName = getFileNameWithSuffix(nameValuePairs[0].getValue());
            System.out.println(localFilePath+File.separatorChar+fileName);
            file = new File(localFilePath+File.separatorChar+fileName);
            try {
                byte[] result = client.download_file1(fileId);
                FileUtils.writeByteArrayToFile(file,result);
            }catch (Exception ex){
                logger.error("download2Local error:{}",ex);
            }
        } catch (IOException e) {
            logger.error("download2Local IOException error:{}",e.getMessage());
        } catch (MyException e) {
            logger.error("download2Local MyException error:{}",e.getMessage());
        }
    }

    /**
     * download to local user.home by FastDFS fileId
     * 说白了 就是类似 C:\Users\jian\dbcfiles\rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc
     * rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc 这个名字是从fileId抠出来
     * @param fileId with FastDFS server
     */
    public static void download2LocalWithFileId(String fileId){
        File file;
        String fileIdName=null;
        try {
            System.out.println(localFilePath+File.separatorChar+fileId);
            if(null!=fileId&& !StringUtils.EMPTY.equals(fileId)){
                fileIdName =fileId.substring(fileId.lastIndexOf("/")+1);
            }
            file = new File(localFilePath+File.separatorChar+fileIdName);
            try {
                byte[] result = client.download_file1(fileId);
                FileUtils.writeByteArrayToFile(file,result);
            }catch (Exception ex){
                logger.error("download2Local error:{}",ex);
            }
        } catch (Exception e) {
            logger.error("download2Local IOException error:{}", e.getMessage());
        }
    }

    /**
     * write dbc to fastDFS
     * @return fileid/fileName
     * group1/M00/00/A2/rBAByVppifeAY964AAAGMXIlLc4291.dbc@zhangsan.dbc
     */
    private static String write2FastDFSServer(String localFileName){
        String result=null;
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", localFileName);
        String fileId = null;
        try {
            fileId = client.upload_file1(localFileName, null, metaList);
            if(fileId!=null){
                logger.debug("success");
                result = fileId + AT +localFileName;
            }else{
                logger.error("error");
            }
        } catch (IOException e) {
            logger.error("write2FastDFSServer IOException:{}",e);
        } catch (MyException e) {
            logger.error("write2FastDFSServer MyException:{}",e);
        }finally {

        }
        return result;
    }

    /**
     * get fileName with suffix
     * @param pathandname
     * @return String
     */
    public static String getFileNameWithSuffix(String pathandname) {
        int start = pathandname.lastIndexOf(File.separatorChar);
        if (-1 != start) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }
}
