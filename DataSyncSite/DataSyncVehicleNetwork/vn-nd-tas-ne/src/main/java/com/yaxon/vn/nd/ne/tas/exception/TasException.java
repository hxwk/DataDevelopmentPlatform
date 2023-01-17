package com.yaxon.vn.nd.ne.tas.exception;

/**
 * Author: 程行荣
 * Time: 2013-07-17 15:39
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * RPC异常。
 */
public final class TasException extends RuntimeException {

    private int code = 0;

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int CODEC_EXCEPTION = 3;
    public static final int TERMINAL_NO_LOGIN_EXCEPTION = 4;

    public TasException() {
        super();
    }

    public TasException(String message, Throwable cause) {
        super(message, cause);
    }

    public TasException(String message) {
        super(message);
    }

    public TasException(Throwable cause) {
        super(cause);
    }

    public TasException(int code) {
        super();
        this.code = code;
    }

    public TasException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public TasException(int code, String message) {
        super(message);
        this.code = code;
    }

    public TasException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
