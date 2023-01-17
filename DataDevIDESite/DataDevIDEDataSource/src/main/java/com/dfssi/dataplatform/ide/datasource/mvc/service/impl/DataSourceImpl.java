package com.dfssi.dataplatform.ide.datasource.mvc.service.impl;


import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.datasource.mvc.dao.DataSourceConfDao;
import com.dfssi.dataplatform.ide.datasource.mvc.dao.DataSourceDao;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.*;
import com.dfssi.dataplatform.ide.datasource.mvc.restful.ISourceDicItemFeign;
import com.dfssi.dataplatform.ide.datasource.mvc.service.CheckConnectionService;
import com.dfssi.dataplatform.ide.datasource.mvc.service.DataSourceService;
import com.dfssi.dataplatform.ide.datasource.mvc.service.DataSourceSubService;
import com.dfssi.dataplatform.ide.datasource.util.Constants;
import com.dfssi.dataplatform.ide.datasource.util.ResultVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service(value = "dataSourceService")
public class DataSourceImpl implements DataSourceService {

    @Autowired
    private DataSourceDao dataSourceDao;

    @Autowired
    private DataSourceSubService dataSourceSubService;

    @Autowired
    protected DataSourceConfDao dataSourceConfDao;

    @Autowired
    private CheckConnectionService checkConnection;

    @Autowired
    private ISourceDicItemFeign sourceDicItemService;

    //保存新增修改数据源实体接口
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String saveEntity(DataSourceEntity entity) {
        String result = "";
            if(StringUtils.isEmpty(entity.getDatasourceId())){
                entity.setDatasourceId(UUID.randomUUID().toString());
            }
            int countId=dataSourceDao.countById(entity.getDatasourceId());
            if(countId==0) {
                //新增
                int count=dataSourceDao.countByName(entity.getDatasourceName());
                if (count != 0) {
                    result = "数据名已存在";
                } else {
                    dataSourceDao.insert(entity);//向dv_datasource表中插数据
                    dataSourceSubService.insertSub(entity);//向dv_datasource_Sub表中插数据
                }
            }else{
                //修改
                int countUpdate=dataSourceDao.countByUpdate(entity);
                if (countUpdate != 0) {
                    result = "数据源名称已存在";
                } else {
                    dataSourceDao.insert(entity);//向dv_datasource表中插数据
                    dataSourceSubService.insertSub(entity);//向dv_datasource_Sub表中插数据
                }
                }
        return result;
    }

    //查询关系型数据源实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataSourceEntity> findDBEntityList(DataSourceEntity entity, PageParam pageParam) {
        Page<DataSourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataSourceEntity> list = dataSourceDao.findDBList(entity);
        PageResult<DataSourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //查询大数据库数据源实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataSourceEntity> findBigdataEntityList(DataSourceEntity entity, PageParam pageParam) {
        Page<DataSourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataSourceEntity> list = dataSourceDao.findBigdataList(entity);
        PageResult<DataSourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //查询协议接口数据源实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataSourceEntity> findInterfaceEntityList(DataSourceEntity entity, PageParam pageParam) {
        Page<DataSourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataSourceEntity> list = dataSourceDao.findInterfaceList(entity);
        PageResult<DataSourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //预览查询数据详细信息
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DataSourceSubEntity> findDatasourceInfo(String datasourceId) {
        return dataSourceSubService.findDatasourceInfo(datasourceId);
    }

    //统计大数据数据源数量
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Map countTotal() {
        return dataSourceDao.countTotal();
    }

    //删除数据源实体列表
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deleteDataSourceEntity(String datasourceId) {
        String result = "";
        try {
            dataSourceDao.delete(datasourceId);
            dataSourceSubService.deleteSubInfo(datasourceId);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    //查询数据源
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DataSourceConfEntity> getAllDataSources(){
        List<DicItemEntity> list= sourceDicItemService.listGetValidSource();
        List<String> datasourceTypes=new ArrayList<>();
        if(list!=null){
            for(DicItemEntity dicItemEntity : list){
                if(dicItemEntity!=null){
                    datasourceTypes.add(dicItemEntity.getValue());
                }
            }
        }
        if(datasourceTypes!=null){
            return    dataSourceConfDao.getAllDataSources(datasourceTypes);
        }else{
            return null;
        }
    }

    //新增数据连接测试
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String connectionTestNew(DataSourceEntity entity) {
        String result = "";
        if(null == entity){
            return "缺少必要参数";
        }
        ResultVo rs = new ResultVo();
        DataConnectionTestEntity info = classTransform(entity);
        String dbType = entity.getDatasourceType();
        //jdbc连接测试
        if ((Constants.STR_MYSQL).equals(dbType) || (Constants.STR_ORACLE).equals(dbType)
                || (Constants.STR_SQLSERVER).equals(dbType) || (Constants.STR_DBTWO).equals(dbType)
                || (Constants.STR_HIVE).equals(dbType) || (Constants.STR_GP).equals(dbType)) {
            String sql = "select count(*) from "+ info.getTableName();
            try {
                rs = checkConnection.exceQuery(info, sql, dbType);
            } catch (Exception e) {
                e.printStackTrace();
                rs.setErrorNo(-1);
                rs.setErrorMsg("无法连接");
            }
        }
        //hdfs连接测试
        if ((Constants.STR_HDFS).equals(dbType)) {
            boolean bol = false;
            try {
                String hdfsJmxDefalutPort = "50070";
                bol = checkConnection.checkHdfsIsActive(info.getIp(), hdfsJmxDefalutPort);
            } catch (Exception e) {
                e.printStackTrace();
                rs.setErrorNo(-1);
                rs.setErrorMsg("测试连接失败");
            }
            if (bol == false) {
                result="hdfs的NameNode节点状态死掉，请重新输入活跃的NameNode节点IP!";
            } else {
                try {
                    String hdfsJmxDefalutUser = "hdfs";
                    String hdfsUri = "hdfs://" + info.getIp() + ":" + info.getPort();
                    rs = checkConnection.checkHdfsFilePathIsExist(hdfsUri, hdfsJmxDefalutUser, info.getHdfsPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    rs.setErrorNo(-1);
                    rs.setErrorMsg("测试连接失败");
                }
            }
        }
        //kafka连接测试
        if ((Constants.STR_KAFKA).equals(dbType)) {
            //rs = checkConnection.queryTopic(info.getZkAddress(), info.getKafkaTopic());
            rs = checkConnection.getTopics(info);
        }
        //HBase连接测试
        if((Constants.STR_HBASE).equals(dbType)){
            rs = checkConnection.checkHbase(info);
        }
        //mongodb连接测试
        if((Constants.STR_MONGODB).equals(dbType)){
            rs = checkConnection.checkMongodb(info);
        }
        //es连接测试
        if((Constants.STR_ES).equals(dbType)){
            rs = checkConnection.checkElasticSearch(info);
        }
        //geode连接测试
        if((Constants.STR_GEODE).equals(dbType)){
            rs = checkConnection.checkGeode(info);
        }
        //udp测试连接
        if((Constants.STR_UDP).equals(dbType)){
            rs = checkConnection.checkUDP(info);
        }
        //tcp连接测试
        if ((Constants.STR_TCP).equals(dbType)) {
            rs = checkConnection.checkTCP(info);
        }
        if((Constants.STR_HTTP).equals(dbType)){
            rs = checkConnection.checkHttp(info);
        }
        if (-1 == rs.getErrorNo()) {
            return rs.getErrorMsg();
        } else {
            return result;
        }
    }

    /**
     * 测试连接类转换
     * @param entity
     * @return
     */
    public DataConnectionTestEntity classTransform(DataSourceEntity entity){
        DataConnectionTestEntity info = new DataConnectionTestEntity();
        String dbType = entity.getDatasourceType();
        //jbdc连接测试
        if ((Constants.STR_MYSQL).equals(dbType) || (Constants.STR_ORACLE).equals(dbType)
                || (Constants.STR_SQLSERVER).equals(dbType) || (Constants.STR_DBTWO).equals(dbType)
                || (Constants.STR_HIVE).equals(dbType) || (Constants.STR_GP).equals(dbType)) {
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if ((DataBaseEntity.DATABASENAME).equals(paramName)) {
                    info.setDatabaseName(paramValue);
                }
                if ((DataBaseEntity.DATABASEUSERNAME).equals(paramName)) {
                    info.setDatabaseUsername(paramValue);
                }
                if ((DataBaseEntity.DATABASEPASSWORD).equals(paramName)) {
                    info.setDatabasePassword(paramValue);
                }
                if ((DataBaseEntity.TABLENAME).equals(paramName)) {
                    info.setTableName(paramValue);
                }
            }
        }
        //hdfs连接测试
        if ((Constants.STR_HDFS).equals(dbType)) {
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.HDFSIP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.HDFSPORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if ((DataBaseEntity.PATH).equals(paramName)) {
                    info.setHdfsPath(paramValue);
                }
            }
        }
        //kafka连接测试
        if ((Constants.STR_KAFKA).equals(dbType)) {
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.ZKADDRESS).equals(paramName)) {
                    info.setZkAddress(paramValue);
                }
                if((DataBaseEntity.KAFKATADDRESS).equals(paramName)){
                    info.setKafkaAddress(paramValue);
                }
                if ((DataBaseEntity.KAFKATOPIC).equals(paramName)) {
                    info.setKafkaTopic(paramValue);
                }
//                if ((DataBaseEntity.KAFKAREQUESTTIMEOUT).equals(paramName)) {
//                    info.setKafkaRequestTimeOut(paramValue);
//                }
//                if ((DataBaseEntity.KAFKASESSIONTIMEOUT).equals(paramName)) {
//                    info.setKafkaSessionTimeOut(paramValue);
//                }
                if ((DataBaseEntity.KAFKAGROUPID).equals(paramName)) {
                    info.setKafkaGroupId(paramValue);
                }
            }
        }
        //HBase测试连接
        if((Constants.STR_HBASE).equals(dbType)) {
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if(DataBaseEntity.TABLENAME.equals(paramName)){
                    info.setTableName(paramValue);
                }
            }
        }
        //mongodb连接测试
        if((Constants.STR_MONGODB).equals(dbType)){
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if(DataBaseEntity.DATABASENAME.equals(paramName)){
                    info.setDatabaseName(paramValue);
                }
            }
        }
        //es连接测试
        if((Constants.STR_ES).equals(dbType)){
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if((DataBaseEntity.ESCLUSTERNAME).equals(paramName)){
                    info.setEsClusterName(paramValue);
                }
            }
        }
        //geode连接测试
        if((Constants.STR_GEODE).equals(dbType)){
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if((DataBaseEntity.TABLENAME).equals(paramName)){
                    info.setTableName(paramValue);
                }
            }
        }
        //tcp连接测试
        if ((Constants.STR_TCP).equals(dbType)) {
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.MASTERIP).equals(paramName)) {
                    info.setMasterIP(paramValue);
                }
                if ((DataBaseEntity.MASTERPORT).equals(paramName)) {
                    info.setMasterPort(paramValue);
                }
                if ((DataBaseEntity.CLIENTIP).equals(paramName)) {
                    info.setClientIP(paramValue);
                }
                if ((DataBaseEntity.CLIENTPORT).equals(paramName)) {
                    info.setClientPort(paramValue);
                }
            }
        }
        //UDP测试连接
        if((Constants.STR_UDP).equals(dbType)){
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
            }
        }
        //http测试连接
        if((Constants.STR_HTTP).equals(dbType)){
            for (DataSourceSubEntity access : entity.getDataSourceSubEntity()) {
                String paramName = access.getParameterName();
                String paramValue = access.getParameterValue();
                if ((DataBaseEntity.STR_HTTP_IP).equals(paramName)) {
                    info.setIp(paramValue);
                }
                if ((DataBaseEntity.STR_HTTP_PORT).equals(paramName)) {
                    info.setPort(paramValue);
                }
                if ((DataBaseEntity.STR_HTTP_PATH).equals(paramName)) {
                    info.setPath(paramValue);
                }
                if ((DataBaseEntity.STR_HTTP_REQUESTMODEL).equals(paramName)) {
                    info.setRequestModel(paramValue);//请求类型，0：get，，1：post
                }
                if ((DataBaseEntity.STR_HTTP_PARAMS).equals(paramName)) {
                    info.setRequestParams(paramValue);//请求参数
                }
            }
        }
        return info;
    }
}
