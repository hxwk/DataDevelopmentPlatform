package com.dfssi.dataplatform.datasync.model.common;

import java.io.Serializable;

/**
 * Created by Hannibal on 2018-02-23.
 */
public interface Message
        extends Serializable
{
    public abstract String id();
}
