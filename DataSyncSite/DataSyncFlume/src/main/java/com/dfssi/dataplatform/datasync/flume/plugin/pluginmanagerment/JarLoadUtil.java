package com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by HSF on 2017/11/28.
 */

@Deprecated /* reference to  ClassLoaderUtil */
public class JarLoadUtil {
      private static final Logger logger = LoggerFactory.getLogger(JarLoadUtil.class);
      private static URLClassLoader loader = null;
      private static URL lib_url = ClassLoader.getSystemClassLoader().getResource("lib");
      private static ReadWriteLock lock = new ReentrantReadWriteLock();
      private static Lock readLock =  lock.readLock();
      private static Lock writeLock = lock.writeLock();

      /*
     * 加载lib目录下所有jar文件，并返回相应的的URLClassLoader
     */
      public static URLClassLoader getURLClassLoader(){

            if(loader == null){
                  String fileNames[] = listFileNames();
                  if(fileNames != null && fileNames.length > 0){
                        URL urls[] = new URL[fileNames.length];
                        for(int i = 0;i < fileNames.length;i++){
                              try {
                                    urls[i] = new URL(lib_url+"/"+fileNames[i]);
                              } catch (MalformedURLException e) {
                                   logger.error("加载lib目录下jar文件出错！",e);
                                  throw new RuntimeException("加载lib目录下jar文件出错！",e);
                              }
                        }
                        loader = new URLClassLoader(urls);
                  }
            }
            return loader;
      }
      /*
       * 查询lib目录下的所有文件名称
       */
      private static String[] listFileNames(){
            File file_directory = new File("lib");
            return file_directory.list();
      }
}
