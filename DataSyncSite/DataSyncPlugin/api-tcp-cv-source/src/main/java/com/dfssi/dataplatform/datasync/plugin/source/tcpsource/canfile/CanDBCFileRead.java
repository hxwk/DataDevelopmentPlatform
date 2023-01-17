package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * x0705 CAN T38 DBC format file parse
 * @author jianKang
 * @date 2017/12/22
 * @update 2018/02/01~2018/02/05
 * @update content: read dbc file dynamically
 */
public class CanDBCFileRead {
    static final Logger logger = LoggerFactory.getLogger(CanDBCFileRead.class);
    private static FileInputStream fileInputStream = null;
    //private Properties prop = null;
    private String charset;
    private String dbcfile;
    private File file;

    public CanDBCFileRead() {
        //load local dbc.properties to dbcfile and charset
        /*String classRootPath = this.getClass().getClassLoader().getResource("").getFile();
        prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(classRootPath, "dbc.properties")));
        } catch (IOException e) {
            logger.error(null, e);
        }
        dbcfile = prop.getProperty("dbcfile");
        charset = prop.getProperty("charset");*/
        if(null == dbcfile){
            dbcfile = CanConstants.fileIdName;
            logger.info("dbcfile:{}",dbcfile);
        }
        if(null == charset){
            charset = CanConstants.charSet;
            logger.info("charset:{}",charset);
        }
        file = new File(CanConstants.localFilePath + File.separatorChar + CanConstants.fileIdName);
        //dbc file is in the local properties
        //file = new File(CanConstants.localFilePath + File.separatorChar + dbcfile);
        try {
            fileInputStream = FileUtils.openInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read DBC file ,read valid data to deque
     * @return deque
     */
    public Queue<String> readValidData(){
        Queue<String> validData = new LinkedBlockingQueue<>();
        LineIterator brd = null;
        String line;
        try {
            brd = IOUtils.lineIterator(fileInputStream, Charsets.toCharset(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(brd.hasNext()){
            line = brd.nextLine();
            if(getStringByPrefixChars(line)){
                validData.add(line);
            }
        }
        return validData;
    }

    /**
     * filter BO_ or SG_
     * @param line
     * @return boolean
     */
    private boolean getStringByPrefixChars(String line){
        boolean flag =false;
        String regex = "^(BO_|\\s+SG_).*";
        if(line.matches(regex)){
            flag = true;
        }
        return flag;
    }
}
