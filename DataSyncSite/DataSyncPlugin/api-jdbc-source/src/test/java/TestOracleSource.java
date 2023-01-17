import com.dfssi.dataplatform.datasync.flume.agent.Channel;
import com.dfssi.dataplatform.datasync.flume.agent.ChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.EventDeliveryException;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ChannelProcessor;
import com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel;
import com.dfssi.dataplatform.datasync.flume.agent.channel.ReplicatingChannelSelector;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurables;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.source.JDBCSource;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.source.LoggerSink;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.source.OrclSqlSource;
import com.google.common.collect.Lists;

import static com.dfssi.dataplatform.datasync.plugin.sqlsource.common.SqlSourceParamConstants.*;
import java.util.List;

/**
 * test oracle jdbc source
 * Created on 2017/12/11.
 * @author JianKang
 */
public class TestOracleSource {

    public static void main(String[] args) {
        JDBCSource jdbcSqlSource = new JDBCSource();
        jdbcSqlSource.setName("oracle source");

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

        jdbcSqlSource.setChannelProcessor(new ChannelProcessor(rcs));

        context.put("jdbcUrl","jdbc:mysql://172.16.1.241:3306/metadata");
        context.put("username","app_user");
        context.put("password","112233");
        context.put("hibernate.connection.autocommit",AUTO_COMMIT);
        context.put("hibernate.dialect",MYSQL_DIALECT);
        context.put("connection.provider_class",CONN_PROVIDER_CLASS);
        context.put("hibernate.c3p0.min_size",C3P0_MINSIZE);
        context.put("maxPoolSize",C3P0_MAXSIZE);
        context.put("startNum",START_FROM);
        context.put("numStep","10");
        context.put("driverClass",MYSQL_CONN_DRIVER_CLASS);
        context.put("custom.query","select * from meta_dataresource_conf_t");
        context.put("tableName","meta_dataresource_conf_t");
        context.put("columns","*");
        context.put("batch.size",BATCH_SIZE);
        context.put("max.rows",MAX_ROWS);

        jdbcSqlSource.configure(context);
        //sink.stop();

        jdbcSqlSource.start();
        try {
            jdbcSqlSource.process();
        } catch (EventDeliveryException e) {
            e.printStackTrace();
        }

        LoggerSink sink = new LoggerSink();
        Context context12 = new Context();
        Configurables.configure(sink, context12);
        sink.setChannel(channel);
        sink.start();
        try {
            sink.process();
        } catch (EventDeliveryException e) {
            e.printStackTrace();
        }

    }
}
