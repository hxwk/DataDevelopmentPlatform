package com.dfssi.dataplatform.datasync.plugin.sink.hbase;


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

import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.*;
/**
 * many to one
 * TcpSource To HbaseSink
 * @author  chenf
 * @date 2018-10-23
 */
public class HbaseSink extends AbstractSink implements Configurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseSink.class);
    private Map<String,Object> taskCofigMap = new HashMap<String,Object>();
    private Map<String,JSONObject> topicConfigMap =  new HashMap<String,JSONObject>();
    private Map<String,JSONObject> adminMap = new HashMap<String,JSONObject>();
    private int batchSize;
    private int flushSize = 2;
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
        long start = System.currentTimeMillis();
         logger.info("Hbase Sink run time,before take:{},time:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),start);

        try {
            long processedEvents = 0;
            //批量获取event
            List<Event> eventlist = Lists.newArrayList();
            for (; processedEvents < batchSize; processedEvents++) {
                Event event = channel.take();
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
                eventlist.add(event);
            }
            long time1 = System.currentTimeMillis();
            logger.info("Hbase Sink run time,after take:{},time,time:{},diff:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),time1,time1-start);
            for(Event event :eventlist){
                Map<String, String> headers = event.getHeaders();
                byte[] eventBody = event.getBody();
                //logger.info("event:{}", JSON.toJSONString(event));
               //logger.info("body:{}", new String(eventBody, "UTF-8"));
                String eventTopic =null;
                String eventMsgId =null;
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
                if(eventTopic!=null&&eventMsgId!=null) {
                    JSONObject body = JSON.parseObject(new String(eventBody, "UTF-8"));

                    JSONObject topicConfig = topicConfigMap.get(eventTopic);
                    if(topicConfig ==null&& isDefualt==1){
                        topicConfig = new JSONObject();
                        topicConfig.put(ParameterConstant.SERICES,"f");
                        topicConfig.put(ParameterConstant.NAMESPACE,"dev");
                        String tn = eventMsgId.replaceAll("jts","").replaceAll("\\.","");
                        String tableName="terminal_"+tn;
                        topicConfig.put(ParameterConstant.TABLENAME,tableName);
                        topicConfig.put(ParameterConstant.TABLE,"dev:"+tableName);
                        topicConfigMap.put(eventTopic,topicConfig);
                    }else if(topicConfig ==null){
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
                         //addEvent(tableName, rowkey, series, body);
                        //加入批量处理list
                         addEventBatch(tableName,rowkey,series,body);
                    }
                    //当不指定rowkey规则时间用 时间戳反转规则 先用 vid 再用sim 卡号
                    else{
                        Object field =StringUtils.isBlank(body.getString("vid"))?body.getString("sim"):body.getString("vid");
                        List concat = Lists.newArrayList();
                        concat.add(System.currentTimeMillis());
                        rowkey = rowkeyRule(ParameterConstant.ROWKEY_RULE_INVERTED, null, field, separator, concat.toArray());
                        //addEvent(tableName, rowkey, series, body);
                        //加入批量处理list
                        addEventBatch(tableName,rowkey,series,body);
                    }
                    //计数,不严谨
                    counter.addToEventDrainSuccessCount(1);
                }
            }
             long time2 = System.currentTimeMillis();
            logger.info("Hbase Sink run time,before put:{},time,time:{},diff:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),time2,time2-time1);

            //提交
            batchDataList(flushSize);

            long time3 = System.currentTimeMillis();
            logger.info("Hbase Sink run time,after put:{},time,time:{},diff:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),time3,time3-time2);

            logger.info("Hbase Sink run time,after put :" +
                    "batchUnderFlowCount:{},success:{}",counter.getBatchCompleteCount(),counter.getEventDrainSuccessCount());

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
        flushSize =context.getInteger(ParameterConstant.FLUSH_SIZE,2);
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
       for(Object jo:array){
           JSONObject json = (JSONObject) jo;
           String nameSpace = json.getString(ParameterConstant.NAMESPACE);
           String tableName = json.getString(ParameterConstant.TABLENAME);
           String table = org.apache.commons.lang3.StringUtils.isBlank(nameSpace)?tableName:nameSpace+":"+tableName;
           String series = json.getString(ParameterConstant.SERICES);
           String prefixlength = json.getString(ParameterConstant.PREFIX_LENGTH);
           String flushSize =json.getString(ParameterConstant.MEMSTORE_FLUSH_SIZE);
           json.put(ParameterConstant.TABLE,table);//最终的表名
           topicConfigMap.put(json.getString(ParameterConstant.TOPIC),json);
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
       }
        if (counter == null) {
            counter = new HbaseSinkCounter(getName());
        }
        LOGGER.info("HbaseSink configure finished");


    }
    
}
