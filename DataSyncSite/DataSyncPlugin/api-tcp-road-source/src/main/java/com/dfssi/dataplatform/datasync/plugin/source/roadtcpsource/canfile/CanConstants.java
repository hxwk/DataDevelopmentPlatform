package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CanConstants {
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String AT = "@";
    public static final String DBCFORMAT ="dbcFormat";
    public static final String CHARSET = "charset";
    public static final String DBCFILE = "dbcfile";

    //public static final String VID2FASTDFSFID = "FASTID:VID:";
    public static final String FTP2SIMID = "FTPID:SIM:";
    public static String fileIdName;

    public static Set<String> dbcNameVersion;
    public static String dbcName = "spf.dbc";

    public static Map<String/**dbc文件*/,Queue<String>/**文件有效值队列*/> fileDataVersion ;//将不同的dbcName的文件流缓存到内存里

    public static String remotePath;
    public static String charSet ="UTF-8";
    public static String dbcFormat ="CURRENT";
    private final static String dbcDirectory = "dbcfiles";
    public static String localFilePath=System.getProperties().getProperty("user.home")+ File.separatorChar+dbcDirectory;

}
