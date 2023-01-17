/**
 * 
 */
package com.dfssi.common.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflects {
	
	private static final Logger LOG = LoggerFactory.getLogger(Reflects.class);
	
	private Reflects(){}

	@SuppressWarnings("unchecked")
	public static <T> T createInstanceByClassName(String className, Class<?>[] parameterTypes, Object[] initargs){

		T instance = null;
		Class<T> classType;
		try {
			
			classType = (Class<T>) Class.forName(className);
			if(parameterTypes == null || parameterTypes.length == 0
					    || initargs == null || initargs.length == 0){
				instance  =  classType.newInstance();
				LOG.info("使用默认构造器实例化对象:" + classType.getName());
			}else{
			
				Constructor<T> constructor = classType.getDeclaredConstructor(parameterTypes);
				switch(constructor.getModifiers()){
				
					case 1 : 
						instance = constructor.newInstance(initargs);
						LOG.info("通过给定的参数类型数据获取的构造器类型为：public , 初始化成功。");
						break;
					case 4 : 
						constructor.setAccessible(true);
						instance = constructor.newInstance(initargs);
						LOG.info("通过给定的参数类型数据获取的构造器类型为：protected , 初始化成功。");
						break;
					case 0 :
						constructor.setAccessible(true);
						instance = constructor.newInstance(initargs);
						LOG.info("通过给定的参数类型数据获取的构造器类型为：default , 初始化成功。");
						break;
					case 2 :
						constructor.setAccessible(true);
						instance = constructor.newInstance(initargs);
						LOG.info("通过给定的参数类型数据获取的构造器类型为：private , 初始化成功。");
						break;
					default :
						constructor.setAccessible(true);
						instance = constructor.newInstance(initargs);
						LOG.info("通过给定的参数类型数据获取的构造器类型为： 未知 , 初始化成功。");
						break;
				}
			}
			
			
		} catch (ClassNotFoundException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (SecurityException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			LOG.error(null, e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			LOG.error(null, e);
			e.printStackTrace();
		}
		
		return instance;
  }

    public static Object executeMethod(String clazz, String method,
								   Class[] parameterTypes, Object[] parameterValues) throws Exception {

	  return executeMethod(Class.forName(clazz), method, parameterTypes, parameterValues);
    }

    public static Object executeMethod(Class clazz, String method,
								   Class[] parameterTypes, Object[] parameterValues) throws Exception {

	  Method declaredMethod = clazz.getDeclaredMethod(method, parameterTypes);
	  declaredMethod.setAccessible(true);
	  return declaredMethod.invoke(clazz.newInstance(), parameterValues);
    }


    /**
     *  递归查找指定目录下的类文件的全路径
     * @param baseFile 查找文件的入口
     */
    public static List<String> getSubFileNameList(File baseFile){
        List<String> res = new ArrayList<>();
		if(baseFile.isDirectory()){
			File[] files = baseFile.listFiles();
			for(File tmpFile : files){
                res.addAll(getSubFileNameList(tmpFile));
			}
		}
		String path = baseFile.getPath();
		if(path.endsWith(".java")){
			if(path.contains("src.main.java.")){
			    path = path.substring(14, path.length() - 5);
            }else{
                path = path.substring(0, path.length() - 5);
            }
            path = path.replaceAll("\\\\", ".");
            //path = path.substring(0, path.lastIndexOf(".java"));
            res.add(path);
		}
		return res;
	}

    /**
     *  从jar包读取所有的class文件名
     */
    public static List<String> getClassNameFrom(String jarName) throws IOException {
        List<String> fileList = new ArrayList<String>();

        JarFile jarFile = new JarFile(new File(jarName));
        Enumeration<JarEntry> en = jarFile.entries(); // 枚举获得JAR文件内的实体,即相对路径
        String clazz;
        while (en.hasMoreElements()) {
            clazz =  en.nextElement().getName();
            if(!clazz.endsWith(".class")){//不是class文件
                continue;
            }
            clazz = clazz.substring(0, clazz.lastIndexOf(".class"));
            clazz = clazz.replaceAll("/", ".");
            fileList.add(clazz);
        }
        return fileList;
    }

    /**
     *  判断一个类是否继承某个父类或实现某个接口
     */
    public static boolean isChildClass(String className, Class parentClazz){
        if(className == null) return false;

        Class clazz;
        try {
            clazz = Class.forName(className);
            if(Modifier.isAbstract(clazz.getModifiers())){//抽象类忽略
                return false;
            }
            if(Modifier.isInterface(clazz.getModifiers())){//接口忽略
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return parentClazz.isAssignableFrom(clazz);

    }

    public static ClassLoader getClassLoader(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null){
            classLoader = Reflects.class.getClassLoader();
        }
        return classLoader;
    }

    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        System.out.println(System.getProperty("user.dir"));

        File baseFile = new File(courseFile);
        System.out.println(baseFile);

        List<String> subFileNameList = getSubFileNameList(baseFile);
        System.out.println(subFileNameList);
    }

}
