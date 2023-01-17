package com.dfssi.hbase.v2.exception;

import java.util.Iterator;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/4 11:33
 */
public class HBaseInsertException extends Exception {

    public HBaseInsertException(String message) {
        super(message);
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
