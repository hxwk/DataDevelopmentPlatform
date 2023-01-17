package com.dfssi.hbase.v2.exception;

import java.util.Iterator;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/4 11:33
 */
public class HBaseInitException extends IllegalArgumentException {

    public HBaseInitException(String message) {
        super(message);
    }

    public HBaseInitException() {
       this("hbase初始化失败。");
    }

    public final synchronized void addSuppresseds(Iterable<Exception> exceptions){

        if(exceptions != null){
            Iterator<Exception> iterator = exceptions.iterator();
            while (iterator.hasNext()){
                addSuppressed(iterator.next());
            }
        }
    }
}
