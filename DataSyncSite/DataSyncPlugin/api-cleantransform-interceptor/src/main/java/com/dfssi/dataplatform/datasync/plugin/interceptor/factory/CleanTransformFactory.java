package com.dfssi.dataplatform.datasync.plugin.interceptor.factory;

import com.dfssi.dataplatform.datasync.plugin.interceptor.common.CleanTranHandlerReader;
import com.dfssi.dataplatform.datasync.plugin.interceptor.constants.CleanTranformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class CleanTransformFactory {
    static final Logger logger = LoggerFactory.getLogger(CleanTransformFactory.class);
    private static Object lockObject = new Object();
    private Map<String,String> types;
    private static CleanTransformFactory instance;

    private CleanTransformFactory() {
        /**
         * 将清洗转换处理名称和处理类注册
         */
        CleanTranHandlerReader.getInstance();
        types = CleanTranformType.getCleanTransformTypes();
    }

    public synchronized static CleanTransformFactory getInstance(){
        if (null == instance) {
            synchronized (lockObject) {
                instance = new CleanTransformFactory();
            }
        }

        return instance;
    }

    /**
     * 根据类型获取实现处理类并做相应的处理
     * @return 对应的处理类
     */
    public synchronized BaseHandler cleanTransform(String rule) {
        logger.debug("rule:{},types:{}",rule,types);
        String handlerCls = types.get(rule);
        logger.debug("handlerCls:{}",handlerCls);
        try {
            Object obj = Class.forName(handlerCls).newInstance();
            if(obj instanceof BaseHandler){
                return (BaseHandler) obj;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
