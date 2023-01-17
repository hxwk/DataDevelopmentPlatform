package com.dfssi.dataplatform.plugin.tcpnesource.exception;


/**
 * RPC异常。
 */
public final class TcpException extends RuntimeException {

    private int code = 0;

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int CODEC_EXCEPTION = 3;
    public static final int TERMINAL_NO_LOGIN_EXCEPTION = 4;

    public TcpException() {
        super();
    }

    public TcpException(String message, Throwable cause) {
        super(message, cause);
    }

    public TcpException(String message) {
        super(message);
    }

    public TcpException(Throwable cause) {
        super(cause);
    }

    public TcpException(int code) {
        super();
        this.code = code;
    }

    public TcpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public TcpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public TcpException(int code, Throwable cause) {
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
