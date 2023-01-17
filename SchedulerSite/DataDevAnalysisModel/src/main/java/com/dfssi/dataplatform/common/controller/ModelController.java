package com.dfssi.dataplatform.common.controller;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.common.service.IModelService;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/8/10.
 */
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private IModelService iModelService;

    @RequestMapping("/listAllModelsOrder")
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, String modelType, String
            modelName, Long startTime, Long endTime, String status, String field, String orderType) {
        return iModelService.listAllModels(pageIdx, pageSize, modelType,
                modelName, startTime, endTime, status, field, orderType);
    }

    @RequestMapping("/listAllModels")
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, Map params) {
        return iModelService.listAllModels(pageIdx, pageSize, params);
    }

    @RequestMapping("/getModel")
    public Model getModel(String modelId) {
        return iModelService.getModel(modelId);
    }

    @RequestMapping("/getModelByModelId")
    public AnalysisModelEntity getModelByModelId(String modelId) {
        return iModelService.getModelByModelId(modelId);
    }

    @RequestMapping("/saveModel")
    public Model saveModel(Model model) {
        return iModelService.saveModel(model);
    }

    @RequestMapping("/deleteModel")
    public void deleteModel(String modelId) {
        iModelService.deleteModel(modelId);
    }

    //监控信息删除
    @RequestMapping("/delectMonitorInfo")
    public void delectMonitorInfo(String modelId) {
        iModelService.delectMonitorInfo(modelId);
    }

    //删除任务邮件告警信息
    @RequestMapping("/delectEmailAlertInfo")
    public void delectEmailAlertInfo(String modelId) {
        iModelService.delectEmailAlertInfo(modelId);
    }

    //输出表注册
    @RequestMapping("/saveResource")
    public Model saveResource(Model model) {
        return iModelService.saveResource(model);
    }

    //上传jar包信息保存
    @RequestMapping("/saveJar")
    public void saveJar(String jarPath, String description, long jarSize) {
        iModelService.saveJar(jarPath, description, jarSize);
    }

    //jar包信息删除
    @RequestMapping("/delectJar")
    public void delectJar(String jarPath) {
        iModelService.delectJar(jarPath);
    }

    //获取已上传文件信息
    @RequestMapping("/getFileInfo")
    public List<AnalysisJarEntity> getFileInfo() {
        return iModelService.getFileInfo();
    }

    //获取oozie任务运行详情
    @RequestMapping("/getOozieTaskInfo")
    public Object getOozieTaskInfo(String modelId) {
        return iModelService.getOozieTaskInfo(modelId);
    }

    //获取yarn任务运行详情
    @RequestMapping("/getYarnTaskInfo")
    public Object getYarnTaskInfo(String oozieId) {
        return iModelService.getYarnTaskInfo(oozieId);
    }

    //保存任务邮件告警配置信息
    @RequestMapping("/saveEmailAlertConfig")
    public void saveEmailAlertConfig(String modelId, String userIds, String ruleIds) {
        iModelService.saveEmailAlertConfig(modelId, userIds, ruleIds);
    }

    //查看任务邮件告警记录
    @RequestMapping("/getEmailAlertRecord")
    public Object getEmailAlertRecord(String modelId) {
        return iModelService.getEmailAlertRecord(modelId);
    }

    @RequestMapping("/isWorkflowTask")
    public Boolean isWorkflowTask(String modelId) {
        return iModelService.isWorkflowTask(modelId);
    }

    @RequestMapping("/listAllSources")
    public List<AnalysisSourceEntity> listAllSources(Map params) {
        return iModelService.listAllSources(params);
    }

    @RequestMapping("/listAllReSources")
    public List<AnalysisResourceEntity> listAllReSources() {
        return iModelService.listAllReSources();
    }

    @RequestMapping("/batchDelete")
    public void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs) {
        iModelService.batchDelete(deletedAttrs);
    }

    @RequestMapping("/updateWhenStart")
    public void updateWhenStart(Map params) {
        iModelService.updateWhenStart(params);
    }

    @RequestMapping("/findAll")
    public List<DataSourceMeta> findAll() {
        return iModelService.findAll();
    }

    @RequestMapping("/getConfigByModelId")
    public List<EmailAlertConfigEntity> getConfigByModelId(String modelId) {
        return iModelService.getConfigByModelId(modelId);
    }

    @RequestMapping("/getOozieId")
    public List<String> getOozieId(String modeId) {
        return iModelService.getOozieId(modeId);
    }

    @RequestMapping("/updateStatus")
    public void updateStatus(String modelId) {
        iModelService.updateStatus(modelId);
    }

    @RequestMapping("/getLastJobId")
    public String getLastJobId(String modelId) {
        return iModelService.getLastJobId(modelId);
    }

    @RequestMapping("/getResourceConf")
    public List<AnalysisResourceConfEntity> getResourceConf(String resourceId) {
        return iModelService.getResourceConf(resourceId);
    }

    @RequestMapping("/listResourceAllTable")
    public List<AnalysisResourceEntity> listResourceAllTable(Map params) {
        return iModelService.listResourceAllTable(params);
    }

    @RequestMapping("/listResource")
    public List<AnalysisResourceEntity> listResource(Map params) {
        return iModelService.listResource(params);
    }

    @RequestMapping("/deleteByResourceId")
    public void deleteByResourceId(String resourceId) {
        iModelService.deleteByResourceId(resourceId);
    }

    @RequestMapping("/getByDataresourceId")
    public AnalysisSourceEntity getByDataresourceId(String dataresourceId) {
        return iModelService.getByDataresourceId(dataresourceId);
    }

    @RequestMapping("/getAllStepTypes")
    public List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup) {
        return iModelService.getAllStepTypes(stepGroup);
    }

    @RequestMapping("/getStepsByModelId")
    public List<AnalysisStepEntity> getStepsByModelId(String modelId) {
        return iModelService.getStepsByModelId(modelId);
    }

    @RequestMapping("/getByStatus")
    public List<TaskMonitorEntity> getByStatus(String status) {
        return iModelService.getByStatus(status);
    }

    @RequestMapping("/getById")
    public YarnMonitorEntity getById(String id) {
        return iModelService.getById(id);
    }

    @RequestMapping("/getByModelId")
    public List<YarnMonitorEntity> getByModelId(String modelId) {
        return iModelService.getByModelId(modelId);
    }

    @RequestMapping("/getOozieIdByModelId")
    public List<String> getOozieIdByModelId(String modelId) {
        return iModelService.getOozieIdByModelId(modelId);
    }

    @RequestMapping("/updateStatusByOozieTaskId")
    public void updateStatusByOozieTaskId(Map params) {
        iModelService.updateStatusByOozieTaskId(params);
    }

    @RequestMapping("/getAllDataResource")
    List<DataResourceConfEntity> getAllDataResource() {
        return getAllDataResource();
    }

    @RequestMapping("/getUserIdsByModelId")
    public List<String> getUserIdsByModelId(String modelId){
        return iModelService.getUserIdsByModelId(modelId);
    }

    @RequestMapping("/getRuleIdsByModelId")
    public List<String> getRuleIdsByModelId(String modelId){
        return iModelService.getRuleIdsByModelId(modelId);
    }

    @RequestMapping("/getRuleByRuleId")
    public EmailAlertRuleEntity getRuleByRuleId(String ruleId){
        return iModelService.getRuleByRuleId(ruleId);
    }

    @RequestMapping("/getUserByUserId")
    public EmailAlertUserEntity getUserByUserId(String userId){
        return iModelService.getUserByUserId(userId);
    }

    @RequestMapping("/getTaskMonitorById")
    public TaskMonitorEntity getTaskMonitorById(String id){
        return iModelService.getTaskMonitorById(id);
    }

    @RequestMapping("/getCoordTaskById")
    public CoordTaskMonitorEntity getCoordTaskById(String id){
        return iModelService.getCoordTaskById(id);
    }

    @RequestMapping("/listRunningTasksByModelId")
    public List<TaskMonitorEntity> listRunningTasksByModelId(String modelId){
        return iModelService.listRunningTasksByModelId(modelId);
    }

    @RequestMapping("/getApplicationIdByOozieId")
    public List<String> getApplicationIdByOozieId(String oozieTaskId){
        return iModelService.getApplicationIdByOozieId(oozieTaskId);
    }

    @RequestMapping("/updateStatusByApplicationId")
    public void updateStatusByApplicationId(Map params){
        iModelService.updateStatusByApplicationId(params);
    }

    @RequestMapping("/listRunningApplicationIdsByOozieId")
    public List<String> listRunningApplicationIdsByOozieId(String oozieTaskId){
        return iModelService.listRunningApplicationIdsByOozieId(oozieTaskId);
    }

    @RequestMapping("/listRunningCoordTasksByModelId")
    public List<CoordTaskMonitorEntity> listRunningCoordTasksByModelId(String oozieTaskId){
        return iModelService.listRunningCoordTasksByModelId(oozieTaskId);
    }

    @RequestMapping("/updateModelStatus")
    public void updateModelStatus(Map params){
        iModelService.updateModelStatus(params);
    }

    @RequestMapping("/listRunningModels")
    public List<AnalysisModelEntity> listRunningModels(){
        return iModelService.listRunningModels();
    }

    @RequestMapping("/insertTaskMonitorEntity")
    public void insertTaskMonitorEntity(TaskMonitorEntity taskMonitorEntity){
        iModelService.insertTaskMonitorEntity(taskMonitorEntity);
    }

    @RequestMapping("/insertCoordTaskMonitorEntity")
    public void insertCoordTaskMonitorEntity(CoordTaskMonitorEntity coordTaskMonitorEntity){
        iModelService.insertCoordTaskMonitorEntity(coordTaskMonitorEntity);
    }

    @RequestMapping("/insertYarnMonitorEntity")
    public void insertYarnMonitorEntity(YarnMonitorEntity yarnMonitorEntity){
        iModelService.insertYarnMonitorEntity(yarnMonitorEntity);
    }

    @RequestMapping("/insertEmailAlertRecordEntity")
    public void insertEmailAlertRecordEntity(EmailAlertRecordEntity emailAlertRecordEntity){
        iModelService.insertEmailAlertRecordEntity(emailAlertRecordEntity);
    }
}
