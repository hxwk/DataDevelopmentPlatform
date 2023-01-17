package com.dfssi.dataplatform.ide.dataresource.mvc.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.dataresource.mvc.dao.DataResourceConfDao;
import com.dfssi.dataplatform.ide.dataresource.mvc.dao.DataResourceDao;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.*;
import com.dfssi.dataplatform.ide.dataresource.mvc.restful.IResourceDicItemFeign;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.CheckConnectionService;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.DataResourceService;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.DataResourceSubService;
import com.dfssi.dataplatform.ide.dataresource.util.Constants;
import com.dfssi.dataplatform.ide.dataresource.util.ResultVo;
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

@Service
@Transactional(readOnly = true)
public class DataResourceServiceImpl implements DataResourceService {

    @Autowired
    private DataResourceDao dataResourceDao;

    @Autowired
    private DataResourceSubService dataResourceSubService;

    @Autowired
    protected DataResourceConfDao dataResourceConfDao;

    @Autowired
    private CheckConnectionService checkConnection;

    @Autowired
    private IResourceDicItemFeign IResourceDicItemFeign;

    //新增、修改
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String saveEntity(DataResourceEntity entity) {
        String result = "";
            if(StringUtils.isEmpty(entity.getDataresourceId())){
                entity.setDataresourceId(UUID.randomUUID().toString());
            }
            int countId=dataResourceDao.countById(entity.getDataresourceId());
            if(countId==0) {
                //新增
                int count=dataResourceDao.countByName(entity.getDataresourceName());
                if (count != 0) {
                    result = "数据名已存在";
                } else {
                    dataResourceDao.insert(entity);//向dv_dataresource表中插数据
                    dataResourceSubService.insertSub(entity);//向dv_dataresource_Sub表中插数据
                }
            }else {
                //修改
                int countUpdate=dataResourceDao.countByUpdate(entity);
                if (countUpdate != 0) {
                    result = "数据资源名称已存在";
                } else {
                    dataResourceDao.insert(entity);//向dv_dataresource表中插数据
                    dataResourceSubService.insertSub(entity);//向dv_dataresource_Sub表中插数据
                }
            }
        return result;
    }

    //查询大数据组件实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataResourceEntity> findBigdataEntityList(DataResourceEntity entity, PageParam pageParam) {
        Page<DataResourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataResourceEntity> list = dataResourceDao.findBigdataEntityList(entity);
        PageResult<DataResourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //查询关系型数据库实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataResourceEntity> findDBEntityList(DataResourceEntity entity, PageParam pageParam) {
        Page<DataResourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataResourceEntity> list = dataResourceDao.findDBEntityList(entity);
        PageResult<DataResourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //查询私有资源实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataResourceEntity> findPrivateResourcesEntityList(DataResourceEntity entity, PageParam pageParam) {
        Page<DataResourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataResourceEntity> list = dataResourceDao.findPrivateResourcesEntityList(entity);
        PageResult<DataResourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //查询共享资源实体列表
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<DataResourceEntity> findSharedResourcesEntityList(DataResourceEntity entity, PageParam pageParam) {
        Page<DataResourceEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<DataResourceEntity> list = dataResourceDao.findSharedResourcesEntityList(entity);
        PageResult<DataResourceEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    //预览数据资源实体详细信息
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DataResourceSubEntity> findDataresourceInfo(String DataresourceId) {
        return dataResourceSubService.findDataresourceSubInfo(DataresourceId);
    }

    //删除
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deleteDataResourceEntity(String dataresourceId) {
        String result = "";
        try {
            dataResourceDao.deleteById(dataresourceId);
            dataResourceSubService.deleteSubInfo(dataresourceId);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    //查询数据源数量
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Map countRtotal() {
        return dataResourceDao.countRtotal();
    }

    //私有化属性变公有化
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String changePrivateStatus(String dataResourceId) {
        String result="";
        try {
            dataResourceDao.changePrivateStatus(dataResourceId);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    //公有化属性变私有化
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String changeSharedStatus(String dataResourceId) {
        String result="";
        try {
            dataResourceDao.changeSharedStatus(dataResourceId);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    /**
     * 查询数据资源
     * @return List<DataResourceConfEntity>
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DataResourceConfEntity> getAllDataResources(){
        List<DicItemEntity> list= IResourceDicItemFeign.listGetValidResource();
        List<String> dataresourceTypes=new ArrayList<>();
        if(list!=null){
            for(DicItemEntity dicItemEntity : list){
                if(dicItemEntity!=null){
                    dataresourceTypes.add(dicItemEntity.getValue());
                }
            }
        }
        if(dataresourceTypes!=null){
        return    dataResourceConfDao.getAllDataResources(dataresourceTypes);
        }else{
            return null;
        }
    }

    /**
     * 连接测试
     * @param entity
     * @return
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String connectionTestNew(DataResourceEntity entity){
        String result = "";
        if(null == entity){
            return "缺少必要参数";
        }
        ResultVo rs = new ResultVo();
        DataConnectionTestEntity info = classTransform(entity);
        String dbType = entity.getDataresourceType();
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
    public DataConnectionTestEntity classTransform(DataResourceEntity entity){
        DataConnectionTestEntity info = new DataConnectionTestEntity();
        String dbType = entity.getDataresourceType();
        //jbdc连接测试
        if ((Constants.STR_MYSQL).equals(dbType) || (Constants.STR_ORACLE).equals(dbType)
                || (Constants.STR_SQLSERVER).equals(dbType) || (Constants.STR_DBTWO).equals(dbType)
                || (Constants.STR_HIVE).equals(dbType) || (Constants.STR_GP).equals(dbType)) {
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
                if ((DataBaseEntity.KAFKAGROUPID).equals(paramName)) {
                    info.setKafkaGroupId(paramValue);
                }
            }
        }
        //HBase测试连接
        if((Constants.STR_HBASE).equals(dbType)) {
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
            for (DataResourceSubEntity access : entity.getDataResourceSubEntity()) {
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
        return info;
    }
}
