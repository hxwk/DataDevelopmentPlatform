package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author KangJian
 * @create time 2018-10-20 14:15
 */
public class FileUtil {
    private static MessageDigest mMessageDigest = null;
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    static {
        try {
            mMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.warn("No Such Algorithm Exception:{}",e.getStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * obtain the file's MD5
     */
    private static String getFileMD5String(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer, 0, 1024)) > 0) {
                mMessageDigest.update(buffer, 0, length);
            }
            fis.close();
            return new BigInteger(1, mMessageDigest.digest()).toString(16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSameFile(String filePath1, String filePath2) {
        boolean result = true;

        File externalFile = new File(filePath1);
        File internallFile = new File (filePath2);

        if (externalFile.length() != internallFile.length()) {
            result = false;
        } else {
            String file1MD5 = getFileMD5String(externalFile);
            String file2MD5 = getFileMD5String(internallFile);
            if (file1MD5 != null && !file1MD5.equals(file2MD5)) {
                result = false;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String filePath1 = "G://spf.dbc";
        String filePath2 = "G://spf1.dbc";
        boolean flag = FileUtil.isSameFile(filePath1,filePath2);
        System.out.println(flag);
    }
}
