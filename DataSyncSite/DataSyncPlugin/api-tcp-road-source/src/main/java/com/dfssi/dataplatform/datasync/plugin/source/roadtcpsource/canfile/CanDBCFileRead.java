package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile;

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
    //private Properties prop = null;
    //private String charset;
    //private String dbcfile;
    private final static Logger logger = LoggerFactory.getLogger(CanDBCFileRead.class);
    private FileInputStream fileInputStream;

    public CanDBCFileRead(File dbcNameFile) {
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
        /*if(null == dbcfile){
            dbcfile = CanConstants.dbcName;
            logger.info("dbc file:{}",dbcfile);
        }
        if(null == charset){
            charset = CanConstants.charSet;
            logger.debug("charset:{}",charset);
        }*/
        //当前文件不等于上次的文件就下载

        /*if(!FileUtil.isSameFile(CanConstants.localFilePath + File.separatorChar + CanConstants.dbcName,
                CanConstants.localFilePath + File.separatorChar + CanConstants.currentDbcName)){
            file = new File(CanConstants.localFilePath + File.separatorChar + CanConstants.currentDbcName);
        }*/
        //dbc file is in the local properties
        //file = new File(CanConstants.localFilePath + File.separatorChar + dbcfile);
        try {
            //fileInputStream = FileUtils.openInputStream(file);
            fileInputStream = FileUtils.openInputStream(dbcNameFile);
        } catch (IOException e) {
            logger.error("CanDBCFileRead dbcFile not exists or open stream exception.");
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
            brd = IOUtils.lineIterator(fileInputStream, Charsets.toCharset(CanConstants.charSet));
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
