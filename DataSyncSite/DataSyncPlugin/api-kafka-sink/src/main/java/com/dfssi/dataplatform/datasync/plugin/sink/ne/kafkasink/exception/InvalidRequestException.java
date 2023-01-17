package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.exception;

/**
 * Author: 程行荣
 * Time: 2013-11-12 17:50
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 无效的请求异常
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
    }
}
