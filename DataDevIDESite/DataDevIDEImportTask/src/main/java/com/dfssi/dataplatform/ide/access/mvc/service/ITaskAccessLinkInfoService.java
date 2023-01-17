package com.dfssi.dataplatform.ide.access.mvc.service;

import com.dfssi.dataplatform.ide.access.mvc.entity.TaskAccessLinkInfoEntity;

import java.util.List;

/**
 * @author
 * @date 2018/9/17
 * @description 接入任务连线信息增删改查模块
 */
public interface ITaskAccessLinkInfoService {
    /**
     * 邏輯刪除接入任务连接信息表
     * @param taskId
     * @return
     */
    int deleteByTaskId(String taskId);

    /**
     * 插入接入任务连接信息表
     * @param taskAccessLinkInfoEntitiesnew
     * @return
     */
    int insertMutil(List<TaskAccessLinkInfoEntity> taskAccessLinkInfoEntitiesnew);

    /**
     * 根据任务id查询接入任务连接信息表
     * @param taskId
     * @return
     */
    List<TaskAccessLinkInfoEntity> findListByTaskId(String taskId);

}