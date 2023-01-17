package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * file tools
 * @author jianKang
 * @date 2017/12/20
 */
public class FileUtils {
    static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 判断文件是否存在
     */
    public static void judeFileExists(File file) {
        if (file.exists()) {
            logger.info("file exists");
        } else {
            logger.info("file not exists, create it ...");
            try {
                if(file.createNewFile()) {
                    org.apache.commons.io.FileUtils.write(file,"{\"LastIndex\":\"1\"}");
                    logger.info("create file success...");
                }else{
                    logger.warn("create file error!");
                }
            } catch (IOException e) {
                logger.error("创建文件有错",e.getMessage());
            }
        }
    }

    /**
     * 判断文件夹是否存在
     * @param file
     */
    public static void judeDirExists(File file) {

        if (file.exists()) {
            if (file.isDirectory()) {
                logger.info("directory exists");
            } else {
                logger.info("the same name file exists, can not create directory");
            }
        } else {
           logger.info("directory not exists, create it ...");
            if(file.mkdirs()){
                logger.info("create directory success...");
            }else{
                logger.warn("create directory failed!");
            }
        }
    }
}
