package com.dfssi.dataplatform.devmanager.analysistask.mvc.service;

import com.alibaba.druid.util.StringUtils;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.dao.AnalysisApplicationFormDao;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.dao.ModelTaskDao;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisApplicationFormEntity;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.devmanager.analysistask.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "analysisTaskManagerService")
@Transactional(readOnly = false)
public class AnalysisManagerTaskService implements IAnalysisManagerTaskService {

    private static Logger logger = Logger.getLogger(AnalysisManagerTaskService.class);

    @Autowired
    private ModelTaskDao modelTaskDao;

    @Autowired
    private AnalysisApplicationFormDao analysisApplicationFormDao;


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<AnalysisModelEntity> findEntityList(AnalysisModelEntity entity) {
        return modelTaskDao.listAllModels(entity);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Map<String, Object> verifyTask(AnalysisApplicationFormEntity entity) {
        Map<String, Object> result = new HashMap<>();

        int status = Constants.REQUEST_STATUS_FAIL;

        if (null == entity || StringUtils.isEmpty(entity.getDataresId())) {
            String message = "请求参数不合格";
            logger.error(message);
            result.put(Constants.KEY_RESULT_MESSAGE, message);
            result.put(Constants.KEY_RESULT_STATUS, status);

            return result;
        }

        try {
            analysisApplicationFormDao.insert(entity);

            status = Constants.REQUEST_STATUS_SUCCESS;
            String message = "请求成功";
            result.put(Constants.KEY_RESULT_MESSAGE, message);
        } catch (Exception e) {
            logger.error(null, e);
        }

        result.put(Constants.KEY_RESULT_STATUS, status);

        return result;
    }

    @Override
    public Map<String, Object> getApplicationformById(String id) {

        Map<String, Object> result = new HashMap<>();

        int status = Constants.REQUEST_STATUS_FAIL;

        if (StringUtils.isEmpty(id)) {
            String message = "请求参数不合格";
            logger.error(message);
            result.put(Constants.KEY_RESULT_MESSAGE, message);
            result.put(Constants.KEY_RESULT_STATUS, status);

            return result;
        }

        try {
            List<AnalysisApplicationFormEntity> list = analysisApplicationFormDao.getById(id);

            if (null !=  list && list.size() > 0) {
                result.put(Constants.KEY_RESULT_DATA, analysisApplicationFormDao.getById(id));
            }

            status = Constants.REQUEST_STATUS_SUCCESS;
            String message = "请求成功";
            result.put(Constants.KEY_RESULT_MESSAGE, message);
        } catch (Exception e) {
            logger.error(null, e);
        }

        result.put(Constants.KEY_RESULT_STATUS, status);

        return result;
    }
}
