package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.JavaOps;
import com.dfssi.common.databases.DBType;
import com.dfssi.dataplatform.analysis.geo.SSIGeoCodeService;
import com.dfssi.dataplatform.analysis.redis.SentinelConnectionPool;
import com.dfssi.dataplatform.analysis.utils.DataBaseConnectionManager;
import com.dfssi.resources.ConfigDetail;
import com.dfssi.resources.Resources;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/16 21:52
 */
public class FuelConfig extends ConfigDetail {
    private static String _CONFIG = "/fuel/fuel";
    private transient Logger logger;

    //数据过滤相关
    //高程最小边界  用于判断是否为高工况
    private double altMin;
    //高程最大边界  用于判断数据是否有误
    private double altMax;
    //数据时间边界  即小于这个时间的数据 均认为是有误的
    private long minTime;
    //最大方向   超过这个值 均认为是有误的
    private int maxDirection;

    private double lonMin;
    private double lonMax;

    private double latMin;
    private double latMax;

    //数据输的url  spark sql专用
    private String url;

    //数据连接ID，根据 此ID可到connectionManager中获取连接
    private String fuelConnectionID;
    private String orderConnectionID;

    //实时油耗表
    private String fueltable;
    //实时行程表
    private String triptable;

    //实时油耗异常表
    private String abnormalfueltable;
    //百公里油耗上限
    private double maxConsumption;

    private String abnormaldrivingtable;
    private Map<String, Integer> alarmLabelMap;
    private String abnormaldrivingReportUrl;

    private String totalfueltable;

    private String ordertable;

    //行程切割相关
    private long tv;
    private long ta;
    private long tr;
    private int da;

    private DataBaseConnectionManager connectionManager;
    private SSIGeoCodeService ssiGeoCodeService;

    private transient SentinelConnectionPool.SentinelRedisEndpoint redisEndpoint;
    private int redisPartition = 20;

    public FuelConfig(){
        this("none", true);
    }

    public FuelConfig(Map<String, String> config, boolean createTableIfNotExist){
        super();
        this.logger = LoggerFactory.getLogger(FuelConfig.class);
        logger.info("开始配置读取及初始化...");

        this.configMap.putAll(config);
        init(createTableIfNotExist);
    }

    public FuelConfig(String envStr, boolean createTableIfNotExist){
        super();
        this.logger = LoggerFactory.getLogger(FuelConfig.class);
        logger.info("开始配置读取及初始化...");

        try {
            if(envStr == null) envStr = "none";
            Resources.Env env = Resources.Env.newInstance(envStr);
            Resources resources = new Resources(env, _CONFIG);
            this.configMap.putAll(resources.getConfigMap());
        } catch (Exception e) {
            logger.error("读取油耗基础配置失败。", e);
        }

      init(createTableIfNotExist);
    }

    private void init(boolean createTableIfNotExist){

        this.connectionManager = new DataBaseConnectionManager();

        initFilterConfig();
        initTripConfig();
        initOutPutConfig(createTableIfNotExist);
        //initOrderConfig();

        String url = getConfigItemValue("rgeocode.server");
        ssiGeoCodeService = new SSIGeoCodeService(url);

        logger.info(String.format("配置读取及初始化完成: \n\t %s", this.configMap));
    }


    private void initFilterConfig(){
        this.altMin = getConfigItemDouble("fuel.filter.altitude.min.threshold", 1000.0);
        this.altMax = getConfigItemDouble("fuel.filter.altitude.max.threshold", 8000.0);
        this.minTime = getConfigItemLong("fuel.filter.uploadtime.threshold",  1483200000000L);
        this.maxDirection = getConfigItemInteger("fuel.filter.direction.threshold", 360);

        this.lonMin = getConfigItemDouble("fuel.filter.lon.min.threshold", -180.0);
        this.lonMax = getConfigItemDouble("fuel.filter.lon.max.threshold", 180.0);

        this.latMin = getConfigItemDouble("fuel.filter.lat.min.threshold", -90.0);
        this.latMax = getConfigItemDouble("fuel.filter.lat.max.threshold", 90.0);

    }

    private void initOutPutConfig(boolean createTableIfNotExist){

        this.fuelConnectionID = getConfigItemValue("fuel.out.database.id", "fuel");

        String typeStr = getConfigItemValue("fuel.out.database.type", "postgresql");
        DBType type = DBType.newDBType(typeStr);
        Preconditions.checkNotNull(type, String.format("数据库类型fuel.order.database.type有误：%s", typeStr));

        String baseUrl = getConfigItemValue("fuel.out.database.url");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(baseUrl), "fuel.out.database.url 不能为空。");

        String user = getConfigItemValue("fuel.out.database.user");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user), "fuel.out.database.user 不能为空。");

        String password = getConfigItemValue("fuel.out.database.password");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "fuel.out.database.password 不能为空。");

        this.url = String.format("%s?user=%s&password=%s", baseUrl, user, password);

        String driver = getConfigItemValue("fuel.out.database.driver");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(driver), "fuel.out.database.driver 不能为空。");

        //增加油耗的数据库连接
        DataBaseConnectionManager.DataBaseConnection dataBaseConnection =
                new DataBaseConnectionManager.DataBaseConnection(type, baseUrl, driver, user, password);
        Connection connection = dataBaseConnection.getConnection();
        Preconditions.checkNotNull(connection, String.format("获取数据库连接失败：\n\t %s", dataBaseConnection.toString()));
        this.connectionManager.addConnection(fuelConnectionID, dataBaseConnection);

        this.fueltable = getConfigItemValue("fuel.out.database.fuel.table", "vehicle_fuel");
        this.triptable = getConfigItemValue("fuel.out.database.trip.table", "vehicle_trip");
        this.abnormalfueltable = getConfigItemValue("fuel.out.database.abnormalfuel.table", "vehicle_abnormal_fuel");
        this.abnormaldrivingtable = getConfigItemValue("fuel.out.database.abnormaldriving.table", "vehicle_abnormal_driving");
        this.totalfueltable = getConfigItemValue("fuel.out.database.totalfuel.table", "vehicle_total_fuel");

        if(createTableIfNotExist) {
            FuelTables.checkAndCreateFuelTable(fueltable, connection);
            FuelTables.checkAndCreateTripTable(triptable, connection);
            //FuelTables.checkAndCreateAbnormalFuelTable(abnormalfueltable, connection);
            FuelTables.checkAndCreateAbnormalDrivingTable(abnormaldrivingtable, connection);
            FuelTables.checkAndCreateTotalFuelTable(totalfueltable, connection);
        }

        Set<String> pairs = getConfigItemSet("fuel.out.database.abnormaldriving.label.pair");
        if(pairs.isEmpty())pairs = Sets.newHashSet("疲劳:0,打电话:1,抽烟:2,未系安全带:3".split(","));
        this.alarmLabelMap = Maps.newHashMap();
        for(String pair : pairs){
            addAlarmLabelPair(pair.split(":"));
        }
        this.abnormaldrivingReportUrl = getConfigItemValue("fuel.out.database.abnormaldriving.report.url");

        this.connectionManager.closeConnection(fuelConnectionID);
        this.maxConsumption = getConfigItemDouble("fuel.out.database.min.abnormalfuel", 15.0);

    }

    private void addAlarmLabelPair(String[] pair){
        if(pair.length == 2){
            try {
                String alarm = pair[0];
                int label = Integer.parseInt(pair[1]);
                this.alarmLabelMap.put(alarm, label);
            } catch (NumberFormatException e) {
            }
        }
    }

    private void initTripConfig(){
        this.tv = JavaOps.timeStringAsMs(getConfigItemValue("fuel.trip.split.tv", "5m"));
        this.ta = JavaOps.timeStringAsMs(getConfigItemValue("fuel.trip.split.ta", "15m"));
        this.tr = JavaOps.timeStringAsMs(getConfigItemValue("fuel.trip.split.tr", "30m"));

        this.da = getConfigItemInteger("fuel.trip.split.da", 1);
    }

    private void initRedisEndpoint(){
        String master = getConfigItemValue("fuel.redis.master", "mymaster");
        String sentinels = getConfigItemValue("fuel.redis.sentinels");
        String password = getConfigItemValue("fuel.redis.password");
        int dbNum = getConfigItemInteger("fuel.redis.dbNum", 9);
        int timeout = getConfigItemInteger("fuel.redis.timeout", 2000);
        redisPartition = getConfigItemInteger("fuel.redis.partitions", 20);

        redisEndpoint = new SentinelConnectionPool.SentinelRedisEndpoint(sentinels, master, password, dbNum, timeout);
    }

    /**
     * 订运单 连接相关
     */
    private void initOrderConfig(){
        this.orderConnectionID = getConfigItemValue("fuel.order.database.id", "fuel");
        DataBaseConnectionManager.DataBaseConnection dataBaseConnection = this.connectionManager.getConnection(orderConnectionID);
        if(dataBaseConnection == null){
            String baseUrl = getConfigItemValue("fuel.order.database.url");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(baseUrl), "fuel.order.database.url 不能为空。");

            String typeStr = getConfigItemValue("fuel.order.database.type", "mysql");
            DBType type = DBType.newDBType(typeStr);
            Preconditions.checkNotNull(type, String.format("数据库类型有fuel.order.database.type误：%s", typeStr));

            String user = getConfigItemValue("fuel.order.database.user");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(user), "fuel.order.database.user 不能为空。");

            String password = getConfigItemValue("fuel.order.database.password");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "fuel.order.database.password 不能为空。");

            String driver = getConfigItemValue("fuel.order.database.driver");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(driver), "fuel.order.database.driver 不能为空。");

            //增加油耗的数据库连接
           dataBaseConnection =
                    new DataBaseConnectionManager.DataBaseConnection(type, baseUrl, driver, user, password);

            Connection connection = dataBaseConnection.getConnection();
            Preconditions.checkNotNull(connection, String.format("获取数据库连接失败：\n\t %s", dataBaseConnection.toString()));
            this.connectionManager.addConnection(orderConnectionID, dataBaseConnection);
            this.connectionManager.closeConnection(orderConnectionID);
        }

        this.ordertable = getConfigItemValue("fuel.order.database.order.table");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ordertable), "fuel.order.database.order.table 不能为空。");

    }

    public double getAltMin() {
        return altMin;
    }

    public double getAltMax() {
        return altMax;
    }

    public double getLonMin() {
        return lonMin;
    }

    public double getLonMax() {
        return lonMax;
    }

    public double getLatMin() {
        return latMin;
    }

    public double getLatMax() {
        return latMax;
    }

    public long getMinTime() {
        return minTime;
    }

    public int getMaxDirection() {
        return maxDirection;
    }

    public String getUrl() {
        return url;
    }

    public String getFueltable() {
        return fueltable;
    }

    public String getTriptable() {
        return triptable;
    }

    public String getAbnormalfueltable() {
        return abnormalfueltable;
    }

    public String getAbnormaldrivingtable() {
        return abnormaldrivingtable;
    }

    public double getMaxConsumption() {
        return maxConsumption;
    }

    public String getFuelDriver() {
        return  this.connectionManager.getConnection(fuelConnectionID).getDriver();
    }

    public String getOrdertable() {
        return ordertable;
    }

    public String getTotalfueltable() {
        return totalfueltable;
    }

    public Connection getFuelConnection(){
        return getConnection(fuelConnectionID);
    }
    public void closeFuelConnection(){
        this.connectionManager.closeConnection(fuelConnectionID);
    }

    public Connection geOrderConnection(){
        return getConnection(orderConnectionID);
    }

    public String getAbnormaldrivingReportUrl() {
        return abnormaldrivingReportUrl;
    }

    public int getRedisPartition() {
        return redisPartition;
    }

    public Connection getConnection(String key){
        DataBaseConnectionManager.DataBaseConnection dataBaseConnection = this.connectionManager.getConnection(key);
        Connection connection = null;
        if(dataBaseConnection != null){
            connection = dataBaseConnection.getConnection();
        }
        Preconditions.checkNotNull(connection, String.format(String.format("获取数据库连接失败：\n\t %s", dataBaseConnection)));
        return connection;
    }

    public Jedis getRedisClient(){
        if(redisEndpoint == null){
            synchronized (FuelConfig.class){
                if(redisEndpoint == null){
                    initRedisEndpoint();
                }
            }
        }
        return redisEndpoint.connect();
    }

    public void closeRedisEndpoint(){
        try {
            if(redisEndpoint != null){
                redisEndpoint.close();
            }
        } catch (Exception e) { }
    }

    public long getTv() {
        return tv;
    }

    public long getTa() {
        return ta;
    }

    public long getTr() {
        return tr;
    }

    public int getDa() {
        return da;
    }


    public Map<String, Integer> getAlarmLabelMap() {
        return alarmLabelMap;
    }

    public int getAlarmLabel(String alarm){
        int label = -1;
        if(alarmLabelMap != null){
            Integer integer = alarmLabelMap.get(alarm);
            if(integer != null) label = integer;
        }
        return label;
    }

    public SSIGeoCodeService getSsiGeoCodeService() {
        return ssiGeoCodeService;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GreenplumConf{");
        sb.append(configMap).append('}');
        return sb.toString();
    }
}
