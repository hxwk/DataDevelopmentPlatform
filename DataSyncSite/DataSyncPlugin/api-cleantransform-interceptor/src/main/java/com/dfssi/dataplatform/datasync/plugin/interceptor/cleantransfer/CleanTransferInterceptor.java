package com.dfssi.dataplatform.datasync.plugin.interceptor.cleantransfer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;
import com.dfssi.dataplatform.datasync.plugin.interceptor.constants.Constant;
import com.dfssi.dataplatform.datasync.plugin.interceptor.factory.BaseHandler;
import com.dfssi.dataplatform.datasync.plugin.interceptor.factory.CleanTransformFactory;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasync.plugin.interceptor.constants.Constant.JDBC_TYPE;
import static com.dfssi.dataplatform.datasync.plugin.interceptor.constants.Constant.SOURCE_TYPE;
import static com.dfssi.dataplatform.datasync.plugin.interceptor.constants.Constant.TCP_TYPE;

/**
 * @author JianKang
 * @date 2018/5/8
 * @description
 */
public class CleanTransferInterceptor implements Interceptor {
    static final Logger logger = LoggerFactory.getLogger(CleanTransferInterceptor.class);
    private String type = null;
    private Map<String, Map<String, MapperRule>> ctRules;
    private List<String> columnList;

    public CleanTransferInterceptor(String type, String columns, String cleanTranRule) {
        this.type = type;
        if (null != columns) {
            columnList = JSONObject.parseObject(columns, new TypeReference<List<String>>() {
            });
        }
        if (null != cleanTranRule) {
            ctRules = JSONObject.parseObject(cleanTranRule, new TypeReference<Map<String, Map<String, MapperRule>>>() {
            });
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public Event intercept(Event event) {
        //1 获取到对象
        String beforeBody = new String(event.getBody());
        logger.info("beforeBody:{}", beforeBody);
        //2 转换成map对象
        Map<String, Object> beforeBodyMap = JSONObject.parseObject(beforeBody, new TypeReference<Map<String, Object>>() {
        });
        logger.info("beforeBodyMap:{}", beforeBodyMap);
        //3 获取待转换的字段内容, 开始清洗转换
        String key = event.getHeaders().get(Constant.HEADER_KEY);
        String sourceType;
        if(StringUtils.isEmpty((String)JSONObject.parseObject(key).get(SOURCE_TYPE))){
            sourceType = TCP_TYPE;
        }else {
            sourceType = (String) JSONObject.parseObject(key).get(SOURCE_TYPE);
        }
        if(sourceType.equals(JDBC_TYPE)){ //JDBC数据格式
            cleanTranJdbcInterceptor(beforeBodyMap);
        }else if(sourceType.equalsIgnoreCase(TCP_TYPE)){ //tcp报文格式
            cleanTranInterceptor(beforeBodyMap);
        }else{ //其他类型
            //todo
            logger.info("other event interceptor need todo");
        }

        //4 封装成对象并生成字节数组
        byte[] afterBody = JSONObject.toJSONBytes(beforeBodyMap);
        logger.info("afterBody:{}", new String(afterBody));
        event.setBody(afterBody);
        return event;
    }

    private void cleanTranJdbcInterceptor(Map<String,Object> bodyMap){
        BaseHandler handler;
        String srcText;
        for (Map.Entry<String, Map<String, MapperRule>> ctRuleMaps : ctRules.entrySet()) {
            String rule;
            MapperRule mapperRule;
            Integer index = Integer.parseInt(ctRuleMaps.getKey());
            String columnValue = columnList.get(index);
            Map<String, MapperRule> mapperRuleMap = ctRuleMaps.getValue();
            //遍历到MapperRule
            for (Map.Entry<String, MapperRule> mapperRuleEntry : mapperRuleMap.entrySet()) {
                rule = mapperRuleEntry.getKey();
                mapperRule = mapperRuleEntry.getValue();
                handler = CleanTransformFactory.getInstance().cleanTransform(rule);
                logger.debug("handler:{}", handler);
                //遍历原始报文数据,找出字段对应的内容
                for (Map.Entry<String, Object> beforeBodys : bodyMap.entrySet()) {
                    String key = beforeBodys.getKey();
                    Object value = beforeBodys.getValue();
                    if(key.equalsIgnoreCase(columnValue)){
                        srcText = String.valueOf(value);
                        String afterColumnText = handler.columnCleanTransform(srcText, mapperRule);
                        bodyMap.put(key, afterColumnText);
                    }
                }
            }
        }
    }

    private void cleanTranInterceptor(Map<String, Object> beforeBodyMap) {
        BaseHandler handler;
        String srcText;
        for (Map.Entry<String, Map<String, MapperRule>> ctRuleMaps : ctRules.entrySet()) {
            String rule;
            MapperRule mapperRule;
            Integer index = Integer.parseInt(ctRuleMaps.getKey());
            String columnValue = columnList.get(index);
            Map<String, MapperRule> mapperRuleMap = ctRuleMaps.getValue();
            //遍历到MapperRule
            for (Map.Entry<String, MapperRule> mapperRuleEntry : mapperRuleMap.entrySet()) {
                rule = mapperRuleEntry.getKey();
                mapperRule = mapperRuleEntry.getValue();
                handler = CleanTransformFactory.getInstance().cleanTransform(rule);
                logger.debug("handler:{}",handler);
                //遍历原始报文数据,找出字段对应的内容
                for (Map.Entry<String, Object> beforeBodys : beforeBodyMap.entrySet()) {
                    String key = beforeBodys.getKey();
                    Object value = beforeBodys.getValue();
                    if (value instanceof Map) {
                        Map<String, Object> objects = (Map) value;
                        for (Map.Entry<String, Object> kvs : objects.entrySet()) {
                            String currentKey = kvs.getKey();
                            Object currentValue = kvs.getValue();
                            List currentList ;
                            Map<String, Object> items;
                            if (columnValue.equals(currentKey)) {
                                srcText = String.valueOf(currentValue);
                                String afterColumnText = handler.columnCleanTransform(srcText, mapperRule);
                                objects.put(currentKey, afterColumnText);
                            }
                            if(currentValue instanceof List){
                                currentList = (List) currentValue;
                                for(int i=0;i<currentList.size();i++){
                                    Object cv = currentList.get(i);
                                    if(cv instanceof Map) {
                                        items = (Map) cv;
                                        for (Map.Entry<String, Object> item : items.entrySet()) {
                                            String itemKey = item.getKey();
                                            Object itemValue = item.getValue();
                                            if (columnValue.equals(itemKey)) {
                                                srcText = String.valueOf(itemValue);
                                                String afterColumnText = handler.columnCleanTransform(srcText, mapperRule);
                                                items.put(itemKey, afterColumnText);
                                            }
                                        }
                                        currentList.set(i,items);
                                    }
                                }
                                objects.put(currentKey,currentList);
                            }
                        }
                        beforeBodyMap.put(key, objects);
                    }
                }
            }
        }
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        Iterator i$ = events.iterator();

        while (i$.hasNext()) {
            Event event = (Event) i$.next();
            this.intercept(event);
        }

        return events;
    }

    @Override
    public void close() {
    }

    public static class Builder implements Interceptor.Builder {
        private static final String TYPE_KEY = "type";
        private static final String CLEANTRANRULE = "cleanTranRule";
        private static final String COLUMNS = "columns";

        private String type;
        private String cleanTranRule;
        private String columns;
        private List<String> columnList;

        @Override
        public void configure(Context context) {
            //get interceptor type
            type = context.getString(TYPE_KEY);
            //get clean transform rule
            cleanTranRule = context.getString(CLEANTRANRULE);
            //get all clean transform columns
            columns = context.getString(COLUMNS);
            columnList = (List<String>) JSONObject.parse(columns);
            //print
            columnList.forEach((entry) -> logger.info("column:{}", entry.toString()));

        }

        @Override
        public Interceptor build() {
            if (null == type) {
                logger.warn("interceptor type cannot be empty");
            }
            if (null == columns) {
                logger.warn("columns cannot be empty");
            }
            if (null == cleanTranRule) {
                logger.warn("cleanTranRule cannot be empty");
            }
            return new CleanTransferInterceptor(type, columns, cleanTranRule);
        }
    }
}
