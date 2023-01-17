package com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy;

/**
 * Created by HSF on 2018/6/21.
 */
import java.util.EventObject;

public class ValueChangedEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 767352958358520268L;
    private Object value;

    public ValueChangedEvent(Object source) {
        this(source, 0);
    }

    public ValueChangedEvent(Object source, Object newValue) {
        super(source);
        value = newValue;
    }

    public Object getValue() {
        return value;
    }
}