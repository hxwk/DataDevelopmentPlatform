package com.dfssi.dataplatform.manager.monitor.task.service;

import com.dfssi.dataplatform.manager.monitor.OozieConfig;
import com.dfssi.dataplatform.manager.monitor.task.dao.MonitorTaskAttrDao;
import com.dfssi.dataplatform.manager.monitor.task.dao.MonitorTaskDao;
import com.dfssi.dataplatform.manager.monitor.task.dao.MonitorTaskExecRecordDao;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskAttrEntity;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskEntity;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskExecRecordEntity;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service(value = "monitorTaskService")
public class MonitorTaskService extends AbstractService {

    @Autowired
    private OozieConfig oozieConfig;
    @Autowired
    private MonitorTaskDao monitorTaskDao;
    @Autowired
    private MonitorTaskAttrDao monitorTaskAttrDao;
    @Autowired
    private MonitorTaskExecRecordDao monitorTaskExecRecordDao;

    @Transactional(value = "monitor", readOnly = true)
    public List<MonitorTaskEntity> listAllMonitorTasks(int pageIdx, int pageSize, String name, Long startTime, Long
            endTime, String taskType) {
        Map<String, Object> params = new HashMap();
        params.put("taskType", taskType);
        params.put("name", name);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        return this.listAllMonitorTasks(pageIdx, pageSize, params);
    }

    @Transactional(value = "monitor", readOnly = true)
    public List<MonitorTaskEntity> listAllMonitorTasks(int pageIdx, int pageSize, Map params) {
        PageHelper.startPage(pageIdx, pageSize);
        List<MonitorTaskEntity> taskEntities = this.monitorTaskDao.listAllTasks(params);

        return taskEntities;
    }

    @Transactional(value = "monitor", readOnly = true)
    public List<MonitorTaskExecRecordEntity> getExecRecords(int pageIdx, int pageSize, String taskId, Long startTime,
                                                            Long endTime) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", taskId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        PageHelper.startPage(pageIdx, pageSize);

        return this.monitorTaskExecRecordDao.getExecRecordsByTaskId(params);
    }

    @Transactional(value = "monitor", readOnly = true)
    public MonitorTaskEntity getTask(String taskId) {
        return this.monitorTaskDao.getByTaskId(taskId);
    }

    @Transactional(value = "monitor", readOnly = false)
    public void deleteTask(String taskId) {
        this.monitorTaskAttrDao.deleteByTaskId(taskId);
        this.monitorTaskExecRecordDao.deleteByTaskId(taskId);
        this.monitorTaskDao.delete(taskId);
    }

    @Transactional(value = "monitor", readOnly = false)
    public void saveTask(MonitorTaskEntity taskEntity) {
        if (StringUtils.isBlank(taskEntity.getId())) {
            taskEntity.setId(taskEntity.buidId());
            this.monitorTaskDao.insert(taskEntity);
        } else {
            this.monitorTaskDao.update(taskEntity);
        }

        this.saveAttrs(taskEntity);
    }

    @Transactional(value = "monitor", readOnly = false)
    private void saveAttrs(MonitorTaskEntity taskEntity) {
        this.monitorTaskAttrDao.deleteByTaskId(taskEntity.getId());

        for (MonitorTaskAttrEntity mtae : taskEntity.getAttrs()) {
            mtae.setId(mtae.buidId());
            mtae.setTaskId(taskEntity.getId());
        }

        this.monitorTaskAttrDao.batchInsert(taskEntity.getAttrs());
    }

    @Transactional(value = "monitor", readOnly = false)
    public void deleteExecRecord(String recordId) {
        this.monitorTaskExecRecordDao.delete(recordId);
    }

    public void startTask(String taskId) throws OozieClientException {
        MonitorTaskEntity taskEntity = this.getTask(taskId);

        if (MonitorTaskEntity.TASK_TYPE_OOZIE.equalsIgnoreCase(taskEntity.getTaskType())) {
            this.startOozieTask(taskEntity);
        }
    }

    public boolean existActiveTask(String taskId) {
        return false;
    }

    public void startOozieTask(MonitorTaskEntity taskEntity) throws OozieClientException {
        OozieClient wc = new OozieClient(oozieConfig.getOozieUrl());

        Properties conf = wc.createConfiguration();
        conf.setProperty("nameNode", oozieConfig.getNameNode());
        conf.setProperty("master", "yarn-cluster");
        conf.setProperty("jobTracker", oozieConfig.getJobTracker());
        conf.setProperty("user.name", oozieConfig.getHdfsUserName());
        conf.setProperty("mapreduce.job.user.name", oozieConfig.getHdfsUserName());
        conf.setProperty("queueName", "default");
        conf.setProperty("oozie.use.system.libpath", "true");

        for (MonitorTaskAttrEntity mtae : taskEntity.getAttrs()) {
            conf.setProperty(mtae.getName(), mtae.getValue());
        }

        String oozieJobId = wc.run(conf);
        addTaskExecRecord(taskEntity.getId(), oozieJobId);
    }

    @Transactional(value = "monitor", readOnly = false)
    public void addTaskExecRecord(String modelId, String oozieJobId) {
        MonitorTaskExecRecordEntity mtere = new MonitorTaskExecRecordEntity();
        mtere.setId(mtere.buidId());
        mtere.setTaskId(modelId);
        mtere.setExecAppId(oozieJobId);
        mtere.setStatus("START");

        monitorTaskExecRecordDao.insert(mtere);
    }

}
