package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.common.DBHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by  2017/12/11.
 * @author OPEN SOURCE ,JianKang
 */
@Deprecated
public class OrclSqlSource extends AbstractSource implements EventDrivenSource, Configurable, PollableSource {
    private static final Logger logger = LoggerFactory.getLogger(OrclSqlSource.class);

    private int startNum;
    private String table;

    private int NUM_STEP;
    private DBHelper dbHelper;
    private Set<String> columns;

    @Override
    public synchronized void start() {
        logger.info("oracle sources start");
        super.start();
    }

    @Override
    public synchronized void stop() {
        logger.info("oracle sources stop");
        super.stop();
        //link pool shut down
        dbHelper.close();
    }

    @Override
    public void configure(Context context) {
        logger.info("oracle sources configure start....");
        String username = context.getString("username");
        String password = context.getString("password");
        String jdbcUrl = context.getString("jdbcUrl");
        int poolSize = context.getString("poolSize") == null ? 2 : context.getInteger("poolSize");
        int maxPoolSize = context.getString("maxPoolSize") == null ? poolSize :
                context.getInteger("maxPoolSize");
        int maxIdleTime = context.getString("maxIdleTime") == null ? 30 :
                context.getInteger("maxIdleTime");

        table = context.getString("table");
        startNum = context.getInteger("startNum");
        NUM_STEP = context.getInteger("numStep");

        columns = Sets.newHashSet(context.getString("columns").split(","));
        dbHelper = new DBHelper(jdbcUrl, username, password, poolSize, maxPoolSize, maxIdleTime);
    }

    @Override
    public Status process() throws EventDeliveryException {
        try {
            List<Event> eventList = Lists.newArrayList();
            Event event;
            Map<String, String> headers;
            List<JSONObject> list = dbHelper.query(table, startNum, startNum + NUM_STEP, columns);
            for(JSONObject message : list){
                headers = Maps.newHashMap();
                headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                headers.put("table", table);
                headers.put("info", "oracle source");
                event = new SimpleEvent();
                byte[] body = message.toJSONString().getBytes();
                event.setHeaders(headers);
                event.setBody(body);
                eventList.add(event);
            }
            this.getChannelProcessor().processEventBatch(eventList);
            return Status.READY;
        }catch (Exception e){
            logger.error("oracle source process error", e.getMessage());
            return Status.BACKOFF;
        }
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }
}