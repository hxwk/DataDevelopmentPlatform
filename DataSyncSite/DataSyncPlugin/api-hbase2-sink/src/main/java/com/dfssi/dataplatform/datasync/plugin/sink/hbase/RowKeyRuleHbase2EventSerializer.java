package com.dfssi.dataplatform.datasync.plugin.sink.hbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.FlumeException;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ComponentConfiguration;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ByteUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.EventTopicConfig;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.HeaderConstants;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.ParameterConstant;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Action;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.addEventBatch;
import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.logger;

/**
 * @author chenf
 * @date 2018-11-1 12:13
 * @descrpition 根据规则将event 序列化
 */
public class RowKeyRuleHbase2EventSerializer implements HBase2EventSerializer {

    private Map<String,List<Action>> actionMap=new HashMap<String,List<Action>>();

    private Map<String,List<Increment>>  incrementMap = new HashMap<String,List<Increment>>();

    private Map<String,EventTopicConfig> topicConfigMap =  new HashMap<String,EventTopicConfig>();

    protected static final AtomicInteger nonce = new AtomicInteger(0);
    protected static final String randomKey = RandomStringUtils.randomAlphanumeric(10);

    protected byte[] cf;
    private byte[] eventBody;
    private byte[] rowkey ;
    private Map<String, String> headers;

    private final List<byte[]> colNames = Lists.newArrayList();
    private int isDefualt=1;
    String eventTopic = null;
    String eventMsgId = null;
    EventTopicConfig topicConfig=null;
    Map<String,Object> columns  =null;
    @Override
    public void initialize(Event event, byte[] columnFamily) {
        this.headers = event.getHeaders();
        this.eventBody = event.getBody();
        this.cf = columnFamily;
    }

    public void initialize(Event event) {
        this.headers = event.getHeaders();
        this.eventBody = event.getBody();
        try {
            //Map<String, String> headers = event.getHeaders();
            //byte[] eventBody = event.getBody();
            //logger.info("event:{}", JSON.toJSONString(event));
            //logger.info("body:{}", new String(eventBody, "UTF-8"));

            String eventKey = headers.get(HeaderConstants.HEADER_KEY);

            if (StringUtils.isNotEmpty(eventKey)) {
                eventTopic = (String) JSONObject.parseObject(eventKey).get(HeaderConstants.HEADER_TOPIC);
                eventMsgId = (String) JSONObject.parseObject(eventKey).get(HeaderConstants.MSGID);

                // logger.info("topic is:{}", eventTopic);
            } else {
                logger.warn("eventTopic:{},exception:error");
            }
                /*if(null==eventTopic){
                    throw new Exception("数据错误event:"+ JSON.toJSONString(event)+" ,topic属性不能为空");
                }*/
            if (eventTopic != null && eventMsgId != null) {
                columns = JSON.parseObject(new String(eventBody, "UTF-8"));

                 topicConfig = topicConfigMap.get(eventTopic);
                if (topicConfig == null && isDefualt == 1) {
                    topicConfig = new EventTopicConfig();
                    topicConfig.setFamily("f");
                    topicConfig.setNamespace("dev");
                    String tn = eventMsgId.replaceAll("jts", "").replaceAll("\\.", "");
                    String tableName = "dev:terminal_" + tn;
                    topicConfig.setTable(tableName);
                    topicConfigMap.put(eventTopic, topicConfig);
                }else if(topicConfig == null){
                    return ;
                }
                String tableName = topicConfig.getTable();
                String series = topicConfig.getFamily();
                String rule = topicConfig.getRule();
                String field = topicConfig.getField();
                String rowkeyRule = topicConfig.getRule();
                String separator = topicConfig.getSeparator();
                String rowkeyRuleParam = topicConfig.getParam();
                Object Clfield = columns.get(field);
                List concatField = topicConfig.getConcat();
                List concat = Lists.newArrayList();
                if(concatField==null) {
                    concatField =Lists.newArrayList();
                }
                for (Object jo : concatField) {
                    concat.add(columns.get(String.valueOf(jo)));
                }

                if (rule != null) {
                    rowkey = RuleRowKeyGenerator.rowkeyRule(rowkeyRule, rowkeyRuleParam, Clfield, separator, concat.toArray());
                    //addEvent(tableName, rowkey, series, body);
                    //加入批量处理list
                    //addEventBatch(tableName, rowkey, series, body);
                }
                //当不指定rowkey规则时间用 时间戳反转规则 先用 vid 再用sim 卡号
                else {
                     field = StringUtils.isBlank((String)columns.get("vid")) ? (String)columns.get("sim") : (String)columns.get("vid");
                     concat = Lists.newArrayList();
                     concat.add(System.currentTimeMillis());
                     rowkey =RuleRowKeyGenerator.rowkeyRule(ParameterConstant.ROWKEY_RULE_INVERTED, null, field, separator, concat.toArray());
                    //addEvent(tableName, rowkey, series, body);
                    //加入批量处理list
                    //addEventBatch(tableName, rowkey, series, body);
                }

            }
        }catch (Exception e){
            logger.error("event initialize error",e);
        }
    }

    @Override
    public List<Row> getActions() {
        List<Row> actions = Lists.newArrayList();
        if(topicConfig==null||org.apache.commons.lang3.StringUtils.isBlank(topicConfig.getTable())||rowkey==null||rowkey.length==0|| org.apache.commons.lang3.StringUtils.isBlank(topicConfig.getFamily())||columns==null||columns.size()==0){
            //throw new IllegalArgumentException("参数错误");
            return actions;
        }
        Put put = new Put(rowkey);
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            put.addColumn(topicConfig.getFamily().getBytes(Charsets.UTF_8), Bytes.toBytes(entry.getKey()),
                    ByteUtils.toBytes(entry.getValue())

            );
        }
        actions.add(put);

        return actions;
    }

    @Override
    public List<Increment> getIncrements() {
        return Lists.newArrayList();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Context context) {

    }

    public void configure(Map<String,EventTopicConfig> map){
        topicConfigMap.putAll(map);

    }

    @Override
    public void configure(ComponentConfiguration conf) {

    }

    public int getIsDefualt() {
        return isDefualt;
    }

    public void setIsDefualt(int isDefualt) {
        this.isDefualt = isDefualt;
    }

    public byte[] getEventBody() {
        return eventBody;
    }

    public void setEventBody(byte[] eventBody) {
        this.eventBody = eventBody;
    }

    public byte[] getRowkey() {
        return rowkey;
    }

    public void setRowkey(byte[] rowkey) {
        this.rowkey = rowkey;
    }

    public String getEventTopic() {
        return eventTopic;
    }

    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }

    public String getEventMsgId() {
        return eventMsgId;
    }

    public void setEventMsgId(String eventMsgId) {
        this.eventMsgId = eventMsgId;
    }
}
