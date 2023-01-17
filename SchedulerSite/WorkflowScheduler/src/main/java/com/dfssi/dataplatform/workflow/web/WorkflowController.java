package com.dfssi.dataplatform.workflow.web;

import com.dfssi.dataplatform.analysis.entity.*;
import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.workflow.app.OozieConfig;
import com.dfssi.dataplatform.workflow.builder.*;
import com.dfssi.dataplatform.workflow.restful.IAnalysisModelFeign;
import com.dfssi.dataplatform.workflow.service.EmailSenderService;
import com.dfssi.dataplatform.workflow.utils.HdfsUtils;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.oozie.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;

@Controller
@RequestMapping(value = "workflow")
public class WorkflowController extends AbstractController {

    private final static String LOG_TAG_WORKFLOW = "[WorkFlow]";
    private static final String WORKFLOW_FILE_NAME = "workflow.xml";
    private static final String COORDINATOR_FILE_NAME = "coordinator.xml";
    private static final String SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME = "OfflineTaskDef.xml";
    private static final String SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME = "StreamingTaskDef.xml";
    private static final String SPARK_INTEGRATE_ANALYSIS_DEF_FILE_NAME = "IntegrateTaskDef.xml";
    private static final String SPARK_EXTERNAL_ANALYSIS_DEF_FILE_NAME = "ExternalTaskDef.xml";


    private static volatile YarnClient yarnClient;
    private static volatile OozieClient oozieClient;

    @Autowired
    private IAnalysisModelFeign iAnalysisModelFeign;

    @Autowired
    private EmailSenderService emailSenderService;

    private OozieConfig oozieConfig;

    @ResponseBody
    @RequestMapping(value = "explordeddeploy", method = RequestMethod.POST)
    public Object explordedDeploy(HttpServletRequest req, HttpServletResponse response, Model model, String request) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Deploy model:\n" + request);

            com.dfssi.dataplatform.analysis.model.Model analysisModel = this.deployModel(com.dfssi
                    .dataplatform.analysis.model.Model.buildFromJson(request));
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Deploy mode successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("modelId", analysisModel.getId());

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to explorded deploy model.\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to explorded deploy model.", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "deploy/{modelId}")
    public String deploy(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) throws Exception {

        this.deployModel(modelId);

        return STATUS_MESSAGE_SUCCESS;
    }

    @ResponseBody
    @RequestMapping(value = "undeploy/{modelId}")
    public Object undeploy(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Undeploy model. modelId=" + modelId);

            this.undeployModel(modelId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Undeploy mode successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("modelId", modelId);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to undeploy model. modelId=" + modelId + "\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to undeploy model. modelId=" + modelId, Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "startoozie/{modelId}")
    public Object startOozie(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Startup oozie task. modelId=" + modelId);
            String oozieTaskId = this.startOozieTask(modelId);
            if (oozieTaskId.contains("fail")) {
                Map result = this.buildResult(STATUS_MESSAGE_FAIL, oozieTaskId);
                return result;
            }
            this.updateTaskStatusWhenStart(modelId, oozieTaskId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Startup oozie successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("oozieTaskId", oozieTaskId);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to startup oozie. modelId=" + modelId + "\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to startup oozie. modelId=" + modelId, Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "startooziecoord/{modelId}")
    public Object startOozieCoord(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            modelId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Startup oozie coordinator task. modelId=" + modelId);

            String oozieTaskId = this.startOozieCoordTask(modelId);
            this.updateCoorTaskStatusWhenStart(modelId, oozieTaskId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Startup oozie coordinator successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("oozieCoordinatorTaskId", oozieTaskId);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to startup oozie coordinator. modelId=" + modelId + "\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to startup oozie coordinator. modelId=" + modelId, Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "killJob/{modelId}")
    public Object killJob(HttpServletRequest req, HttpServletResponse response, Model model,@PathVariable String
            modelId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Entry killJob !");

            String oozieTaskId = this.killJob(modelId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "killJob oozie task successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("oozieCoordinatorTaskId", oozieTaskId);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to getAllOozieTask", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to getAllOozieTask", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getJobInfo/updateStatus")
    public Object updateJobStatus(HttpServletRequest req, HttpServletResponse response) {
        try {
            this.updateJobStatus();
            logger.info(LOG_TAG_WORKFLOW + "Update job oozie task status successfully !");
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Update job oozie task status successfully.");
            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to update job oozie task status", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return this.buildResult(STATUS_CODE_ERROR, "Failed to getAllOozieTask", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskInfo/{modelId}")
    public Object getOozieTaskInfoByModelId(HttpServletRequest req, HttpServletResponse response, Model model,@PathVariable String
            modelId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Get oozie task info !");

            Object oozieTaskInfos = this.getOozieTaskInfoByModelId(modelId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "killJob oozie task successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("oozieTaskInfos", oozieTaskInfos);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to get oozie task info !", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to get oozie task info !", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getYarnTaskInfo/{oozieId}")
    public Object getYarnTaskInfoByOozieId(HttpServletRequest req, HttpServletResponse response, Model model,@PathVariable String
            oozieId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Get oozie task info !");

            Object yarnTaskInfos = this.getYarnTaskInfoByOozieId(oozieId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "killJob yarn task successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("yarnTaskInfos", yarnTaskInfos);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to get yarn task info !", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to get yarn task info !", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getOozieTaskLog/{oozieId}")
    public Object getOozieTaskLog(HttpServletRequest req, HttpServletResponse response, Model model,@PathVariable String
            oozieId) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Get oozie task log.");

            String oozieTaskLog = this.getOozieTaskLog(oozieId);
            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Get oozie task log successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("oozieTaskLog", oozieTaskLog);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to get oozie task log !", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to get oozie task log !", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    //文件上传
    @ResponseBody
    @RequestMapping(value = "upload/file", method = RequestMethod.POST)
    public String uploadFileToHdfs(HttpServletRequest req, HttpServletResponse response, String request) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Upload file:\n" + request);
            String path = this.uploadFileToHdfs(request);

            return path;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to upload file.\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return null;
        }
    }

    //文件删除
    @ResponseBody
    @RequestMapping(value = "delect/file", method = RequestMethod.POST)
    public Object delectFileFromHdfs(HttpServletRequest req, HttpServletResponse response, String request) {
        try {
            logger.info(LOG_TAG_WORKFLOW + "Delect file:\n" + request);
            this.delectFileFromHdfs(request);

            Map result = this.buildResult(STATUS_CODE_SUCCESS, "Delect file successfully.");
            ((Map) result.get(TAG_RESULT_BODY)).put("path", request);

            return result;
        } catch (Throwable t) {
            logger.error(LOG_TAG_WORKFLOW + "Failed to delect file.\n", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return this.buildResult(STATUS_CODE_ERROR, "Failed to delect file.", Exceptions
                    .getStackTraceAsString(t));
        }
    }

    public com.dfssi.dataplatform.analysis.model.Model getModel(String modelId) {
        AnalysisModelEntity modelEntity = iAnalysisModelFeign.getModelByModelId(modelId);
        List<AnalysisLinkEntity> hopEntities = iAnalysisModelFeign.getModel(modelId).toLinkEntities();
        List<AnalysisStepEntity> stepEntities = iAnalysisModelFeign.getStepsByModelId(modelId);

        return com.dfssi.dataplatform.analysis.model.Model.buildFromModelEntity(modelEntity, hopEntities, stepEntities);
    }

    public AnalysisTaskBuilder getAnalysisTaskBuilder(String modelId) throws Exception {
        com.dfssi.dataplatform.analysis.model.Model model = this.getModel(modelId);

        return getAnalysisTaskBuilder(model);
    }

    public AnalysisTaskBuilder getAnalysisTaskBuilder(com.dfssi.dataplatform.analysis.model.Model model) throws Exception {
        AnalysisTaskBuilder analysisTaskBuilder = null;
        if (com.dfssi.dataplatform.analysis.model.Model.isOfflineAnalysisModel(model.getModelTypeId())) {
            analysisTaskBuilder = new OfflineTaskBuilder(model);
        } else if (com.dfssi.dataplatform.analysis.model.Model.isStreamingAnalysisModel(model.getModelTypeId())) {
            analysisTaskBuilder = new StreamingTaskBuilder(model);
        } else if (com.dfssi.dataplatform.analysis.model.Model.isIntegrateAnalysisModel(model.getModelTypeId())) {
            analysisTaskBuilder = new IntegrateTaskBuilder(model);
        } else if (com.dfssi.dataplatform.analysis.model.Model.isExternalAnalysisModel(model.getModelTypeId())) {
            analysisTaskBuilder = new ExternalTaskBuilder(model);
        }
        return analysisTaskBuilder;
    }

    public void deployModel(String modelId) throws Exception {
        com.dfssi.dataplatform.analysis.model.Model model = this.getModel(modelId);
        this.deployModel(model);
    }

    public com.dfssi.dataplatform.analysis.model.Model deployModel(com.dfssi.dataplatform.analysis.model.Model model) throws Exception {
        HdfsUtils.deleteDir(oozieConfig.getNameNode(), getAppPath(model.getId()), oozieConfig.getHdfsUserName());
        AnalysisTaskBuilder analysisTaskBuilder = this.getAnalysisTaskBuilder(model);
        analysisTaskBuilder.build();

        //生成workflow.xml
        HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), analysisTaskBuilder.getWorkflowAppDef().toXml(),
                getAppPath(model.getId()) + WORKFLOW_FILE_NAME, oozieConfig.getHdfsUserName());

        if (analysisTaskBuilder instanceof OfflineTaskBuilder
                && model.getCronExp() != null) {
            //生成coordinator.xml
            HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), analysisTaskBuilder.getCoordinatorAppDef().toXml(), getAppPath
                    (model.getId()) + COORDINATOR_FILE_NAME, oozieConfig.getHdfsUserName());
        }

        //生成任务运行参数配置xml
        HdfsUtils.writeHdfsFile(oozieConfig.getNameNode(), analysisTaskBuilder.getSparkTaskDef().toXml(), getAppPath
                (model.getId()) + getSparkTaskDefFileName(model), oozieConfig.getHdfsUserName());

        return model;
    }

    private String getSparkTaskDefFileName(com.dfssi.dataplatform.analysis.model.Model model) {
        if (com.dfssi.dataplatform.analysis.model.Model.isOfflineAnalysisModel(model.getModelTypeId())) {
            return SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME;
        } else if (com.dfssi.dataplatform.analysis.model.Model.isStreamingAnalysisModel(model.getModelTypeId())) {
            return SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME;
        } else if (com.dfssi.dataplatform.analysis.model.Model.isIntegrateAnalysisModel(model.getModelTypeId())) {
            return SPARK_INTEGRATE_ANALYSIS_DEF_FILE_NAME;
        } else {
            return SPARK_EXTERNAL_ANALYSIS_DEF_FILE_NAME;
        }
    }

    public void undeployModel(String modelId) throws Exception {
        HdfsUtils.deleteDir(oozieConfig.getNameNode(), getAppPath(modelId), oozieConfig.getHdfsUserName());
    }

    private String getAppPath(String modelId) {
        return oozieConfig.getSparkRootPath() + "/app/" + modelId + "/";
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String startOozieTask(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());

        Properties conf = wc.createConfiguration();
        conf.setProperty(OozieClient.APP_PATH, oozieConfig.getNameNode() + getAppPath(modelId));
        conf.setProperty("nameNode", oozieConfig.getNameNode());
        conf.setProperty("master", "yarn-cluster");
        conf.setProperty("jobTracker", oozieConfig.getJobTracker());
        conf.setProperty("user.name", oozieConfig.getHdfsUserName());
        conf.setProperty("mapreduce.job.user.name", oozieConfig.getHdfsUserName());
        conf.setProperty("queueName", "default");
        conf.setProperty("oozie.use.system.libpath", "true");
        conf.setProperty("sparkRootPath", oozieConfig.getSparkRootPath());
        conf.setProperty("sparkActionjar", oozieConfig.getSparkActionjar());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());
        String libpath = oozieConfig.getLibpath();
        if (libpath != null && !libpath.isEmpty())
            conf.setProperty(OozieClient.LIBPATH, libpath);
        conf.setProperty("offlineSparkActionClass", oozieConfig.getOfflineSparkActionClass());
        conf.setProperty("streamingSparkActionClass", oozieConfig.getStreamingSparkActionClass());
        conf.setProperty("integrateSparkActionClass", oozieConfig.getIntegrateSparkActionClass());
        conf.setProperty("externalSparkActionClass", oozieConfig.getExternalSparkActionClass());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());
        conf.setProperty("offlineSparkOpts", oozieConfig.getOfflineSparkOpts());
        conf.setProperty("streamingSparkOpts", oozieConfig.getStreamingSparkOpts());
        conf.setProperty("appRelativePath", modelId);
        Object memoryInfo = this.getMemoryInfo(modelId);
        // 判断集群内存是否足够执行本次任务
        boolean isEnough = memoryInfo.toString().contains("success");
        if(isEnough) {
            String oozieId = wc.run(conf);
            return oozieId;
        } else {
            return memoryInfo.toString();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String startOozieCoordTask(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());

        Properties conf = wc.createConfiguration();
        conf.setProperty(OozieClient.COORDINATOR_APP_PATH, oozieConfig.getNameNode() + getAppPath(modelId));
        conf.setProperty("nameNode", oozieConfig.getNameNode());
        conf.setProperty("master", "yarn-cluster");
        conf.setProperty("jobTracker", oozieConfig.getJobTracker());
        conf.setProperty("userName", oozieConfig.getHdfsUserName());
        conf.setProperty("mrUserName", oozieConfig.getHdfsUserName());
        conf.setProperty("queueName", "default");
        conf.setProperty("useSystemLibpath", "true");
        conf.setProperty("sparkRootPath", oozieConfig.getSparkRootPath());
        conf.setProperty("sparkActionjar", oozieConfig.getSparkActionjar());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());
        String libpath = oozieConfig.getLibpath();
        if (libpath != null && !libpath.isEmpty())
            conf.setProperty(OozieClient.LIBPATH, libpath);
        conf.setProperty("offlineSparkActionClass", oozieConfig.getOfflineSparkActionClass());
        conf.setProperty("streamingSparkActionClass", oozieConfig.getStreamingSparkActionClass());
        conf.setProperty("integrateSparkActionClass", oozieConfig.getIntegrateSparkActionClass());
        conf.setProperty("externalSparkActionClass", oozieConfig.getExternalSparkActionClass());
        conf.setProperty("sparkOpts", oozieConfig.getSparkOpts());
        conf.setProperty("offlineSparkOpts", oozieConfig.getOfflineSparkOpts());
        conf.setProperty("streamingSparkOpts", oozieConfig.getStreamingSparkOpts());
        conf.setProperty("appRelativePath", modelId);

        String oozieId = wc.run(conf);

        return oozieId;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String killJob(String modelId) throws Exception {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());
        if (isWorkflowTsak(modelId)) {
            List<String> oozieIds = iAnalysisModelFeign.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return "fail";
            }
            for (String oozieId : oozieIds) {
                wc.kill(oozieId);
            }
            return "succ";
        } else {
            List<String> oozieIds = iAnalysisModelFeign.getOozieIdByModelId(modelId);
            if (oozieIds.size() == 0) {
                return "fail";
            }
            for (String oozieId : oozieIds) {
                wc.kill(oozieId);
            }
            return "succ";
        }
    }

    //文件上传
    public String uploadFileToHdfs(String localPath) throws Exception {
        String substring = localPath.substring(localPath.lastIndexOf('\\') + 1);
        HdfsUtils.deleteDir(oozieConfig.getNameNode(), oozieConfig.getLibpath() + '/' + substring, oozieConfig.getHdfsUserName());

        HdfsUtils.copyLocalFileToHdfs(oozieConfig.getNameNode(), localPath,
                oozieConfig.getLibpath() + '/' + substring, oozieConfig.getHdfsUserName());

        return oozieConfig.getLibpath() + '/' + substring;

    }

    //文件删除
    public void delectFileFromHdfs(String localPath) throws Exception {
        HdfsUtils.deleteDir(oozieConfig.getNameNode(), localPath, oozieConfig.getHdfsUserName());
    }

    /**
     * 更新workflow类型model任务启动时状态
     * @param modelId
     * @param oozieId
     * @throws OozieClientException
     * @throws IOException
     * @throws YarnException
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateTaskStatusWhenStart(String modelId, String oozieId) throws OozieClientException, IOException, YarnException {

        oozieClientInit();
        WorkflowJob jobInfo = oozieClient.getJobInfo(oozieId);

        Map map = new HashMap();
        map.put("modelId", modelId);
        map.put("status", "执行中");
        iAnalysisModelFeign.updateModelStatus(map);

        TaskMonitorEntity taskMonitorEntity = new TaskMonitorEntity(
                modelId,
                oozieId,
                jobInfo.getAppName(),
                getStatus(jobInfo.getStatus()),
                dateConvert(jobInfo.getStartTime()),
                dateConvert(jobInfo.getEndTime()),
                dateConvert(jobInfo.getCreatedTime()),
                dateConvert(jobInfo.getLastModifiedTime())
        );
        iAnalysisModelFeign.insertTaskMonitorEntity(taskMonitorEntity);
    }

    /**
     * 更新coordinator类型model任务启动时状态
     * @param modelId
     * @param oozieId
     * @throws OozieClientException
     * @throws IOException
     * @throws YarnException
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateCoorTaskStatusWhenStart(String modelId, String oozieId) throws OozieClientException, IOException, YarnException {

        oozieClientInit();
        CoordinatorJob coordJobInfo = oozieClient.getCoordJobInfo(oozieId);

        Map map = new HashMap<String, Object>();
        map.put("modelId", modelId);
        map.put("status", "执行中");
        iAnalysisModelFeign.updateModelStatus(map);

        CoordTaskMonitorEntity coordTaskMonitorEntity = new CoordTaskMonitorEntity(
                modelId,
                oozieId,
                coordJobInfo.getAppName(),
                getCoordStatus(coordJobInfo.getStatus()),
                coordJobInfo.getFrequency(),
                dateConvert(coordJobInfo.getStartTime()),
                dateConvert(coordJobInfo.getEndTime()),
                coordJobInfo.getTimeZone()
        );
        iAnalysisModelFeign.insertCoordTaskMonitorEntity(coordTaskMonitorEntity);
    }

    /**
     * 定时更新正在运行的model任务状态及异常状态告警
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Scheduled(fixedDelay = 1000 * 60)
    public void updateJobStatus() throws Exception {

        oozieClientInit();
        yarnClientInit();
        List<AnalysisModelEntity> analysisModelEntities = iAnalysisModelFeign.listRunningModels();

        for (AnalysisModelEntity analysisModel : analysisModelEntities) {
            String modelId = analysisModel.getId();

            if (isWorkflowTsak(modelId)) {
                List<TaskMonitorEntity> tes = iAnalysisModelFeign.listRunningTasksByModelId(modelId);
                for (TaskMonitorEntity te : tes) {
                    updateTaskStatus(te, modelId);
                    emailAlert(iAnalysisModelFeign.getTaskMonitorById(te.getId()), modelId);
                }
            } else {
                List<CoordTaskMonitorEntity> ctes = iAnalysisModelFeign.listRunningCoordTasksByModelId(modelId);
                for (CoordTaskMonitorEntity cte : ctes) {
                    updateCoordTaskStatus(cte, modelId);
                    emailAlert(iAnalysisModelFeign.getCoordTaskById(cte.getId()), modelId);
                }
            }

            updateModelStatus(modelId);
        }
//        yarnClient.close();
    }

    /**
     * 获取model任务下的所有oozie任务详情
     * @param modelId
     * @return
     * @throws OozieClientException
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object getOozieTaskInfoByModelId(String modelId) throws OozieClientException {
        oozieClientInit();
        List<Object> jobInfos = new ArrayList<>();
        if (isWorkflowTsak(modelId)) {
            List<String> oozieIds = iAnalysisModelFeign.getOozieIdByModelId(modelId);
            for (String oozieId : oozieIds) {
                jobInfos.add(oozieClient.getJobInfo(oozieId));
            }
        } else {
            List<String> oozieIds = iAnalysisModelFeign.getOozieIdByModelId(modelId);
            for (String oozieId : oozieIds) {
                jobInfos.add(oozieClient.getCoordJobInfo(oozieId));
            }
        }
        return jobInfos;
    }

    /**
     * 获取oozie任务下的所有yarn任务详情
     * @param oozieId
     * @return
     * @throws IOException
     * @throws YarnException
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object getYarnTaskInfoByOozieId(String oozieId) throws IOException, YarnException {
        yarnClientInit();
        List<ApplicationReport> yarnInfos = new ArrayList<>();
        List<String> applicationIds = iAnalysisModelFeign.getApplicationIdByOozieId(oozieId);
        for (String applicationId : applicationIds) {
            ApplicationId ai = getApplicationId(applicationId, 0);
            yarnInfos.add(yarnClient.getApplicationReport(ai));
        }
        return yarnInfos;
    }

    /**
     * 获取oozie任务日志
     * @param oozieId
     * @return
     * @throws OozieClientException
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String getOozieTaskLog(String oozieId) throws OozieClientException {
        oozieClientInit();
        return oozieClient.getJobLog(oozieId);
    }

    /**
     * 更新workflow任务状态及相关yarn任务状态
     * @param te
     * @param modelId
     * @throws OozieClientException
     * @throws IOException
     * @throws YarnException
     */
    private void updateTaskStatus(TaskMonitorEntity te, String modelId) throws OozieClientException, IOException, YarnException {
        String oozieId = te.getOozieTaskId();
        WorkflowJob jobInfo = oozieClient.getJobInfo(oozieId);
        Map map = new HashMap<>();

        map.put("oozieTaskId", oozieId);
        map.put("status", getStatus(jobInfo.getStatus()));
        map.put("startTime", dateConvert(jobInfo.getStartTime()));
        map.put("endTime", dateConvert(jobInfo.getEndTime()));
        map.put("createTime", dateConvert(jobInfo.getCreatedTime()));
        map.put("lastModifiedTime", dateConvert(jobInfo.getLastModifiedTime()));
        map.put("modelId", modelId);

        iAnalysisModelFeign.updateStatusByOozieTaskId(map);

        List<WorkflowAction> actions = jobInfo.getActions();
        WorkflowAction wa = getWorkflowAction(actions);
        if (wa == null) {
            return;
        }
        List<String> applicationIds1 = iAnalysisModelFeign.getApplicationIdByOozieId(oozieId);
        List<String> applicationIds2 = iAnalysisModelFeign.listRunningApplicationIdsByOozieId(oozieId);

        ApplicationId applicationId = getApplicationIdByConsoleUrl(wa, jobInfo.getAppName());
        ApplicationReport ar = yarnClient.getApplicationReport(applicationId);

        if (applicationIds1 == null || !applicationIds1.contains(applicationId.toString())) {

            YarnMonitorEntity ym = new YarnMonitorEntity(
                    applicationId.toString(),
                    modelId,
                    oozieId,
                    ar.getName(),
                    getStatus(ar.getFinalApplicationStatus()),
                    dateConvert(ar.getStartTime()),
                    dateConvert(ar.getFinishTime()),
                    ar.getApplicationResourceUsageReport().getNumUsedContainers(),
                    ar.getApplicationResourceUsageReport().getUsedResources().getMemory(),
                    ar.getApplicationResourceUsageReport().getUsedResources().getVirtualCores(),
                    ar.getProgress()
            );
            iAnalysisModelFeign.insertYarnMonitorEntity(ym);

        } else if(applicationIds2 != null && applicationIds2.contains(applicationId.toString())) {
            Map yarnMap = new HashMap();
            yarnMap.put("applicationId", applicationId.toString());
            yarnMap.put("status", getStatus(ar.getFinalApplicationStatus()));
            yarnMap.put("startTime", dateConvert(ar.getStartTime()));
            yarnMap.put("finishTime", dateConvert(ar.getFinishTime()));
            ApplicationResourceUsageReport aru = ar.getApplicationResourceUsageReport();
            Resource ur = aru.getUsedResources();
            yarnMap.put("runContainers", aru.getNumUsedContainers());
            yarnMap.put("memory", ur.getMemory());
            yarnMap.put("virtualCores", ur.getVirtualCores());
            yarnMap.put("progress", ar.getProgress());

            iAnalysisModelFeign.updateStatusByApplicationId(yarnMap);
        }
    }

    /**
     * 更新coordinator任务状态及相关yarn任务状态
     * @param cte
     * @param modelId
     * @throws OozieClientException
     * @throws IOException
     * @throws YarnException
     */
    private void updateCoordTaskStatus(CoordTaskMonitorEntity cte, String modelId) throws OozieClientException, IOException, YarnException {
        String oozieId = cte.getOozieTaskId();
        CoordinatorJob coordJobInfo = oozieClient.getCoordJobInfo(oozieId);
        Map map = new HashMap<>();

        map.put("modelId", modelId);
        map.put("oozieTaskId", oozieId);
        map.put("status", getCoordStatus(coordJobInfo.getStatus()));
        map.put("cronExp", coordJobInfo.getFrequency());
        map.put("coordStart", dateConvert(coordJobInfo.getStartTime()));
        map.put("coordEnd", dateConvert(coordJobInfo.getEndTime()));
        map.put("timezone", coordJobInfo.getTimeZone());

        iAnalysisModelFeign.updateStatusByOozieTaskId(map);

        List<CoordinatorAction> actions = coordJobInfo.getActions();
        List<String> applicationIds1 = iAnalysisModelFeign.getApplicationIdByOozieId(oozieId);
        List<String> applicationIds2 = iAnalysisModelFeign.listRunningApplicationIdsByOozieId(oozieId);
        for (CoordinatorAction action : actions) {

            ApplicationId applicationId = getApplicationIdByConsoleUrl(action, coordJobInfo.getAppName());
            ApplicationReport ar = yarnClient.getApplicationReport(applicationId);

            if (applicationIds1 == null || !applicationIds1.contains(applicationId.toString())) {

                YarnMonitorEntity ym = new YarnMonitorEntity(
                        applicationId.toString(),
                        modelId,
                        oozieId,
                        ar.getName(),
                        getStatus(ar.getFinalApplicationStatus()),
                        dateConvert(ar.getStartTime()),
                        dateConvert(ar.getFinishTime()),
                        ar.getApplicationResourceUsageReport().getNumUsedContainers(),
                        ar.getApplicationResourceUsageReport().getUsedResources().getMemory(),
                        ar.getApplicationResourceUsageReport().getUsedResources().getVirtualCores(),
                        ar.getProgress()
                );
                iAnalysisModelFeign.insertYarnMonitorEntity(ym);

            } else if(applicationIds2 != null && applicationIds2.contains(applicationId.toString())) {
                Map yarnMap = new HashMap();
                yarnMap.put("applicationId", applicationId.toString());
                yarnMap.put("status", getStatus(ar.getFinalApplicationStatus()));
                yarnMap.put("startTime", dateConvert(ar.getStartTime()));
                yarnMap.put("finishTime", dateConvert(ar.getFinishTime()));
                ApplicationResourceUsageReport aru = ar.getApplicationResourceUsageReport();
                Resource ur = aru.getUsedResources();
                yarnMap.put("runContainers", aru.getNumUsedContainers());
                yarnMap.put("memory", ur.getMemory());
                yarnMap.put("virtualCores", ur.getVirtualCores());
                yarnMap.put("progress", ar.getProgress());

                iAnalysisModelFeign.updateStatusByApplicationId(yarnMap);
            }
        }
    }

    /**
     * workflow任务邮件告警
     * @param te
     * @param modelId
     */
    private void emailAlert(TaskMonitorEntity te, String modelId)  {
        String name = iAnalysisModelFeign.getModelByModelId(modelId).getName();
        String status = te.getStatus();
        List<String> users = iAnalysisModelFeign.getUserIdsByModelId(modelId);
        List<String> rules = iAnalysisModelFeign.getRuleIdsByModelId(modelId);
        String[] userIds = Joiner.on(";").skipNulls().join(users).split(";");
        String[] ruleIds = Joiner.on(";").skipNulls().join(rules).split(";");
        String[] emails = new String[userIds.length];

        for (int i = 0; i < userIds.length; i++) {
            emails[i] = iAnalysisModelFeign.getUserByUserId(userIds[i]).getEmail();
        }

        for (String ruleId : ruleIds) {
            if (iAnalysisModelFeign.getRuleByRuleId(ruleId).getStatus().equals(status)) {
                String log;
                try {
                    log = getOozieTaskLog(te.getOozieTaskId());
                } catch (OozieClientException e) {
                    log = "获取任务日志失败";
                }
                String message = "任务【" + name + "】在 " + dateConvert(new Date()) +
                        " 触发告警规则，运行状态为【" + te.getStatus() + "】，\r\n任务日志：\r\n" + log;
                String message1 = "任务【" + name + "】在 " + dateConvert(new Date()) +
                        " 触发告警规则，运行状态为【" + te.getStatus() + "】";
                try {
//                    sendEmail(emails, message);
                    emailSenderService.sendSampleMail(emails, "云平台任务告警信息", "分析任务告警", message);
                    emailAlertRecord(modelId, 1, message1);
                } catch (MessagingException e) {
                    emailAlertRecord(modelId, 1, "邮件发送失败");
                } catch (GeneralSecurityException e) {
                    emailAlertRecord(modelId, 1, "邮件发送失败");
                } catch (UnsupportedEncodingException e) {
                    emailAlertRecord(modelId, 1, "邮件发送失败");
                }
                break;
            }
        }
    }

    /**
     * coordinator任务邮件告警
     * @param cte
     * @param modelId
     */
    private void emailAlert(CoordTaskMonitorEntity cte, String modelId) {
        String name = iAnalysisModelFeign.getModelByModelId(modelId).getName();
        String status = cte.getStatus();
        List<String> users = iAnalysisModelFeign.getUserIdsByModelId(modelId);
        List<String> rules = iAnalysisModelFeign.getRuleIdsByModelId(modelId);
        String[] userIds = Joiner.on(";").skipNulls().join(users).split(";");
        String[] ruleIds = Joiner.on(";").skipNulls().join(rules).split(";");
        String[] emails = new String[userIds.length];

        for (int i = 0; i < userIds.length; i++) {
            emails[i] = iAnalysisModelFeign.getUserByUserId(userIds[i]).getEmail();
        }

        for (String ruleId : ruleIds) {
            if (iAnalysisModelFeign.getRuleByRuleId(ruleId).getStatus().equals(status)) {
                String log;
                try {
                    log = getOozieTaskLog(cte.getOozieTaskId());
                } catch (OozieClientException e) {
                    log = "获取任务日志失败";
                }

                String message = "任务【" + name + "】在 " + dateConvert(new Date()) +
                        " 触发告警规则，运行状态为【" + cte.getStatus() + "】，\r\n任务日志：\r\n" + log;
                String message1 = "任务【" + name + "】在 " + dateConvert(new Date()) +
                        " 触发告警规则，运行状态为【" + cte.getStatus() + "】";
                try {
//                    sendEmail(emails, message);
                    emailSenderService.sendSampleMail(emails, "云平台任务告警信息", "分析任务告警", message);
                    emailAlertRecord(modelId, 1, message1);
                } catch (MessagingException e) {
                    emailAlertRecord(modelId, 0, "邮件发送失败");
                    logger.info(Exceptions.getStackTraceAsString(e));
                } catch (GeneralSecurityException e) {
                    emailAlertRecord(modelId, 0, "邮件发送失败");
                } catch (UnsupportedEncodingException e) {
                    emailAlertRecord(modelId, 0, "邮件发送失败");
                }
                break;
            }
        }
    }

    /**
     * 告警信息记录
     * @param modelId
     * @param message
     */
    private void emailAlertRecord(String modelId, int isDispose, String message) {
        String[] split = message.split("\r\n");
        EmailAlertRecordEntity eare = new EmailAlertRecordEntity(modelId, 1,
                0, isDispose, split[0], dateConvert(new Date()), dateConvert(new Date()));
        iAnalysisModelFeign.insertEmailAlertRecordEntity(eare);
    }

    /**
     * 根据model任务下的所有oozie任务状态来更新其状态
     * @param modelId
     */
    private void updateModelStatus(String modelId) {
        Set<String> set = new HashSet<>();
        Map map = new HashMap();
        map.put("modelId", modelId);
        if (isWorkflowTsak(modelId)) {
            List<TaskMonitorEntity> tmes = (List<TaskMonitorEntity>)iAnalysisModelFeign.getOozieTaskInfo(modelId);
            for (TaskMonitorEntity tme : tmes) {
                set.add(tme.getStatus());
            }
            map.put("status", getModelStatusByTaskStatus(set));
        } else {
            List<CoordTaskMonitorEntity> ctmes = (List<CoordTaskMonitorEntity>)iAnalysisModelFeign.getOozieTaskInfo(modelId);
            for (CoordTaskMonitorEntity ctme : ctmes) {
                set.add(ctme.getStatus());
            }
            map.put("status", getModelStatusByCoordTaskStatus(set));
        }
        iAnalysisModelFeign.updateModelStatus(map);
    }

    private void yarnClientInit() {
        if (yarnClient == null) {
            synchronized (YarnClient.class) {
                if (yarnClient == null) {
                    yarnClient = YarnClient.createYarnClient();
                    Configuration conf = new Configuration();
                    Configuration yarnConf = new YarnConfiguration(conf);
                    yarnConf.set(YarnConfiguration.RM_ADDRESS, oozieConfig.getJobTracker());
                    yarnClient.init(yarnConf);
                    yarnClient.start();
                }
            }
        }
    }

    private void oozieClientInit() {
        if (oozieClient == null) {
            synchronized (YarnClient.class) {
                if (oozieClient == null) {
                    oozieClient = new OozieClient(oozieConfig.getOozieUrl());
                }
            }
        }
    }

    private String getModelStatusByTaskStatus(Set taskStatusSet) {
        String status;
        if (taskStatusSet.size() == 1 && taskStatusSet.contains("PREP")) {
            status = "未执行";
        } else if (taskStatusSet.size() > 0
                && !taskStatusSet.contains("PREP")
                && !taskStatusSet.contains("RUNNING")
                && !taskStatusSet.contains("SUSPENDED")) {
            status = "执行结束";
        } else {
            status = "执行中";
        }
        return status;
    }

    private String getModelStatusByCoordTaskStatus(Set taskStatusSet) {
        String status;
        if (taskStatusSet.size() == 1 && taskStatusSet.contains("PREP")) {
            status = "未执行";
        } else if (taskStatusSet.size() > 0
                && !taskStatusSet.contains("PREMATER")
                && !taskStatusSet.contains("PREP")
                && !taskStatusSet.contains("RUNNING")
                && !taskStatusSet.contains("SUSPENDED")
                && !taskStatusSet.contains("PAUSED")
                && !taskStatusSet.contains("PREPPAUSED")
                && !taskStatusSet.contains("PREPSUSPENDED")
                && !taskStatusSet.contains("RUNNINGWITHERROR")
                && !taskStatusSet.contains("SUSPENDEDWITHERROR")
                && !taskStatusSet.contains("PAUSEDWITHERROR")
                && !taskStatusSet.contains("DONEWITHERROR")
                && !taskStatusSet.contains("IGNORED")) {
            status = "执行结束";
        } else {
            status = "执行中";
        }
        return status;
    }

    private WorkflowAction getWorkflowAction(List<WorkflowAction> actions) {
        for (WorkflowAction action : actions) {
            if (action.getName().equalsIgnoreCase("analysis-task-action")) {
                return action;
            }
        }
        return null;
    }

    private ApplicationId getApplicationId(String jobId, int i) {
        String[] split = jobId.split("_");
        return ApplicationId.newInstance(Long.valueOf(split[1]), Integer.valueOf(split[2]) + i);
    }

    private ApplicationId getApplicationIdByConsoleUrl(WorkflowAction action, String name) throws IOException, YarnException {
        String consoleUrl = action.getConsoleUrl();
        String[] split = consoleUrl.split("/");
        String jobId = split[split.length - 1];
        for (int i = 1; i < 100; i++) {
            ApplicationId id = getApplicationId(jobId, i);
            if (yarnClient.getApplicationReport(id).getName().equals(name)) {
                return id;
            }
        }
        return null;
    }

    private ApplicationId getApplicationIdByConsoleUrl(CoordinatorAction action, String name) throws IOException, YarnException {
        String consoleUrl = action.getConsoleUrl();
        String[] split = consoleUrl.split("/");
        String jobId = split[split.length - 1];
        for (int i = 1; i < 100; i++) {
            ApplicationId id = getApplicationId(jobId, i);
            if (yarnClient.getApplicationReport(id).getName().equals(name)) {
                return id;
            }
        }
        return null;
    }

    private String dateConvert(Long date) {
        if (null == date) {
            return null;
        }
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    private String dateConvert(Date date) {
        if (null == date) {
            return null;
        }
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    private String getStatus(FinalApplicationStatus finalStatus) {
        switch (finalStatus) {
            case UNDEFINED:
                return "UNDEFINED";
            case SUCCEEDED:
                return "SUCCEEDED";
            case KILLED:
                return "KILLED";
            case FAILED:
                return "FAILED";
            default:
                return "UNDEFINED";
        }
    }

    private String getStatus(WorkflowJob.Status status) {
        switch (status) {
            case PREP:
                return "PREP";
            case RUNNING:
                return "RUNNING";
            case SUCCEEDED:
                return "SUCCEEDED";
            case KILLED:
                return "KILLED";
            case FAILED:
                return "FAILED";
            case SUSPENDED:
                return "SUSPENDED";
            default:
                return "UNDEFINED";
        }
    }

    private String getCoordStatus(Job.Status status) {
        switch (status) {
            case PREMATER:
                return "PREMATER";
            case PREP:
                return "PREP";
            case RUNNING:
                return "RUNNING";
            case SUSPENDED:
                return "SUSPENDED";
            case SUCCEEDED:
                return "SUCCEEDED";
            case KILLED:
                return "KILLED";
            case FAILED:
                return "FAILED";
            case PAUSED:
                return "PAUSED";
            case PREPPAUSED:
                return "PREPPAUSED";
            case PREPSUSPENDED:
                return "PREPSUSPENDED";
            case RUNNINGWITHERROR:
                return "RUNNINGWITHERROR";
            case SUSPENDEDWITHERROR:
                return "SUSPENDEDWITHERROR";
            case PAUSEDWITHERROR:
                return "PAUSEDWITHERROR";
            case DONEWITHERROR:
                return "DONEWITHERROR";
            case IGNORED:
                return "IGNORED";
            default:
                return "UNDEFINED";
        }
    }

    /**
     * 获取内存判断情况，集群内存是否足够执行此次任务
     * @param modelId
     * @return sucess:内存足够；fail:内存不足
     * @throws Exception
     */
    public Object getMemoryInfo(String modelId) throws Exception {
        try {
            Map map = getClusterMetrics();
            int availableMB = (int) Double.parseDouble(map.get("availableMB").toString());
            AnalysisModelEntity analysisModelEntity = iAnalysisModelFeign.getModelByModelId(modelId);
            String executorMemory = null;
            // 判断任务类型，是否属于第三方任务
            if (analysisModelEntity.getSparkOpts() != null && !analysisModelEntity.getSparkOpts().isEmpty()) {
                executorMemory = analysisModelEntity.getSparkOpts().split(" ")[3];
            } else {
                // 判断是流式任务还是离线任务
                if (analysisModelEntity.getBatchDurationSecond() != null && !analysisModelEntity.getBatchDurationSecond().isEmpty()) {
                    // 流式任务
                    executorMemory = oozieConfig.getStreamingSparkOpts().split(" ")[3];
                } else {
                    // 离线任务
                    executorMemory = oozieConfig.getOfflineSparkOpts().split(" ")[3];
                }
            }
            boolean isGB = executorMemory.toLowerCase().contains("g");
            int memory = Integer.parseInt(executorMemory.substring(0, executorMemory.length() - 1));
            if (isGB) {
                if ((availableMB / 1024 - memory) < 0) {
                    return "fail:Yarn内存不足，任务启动失败";
                }
            } else {
                if ((availableMB - memory) < 0) {
                    return "fail:Yarn内存不足，任务启动失败";
                }
            }
        } catch (Exception e) {
            logger.error("获取集群内存剩余容量出错");
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 通过restful接口查询集群信息
     *
     * @return 集群信息的Map
     * @throws Exception
     */
    public Map getClusterMetrics() throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(oozieConfig.getYarnRestUrl());
        Map map = null;
        HttpResponse response = httpClient.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String available = EntityUtils.toString(response.getEntity());
            map = new HashMap<String, String>();
            Gson gson = new Gson();
            map = gson.fromJson(available, map.getClass());
            String clusterMetrics = map.get("clusterMetrics").toString();
            map = gson.fromJson(clusterMetrics, map.getClass());
        }
        return map;
    }

    private Boolean isWorkflowTsak(String modelId) {
        AnalysisModelEntity modelEntity = iAnalysisModelFeign.getModelByModelId(modelId);
        if(modelEntity.getCronExp() == null || modelEntity.getCoordStart() == null || modelEntity.getCoordEnd() == null) {
            return true;
        }
        return false;
    }

}