package com.dfssi.dataplatform.common.service;

import com.dfssi.dataplatform.analysis.dao.*;
import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.analysis.model.step.Step;
import com.dfssi.dataplatform.metadata.dao.DataResourceConfDao;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ModelService extends AbstractService implements IModelService{

    @Autowired
    protected ModelDao modelDao;
    @Autowired
    protected LinkDao linkDao;
    @Autowired
    protected StepDao stepDao;
    @Autowired
    protected AttrDao attrDao;
    @Autowired
    protected StepTypeDao stepTypeDao;
    @Autowired
    protected SourceDao sourceDao;
    @Autowired
    protected SourceConfDao sourceConfDao;
    @Autowired
    protected ResourceDao resourceDao;
    @Autowired
    protected ResourceConfDao resourceConfDao;
    @Autowired
    protected JarDao jarDao;
    @Autowired
    protected DataResourceConfDao metaDataSourceConfDao;
    @Autowired
    protected TaskMonitorDao taskMonitorDao;
    @Autowired
    protected CoordTaskMonitorDao coordTaskMonitorDao;
    @Autowired
    protected YarnMonitorDao yarnMonitorDao;
    @Autowired
    protected EmailAlertConfigDao emailAlertConfigDao;
    @Autowired
    protected EmailAlertRecordDao emailAlertRecordDao;
    @Autowired
    protected EmailAlertRuleDao emailAlertRuleDao;
    @Autowired
    protected EmailAlertUserDao emailAlertUserDao;
    @Autowired
    protected DataSourceMetaDao dataSourceMetaDao;
    @Autowired
    protected OfflineAnalysisRecDao offlineAnalysisRecDao;

    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, String modelType, String
            modelName, Long startTime, Long endTime, String status, String field, String orderType) {
        Map<String, Object> params = new HashMap();
        params.put("group", modelType);
        params.put("modelName", modelName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("status", status);
        params.put("field", field);
        params.put("orderType", orderType);

        return this.listAllModels(pageIdx, pageSize, params);
    }

    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, Map params) {
        PageHelper.startPage(pageIdx, pageSize);
        List<AnalysisModelEntity> modelEntities = this.modelDao.listAllModels(params);

        return modelEntities;
    }

    public Model getModel(String modelId) {
        AnalysisModelEntity modelEntity = modelDao.get(modelId);
        if (modelEntity == null) return null;

        List<AnalysisLinkEntity> linkEntities = linkDao.getLinksByModelId(modelId);
        List<AnalysisStepEntity> stepEntities = stepDao.getStepsByModelId(modelId);

        return Model.buildFromModelEntity(modelEntity, linkEntities, stepEntities);
    }

    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public Model saveModel(Model model) {
        this.deleteModel(model.getId());
        this.saveModelHeader(model);

        return model;
    }

    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteModel(String modelId) {
        attrDao.deleteByModelId(modelId);
        stepDao.deleteByModelId(modelId);
        linkDao.deleteByModelId(modelId);
        modelDao.delete(modelId);
    }

    private void saveModelHeader(Model model) {
        AnalysisModelEntity modelEntity = model.toModelEntity();
        List<AnalysisStepEntity> stepEntities = model.toStepEntities();
        List<AnalysisLinkEntity> linkEntities = model.toLinkEntities();
        List<AnalysisStepAttrEntity> attrEntities = model.toAttrEntities();

        modelDao.insert(modelEntity);
        if (stepEntities.size() > 0) {
            stepDao.batchInsert(stepEntities);
        }
        if (linkEntities.size() > 0) {
            linkDao.batchInsert(linkEntities);
        }
        if (attrEntities.size() > 0) {
            attrDao.batchInsert(attrEntities);
        }
    }

    //监控信息删除
    public void delectMonitorInfo(String modelId) {
        yarnMonitorDao.deleteByModelId(modelId);
        if (isWorkflowTask(modelId)) {
            taskMonitorDao.deleteByModelId(modelId);
        } else {
            coordTaskMonitorDao.deleteByModelId(modelId);
        }
    }

    //删除任务邮件告警信息
    public void delectEmailAlertInfo(String modelId) {
        emailAlertConfigDao.deleteByModelId(modelId);
        emailAlertRecordDao.deleteByModelId(modelId);
    }

    //输出表注册
    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public Model saveResource(Model model) {
        List<Step> steps = model.getSteps();
        for (Step step: steps) {
            if (step.getBuildType().equalsIgnoreCase("Output")) {
                String type = getResourceByStepId(step.toStepEntities().getStepType().getId());
                AnalysisResourceEntity analysisResourceEntity = new AnalysisResourceEntity(
                        model.getId(),
                        step.getId(),
                        model.getName(),
                        model.getDescription(),
                        type
                );
                resourceDao.insert(analysisResourceEntity);
                for (AnalysisStepAttrEntity attrEntity : step.toAttrEntities()) {
                    AnalysisResourceConfEntity analysisResourceConfEntity = new AnalysisResourceConfEntity(
                            analysisResourceEntity.getResourceId(),
                            attrEntity.getCode(),
                            attrEntity.getValueStr()
                    );
                    resourceConfDao.insert(analysisResourceConfEntity);
                }
                AnalysisResourceConfEntity analysisResourceConfEntity = new AnalysisResourceConfEntity(
                        analysisResourceEntity.getResourceId(),
                        "dataresourceType",
                        type
                );
                resourceConfDao.insert(analysisResourceConfEntity);
            }
        }
        return model;
    }

    //上传jar包信息保存
    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public void saveJar(String jarPath, String description, long jarSize) {
        String jarName = jarPath.substring(jarPath.lastIndexOf('/') + 1);
        AnalysisJarEntity jarEntity= new AnalysisJarEntity(jarName, jarPath, description, jarSize);
        delectJar(jarPath);
        jarDao.insert(jarEntity);
    }

    //jar包信息删除
    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public void delectJar(String jarPath) {
        jarDao.delectJarByPath(jarPath);
    }

    //获取已上传文件信息
    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public List<AnalysisJarEntity> getFileInfo() {
        List<AnalysisJarEntity> jarEntities = jarDao.listAllJars(new HashMap<String, Object>());

        return jarEntities;
    }

    //获取oozie任务运行详情
    @Transactional(value = "analysis", readOnly = false, propagation = Propagation.REQUIRED)
    public Object getOozieTaskInfo(String modelId) {
        if (isWorkflowTask(modelId)) {
            List<TaskMonitorEntity> taskMonitorEntitys = taskMonitorDao.getByModelId(modelId);
            return taskMonitorEntitys;
        } else {
            List<CoordTaskMonitorEntity> coordTaskMonitorEntitys = coordTaskMonitorDao.getByModelId(modelId);
            return coordTaskMonitorEntitys;
        }
    }

    //获取yarn任务运行详情
    public Object getYarnTaskInfo(String oozieId) {
        return  yarnMonitorDao.getByOozieId(oozieId);
    }

    //保存任务邮件告警配置信息
    public void saveEmailAlertConfig(String modelId, String userIds, String ruleIds) {
        Date date = new Date();
        String s = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        EmailAlertConfigEntity eace = new EmailAlertConfigEntity(modelId, userIds, ruleIds, 1, s, s, "", "");
        emailAlertConfigDao.insert(eace);
    }

    //查看任务邮件告警记录
    public Object getEmailAlertRecord(String modelId) {
        return emailAlertRecordDao.getRecordByModelId(modelId);
    }

    public List<AnalysisSourceConfEntity>  getSourceConf(String sourceId){
       return sourceConfDao.getSourceConf(sourceId);
    }

    @Override
    public List<AnalysisSourceEntity> listAllSources(Map params) {
        return sourceDao.listAllSources(params);
    }

    @Override
    public void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs) {
        attrDao.batchDelete(deletedAttrs);
    }

    @Override
    public void updateStatusByModelId(Map params) {
        yarnMonitorDao.updateStatusByModelId(params);
    }

    @Override
    public void updateWhenStart(Map params) {
        yarnMonitorDao.updateWhenStart(params);
    }

    @Override
    public List<DataSourceMeta> findAll() {
        return dataSourceMetaDao.findAll();
    }

    @Override
    public List<EmailAlertConfigEntity> getConfigByModelId(String modelId) {
        return emailAlertConfigDao.getConfigByModelId(modelId);
    }

    @Override
    public List<String> getUserIdsByModelId(String modelId) {
        return emailAlertConfigDao.getUserIdsByModelId(modelId);
    }

    @Override
    public List<String> getRuleIdsByModelId(String modelId) {
        return emailAlertConfigDao.getRuleIdsByModelId(modelId);
    }

    @Override
    public EmailAlertRuleEntity getRuleByRuleId(String ruleId) {
        return emailAlertRuleDao.getRuleByRuleId(ruleId);
    }

    @Override
    public EmailAlertUserEntity getUserByUserId(String userId) {
        return emailAlertUserDao.getUserByUserId(userId);
    }

    @Override
    public List<String> getOozieId(String modeId) {
        return yarnMonitorDao.getOozieIdByModelId(modeId);
    }

    @Override
    public void updateStatus(String modelId) {
        offlineAnalysisRecDao.updateStatus(modelId);
    }

    @Override
    public String getLastJobId(String modelId) {
        return offlineAnalysisRecDao.getLastJobId(modelId);
    }

    @Override
    public List<AnalysisResourceConfEntity> getResourceConf(String resourceId) {
        return resourceConfDao.getResourceConf(resourceId);
    }

    @Override
    public List<AnalysisResourceEntity> listResourceAllTable(Map params) {
        return resourceDao.listResourceAllTable(params);
    }

    @Override
    public List<AnalysisResourceEntity> listResource(Map params) {
        return resourceDao.listResource(params);
    }

    @Override
    public List<AnalysisResourceEntity> listAllSources() {
        return resourceDao.listAllSources();
    }

    @Override
    public void deleteByResourceId(String resourceId) {
        resourceDao.deleteByResourceId(resourceId);
    }

    @Override
    public AnalysisSourceEntity getByDataresourceId(String dataresourceId) {
        return sourceDao.getByDataresourceId(dataresourceId);
    }

    @Override
    public List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup) {
        return stepTypeDao.getAllStepTypes(stepGroup);
    }


    public List<AnalysisStepEntity> getStepsByModelId(String modelId){
        return stepDao.getStepsByModelId(modelId);
    }

    @Override
    public List<TaskMonitorEntity> getByStatus(String status) {
        return taskMonitorDao.getByStatus(status);
    }

    @Override
    public YarnMonitorEntity getById(String id) {
        return yarnMonitorDao.getById(id);
    }

    @Override
    public List<YarnMonitorEntity> getByModelId(String modelId) {
        return yarnMonitorDao.getByModelId(modelId);
    }

    @Override
    public List<String> getOozieIdByModelId(String modelId) {
        return yarnMonitorDao.getOozieIdByModelId(modelId);
    }

    @Override
    public void updateStatusByOozieTaskId(Map params) {
        yarnMonitorDao.updateStatusByOozieTaskId(params);
    }

    @Override
    public AnalysisModelEntity getModelByModelId(String modelId) {
        return modelDao.get(modelId);
    }

    @Override
    public List<DataResourceConfEntity> getAllDataResource() {
        return metaDataSourceConfDao.getAllDataResource();
    }

    @Override
    public List<AnalysisResourceEntity> listAllReSources() {
        return resourceDao.findAllList();
    }

    @Override
    public TaskMonitorEntity getTaskMonitorById(String id) {
        return taskMonitorDao.getById(id);
    }

    @Override
    public List<TaskMonitorEntity> listRunningTasksByModelId(String modelId) {
        return taskMonitorDao.listRunningTasksByModelId(modelId);
    }

    @Override
    public List<String> getApplicationIdByOozieId(String oozieTaskId) {
        return yarnMonitorDao.getApplicationIdByOozieId(oozieTaskId);
    }

    @Override
    public void updateStatusByApplicationId(Map params) {
        yarnMonitorDao.updateStatusByApplicationId(params);
    }

    @Override
    public List<String> listRunningApplicationIdsByOozieId(String oozieTaskId) {
        return yarnMonitorDao.listRunningApplicationIdsByOozieId(oozieTaskId);
    }

    @Override
    public List<CoordTaskMonitorEntity> listRunningCoordTasksByModelId(String modelId) {
        return coordTaskMonitorDao.listRunningCoordTasksByModelId(modelId);
    }

    @Override
    public void updateModelStatus(Map params) {
        modelDao.updateStatus(params);
    }

    @Override
    public List<AnalysisModelEntity> listRunningModels() {
        return modelDao.listRunningModels();
    }

    public void insertTaskMonitorEntity(TaskMonitorEntity taskMonitorEntity){
        taskMonitorDao.insert(taskMonitorEntity);
    }

    public void insertCoordTaskMonitorEntity(CoordTaskMonitorEntity coordTaskMonitorEntity){
        coordTaskMonitorDao.insert(coordTaskMonitorEntity);
    }

    public void insertYarnMonitorEntity(YarnMonitorEntity ym){
        yarnMonitorDao.insert(ym);
    }

    public void insertEmailAlertRecordEntity(EmailAlertRecordEntity eare){
        emailAlertRecordDao.insert(eare);
    }

    public CoordTaskMonitorEntity getCoordTaskById(String id){
        return coordTaskMonitorDao.get(id);
    }


    public Boolean isWorkflowTask(String modelId) {
        AnalysisModelEntity modelEntity = modelDao.get(modelId);
        if(modelEntity.getCronExp() == null || modelEntity.getCoordStart() == null || modelEntity.getCoordEnd() == null) {
            return true;
        }
        return false;
    }

    private String getResourceByStepId(String id) {
        String type;
        switch (id) {
            case "OutputMySQLTable":
                type = "mysql";
                break;
            case "OutputOracleTable":
                type = "oracle";
                break;
            case "OutputDB2Table":
                type = "db2";
                break;
            case "OutputGreenplumTable":
                type = "greenplum";
                break;
            case "OutputMongodbTable":
                type = "mongodb";
                break;
            case "OutputImpalaTable":
                type = "impala";
                break;
            case "OutputHiveTable":
                type = "hive";
                break;
            case "OutputHbaseTable":
                type = "hbase";
                break;
            case "OutputES":
                type = "elasticsearch";
                break;
            default: type = "其它";
        }
        return type;
    }
}
