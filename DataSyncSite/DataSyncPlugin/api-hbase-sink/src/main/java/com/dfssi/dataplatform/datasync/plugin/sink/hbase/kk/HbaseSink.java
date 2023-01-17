package com.dfssi.dataplatform.datasync.plugin.sink.hbase.kk;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.common.EventHeader;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ConfigurationException;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.HbaseSinkCounter;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ByteUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HeaderConstants;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ParameterConstant;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.*;
/**
 * many to one
 * TcpSource To HbaseSink
 * @author  chenf
 * @date 2018-10-23
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.common.EventHeader;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ConfigurationException;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ByteUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HeaderConstants;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ParameterConstant;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.*;
/**
 * many to one
 * TcpSource To HbaseSink
 * @author  chenf
 * @date 2018-10-23
 */
public class HbaseSink extends AbstractSink implements Configurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(com.dfssi.dataplatform.datasync.plugin.sink.hbase.HbaseSink.class);
    private Map<String,Object> taskCofigMap = new HashMap<String,Object>();
    private Map<String,JSONObject> topicConfigMap =  new HashMap<String,JSONObject>();
    private Map<String,JSONObject> adminMap = new HashMap<String,JSONObject>();
    private int batchSize;
    public static final int DEFAULT_BATCH_SIZE = 100;
    private HbaseSinkCounter counter;
    private int isDefualt=0;


    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        //List<Event> listEvents = new ArrayList<>();
        Transaction transaction = null;
        Channel channel = getChannel();
        transaction = channel.getTransaction();
        transaction.begin();
        JSONObject topicConfig = topicConfigMap.get("topic");

        try {
            long processedEvents = 0;
            //批量获取event
            Event event;
            for (; processedEvents < batchSize; processedEvents++) {
                event = channel.take();
                if (event == null) {
                    // no events available in channel
                    if (processedEvents == 0) {
                        result = Status.BACKOFF;
                        counter.incrementBatchEmptyCount();
                    } else {
                        counter.incrementBatchUnderflowCount();
                    }
                    break;
                }


                Map<String, String> headers = event.getHeaders();
                byte[] eventBody = event.getBody();
                logger.info("event:{}", JSON.toJSONString(event));
                logger.info("body:{}", new String(eventBody, "UTF-8"));
                String eventTopic =null;
                String eventMsgId =null;

                if(eventBody!=null) {
                    JSONObject body = JSON.parseObject(new String(eventBody, "UTF-8"));

                    if(topicConfig==null){
                        continue;
                    }
                    String tableName = topicConfig.getString(ParameterConstant.TABLE);
                    String series = topicConfig.getString(ParameterConstant.SERICES);
                    JSONObject ro = topicConfig.getJSONObject(ParameterConstant.ROWKEY);
                    String rowkeyField = null;
                    String rowkeyRule =null;
                    String rowkeyRuleParam =null;
                    JSONArray rowkeyConcat = null;
                    byte[] rowkey =null;
                    String separator = null;
                    if(ro!=null) {
                        rowkeyField = ro.getString(ParameterConstant.FIELD);
                        rowkeyRule = ro.getString(ParameterConstant.ROWKEY_RULE);
                        rowkeyRuleParam = ro.getString(ParameterConstant.ROWKEY_RULE_PARAM);
                        rowkeyConcat = ro.getJSONArray(ParameterConstant.ROWKEYCONCAT);
                        separator = ro.getString(ParameterConstant.ROWKEY_SEPARATOR);
                        Object field = body.get(rowkeyField);
                        List concat = Lists.newArrayList();
                        for(Object jo :rowkeyConcat){
                            concat.add(body.get(String.valueOf(jo)));
                        }
                        rowkey = rowkeyRule(rowkeyRule, rowkeyRuleParam, field, separator, concat.toArray());
                        addEvent(tableName, rowkey, series, body);
                    }
                    //当不指定rowkey规则时间用 时间戳反转规则 先用 vid 再用sim 卡号
                    else{
                        Object field =StringUtils.isBlank(body.getString("vid"))?body.getString("sim"):body.getString("vid");
                        List concat = Lists.newArrayList();
                        concat.add(System.currentTimeMillis());
                        rowkey = rowkeyRule(ParameterConstant.ROWKEY_RULE_INVERTED, null, field, separator, concat.toArray());
                        addEvent(tableName, rowkey, series, body);
                    }
                    counter.addToEventDrainSuccessCount(1);
                }
            }


            transaction.commit();
        }catch (Exception ex){
            String errorMsg = "Failed to save events to Hbase";
            logger.error("Failed to save events to Hbass", ex);
            result = Status.BACKOFF;
            if (transaction != null) {
                try {

                    transaction.rollback();
                    counter.incrementRollbackCount();
                } catch (Exception e) {
                    logger.error("Transaction rollback failed", e);
                    throw Throwables.propagate(e);
                }
            }
            throw new EventDeliveryException(errorMsg, ex);
        } finally {
            if (transaction != null) {
                transaction.close();
            }
        }
        return result;
    }
    private byte[] rowkeyRule(String rule,String ruleParam,Object field,String separator ,Object... concat){
        if(field==null||concat==null) {
            logger.warn("field,concat 不能为空" );
            return null;
        }
        String hashPrefix = null;
        Integer param = null;
        int paramLength = 0;
        int modLength =0;
        long  mod =0;
        byte[] rowkey =null;
        if("default".equals(separator)){
            separator = "";
        }
        byte[] bytes =null;
        if(field!=null) {
            bytes= ByteUtils.toBytes(field);
        }
        if(StringUtils.isNotBlank(ruleParam)) {
            try {
                param = Integer.valueOf(ruleParam);
            } catch (Exception e) {
                logger.error("ruleParam 格式错误", e.getMessage());
            }
        }
        switch (rule){
            //md5 加密
            case ParameterConstant.ROWKEY_RULE_HASH:
                if(StringUtils.isNotBlank(ruleParam)&&param!=null) {
                    hashPrefix = MD5Hash.getMD5AsHex(bytes).substring(0,param);
                }else{
                    hashPrefix = MD5Hash.getMD5AsHex(bytes).substring(0, 5);//默认去五位
                }
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //mod 取模
            case ParameterConstant.ROWKEY_RULE_MOD:
                if(param==null){
                    param = 1000;
                }

                if(field instanceof Integer ||field instanceof Short || field instanceof Long || field instanceof Byte ){
                    paramLength = String.valueOf(param).length();
                    mod =  Math.floorMod(  Long.valueOf(String.valueOf(field)),param);
                    modLength = String.valueOf(mod).length();
                }else{
                    int hashCode = field.hashCode();
                    paramLength = String.valueOf(param).length();
                    mod =  Math.floorMod(  hashCode,Long.valueOf(param));
                    modLength = String.valueOf(mod).length();
                }
                if(paramLength==modLength){
                    hashPrefix=""+mod;
                }else {
                    for (int i = 0; i < paramLength - modLength; i++) {
                        if (i == 0) {
                            hashPrefix = "0" + mod;
                        } else {
                            hashPrefix = "0" + hashPrefix;
                        }
                    }
                }

                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //mod 随机数
            case ParameterConstant.ROWKEY_RULE_RANDOM:
                if(param==null){
                    param = 1000;
                }
                mod = Math.floorMod(Math.round( Math.random()*100000),param);
                paramLength = String.valueOf(param).length();
                modLength = String.valueOf(mod).length();
                if(paramLength==modLength){
                    hashPrefix=""+mod;
                }else {
                    for (int i = 0; i < paramLength - modLength; i++) {
                        if (i == 0) {
                            hashPrefix = "0" + mod;
                        } else {
                            hashPrefix = "0" + hashPrefix;
                        }
                    }
                }
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //默认时间戳反转
            case ParameterConstant.ROWKEY_RULE_INVERTED:
                ;
                //;break;
                //时间戳反转
            case ParameterConstant.ROWKEY_RULE_INVERTED_TIME:
                hashPrefix =  new StringBuilder(String.valueOf(field)).reverse().toString();
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                Long time =Long.MAX_VALUE - Long.valueOf(String.valueOf(concat[0]));
                byte[] bytes2 = Bytes.toBytes(hashPrefix);
                bytes = ByteUtils.toBytes(time);
                rowkey = Bytes.add(bytes2, bytes);
                ;break;
            //字符串反转
            case ParameterConstant.ROWKEY_RULE_INVERTED_STRING:
                hashPrefix =  new StringBuilder(String.valueOf(field)).reverse().toString();
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //截取
            case ParameterConstant.ROWKEY_RULE_SUBSTRING:
                if(param==null)
                    param = 1;
                hashPrefix = String.valueOf(field).substring(0,param);
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;

            default:
                hashPrefix=String.valueOf(field);
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                break;

            //

        }
        //链接key
        if(rowkey==null) {
            byte[] bytes2 = Bytes.toBytes(hashPrefix);
            if (concat == null || concat.length == 0) {
                rowkey = bytes2;//Bytes.add(bytes2, bytes);
            } else {
                for (int i = 0; i < concat.length; i++) {
                    Object obj = concat[i];
                    bytes = ByteUtils.toBytes(obj);
                    if (i == 0) {
                        rowkey = Bytes.add(bytes2, bytes);
                    } else {
                        rowkey = Bytes.add(rowkey, bytes);
                    }
                }
            }
        }
        return rowkey;
    }

    @Override
    public synchronized void stop() {
        super.stop();
        closeTable();
    }

    @Override
    public void configure(Context context) {
        LOGGER.info("HbaseSink start configure ......");
        String zookeeper =  context.getString(ParameterConstant.HBASE_ZOOKEEPER_QUORUM);
        batchSize = context.getInteger(ParameterConstant.EVENT_BATCH_SIZE);
        String topicList = context.getString(ParameterConstant.TOPICLIST);
        JSONArray array = JSONObject.parseArray(topicList);
        if("default".equals(context.getString(ParameterConstant.IS_RUN_MODE))){
            isDefualt = 1;
        }
        taskCofigMap.put(ParameterConstant.HBASE_ZOOKEEPER_QUORUM,zookeeper);
        taskCofigMap.put(ParameterConstant.EVENT_BATCH_SIZE,batchSize);
        taskCofigMap.put(ParameterConstant.TOPICLIST,topicList);

        setTaskconfig(taskCofigMap);

        if(!HbaseUtils.init(zookeeper)){
            LOGGER.warn("Hbase 配置失败 ......");
            throw new ConfigurationException("Hbase 初始化连接失败失败");
        }

            String nameSpace = context.getString(ParameterConstant.NAMESPACE);
            String tableName = context.getString(ParameterConstant.TABLENAME);
            String table = org.apache.commons.lang3.StringUtils.isBlank(nameSpace)?tableName:nameSpace+":"+tableName;
            String series = context.getString(ParameterConstant.SERICES);
            String prefixlength = context.getString(ParameterConstant.PREFIX_LENGTH);
            String flushSize =context.getString(ParameterConstant.MEMSTORE_FLUSH_SIZE);
            context.put(ParameterConstant.TABLE,table);//最终的表名
            topicConfigMap.put("topic",JSON.parseObject(JSON.toJSONString(context)));
            if(!isExists(table)){
                LOGGER.warn("Hbase 表 :{}不存在",table);
                try {
                    createTable(table, series, prefixlength, flushSize);
                    LOGGER.info("Hbase 表 :{}自动创建成功",table);
                }catch (Exception e){
                    LOGGER.error("Hbase表 :{} 自动创建失败",table);
                    throw new ConfigurationException("Hbase 初始化连接失败失败");
                }
            }
        if (counter == null) {
            counter = new HbaseSinkCounter(getName());
        }
        LOGGER.info("HbaseSink configure finished");


    }

}

