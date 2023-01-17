package com.dfssi.dataplatform.ide.access.mvc.service.impl;

import com.dfssi.dataplatform.ide.access.mvc.dao.TaskAccessStepInfoDao;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessStepInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.service.ITaskAccessStepInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2018/9/17
 * @description
 */
@Service(value = "taskAccessStepInfoService")
public class TaskAccessStepInfoService implements ITaskAccessStepInfoService {
    @Autowired
    private TaskAccessStepInfoDao taskAccessStepInfoDao;

    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TaskAccessStepInfoEntity> findListByTaskId(String taskId) {
        return taskAccessStepInfoDao.findListByTaskId(taskId);
    }

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteByTaskId(String taskId) {
        return taskAccessStepInfoDao.deleteByTaskId(taskId);
    }

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteByTaskAccessStepInfoMap(Map map) {
        return taskAccessStepInfoDao.deleteByTaskAccessStepInfoMap(map);
    }

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertMutil(List<TaskAccessStepInfoEntity> taskAccessStepInfoEntitiesnew) {
        return taskAccessStepInfoDao.insertMutil(taskAccessStepInfoEntitiesnew);
    }

}