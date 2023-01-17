package com.dfssi.dataplatform.plugin.tcpnesource.exception;

/**
 * Author: 程行荣
 * Time: 2013-11-12 17:50
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 格式不良的协议异常
 */
public class BadFormattedProtocolException extends RuntimeException {
    public BadFormattedProtocolException() {
    }

    public BadFormattedProtocolException(String message) {
        super(message);
    }

    public BadFormattedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadFormattedProtocolException(Throwable cause) {
        super(cause);
    }
}
