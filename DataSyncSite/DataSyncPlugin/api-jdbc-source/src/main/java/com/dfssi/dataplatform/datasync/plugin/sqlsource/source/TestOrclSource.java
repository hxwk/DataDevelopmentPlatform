package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import com.dfssi.dataplatform.datasync.flume.agent.Channel;
import com.dfssi.dataplatform.datasync.flume.agent.ChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.EventDeliveryException;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ReplicatingChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * flume , test sql source
 * @author jianKang
 * @date 2017/12/08
 */
public class TestOrclSource {
    static final Logger logger = LoggerFactory.getLogger(TestOrclSource.class);
    public static void main(String[] args) throws EventDeliveryException {
        JDBCSource source = new JDBCSource();
        source.setName("orclSourceTest");
        Channel channel = new MemoryChannel();
        Context channelContext = new Context();
        Context context = new Context();

        //add memory channel properties information
        channelContext.put("capacity",String.valueOf(1000000));
        channelContext.put("transactionCapacity", String.valueOf(100000));

        Configurables.configure(channel,channelContext);

        List<Channel> channels = Lists.newArrayList();
        channels.add(channel);

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(channels);
        source.setChannelProcessor(new ChannelProcessor(rcs));

        context.put("taskId", UUID.randomUUID().toString());
        //context.put("hibernate.connection.url","jdbc:oracle:thin:@172.16.1.243:1521/hello");
        context.put("hibernate.connection.url","jdbc:oracle:thin:@192.168.1.235:1521/test");
        context.put("hibernate.connection.user","vn_nd_1");
        context.put("hibernate.connection.password","123456");
        context.put("hibernate.temp.use_jdbc_metadata_defaults","false");
        context.put("table","GPS_201712");
        context.put("columns","['id','vid','sim','gps_time','alarm','state','lon','lat','speed','speed1','dir','alt','mile','fuel','signal_state','alarm_extra','ct']");
        //context.put("custom.query","select * from GPS_201712 where row_num<10");

        source.configure(context);
        logger.info(context.toString());
        source.start();
        source.process();

        LoggerSink sink = new LoggerSink();
        Context context12 = new Context();
        Configurables.configure(sink, context12);
        sink.setChannel(channel);
        sink.start();

        sink.process();
        sink.stop();
        source.stop();
    }
}
