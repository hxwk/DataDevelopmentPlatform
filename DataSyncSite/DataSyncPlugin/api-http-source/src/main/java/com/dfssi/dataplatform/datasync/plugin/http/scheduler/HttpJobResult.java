package com.dfssi.dataplatform.datasync.plugin.http.scheduler;

import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ListenerRegister;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangeListener;
import com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy.ValueChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * HttpExecuteJob执行结果传出类
 * Created by HSF on 2018/6/2.
 */
public class HttpJobResult {
    private volatile  String result = null;
    private  final Logger logger = LoggerFactory.getLogger(HttpJobResult.class);
    ListenerRegister register = new ListenerRegister();

    public HttpJobResult(){}

    public  String getResult() {
        return result;
    }

    public  void setResult(String newResult) {
        this.result = newResult;
        ValueChangedEvent event = new ValueChangedEvent(this, result);
        fireAEvent(event);
        logger.info("HttpJobResult->setResult {}",result);
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
