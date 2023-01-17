package com.dfssi.dataplatform.ide.service.restful;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/8/10.
 */
@Component
public class AnalysisModelHystrix implements IAnalysisModelFeign {
    @Override
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, String modelType, String modelName, Long startTime, Long endTime, String status, String field, String orderType) {
        return null;
    }

    @Override
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, Map params) {
        return null;
    }

    @Override
    public Model getModel(String modelId) {
        return null;
    }

    @Override
    public Model saveModel(Model model) {
        return null;
    }

    @Override
    public void deleteModel(String modelId) {

    }

    @Override
    public AnalysisModelEntity getModelByModelId(String modelId) {
        return null;
    }

    @Override
    public void delectMonitorInfo(String modelId) {

    }

    @Override
    public void delectEmailAlertInfo(String modelId) {

    }

    @Override
    public Model saveResource(Model model) {
        return null;
    }

    @Override
    public void saveJar(String jarPath, String description, long jarSize) {

    }

    @Override
    public void delectJar(String jarPath) {

    }

    @Override
    public List<AnalysisJarEntity> getFileInfo() {
        return null;
    }

    @Override
    public Object getOozieTaskInfo(String modelId) {
        return null;
    }

    @Override
    public Object getYarnTaskInfo(String oozieId) {
        return null;
    }

    @Override
    public void saveEmailAlertConfig(String modelId, String userIds, String ruleIds) {

    }

    @Override
    public Object getEmailAlertRecord(String modelId) {
        return null;
    }

    @Override
    public Boolean isWorkflowTask(String modelId) {
        return null;
    }

    @Override
    public List<AnalysisSourceConfEntity> getSourceConf(String sourceId) {
        return null;
    }

    @Override
    public List<AnalysisSourceEntity> listAllSources(Map<String, Object> params) {
        return null;
    }

    @Override
    public void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs) {

    }

    @Override
    public void updateWhenStart(Map params) {

    }

    @Override
    public List<DataSourceMeta> findAll() {
        return null;
    }

    @Override
    public List<EmailAlertConfigEntity> getConfigByModelId(String modelId) {
        return null;
    }

    @Override
    public List<String> getOozieId(String modeId) {
        return null;
    }

    @Override
    public void updateStatus(String modelId) {

    }

    @Override
    public String getLastJobId(String modelId) {
        return null;
    }

    @Override
    public List<AnalysisResourceConfEntity> getResourceConf(String resourceId) {
        return null;
    }

    @Override
    public List<AnalysisResourceEntity> listResourceAllTable(Map params) {
        return null;
    }

    @Override
    public List<AnalysisResourceEntity> listResource(Map params) {
        return null;
    }

    @Override
    public List<AnalysisResourceEntity> listAllSources() {
        return null;
    }

    @Override
    public void deleteByResourceId(String resourceId) {

    }

    @Override
    public AnalysisSourceEntity getByDataresourceId(String dataresourceId) {
        return null;
    }

    @Override
    public List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup) {
        return null;
    }

    @Override
    public List<AnalysisStepEntity> getStepsByModelId(String modelId) {
        return null;
    }

    @Override
    public List<TaskMonitorEntity> getByStatus(String status) {
        return null;
    }

    @Override
    public YarnMonitorEntity getById(String id) {
        return null;
    }

    @Override
    public List<YarnMonitorEntity> getByModelId(String modelId) {
        return null;
    }

    @Override
    public List<String> getOozieIdByModelId(String modelId) {
        return null;
    }

    @Override
    public void updateStatusByOozieTaskId(Map params) {

    }

    @Override
    public List<DataResourceConfEntity> getAllDataResource() {
        return null;
    }
}
