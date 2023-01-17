package com.dfssi.dataplatform.datasync.plugin.source.crawler;

import com.dfssi.dataplatform.datasync.flume.agent.Channel;
import com.dfssi.dataplatform.datasync.flume.agent.ChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ReplicatingChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.dfssi.dataplatform.datasync.flume.agent.sink.NullSink;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * flume , test taildir source and crawler
 * @author jianKang
 * @date 2017/11/21
 */
public class TestCrawlerTaildir {
    public static void main(String[] args) {
        CrawlerSource source = new CrawlerSource();
        Channel channel = new MemoryChannel();
        Context channelContext = new Context();
        Context context = new Context();

        channelContext.put("capacity",String.valueOf(1000000));
        channelContext.put("transactionCapacity", String.valueOf(100000));

        Configurables.configure(channel,channelContext);

        List<Channel> channels = Lists.newArrayList();
        channels.add(channel);

        ChannelSelector rsc = new ReplicatingChannelSelector();
        rsc.setChannels(channels);
        source.setChannelProcessor(new ChannelProcessor(rsc));



        context.put("crawlerIntervalTime", "5");
        context.put("filegroups","ssi");
        context.put("positionFile" , "/home/hadoop/flume1.7/tmp/taildir_position.json");
        //context.put("positionFile" , "D:\\test\\data\\taildir_position.json");
        //context.put("filegroups.ssi", "/home/kangj/flume1.7/tmp/^[0-9A-Za-z\\-]+.dat");
        context.put("filegroups.ssi", "/home/hadoop/flume1.7/tmp/^[0-9A-Za-z]+.dat");
        context.put("batchSize","2");
        context.put("filePaths", "/home/hadoop/flume1.7/tmp/");
        context.put("headers.ssi.headerKeyTest","value1");
        context.put("fileHeader","true");
        context.put("fileHeaderKey","path");



        source.configure(context);
        //TCPClient client1 = new TCPClient();

        NullSink sink = new NullSink();
        sink.setChannel(channel);
        //CountingSinkRunner sinkRunner = new CountingSinkRunner(sink);

        //sinkRunner.start();
        source.start();
        source.process();
    }
}
