import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Description:
 *
 * @author Weijj
 * @version 2018/9/14 13:38
 */
public class AbsConsumer {

    public static void main(String[] args) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "172.16.1.121:9092");
        props.put("group.id", "abs_check");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("POSITIONINFORMATION_0200_TOPIC"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("value = %s%n",
                         record.value());

            }
        }

    }
}
