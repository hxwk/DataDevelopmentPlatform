package com.dfssi.dataplatform.ide.access.mvc.service.impl;

import com.dfssi.dataplatform.ide.access.mvc.dao.TaskAccessLinkInfoDao;
import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessLinkInfoEntity;
import com.dfssi.dataplatform.ide.access.mvc.service.ITaskAccessLinkInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description
 */
@Service(value = "taskAccessLinkInfoService")
public class TaskAccessLinkInfoService implements ITaskAccessLinkInfoService {

    @Autowired
    private TaskAccessLinkInfoDao taskAccessLinkInfoDao;

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int deleteByTaskId(String taskId) {
        return taskAccessLinkInfoDao.deleteByTaskId(taskId);
    }

    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertMutil(List<TaskAccessLinkInfoEntity> taskAccessLinkInfoEntitiesnew) {
        return taskAccessLinkInfoDao.insertMutil(taskAccessLinkInfoEntitiesnew);
    }

    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TaskAccessLinkInfoEntity> findListByTaskId(String taskId) {
        return taskAccessLinkInfoDao.findListByTaskId(taskId);
    }


}