package com.dfssi.dataplatform.datasync.plugin.interceptor.exception;

/**
 * unsupported protocol exception handle
 * @author jianKang
 * @date 2017/12/15
 */
public class UnsupportedProtocolException extends RuntimeException {
    public UnsupportedProtocolException(){

    }

    public UnsupportedProtocolException(String message){
        super(message);
    }

    public UnsupportedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedProtocolException(Throwable cause) {
        super(cause);
    }
}
