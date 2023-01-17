package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink;

import com.dfssi.dataplatform.datasync.common.common.EventHeader;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.dfssi.dataplatform.datasync.flume.agent.event.EventBuilder;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.ByteBufUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.Constants;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static com.dfssi.dataplatform.datasync.common.common.EventHeader.HEADER_TOPIC;
import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.KafkaSinkConstants.*;
import static org.junit.Assert.fail;

/**
 * @author JianKang
 * @date 2018/4/3
 * @description
 */
public class TestNESinkKafka {
    @Test
    public void setup() {
        Context context = new Context();
        context.put(BOOTSTRAP_SERVERS_CONFIG, "172.16.1.121:9092");
        context.put(BATCH_SIZE, "2");
        context.put(HEADER_TOPIC, Constants.NEREALTIMEDATAREPORT_TOPIC);
        byte[] data = createObject();

        Map<String, String> headers = Maps.newHashMap();
        headers.put("taskid", UUID.randomUUID().toString());
        headers.put(EventHeader.HEADER_COMMANDSIGN, "02");
        headers.put(EventHeader.HEADER_VIN, "1a2b3c4d5e6f7g");
        try {
            Sink.Status status = prepareAndSend(context, data, headers);
            if (status == Sink.Status.BACKOFF) {
                fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
    }

    private static Sink.Status prepareAndSend(Context context, byte[] msg, Map<String, String> headers)
            throws EventDeliveryException {
        Sink kafkaSink = new KafkaSink();

        //从消息头获取是02还是03
        String commandSign = headers.get(EventHeader.HEADER_COMMANDSIGN);
        if ("02".equals(commandSign)) {
            context.put(TOPIC_CONFIG, Constants.NEREALTIMEDATAREPORT_TOPIC);
        } else if ("03".equals(commandSign)) {
            context.put(TOPIC_CONFIG, Constants.NESUPPLEMENTDATAREPORT_TOPIC);
        }
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        //Channel kafkaChannel = new KafkaChannel();
        //Context context1 = new Context();
        //context1.put(KafkaChannelConfiguration.KEY_HEADER,"12b3b4b##02##topicabc123");
        //Configurables.configure(kafkaChannel,context1);
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();

        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg, headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();

        return kafkaSink.process();
    }

    private static byte[] createObject() {
        String data = "110C190D052C0103030107FF007FFFFF0D20274C31013607FF3131020101FF7F4E2007FF7F07FF2744050006CE7F5F01D17C970601010DAD01020DAC010144010144";
        ByteBuf bb = ByteBufUtils.hexStringToByteBuf(data);
        return bb.array();
    }
}
