/**
 * Created by HSF on 2018/6/21.
 */


public class ValueChangeTest {
    public static void main(String[] args) {
        EventProducer producer = new EventProducer();
        producer.addListener(new EventConsumer());
        producer.setValue(2);
    }
}
