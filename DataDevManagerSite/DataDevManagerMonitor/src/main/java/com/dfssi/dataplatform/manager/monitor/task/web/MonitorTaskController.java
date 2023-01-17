package com.dfssi.dataplatform.manager.monitor.task.web;

import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskEntity;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskExecRecordEntity;
import com.dfssi.dataplatform.manager.monitor.task.service.MonitorTaskService;
import com.dfssi.dataplatform.manager.monitor.task.utils.Exceptions;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "manager/monitor/task")
public class MonitorTaskController extends AbstractController {

    private final static String LOG_TAG_TASK_MONITOR = "[Monitor Task]";

    @Autowired
    private MonitorTaskService monitorTaskService;

    @ResponseBody
    @RequestMapping(value = "listtasks/{pageIdx}/{pageSize}")
    public Object listAllTasks(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx") int
            pageIdx, @PathVariable("pageSize") int pageSize, String name, Long startTime, Long endTime, String
            taskType) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "List all tasks.");

            List<MonitorTaskEntity> modelEntities = this.monitorTaskService.listAllMonitorTasks(pageIdx, pageSize,
                    name, startTime, endTime, taskType);
            long total = ((Page) modelEntities).getTotal();
            responseObj.setTotal(total);
            responseObj.setData(modelEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all tasks.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to list all tasks.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "listrecords/{pageIdx}/{pageSize}")
    public Object listAllExecRecords(HttpServletRequest req, HttpServletResponse response, @PathVariable("pageIdx")
            int pageIdx, @PathVariable("pageSize") int pageSize, String taskId, Long startTime, Long endTime) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "List all execute records.");

            List<MonitorTaskExecRecordEntity> execRecordEntities = this.monitorTaskService.getExecRecords(pageIdx,
                    pageSize, taskId, startTime, endTime);
            long total = ((Page) execRecordEntities).getTotal();
            responseObj.setTotal(total);
            responseObj.setData(execRecordEntities);
            responseObj.buildSuccessMsg(ResponseObj.MSG_SUCCESS_QUERY_SUCCESS);
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to list all execute records.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to list all execute records.\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "get/{taskId}", method = RequestMethod.GET)
    public Object getTask(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            taskId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "Get task. taskId=" + taskId);

            MonitorTaskEntity taskEntity = monitorTaskService.getTask(taskId);
            if (taskEntity == null) {
                responseObj.buildFailMsg(ResponseObj.CODE_CONTENT_NOT_EXIST, "Task does not exists.", null);
            } else {
                responseObj.buildSuccessMsg("Get task successfully.");
                responseObj.setData(taskEntity);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get task. taskId=" + taskId + "\n", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to get task. taskId=" + taskId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "delete/{taskId}", method = RequestMethod.GET)
    public Object deleteTask(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            taskId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "Delete task. taskId=" + taskId);

            this.monitorTaskService.deleteTask(taskId);

            responseObj.buildSuccessMsg("Delete task successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to delete task. taskId=" + taskId + "\n",
                    Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to delete task. taskId=" + taskId + "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "deleterec/{execRecordId}", method = RequestMethod.GET)
    public Object deleteExecRecord(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable
            String execRecordId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "Delete execute record. execRecordId=" + execRecordId);

            this.monitorTaskService.deleteExecRecord(execRecordId);

            responseObj.buildSuccessMsg("Delete execute record successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to delete execute record. execRecordId=" +
                    execRecordId + "\n", Exceptions.getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to delete execute record. execRecordId=" + execRecordId +
                    "\n", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Object saveModel(HttpServletRequest req, HttpServletResponse response, Model model, @RequestBody
            MonitorTaskEntity taskEntity) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            if (taskEntity == null) {
                responseObj.buildFailMsg(ResponseObj.CODE_CONTENT_NOT_EXIST, "Task does not exists.", null);
            } else {
                this.monitorTaskService.saveTask(taskEntity);
                responseObj.buildSuccessMsg("Save task successfully.");
                logger.info(LOG_TAG_TASK_MONITOR + "Save task. " + taskEntity.getId());
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to save task.", Exceptions.getStackTraceAsString
                    (t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to save task.", t);
        }

        return responseObj;
    }

    @ResponseBody
    @RequestMapping(value = "starttask/{taskId}")
    public Object startOozie(HttpServletRequest req, HttpServletResponse response, Model model, @PathVariable String
            taskId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            logger.info(LOG_TAG_TASK_MONITOR + "Startup task. taskId=" + taskId);

            monitorTaskService.startTask(taskId);
            responseObj.buildSuccessMsg("Startup task successfully.");
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to startup task.", Exceptions
                    .getStackTraceAsString(t));

            logger.error(LOG_TAG_TASK_MONITOR + "Failed to startup task. taskId=" + taskId + "\n", t);
        }

        return responseObj;
    }
}
