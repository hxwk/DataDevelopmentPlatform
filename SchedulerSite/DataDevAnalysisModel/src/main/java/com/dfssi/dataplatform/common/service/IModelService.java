package com.dfssi.dataplatform.common.service;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/8/10.
 */
public interface IModelService {
    List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, String modelType, String
            modelName, Long startTime, Long endTime, String status, String field, String orderType) ;

    List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, Map params);

    Model getModel(String modelId);

    Model saveModel(Model model) ;

    void deleteModel(String modelId);

    //监控信息删除
    void delectMonitorInfo(String modelId);

    //删除任务邮件告警信息
    void delectEmailAlertInfo(String modelId);

    //输出表注册
    Model saveResource(Model model);

    //上传jar包信息保存
    void saveJar(String jarPath, String description, long jarSize);

    //jar包信息删除
    void delectJar(String jarPath);

    //获取已上传文件信息
    List<AnalysisJarEntity> getFileInfo();

    //获取oozie任务运行详情
    Object getOozieTaskInfo(String modelId);

    //获取yarn任务运行详情
    Object getYarnTaskInfo(String oozieId);
    //保存任务邮件告警配置信息
    void saveEmailAlertConfig(String modelId, String userIds, String ruleIds);

    //查看任务邮件告警记录
    Object getEmailAlertRecord(String modelId) ;

    Boolean isWorkflowTask(String modelId);

    List<AnalysisSourceConfEntity>  getSourceConf(String resourceId);

    List<AnalysisSourceEntity> listAllSources(Map params);

    void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs);

    void updateStatusByModelId(Map params);

    void updateWhenStart(Map params);

    List<DataSourceMeta> findAll();

    List<EmailAlertConfigEntity> getConfigByModelId(String modelId);

   List<String> getUserIdsByModelId(String modelId);

   List<String> getRuleIdsByModelId(String modelId);

   EmailAlertRuleEntity getRuleByRuleId(String ruleId);

   EmailAlertUserEntity getUserByUserId(String userId);

    List<String> getOozieId(String modeId);

    void updateStatus(String modelId);

    String getLastJobId(String modelId);

    List<AnalysisResourceConfEntity> getResourceConf(String resourceId);


    List<AnalysisResourceEntity> listResourceAllTable(Map params);

    List<AnalysisResourceEntity> listResource(Map params);

    List<AnalysisResourceEntity> listAllSources();

    void deleteByResourceId(String resourceId);

    AnalysisSourceEntity getByDataresourceId(String dataresourceId);

    List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup);

    List<AnalysisStepEntity> getStepsByModelId(String modelId);

    List<TaskMonitorEntity> getByStatus(String status);

    YarnMonitorEntity getById(String id);

    List<YarnMonitorEntity> getByModelId(String modelId);

    List<String> getOozieIdByModelId(String modelId);

    void updateStatusByOozieTaskId(Map params);

    AnalysisModelEntity getModelByModelId(String modelId);

    List<DataResourceConfEntity> getAllDataResource();

    List<AnalysisResourceEntity> listAllReSources();

    TaskMonitorEntity getTaskMonitorById(String id);

    CoordTaskMonitorEntity getCoordTaskById(String id);

    List<TaskMonitorEntity> listRunningTasksByModelId(String modelId);

   List<String> getApplicationIdByOozieId(String oozieTaskId);

   void updateStatusByApplicationId(Map params);

   List<String> listRunningApplicationIdsByOozieId(String oozieTaskId);

   List<CoordTaskMonitorEntity> listRunningCoordTasksByModelId(String modelId);

   void updateModelStatus(Map params);

   List<AnalysisModelEntity> listRunningModels();

   void insertTaskMonitorEntity(TaskMonitorEntity taskMonitorEntity);

   void insertCoordTaskMonitorEntity(CoordTaskMonitorEntity coordTaskMonitorEntity);

   void insertYarnMonitorEntity(YarnMonitorEntity ym);

   void insertEmailAlertRecordEntity(EmailAlertRecordEntity eare);


}
