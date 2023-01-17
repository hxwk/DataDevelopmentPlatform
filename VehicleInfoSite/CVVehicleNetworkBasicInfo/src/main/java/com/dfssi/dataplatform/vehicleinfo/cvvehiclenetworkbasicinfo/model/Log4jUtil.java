package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;


public class Log4jUtil {
    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(Log4jUtil.class);
	 /*************************************************************************
     *                                基础日志                                 *
     **************************************************************************/

	private static Object log4jUtilObj = null;
	
	static{
		try {
			log4jUtilObj = Log4jUtil.class.newInstance();
		} catch (InstantiationException e) {
			Log4jUtil.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log4jUtil.error(e.getMessage(), e);
		}
	}

    /**
     * 记录一条日志，级别为debug
     * 
     * @param message
     */
    public static void debug(String message) {
    	if(log.isDebugEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            log.debug(msg + ":值:"+message);
        }
    }

    /**
     * 记录一条日志，级别为debug
     * 
     * @param message
     * @param t
     */
    public static void debug(String message, Throwable t) {
        if(log.isDebugEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            message = msg + "----->\n" + message;
            log.debug(message + ":开始:");
            log.debug(stackTraceToString(t));
            log.debug(message + ":结束");
        }
    }

    /**
     * 记录一条日志，级别为info
     * 
     * @param message
     */
    public static void info(String message) {
        if(log.isInfoEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            log.info(msg + "----->\n"+message);
        }
    }

    /**
     * 记录一条日志，级别为info
     * 
     * @param message
     * @param t
     */
    public static void info(String message, Throwable t) {
        if(log.isInfoEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            message = msg + "----->/n" + message;
            log.info(message + ":开始:");
            log.info(stackTraceToString(t));
            log.info(message + ":结束");
        }
    }

    /**
     * 记录一条日志，级别为error
     * 
     * @param message
     */
    public static void error(String message) {
//    	if(log.isErrorEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            log.error(msg + "----->\n"+message);
//        }
    }

    /**
     * 记录一条日志，级别为error
     * 
     * @param message
     * @param t
     */
    public static void error(String message, Throwable t) {
//        if(log.isErrorEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {
            	
                try {
                	msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            message = msg + "----->\n" + message;
            log.error(message + ":开始:");
            log.error(stackTraceToString(t));
            log.error(message + ":结束");
//        }
    }


    /**
     * 记录一条日志，级别为fatal
     * 
     * @param message
     */
    public static void warn(String message) {
//        if(log.isWarnEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {

                try {
                	msg = getClassInfo(e1);

                } catch (Exception e2) {
                    msg = "";
                }
            }
            log.warn(msg + "----->/n值:"+message);
//        }
    }

    /**
     * 记录一条日志，级别为fatal
     * 
     * @param message
     * @param t
     */
    public static void warn(String message, Throwable t) {
//        if(log.isWarnEnabled()){
            String msg = "";
            try {
                throw new Exception("");
            } catch (Exception e1) {
                try {
                    msg = getClassInfo(e1);
                } catch (Exception e2) {
                    msg = "";
                }
            }
            message = msg + "----->/n" + message;
            log.warn(message + ":开始:");
            log.warn(stackTraceToString(t));
            log.warn(message + ":结束");
//        }
    }
    
    public static final String DEBUG = "debug";
    public static final String INFO = "info";
    public static final String ERROR = "error";
    public static final String WARN = "warn";
    public static final String FATAL = "fatal";
    private static final String EMPTY_OBJECT = "Import is empty Object.";
    
    /**
     * 记录一条传入的Object对象信息
     * 
     * @param object
     */
    public static void printObject(Object object){
    	printObject(object, INFO);
    }
    
    /**
     * 记录一条传入的Object对象信息
     * 
     * @param object
     * @param level
     */
    public static void printObject(Object object,String level){
    	try {
			if(object == null){
				ClassUtil.invokeMethod(level, log4jUtilObj, new Object[]{EMPTY_OBJECT});
			}else{
				Class clas = object.getClass();
				Field[] array = clas.getDeclaredFields();
				ClassUtil.invokeMethod(level, log4jUtilObj, new Object[]{"Java class : "+clas.getName()});
				Field field;
				String fieldName;
				for (int i = 0; i < array.length; i++) {
					field = array[i];
					fieldName = field.getName();
					String value = getFieldValue(object, field);
					ClassUtil.invokeMethod(level, log4jUtilObj, new Object[]{fieldName+" : ["+ value+"]"});
				}
			}
		} catch(Exception e){
			error(e.getMessage(), e);
		}
    }
    /**
     * 得到对象对应的属性值
     * @param object
     * @param field
     * @return
     */
    private static String getFieldValue(Object object,Field field){
    	String returnStr = "";
    	try {
    		if(!field.isAccessible()){
    			field.setAccessible(true);
    		}
			Object obj = field.get(object);
			if(obj!=null){
				returnStr = obj.toString();
			}
		} catch (Exception e) {
			//异常处理
			returnStr = " can't find 'get" + field.getName() + "' method";
		}
		return returnStr;
    }
    public static Logger getLogger(){
    	return log;
    }
    private static String getClassInfo(Exception e){
    	 String msg = "";
    	 try {
             StackTraceElement st = e.getStackTrace()[1];
             String className = st.getClassName();
             if (className.lastIndexOf('.') >= 0) {
                 className = className.substring(className.lastIndexOf('.') + 1);
             }

             msg = className + ":" + st.getMethodName() + ":Line" + st.getLineNumber();

         } catch (Exception e2) {
             msg = "";
         }
    	 return msg;
    }
    private static String stackTraceToString(Throwable t){
    	StringWriter l_oSw = new StringWriter();
		t.printStackTrace(new PrintWriter(l_oSw));
		return l_oSw.toString();
    }
}
