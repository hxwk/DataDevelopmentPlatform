package com.dfssi.dataplatform.ide.cleantransform.mvc.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.ide.cleantransform.mvc.dao.ITaskCleanTransformDao;
import com.dfssi.dataplatform.ide.cleantransform.mvc.dao.ITaskCleanTransformMapperDao;
import com.dfssi.dataplatform.ide.cleantransform.mvc.entity.*;
import com.dfssi.dataplatform.ide.cleantransform.mvc.service.ICleanTransformService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hongs on 2018/5/22.
 */
@Service(value = "cleanTransformService")
public class CleanTransformService implements ICleanTransformService {

    @Autowired
    private ITaskCleanTransformMapperDao ITaskCleanTransformMapperDao;

    @Autowired
    private ITaskCleanTransformDao ITaskCleanTransformDao;

    /**
     * 保存清洗转换信息
     * @param lists
     * @return
     */
    public int saveCleanTransformModel(List<String> lists){
        List<TaskCleanTransformMapperEntity> listbean= CTEntity.buildFromJson(lists);
        int N=0;
        if (listbean.size() > 0) {
            N= ITaskCleanTransformMapperDao.batchInsert(listbean);
        }
        return N;
    }

    /**
     * 根据id删除字段映射主表信息
     * @param id
     * @return
     */
    public int deleteMappingById(String id){
        return ITaskCleanTransformDao.delete(id);
    }

    /**
     * 修改清洗转换信息
     * @param jsonStr
     * @return
     */
    public int updateCleanTransformModel(String jsonStr){
        TaskCleanTransformMapperEntity tctie= CTEntity.buildFromJson(jsonStr);
        return ITaskCleanTransformMapperDao.update(tctie);
    }

    /**
     * 查询所有的清洗抓换信息列表
     * @return
     */
    @Override
    public List<TaskCleanTransformMapperEntity> findeEntityList() {
         List<TaskCleanTransformMapperEntity> listbean= ITaskCleanTransformMapperDao.findAllList();
        return listbean;
    }

    /**
     * 查询字段映射主表信息
     * @param entity
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<TaskCleanTransformEntity> getMappings(TaskCleanTransformEntity entity, PageParam pageParam) {
        Page<TaskCleanTransformEntity> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<TaskCleanTransformEntity> list = ITaskCleanTransformDao.findList(entity);
        PageResult<TaskCleanTransformEntity> pageResult = new PageResult();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 保存清洗转换字段映射主表和子表信息
     * @param taskModel
     * @return
     */
    @Override
    //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SerResponseEntity saveModel (TaskCleanTransformModelEntity taskModel) {
        SerResponseEntity responseObject = new SerResponseEntity();
        responseObject.setStatusCode("1");
        //检查参数是否正确传输
        if (null == taskModel.getTaskCleanTransformMapperEntityys()|| null == taskModel.getTaskCleanTransformEntity()
                || taskModel.getTaskCleanTransformMapperEntityys().size() <= 0) {
            responseObject.setMessage("任务实体不能为空");
            responseObject.setFlag(false);
            return responseObject;
        }
        //字段映射主表信息
        TaskCleanTransformEntity taskcleantransformmappingentity = taskModel.getTaskCleanTransformEntity();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=df.format(day);
        if (StringUtils.isNotBlank(taskcleantransformmappingentity.getId())){
             /*修改*/
            //判断名字重复
            List<TaskCleanTransformEntity> list = ITaskCleanTransformDao.queryRepeatName(taskcleantransformmappingentity);
            if(list.size() > 0){
                responseObject.setMessage("映射规则名称不能重复");
                responseObject.setFlag(false);
                return responseObject;
            }
            taskcleantransformmappingentity.setUpdateDate(s);
            taskcleantransformmappingentity.setUpdateUser("sessionUser");
            id = taskcleantransformmappingentity.getId();
            ITaskCleanTransformDao.update(taskcleantransformmappingentity);
            //先将子表逻辑删除,保留原始数据
            ITaskCleanTransformMapperDao.deleteByMappingId(id);
            //批量插入子表信息
            List<TaskCleanTransformMapperEntity> TaskCleanTransformInfoEntities = taskModel.getTaskCleanTransformMapperEntityys();
            List<TaskCleanTransformMapperEntity> TaskCleanTransformInfoEntitiesnew=new ArrayList<TaskCleanTransformMapperEntity>();
            for(TaskCleanTransformMapperEntity taskcleantransforminfoentity:TaskCleanTransformInfoEntities){
                String eid = UUID.randomUUID().toString().replaceAll("-", "");
                taskcleantransforminfoentity.setCreateDate(s);
                taskcleantransforminfoentity.setCreateUser("sessionUser");
                taskcleantransforminfoentity.setUpdateDate(s);
                taskcleantransforminfoentity.setUpdateUser("sessionUser");
                taskcleantransforminfoentity.setId(eid);
                taskcleantransforminfoentity.setMappingId(id);
                TaskCleanTransformInfoEntitiesnew.add(taskcleantransforminfoentity);
            }
            ITaskCleanTransformMapperDao.insertMutil(TaskCleanTransformInfoEntitiesnew);
        }else{
            /*新增操作*/
            //判断名字重复
            List<TaskCleanTransformEntity> list = ITaskCleanTransformDao.queryRepeatName(taskcleantransformmappingentity);
            if(list.size() > 0){
                responseObject.setMessage("映射规则名称不能重复");
                responseObject.setFlag(false);
                return responseObject;
            }
            taskcleantransformmappingentity.setCreateDate(s);
            taskcleantransformmappingentity.setCreateUser("sessionUser");
            taskcleantransformmappingentity.setId(id);
            ITaskCleanTransformDao.insert(taskcleantransformmappingentity);
            //批量插入子表信息
            List<TaskCleanTransformMapperEntity> taskcleantransforminfoentities = taskModel.getTaskCleanTransformMapperEntityys();
            List<TaskCleanTransformMapperEntity> taskcleantransforminfoentitiesnew=new ArrayList<TaskCleanTransformMapperEntity>();
            for(TaskCleanTransformMapperEntity taskcleantransforminfoentity:taskcleantransforminfoentities){
                String eid = UUID.randomUUID().toString().replaceAll("-", "");
                taskcleantransforminfoentity.setCreateDate(s);
                taskcleantransforminfoentity.setCreateUser("sessionUser");
                taskcleantransforminfoentity.setId(eid);
                taskcleantransforminfoentity.setMappingId(id);
                taskcleantransforminfoentitiesnew.add(taskcleantransforminfoentity);
            }
            ITaskCleanTransformMapperDao.insertMutil(taskcleantransforminfoentitiesnew);
        }
        responseObject.setMessage("清洗转换信息保存成功");
        responseObject.setFlag(true);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", id);
        responseObject.setData(dataMap);
        return responseObject;
    }

    /**
     * 清洗转换字段映射根据id查询主表和子表信息
     * @param
     * @return
     */
    @Override
    public Object getModel (String id) {
        SerResponseEntity responseObject = new SerResponseEntity();
        responseObject.setStatusCode("1");
        //检查参数是否正确传输
        if (null == id || StringUtils.isBlank(id)) {
            String message = "id不能为空";
            responseObject.setMessage(message);
            responseObject.setFlag(false);
            return responseObject;
        }
        TaskCleanTransformModelEntity taskCleanTransformModelEntity = new TaskCleanTransformModelEntity();
        taskCleanTransformModelEntity.setTaskCleanTransformEntity(ITaskCleanTransformDao.get(id));
        List<TaskCleanTransformMapperEntity> taskCleanTransformInfoEntities = ITaskCleanTransformMapperDao.findListByMappingId(id);
        taskCleanTransformModelEntity.setTaskCleanTransformMapperEntityys(taskCleanTransformInfoEntities);
        responseObject.setMessage("清洗转换信息获取成功");
        responseObject.setFlag(true);
        responseObject.setData(taskCleanTransformModelEntity);
        return responseObject;
    }

    /**
     * 根据主表Id删除单条任务记录
     * @param id
     * @return
     */
    public Object deleteSingleByMappingId(String id){
        //先删子表，再删主表
        ITaskCleanTransformMapperDao.deleteByMappingId(id);
        int i = ITaskCleanTransformDao.delete(id);
        SerResponseEntity responseObject = new SerResponseEntity();
        if(i>0){
            responseObject.setStatusCode("1");
            responseObject.setMessage("单条主表信息删除成功");
            responseObject.setFlag(true);
            responseObject.setData(i);
        }
        else{
            responseObject.setStatusCode("0");
            responseObject.setMessage("单条主表信息删除失败");
            responseObject.setFlag(false);
            responseObject.setData(i);
        }
        return responseObject;
    }

    /**
     * 根据映射ID查询字表list
     * @param id
     * @return
     */
    public List<TaskCleanTransformMapperEntity> findListByMappingId(String id){
        return ITaskCleanTransformMapperDao.findListByMappingId(id);
    }

    public List<TaskCleanTransformEntity> getAllCleanTranses(){
        return ITaskCleanTransformDao.getAllCleanTranses();
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

}
