package com.dfssi.dataplatform.quartz.sync.evs;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *    从redis中定时同步更新数据到Greenplum中
 * @author LiXiaoCong
 * @version 2018/4/18 9:32
 */
public class EvsCountDataSynFromRedis {
    private final static Logger logger = LoggerFactory.getLogger(EvsCountDataSynFromRedis.class);

    private EvsConfig config;
    private NumberFormat nf;


    public EvsCountDataSynFromRedis(EvsConfig config){
        this.config = config;
        this.nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

    }

    public void executeSync() throws SQLException {
        Jedis jedis = config.getJedis();

        logger.info("开始同步数据");

        logger.info("开始同步全量统计数据...");
        syncData(jedis, "evsDetect:driving");
        logger.info("同步全量统计数据完成");

        logger.info("开始同步错误统计数据...");
        syncData(jedis, "evsDetect:err");
        logger.info("同步错误统计数据完成");

        jedis.close();
        config.closeConnection();
        config.closeJedisPool();
        logger.info("同步数据完成。");
    }

    private void syncData(Jedis jedis, String prefix) throws SQLException {
        Connection connection = config.getConnection();
        Statement statement = connection.createStatement();

        int n = 0;
        Map<String, String> record;
        String sql;
        Set<String> keys;
        String keyPattern;
        for(int i = 0; i <= config.getRedisKeyPartitions(); i++){
            keyPattern = String.format("%s:%s:*", prefix, i);
            keys = jedis.keys(keyPattern);

            for(String key : keys){
                record = jedis.hgetAll(key);
                if(checkRecordIsDeprecated(record)){
                    jedis.del(key);
                }else{
                    sql = createCountDataUpdateSql(key, record);
                    if(sql != null)statement.addBatch(sql);

                }
                n = n + 1;
                if(n >= config.getRedisFetchBatch()){
                    statement.executeBatch();
                    n = 0;
                }
            }
        }

        if(n > 0){
            statement.executeBatch();
        }
        statement.close();
    }

    private boolean checkRecordIsDeprecated(Map<String, String> record){
        String updatetime = record.get("updatetime");
        if(updatetime == null) return false;
        boolean b = false;
        try {
            b = (System.currentTimeMillis() - Long.parseLong(updatetime)) >= 24 * 60 * 60 * 1000L;
        } catch (NumberFormatException e) {
            logger.error(null, e);
        }
        return b;
    }

    private String createCountDataUpdateSql(String redisKey,
                                            Map<String, String> record){

        String sql = null;
        String[] recordTableAndVin = getRecordTableAndVin(redisKey);
        if(recordTableAndVin != null){

            switch (recordTableAndVin[3]){
                case "err":
                    sql = createErrUpdateSql(recordTableAndVin[0],
                            recordTableAndVin[2], recordTableAndVin[1], record);
                    break;
                case "driving":
                    sql = createDataUpdateSql(recordTableAndVin[0],
                            recordTableAndVin[2], recordTableAndVin[1], record);
                    break;
            }

        }
        return sql;
    }

    private String[] getRecordTableAndVin(String redisKey){

        String[] res = null;

        String[] split = redisKey.split(":");
        if(split.length == 5){
            String label = split[1];
            String day   = split[3];
            String vin   = split[4];

            String table;

            switch (label){
                case "err":
                    table = "total".equals(day) ?
                            config.getErrtotalTable() : config.getErrdayTable();
                    break;
                case "driving":
                    table = "total".equals(day) ?
                            config.getDatatotalTable() : config.getDatadayTable();
                    break;
                default:
                    return null;
            }
            res = new String[]{table, vin, day, label};
        }
        return res;
    }

    private String createErrUpdateSql(String table,
                                      String day,
                                      String vin,
                                      Map<String, String> record){

        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(table).append(" set ");

        boolean changed = false;
        Set<String> detectFieldSet = config.getDetectFieldSet();
        String value;
        for(String field : detectFieldSet){
            value = record.get(field);
            if(value != null){
                sql.append(field).append("=").append(value).append(",");
                changed = true;
            }
        }

        if(changed){
            sql.append(" count=").append(record.get("count"));
            sql.append(", logic_count=").append(record.get("logic_count"));
            sql.append(", detect_count=").append(record.get("detect_count"));
            sql.append(", endtime=").append(record.get("endtime"));
            sql.append(", updateTime=").append(record.get("updatetime"));
            if("total".equals(day)){
                sql.append(" where vin='").append(vin).append("'");
            }else{
                sql.append(" where vin='").append(vin).append("' and day=").append(day);
            }
        }else {
            sql = null;
        }
        return (sql != null) ? sql.toString() : null;
    }

    private String createDataUpdateSql(String table,
                                       String day,
                                       String vin,
                                       Map<String, String> record){

        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(table).append(" set");

        sql.append(" totalpower=").append(precision(record.get("totalpower"), 4));
        sql.append(", totalgpsmile=").append(precision(record.get("totalgpsmile"), 1));
        sql.append(", totalmile=").append(precision(record.get("totalmile"), 1));
        sql.append(", totaltime=").append(record.get("totaltime"));
        sql.append(", count=").append(record.get("count"));
        sql.append(", alarm1=").append(record.get("alarm1"));
        sql.append(", alarm2=").append(record.get("alarm2"));
        sql.append(", alarm3=").append(record.get("alarm3"));
        sql.append(", updateTime=").append(record.get("updatetime"));

        if("total".equals(day)){
            sql.append(", endtime=").append(record.get("endtime"));
            sql.append(", endmile=").append(noGroupDouble(record.get("endmile"), 1));

            sql.append(" where vin = '").append(vin).append("'");
        }else{
            sql.append(", latest_lon=").append(record.get("latest_lon"));
            sql.append(", latest_lat=").append(record.get("latest_lat"));
            sql.append(", latest_mile=").append(noGroupDouble(record.get("latest_mile"), 1));
            sql.append(", latest_voltage=").append(record.get("latest_voltage"));
            sql.append(", latest_electricity=").append(record.get("latest_electricity"));
            sql.append(", latest_time=").append(record.get("latest_time"));
            sql.append(", latest_status='").append(record.get("latest_status")).append("'");
            sql.append(", latest_alarm=").append(record.get("latest_alarm"));

            sql.append(String.format(" where vin = '%s' and day=%s", vin, day));
        }
        return sql.toString();
    }

    private double precision(Object value, int precision){
        double v = Double.parseDouble(value.toString());
        return  precisionDouble(v, precision);
    }

    private double precisionDouble(double value, int precision){
        BigDecimal bg = new BigDecimal(value);
        return  bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private String noGroupDouble(Object value){
        return noGroupDouble(value, 0);
    }

    private String noGroupDouble(Object value, int precision){
        double v = Double.parseDouble(value.toString());
        if(precision > 0){
            v = precisionDouble(v, precision);
        }

        return nf.format(v);
    }

    public static void main(String[] args) throws Exception {

        CommandLine line = parseArgs(args);
        String configDir = line.getOptionValue("configDir");
        String nameNodeHost = line.getOptionValue("nameNodeHost");
        int nameNodeWebhdfsPort = Integer.parseInt(
                line.getOptionValue("nameNodeWebhdfsPort", "50070"));

        logger.info("新能源数据redis-gp同步参数配置如下：\n");
        logger.info(String.format("configDir ： %s \n nameNodeHost  ： %s \n  nameNodeWebhdfsPort  ： %s",
                configDir,  nameNodeHost, nameNodeWebhdfsPort));

        EvsConfig config = new EvsConfig(nameNodeHost, nameNodeWebhdfsPort, configDir);
        EvsCountDataSynFromRedis evsCountDataSynFromRedis =
                new EvsCountDataSynFromRedis(config);
        evsCountDataSynFromRedis.executeSync();
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("help", false, "帮助 打印参数详情");
        options.addOption("configDir", true, "数据同步的配置目录");
        options.addOption("nameNodeHost", true, "cdh集群中hdfs的处于active状态下的namenode的ip或主机名");
        options.addOption("nameNodeWebhdfsPort", true, "webhdfs的端口， 默认：50070");


        CommandLineParser parser = new DefaultParser();
        CommandLine lines = parser.parse(options, args);
        if (lines.hasOption("help")
                || lines.getOptionValue("nameNodeHost") == null)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("EvsCountDataSynFromRedisJob", options);
            System.exit(0);
        }
        return lines;
    }

}
