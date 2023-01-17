package com.dfssi.dataplatform.datasync.flume.plugin.pluginmanagerment;

/**
 * Created by HSF on 2017/11/28.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarFile;


public final class ClassLoaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);
    private static URL lib_url = ClassLoader.getSystemClassLoader().getResource("lib");
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static Lock readLock =  lock.readLock();
    private static Lock writeLock = lock.writeLock();
    /**
     * URLClassLoader的addURL方法
     */
    private static Method addURL = initAddMethod();

    /**
     * 初始化方法
     */
    private static final Method initAddMethod() {
        try {
            Method add = URLClassLoader.class
                    .getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ClassLoaderUtil::initAddMethod e={}",e);
        }
        return null;
    }

   private static URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();


    /**
     * 循环遍历目录，找出所有的JAR包
     */
    private static final void loopFiles(File file, List<File> files) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp, files);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                files.add(file);
            }
        }
    }

    /**
     * <pre>
     * 加载JAR文件
     * </pre>
     *
     * @param file
     */
    public static final void loadJarFile(File file) {
        try {
            writeLock.lock();
            addURL.invoke(systemClassLoader, new Object[]{file.toURI().toURL()});
            logger.info("加载JAR包：" + file.getAbsolutePath());
        } catch (Exception e) {
           // e.printStackTrace();
            logger.error("加载JAR包异常！ClassLoaderUtil::loadJarFile e={}",e);
        }finally {
            writeLock.unlock();
        }
    }

    /**
     * <pre>
     * 从一个目录加载所有JAR文件
     * </pre>
     *
     * @param path
     */
    public static final void loadJarPath(String path) {
        List<File> files = new ArrayList<File>();
        File lib = new File(path);
        loopFiles(lib, files);
        for (File file : files) {
            loadJarFile(file);
        }
    }


    /**
     * <pre>
     * 从JVM的classpath中移除一个jar包
     *  FIXME 由于classLoader中获取的path和loader不匹配，根据下标来移除可能会存在误删的风险，待验证
     * </pre>
     *
     * @param path
     */
    public static final boolean closeJarFile(String path){
        boolean flag = false;
        try{
            readLock.lock();
            File jar = new File(path);
            URL[] urls = new URL[]{jar.toURI().toURL()};
            Class<URLClassLoader> sysclass = URLClassLoader.class;
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);

            // 查找URLClassLoader中的ucp
            Object ucpObj = null;
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            ucpObj = ucpField.get(classLoader);
            URL[] list = classLoader.getURLs();
            for(int i=0;i<list.length;i++){
                for (int j=0;j<urls.length;j++){
                    if (list[i].equals(urls[j])){
                        // 获得ucp内部的jarLoader
                        Method m = ucpObj.getClass().getDeclaredMethod("getLoader", int.class);
                        m.setAccessible(true);
                        Object jarLoader = m.invoke(ucpObj, i);
                        String clsName = jarLoader.getClass().getName();
                        if(clsName.indexOf("JarLoader")!=-1){
                            m = jarLoader.getClass().getDeclaredMethod("ensureOpen");
                            m.setAccessible(true);
                            m.invoke(jarLoader);
                            m = jarLoader.getClass().getDeclaredMethod("getJarFile");
                            m.setAccessible(true);
                            JarFile jf = (JarFile)m.invoke(jarLoader);
                            // 释放jarLoader中的jar文件
                            jf.close();
                            System.out.println("release jar: "+jf.getName());
                            logger.info("release jar: "+jf.getName());
                        }
                    }
                }
            }
            flag = true;
        }catch (MalformedURLException e){
            logger.error(e.getMessage());
        }catch (NoSuchFieldException e1){
            logger.error(e1.getMessage());
        }catch (IllegalAccessException e2){
            logger.error(e2.getMessage());
        }catch (NoSuchMethodException e3){
            logger.error(e3.getMessage());
        }catch (InvocationTargetException e4){
            logger.error(e4.getMessage());
        }catch (IOException e5){
            logger.error(e5.getMessage());
        }finally {
            readLock.unlock();
        }
        return flag;
    }
}