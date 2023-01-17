import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ListenerRegister;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangeListener;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangedEvent;

/**
 * Created by HSF on 2018/6/21.
 */
public class EventProducer {
    ListenerRegister register = new ListenerRegister();
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        if (value != newValue) {
            value = newValue;
            ValueChangedEvent event = new ValueChangedEvent(this, value);
            fireAEvent(event);
        }
    }

    public void addListener(ValueChangeListener a) {
        register.addListener(a);
    }

    public void removeListener(ValueChangeListener a) {
        register.removeListener(a);
    }

    public void fireAEvent(ValueChangedEvent event) {
        register.fireAEvent(event);
    }

}