package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util;

import com.dfssi.dataplatform.datasync.common.common.MyException;
import com.dfssi.dataplatform.datasync.common.common.NameValuePair;
import com.dfssi.dataplatform.datasync.common.fastdfs.*;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * FastDFS server Handler upload download
 * @author jianKang
 * @date 2018/01/26
 */
public class FastDFSHandler {
    final static Logger logger = LoggerFactory.getLogger(FastDFSHandler.class);

    static String localFilePath = System.getProperties().getProperty("user.home")+File.separatorChar+"dbcfiles";

    private static StorageClient1 client =null;
    private static TrackerServer trackerServer;

    private static FastDFSHandler instance;

    private FastDFSHandler() {
        try {
            FastDFSEnviroment.init();

            TrackerClient tracker = new TrackerClient();
            trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            client = new StorageClient1(trackerServer, storageServer);

        } catch (Exception ex) {
            logger.error("StorageClient1 error:{}",ex);
        }
    }

    public synchronized static FastDFSHandler getInstance() {

        if (null == instance) {
            instance = new FastDFSHandler();
        }

        return instance;
    }

    private StorageClient1 getConnFastDFSClient() {
        return client;
    }

    public static void main(String[] args) {
        String localFilePath = "D:\\spf1.dbc";
//                String localFilePath = "D:\\data\\newapp";
        String path = FastDFSHandler.getInstance().write2FastDFSServer(localFilePath);
        logger.info("upload path>>: {}",path);
    }

    /**
     * write dbc to fastDFS
     * @return fileid/fileName
     * group1/M00/00/A2/rBAByVppifeAY964AAAGMXIlLc4291.dbc@zhangsan.dbc
     */
    public String write2FastDFSServer(String localFileName){
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", localFileName);
        String fileId = null;
        try {
            fileId = client.upload_file1(localFileName, null, metaList);
            if(fileId!=null){
                logger.debug(" 上次流媒体到fastdfs  success");
            }else{
                logger.error("上次流媒体到fastdfs error");
            }
        } catch (IOException e) {
            logger.error("write2FastDFSServer IOException:{}",e);
        } catch (MyException e) {
            logger.error("write2FastDFSServer MyException:{}",e);
        }finally {

        }
        return fileId;
    }


    public static byte[] readFile(String fileId){
        byte[] result=null;
        try {
             result = client.download_file1(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * download to local user.home by FastDFS fileId
     * 说白了 就是类似 C:\Users\jian\dbcfiles\rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc
     * ~/dbcfiles/rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc
     * rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc 这个名字是从fileId出来
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
                FastDFSHandler.getInstance();
                byte[] result = client.download_file1(fileId);

                FileUtils.writeByteArrayToFile(file,result);
                CanConstants.fileIdName=fileIdName;
            }catch (Exception ex){
                logger.error("download2Local error:{}",ex);
            }
        } catch (Exception e) {
            logger.error("download2Local IOException error:{}", e.getMessage());
        }
    }

}
