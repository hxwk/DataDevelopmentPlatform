package com.dfssi.dataplatform.datasync.plugin.sink.hbase.common;


import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.utils.UUIDUtil;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.ParameterConstant;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * HBASE 工具类
 * @author  chenfu
 * @date 2018-04-03
 */
public class HbaseUtils {
	public final static Logger logger = LoggerFactory.getLogger(HbaseUtils.class);

	private static String SERIES = "f";
	private static Connection conn;
	private static String zookeeper = "192.168.102.200,192.168.102.201,192.168.102.202";
	private static Map taskconfig = null;
	private static Map<String,HTable> adminMap= new HashMap<String,HTable>();
	private static Map<String,List<Put>> dataList = new HashMap<String,List<Put>>();

	/**
	 * 初始化
	 *
	 */
	public static boolean init( ) {
		Configuration config = HBaseConfiguration.create();

		try {
			zookeeper  = (String)taskconfig.get(ParameterConstant.HBASE_ZOOKEEPER_QUORUM);
			config.set("hbase.zookeeper.quorum", zookeeper);

			SERIES = StringUtils.isBlank((String)taskconfig.get("series"))?SERIES:(String)
					taskconfig.get("series");
			conn = ConnectionFactory.createConnection(config);

			logger.info("Hbase 初始化连接成功");
			return true;
		} catch (IOException e) {
			logger.error("Hbase 初始化连接失败:{}",e.getMessage());
		}
		return false;
	}
	public static boolean init(String zookeeper) {
		HbaseUtils.taskconfig=taskconfig;
		Configuration config = HBaseConfiguration.create();
		try {
			config.set("hbase.zookeeper.quorum", zookeeper);
			conn = ConnectionFactory.createConnection(config);
			logger.info("Hbase 初始化连接成功");
			return true;
		} catch (IOException e) {
			logger.error("Hbase 初始化连接失败:{}",e.getMessage());
		}
		return false;
	}

	/**
	 * 创建表
	 * @param tableName
	 * @param seriesStr
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void createTable(String tableName, String seriesStr)
			throws IllegalArgumentException, IOException {
		Admin admin = null;
		TableName table = TableName.valueOf(tableName);
		try {
			if(conn==null){
				init();
			}

			admin = conn.getAdmin();

			if (!admin.tableExists(table)) {
				System.out.println(tableName + " table not Exists");
				HTableDescriptor descriptor = new HTableDescriptor(table);

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

	/**
	 * 创建表
	 * @param tableName
	 * @param seriesStr
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void createTable(String tableName, String seriesStr,String prefix_length,String memStore)
			throws IllegalArgumentException, IOException {
		Admin admin = null;
		TableName table = TableName.valueOf(tableName);
		try {
			if(conn==null){
				init();
			}
			admin = conn.getAdmin();

			if (!admin.tableExists(table)) {
				System.out.println(tableName + " table not Exists");
				HTableDescriptor descriptor = new HTableDescriptor(table);
				//为空的时候用hbase的默认配置
				if(StringUtils.isNotBlank(prefix_length)&&Integer.valueOf(prefix_length)!=null) {
					descriptor.setValue(HTableDescriptor.SPLIT_POLICY, KeyPrefixRegionSplitPolicy.class.getName());// 指定策略
					descriptor.setValue("prefix_split_key_policy.prefix_length", prefix_length);
				}
				/*if(StringUtils.isNotBlank(memStore)&&Integer.valueOf(memStore)!=null){
					//descriptor.setValue("MEMSTORE_FLUSHSIZE", "5242880"); // 5M
					//descriptor.setValue("MEMSTORE_FLUSHSIZE", "104857600"); //100M
					descriptor.setValue("MEMSTORE_FLUSHSIZE", String.valueOf(Integer.valueOf(memStore)*1024*1024)); //  默认64M
				}*/

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

	/**
	 * 创建表
	 * @param tableName
	 * @param seriesStr
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void updateTableDescriptor(String tableName, String seriesStr,String prefix_length,String memStore)
			throws IllegalArgumentException, IOException {
		Admin admin = null;
		TableName table = TableName.valueOf(tableName);
		try {

			admin = conn.getAdmin();
			if (!admin.tableExists(table)) {
				System.out.println(tableName + " table not Exists");
				HTableDescriptor descriptor = new HTableDescriptor(table);
				if(StringUtils.isNoneBlank(prefix_length)) {
					descriptor.setValue(HTableDescriptor.SPLIT_POLICY, KeyPrefixRegionSplitPolicy.class.getName());// 指定策略
					descriptor.setValue("prefix_split_key_policy.prefix_length", prefix_length);

				}
				if(StringUtils.isNoneBlank(memStore)){
					//descriptor.setValue("MEMSTORE_FLUSHSIZE", "5242880"); // 5M
					//descriptor.setValue("MEMSTORE_FLUSHSIZE", "104857600"); //100M
					descriptor.setValue("MEMSTORE_FLUSHSIZE", memStore); //  默认64M
				}
				admin.disableTable( table);
				admin.modifyTable(table, descriptor);
				admin.enableTable( table);
			}
		} finally {
			IOUtils.closeQuietly(admin);

		}
	}
	
	public static boolean isExists(String tableName) {
		Admin admin = null;
		TableName table = TableName.valueOf(tableName);
		try {
			if(conn==null){
				init();
			}
			admin = conn.getAdmin();
			return admin.tableExists(table);
		} catch (Exception e) {
			logger.error("表是否存在",e.getMessage());
		}finally {
			IOUtils.closeQuietly(admin);

		}
		return false;
	}
	public static boolean isExists(String nameSpace,String tableName) {
		String table =StringUtils.isBlank(nameSpace)?tableName:nameSpace+":"+tableName;
		return isExists(table);
	}
	/**
	 * 添加数据
	 * @param rowKey
	 * @param columns
	 * @throws IOException
	 */
	public static int add(String tableName, String rowKey, Map<String, Object> columns)
			throws IOException {
		Table table = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));

			Put put = new Put(Bytes.toBytes(rowKey));
			for (Entry<String, Object> entry : columns.entrySet()) {
				put.addColumn(SERIES.getBytes(), Bytes.toBytes(entry.getKey()),
						ByteUtils.toBytes(entry.getValue())

						);
			}
			table.put(put);
			return 1;
			
		}catch (Exception e){
			return 0;
		}finally {
			IOUtils.closeQuietly(table);

		}
	}
	public static Object[] batch(String tableName,List<Put> datalist){
		HTable table = null;
		try {
			table =(HTable) conn.getTable(TableName.valueOf(tableName));
			Object[] results = new Object[datalist.size()];
			//table.batch(datalist,results);
			table.setAutoFlush(false,true);
			table.setWriteBufferSize(50*1024*1024);
			table.put(datalist);
			table.flushCommits();
			table.close();
			return results;
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(table);

		}
		return null;
	}
	public static HTable getTable(String tableName){
		HTable table =  adminMap.get(tableName);
		if(table==null){
			try {
				table =(HTable) conn.getTable(TableName.valueOf(tableName));
				adminMap.put(tableName,table);
			}catch (Exception e){
				logger.error(e.getMessage());
			}
		}
		return table;
	}
    public static HTable getTableDefault(String tableName){
        HTable table =  adminMap.get(tableName);
        if(table==null){
            try {
            	if( conn.getAdmin().tableExists(TableName.valueOf(tableName))) {
					table = (HTable) conn.getTable(TableName.valueOf(tableName));
				}
               else{
                    createTable(tableName,SERIES,"5","");
					table =(HTable)conn.getTable(TableName.valueOf(tableName));
                }
                adminMap.put(tableName,table);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return table;
    }
    public static void closeTable(String tableName){
        Table table =  adminMap.get(tableName);
        if(table!=null){
            IOUtils.closeQuietly(table);
        }
    }

    public static void closeTable(){
	    for(Entry<String,HTable> entry:adminMap.entrySet()){
            if(entry.getValue()!=null){
                IOUtils.closeQuietly(entry.getValue());
            }
        }
    }

    /**
     * 添加数据
     * @param rowKey
     * @param columns
     * @throws IOException
     */
    public static void addEvent(String tableName, String rowKey,String series, Map<String, Object> columns)
            throws IOException,IllegalArgumentException {
    	if(StringUtils.isBlank(tableName)||StringUtils.isBlank(rowKey)||StringUtils.isBlank(series)||columns==null||columns.size()==0){
    		throw new IllegalArgumentException("参数错误");
		}
        Table table = getTableDefault(tableName);
         Put put = new Put(Bytes.toBytes(rowKey));
		for (Entry<String, Object> entry : columns.entrySet()) {
			put.addColumn(series.getBytes(), Bytes.toBytes(entry.getKey()),
					ByteUtils.toBytes(entry.getValue())

			);
		}
            table.put(put);
    }
	public static void addEvent(String tableName, byte[] rowKey,String series, Map<String, Object> columns)
			throws IOException,IllegalArgumentException {
		if(StringUtils.isBlank(tableName)||rowKey==null||rowKey.length==0||StringUtils.isBlank(series)||columns==null||columns.size()==0){
			throw new IllegalArgumentException("参数错误");
		}
		Table table = getTableDefault(tableName);
		Put put = new Put(rowKey);
		for (Entry<String, Object> entry : columns.entrySet()) {
			put.addColumn(series.getBytes(), Bytes.toBytes(entry.getKey()),
					ByteUtils.toBytes(entry.getValue())

			);
		}
		table.put(put);
	}
	public static void addEventBatch( String tableName, byte[] rowKey, String series, Map<String, Object> columns)
			throws IOException,IllegalArgumentException {
		if(StringUtils.isBlank(tableName)||rowKey==null||rowKey.length==0||StringUtils.isBlank(series)||columns==null||columns.size()==0){
			throw new IllegalArgumentException("参数错误");
		}
		List list = dataList.get(tableName);
		if(list==null){
			list = Lists.newArrayList();
			dataList.put(tableName,list);
		}
		Put put = new Put(rowKey);

		for (Entry<String, Object> entry : columns.entrySet()) {
			put.addColumn(series.getBytes(), Bytes.toBytes(entry.getKey()),
					ByteUtils.toBytes(entry.getValue())

			);
		}
		list.add(put);
	}
	public static void batchDataList(int flushsize)throws Exception{
    	for(Entry<String,List<Put>> entry:dataList.entrySet()){
			HTable table = getTableDefault(entry.getKey());
			List<Put> list = entry.getValue();
			//Object[] ob = new Object[list.size()];
			//table.batch(entry.getValue(),ob);
			//table.setAutoFlush(false,true);
			if(flushsize>2)
				table.setWriteBufferSize(1024*1024*flushsize);
			table.put(list);
			//table.flushCommits();
			//table.close();
		}
		dataList.clear();
	}

	/**
	 * 添加数据
	 * @param rowKey
	 * @param columns
	 * @throws IOException
	 */
	public static int add(String tableName, String rowKey,String series, Map<String, Object> columns)
			throws IOException {
		Table table = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			for (Entry<String, Object> entry : columns.entrySet()) {
				put.addColumn(series.getBytes(), Bytes.toBytes(entry.getKey()),
						ByteUtils.toBytes(entry.getValue())

				);
			}
			table.put(put);
			return 1;

		}catch (Exception e){
			return 0;
		}finally {
			IOUtils.closeQuietly(table);

		}

	}
	public static int add(String tableName, String rowKey,String series, List<Map<String, Object>> list)
			throws IOException {
		Table table = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			for(Map<String,Object> columns:list) {
				for (Entry<String, Object> entry : columns.entrySet()) {
					put.addColumn(series.getBytes(), Bytes.toBytes(entry.getKey()),
							ByteUtils.toBytes(entry.getValue())

					);
				}
				table.put(put);
			}
			return 1;

		}catch (Exception e){
			return 0;
		}finally {
			IOUtils.closeQuietly(table);

		}

	}

	/**
	 * 根据rowkey获取数据
	 * @param tableName
	 * @param rowKey
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static Map<String, String> getAllValue(String tableName,String rowKey)
			throws IllegalArgumentException, IOException {
		Table table = null;
		Map<String, String> resultMap = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addFamily(SERIES.getBytes());
			Result res = table.get(get);
			Map<byte[], byte[]> result = res.getFamilyMap(SERIES.getBytes());
			Iterator<Entry<byte[], byte[]>> it = result.entrySet().iterator();
			resultMap = new HashMap<String, String>(32);
			while (it.hasNext()) {
				Entry<byte[], byte[]> entry = it.next();
				resultMap.put(Bytes.toString(entry.getKey()),
						Bytes.toString(entry.getValue()));
			}
		} finally {
			IOUtils.closeQuietly(table);
		}
		return resultMap;
	}

	// 根据rowkey和column获取数据

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param column
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static String getValueBySeries(String tableName,String rowKey, String column)
			throws IllegalArgumentException, IOException {
		Table table = null;
		String resultStr = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(SERIES), Bytes.toBytes(column));
			Result res = table.get(get);
			byte[] result = res.getValue(Bytes.toBytes(SERIES),
					Bytes.toBytes(column));
			resultStr = Bytes.toString(result);
		} finally {
			IOUtils.closeQuietly(table);
		}

		return resultStr;

	}

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param column
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static byte[] getValueBytesBySeries(String tableName,String rowKey, String column)
			throws IllegalArgumentException, IOException {
		Table table = null;
		String resultStr = null;
		byte[] result=null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(SERIES), Bytes.toBytes(column));
			Result res = table.get(get);
			 result = res.getValue(Bytes.toBytes(SERIES),
					Bytes.toBytes(column));
			//resultStr = Bytes.toString(result);
		} finally {
			IOUtils.closeQuietly(table);
		}

		return result;

	}
	/**
	 * 根据table查询所有数据
	 * @param tableName
	 * @throws Exception
	 */
	public static void getValueByTable(String tableName) throws Exception {
		Map<String, String> resultMap = null;
		Table table = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			ResultScanner rs = table.getScanner(new Scan());
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println(
							"列f："
							+ new String(keyValue.getFamily())
							+ "====列q："
							+ new String(keyValue.getQualifier())
							+"====值："
							+
							keyValue.getValue());

				}

			}
		} finally {
			IOUtils.closeQuietly(table);
		}

	}

	public static void getValueByTable(String tableName,Class cls) throws Exception {
		Map<String, String> resultMap = null;
		Table table = null;
		try {
			table = conn.getTable(TableName.valueOf(tableName));
			ResultScanner rs = table.getScanner(new Scan());
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					byte[] bts = keyValue.getValue();
					System.out.println(
							"列f："
									+ new String(keyValue.getFamily())
									+ "====列q："
									+ new String(keyValue.getQualifier())
									+"====值："
					);

				}

			}
		} finally {
			IOUtils.closeQuietly(table);
		}
	}
	/**
	 *  删除表
	 * @param tableName
	 * @throws IOException
	 */
	public static void dropTable(String tableName) throws IOException {
		Admin admin = null;
		TableName table = TableName.valueOf(tableName);
		try {
			admin = conn.getAdmin();
			if (admin.tableExists(table)) {
				admin.disableTable(table);
				admin.deleteTable(table);
			}
		} finally {
			IOUtils.closeQuietly(admin);
		}
	}


	/**
	 * 删除行
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @param timeStampe
	 */
    public static int delete(String tableName,String rowKey,String family,String qualifier,String timeStampe)
    {
        Admin admin = null;
        try {
            admin = conn.getAdmin();
            if(!admin.tableExists(TableName.valueOf(tableName)))
            {
                System.err.println("the table "+tableName+" is not exist");
                System.exit(1);
            }
            //创建表连接
            Table table=conn.getTable(TableName.valueOf(tableName));
            //准备删除数据

			Delete delete=new Delete(Bytes.toBytes(rowKey));

            if(family != null && qualifier != null)
            {
				delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
            }
            else if(family !=null )
            {
				delete.addFamily(Bytes.toBytes(family));
            }
            //检查时间戳
            if(timeStampe!=null)
            {
                delete.setTimestamp(Long.parseLong(timeStampe));
            }
            //进行数据删除
            table.delete(delete);
            table.close();
            return 1;
        } catch (Exception e) {
        	e.printStackTrace();
        	return 0;
        }
    }


	public byte[] makeRowkey(String getTollgateId ,Long timeStamp){
       byte[] tollgateId = getTollgateId.getBytes();
        byte[] timestamp = Bytes.toBytes(timeStamp);
        byte[] newTollgateId = new byte[tollgateId.length + timestamp.length];
        System.arraycopy(tollgateId, 0, newTollgateId, 0, tollgateId.length);
        System.arraycopy(timestamp,  0, newTollgateId, tollgateId.length, timestamp.length);
        return newTollgateId ;
	}

	public static void  getDeviceIdAndTimeStampFromRowKey(byte[] byteArray){
    	int tollgateLength = byteArray.length-8;

    	int timeStampLength = 8;

		byte[] tollgateIdArry  = new byte[tollgateLength];

		byte[] timeStampArray  = new byte[timeStampLength];

		System.arraycopy(byteArray, 0, tollgateIdArry, 0, tollgateLength);

		System.arraycopy(byteArray,  tollgateLength, timeStampArray, 0, timeStampLength);

		System.out.println(new String(tollgateIdArry));

		System.out.println(ByteUtils.getInt(timeStampArray));

	}


	public Map<String,Object> getDeviceIdAndTimeStamp(byte[] bt){
		//String st Base64Util.byte2Base64String(bt);
		return null;
	}

	public static Map getTaskconfig() {
		return taskconfig;
	}

	public static void setTaskconfig(Map taskconfig) {
		HbaseUtils.taskconfig = taskconfig;
	}
	//注意配置开发机器host
	//devmaster
	public static void main(String[] args) {
		batchTest();
		//putTest();
	}

	public static void putTest(){
		HashMap taskconfig = new HashMap();
		taskconfig.put(ParameterConstant.HBASE_ZOOKEEPER_QUORUM,"172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181");
		setTaskconfig(taskconfig);
		init();
		try {


			String json = "{\"abnormalDrivingBehaviorAlarmDegree\":0,\"abnormalDrivingBehaviorAlarmType\":[],\"absStatus\":0,\"alarm\":0,\"alarmInforCode\":0,\"alarmLists\":[],\"alarms\":[],\"alt\":0,\"batteryVoltage\":0,\"brakeStatus\":0,\"braking\":0,\"canAndHydraulicTankStatus\":0,\"cumulativeOilConsumption\":0.0,\"dir\":0,\"engineSpeed\":0.0,\"extraInfoItems\":[{\"data\":\"AAAAAA==\",\"id\":20},{\"data\":\"AAAAAA==\",\"id\":43},{\"data\":\"AA==\",\"id\":48},{\"data\":\"AA==\",\"id\":49},{\"data\":\"\",\"id\":0},{\"data\":\"\",\"id\":0}],\"fromQly\":0,\"fuel\":0.0,\"gear\":0,\"gpsTime\":1540440741000,\"hydraulicTank\":0,\"ioState\":0,\"lat\":0,\"lon\":0,\"mile\":0,\"msgId\":\"0200\",\"receiveMsgTime\":1540440741215,\"signalState\":0,\"signalStates\":[],\"sim\":\"18888888888\",\"speed\":0,\"speed1\":0,\"state\":0,\"throttleOpen\":0,\"torquePercentage\":0,\"totalFuelConsumption\":0,\"vehicleStatus\":[],\"vehicleWeight\":62468.0,\"vid\":\"695e023e519c4d6ba77858e105c2f888\"}";
			Map map =JSON.parseObject(json);
			long start = System.currentTimeMillis();
			List list =Lists.newArrayList();
			for(int i = 0;i<30000;i++) {
				//add("dev:terminal_0200_1", UUIDUtil.getRandomUuidByTrim(), "f",map );
				list.add(map);
				if(list.size()==100){
					add("dev:terminal_0200_1", UUIDUtil.getRandomUuidByTrim(), "f",list );
					list = Lists.newArrayList();
				}
				System.out.println(i);
			}
			//add("dev:terminal_0200_1", UUIDUtil.getRandomUuidByTrim(), "f",list );
			long end = System.currentTimeMillis();
			System.out.println(end-start);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static void batchTest(){
		HashMap taskconfig = new HashMap();
		//taskconfig.put(ParameterConstant.HBASE_ZOOKEEPER_QUORUM,"172.16.1.210,172.16.1.211,172.16.1.212");
		taskconfig.put(ParameterConstant.HBASE_ZOOKEEPER_QUORUM,"172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181");
		setTaskconfig(taskconfig);
		long start = System.currentTimeMillis();
		init();
		try {
			String json = "{\"abnormalDrivingBehaviorAlarmDegree\":0,\"abnormalDrivingBehaviorAlarmType\":[],\"absStatus\":0,\"alarm\":0,\"alarmInforCode\":0,\"alarmLists\":[],\"alarms\":[],\"alt\":0,\"batteryVoltage\":0,\"brakeStatus\":0,\"braking\":0,\"canAndHydraulicTankStatus\":0,\"cumulativeOilConsumption\":0.0,\"dir\":0,\"engineSpeed\":0.0,\"extraInfoItems\":[{\"data\":\"AAAAAA==\",\"id\":20},{\"data\":\"AAAAAA==\",\"id\":43},{\"data\":\"AA==\",\"id\":48},{\"data\":\"AA==\",\"id\":49},{\"data\":\"\",\"id\":0},{\"data\":\"\",\"id\":0}],\"fromQly\":0,\"fuel\":0.0,\"gear\":0,\"gpsTime\":1540440741000,\"hydraulicTank\":0,\"ioState\":0,\"lat\":0,\"lon\":0,\"mile\":0,\"msgId\":\"0200\",\"receiveMsgTime\":1540440741215,\"signalState\":0,\"signalStates\":[],\"sim\":\"18888888888\",\"speed\":0,\"speed1\":0,\"state\":0,\"throttleOpen\":0,\"torquePercentage\":0,\"totalFuelConsumption\":0,\"vehicleStatus\":[],\"vehicleWeight\":62468.0,\"vid\":\"695e023e519c4d6ba77858e105c2f888\"}";
			Map<String,Object> map =JSON.parseObject(json);
			long start2 = System.currentTimeMillis();

				/*if(list.size()==3000){
					batch("dev:terminal_0200_1",list);
					list=new ArrayList();
				}*/

				Runnable t = new Runnable() {
					@Override
					public void run() {
							List list = new ArrayList();
							for(int j = 0;j<120000;j++) {
								Put put = new Put(Bytes.toBytes(UUIDUtil.getRandomUuidByTrim()));
								for (Entry<String, Object> entry : map.entrySet()) {
									put.addColumn("f".getBytes(), Bytes.toBytes(entry.getKey()),
											ByteUtils.toBytes(entry.getValue())
									);
								}
								list.add(put);
							}
							batch("dev:terminal_0200_1",list);

						long end = System.currentTimeMillis();
						System.out.println(end-start2);

					}

				};
				new Thread(t).start();
				new Thread(t).start();
				new Thread(t).start();




		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
