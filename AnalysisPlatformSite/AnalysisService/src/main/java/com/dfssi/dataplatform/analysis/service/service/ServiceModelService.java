package com.dfssi.dataplatform.analysis.service.service;

import com.dfssi.dataplatform.analysis.common.util.DateUtils;
import com.dfssi.dataplatform.analysis.common.util.JacksonUtils;
import com.dfssi.dataplatform.analysis.service.entity.ResourceConfEntity;
import com.dfssi.dataplatform.analysis.service.entity.ServiceModelEntity;
import com.dfssi.dataplatform.analysis.service.entity.ResourceEntity;
import com.dfssi.dataplatform.analysis.service.entity.ServiceStepEntity;
import com.dfssi.dataplatform.analysis.service.mapper.ServiceModelDao;
import com.dfssi.dataplatform.analysis.service.mapper.ResourceConfDao;
import com.dfssi.dataplatform.analysis.service.mapper.ResourceDao;
import com.dfssi.dataplatform.analysis.service.mapper.ServiceStepDao;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/11 16:57
 */
@Service(value = "ServiceModelService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ServiceModelService extends AbstractService {

    private static volatile long index = 0;

    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private ResourceConfDao resourceConfDao;
    @Autowired
    private ServiceStepDao serviceStepDao;
    @Autowired
    private ServiceModelDao serviceModelDao;

    public PageServiceLeftMenuItems getPageMenuItems() {
        PageServiceLeftMenuItems leftMenuItems = new PageServiceLeftMenuItems();

        List<ResourceEntity> dataResources = resourceDao.listAllSources();
        for (ResourceEntity rce : dataResources) {
            leftMenuItems.addDataResourceItem(rce);
        }

        List<ServiceStepEntity> list = serviceStepDao.getPageSteps();
        for (ServiceStepEntity sse : list) {
            leftMenuItems.addBICompItem(sse);
        }

        return leftMenuItems;
    }

    public DataServiceLeftMenuItems getDataMenuItems() {
        DataServiceLeftMenuItems leftMenuItems = new DataServiceLeftMenuItems();

        List<ResourceEntity> dataResources = resourceDao.listAllSources();
        for (ResourceEntity drce : dataResources) {
            leftMenuItems.addDataResourceItem(drce);
        }

        List<ServiceStepEntity> list = serviceStepDao.getDataSteps();
        for (ServiceStepEntity sse : list) {
            leftMenuItems.addBICompItem(sse);
        }

        return leftMenuItems;
    }

    public List<ServiceModelEntity> listModels(int pageIdx, int pageSize, String modelName, String modelType,
                                               Long startTime, Long endTime, String status, String field, String orderType) {
        Map<String, Object> params = new HashMap<>();
        params.put("modelName", modelName);
        params.put("modelType", modelType);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("status", status);
        params.put("field", field);
        params.put("orderType", orderType);

        PageHelper.startPage(pageIdx, pageSize);
        return serviceModelDao.listModels(params);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveModel(String modelJson) throws Exception {
        JsonObject jsonObject = new JsonParser().parse(modelJson).getAsJsonObject();
//        isDagModel(jsonObject);

        ServiceModelEntity serviceModelEntity = new ServiceModelEntity(
                JacksonUtils.getAsString(jsonObject, "modelId"),
                JacksonUtils.getAsString(jsonObject, "name"),
                JacksonUtils.getAsString(jsonObject, "modelType"),
                JacksonUtils.getAsString(jsonObject, "description"),
                modelJson,
                JacksonUtils.getAsString(jsonObject, "status"),
                JacksonUtils.getAsString(jsonObject, "createUser"),
                DateUtils.getNowDate(),
                JacksonUtils.getAsString(jsonObject, "updateUser"),
                null,
                1
        );
        serviceModelDao.insert(serviceModelEntity);
    }

    public String getServiceModel(String modelId) {
        return serviceModelDao.getModelConf(modelId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteServiceModel(String modelId) {
        serviceModelDao.delete(modelId);
    }

    public String generateNewId() {
        index = (index == 999999) ? 0 : index + 1;
        String indexStr = StringUtils.right("000000" + index, 6);
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + indexStr;
    }

    public List<Map<String, String>> getResourceConf(String resourceId) {
        List<ResourceConfEntity> confEntities = resourceConfDao.getResourceConf(resourceId);
        List<Map<String, String>> confs = new ArrayList<>();
        for (ResourceConfEntity confEntity : confEntities) {
            Map<String, String> conf = new HashMap<>();
            conf .put("paramName", confEntity.getParamName());
            conf.put("paramValue", confEntity.getParamValue());
            confs.add(conf);
        }
        return confs;
    }

    private void isDagModel(JsonObject jsonObject) throws Exception {
        JsonArray steps = JacksonUtils.getAsJsonArray(jsonObject, "steps");
        JsonArray links = JacksonUtils.getAsJsonArray(jsonObject, "links");
        List<JsonElement> list = new ArrayList<>();
        Map<String, String> map = new LinkedHashMap<>();

        for (JsonElement step : steps) {
            list.add(step);
        }
        for (JsonElement link : links) {
            map.put(JacksonUtils.getAsString(link.getAsJsonObject(), "modelStepFrom").substring(0, 20),
                    JacksonUtils.getAsString(link.getAsJsonObject(), "modelStepTo").substring(0, 20));
        }

        int size = steps.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String id = JacksonUtils.getAsString(steps.get(j).getAsJsonObject(), "id");
                if (!map.containsValue(id)) {
                    list.remove(steps.get(j));
                    map.remove(id);
                }
            }
        }
        if (list.size() != 0) {
            throw new Exception("任务不是一个Dag");
        }
    }
}
