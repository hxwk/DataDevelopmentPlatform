package com.dfssi.dataplatform.quartz.sync.ccv.fuel;

import com.dfssi.dataplatform.quartz.util.ByteBufferRedisClient;
import com.dfssi.dataplatform.quartz.util.Encoders;
import com.dfssi.dataplatform.quartz.util.MathUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/16 19:14
 */
public class FuelDataSyncFromRedis {
    private static final Logger logger = LoggerFactory.getLogger(FuelDataSyncFromRedis.class);

    private FuelDataSyncConfig fuelDataSyncConfig;

    public FuelDataSyncFromRedis(FuelDataSyncConfig fuelDataSyncConfig){
        this.fuelDataSyncConfig = fuelDataSyncConfig;
    }

    public void executeSync(){

        Jedis jedis = fuelDataSyncConfig.getJedis();
        ByteBufferRedisClient redisClient = new ByteBufferRedisClient(jedis);

        logger.info("开始同步行程数据..");
        try {
            syncTrip(redisClient);
            logger.info("同步行程数据完成。");
        } catch (SQLException e) {
            logger.error("同步行程数据失败。", e);
        }

        logger.info("开始同步累计数据..");
        try {
            syncTotal(redisClient);
            logger.info("同步累计数据完成。");
        } catch (SQLException e) {
            logger.error("同步累计数据失败。", e);
        }finally {
            redisClient.close();
            fuelDataSyncConfig.closeConnection();
            fuelDataSyncConfig.closeJedisPool();
        }
        System.out.println("同步任务结束。");
    }

    private void syncTrip(ByteBufferRedisClient redisClient) throws SQLException {
        String sql = String.format("update %s set isover=?, endtime=?, interval=?, endtotalmile=?,  endtotalfuel=?, endlat=?, endlon=?, isvalid=?, totalmile=?, totalfuel=? where id=?", fuelDataSyncConfig.getTripTable());
        PreparedStatement preparedStatement = fuelDataSyncConfig.getConnection().prepareStatement(sql);
        Set<byte[]> keys;
        for (int i = 0; i <= fuelDataSyncConfig.getRedisKeyPartitions(); i++) {
            keys = redisClient.keys(String.format("ccv:trip:%s:*", i));
            syncTripByKeys(redisClient, keys, preparedStatement);
        }

        preparedStatement.close();
    }

    private void syncTripByKeys(ByteBufferRedisClient redisClient,
                                Set<byte[]> keys,
                                PreparedStatement preparedStatement) throws SQLException {
        String id;
        long starttime;
        long endtime;
        double totalmile;
        double totalfuel;
        double endtotalmile;
        double endtotalfuel;
        double endlat;
        double endlon;
        int isover;
        int isvalid;
        ByteBuffer byteBuffer;
        ByteBuf buf;
        int n = 0;
        for(byte[] key : keys){
            byteBuffer = redisClient.get(key);
            if(byteBuffer != null){
                n += 1;
                buf = Unpooled.wrappedBuffer(byteBuffer);
                id = Encoders.Strings.decode(buf);
                Encoders.Strings.decode(buf);
                Encoders.Strings.decode(buf);
                starttime = Long.parseLong(Encoders.Strings.decode(buf));
                endtime = Long.parseLong(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                totalmile = Double.parseDouble(Encoders.Strings.decode(buf));
                totalfuel = Double.parseDouble(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                endtotalmile = Double.parseDouble(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                endtotalfuel = Double.parseDouble(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                endlat = Double.parseDouble(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                endlon = Double.parseDouble(Encoders.Strings.decode(buf));
                Encoders.Strings.decode(buf);
                isover = Integer.parseInt(Encoders.Strings.decode(buf));
                isvalid = Integer.parseInt(Encoders.Strings.decode(buf));

                preparedStatement.setInt(1, isover);
                preparedStatement.setLong(2, endtime);
                preparedStatement.setLong(3, endtime - starttime);
                preparedStatement.setDouble(4, MathUtil.rounding(endtotalmile, 1));
                preparedStatement.setDouble(5, MathUtil.rounding(endtotalfuel, 5));
                preparedStatement.setDouble(6, endlat);
                preparedStatement.setDouble(7, endlon);
                preparedStatement.setInt(8, isvalid);
                preparedStatement.setDouble(9, MathUtil.rounding(totalmile, 1));
                preparedStatement.setDouble(10, MathUtil.rounding(totalfuel, 5));
                preparedStatement.setString(11, id);

                preparedStatement.addBatch();
            }
            if(n >= fuelDataSyncConfig.getRedisFetchBatch()){
                preparedStatement.executeBatch();
                n = 0;
            }
        }
        if(n > 0){
            preparedStatement.executeBatch();
        }
    }

    private void syncTotal(ByteBufferRedisClient redisClient) throws SQLException {

        String sql = String.format("update %s set endtime=?, totaltime=?, totalmile=?, totalfuel=? where vid=? ", fuelDataSyncConfig.getTotalTable());
        PreparedStatement preparedStatement = fuelDataSyncConfig.getConnection().prepareStatement(sql);
        Set<byte[]> keys;
        for (int i = 0; i <= fuelDataSyncConfig.getRedisKeyPartitions(); i++) {
            keys = redisClient.keys(String.format("ccv:total:%s:*", i));
            syncTotalByKeys(redisClient, keys, preparedStatement);
        }
        preparedStatement.close();

    }

    private void syncTotalByKeys(ByteBufferRedisClient redisClient,
                                Set<byte[]> keys,
                                PreparedStatement preparedStatement) throws SQLException {
        String vid;
        long endtime;
        long totaltime;
        double totalmile;
        double totalfuel;
        ByteBuffer byteBuffer;
        ByteBuf buf;
        int n = 0;
        for(byte[] key : keys){
            byteBuffer = redisClient.get(key);
            if(byteBuffer != null){
                n += 1;
                buf = Unpooled.wrappedBuffer(byteBuffer);
                vid = Encoders.Strings.decode(buf);
                endtime = Long.parseLong(Encoders.Strings.decode(buf));
                totaltime = Long.parseLong(Encoders.Strings.decode(buf));
                totalmile = Double.parseDouble(Encoders.Strings.decode(buf));
                totalfuel = Double.parseDouble(Encoders.Strings.decode(buf));


                preparedStatement.setLong(1, endtime);
                preparedStatement.setLong(2, totaltime);
                preparedStatement.setDouble(3, MathUtil.rounding(totalmile, 1));
                preparedStatement.setDouble(4, MathUtil.rounding(totalfuel, 5));
                preparedStatement.setString(5, vid);

                preparedStatement.addBatch();
            }
            if(n >= fuelDataSyncConfig.getRedisFetchBatch()){
                preparedStatement.executeBatch();
                n = 0;
            }
        }
        if(n > 0){
            preparedStatement.executeBatch();
        }
    }

    public static void main(String[] args) throws ParseException {

        CommandLine line = parseArgs(args);
        String configDir = line.getOptionValue("configDir");
        String nameNodeHost = line.getOptionValue("nameNodeHost");
        int nameNodeWebhdfsPort = Integer.parseInt(
                line.getOptionValue("nameNodeWebhdfsPort", "50070"));

        logger.info("新能源数据redis-gp同步参数配置如下：\n");
        logger.info(String.format("configDir ： %s \n nameNodeHost  ： %s \n  nameNodeWebhdfsPort  ： %s",
                configDir,  nameNodeHost, nameNodeWebhdfsPort));

        FuelDataSyncConfig fuelDataSyncConfig = new FuelDataSyncConfig(nameNodeHost, nameNodeWebhdfsPort, configDir);
        FuelDataSyncFromRedis fuelDataSyncFromRedis = new FuelDataSyncFromRedis(fuelDataSyncConfig);
        fuelDataSyncFromRedis.executeSync();
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
            formatter.printHelp("FuelDataSyncFromRedis", options);
            System.exit(0);
        }
        return lines;
    }
}
