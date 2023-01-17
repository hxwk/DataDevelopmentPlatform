package com.dfssi.dataplatform.analysis.service.controller;

import com.dfssi.dataplatform.analysis.common.constant.Constants;
import com.dfssi.dataplatform.analysis.common.util.Exceptions;
import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.service.service.BIService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/12 13:51
 */
@Controller
@RequestMapping(value = "service")
@Api(tags = {"服务BI控制器"})
@CrossOrigin
public class BIController extends AbstractController {

    @Autowired
    private BIService biService;

    @ResponseBody
    @RequestMapping(value = "getTableData", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定表格n条指定列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colNames", value = "列名，用；号隔开，查询所有列用*号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "获取数据条数", dataType = "int", paramType = "query")
    })
    public Object getTableData(String dataresourceType,
                               String ip,
                               String port,
                               String databaseUsername,
                               String databasePassword,
                               String databaseName,
                               String tableName,
                               String colNames,
                               int number) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Get dataresourceType=%s databaseName=%s table=%s date.", dataresourceType, databaseName, tableName));
            List<LinkedHashMap<String, Object>> data = biService.getTableData(
                    dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colNames, number);
            if (data != null) {
                return ResponseUtils.buildSuccessResult(
                        String.format("Get dataresourceType=%s databaseName=%s table=%s date successfully.", dataresourceType, databaseName, tableName), data);
            } else {
                return ResponseUtils.buildFailResult("Data is null!");
            }
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Fail to get dataresourceType=%s databaseName=%s table=%s date.\n", dataresourceType, databaseName, tableName), t);
            return ResponseUtils.buildFailResult(String.format("Fail to get dataresourceType=%s databaseName=%s table=%s date.", dataresourceType, databaseName, tableName),
                    Exceptions.getStackTraceAsString(t));
        }

    }

    @ResponseBody
    @RequestMapping(value = "getTableColumnInfo", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定表格字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
    })
    public Object getTableColumnInfo(String dataresourceType,
                                     String ip,
                                     String port,
                                     String databaseUsername,
                                     String databasePassword,
                                     String databaseName,
                                     String tableName) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Get dataresourceType=%s databaseName=%s table=%s column info.", dataresourceType, databaseName, tableName));
            Map<String, String> data = biService.getTableColumnInfo(
                    dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName);
            return ResponseUtils.buildSuccessResult(
                    String.format("Get dataresourceType=%s databaseName=%s table=%s column info successfully.", dataresourceType, databaseName, tableName), data);
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Fail to get dataresourceType=%s databaseName=%s table=%s column info.\n", dataresourceType, databaseName, tableName), t);
            return ResponseUtils.buildFailResult(String.format("Fail to get dataresourceType=%s databaseName=%s table=%s column info.", dataresourceType, databaseName, tableName),
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getTableColumnCategory", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定表格指定字段数据类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colName", value = "类别列列名", dataType = "String", paramType = "query"),

    })
    public Object getTableColumnCategory(String dataresourceType,
                                         String ip,
                                         String port,
                                         String databaseUsername,
                                         String databasePassword,
                                         String databaseName,
                                         String tableName,
                                         String colName) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Get dataresourceType=%s databaseName=%s table=%s column category.", dataresourceType, databaseName, tableName));
            Set<Object> data = biService.getTableColumnCategory(
                    dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colName);
            if (data != null) {
                return ResponseUtils.buildSuccessResult(
                        String.format("Get dataresourceType=%s databaseName=%s table=%s column category successfully.", dataresourceType, databaseName, tableName), data);
            } else {
                return ResponseUtils.buildFailResult("Data is null!");
            }
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Fail to get dataresourceType=%s databaseName=%s table=%s column category.\n", dataresourceType, databaseName, tableName), t);
            return ResponseUtils.buildFailResult(String.format("Fail to get dataresourceType=%s databaseName=%s table=%s column category.", dataresourceType, databaseName, tableName),
                    Exceptions.getStackTraceAsString(t));
        }
    }

    @ResponseBody
    @RequestMapping(value = "getDataByCategory", method = RequestMethod.GET)
    @ApiOperation(value = "按类别列列值获取特定列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colName", value = "类别列列名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colNames", value = "列名，用；号隔开，查询所有列用*号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "value", value = "类别值，用；号隔开", dataType = "String", paramType = "query")
    })
    public Object getDataByCategory(String dataresourceType,
                                    String ip,
                                    String port,
                                    String databaseUsername,
                                    String databasePassword,
                                    String databaseName,
                                    String tableName,
                                    String colName,
                                    String colNames,
                                    String value) {
        try {
            logger.info(Constants.LOG_TAG_MODEL_SERVICE +
                    String.format("Get dataresourceType=%s databaseName=%s table=%s special column and value data.", dataresourceType, databaseName, tableName));
            Object data = biService.getDataByCategory(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colName, colNames, value);
            if (data != null) {
                return ResponseUtils.buildSuccessResult(
                        String.format("Get dataresourceType=%s databaseName=%s table=%s special column and value data successfully.", dataresourceType, databaseName, tableName), data);
            } else {
                return ResponseUtils.buildFailResult("Data is null!");
            }
        } catch (Throwable t) {
            logger.error(Constants.LOG_TAG_MODEL_SERVICE + "Failed to get special column and value data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
            return ResponseUtils.buildFailResult(String.format("Fail to get dataresourceType=%s databaseName=%s table=%s special column and value data.", dataresourceType, databaseName, tableName),
                    Exceptions.getStackTraceAsString(t));
        }
    }
}
