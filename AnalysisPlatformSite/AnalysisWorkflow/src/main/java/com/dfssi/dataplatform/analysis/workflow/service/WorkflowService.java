package com.dfssi.dataplatform.analysis.workflow.service;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.HdfsUtils;
import com.dfssi.dataplatform.analysis.common.util.JacksonUtils;
import com.dfssi.dataplatform.analysis.workflow.config.OozieConfig;
import com.dfssi.dataplatform.analysis.workflow.mapper.ModelConfDao;
import com.dfssi.dataplatform.analysis.workflow.mapper.MonitorCoordinatorDao;
import com.dfssi.dataplatform.analysis.workflow.mapper.MonitorWorkflowDao;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.oozie.client.OozieClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "WorkflowService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class WorkflowService extends AbstractService {

    @Autowired
    private OozieConfig oozieConfig;
    @Autowired
    private ModelConfDao modelConfDao;
    @Autowired
    private MonitorWorkflowDao monitorWorkflowDao;
    @Autowired
    private MonitorCoordinatorDao monitorCoordinatorDao;


    public void deployModel(String modelId) throws Exception {

        String modelConf = modelConfDao.getModelConf(modelId);
        JsonObject jsonObject = new JsonParser().parse(modelConf).getAsJsonObject();

        //检查并写workflow文件
        WorkflowXml workflowXml = WorkflowXml.newWorkflowXml(jsonObject);
        Preconditions.checkNotNull(workflowXml, "workflow的配置为空！");
        HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), workflowXml.toString(),
                    getAppPath(modelId) + Constants.WORKFLOW_FILE_NAME,
                    oozieConfig.getHdfsUserName());


        //检查是否需要写coordinator文件，并写入
        CoordinatorXml coordinatorXml = CoordinatorXml.newCoordinatorXml(jsonObject);
        if (null != coordinatorXml) {
            HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), coordinatorXml.toString(),
                    getAppPath(modelId) + Constants.COORDINATOR_FILE_NAME,
                    oozieConfig.getHdfsUserName());
        }

        //写参数配置文件
        HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), getSparkTaskDef(jsonObject),
                getAppPath(modelId) + Constants.SPARK_TASK_DEF_FILE_NAME,
                oozieConfig.getHdfsUserName());
    }

    public void undeployModel(String modelId) throws Exception {
        HdfsUtils.deleteDir(oozieConfig.getNameNode(), getAppPath(modelId), oozieConfig.getHdfsUserName());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String startModel(String modelId) throws Exception {
        this.deployModel(modelId);
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());
        Properties conf = wc.createConfiguration();

        conf.setProperty(OozieClient.APP_PATH, oozieConfig.getNameNode() + getAppPath(modelId));
        conf.setProperty("nameNode", oozieConfig.getNameNode());
        conf.setProperty("master", "yarn-cluster");
        conf.setProperty("jobTracker", oozieConfig.getJobTracker());
        conf.setProperty("sparkRootPath", oozieConfig.getSparkRootPath());
        conf.setProperty("sparkActionjar", oozieConfig.getSparkActionjar());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());

        String libpath = oozieConfig.getLibpath();
        conf.setProperty(OozieClient.LIBPATH, libpath);
        conf.setProperty("offlineSparkActionClass", oozieConfig.getOfflineSparkActionClass());
        conf.setProperty("streamingSparkActionClass", oozieConfig.getStreamingSparkActionClass());
        conf.setProperty("externalSparkActionClass", oozieConfig.getExternalSparkActionClass());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());
        conf.setProperty("defaultSparkOpts", oozieConfig.getDefaultSparkOpts());
        conf.setProperty("appRelativePath", modelId);
        if (isWorkflowTsak(modelId)) {
            conf.setProperty("user.name", oozieConfig.getHdfsUserName());
            conf.setProperty("mapreduce.job.user.name", oozieConfig.getHdfsUserName());
            conf.setProperty("queueName", "default");
            conf.setProperty("oozie.use.system.libpath", "true");
        } else {
            conf.setProperty("userName", oozieConfig.getHdfsUserName());
            conf.setProperty("mrUserName", oozieConfig.getHdfsUserName());
            conf.setProperty("queueName", "default");
            conf.setProperty("useSystemLibpath", "true");
        }

        return wc.run(conf);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void suspendModel(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());
        if (isWorkflowTsak(modelId)) {
            List<String> oozieIds = monitorWorkflowDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.suspend(oozieId);
            }
        } else {
            List<String> oozieIds = monitorCoordinatorDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.suspend(oozieId);
            }
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void resumeModel(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());
        if (isWorkflowTsak(modelId)) {
            List<String> oozieIds = monitorWorkflowDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.resume(oozieId);
            }
        } else {
            List<String> oozieIds = monitorCoordinatorDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.resume(oozieId);
            }
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void killModel(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());
        if (isWorkflowTsak(modelId)) {
            List<String> oozieIds = monitorWorkflowDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.kill(oozieId);
            }
        } else {
            List<String> oozieIds = monitorCoordinatorDao.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return;
            }
            for (String oozieId : oozieIds) {
                wc.kill(oozieId);
            }
        }
    }

    private String getSparkTaskDef(JsonObject jsonObject) {
        Document doc = DocumentHelper.createDocument();
        String modelType = JacksonUtils.getAsString(jsonObject, Constants.SPARK_DEF_ELEMENT_TAG_MODELTYPE);
        Element sparkTaskDefEl = doc.addElement(getRootTagByType(modelType));

        sparkTaskDefEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_NAME, JacksonUtils.getAsString(jsonObject, Constants.SPARK_DEF_ATTR_TAG_NAME));
        sparkTaskDefEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_ID, JacksonUtils.getAsString(jsonObject, Constants.SPARK_DEF_ATTR_TAG_ID));
        if (modelType.toLowerCase().contains(Constants.SPARK_DEF_ELEMENT_TAG_STREAMING)) {
            sparkTaskDefEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_DURATION, JacksonUtils.getAsString(jsonObject, Constants.SPARK_DEF_ATTR_TAG_DURATION));
        }

        Element inputsEl = sparkTaskDefEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_INPUTS);
        Element preprocessEl = sparkTaskDefEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_PREPROCESS);
        Element agorithmsEl = sparkTaskDefEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_ALGORITHMS);
        Element outputsEl = sparkTaskDefEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_OUTPUTS);

        JsonArray steps = JacksonUtils.getAsJsonArray(jsonObject, Constants.SPARK_DEF_ATTR_TAG_STEPS);
        JsonArray links = JacksonUtils.getAsJsonArray(jsonObject, Constants.SPARK_DEF_ATTR_TAG_LINKS);
        for (JsonElement stepAsJsonElement: steps) {
            Element stepEl;
            JsonObject step = stepAsJsonElement.getAsJsonObject();
            String stepType = JacksonUtils.getAsString(step, Constants.SPARK_DEF_ELEMENT_TAG_STEPTYPE);

            if (stepType.equalsIgnoreCase(Constants.SPARK_DEF_ELEMENT_TAG_INPUT)) {
                stepEl = inputsEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_INPUT);
            } else if (stepType.equalsIgnoreCase(Constants.SPARK_DEF_ELEMENT_TAG_OUTPUT)) {
                stepEl = outputsEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_OUTPUT);
            } else if (stepType.equalsIgnoreCase(Constants.SPARK_DEF_ELEMENT_TAG_ALGORITHM)) {
                stepEl = agorithmsEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_ALGORITHM);
            } else {
                stepEl = preprocessEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_PROCESS);
            }

            String stepId = JacksonUtils.getAsString(step, Constants.SPARK_DEF_ATTR_TAG_ID);
            String className = JacksonUtils.getAsString(step, Constants.SPARK_DEF_ATTR_TAG_CLASSNAME);
            stepEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_ID, stepId);
            stepEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_CLASSNAME, className);
            stepEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_INPUTIDS, getStepInputIds(links, stepId));

            JsonObject params = JacksonUtils.getAsJsonObject(step, Constants.SPARK_DEF_ELEMENT_TAG_PARAMS);
            if (null != params) {
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    Element paramEl = stepEl.addElement(Constants.SPARK_DEF_ELEMENT_TAG_PARAM);
                    paramEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_NAME, key);
                    paramEl.addAttribute(Constants.SPARK_DEF_ATTR_TAG_VALUE, JacksonUtils.getAsString(params, key));
                }
            }
        }

        return doc.getRootElement().asXML();
    }

    private String getRootTagByType(String modelType) {
        if (modelType.toLowerCase().contains("offline")) {
            return Constants.SPARK_DEF_ELEMENT_TAG_ROOT;
        } else if (modelType.toLowerCase().contains("streaming")) {
            return Constants.STREAMING_DEF_ELEMENT_TAG_ROOT;
        } else {
            return Constants.EXTERNAL_DEF_ELEMENT_TAG_ROOT;
        }
    }

    private String getStepInputIds(JsonArray links, String stepId) {
        Map<String, String> map = new TreeMap<>();
        StringBuilder inputIds = new StringBuilder(" ");
        if (null == links) {
            return "";
        }
        for (JsonElement linkAsJsonElement: links) {
            JsonObject link = linkAsJsonElement.getAsJsonObject();
            String modelStepTo = JacksonUtils.getAsString(link, Constants.SPARK_DEF_ATTR_TAG_MODELSTEPTO);
            if (modelStepTo.contains(stepId)) {
                String modelStepFrom = JacksonUtils.getAsString(link, Constants.SPARK_DEF_ATTR_TAG_MODELSTEPFROM);
                map.put(modelStepTo, modelStepFrom);
            }
        }
        for (String string : map.keySet()) {
            inputIds.append(map.get(string)).append(",");
        }
        return inputIds.substring(0, inputIds.length() - 1).trim();
    }

    private String getAppPath(String modelId) {
        return oozieConfig.getSparkRootPath() + "/app/" + modelId + "/";
    }

    private boolean isWorkflowTsak(String modelId) {
        String modelConf = modelConfDao.getModelConf(modelId);
        JsonObject jsonObject = new JsonParser().parse(modelConf).getAsJsonObject();

        return !JacksonUtils.getAsString(jsonObject, "modelType").toLowerCase().contains("coord");
    }

}
