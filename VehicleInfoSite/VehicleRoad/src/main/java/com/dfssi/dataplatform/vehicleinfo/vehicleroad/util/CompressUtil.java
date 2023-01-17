package com.dfssi.dataplatform.vehicleinfo.vehicleroad.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

public class CompressUtil {

    private CompressUtil(){}

    /**
     * zip加密压缩
     *
     * @param zipfile  压缩文件完整路径（包含文件名）
     * @param filePath 压缩文件路径
     */
    public static void zipQuickly(String zipfile, String filePath) throws ZipException {
        ZipFile zipFile = new ZipFile(zipfile);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setIncludeRootFolder(false);
        zipFile.addFolder(new File(filePath), parameters);
    }

    public static void zipQuickly(String zipfile, ArrayList<String> files) throws ZipException {
        ZipFile zipFile = new ZipFile(zipfile);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.addFiles(files, parameters);
    }

}
