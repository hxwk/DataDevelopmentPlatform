package com.dfssi.dataplatform.datasync.service.util.zkutil;

/**
 * Created by HSF on 2017/12/13.
 * 由具体的监听者自己去实现接口
 */
public interface ZkStateListener {
    void reconnected();
}