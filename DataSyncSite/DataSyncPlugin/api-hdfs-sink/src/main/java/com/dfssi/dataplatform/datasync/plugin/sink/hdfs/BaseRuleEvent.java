package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskDataDestination;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * generate Event by rules, Columns Mapping Task
 * @author JianKang
 */
public class BaseRuleEvent implements Event {
    static final Logger logger = LoggerFactory.getLogger(BaseRuleEvent.class);

    private Map<String, String> headers;
    private byte[] body;

    public BaseRuleEvent() {
        headers = new HashMap();
        body = new byte[0];
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public void setBody(byte[] body) {
        if (body == null) {
            body = new byte[0];
        }
        this.body = body;
    }

    @Override
    public String toString() {
        return "BaseRuleEvent{" +
                "headers=" + headers +
                ", body=" + Arrays.toString(body) +
                '}';
    }

    /**
     * 重新封装event
     * @param dataDestination
     * @param context
     * @return
     */
    public Event mapping(TaskDataDestination dataDestination, Context context){
        logger.info("start mapping event body...");
        Event event = new BaseRuleEvent();
        List<Integer> columnOrder = new ArrayList<>();
        StringBuilder newBody = new StringBuilder();
        Map<Integer, List<Integer>> mapping = dataDestination.getMapping();
        List<String> textes = Arrays.asList(body.toString().split(context.getString("col_separator", StringUtils.SPACE)));
        Map<Integer, Integer> sort = sort(mapping);
        for(Map.Entry<Integer, Integer> entries : sort.entrySet()){
            columnOrder.add(entries.getValue());
        }
        /**
         * {0=0, 1=1, 2=3, 3=2, 4=4, 5=4}
         * 根据列对内容进行排序,冰封装成心的Event body
         */
        for(Integer order:columnOrder){
            newBody.append(textes.get(order));
            newBody.append(context.getString("col_separator", StringUtils.SPACE));
        }
        /**
         * 组装成新Event,delete last separator
         */
        newBody.deleteCharAt(newBody.length()-1);
        event.setBody(StringUtils.trim(newBody.toString()).getBytes());
        return event;
    }

    /**
     * sort by field
     * @param mapping Integer:data column, List<Integer>: field i
     * @return field index 2 column name
     */
    private static Map<Integer,Integer> sort(Map<Integer,List<Integer>> mapping){
        Map<Integer /*data column*/,Integer /*filed name*/> columns2fileds = Maps.newTreeMap();
        Map<Integer,List<Integer>> mappings = mapping;
        List<Integer> destColumn ;
        for(Map.Entry<Integer, List<Integer>> entity :mappings.entrySet()){
            destColumn = entity.getValue();
            for(Integer columnIdx:destColumn){
                columns2fileds.put(columnIdx,entity.getKey());
            }
        }
        return columns2fileds;
    }

    /*public static void main(String[] args) {
        BaseRuleEvent re = new BaseRuleEvent();
        HashMap<Integer,List<Integer>> a = new HashMap<>();
        List<Integer> b = new ArrayList<>();
        List<Integer> c = new ArrayList<>();
        List<Integer> d = new ArrayList<>();
        List<Integer> e = new ArrayList<>();
        List<Integer> f = new ArrayList<>();
        c.add(0);
        b.add(1);
        d.add(3);
        e.add(2);
        f.add(4);
        f.add(5);
        a.put(0,c);
        a.put(1,b);
        a.put(2,d);
        a.put(3,e);
        a.put(4,f);
        for(Map.Entry<Integer, Integer> entries: re.sort(a).entrySet()){
            System.out.println(entries.getKey()+StringUtils.SPACE+entries.getValue());
        }

        System.out.println(re.sort(a));
    }*/
}
