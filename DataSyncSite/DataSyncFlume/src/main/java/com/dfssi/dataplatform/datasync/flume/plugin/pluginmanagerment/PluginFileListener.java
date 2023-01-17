package com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by HSF on 2017/11/28.
 */
public class PluginFileListener implements FileAlterationListener {
    private static final Logger logger = LoggerFactory.getLogger(PluginFileListener.class);
    PluginFileMonitor monitor = null;
    @Override
    public void onStart(FileAlterationObserver observer) {
        logger.debug("OnStart");
    }
    @Override
    public void onDirectoryCreate(File directory) {
        logger.debug("onDirectoryCreate:" +  directory.getName());
    }

    @Override
    public void onDirectoryChange(File directory) {
        logger.debug("onDirectoryChange:" + directory.getName());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        logger.debug("onDirectoryDelete:" + directory.getName());
    }

    @Override
    public void onFileCreate(File file) {
        logger.debug("onFileCreate:" + file.getName());
        if (file.getAbsolutePath().endsWith(".jar")){
            ClassLoaderUtil.loadJarFile(file);
            logger.info("onFileCreate loadJarFile:"+file.getName());
        }
    }

    @Override
    public void onFileChange(File file) {
        logger.debug("onFileCreate : " + file.getName());
    }

    @Override
    public void onFileDelete(File file) {
        logger.debug("onFileDelete :" + file.getName());

        if (file.getAbsolutePath().endsWith(".jar")){
           boolean flag = ClassLoaderUtil.closeJarFile(file.getAbsolutePath());
            if (flag){
                logger.info("onFileDelete closeJarFile:"+file.getName()+" successfully!");
            }else {
                logger.error("onFileDelete closeJarFile:"+file.getName()+" failed!");
            }

        }
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        logger.debug("onStop");
    }

}
