package com.dfssi.dataplatform.ide.dataresource.mvc.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataBaseEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceConfEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DataResourceSubEntity;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.DataResourceService;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.DataResourceSubService;
import com.dfssi.dataplatform.ide.dataresource.mvc.service.FieldRulesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 数据资源信息可视化
 *
 * @author dingsl
 * @since 2018/8/21
 */
@RestController
@RequestMapping("/dataresource")
@Api(value = "DataSourceController", description = "数据资源信息可视化")
public class DataResourceController {

    @Autowired
    private DataResourceService dataResourceService;

    @Autowired
    private DataResourceSubService dataResourceSubService;

    @Autowired
    private FieldRulesService fieldRulesService;

    /**
     * 新增和修改数据资源实体
     *
     * @param entity
     * @return ResponseObj
     */
    @RequestMapping(value = "/saveDataResource", method = RequestMethod.POST)
    @ApiOperation(value = "新增和修改数据资源实体", notes = "dataresourceName、dataresourceDesc、dataresourceType、dataResourceAccessEntity为必填项，dataResourceColumnEntity为可选项")
    @LogAudit
    public ResponseObj saveDataResource(@RequestBody @Validated DataResourceEntity entity, BindingResult bindingResult) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String str =fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
                return responseObj;
            }
            String result = dataResourceService.saveEntity(entity);
            if (StringUtils.isEmpty(result)) {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else {
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return responseObj;
    }

    /**
     * 删除数据资源实体
     *
     * @param dataresourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/deleteDataResource/{dataresourceId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据资源中的实体")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataresourceId", value = "表的dataresource_id", required = true, dataType = "String", paramType = "path")
    })
    @LogAudit
    public ResponseObj deleteDataResource(@PathVariable String dataresourceId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = dataResourceService.deleteDataResourceEntity(dataresourceId);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    /**
     * 分页查询查询数据资源实体（需要区分大数据组件和关系型数据库和私有资源和共有资源）
     *
     * @param queryType
     * @param entity
     * @param pageParam
     * @return ResponseObj
     * @throws ParseException
     */
    @RequestMapping(value = "/dataResourceList/queryType/{queryType}", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询数据资源中的实体信息", notes = "通过queryType大数据组件、关系型数据库、私有资源、共有资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryType", value = "数据资源类别（queryDB、queryBigdata、queryPrivateResources、querySharedResources）", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dataresourceName", value = "数据源资名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startCreateTime", value = "最早创建时间", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endCreateTime", value = "最晚创建时间", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj dataResourceList(@PathVariable String queryType, DataResourceEntity entity, PageParam pageParam) throws ParseException {
        ResponseObj result = ResponseObj.createResponseObj();
        DataBaseEntity info = new DataBaseEntity();
        try {
            if ((DataBaseEntity.QUERYBIGDATA).equals(queryType)) {
                PageResult<DataResourceEntity> pageResult = dataResourceService.findBigdataEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else if ((DataBaseEntity.QUERYDB).equals(queryType)) {
                PageResult<DataResourceEntity> pageResult = dataResourceService.findDBEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else if ((DataBaseEntity.QUERYPRIVATERESOURCES).equals(queryType)) {
                PageResult<DataResourceEntity> pageResult = dataResourceService.findPrivateResourcesEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else if ((DataBaseEntity.QUERYSHAREDRESOURCES).equals(queryType)) {
                PageResult<DataResourceEntity> pageResult = dataResourceService.findSharedResourcesEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return result;
    }

    /**
     * 根据数据资源的id预览查询数据资源的详细信息
     *
     * @param dataResourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/queryDataresourceInfo/{dataResourceId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据资源的id预览数据资源的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataResourceId", value = "表的dataresource_id", required = true, dataType = "String", paramType = "path")
    })
    @LogAudit
    public ResponseObj findById(@PathVariable String dataResourceId) {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            List<DataResourceSubEntity> list = dataResourceService.findDataresourceInfo(dataResourceId);
            result.setData(list);
            result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return result;
    }

    /**
     * 数据连接测试
     *
     * @param entity
     * @return ResponseObj
     */
    @RequestMapping(value = "/connectionTest", method = RequestMethod.POST)
    @ApiOperation(value = "连接测试")
    @LogAudit
    public ResponseObj connectionTest(@RequestBody DataResourceEntity entity) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = null;
            result = dataResourceService.connectionTestNew(entity);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
       } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
       }
        return responseObj;
    }

    /**
     * 查询数据资源数量
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/resourceTotal", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据资源数量")
    @LogAudit
    public ResponseObj resourceTotal() {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            Map total = dataResourceService.countRtotal();
            result.setData(total);
            result.setStatus(ResponseObj.CODE_SUCCESS, "成功！", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B, "失败！", e.getMessage());
        }
        return result;
    }

    /**
     * 私有化属性变公有化
     *
     * @param dataresourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/changePrivateStatus/{dataresourceId}", method = RequestMethod.POST)
    @ApiOperation(value = "私有化属性变公有化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataResourceId", value = "表的dataresource_id", required = true, dataType = "String", paramType = "path")
    })
    @LogAudit
    public ResponseObj changePrivateStatus(@PathVariable String dataresourceId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = dataResourceService.changePrivateStatus(dataresourceId);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    /**
     * 公有化属性变私有化
     *
     * @param dataresourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/changeSharedStatus/{dataresourceId}", method = RequestMethod.POST)
    @ApiOperation(value = "公有化属性变私有化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataResourceId", value = "表的dataresource_id", required = true, dataType = "String", paramType = "path")
    })
    @LogAudit
    public ResponseObj changechangeSharedStatusStatus(@PathVariable String dataresourceId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        String result = dataResourceService.changeSharedStatus(dataresourceId);
        if (StringUtils.isEmpty(result)) {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } else {
            responseObj.setData("");
            responseObj.setStatus(ResponseObj.CODE_FAIL_B, "失败", result);
        }
        return responseObj;
    }

    /**
     * 查询数据资源
     *
     * @return Object
     */
    @RequestMapping(value = "/getAllDataResources", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据资源")
    @LogAudit
    public List<DataResourceConfEntity> getAllDataResources() {
        return dataResourceService.getAllDataResources();
    }

    @RequestMapping(value = "/micro/getAccessInfoById",method = RequestMethod.POST)
    @LogAudit
    public List<DataResourceSubEntity> getResourceSubInfoByResId(@RequestBody String resId){
        return dataResourceSubService.getResourceSubInfoByResId(resId);
    }

}
