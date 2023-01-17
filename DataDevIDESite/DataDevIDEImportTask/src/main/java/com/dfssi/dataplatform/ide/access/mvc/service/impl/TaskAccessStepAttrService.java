package com.dfssi.dataplatform.ide.access.mvc.service.impl;

import com.dfssi.dataplatform.ide.access.mvc.dao.TaskAccessStepAttrDao;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepAttrEntity;
import com.dfssi.dataplatform.ide.access.mvc.service.ITaskAccessStepAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description
 */
@Service(value = "taskAccessStepAttrService")
public class TaskAccessStepAttrService implements ITaskAccessStepAttrService {

    @Autowired
    private TaskAccessStepAttrDao taskAccessStepAttrDao;

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteByTaskId(String taskId) {
        return taskAccessStepAttrDao.deleteByTaskId(taskId);
    }

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertMutil(List<TaskAccessStepAttrEntity> taskAccessStepAttrEntitiesnew) {
        return taskAccessStepAttrDao.insertMutil(taskAccessStepAttrEntitiesnew);
    }

    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TaskAccessStepAttrEntity> findListByTaskId(String taskId) {
        return taskAccessStepAttrDao.findListByTaskId(taskId);
    }

    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TaskAccessStepAttrEntity> findListByTaskIdandStepId(String taskId, String stepId) {
        return taskAccessStepAttrDao.findListByTaskIdandStepId(taskId,stepId);
    }


}