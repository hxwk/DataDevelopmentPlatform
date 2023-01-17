package com.dfssi.dataplatform.datasync.plugin.source.crawler;

/**
 * flume
 * @author JK
 * @date 2017/11/21
 */
public class PollableSourceConstants {
    public static final String BACKOFF_SLEEP_INCREMENT = "backoffSleepIncrement";
    public static final String MAX_BACKOFF_SLEEP = "maxBackoffSleep";
    public static final long DEFAULT_BACKOFF_SLEEP_INCREMENT = 1000L;
    public static final long DEFAULT_MAX_BACKOFF_SLEEP = 5000L;

    public PollableSourceConstants() {
    }
}
