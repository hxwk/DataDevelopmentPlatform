package com.dfssi.dataplatform.ide.service.restful;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.model.Model;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 模型微服务调用
 * Created by yanghs on 2018/8/10.
 */
@FeignClient(value = "ide-analysismodel", fallback = AnalysisModelHystrix.class)
public interface IAnalysisModelFeign {

    @RequestMapping("/model/listAllModelsOrder")
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, String modelType, String
            modelName, Long startTime, Long endTime, String status, String field, String orderType);

    @RequestMapping("/model/listAllModels")
    public List<AnalysisModelEntity> listAllModels(int pageIdx, int pageSize, Map params);

    @RequestMapping("/model/getModel")
    public Model getModel(String modelId);

    @RequestMapping("/model/saveModel")
    public Model saveModel(Model model);

    @RequestMapping("/model/deleteModel")
    public void deleteModel(String modelId);

    @RequestMapping("/model/getModelByModelId")
    public AnalysisModelEntity getModelByModelId(String modelId);

    //监控信息删除
    @RequestMapping("/model/delectMonitorInfo")
    public void delectMonitorInfo(String modelId);

    //删除任务邮件告警信息
    @RequestMapping("/model/delectEmailAlertInfo")
    public void delectEmailAlertInfo(String modelId);

    //输出表注册
    @RequestMapping("/model/saveResource")
    public Model saveResource(Model model);

    //上传jar包信息保存
    @RequestMapping("/model/saveJar")
    public void saveJar(String jarPath, String description, long jarSize);

    //jar包信息删除
    @RequestMapping("/model/delectJar")
    public void delectJar(String jarPath);

    //获取已上传文件信息
    @RequestMapping("/model/getFileInfo")
    public List<AnalysisJarEntity> getFileInfo();

    //获取oozie任务运行详情
    @RequestMapping("/model/getOozieTaskInfo")
    public Object getOozieTaskInfo(String modelId);

    //获取yarn任务运行详情
    @RequestMapping("/model/getYarnTaskInfo")
    public Object getYarnTaskInfo(String oozieId);

    //保存任务邮件告警配置信息
    @RequestMapping("/model/saveEmailAlertConfig")
    public void saveEmailAlertConfig(String modelId, String userIds, String ruleIds);

    //查看任务邮件告警记录
    @RequestMapping("/model/getEmailAlertRecord")
    public Object getEmailAlertRecord(String modelId);

    @RequestMapping("/model/isWorkflowTask")
    public Boolean isWorkflowTask(String modelId);

    @RequestMapping("/model/getSourceConf")
    List<AnalysisSourceConfEntity> getSourceConf(String sourceId);

    @RequestMapping("/model/listAllSources")
    List<AnalysisSourceEntity> listAllSources(Map<String, Object> params);


    @RequestMapping("/model/batchDelete")
    public void batchDelete(List<AnalysisStepAttrEntity> deletedAttrs);

    @RequestMapping("/model/updateWhenStart")
    public void updateWhenStart(Map params);

    @RequestMapping("/model/findAll")
    public List<DataSourceMeta> findAll();

    @RequestMapping("/model/getConfigByModelId")
    public List<EmailAlertConfigEntity> getConfigByModelId(String modelId);

    @RequestMapping("/model/getOozieId")
    public List<String> getOozieId(String modeId);

    @RequestMapping("/model/updateStatus")
    public void updateStatus(String modelId);

    @RequestMapping("/model/getLastJobId")
    public String getLastJobId(String modelId);

    @RequestMapping("/model/getResourceConf")
    public List<AnalysisResourceConfEntity> getResourceConf(String resourceId);

    @RequestMapping("/model/listResourceAllTable")
    public List<AnalysisResourceEntity> listResourceAllTable(Map params);

    @RequestMapping("/model/listResource")
    public List<AnalysisResourceEntity> listResource(Map params);

    @RequestMapping("/model/listAllSources")
    public List<AnalysisResourceEntity> listAllSources();

    @RequestMapping("/model/deleteByResourceId")
    public void deleteByResourceId(String resourceId);

    @RequestMapping("/model/getByDataresourceId")
    public AnalysisSourceEntity getByDataresourceId(String dataresourceId);

    @RequestMapping("/model/getAllStepTypes")
    public List<AnalysisStepTypeEntity> getAllStepTypes(String stepGroup);

    @RequestMapping("/model/getStepsByModelId")
    public List<AnalysisStepEntity> getStepsByModelId(String modelId);

    @RequestMapping("/model/getByStatus")
    public List<TaskMonitorEntity> getByStatus(String status);

    @RequestMapping("/model/getById")
    public YarnMonitorEntity getById(String id);

    @RequestMapping("/model/getByModelId")
    public List<YarnMonitorEntity> getByModelId(String modelId);

    @RequestMapping("/model/getOozieIdByModelId")
    public List<String> getOozieIdByModelId(String modelId);

    @RequestMapping("/model/updateStatusByOozieTaskId")
    public void updateStatusByOozieTaskId(Map params);

    @RequestMapping("/model/getAllDataResource")
    public List<DataResourceConfEntity> getAllDataResource();
}
