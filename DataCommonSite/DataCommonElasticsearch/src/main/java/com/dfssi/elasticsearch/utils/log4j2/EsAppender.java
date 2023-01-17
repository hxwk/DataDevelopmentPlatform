package com.dfssi.elasticsearch.utils.log4j2;

import com.dfssi.common.Dates;
import com.dfssi.common.JavaOps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.MapMessage;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/5 13:16
 */
@Plugin(name = "EsAppender", category = "Core", elementType = "appender", printObject = true)
public class EsAppender extends AbstractAppender {

    private Config config;
    private TransportClient client;
    private volatile BulkProcessor bulkProcessor;

    protected EsAppender(Config config, Filter filter,
                              Layout<? extends Serializable> layout) {
        super(config.name, filter, layout);
        this.config = config;
        LOGGER.info(String.format("Generated EsAppender: \n\t %s ", config));
        initEsClient();

    }

    private void initEsClient(){
        try {
            Map<String, String> esConf = config.esConf;
            String value = esConf.get("client.transport.sniff");
            boolean sniff = false;
            esConf.remove("client.transport.sniff");
            if("true".equalsIgnoreCase(value))sniff = true;

            value = esConf.get("client.transport.ping_timeout");
            String pingTimeout = "120s";
            esConf.remove("client.transport.ping_timeout");
            if(value != null) pingTimeout = value;

            Settings.Builder builder = Settings.builder()
                    .put("client.transport.sniff", sniff)
                    .put("client.transport.ping_timeout", pingTimeout)
                    //.put("client.transport.ignore_cluster_name", true) // 忽略集群名字验证, 打开后集群名字不对也能连接上
                    .put("cluster.name", config.cluster);

            Properties properties =  new Properties();
            properties.putAll(esConf);
            builder.putProperties(esConf, key -> {return key;});


            Settings settings = builder.build();
            client = new PreBuiltTransportClient(settings);

            Set<Map.Entry<String, Integer>> entries = config.hostAndPorts.entries();
            for (Map.Entry<String, Integer> entry : entries) {
                client.addTransportAddress(
                        new TransportAddress(InetAddress.getByName(entry.getKey()), entry.getValue()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("构建es客户端失败：\n\t %s", config), e);
        }
    }

    private void initBulkProcessor() {
        bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        if (response.hasFailures()) {
                            LOGGER.error(String.format("executionId = %s, FailureMessage = %s",
                                    executionId, response.buildFailureMessage()));
                        }
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        LOGGER.error(String.format("executionId = %s", executionId), failure);
                    }
                })
                // 5k次请求执行一次bulk
                .setBulkActions(config.maxActionsPerBulkRequest)
                // 1mb的数据刷新一次bulk
                .setBulkSize(config.getMaxVolumePerBulkRequest())
                //固定60s必须刷新一次
                .setFlushInterval(config.getFlushInterval())
                // 并发请求数量, 0不并发, 1并发允许执行
                .setConcurrentRequests(config.maxConcurrentBulkRequests)
                // 设置退避, 100ms后执行, 最大请求3次
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(200), 5))
                .build();

    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().isMoreSpecificThan(config.level)) {
            //写入es
        }
    }


    public void writeToEs(LogEvent event){

        String index = config.indexCreateRule.getIndexName(config.index, new Date(event.getTimeMillis()));

        Map<String, Object> record = Maps.newHashMap();
        if (event.getMessage() instanceof MapMessage) {
            record.putAll(((MapMessage)event.getMessage()).getData());
        } else {
            record.put("message", event.getMessage().getFormattedMessage());
        }
        record.put("name", config.name);
        record.put("host",  config.publisher);
        record.put("ti", event.getTimeMillis());
        record.put("", Dates.long2Str(event.getTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
        record.put("level", event.getLevel().toString());
        record.put("logger", event.getLoggerName());
        record.put("loggerFQDN", event.getLoggerFqcn());
        if (event.getMarker() !=null ) {
            record.put("marker", event.getMarker().toString());
        };
      /*  record.put("thread", ievent.threadName);
        if (event.getSource()!=null) {
            record.put("stack",event.getSource().toString());
        };
        if (event.getThrown()!=null) {
            record.put("throw",convThrowable(event.getThrown()));
        };*/
        record.put("context", event.getContextMap());
        IndexRequestBuilder d=		client.prepareIndex(index, config.type).setSource(record);

    }


    @PluginFactory
    public static synchronized EsAppender createAppender(
            @PluginAttribute(value = "name", defaultString = "elasticsearchAppender") String name,
            @PluginAttribute(value = "publisher", defaultString = "default") String publisher,
            @PluginAttribute(value = "servers", defaultString = "localhost:9300") String servers,
            @PluginAttribute(value = "cluster", defaultString = "es") String cluster,
            @PluginAttribute(value = "esConf") String esConf,
            @PluginAttribute(value = "index", defaultString ="logstash") String index,
            @PluginAttribute(value = "indexCreateRule", defaultString = "none") String indexCreateRule,
            @PluginAttribute(value = "type", defaultString = "logs") String type,
            @PluginAttribute(value = "maxActionsPerBulkRequest", defaultInt = 100) int maxActionsPerBulkRequest,
            @PluginAttribute(value = "maxConcurrentBulkRequests", defaultInt = 1) int maxConcurrentBulkRequests,
            @PluginAttribute(value = "maxVolumePerBulkRequest", defaultString = "10m") String maxVolumePerBulkRequest,
            @PluginAttribute(value = "flushInterval", defaultString = "5s") String flushInterval,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = false) boolean ignoreExceptions,
            @PluginAttribute(value = "level") Level level,
            @PluginElement("Filters") Filter filter) {

        if(level == null)level = Level.INFO;

        Config config = new Config();
        config.name = name;
        config.publisher = publisher;
        config.cluster = cluster;
        config.index = index;
        config.indexCreateRule = IndexCreateRule.getRule(indexCreateRule);
        config.type = type;
        config.maxActionsPerBulkRequest = maxActionsPerBulkRequest;
        config.maxConcurrentBulkRequests = maxConcurrentBulkRequests;
        config.maxVolumePerBulkRequest = maxVolumePerBulkRequest;
        config.flushInterval = flushInterval;
        config.ignoreExceptions = ignoreExceptions;
        config.level = level;

        config.hostAndPorts = HashMultimap.create();
        String[] split = servers.split(",");
        try {
            String[] kv;
            for(String hp : split){
                kv = hp.split(":");
                if(kv.length == 2){
                    config.hostAndPorts.put(kv[0], Integer.parseInt(kv[1]));
                }
            }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(String.format("servers：%s格式有误, 示例：host1:port1,host2:port2", servers), e);
        }

        config.esConf = Maps.newHashMap();
        if(esConf != null){
            split = esConf.split(",");
            String[] kv;
            for(String hp : split) {
                kv = hp.split(":");
                if(kv.length == 2){
                    config.esConf.put(kv[0], kv[1]);
                }
            }
        }

        return new EsAppender(config,filter,null);
    }

    private static class Config {
        String name;
        String publisher;

        String cluster;
        Map<String, String> esConf;
        HashMultimap<String, Integer> hostAndPorts;
        String index;
        IndexCreateRule indexCreateRule;
        String type;
        int maxActionsPerBulkRequest;
        int maxConcurrentBulkRequests;
        String maxVolumePerBulkRequest;
        String flushInterval;

        boolean ignoreExceptions;
        Level level;

        public ByteSizeValue getMaxVolumePerBulkRequest(){
            long kb = JavaOps.byteStringAsKb(maxVolumePerBulkRequest);
            return new ByteSizeValue(kb, ByteSizeUnit.KB);
        }

        public TimeValue getFlushInterval(){
            long sec = JavaOps.timeStringAsSec(flushInterval);
            return TimeValue.timeValueSeconds(sec);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Config{");
            sb.append("name='").append(name).append('\'');
            sb.append(", hostAndPorts=").append(hostAndPorts);
            sb.append(", cluster='").append(cluster).append('\'');
            sb.append(", index='").append(index).append('\'');
            sb.append(", indexCreateRule=").append(indexCreateRule);
            sb.append(", type='").append(type).append('\'');
            sb.append(", maxActionsPerBulkRequest=").append(maxActionsPerBulkRequest);
            sb.append(", maxConcurrentBulkRequests=").append(maxConcurrentBulkRequests);
            sb.append(", maxVolumePerBulkRequest='").append(maxVolumePerBulkRequest).append('\'');
            sb.append(", flushInterval='").append(flushInterval).append('\'');
            sb.append(", ignoreExceptions=").append(ignoreExceptions);
            sb.append(", level=").append(level);
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        long l = JavaOps.byteStringAsKb("10M");
        System.out.println(l);
    }
}
