package com.dfssi.dataplatform.ide.access.mvc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.access.mvc.constants.Constants;
import com.dfssi.dataplatform.ide.access.mvc.dao.TaskAccessInfoDao;
import com.dfssi.dataplatform.ide.access.mvc.entity.*;
import com.dfssi.dataplatform.ide.access.mvc.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 接入任务service
 */
@Service(value = "taskService")
public class TaskService implements ITaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskAccessInfoDao taskAccessInfoDao;

    @Autowired
    private ITaskAccessStepInfoService taskAccessStepInfoService;

    @Autowired
    private ITaskAccessStepAttrService taskAccessStepAttrService;

    @Autowired
    private ITaskAccessLinkInfoService taskAccessLinkInfoService;

    @Autowired
    RestTemplate resttemplate;

    @Autowired
    private IFeignService feignService;

    @Value("${access.service.url}")
    private String accessServiceUrl;

    @Override
    //@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResult<TaskAccessInfoEntity> findeEntityList(TaskAccessInfoEntity entity, PageParam pageParam) {
        Page<TaskAccessInfoEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<TaskAccessInfoEntity> list = taskAccessInfoDao.findList(entity);
        PageResult<TaskAccessInfoEntity> pageResult = new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 保存接入任务信息
     * @param taskModel
     * @return
     */
    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object saveTaskModel (TaskModelEntity taskModel) {
        ResponseObjectEntity responseObject = new ResponseObjectEntity();
        responseObject.setStatusCode("1");
        //检查参数是否正确传输
        if (null == taskModel.getTaskAccessInfoEntity() || null == taskModel.getTaskAccessStepInfoEntities()
                || null == taskModel.getTaskAccessLinkInfoEntities() || taskModel.getTaskAccessStepInfoEntities().size() <= 0
                || taskModel.getTaskAccessLinkInfoEntities().size() <= 0) {
            String message = "任务实体不能为空";
            responseObject.setMessage(message);
            responseObject.setFlag(false);
            return responseObject;
        }
        for (TaskAccessStepInfoEntity taskAccessStepInfoEntity : taskModel.getTaskAccessStepInfoEntities()) {
            if (null == taskAccessStepInfoEntity.getTaskAccessStepAttrEntities()) {
                String message = "任务实体不能为空";
                responseObject.setMessage(message);
                responseObject.setFlag(false);
                return responseObject;
            }
        }
        TaskAccessInfoEntity taskAccessInfoEntity = taskModel.getTaskAccessInfoEntity();
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        if (StringUtils.isNotBlank(taskAccessInfoEntity.getId())) {
             /*修改任务*/
            List<TaskAccessInfoEntity> list = taskAccessInfoDao.queryRepeatName(taskAccessInfoEntity);
            if(list.size() > 0){
                responseObject.setMessage("任务名称不能重复");
                responseObject.setFlag(false);
                return responseObject;
            }
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s=df.format(day);
            taskAccessInfoEntity.setCreateUser("angel");
            taskAccessInfoEntity.setUpdateDate(s);
            taskAccessInfoEntity.setUpdateUser("angel");
            taskAccessInfoEntity.setTaskStartTime(s);
            taskAccessInfoEntity.setTaskEndTime(s);
            taskAccessInfoDao.update(taskAccessInfoEntity);
            taskId = taskAccessInfoEntity.getId();
            //批量插入接入步骤信息
            List<TaskAccessStepInfoEntity> taskAccessStepInfoEntities = taskModel.getTaskAccessStepInfoEntities();
            //逻辑删除
            taskAccessStepAttrService.deleteByTaskId(taskId);
            taskAccessLinkInfoService.deleteByTaskId(taskId);
            List<TaskAccessStepInfoEntity> queryTaskAccessStepInfoEntities = taskAccessStepInfoService.findListByTaskId(taskId);
            Map map = new HashMap();
            for(TaskAccessStepInfoEntity taskaccessstepinfoentitydelete:queryTaskAccessStepInfoEntities){
                //为了保留原始数据，采用逻辑删除，为保证主键不冲突采用生成新的id作为原始数据的主键id，因为数据有关联性，所以采用此办法
                String oldId = taskaccessstepinfoentitydelete.getId();
                map.put("oldId",oldId);
                String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                map.put("newId",uid);
                map.put("taskId",taskId);
                //逻辑删除
                taskAccessStepInfoService.deleteByTaskAccessStepInfoMap(map);
            }
            List<TaskAccessStepInfoEntity> taskAccessStepInfoEntitiesnew=new ArrayList<TaskAccessStepInfoEntity>();
            for(TaskAccessStepInfoEntity taskaccessstepinfoentity:taskAccessStepInfoEntities){
                //主键id为页面传递过来，因为页面在生成连线信息时是根据主键id关联的，所以这里新增的时候不采用生成新id的方法
                //此主键id为原始数据id，固逻辑删除时需要生成新的id，保证主键不冲突
                taskaccessstepinfoentity.setCreateDate(s);
                taskaccessstepinfoentity.setCreateUser("angel");
                taskaccessstepinfoentity.setUpdateDate(s);
                taskaccessstepinfoentity.setUpdateUser("angel");
                taskaccessstepinfoentity.setTaskId(taskId);
                taskAccessStepInfoEntitiesnew.add(taskaccessstepinfoentity);
            }
            taskAccessStepInfoService.insertMutil(taskAccessStepInfoEntitiesnew);
            List<TaskAccessStepAttrEntity> taskAccessStepAttrEntitiesnew = new ArrayList<>();
            for (TaskAccessStepInfoEntity taskAccessStepInfoEntity : taskAccessStepInfoEntities) {
                List<TaskAccessStepAttrEntity> taskAccessStepAttrEntities1 = taskAccessStepInfoEntity.getTaskAccessStepAttrEntities();
                for(TaskAccessStepAttrEntity taskaccessstepattrentity:taskAccessStepAttrEntities1){
                    String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();;
                    taskaccessstepattrentity.setId(id);
                    taskaccessstepattrentity.setCreateDate(s);
                    taskaccessstepattrentity.setCreateUser("angel");
                    taskaccessstepattrentity.setUpdateDate(s);
                    taskaccessstepattrentity.setUpdateUser("angel");
                    taskaccessstepattrentity.setTaskId(taskId);
                    taskAccessStepAttrEntitiesnew.add(taskaccessstepattrentity);
                }
            }
            if(taskAccessStepAttrEntitiesnew.size()>0){
                taskAccessStepAttrService.insertMutil(taskAccessStepAttrEntitiesnew);
            }
            //批量插入连线信息
            List<TaskAccessLinkInfoEntity>  taskAccessLinkInfoEntitiesnew=new ArrayList<TaskAccessLinkInfoEntity>();
            List<TaskAccessLinkInfoEntity>  taskAccessLinkInfoEntities=taskModel.getTaskAccessLinkInfoEntities();
            for(TaskAccessLinkInfoEntity taskaccesslinkinfoentity:taskAccessLinkInfoEntities){
                String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();;
                taskaccesslinkinfoentity.setId(id);
                taskaccesslinkinfoentity.setCreateDate(s);
                taskaccesslinkinfoentity.setCreateUser("angel");
                taskaccesslinkinfoentity.setUpdateDate(s);
                taskaccesslinkinfoentity.setUpdateUser("angel");
                taskaccesslinkinfoentity.setTaskId(taskId);
                taskAccessLinkInfoEntitiesnew.add(taskaccesslinkinfoentity);
            }
            taskAccessLinkInfoService.insertMutil(taskAccessLinkInfoEntitiesnew);
        }
        else {
            //新增
            List<TaskAccessInfoEntity> list = taskAccessInfoDao.queryRepeatName(taskAccessInfoEntity);
            if(list.size() > 0){
                responseObject.setMessage("任务名称不能重复");
                responseObject.setFlag(false);
                return responseObject;
            }
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = df.format(day);
            taskAccessInfoEntity.setCreateDate(s);
            taskAccessInfoEntity.setCreateUser("angel");
            taskAccessInfoEntity.setUpdateDate(s);
            taskAccessInfoEntity.setUpdateUser("angel");
            taskAccessInfoEntity.setTaskStartTime(s);
            taskAccessInfoEntity.setTaskEndTime(s);
            taskAccessInfoEntity.setId(taskId);
            /*新增任务*/
            taskAccessInfoDao.insert(taskAccessInfoEntity);

            //批量插入接入步骤信息
            List<TaskAccessStepInfoEntity> taskAccessStepInfoEntities = taskModel.getTaskAccessStepInfoEntities();
            List<TaskAccessStepInfoEntity> taskAccessStepInfoEntitiesnew=new ArrayList<TaskAccessStepInfoEntity>();
            for(TaskAccessStepInfoEntity taskaccessstepinfoentity:taskAccessStepInfoEntities){
                //这里没有生成主键id是因为页面调用生成主键id的方法后提交过来有
                taskaccessstepinfoentity.setCreateDate(s);
                taskaccessstepinfoentity.setCreateUser("angel");
                taskaccessstepinfoentity.setUpdateDate(s);
                taskaccessstepinfoentity.setUpdateUser("angel");
                taskaccessstepinfoentity.setTaskId(taskId);
                taskAccessStepInfoEntitiesnew.add(taskaccessstepinfoentity);
            }
            taskAccessStepInfoService.insertMutil(taskAccessStepInfoEntitiesnew);

            //批量插入接入步骤属性信息
            List<TaskAccessStepAttrEntity> taskAccessStepAttrEntitiesnew = new ArrayList<>();
            for (TaskAccessStepInfoEntity taskAccessStepInfoEntity : taskAccessStepInfoEntities) {
                List<TaskAccessStepAttrEntity> taskAccessStepAttrEntities = taskAccessStepInfoEntity.getTaskAccessStepAttrEntities();
                for(TaskAccessStepAttrEntity taskaccessstepattrentity:taskAccessStepAttrEntities){
                    String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();;
                    taskaccessstepattrentity.setId(id);
                    taskaccessstepattrentity.setCreateDate(s);
                    taskaccessstepattrentity.setCreateUser("angel");
                    taskaccessstepattrentity.setUpdateDate(s);
                    taskaccessstepattrentity.setUpdateUser("angel");
                    taskaccessstepattrentity.setTaskId(taskId);
                    taskAccessStepAttrEntitiesnew.add(taskaccessstepattrentity);
                }
            }
            if(taskAccessStepAttrEntitiesnew.size()>0){
                taskAccessStepAttrService.insertMutil(taskAccessStepAttrEntitiesnew);
            }
            //批量插入步骤连线信息
            List<TaskAccessLinkInfoEntity>  TaskAccessLinkInfoEntitiesnew=new ArrayList<TaskAccessLinkInfoEntity>();
            List<TaskAccessLinkInfoEntity>  TaskAccessLinkInfoEntities=taskModel.getTaskAccessLinkInfoEntities();
            for(TaskAccessLinkInfoEntity taskaccesslinkinfoentity:TaskAccessLinkInfoEntities){
                String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();;
                taskaccesslinkinfoentity.setId(id);
                taskaccesslinkinfoentity.setCreateDate(s);
                taskaccesslinkinfoentity.setCreateUser("angel");
                taskaccesslinkinfoentity.setUpdateDate(s);
                taskaccesslinkinfoentity.setUpdateUser("angel");
                taskaccesslinkinfoentity.setTaskId(taskId);
                TaskAccessLinkInfoEntitiesnew.add(taskaccesslinkinfoentity);
            }
            taskAccessLinkInfoService.insertMutil(TaskAccessLinkInfoEntitiesnew);
        }
        responseObject.setMessage("接入任务信息保存成功");
        responseObject.setFlag(true);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("taskId", taskId);
        responseObject.setData(dataMap);
        return responseObject;
    }

    /**
     * 根据任务实体获取任务详情
     * @param taskAccessInfoEntity
     * @return
     */
    @Override
    public Object getTaskModel (TaskAccessInfoEntity taskAccessInfoEntity) {
        ResponseObjectEntity responseObject = new ResponseObjectEntity();
        responseObject.setStatusCode("1");
        //检查参数是否正确传输
        if (null == taskAccessInfoEntity || StringUtils.isBlank(taskAccessInfoEntity.getId())) {
            String message = "taskId不能为空";
            responseObject.setMessage(message);
            responseObject.setFlag(false);
            return responseObject;
        }
        String taskId = taskAccessInfoEntity.getId();
        TaskModelEntity taskModel = new TaskModelEntity();
        taskModel.setTaskAccessInfoEntity(taskAccessInfoDao.get(taskId));
        List<TaskAccessStepInfoEntity> taskAccessStepInfoEntities = taskAccessStepInfoService.findListByTaskId(taskId);
        taskModel.setTaskAccessLinkInfoEntities(taskAccessLinkInfoService.findListByTaskId(taskId));
        //步骤属性只从数据库查询一次，用代码逻辑去拼接，提高效率
        List<TaskAccessStepAttrEntity> taskAccessStepAttrEntities = taskAccessStepAttrService.findListByTaskId(taskId);
        if (null != taskAccessStepAttrEntities && taskAccessStepAttrEntities.size() > 0) {
            for (TaskAccessStepAttrEntity taskAccessStepAttrEntity : taskAccessStepAttrEntities) {
                for (int i = 0; i < taskAccessStepInfoEntities.size(); i++) {
                    if (taskAccessStepAttrEntity.getStepId().equals(taskAccessStepInfoEntities.get(i).getId())) {
                        if (null == taskAccessStepInfoEntities.get(i).getTaskAccessStepAttrEntities()) {
                            List<TaskAccessStepAttrEntity> stepAttrList = new ArrayList<>();
                            stepAttrList.add(taskAccessStepAttrEntity);
                            taskAccessStepInfoEntities.get(i).setTaskAccessStepAttrEntities(stepAttrList);
                        } else {
                            taskAccessStepInfoEntities.get(i).getTaskAccessStepAttrEntities().add(taskAccessStepAttrEntity);
                        }
                        break;
                    }
                }
            }
        }
        taskModel.setTaskAccessStepInfoEntities(taskAccessStepInfoEntities);
        responseObject.setMessage("接入任务信息获取成功");
        responseObject.setFlag(true);
        responseObject.setData(taskModel);
        return responseObject;
    }

    /**
     * 根据任务id获取任务详情
     * @param taskId
     * @return
     */
    @Override
    public Object getDetailByTaskId (String taskId) {
        ResponseObjectEntity responseObject = new ResponseObjectEntity();
        responseObject.setStatusCode("1");
        //检查参数是否正确传输
        if (StringUtils.isBlank(taskId)) {
            String message = "taskId不能为空";
            responseObject.setMessage(message);
            responseObject.setFlag(false);
            return responseObject;
        }
        TaskAccessInfoEntity taskAccessInfoEntity= taskAccessInfoDao.get(taskId);
        String status = taskAccessInfoEntity.getStatus();
        Map<String, String> map = getMessageByTaskId(taskId);
        String masterUrl = getMasterUrl(map.get("dataSourceId"));//根据数据源id获取masterUrl
        JSONObject jobj = getAliveClientList(masterUrl);//获取存活的客户端
        List<String> clientList = null;
        if(null != jobj){
            clientList = (List<String>) jobj.get("list");
        }
        List<TaskAccessInfoEntity> returnEntitys = new ArrayList<>();
        List list = new ArrayList();
        if(taskAccessInfoEntity.getClientIds().contains(",")){
            String clientId[] = taskAccessInfoEntity.getClientIds().split(",");
            for(String cId:clientId){
                list.add(cId);
            }
        }else{
            String clientId = taskAccessInfoEntity.getClientIds();
            list.add(clientId);
        }
        for(int i = 0; i<list.size(); i++){
            TaskAccessInfoEntity entity =new TaskAccessInfoEntity();
            //存活，
            if(null != clientList && clientList.contains(list.get(i))){
                entity.setClientIds(list.get(i).toString());
                entity.setStatus(status);
            }else{
                entity.setClientIds(list.get(i).toString());
                entity.setStatus("0");
            }
            entity=InitValue(entity,taskAccessInfoEntity);
            returnEntitys.add(entity);
        }
        responseObject.setMessage("接入任务信息获取成功");
        responseObject.setTotal(String.valueOf(returnEntitys.size()));
        responseObject.setFlag(true);
        responseObject.setData(returnEntitys);
        return responseObject;
    }

    public TaskAccessInfoEntity InitValue(TaskAccessInfoEntity entity, TaskAccessInfoEntity taskaccessinfoentity){
        entity.setId(taskaccessinfoentity.getId());
        entity.setTaskName(taskaccessinfoentity.getTaskName());
        entity.setTaskDescription(taskaccessinfoentity.getTaskDescription());
        entity.setApproveStatus(taskaccessinfoentity.getApproveStatus());
        entity.setTaskStartTime(taskaccessinfoentity.getTaskStartTime());
        entity.setTaskEndTime(taskaccessinfoentity.getTaskEndTime());
        entity.setTaskFrequency(taskaccessinfoentity.getTaskFrequency());
        entity.setTaskInterval(taskaccessinfoentity.getTaskInterval());
        entity.setIsDeleted(taskaccessinfoentity.getIsDeleted());
        entity.setCreateDate(taskaccessinfoentity.getCreateDate());
        entity.setCreateUser(taskaccessinfoentity.getCreateUser());
        entity.setUpdateDate(taskaccessinfoentity.getUpdateDate());
        entity.setUpdateUser(taskaccessinfoentity.getUpdateUser());
        return entity;
    }

    /**
     *查询存活的客户端
     * @param
     * @return
     */
    public JSONObject getAliveClientList(String masterUrl){
        String serverUrl = masterUrl+"/service/clientList";
        JSONObject json= resttemplate.postForObject(serverUrl,null,JSONObject.class);
        return json;
    }

     /*根据taskId删除单条任务记录*/
    public Object deleteTaskSingleByTaskId(String taskId){
        int i=taskAccessInfoDao.delete(taskId);
        ResponseObjectEntity responseObject = new ResponseObjectEntity();
        if(i>0){
            responseObject.setStatusCode("1");
            responseObject.setMessage("单条接入任务信息删除成功");
            responseObject.setFlag(true);
            responseObject.setData(i);
        }
        else{
            responseObject.setStatusCode("0");
            responseObject.setMessage("单条接入任务信息删除失败");
            responseObject.setFlag(false);
            responseObject.setData(i);
        }
        return responseObject;
    }

//    /**
//     * 启动或停止任务
//     * @param map
//     * @return
//     */
//    public ResponseObjectEntity taskStartOrStop(Map<String,String> map){
//        //任务id
//        String taskId = map.get("taskId");
//        //根据任务id获取数据源，数据资源信息
//        Map<String, String> messageMap = getMessageByTaskId(taskId);
//        JSONObject returnjson = null;
//        ResponseObjectEntity responseObject = new ResponseObjectEntity();
//        responseObject.setFlag(false);
//        try {
//            String masterUrl = getMasterUrl(messageMap.get("dataSourceId"));
//            String serverUrl = masterUrl+"/service/task";
//            String clientId=map.get("clientId");
//            String taskAction = map.get("taskAction");
//            if(clientId.contains(",")){
//                String[] clientIds=clientId.split(",");
//                for(String s:clientIds){
//                    map.put("clientId",s);
//                    returnjson=  resttemplate.postForObject(serverUrl,getTaskStartMessage(map),JSONObject.class);
//                    if(returnjson.get("returnCode").equals(0)){
//                        continue;
//                    }else{
//                        break;
//                    }
//                }
//            }else{
//                returnjson = resttemplate.postForObject(serverUrl,getTaskStartMessage(map),JSONObject.class);
//            }
//            if(Constants.S_NUM_ZERO.equals(returnjson.get("returnCode").toString())){
//               // String taskId = map.get("taskId");
//                //启动，修改状态为1
//                if(Constants.STR_TASK_START.equals(taskAction)){
//                    updateStatus(taskId,"1");
//                    responseObject.setFlag(true);
//                    responseObject.setMessage("启动成功");
//                }else if(Constants.STR_TASK_STOP.equals(taskAction)){
//                    //停止，修改状态为2
//                    updateStatus(taskId,"2");
//                    responseObject.setFlag(true);
//                    responseObject.setMessage("停止成功");
//                }
//            }else{
//                responseObject.setFlag(false);
//                responseObject.setMessage("操作失败");
//            }
//        } catch (RestClientException e) {
//            responseObject.setFlag(false);
//            responseObject.setMessage(e.getMessage());
//            e.printStackTrace();
//        }
//        return responseObject;
//    }

    /**
     * 拼接启动或停止任务报文
     * @param map
     * @return
     */
    public TaskStartEntity getTaskStartMessage(Map<String,String> map){
        TaskStartEntity taskStart=new TaskStartEntity();
        String taskType = "FLUME";//任务类型
        String taskAction = map.get("taskAction");
        String channelType = null;//通道类型
        //String stepTypeCode = map.get("stepTypeCode");
        String stepTypeCode = map.get("passageway");//通道号
        if(Constants.S_STEP_TYPE_CODE.equals(stepTypeCode)){
            channelType = "com.dfssi.dataplatform.datasync.flume.agent.channel.MemoryChannel";
        }else if(Constants.S_STEP_TYPE_CODE_OTHER.equals(stepTypeCode)){
            channelType = "com.dfssi.dataplatform.datasync.flume.agent.channel.kafka.KafkaChannel";
        }else{
            channelType = "com.dfssi.dataplatform.datasync.flume.agent.channel.kafka.KafkaChannel";
        }
        taskStart.setTaskId(map.get("taskId"));
        taskStart.setTaskType(taskType);
        taskStart.setTaskAction(taskAction);
        taskStart.setChannelType(channelType);
        /*封装数据源 start*/
        TaskDataSourceEntity taskDataSource = encapTaskDataSourceMessage(map);
        taskStart.setTaskDataSource(taskDataSource);
        /*封装清洗转换 start*/
        if(map.containsKey("cleanTransId")){
            List<TaskDataCleanTransformsEntity> taskdatacleantransforms = encapTaskCleanTrans(map);
            taskStart.setTaskDataCleanTransforms(taskdatacleantransforms);
        }
        /*封装数据资源数据 start*/
        List<TaskDataDestinationsEntity> taskDataDestinations = encapTaskDataResourceMessage(map);
        taskStart.setTaskDataDestinations(taskDataDestinations);
        logger.info("启动不完整报文："+ taskStart);
        return taskStart;
    }

    /**
     * 清洗转换数据封装用于启动或结束任务报文生成
     * @param map
     * @return
     */
    public List<TaskDataCleanTransformsEntity> encapTaskCleanTrans(Map<String, String> map){
        List<TaskDataCleanTransformsEntity> taskdatacleantransforms = new ArrayList();
        TaskDataCleanTransformsEntity taskDataCleanTransform=new TaskDataCleanTransformsEntity();
        String cttype="com.dfssi.dataplatform.datasync.plugin.interceptor.cleantransfer.CleanTransferInterceptor$Builder";
        taskDataCleanTransform.setType(cttype);
        taskDataCleanTransform.setCleanTransId(map.get("cleanTransId"));
        //微服务调用，根据清洗转换ID查询出清洗转换子表数据
        List<TaskCleanTransformInfoEntity> listTCTIE = feignService.findListByMappingId(taskDataCleanTransform.getCleanTransId());
        List<String> columns=new ArrayList<String>();
        for(int i=0;i<listTCTIE.size();i++){
            TaskCleanTransformInfoEntity taskcleantransforminfoentity=listTCTIE.get(i);
            //将待处理字段添加到list
            columns.add(taskcleantransforminfoentity.getField());
            //list里面相同字段去重
            removeDuplicate(columns);
        }
        Map<String,Map<String, HandleTypeEntity>> cleanTranRule=new HashMap<String,Map<String, HandleTypeEntity>>();
        for(int l=0;l<columns.size();l++){
            //封装转换规则
            Map<String, HandleTypeEntity> handlerule=new HashMap<String, HandleTypeEntity>();
            for(int i=0;i<listTCTIE.size();i++){
                TaskCleanTransformInfoEntity taskcleantransforminfoentity=listTCTIE.get(i);
                if(columns.get(l).equals(taskcleantransforminfoentity.getField())){
                    String param=taskcleantransforminfoentity.getParam();
                    //json串转json
                    JSONObject jsonObject=JSONObject.parseObject(param);
                    //json转对象
                    HandleTypeEntity handleType=JSONObject.toJavaObject(jsonObject, HandleTypeEntity.class);
                    handlerule.put(taskcleantransforminfoentity.getRuleName(),handleType);
                }
            }
            cleanTranRule.put(l+"",handlerule);
        }
        taskDataCleanTransform.setColumns(columns);
        taskDataCleanTransform.setCleanTranRule(cleanTranRule);
        Map<String,String> ctconfig=new HashMap<String,String>();
        taskDataCleanTransform.setConfig(ctconfig);
        taskdatacleantransforms.add(taskDataCleanTransform);
        return taskdatacleantransforms;
    }

    /**
     * 启动任务后更新运行状态
     * @param taskId
     * @param status
     * @return
     */
    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateStatus(String taskId, String status) {
        return taskAccessInfoDao.updateStatus(taskId, status);
    }

    /**
     * list去重
     * @param list
     */
    public static void removeDuplicate(List list) {
        for(int i = 0; i < list.size() - 1; i++) {
            for(int j = list.size() - 1; j > i; j--) {
                if(list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
    }

    /**
     * 参数校验异常捕获判断
     * @param bindingResult
     * @return  校验参数合法性，校验的信息会存放在bindingResult
     */
    public String paramValid(BindingResult bindingResult){
        StringBuilder sb = new StringBuilder();
        if(bindingResult.hasErrors()){
            //bindingResult.getFieldError()随机返回一个对象属性的异常信息
            //如果要一次性返回所有对象属性异常信息，则调用getFieldErrors()
            FieldError fieldError = bindingResult.getFieldError();
            sb.append("参数[").append(fieldError.getRejectedValue()).append("]错误，")
                    .append(fieldError.getDefaultMessage());
            return sb.toString();
        }else{
            return null;
        }
    }

    /**
     * 封装数据源报文，实时调用微服务查询
     * @param map
     * @return
     */
    public TaskDataSourceEntity encapTaskDataSourceMessage(Map<String, String> map){
        TaskDataSourceEntity taskDataSource=new TaskDataSourceEntity();
        //数据源主表的主键id
        String dataSourceId = map.get("dataSourceId");
        taskDataSource.setSrcId(dataSourceId);
        String dataSourcetype = map.get("dataSourceType");
        String type=null;
        Map<String, String> configMap = new HashMap();
        //微服务调用，根据数据源主表的主键id查询出子表的配置信息
        List<MetaDatasourceAccessInfoEntity> list = feignService.getSourceAccessInfoBySrcId(dataSourceId);
        if((Constants.STR_TASK_TCP).equals(dataSourcetype)){
            //tcp
            for(MetaDatasourceAccessInfoEntity datasourceAccessInfo : list){
                String paramValue = datasourceAccessInfo.getParameterValue();
                String paramName = datasourceAccessInfo.getParameterName();
                if((Constants.STR_TCP_MESSAGE_TYPE).equals(paramName)){
                    type = paramValue;
                }
                //取clientip和port
                if((Constants.CLIENT_IP).equals(paramName)){
                    configMap.put("host", paramValue);
                }else if((Constants.CLIENT_PORT).equals(paramName)){
                    configMap.put("ports",paramValue);
                }
            }
        } else if((Constants.STR_TASK_MYSQL).equals(dataSourcetype)){
            //Mysql
            type="com.dfssi.dataplatform.datasync.plugin.sqlsource.source.JDBCSource";
        }else if((Constants.STR_TASK_HTTP).equals(dataSourcetype)){
            //http
            String path = null;
            String params = null;
            String requestModel = null;
            type="com.dfssi.dataplatform.datasync.plugin.http.HTTPSource";
            for(MetaDatasourceAccessInfoEntity datasourceAccessInfo : list){
                String paramValue = datasourceAccessInfo.getParameterValue();
                String paramName = datasourceAccessInfo.getParameterName();
                if((Constants.STR_HTTP_IP).equals(paramName)){
                    configMap.put("host", "http://"+paramValue);
                }else if((Constants.STR_HTTP_PORT).equals(paramName)){
                    configMap.put("port",paramValue);
                }else if((Constants.STR_HTTP_PATH).equals(paramName)){
                    //请求路径
                    path = paramValue;
                }else if((Constants.STR_REQUEST_MODEL).equals(paramName)){
                    //请求方式，0：get，1：post
                    requestModel = paramValue;
                }else if((Constants.STR_HTTP_CRONTIME).equals(paramName)){
                    //定时时间，Quartz的Cron表达式
                    configMap.put("cronTime","0/"+paramValue+" * * * * ?");
                }else if((Constants.STR_HTTP_PARAMS).equals(paramName)){
                    //参数
                    params = paramValue;
                }else{
                    configMap.put(paramName, paramValue);
                }
            }
            if((Constants.STR_HTTP_REQUESTMODEL_GET).equals(requestModel)){
                //get请求参数拼接在路径末尾
                configMap.put("contextPath", path+"/"+params);
                configMap.put("httpRequestType", requestModel);
                configMap.put("contextParams", "");
            }else if((Constants.STR_HTTP_REQUESTMODEL_POST).equals(requestModel)){
                configMap.put("contextPath", path);
                configMap.put("httpRequestType", requestModel);
                configMap.put("contextParams", params);
            }
            configMap.put("handler","com.dfssi.dataplatform.datasync.plugin.http.JSONHandler");
            configMap.put("enableSSL","true");
            configMap.put("taskId",map.get("taskId"));
        }
        taskDataSource.setType(type);
        taskDataSource.setConfig(configMap);
        return taskDataSource;
    }

    /**
     * 封装数据资源报文，实时调用微服务查询
     * @param map
     * @return
     */
    public List<TaskDataDestinationsEntity> encapTaskDataResourceMessage(Map<String, String> map) {
        List<TaskDataDestinationsEntity> taskDataDestinations = new ArrayList<TaskDataDestinationsEntity>();
        TaskDataDestinationsEntity taskDataDestination = new TaskDataDestinationsEntity();
        String dataResourceId = map.get("dataResourceId");//数据资源主表的主键id
        taskDataDestination.setDestId(dataResourceId);
        String dataSourceType = map.get("dataSourceType");//数据源类型
        String dataResourceType = map.get("dataResourceType");//数据资源类型
        String destinationType = null;
        if((Constants.STR_TASK_KAFKA).equals(dataResourceType)){
            if((Constants.STR_TASK_TCP).equals(dataSourceType)){
                destinationType = "com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.KafkaSink";
            }else if((Constants.STR_TASK_HTTP).equals(dataSourceType)){
                destinationType = "com.dfssi.dataplatform.datasync.plugin.sink.km.kafka.KafkaSink";
            }
        }else if((Constants.STR_TASK_HDFS).equals(dataResourceType)){
            destinationType = "com.dfssi.dataplatform.datasync.plugin.sink.hdfs.SSIHDFSEventSink";
        }
        taskDataDestination.setType(destinationType);
        //微服务调用，根据数据资源主键id查询配置属性
        List<MetaDataresourceAccessInfoEntity> list = feignService.getResourceAccessInfoByResId(dataResourceId);
        Map<String,String> destinationConfigMap = new HashMap();
        for(MetaDataresourceAccessInfoEntity dataResourceAccessInfo : list){
            String paramValue = dataResourceAccessInfo.getParameterValue();
            String paramName = dataResourceAccessInfo.getParameterName();
            destinationConfigMap.put(paramName, paramValue);
        }
        taskDataDestination.setConfig(destinationConfigMap);
        taskDataDestinations.add(taskDataDestination);
        return taskDataDestinations;
    }

    /**
     * 根据任务id获取步骤信息数据，分离出数据源主表的主键id，数据源类型，
     * 数据资源主表的主键id，数据资源类型，通道类型，清洗转换
     * @param taskId
     * @return
     */
    public Map getMessageByTaskId(String taskId){
        Map<String, String> map = new HashMap<>();
        TaskAccessInfoEntity taskAccessInfoEntity = taskAccessInfoDao.get(taskId);
        map.put("taskId", taskId);
        map.put("clientId", taskAccessInfoEntity.getClientIds());
        List<TaskAccessStepInfoEntity> taskAccessStepInfoList = taskAccessStepInfoService.findListByTaskId(taskId);
        if(taskAccessStepInfoList.size() > 0){
            for(TaskAccessStepInfoEntity info : taskAccessStepInfoList){
                //可以表示哪种资源，1开头表示数据源，2开头表示通道，3开头表示清洗转换，4开头表示数据资源
                String pluginId = info.getPluginId();
                //可以表示资源的主键id
                String leftnodeId = info.getLeftnodeId();
                //可以表示资源类型
                String cleantransformId = info.getCleantransformId();
                String tempId = null;
                if(StringUtils.isNotEmpty(pluginId)){
                    //截取第一位，1表示数据源，2表示通道，3表示清洗转换，4表示数据资源
                    tempId = pluginId.trim().substring(0,1);
                }
                if((Constants.S_NUM_ONE).equals(tempId)){
                    //数据源
                    map.put("dataSourceId", leftnodeId);//数据源主表主键id
                    map.put("dataSourceType", cleantransformId);//数据源类型
                }else if((Constants.S_NUM_TWO).equals(tempId)){
                    //通道
                    map.put("passageway", cleantransformId);//通道号
                }else if((Constants.S_NUM_THREE).equals(tempId)){
                    //清洗转换
                    map.put("cleanTransId", leftnodeId);//清洗转换id
                }else if((Constants.S_NUM_FOUR).equals(tempId)){
                    //数据资源
                    map.put("dataResourceId", leftnodeId);//数据资源主表主键id
                    map.put("dataResourceType", cleantransformId);//数据资源类型
                }
            }
        }
        return map;
    }

    /**
     * 根据数据源id获取masterip和port，没有就使用默认配置
     * @param dataSourceId
     * @return
     */
    @Override
    public String getMasterUrl(String dataSourceId){
        String masterUrl = null;
        String masterIP = null;
        String masterPort = null;
        List<MetaDatasourceAccessInfoEntity> list = feignService.getSourceAccessInfoBySrcId(dataSourceId);
        if(list.size() > 0){
            for(MetaDatasourceAccessInfoEntity info : list){
                String paramName = info.getParameterName();
                String paramValue = info.getParameterValue();
                if((Constants.MASTER_IP).equals(paramName)){
                    masterIP = paramValue;
                }else if((Constants.MASTER_PORT).equals(paramName)){
                    masterPort = paramValue;
                }
            }
        }
        if(StringUtils.isNotEmpty(masterIP) && StringUtils.isNotEmpty(masterPort)){
            //如果数据源有配置master
            masterUrl = "http://" + masterIP + ":" + masterPort;
        }else{
            //没有配置就是用默认配置
            masterUrl = accessServiceUrl;
        }
        return masterUrl;
    }

    /**
     * 启动或停止任务
     * @param map  参数包含任务taskId：任务id，taskAction：启停标志
     * @author wangke
     * @return
     */
    @Override
    public ResponseObjectEntity taskStartOrStop(Map<String,String> map){
        JSONObject returnjson = null;
        ResponseObjectEntity responseObject = new ResponseObjectEntity();
        responseObject.setFlag(false);
        String taskId = map.get("taskId");//任务id
        String taskAction = map.get("taskAction");//启停标志
        //根据任务id获取接入任务，数据源，数据资源信息，查询表dv_accesstask_stepinfo
        Map<String, String> messageMap = getMessageByTaskId(taskId);
        messageMap.put("taskAction", taskAction);
        //messageMap.put("", );
        String dataSourceId = messageMap.get("dataSourceId");//数据源主表主键id
        try {
            String masterUrl = getMasterUrl(dataSourceId);//获取master地址
            String serverUrl = masterUrl + "/service/task";
            String clientIds = messageMap.get("clientId");//获取数据库存储的客户端
            JSONObject jsonObj = getAliveClientList(masterUrl);//获取master存活的客户端
            List<String> clientList = null;//将master存活的客户端转为list
            if(null != jsonObj){
                clientList = (List<String>) jsonObj.get("list");
            }
            if(0 == clientList.size()){
                //没有存活的客户端
                responseObject.setFlag(false);
                responseObject.setMessage("没有存活的客户端，无法启动任务");
            }else{
                TaskStartEntity entity = getTaskStartMessage(messageMap);//获取报文
                List<String> list = new ArrayList<>();//将数据库存储的客户端字符串转为list
                if(clientIds.contains(",")){
                    String clientId[] = clientIds.split(",");
                    for(String cId : clientId){
                        list.add(cId);
                    }
                }else{
                    String clientId = clientIds;
                    list.add(clientId);
                }
                for(int i = 0; i < list.size(); i++){
                    //master返回的客户端与数据库存储的客户端做匹配，匹配的话采用循环启动的方式
                    if(null != clientList && clientList.contains(list.get(i))){
                        entity.setClientId(list.get(i));
                        logger.info("启动完整报文："+entity);
                        returnjson =  resttemplate.postForObject(serverUrl, entity, JSONObject.class);
                    }
                }
                if(Constants.S_NUM_ZERO.equals(returnjson.get("returnCode").toString())){
                    responseObject.setFlag(true);
                    //启动，修改状态为1
                    if(Constants.STR_TASK_START.equals(taskAction)){
                        updateStatus(taskId,"1");
                        responseObject.setMessage("启动成功");
                    }else if(Constants.STR_TASK_STOP.equals(taskAction)){
                        //停止，修改状态为2
                        updateStatus(taskId,"2");
                        responseObject.setMessage("停止成功");
                    }
                }else{
                    responseObject.setFlag(false);
                    responseObject.setMessage("启动失败");
                }
            }
        } catch (RestClientException e) {
            responseObject.setFlag(false);
            responseObject.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return responseObject;
    }

}
