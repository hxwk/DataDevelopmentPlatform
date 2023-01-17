package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.common.EventHeader;
import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ConfigurationException;
import com.dfssi.dataplatform.datasync.flume.agent.conf.LogPrivacyUtil;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.kafka.KafkaSinkCounter;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze.InformationPH;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.GeodeTool;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.KafkaSinkConstants;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.MessageHandlerReader;
import com.google.common.base.Throwables;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

import static com.dfssi.dataplatform.datasync.plugin.sink.kafka.KafkaSinkConstants.BOOTSTRAP_SERVERS_CONFIG;
import static com.dfssi.dataplatform.datasync.plugin.sink.kafka.KafkaSinkConstants.TOPIC_HEADER;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.Constants._32960_02;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.Constants._32960_03;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.KafkaSinkConstants.TOPIC_CONFIG;

/**
 * @author: apache open source
 * @Modifier: JianKang
 * @Modify Time:2018-04-03 10:12
 * @Content: support NE protocol parse 02,03
 */
public class KafkaSink extends AbstractSink implements Configurable {
    private static final Logger logger = LoggerFactory.getLogger(KafkaSink.class);

    private final Properties kafkaProps = new Properties();
    private KafkaProducer<String, byte[]> producer;

    private String topic;
    private int batchSize;
    private List<Future<RecordMetadata>> kafkaFutures;
    private KafkaSinkCounter counter;
    private String partitionHeader = null;
    private Integer staticPartitionId = null;

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = null;
        Event event;
        String eventTopic=null;
        String eventKey;
        String topicKey = null;
        //get parse real or fill message object
        try {
            long processedEvents = 0;

            transaction = channel.getTransaction();
            transaction.begin();

            kafkaFutures.clear();
            long batchStartTime = System.nanoTime();
            for (; processedEvents < batchSize; processedEvents += 1) {
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
                //处理event
                Map<String, String> headers = event.getHeaders();
                String header = headers.get(EventHeader.HEADER_KEY);
                byte[] eventBody = serializeEvent(event);
                //logger.info("before header:{},eventBody:{}",header,new String(eventBody,"utf-8"));

                //handle protocol message
                String commandSignStr;
                String msgid = null;
                String vin = null;
                String upMsgBody ;
                if(StringUtils.isNotEmpty(header)) {
                    eventTopic = (String) JSONObject.parseObject(header).get(TOPIC_HEADER);
                    msgid = (String) JSONObject.parseObject(header).get(EventHeader.MSGID);
                    vin = (String)JSONObject.parseObject(header).get(EventHeader.HEADER_VIN);
                }else{
                    logger.warn("event key is null");
                }
                if(_32960_02.equals(msgid)||_32960_03.equals(msgid)){
                    commandSignStr = msgid.substring(6);
                    logger.debug("commandSignStr:{}",commandSignStr);
                    upMsgBody = handleNE(eventBody,commandSignStr,vin);
                    if(StringUtils.isNotEmpty(upMsgBody)){
                        event.setBody(upMsgBody.getBytes());
                    }
                }else{
                    event.setBody(eventBody);
                 }

                //if (StringUtils.isNotBlank(upMsgBody)) {
                    //event.setBody(upMsgBody.getBytes());
                    //logger.debug(" 重新封装event发送到kafka >>>");

                    if (null != topicKey) {
                        eventTopic = topicKey;
                    }
                    if (null == eventTopic) {
                        eventTopic = topic;
                    }
                    eventKey = headers.get(KafkaSinkConstants.KEY_HEADER);
                    if (logger.isTraceEnabled()) {
                        if (LogPrivacyUtil.allowLogRawData()) {
                            logger.trace("{Event} " + eventTopic + " : " + eventKey + " : "
                                    + new String(eventBody, "UTF-8"));
                        } else {
                            logger.trace("{Event} " + eventTopic + " : " + eventKey);
                        }
                    }
                    logger.debug("event #{}", processedEvents);

                    // create a message and add to buffer
                    long startTime = System.currentTimeMillis();

                    Integer partitionId = null;
                    try {
                        ProducerRecord<String, byte[]> record;
                        if (staticPartitionId != null) {
                            partitionId = staticPartitionId;
                        }
                        //Allow a specified header to override a static ID
                        if (partitionHeader != null) {
                            String headerVal = event.getHeaders().get(partitionHeader);
                            if (headerVal != null) {
                                partitionId = Integer.parseInt(headerVal);
                            }
                        }
                        if (partitionId != null) {
                            record = new ProducerRecord<>(eventTopic, partitionId, eventKey,
                                    serializeEvent(event));
                        } else {
                            record = new ProducerRecord<>(eventTopic, eventKey,
                                    serializeEvent(event));
                        }
                        kafkaFutures.add(producer.send(record, new SinkCallback(startTime)));
                    } catch (NumberFormatException ex) {
                        throw new EventDeliveryException("Non integer partition id specified", ex);
                    } catch (Exception ex) {
                        throw new EventDeliveryException("Could not send event", ex);
                    }
            }

            //Prevent linger.ms from holding the batch
            producer.flush();

            // publish batch and commit.
            if (!kafkaFutures.isEmpty()) {
                for (Future<RecordMetadata> future : kafkaFutures) {
                    future.get();
                }
                long endTime = System.nanoTime();
                counter.addToKafkaEventSendTimer((endTime - batchStartTime) / (1000 * 1000));
                counter.addToEventDrainSuccessCount(Long.valueOf(kafkaFutures.size()));
            }

            transaction.commit();

        } catch (Exception ex) {
            String errorMsg = "Failed to publish events";
            logger.error("Failed to publish events", ex.getMessage());
            result = Status.BACKOFF;
            if (transaction != null) {
                try {
                    kafkaFutures.clear();
                    transaction.rollback();
                    counter.incrementRollbackCount();
                } catch (Exception e) {
                    logger.error("Transaction rollback failed", e.getMessage());
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

    public synchronized String handleNE(byte[] body,String commandSignStr,String vin){
        ProtoMsg protoMsg = new ProtoMsg();
        InformationPH handlersManager = new InformationPH();
        //protoMsg.msgId = Short.parseShort(commandSignStr);
//        protoMsg.bytes = body;
        protoMsg.dataBuf = Unpooled.copiedBuffer(body);
        protoMsg.vin = vin;
        protoMsg.commandSign=Byte.parseByte(commandSignStr);
        String upMsgBody = handlersManager.doUpMsg(protoMsg);
        return upMsgBody;
    }

    @Override
    public synchronized void start() {
        MessageHandlerReader.getInstance();

        GeodeTool.getInstance();
        // instantiate the producer
        producer = new KafkaProducer<String, byte[]>(kafkaProps);
        counter.start();
        super.start();
    }

    @Override
    public synchronized void stop() {
        producer.close();
        counter.stop();
        logger.info("Kafka Sink {} stopped. Metrics: {}", getName(), counter);
        super.stop();
    }


    /**
     * We configure the sink and generate properties for the Kafka Producer
     *
     * Kafka producer properties is generated as follows:
     * 1. We generate a properties object with some static defaults that
     * can be overridden by Sink configuration
     * 2. We add the configuration users added for Kafka (parameters starting
     * with .kafka. and must be valid Kafka Producer properties
     * 3. We add the sink's documented parameters which can override other
     * properties
     *
     * @param context
     */
    @Override
    public void configure(Context context) {
        context.put(BOOTSTRAP_SERVERS_CONFIG,context.getString("kafkaAddress"));
        translateOldProps(context);

        String topicStr = context.getString("kafkaTopic");
        if (topicStr == null || topicStr.isEmpty()) {
            topicStr = KafkaSinkConstants.DEFAULT_TOPIC;
            logger.warn("Topic was not specified. Using {} as the topic.", topicStr);
        } else {
            logger.info("Using the static topic {}. This may be overridden by event headers", topicStr);
        }

        topic = topicStr;

        batchSize = context.getInteger(KafkaSinkConstants.BATCH_SIZE, KafkaSinkConstants.DEFAULT_BATCH_SIZE);

        if (logger.isDebugEnabled()) {
            logger.debug("Using batch size: {}", batchSize);
        }

        partitionHeader = context.getString(KafkaSinkConstants.PARTITION_HEADER_NAME);
        staticPartitionId = context.getInteger(KafkaSinkConstants.STATIC_PARTITION_CONF);

        kafkaFutures = new LinkedList<Future<RecordMetadata>>();

        String bootStrapServers = context.getString(KafkaSinkConstants.BOOTSTRAP_SERVERS_CONFIG);
        if (bootStrapServers == null || bootStrapServers.isEmpty()) {
            throw new ConfigurationException("Bootstrap Servers must be specified");
        }

        setProducerProps(context, bootStrapServers);

        if (logger.isDebugEnabled() && LogPrivacyUtil.allowLogPrintConfig()) {
            logger.debug("Kafka producer properties: {}", kafkaProps);
        }

        if (counter == null) {
            counter = new KafkaSinkCounter(getName());
        }
    }

    private void translateOldProps(Context ctx) {

        if (!(ctx.containsKey(TOPIC_CONFIG))) {
            ctx.put(TOPIC_CONFIG, ctx.getString("topic"));
            logger.warn("{} is deprecated. Please use the parameter {}", "topic", TOPIC_CONFIG);
        }
        //Broker List
        // If there is no value we need to check and set the old param and log a warning message
        if (!(ctx.containsKey(KafkaSinkConstants.BOOTSTRAP_SERVERS_CONFIG))) {
            String brokerList = ctx.getString(KafkaSinkConstants.BROKER_LIST_FLUME_KEY);
            if (brokerList == null || brokerList.isEmpty()) {
                throw new ConfigurationException("Bootstrap Servers must be specified.");
            } else {
                ctx.put(KafkaSinkConstants.BOOTSTRAP_SERVERS_CONFIG, brokerList);
                logger.warn("{} is deprecated. Please use the parameter {}",
                        KafkaSinkConstants.BROKER_LIST_FLUME_KEY, KafkaSinkConstants.BOOTSTRAP_SERVERS_CONFIG);
            }
        }

        //batch Size
        if (!(ctx.containsKey(KafkaSinkConstants.BATCH_SIZE))) {
            String oldBatchSize = ctx.getString(KafkaSinkConstants.OLD_BATCH_SIZE);
            if (oldBatchSize != null && !oldBatchSize.isEmpty()) {
                ctx.put(KafkaSinkConstants.BATCH_SIZE, oldBatchSize);
                logger.warn("{} is deprecated. Please use the parameter {}.", KafkaSinkConstants.OLD_BATCH_SIZE, KafkaSinkConstants.BATCH_SIZE);
            }
        }

        // Acks
        if (!(ctx.containsKey(KafkaSinkConstants.KAFKA_PRODUCER_PREFIX + ProducerConfig.ACKS_CONFIG))) {
            String requiredKey = ctx.getString(
                    KafkaSinkConstants.REQUIRED_ACKS_FLUME_KEY);
            if (!(requiredKey == null) && !(requiredKey.isEmpty())) {
                ctx.put(KafkaSinkConstants.KAFKA_PRODUCER_PREFIX + ProducerConfig.ACKS_CONFIG, requiredKey);
                logger.warn("{} is deprecated. Please use the parameter {}.", KafkaSinkConstants.REQUIRED_ACKS_FLUME_KEY,
                        KafkaSinkConstants.KAFKA_PRODUCER_PREFIX + ProducerConfig.ACKS_CONFIG);
            }
        }

        if (ctx.containsKey(KafkaSinkConstants.KEY_SERIALIZER_KEY)) {
            logger.warn("{} is deprecated. Flume now uses the latest Kafka producer which implements " +
                            "a different interface for serializers. Please use the parameter {}",
                    KafkaSinkConstants.KEY_SERIALIZER_KEY, KafkaSinkConstants.KAFKA_PRODUCER_PREFIX + ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
        }

        if (ctx.containsKey(KafkaSinkConstants.MESSAGE_SERIALIZER_KEY)) {
            logger.warn("{} is deprecated. Flume now uses the latest Kafka producer which implements " +
                            "a different interface for serializers. Please use the parameter {}",
                    KafkaSinkConstants.MESSAGE_SERIALIZER_KEY,
                    KafkaSinkConstants.KAFKA_PRODUCER_PREFIX + ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
        }
    }

    private void setProducerProps(Context context, String bootStrapServers) {
        kafkaProps.put(ProducerConfig.ACKS_CONFIG, KafkaSinkConstants.DEFAULT_ACKS);
        //Defaults overridden based on config.
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaSinkConstants.DEFAULT_KEY_SERIALIZER);
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSinkConstants.DEFAULT_VALUE_SERIAIZER);
        kafkaProps.putAll(context.getSubProperties(KafkaSinkConstants.KAFKA_PRODUCER_PREFIX));
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
    }

    protected Properties getKafkaProps() {
        return kafkaProps;
    }

    private byte[] serializeEvent(Event event) throws IOException {
        byte[] bytes;
        bytes = event.getBody();
        return bytes;
    }

    private static Map<CharSequence, CharSequence> toCharSeqMap(Map<String, String> stringMap) {
        Map<CharSequence, CharSequence> charSeqMap = new HashMap<CharSequence, CharSequence>();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            charSeqMap.put(entry.getKey(), entry.getValue());
        }
        return charSeqMap;
    }

}

class SinkCallback implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(SinkCallback.class);
    private long startTime;

    public SinkCallback(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            logger.debug("Error sending message to Kafka {} ", exception.getMessage());
        }

        if (logger.isDebugEnabled()) {
            long eventElapsedTime = System.currentTimeMillis() - startTime;
            logger.debug("Acked message partition:{} ofset:{}", metadata.partition(), metadata.offset());
            logger.debug("Elapsed time for send: {}", eventElapsedTime);
        }
    }
}

