package com.dfssi.dataplatform.userhome.exception;

/**
 * 会话令牌创建异常
 * @author wanlong
 */
public class AccessTokenException extends Exception{

    public AccessTokenException(String message){
        super(message);
    }

    public AccessTokenException(String message, Throwable cause){
        super(message, cause);
    }
}
