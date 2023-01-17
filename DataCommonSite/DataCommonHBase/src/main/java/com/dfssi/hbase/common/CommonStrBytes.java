package com.dfssi.hbase.common;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by lulin on 2016/12/9.
 *
 * 常用的一些符号以及对应的Hbase中的字节
 */
public class CommonStrBytes {

    public static final String SPACE = " ";

    public static final String HASH = "#";
    public static final byte[] HASH_BYTES = Bytes.toBytes(HASH);

    public static final String UNDERLINE = "_";
    public static final byte[] UNDERLINE_BYTES = Bytes.toBytes(UNDERLINE);

    public static final String PIPE = "|";
    public static final byte[] PIPE_BYTES = Bytes.toBytes(PIPE);

    public static final String ETL_MAC = "000MACMACMAC";
    public static final byte[] ETL_MAC_BYTES = Bytes.toBytes(ETL_MAC);

    public static final String ETL_PLATE = "ETL_PLATE";
    public static final byte[] ETL_PLATE_BYTES = Bytes.toBytes(ETL_MAC);

    public static final String Frequency = "frequency";
    public static final String SumDuration = "sumDuration";
    public static final String Sitecode = "sitecode";
    public static final String Timestamp = "timestamp";
    public static final String Isvalid = "isvalid";
    public static final String EtlDate = "lastEtlDate";
    public static final String Events = "events";
    public static final String Devicenum = "devicenum";
    public static final String UserTarget = "userTarget";
    public static final String Detecting = "detecting";
    public static final String Rule = "Rule";
    public static final String deviceEtlTime = "deviceEtlTime";
    public static final String deviceEtlDate = "deviceEtlDate";
    public static final String behave = "Behave";
    public static final String detour = "detour";
    public static final String direction = "dir";

    public static final byte[] cf_event = Bytes.toBytes("Event");
    public static final byte[] cf_recentEvent = Bytes.toBytes("RecentEvent");
    public static final byte[] cf_records = Bytes.toBytes("Records");
    public static final byte[] cf_anomaly = Bytes.toBytes("Anomaly");
    public static final byte[] cf_pairStat = Bytes.toBytes("PairStat");
    public static final byte[] cf_rule = Bytes.toBytes(Rule);
    public static final byte[] cf_behave = Bytes.toBytes(behave);


    public static final byte[] cl_timestamp = Bytes.toBytes(Timestamp);
    public static final byte[] cl_sitecode = Bytes.toBytes(Sitecode);
    public static final byte[] cl_frequency = Bytes.toBytes(Frequency);
    public static final byte[] cl_SD = Bytes.toBytes(SumDuration);
    public static final byte[] cl_isvalid = Bytes.toBytes(Isvalid);
    public static final byte[] cl_lastEtlDate = Bytes.toBytes(EtlDate);
    public static final byte[] cl_events = Bytes.toBytes(Events);
    public static final byte[] cl_devicenum = Bytes.toBytes(Devicenum);
    public static final byte[] cl_detour = Bytes.toBytes(detour);
    public static final byte[] cl_direction = Bytes.toBytes(direction);

    public static final byte[] cl_userTarget = Bytes.toBytes(UserTarget);
    public static final byte[] cl_detecting = Bytes.toBytes(Detecting);

    public static final byte[] cl_exponent = Bytes.toBytes("exponent");
    public static final byte[] cl_prob = Bytes.toBytes("probability");

}
