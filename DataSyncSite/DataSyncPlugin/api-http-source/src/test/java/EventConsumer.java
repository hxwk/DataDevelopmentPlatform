import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangeListener;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangedEvent;

/**
 * Created by HSF on 2018/6/21.
 */


public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangedEvent e) {
        System.out.println("value changed, new value = " + e.getValue());
    }
}