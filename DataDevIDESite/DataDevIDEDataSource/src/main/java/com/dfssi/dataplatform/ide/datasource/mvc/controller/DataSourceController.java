package com.dfssi.dataplatform.ide.datasource.mvc.controller;

import com.dfssi.dataplatform.cloud.common.annotation.LogAudit;
import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.cloud.common.entity.ResponseObj;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataBaseEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceConfEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataSourceSubEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.service.DataSourceService;
import com.dfssi.dataplatform.ide.datasource.mvc.service.DataSourceSubService;
import com.dfssi.dataplatform.ide.datasource.mvc.service.FieldRulesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据源处理
 * @author dingsl
 * @since 2018/8/21
 */
@RestController
@RequestMapping("/datasource")
@Api(value = "DataSourceController", description = "数据源信息可视化")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataSourceSubService dataSourceSubService;

    @Autowired
    private FieldRulesService fieldRulesService;

    /**
     * 保存数据源（新增/修改）
     * @param entity
     * @return ResponseObj
     */
    @RequestMapping(value = "/saveDataSource", method = RequestMethod.POST)
    @ApiOperation(value = "数据源单条信息录入、修改",notes="datasourceName、datasourceDesc、datasourceType、dataSourceSubEntity为必填项")
    @LogAudit
    public ResponseObj saveDataSource(@RequestBody @Validated DataSourceEntity entity, BindingResult bindingResult)  {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        try {
            String str =fieldRulesService.paramValid(bindingResult);
            if(StringUtils.isNotEmpty(str)){
                responseObj.setData("");
                responseObj.setStatus(ResponseObj.CODE_FAIL_P,"参数格式不正确！",str);
                return responseObj;
            }
            String  result = dataSourceService.saveEntity(entity);
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
     * 数据连接测试
     * @param entity
     * @return ResponseObj
     */
    @RequestMapping(value = "/connectionTest", method = RequestMethod.POST)
    @ApiOperation(value = "连接测试")
    @LogAudit
    public ResponseObj connectionTest(@RequestBody DataSourceEntity entity) {
        String result = null;
        ResponseObj responseObj = ResponseObj.createResponseObj();
            result = dataSourceService.connectionTestNew(entity);
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
     * 删除数据源中的实体信息
     * @param datasourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/deleteDataSource/{datasourceId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据源中的实体")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "datasourceId",value = "表的pk_datasource_id",required = true,dataType = "String",paramType = "path")
    })
    @LogAudit
    public ResponseObj deleteDataSource(@PathVariable String datasourceId) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
            String result = dataSourceService.deleteDataSourceEntity(datasourceId);
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
     *分页查询数据源中的实体信息（区分大数据，关系型数据库，协议接口）
     * @param queryType
     * @param entity
     * @param pageParam
     * @return ResponseObj
     */
    @RequestMapping(value = "/dataSourceList/queryType/{queryType}", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询数据源中的实体信息",notes="通过queryType区分大数据，关系型数据库，协议接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryType",value ="数据源类别（queryDB、queryBigdata、queryInterface）",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "datasourceName", value = "数据源名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dbname", value = "数据源类型", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startCreateTime", value = "最早创建时间", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endCreateTime", value = "最晚创建时间", required = false, dataType = "String", paramType = "query")
    })
    @LogAudit
    public ResponseObj dataSourceList(@PathVariable String queryType,DataSourceEntity entity, PageParam pageParam) {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            if ((DataBaseEntity.QUERYDB).equals(queryType)) {
                PageResult<DataSourceEntity> pageResult = dataSourceService.findDBEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else if ((DataBaseEntity.QUERYBIGDATA).equals(queryType)) {
                PageResult<DataSourceEntity> pageResult = dataSourceService.findBigdataEntityList(entity, pageParam);
                result.setData(pageResult);
                result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
            } else if ((DataBaseEntity.QUERYINTERFACE).equals(queryType)) {
                PageResult<DataSourceEntity> pageResult = dataSourceService.findInterfaceEntityList(entity, pageParam);
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
     * 查询数据源数量
     * @return ResponseObj
     */
    @RequestMapping(value = "/sourceTotal", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据源数量")
    @LogAudit
    public ResponseObj sourceTotal() {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            Map total = dataSourceService.countTotal();
            result.setData(total);
            result.setStatus(ResponseObj.CODE_SUCCESS, "成功！", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B, "失败!", e.getMessage());
        }
        return result;
    }

    /**
     * 根据数据源的id预览数据源的详细信息
     * @param dataSourceId
     * @return ResponseObj
     */
    @RequestMapping(value = "/queryDatasourceInfo/{dataSourceId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据源的id预览数据源的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataSourceId",value = "表的pk_datasource_id",required = true,dataType = "String",paramType = "path")
    })
    @LogAudit
    public ResponseObj findById(@PathVariable String dataSourceId) {
        ResponseObj result = ResponseObj.createResponseObj();
        try {
            List<DataSourceSubEntity> list = dataSourceService.findDatasourceInfo(dataSourceId);
            result.setData(list);
            result.setStatus(ResponseObj.CODE_SUCCESS, "成功", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("");
            result.setStatus(ResponseObj.CODE_FAIL_B, "失败", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/getAllDataSources", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据源")
    @LogAudit
    public List<DataSourceConfEntity> getAllDataResources(){
        return   dataSourceService.getAllDataSources();
    }

    /**
     * 可视化接入里的微服务调用
     * @param srcId
     * @return
     */
    @RequestMapping(value ="/micro/getAccessInfoById",method = RequestMethod.POST)
    @LogAudit
    public List<DataSourceSubEntity> getSubinfoById(@RequestBody String srcId){
        return dataSourceSubService.getSubinfoById(srcId);
    }


}
