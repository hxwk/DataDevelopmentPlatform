package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jian on 2017/12/11.
 */
public class LoggerSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory
            .getLogger(LoggerSink.class);

    // Default Max bytes to dump
    public static final int DEFAULT_MAX_BYTE_DUMP = 16;

    // Max number of bytes to be dumped
    private int maxBytesToLog = DEFAULT_MAX_BYTE_DUMP;

    public static final String MAX_BYTES_DUMP_KEY = "maxBytesToLog";

    @Override
    public void configure(Context context) {
        String strMaxBytes = context.getString(MAX_BYTES_DUMP_KEY);
        if (!Strings.isNullOrEmpty(strMaxBytes)) {
            try {
                maxBytesToLog = Integer.parseInt(strMaxBytes);
            } catch (NumberFormatException e) {
                logger.warn(String.format(
                        "Unable to convert %s to integer, using default value(%d) for maxByteToDump",
                        strMaxBytes, DEFAULT_MAX_BYTE_DUMP));
                maxBytesToLog = DEFAULT_MAX_BYTE_DUMP;
            }
        }
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        Event event = null;

        try {
            transaction.begin();
            event = channel.take();

            if (event != null) {
                if (logger.isInfoEnabled()) {
                    logger.info("Event: " + EventHelper.dumpEvent(event, maxBytesToLog));
                }
            } else {
                // No event found, request back-off semantics from the sink runner
                result = Status.BACKOFF;
            }
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new EventDeliveryException("Failed to log event: " + event, ex);
        } finally {
            transaction.close();
        }

        return result;
    }
}