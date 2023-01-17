package com.dfssi.dataplatform.datasync.plugin.http.valuechangeproxy;

import java.util.EventListener;

/**
 * Created by HSF on 2018/6/21.
 */
public interface ValueChangeListener extends EventListener {

    public abstract void performed(ValueChangedEvent e);
}