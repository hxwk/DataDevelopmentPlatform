package com.yaxon.vn.nd.tas.common;

import java.io.File;

/**
 * @author: JianKang
 * @Time: 2018-01-26 10:31
 * @Content: 主要记录redis中对应的key值,从dbc配置文件中进来的配置信息及fileIdName文件信息
 */
public class CanConstants {
    public static final String VID2FASTDFSFID = "FASTID:VID:";
    public static String dbcFastDFSFileId;
    public static String fileIdName;
    public static String charSet ="UTF-8";
    public static String dbcFormat ="old";
    public static String localFilePath=System.getProperties().getProperty("user.home")+ File.separatorChar+"dbcfiles";
}
