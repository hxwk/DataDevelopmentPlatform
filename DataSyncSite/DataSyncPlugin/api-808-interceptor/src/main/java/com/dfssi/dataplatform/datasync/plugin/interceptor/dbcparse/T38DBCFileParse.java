package com.dfssi.dataplatform.datasync.plugin.interceptor.dbcparse;

import com.google.common.collect.Lists;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * x0705 CAN T38 DBC format file parse
 * @author jianKang
 * @date 2017/12/22
 */
public class T38DBCFileParse {
    static final Logger logger = LoggerFactory.getLogger(T38DBCFileParse.class);
    private static InputStream is = null;
    private static final String COMMA = ",";
    Properties prop = null;
    String charset;
    List<String> files ;

    public T38DBCFileParse() {
        prop = new Properties();
        files = Lists.newArrayList();
        try {
            prop.load(T38DBCFileParse.class.getClassLoader().getResourceAsStream("dbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dbcfile = prop.getProperty("dbcfile");
        charset = prop.getProperty("charset");
        if(dbcfile.contains(COMMA)){
            files = Arrays.asList(dbcfile.split(COMMA));
            //todo files list to Queue
        }else
        {
            is = T38DBCFileParse.class.getClassLoader().getResourceAsStream(dbcfile);
        }
        //is = T38DBCFileParse.class.getClassLoader().getResourceAsStream("hbhejiashiyan.DBC");
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
            brd = IOUtils.lineIterator(is,Charsets.toCharset(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(brd.hasNext()){
            line = brd.nextLine();
            if(getStringByPrefixChars(line)){
                logger.info("parse each line from file: "+line);
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
