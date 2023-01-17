package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.canfile;

import java.io.File;

public class CanConstants {
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String AT = "@";
    public static final String DBCFORMAT ="dbcFormat";
    public static final String CHARSET = "charset";
    public static final String DBCFILE = "dbcfile";

    public static final String VID2FASTDFSFID = "FASTID:VID:";
    public static String dbcFastDFSFileId;
    public static String fileIdName;
    public static String charSet ="UTF-8";
    public static String dbcFormat ="old";
    public static String localFilePath=System.getProperties().getProperty("user.home")+ File.separatorChar+"dbcfiles";

}
