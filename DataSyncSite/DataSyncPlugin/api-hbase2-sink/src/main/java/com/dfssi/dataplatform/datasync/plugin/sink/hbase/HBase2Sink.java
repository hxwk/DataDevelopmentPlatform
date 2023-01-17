/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.dfssi.dataplatform.datasync.plugin.sink.hbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.annotations.InterfaceAudience;
import com.dfssi.dataplatform.datasync.flume.agent.auth.FlumeAuthenticationUtil;
import com.dfssi.dataplatform.datasync.flume.agent.auth.PrivilegedExecutor;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.conf.ConfigurationException;
import com.dfssi.dataplatform.datasync.flume.agent.instrumentation.SinkCounter;
import com.dfssi.dataplatform.datasync.flume.agent.lifecycle.LifecycleState;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.EventTopicConfig;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.ParameterConstant;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.*;
import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.logger;

/**
 * A simple sink which reads events from a channel and writes them to HBase 2.
 * The HBase configuration is picked up from the first <tt>hbase-site.xml</tt>
 * encountered in the classpath. This sink supports batch reading of
 * events from the channel, and writing them to HBase, to minimize the number
 * of flushes on the HBase tables. To use this sink, it has to be configured
 * with certain mandatory parameters:<p>
 * <tt>table: </tt> The name of the table in HBase to write to. <p>
 * <tt>columnFamily: </tt> The column family in HBase to write to.<p>
 * This sink will commit each transaction if the table's write buffer size is
 * reached or if the number of events in the current transaction reaches the
 * batch size, whichever comes first.<p>
 * Other optional parameters are:<p>
 * <tt>serializer:</tt> A class implementing {@link HBase2EventSerializer}.
 * An instance of
 * this class will be used to write out events to HBase.<p>
 * <tt>serializer.*:</tt> Passed in the configure() method to serializer
 * as an object of {@link }.<p>
 * <tt>batchSize: </tt>This is the batch size used by the client. This is the
 * maximum number of events the sink will commit per transaction. The default
 * batch size is 100 events.
 * <p>
 * <p>
 * <strong>Note: </strong> While this sink flushes all events in a transaction
 * to HBase in one shot, HBase does not guarantee atomic commits on multiple
 * rows. So if a subset of events in a batch are written to disk by HBase and
 * HBase fails, the flume transaction is rolled back, causing flume to write
 * all the events in the transaction all over again, which will cause
 * duplicates. The serializer is expected to take care of the handling of
 * duplicates etc. HBase also does not support batch increments, so if
 * multiple increments are returned by the serializer, then HBase failure
 * will cause them to be re-written, when HBase comes back up.
 */
public class HBase2Sink extends AbstractSink implements Configurable, BatchSizeSupported {
  private Connection conn;
  private long batchSize;
  private final Configuration config;
  private static final Logger logger = LoggerFactory.getLogger(HBase2Sink.class);
  private RowKeyRuleHbase2EventSerializer serializer;
  private String kerberosPrincipal;
  private String kerberosKeytab;
  private boolean enableWal = true;
  private boolean batchIncrements = false;
  private SinkCounter sinkCounter;
  private PrivilegedExecutor privilegedExecutor;
  private int flushSize = 6;
  private static Map<String, EventTopicConfig> topicConfigMap = new HashMap<String, EventTopicConfig>();
  private Map<String, BufferedMutator> topicTable = new HashMap<>();
  // Internal hooks used for unit testing.
  private DebugIncrementsCallback debugIncrCallback = null;

  public HBase2Sink() {
    this(HBaseConfiguration.create());
  }

  public HBase2Sink(Configuration conf) {
    this.config = conf;
  }

  @VisibleForTesting
  @InterfaceAudience.Private
  HBase2Sink(Configuration conf, DebugIncrementsCallback cb) {
    this(conf);
    this.debugIncrCallback = cb;
  }

  @Override
  public void start() {
    Preconditions.checkArgument(topicConfigMap != null && topicConfigMap.size() >= 0, "Please call stop " +
            "before calling start on an old instance.");
    List tableNamelist = Lists.newArrayList();
    try {
      privilegedExecutor =
              FlumeAuthenticationUtil.getAuthenticator(kerberosPrincipal, kerberosKeytab);
    } catch (Exception ex) {
      sinkCounter.incrementConnectionFailedCount();
      throw new FlumeException("Failed to login to HBase using "
              + "provided credentials.", ex);
    }

    try {
      conn = privilegedExecutor.execute((PrivilegedExceptionAction<Connection>) () -> {
        conn = ConnectionFactory.createConnection(config);
        return conn;
      });

      for (Map.Entry<String, EventTopicConfig> entry : topicConfigMap.entrySet()) {
        String tableName = entry.getValue().getTable();
        tableNamelist.add(tableName);
        BufferedMutator table = getTableDefault(entry.getValue());//conn.getBufferedMutator(TableName.valueOf(tableName));
        topicTable.put(entry.getKey(), table);

      }

    } catch (Exception e) {
      sinkCounter.incrementConnectionFailedCount();
      logger.error("Could not load table, " + JSON.toJSONString(tableNamelist) +
              " from HBase", e);
      throw new FlumeException("Could not load table, " + JSON.toJSONString(tableNamelist) +
              " from HBase", e);
    }

    super.start();
    sinkCounter.incrementConnectionCreatedCount();
    sinkCounter.start();
    super.start();
  }

  @Override
  public void stop() {

    try {
      if (topicTable != null && topicTable.size() > 0) {
        for (Map.Entry<String, BufferedMutator> entry : topicTable.entrySet()) {
          entry.getValue().close();
        }
      }
      topicTable.clear();
    } catch (IOException e) {
      throw new FlumeException("Error closing table.", e);
    }
    try {
      if (conn != null) {
        conn.close();
      }
      conn = null;
    } catch (IOException e) {
      throw new FlumeException("Error closing connection.", e);
    }
    sinkCounter.incrementConnectionClosedCount();
    sinkCounter.stop();
    super.stop();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void configure(Context context) {
    logger.info("HbaseSink start configure ......");
    String zkQuorum = context.getString(ParameterConstant.HBASE_ZOOKEEPER_QUORUM);
    batchSize = context.getInteger(ParameterConstant.EVENT_BATCH_SIZE, 1000);
    flushSize = context.getInteger(ParameterConstant.FLUSH_SIZE, 6);

    String topicList = context.getString(ParameterConstant.TOPICLIST);

    JSONArray array = JSONObject.parseArray(topicList);

    for (Object jo : array) {
      JSONObject topicconfig = (JSONObject) jo;
      EventTopicConfig tf = new EventTopicConfig();
      String topic = topicconfig.getString(ParameterConstant.TOPIC);

      String serices = topicconfig.getString(ParameterConstant.SERICES);
      serices = StringUtils.isBlank(serices) ? serices : "f";
      tf.setFamily(serices);
      String table = topicconfig.getString(ParameterConstant.TABLE);
      if (StringUtils.isBlank(table)) {
        table = topicconfig.getString(ParameterConstant.TABLENAME);
      }
      if (StringUtils.isBlank(table)) {
        throw new InvalidParameterException("table can't be null ");
      }
      tf.setTable(table);
      if (table.indexOf(":") > 1) {
        String[] str = table.split(";");
        tf.setNamespace(str[0]);
      }
      tf.setTopic(topic);
      tf.setRule(topicconfig.getString(ParameterConstant.ROWKEY_RULE));
      tf.setField(topicconfig.getString(ParameterConstant.FIELD));
      tf.setPrefixLength(topicconfig.getInteger(ParameterConstant.PREFIX_LENGTH));
      tf.setParam(topicconfig.getString(ParameterConstant.ROWKEY_RULE_PARAM));
      List list = topicconfig.getJSONArray(ParameterConstant.ROWKEYCONCAT);
      tf.setConcat(list);
      topicConfigMap.put(topic, tf);
    }
    //原始不兼容
    serializer = new RowKeyRuleHbase2EventSerializer();
    serializer.configure(topicConfigMap);

    this.config.set(HConstants.ZOOKEEPER_QUORUM, zkQuorum);

    String hbaseZnode = context.getString(
            HBase2SinkConfigurationConstants.ZK_ZNODE_PARENT);
    if (hbaseZnode != null && !hbaseZnode.isEmpty()) {
      this.config.set(HConstants.ZOOKEEPER_ZNODE_PARENT, hbaseZnode);
    }
    // conn = ConnectionFactory.createConnection(config);
    sinkCounter = new SinkCounter(this.getName());
  }

  public Configuration getConfig() {
    return config;
  }

  @Override
  public Status process() throws EventDeliveryException {
    Status status = Status.READY;
    Channel channel = getChannel();
    Transaction txn = channel.getTransaction();
    //List<Row> actions = new LinkedList<>();
    //List<Increment> incs = new LinkedList<>();

    Map<String, List<Row>> actions = new HashMap<>();
    Map<String, List<Increment>> incs = new HashMap<>();
    try {
      txn.begin();

      if (serializer instanceof BatchAware) {
        ((BatchAware) serializer).onBatchStart();
      }
      long i = 0;
      long time1 = System.currentTimeMillis();
      for (; i < batchSize; i++) {
        Event event = channel.take();
        if (event == null) {
          if (i == 0) {
            status = Status.BACKOFF;
            sinkCounter.incrementBatchEmptyCount();
             time1 = System.currentTimeMillis();
            logger.info("Hbase Sink run time, take:{},time,time:{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), time1);

          } else {
            sinkCounter.incrementBatchUnderflowCount();
          }
          break;
        } else {
          serializer.initialize(event);
          String topic = serializer.getEventTopic();

          List<Row> actionsList = actions.get(topic);
          List<Increment> incsList = incs.get(topic);
          if (actions.get(topic) == null) {
            actionsList = Lists.newArrayList();

          }
          if (incs.get(topic) == null) {
            incsList = Lists.newArrayList();

          }
          actionsList.addAll(serializer.getActions());
          incsList.addAll(serializer.getIncrements());
          actions.put(topic, actionsList);
          incs.put(topic, incsList);

        }
      }
      long time2 = System.currentTimeMillis();
      logger.info("Hbase Sink run time,before put:{},time,time:{},diff:{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), time2, time2 - time1);

      if (i == batchSize) {
        sinkCounter.incrementBatchCompleteCount();
      }
      sinkCounter.addToEventDrainAttemptCount(i);

      putEventsAndCommit(actions, incs, txn);

      long time3 = System.currentTimeMillis();
      logger.info("Hbase Sink run time,after put:{},time,time:{},diff:{},diff2:{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), time3, time3 - time2,time3-time1);

      logger.info("Hbase Sink run time,after put :" +
              "batchUnderFlowCount:{},success:{},attemptCount:{}",
              sinkCounter.getBatchCompleteCount(),
              sinkCounter.getEventDrainSuccessCount(),
              sinkCounter.getEventDrainAttemptCount());


    } catch (Throwable e) {
      try {
        txn.rollback();
      } catch (Exception e2) {
        logger.error("Exception in rollback. Rollback might not have been " +
                "successful.", e2);
      }
      logger.error("Failed to commit transaction." +
              "Transaction rolled back.", e);
      //sinkCounter.incrementEventWriteOrChannelFail(e);
      sinkCounter.incrementConnectionFailedCount();
      if (e instanceof Error || e instanceof RuntimeException) {
        logger.error("Failed to commit transaction." +
                "Transaction rolled back.", e);
        Throwables.propagate(e);
      } else {
        logger.error("Failed to commit transaction." +
                "Transaction rolled back.", e);
        throw new EventDeliveryException("Failed to commit transaction." +
                "Transaction rolled back.", e);
      }
    } finally {
      txn.close();
    }
    return status;
  }

  private void putEventsAndCommit(final String topic, final List<Row> actions,
                                  final List<Increment> incs, Transaction txn) throws Exception {

    privilegedExecutor.execute((PrivilegedExceptionAction<Void>) () -> {
      final List<Mutation> mutations = new ArrayList<>(actions.size());
      for (Row r : actions) {
        if (r instanceof Put) {
          ((Put) r).setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
        }
        // Newer versions of HBase - Increment implements Row.
        if (r instanceof Increment) {
          ((Increment) r).setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
        }
        if (r instanceof Mutation) {
          mutations.add((Mutation) r);
        } else {
          logger.warn("dropping row " + r + " since it is not an Increment or Put");
        }
      }
      BufferedMutator table = topicTable.get(topic);
      table.mutate(mutations);
      table.flush();
      return null;
    });

    privilegedExecutor.execute((PrivilegedExceptionAction<Void>) () -> {

      List<Increment> processedIncrements;
      if (batchIncrements) {
        processedIncrements = coalesceIncrements(incs);
      } else {
        processedIncrements = incs;
      }

      // Only used for unit testing.
      if (debugIncrCallback != null) {
        debugIncrCallback.onAfterCoalesce(processedIncrements);
      }
      BufferedMutator table = topicTable.get(topic);
      for (final Increment i : processedIncrements) {
        i.setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
        table.mutate(i);
      }
      table.flush();
      return null;
    });

    txn.commit();
    sinkCounter.addToEventDrainSuccessCount(actions.size());
  }


  private void putEventsAndCommit(final Map<String, List<Row>> actions,
                                  final Map<String, List<Increment>> incs, Transaction txn) throws Exception {

    privilegedExecutor.execute((PrivilegedExceptionAction<Void>) () -> {
      for (Map.Entry<String, List<Row>> entry : actions.entrySet()) {
        final List<Mutation> mutations = new ArrayList<>(actions.size());
        for (Row r : entry.getValue()) {
          if (r instanceof Put) {
            ((Put) r).setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
          }
          // Newer versions of HBase - Increment implements Row.
          if (r instanceof Increment) {
            ((Increment) r).setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
          }
          if (r instanceof Mutation) {
            mutations.add((Mutation) r);
          } else {
            logger.warn("dropping row " + r + " since it is not an Increment or Put");
          }
        }
        BufferedMutator table = topicTable.get(entry.getKey());
        if (table != null) {
          table.mutate(mutations);
          table.flush();
        }
      }
      return null;
    });


    privilegedExecutor.execute((PrivilegedExceptionAction<Void>) () -> {
      for (Map.Entry<String, List<Increment>> entry : incs.entrySet()) {
        List<Increment> processedIncrements;
        if (batchIncrements) {
          processedIncrements = coalesceIncrements(entry.getValue());
        } else {
          processedIncrements = entry.getValue();
        }

        // Only used for unit testing.
        if (debugIncrCallback != null) {
          debugIncrCallback.onAfterCoalesce(processedIncrements);
        }
        BufferedMutator table = topicTable.get(entry.getKey());
        if (table != null) {
          for (final Increment i : processedIncrements) {
            i.setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
            table.mutate(i);
          }
          table.flush();
        }
      }
      return null;
    });

    txn.commit();
    sinkCounter.addToEventDrainSuccessCount(actions.size());
  }


  @SuppressWarnings("unchecked")
  private Map<byte[], NavigableMap<byte[], Long>> getFamilyMap(Increment inc) {
    Preconditions.checkNotNull(inc, "Increment required");
    return inc.getFamilyMapOfLongs();
  }

  /**
   * Perform "compression" on the given set of increments so that Flume sends
   * the minimum possible number of RPC operations to HBase per batch.
   *
   * @param incs Input: Increment objects to coalesce.
   * @return List of new Increment objects after coalescing the unique counts.
   */
  private List<Increment> coalesceIncrements(Iterable<Increment> incs) {
    Preconditions.checkNotNull(incs, "List of Increments must not be null");
    // Aggregate all of the increment row/family/column counts.
    // The nested map is keyed like this: {row, family, qualifier} => count.
    Map<byte[], Map<byte[], NavigableMap<byte[], Long>>> counters =
            Maps.newTreeMap(Bytes.BYTES_COMPARATOR);
    for (Increment inc : incs) {
      byte[] row = inc.getRow();
      Map<byte[], NavigableMap<byte[], Long>> families = getFamilyMap(inc);
      for (Map.Entry<byte[], NavigableMap<byte[], Long>> familyEntry : families.entrySet()) {
        byte[] family = familyEntry.getKey();
        NavigableMap<byte[], Long> qualifiers = familyEntry.getValue();
        for (Map.Entry<byte[], Long> qualifierEntry : qualifiers.entrySet()) {
          byte[] qualifier = qualifierEntry.getKey();
          Long count = qualifierEntry.getValue();
          incrementCounter(counters, row, family, qualifier, count);
        }
      }
    }

    // Reconstruct list of Increments per unique row/family/qualifier.
    List<Increment> coalesced = Lists.newLinkedList();
    for (Map.Entry<byte[], Map<byte[], NavigableMap<byte[], Long>>> rowEntry :
            counters.entrySet()) {
      byte[] row = rowEntry.getKey();
      Map<byte[], NavigableMap<byte[], Long>> families = rowEntry.getValue();
      Increment inc = new Increment(row);
      for (Map.Entry<byte[], NavigableMap<byte[], Long>> familyEntry : families.entrySet()) {
        byte[] family = familyEntry.getKey();
        NavigableMap<byte[], Long> qualifiers = familyEntry.getValue();
        for (Map.Entry<byte[], Long> qualifierEntry : qualifiers.entrySet()) {
          byte[] qualifier = qualifierEntry.getKey();
          long count = qualifierEntry.getValue();
          inc.addColumn(family, qualifier, count);
        }
      }
      coalesced.add(inc);
    }

    return coalesced;
  }

  /**
   * Helper function for {@link #coalesceIncrements} to increment a counter
   * value in the passed data structure.
   *
   * @param counters  Nested data structure containing the counters.
   * @param row       Row key to increment.
   * @param family    Column family to increment.
   * @param qualifier Column qualifier to increment.
   * @param count     Amount to increment by.
   */
  private void incrementCounter(
          Map<byte[], Map<byte[], NavigableMap<byte[], Long>>> counters,
          byte[] row, byte[] family, byte[] qualifier, Long count) {

    Map<byte[], NavigableMap<byte[], Long>> families =
            counters.computeIfAbsent(row, k -> Maps.newTreeMap(Bytes.BYTES_COMPARATOR));

    NavigableMap<byte[], Long> qualifiers =
            families.computeIfAbsent(family, k -> Maps.newTreeMap(Bytes.BYTES_COMPARATOR));

    qualifiers.merge(qualifier, count, (a, b) -> a + b);
  }

  String getHBbaseVersionString() {
    return VersionInfo.getVersion();
  }

  private int getMajorVersion(String version) throws NumberFormatException {
    return Integer.parseInt(version.split("\\.")[0]);
  }

  private boolean hasVersionAtLeast2() {
    String version = getHBbaseVersionString();
    try {
      if (this.getMajorVersion(version) >= 2) {
        return true;
      }
    } catch (NumberFormatException ex) {
      logger.error(ex.getMessage());
    }
    logger.error("Invalid HBase version for hbase2sink:" + version);
    return false;
  }

  @VisibleForTesting
  @InterfaceAudience.Private
  HBase2EventSerializer getSerializer() {
    return serializer;
  }

  @Override
  public long getBatchSize() {
    return batchSize;
  }


  @VisibleForTesting
  @InterfaceAudience.Private
  interface DebugIncrementsCallback {
    void onAfterCoalesce(Iterable<Increment> increments);
  }


  public BufferedMutator getTableDefault(EventTopicConfig topic) {
    BufferedMutator table = topicTable.get(topic.getTopic());
    if (table == null) {
      try {
        if (conn.getAdmin().tableExists(TableName.valueOf(topic.getTable()))) {
          table = conn.getBufferedMutator(TableName.valueOf(topic.getTable()));
        } else {
          String f = StringUtils.isBlank(topic.getFamily()) ? "f" : topic.getFamily();
          Integer pl = topic.getPrefixLength();
          pl = pl == null || pl == 0 ? 4 : pl;
          createTable(topic.getTable(), f, String.valueOf(pl), "");
          table = conn.getBufferedMutator(TableName.valueOf(topic.getTable()));
        }
        topicTable.put(topic.getTopic(), table);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    return table;
  }

  /**
   * 创建表
   *
   * @param tableName
   * @param seriesStr
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public void createTable(String tableName, String seriesStr, String prefix_length, String memStore)
          throws IllegalArgumentException, IOException {
    Admin admin = null;
    TableName table = TableName.valueOf(tableName);
    try {
      if (conn == null) {
        init();
      }
      admin = conn.getAdmin();

      if (!admin.tableExists(table)) {
        System.out.println(tableName + " table not Exists");
        HTableDescriptor descriptor = new HTableDescriptor(table);
        //为空的时候用hbase的默认配置
        if (org.apache.commons.lang3.StringUtils.isNotBlank(prefix_length) && Integer.valueOf(prefix_length) != null) {
          descriptor.setValue(HTableDescriptor.SPLIT_POLICY, KeyPrefixRegionSplitPolicy.class.getName());// 指定策略
          descriptor.setValue("prefix_split_key_policy.prefix_length", prefix_length);
        }
        String[] series = seriesStr.split(",");
        for (String s : series) {
          descriptor.addFamily(new HColumnDescriptor(s.getBytes()));
        }
        admin.createTable(descriptor);
      }
    } finally {
      IOUtils.closeQuietly(admin);

    }
  }

  public static Map<String, EventTopicConfig> getTopicConfigMap() {
    return topicConfigMap;
  }
}
