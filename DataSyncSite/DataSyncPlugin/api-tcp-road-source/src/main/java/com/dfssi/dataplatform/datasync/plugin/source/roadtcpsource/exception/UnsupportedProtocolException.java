package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.exception;

/**
 * Time: 2013-11-12 17:50
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 不支持的协议异常
 */
public class UnsupportedProtocolException extends RuntimeException {
    public UnsupportedProtocolException() {
    }

    public UnsupportedProtocolException(String message) {
        super(message);
    }

    public UnsupportedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedProtocolException(Throwable cause) {
        super(cause);
    }
}
