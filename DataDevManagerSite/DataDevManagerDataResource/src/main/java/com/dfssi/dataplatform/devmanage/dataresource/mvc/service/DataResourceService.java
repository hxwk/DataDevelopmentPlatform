package com.dfssi.dataplatform.devmanage.dataresource.mvc.service;

import com.alibaba.druid.util.StringUtils;
import com.dfssi.dataplatform.devmanage.dataresource.Config.HiveConfig;
import com.dfssi.dataplatform.devmanage.dataresource.cache.CacheEntity;
import com.dfssi.dataplatform.devmanage.dataresource.db.DBCommon;
import com.dfssi.dataplatform.devmanage.dataresource.db.DruidHiveDataSource;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.dao.DataResourceAccessDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.dao.DataResourceDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.dao.DataResourceTableColumnDao;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceAccessEntity;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceTableColumnEntity;
import com.dfssi.dataplatform.devmanage.dataresource.thread.CacheEventSubscriber;
import com.dfssi.dataplatform.devmanage.dataresource.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(value = "dataResourceService")
@Transactional(readOnly = false)
public class DataResourceService implements IDataResourceService {

    private static Logger logger = Logger.getLogger(DataResourceService.class);

    @Autowired
    private DataResourceDao dataResourceDao;

    @Autowired
    private DataResourceTableColumnDao dataResourceTableColumnDao;

    @Autowired
    private DataResourceAccessDao dataResourceAccessDao;

    @Autowired
    private HiveConfig hiveConfig;

    @Autowired
    private CacheEventSubscriber cacheEventSubscriber;

//    @Autowired
//    private DruidHiveDataSource druidHiveDataSource;

    @Override
    public DataResourceEntity getEntity(DataResourceEntity entity) {
        return null;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public DataResourceEntity getEntity(String dataresId) {
        return dataResourceDao.get(dataresId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DataResourceEntity> findEntityList(DataResourceEntity entity) {
        return dataResourceDao.findList(entity);
    }

    @Override
    public Map<String, Object> shareResource(DataResourceEntity entity) {
        Map<String, Object> result = new HashMap<>();

        int shareStatus = Constants.REQUEST_STATUS_FAIL;

        if (null == entity || StringUtils.isEmpty(entity.getDataresId())
                || StringUtils.isEmpty(entity.getDataresType()) || !Constants.RESOURCETYPE_HDFS.equalsIgnoreCase(entity.getDataresType())) {
            String message = "请求参数不合格";
            logger.error(message);
            result.put(Constants.KEY_RESULT_MESSAGE, message);
            result.put(Constants.KEY_RESULT_STATUS, shareStatus);

            return result;
        }

        //1 读取数据资源配置表，数据资源列配置表，数据资源访问配置表
        String dsId = entity.getDataresId();
        List<DataResourceTableColumnEntity> tableColumnList = dataResourceTableColumnDao.findListByDsId(dsId);
        List<DataResourceAccessEntity> accessEntityList = dataResourceAccessDao.findListByDsId(dsId);

        if (null == tableColumnList || tableColumnList.isEmpty() ||
                null == accessEntityList || accessEntityList.isEmpty()) {
            String message = "未有配置表字段或者数据资源访问信息";
            logger.error(message);
            result.put(Constants.KEY_RESULT_MESSAGE, message);
            result.put(Constants.KEY_RESULT_STATUS, shareStatus);

            return result;
        }

        Map<String, String> accessEntitys = new HashMap<>();
        for (DataResourceAccessEntity accessEntity : accessEntityList) {
            accessEntitys.put(accessEntity.getParameterName(), accessEntity.getParameterValue());
        }

        //2 根据配置将hdfs转换成hive表
        shareStatus = createHiveTableFromHDFS(tableColumnList, accessEntitys, dsId) ? Constants.REQUEST_STATUS_FAIL : Constants.REQUEST_STATUS_SUCCESS;

        String message = "请求成功";
        result.put(Constants.KEY_RESULT_MESSAGE, message);
        result.put(Constants.KEY_RESULT_STATUS, shareStatus);

        return result;
    }

    /**
     * 根据hdfs创建hive外部表
     * @param tableColumnList
     * @param accessEntitys
     * @param dsId
     * @return
     */
    private boolean createHiveTableFromHDFS(List<DataResourceTableColumnEntity> tableColumnList, Map<String, String> accessEntitys, String dsId) {
        boolean flag = false;

        //1 创建库
        Connection conn = null;
        try {
            conn = DruidHiveDataSource.getInstance(hiveConfig).getDataSource().getConnection();
            Statement statement = conn.createStatement();
            StringBuilder dbBuf = new StringBuilder();
            dbBuf.append("CREATE DATABASE IF NOT EXISTS ");
            dbBuf.append(Constants.HIVE_DBNAME);

            logger.info("建库sql: " + dbBuf.toString());
            statement.execute(dbBuf.toString());
            DBCommon.close(null, statement, null);
        } catch (Exception e) {
            logger.error("创建hive数据库失败", e);

            return flag;
        }

        //2 创建表
        try {
            conn = DruidHiveDataSource.getInstance(hiveConfig).getDataSource().getConnection();
            Statement statement = conn.createStatement();

            String path = accessEntitys.get(Constants.CONF_PATH);
            String delimiter = accessEntitys.get(Constants.CONF_DELIMITER);
//            String partitionKey = accessEntitys.get(Constants.HDFS_CONF_PARTITIONKEY);

            if (StringUtils.isEmpty(path) || StringUtils.isEmpty(delimiter)) {
                logger.info("path, delimiter, partitionKey未配置");

                return flag;
            }

            if (null == CacheEntity.fieldTypeCache || CacheEntity.fieldTypeCache.isEmpty()) {
                logger.info("缓存的字段数据为空");

                return flag;
            }

            String tableName = null;
            if (path.lastIndexOf("/") == path.length() - 1) {
                String tPath = path.substring(0, path.lastIndexOf("/"));
                tableName = tPath.substring(tPath.lastIndexOf("/") + 1);
            } else {
                tableName = path.substring(path.lastIndexOf("/") + 1);
            }

            StringBuilder dbBuf = new StringBuilder();
            dbBuf.append("create EXTERNAL table IF NOT EXISTS ");
            dbBuf.append(Constants.HIVE_DBNAME);
            dbBuf.append(".");
            dbBuf.append(tableName);
            dbBuf.append(" ( ");
            int index = 0;
            for (DataResourceTableColumnEntity tableColumnEntity : tableColumnList) {
                dbBuf.append(tableColumnEntity.getDataresTableColumnName());
                dbBuf.append(" ");
                dbBuf.append(CacheEntity.fieldTypeCache.get(tableColumnEntity.getDataresTableColumnType()));

                if (index < tableColumnList.size() - 1) {
                    dbBuf.append(", ");
                }

                index++;
            }
            dbBuf.append(") ROW FORMAT DELIMITED FIELDS TERMINATED BY '");
            dbBuf.append(delimiter);
            dbBuf.append("' LINES TERMINATED BY '\\n' STORED AS TEXTFILE LOCATION '");
            dbBuf.append(path);
            dbBuf.append("'");

            logger.info("建表sql：" + dbBuf.toString());
            statement.execute(dbBuf.toString());
            DBCommon.close(null, statement, null);

            updateDataResoueInfo(dsId, tableName);

            flag = true;
        } catch (Exception e) {
            logger.error("从hdfs转换hive表失败", e);
        } finally {
            try {
                DBCommon.close(conn, null, null);
            } catch (SQLException e) {
                logger.error(null, e);
            }
        }

        return flag;
    }

    /**
     * 创建表成功后更新数据资源配置
     * @param dsId hdfs资源id
     * @param tableName hive表名
     */
    private void updateDataResoueInfo(String dsId, String tableName) {
        //1 更新原资源表的审核状态 插入数据资源配置表跟数据资源访问表
        dataResourceDao.insertStatus(dsId);

        String curDsId = UUID.randomUUID().toString().replaceAll("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATEFORMAT_YYYYMMDDHHMMSS);
        DataResourceEntity resourceEntity = new DataResourceEntity();
        resourceEntity.setDataresType(Constants.RESOURCETYPE_HIVE);
        resourceEntity.setDataresName(" hdfs2hive : " + tableName);
        resourceEntity.setDataresDesc(" hdfs2hive : " + tableName);
        resourceEntity.setDataresId(curDsId);
        resourceEntity.setStatus(1);
        resourceEntity.setIsValid(1);
        resourceEntity.setCreateUser(Constants.USER_ADMIN);
        resourceEntity.setCreateDate(sdf.format(Calendar.getInstance().getTime()));
        resourceEntity.setUpdateUser(Constants.USER_ADMIN);
        resourceEntity.setUpdateDate(sdf.format(Calendar.getInstance().getTime()));

        dataResourceDao.insert(resourceEntity);

        List<DataResourceAccessEntity> resourceAccessEntityList = new ArrayList<>();
        for (String field : Constants.HIVE_CONF_FIELDS) {
            DataResourceAccessEntity resourceAccessEntity = new DataResourceAccessEntity();
            resourceAccessEntity.setDsId(curDsId);
            resourceAccessEntity.setDsAccessInfoId(UUID.randomUUID().toString().replaceAll("-", ""));
            resourceAccessEntity.setParameterName(field);
            if (Constants.CONF_DRIVER.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(hiveConfig.getDriverclassname());
            } else if (Constants.CONF_DBNAME.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(hiveConfig.getDbname());
            } else if (Constants.CONF_URL.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(hiveConfig.getUrl());
            } else if (Constants.CONF_USERNAME.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(hiveConfig.getUsername());
            } else if (Constants.CONF_PASSWORD.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(hiveConfig.getPassword());
            } else if (Constants.CONF_TABLENAME.equalsIgnoreCase(field)) {
                resourceAccessEntity.setParameterValue(tableName);
            }

            resourceAccessEntity.setIsValid(1);
            resourceAccessEntity.setCreateUser(Constants.USER_ADMIN);
            resourceAccessEntity.setCreateDate(sdf.format(Calendar.getInstance().getTime()));
            resourceAccessEntity.setUpdateUser(Constants.USER_ADMIN);
            resourceAccessEntity.setUpdateDate(sdf.format(Calendar.getInstance().getTime()));
            resourceAccessEntityList.add(resourceAccessEntity);
        }
        dataResourceAccessDao.insertMutil(resourceAccessEntityList);
    }


}
