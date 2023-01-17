package com.dfssi.dataplatform.ide.service.bi;


import com.dfssi.dataplatform.ide.service.bi.converters.ConveterType;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseSource;
import com.dfssi.dataplatform.ide.service.bi.sources.database.greenplum.GreenplumDataSource;
import com.dfssi.dataplatform.ide.service.bi.sources.database.hive.HiveDataSource;
import com.dfssi.dataplatform.ide.service.bi.sources.database.mysql.MysqlDataSource;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *     图表生成流程为： sources => converters =>
 * @author LiXiaoCong
 * @version 2018/1/9 11:46
 */
public class BIContext {
    private final Logger logger = LogManager.getLogger(BIContext.class);

    private volatile static BIContext BIContext;
    private Map<String, ConnectEntity> connectionMap;

    private BIContext(){
        this.connectionMap = Maps.newConcurrentMap();
    }

    public static BIContext get(){
        if(BIContext == null){
            synchronized (BIContext.class){
                if(BIContext == null){
                    BIContext = new BIContext();
                }
            }
        }
        return BIContext;
    }

    public ConnectEntity createConnnection(String url, String driver, String user, String password){
        return createConnnection(new ConnectEntity(url, driver, user, password));
    }

    public synchronized ConnectEntity createConnnection(ConnectEntity entity){
        String id = entity.toString();
        ConnectEntity connectEntity = connectionMap.get(id);
        if(connectEntity == null){
            connectEntity = entity;
            connectionMap.put(id, connectEntity);
        }
        return connectEntity;
    }

    public DataBaseSource newDataBaseSource(String type,
                                            String server,
                                            String user,
                                            String password,
                                            String database,
                                            String tableName,
                                            String columns,
                                            String chart,
                                            String conditions){
        Map<String, String> config = Maps.newHashMap();
        if(server != null) config.put("database.server", server);
        if(user != null) config.put("database.user", user);
        if(password != null)config.put("database.password", password);
        if(database != null)config.put("database.database", database);
        if(tableName != null)config.put("database.tableName", tableName);
        if(columns != null)config.put("database.columns", columns);
        if(chart != null) config.put("chart.type", chart);
        if(conditions != null) config.put("database.conditions", conditions);

        switch (type.toLowerCase()){
            case "greenplum" :
                return new GreenplumDataSource(config);
            case "mysql" :
                return new MysqlDataSource(config);
            case "hive" :
                return new HiveDataSource(config);
            default:
                logger.error(String.format("暂不支持的数据源类别：%s", type));
        }

        return null;
    }

    public static void main(String[] args) throws SQLException {

        Map<String, String> config = Maps.newHashMap();
        config.put("database.server", "172.16.1.221:5432");
        config.put("database.user", "analysis");
        config.put("database.password", "112233");
        config.put("database.database", "analysis");
        config.put("database.tableName", "employee");
        config.put("database.columns", "age,salary");
        config.put("chart.type", ConveterType.BAR.name());
        //config.put("database.conditions", "");


        GreenplumDataSource greenplumDataSource = new GreenplumDataSource(config);
        List<String> strings = greenplumDataSource.listTables();

        System.out.println(strings);
        strings.forEach(tableName ->{

            try {
                System.out.println(greenplumDataSource.getTableColumnAndTypes(tableName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        System.out.println(greenplumDataSource.readData());
    }
}
