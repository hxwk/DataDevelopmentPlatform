//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

public class FileResource {
    protected static Logger logger = LoggerFactory.getLogger(FileResource.class);
    public static final String CLASS_PATH_PREFIX = "classpath:";
    protected File file;
    protected String classPath;
    InputStream fis;
    protected final boolean streamResource;

    public FileResource(File f) {
        this.fis = null;
        this.file = f;
        this.streamResource = false;
    }


    public FileResource(String fileName) {
        this.fis = null;
        if(fileName.startsWith("classpath:")) {
            this.initClassPathFile(fileName);
            this.streamResource = true;
        } else {
            this.file = new File(fileName);

            this.streamResource = false;
        }

    }

    protected void initClassPathFile(String classpathFile) {
        this.classPath = classpathFile.substring("classpath:".length());
//        System.out.println(", FileResource.class.getResource(\"\") = " +FileResource.class.getResource("")
//                + ", FileResource.class.getClassLoader().getResource(\"\") = " + FileResource.class.getClassLoader().getResource(""));
        String classRootPath = FileResource.class.getClassLoader().getResource("").getFile();
        String m_fileName = classpathFile.substring("classpath:".length());
        this.file = new File(URLDecoder.decode(classRootPath), m_fileName);
    }

    public void close() {
        if(this.fis != null) {
            try {
                this.fis.close();
            } catch (IOException var2) {
                logger.error("error on close the inputstream.", var2);
            }
        }

    }

    public InputStream getInputStream() throws IOException {
        if(this.fis == null) {
            if(this.isStreamResource()) {
                this.fis = FileResource.class.getClassLoader().getResourceAsStream(this.classPath);
            } else {
                this.fis = new FileInputStream(this.file);
            }
        }

        if(this.fis == null) {
            throw new IOException("resource is not available. file is:" + this.classPath);
        } else {
            return this.fis;
        }
    }

    public String toString() {
        return "file resource. file is:" + (this.file == null?this.classPath:this.file.getAbsolutePath());
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isStreamResource() {
        return this.streamResource;
    }
}
