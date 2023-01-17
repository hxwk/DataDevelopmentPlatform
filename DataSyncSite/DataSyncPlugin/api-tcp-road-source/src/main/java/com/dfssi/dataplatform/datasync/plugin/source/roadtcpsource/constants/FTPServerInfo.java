package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.constants;

import java.io.File;

import static com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.canfile.CanConstants.localFilePath;

public class FTPServerInfo {

    public static String url = "172.16.1.180";

    public static int port = 21;

    public static String userName = "ssi-ter";

    public static String password = "SSI-NanDou-ter";

    public static String remotePath = "/var/ftp/pub/ndypt/application/roadVehicle/upload/";

    public static String fileName = "spf.dbc";

    public static String localPath = localFilePath+File.separatorChar;

}
