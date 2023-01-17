package com.dfssi.dataplatform.ide.service.web;


import com.dfssi.dataplatform.analysis.utils.Exceptions;
import com.dfssi.dataplatform.common.controller.ResponseObj;
import com.dfssi.dataplatform.ide.service.service.BIModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/12 9:52
 */
@Api(tags = {"BI图表数据控制器"})
@RestController
@RequestMapping(value = "/bi/")
public class BIController {
//    private final Logger logger = LogManager.getLogger(BIController.class);
//
//    @ApiOperation(value = "获取数据表的字段信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "datasourceid", value = "数据源ID", required = true, dataType = "Long", paramType = "get"),
//            @ApiImplicitParam(name = "table", value = "表名称", required = true, dataType = "String", paramType = "get"),
//    })
//    @RequestMapping(value = "list/columns", method = {RequestMethod.GET})
//    @ResponseBody
//    public Object getTableColumnAndType(HttpServletRequest request, @RequestParam Long datasourceid, @RequestParam String table){
//
//        String error;
//        try {
//            StringBuilder sb = new StringBuilder("获取数据表的字段信息, 参数如下：\n\t");
//            sb.append("datasourceid = ").append(datasourceid).append(", ");
//            sb.append("table = ").append(table).append(".");
//
//            DataSourceMeta dataSourceMeta = getDataSourceMeta(request, datasourceid);
//            logger.info(sb.toString());
//
//            DataBaseSource dataBaseSource = BIContext.get().newDataBaseSource(dataSourceMeta.getSourceType(),
//                    dataSourceMeta.getServers(), dataSourceMeta.getDatabaseUser(), dataSourceMeta.getDatabasePassword(),
//                    dataSourceMeta.getDatabaseName(), null, null, null, null);
//
//            List<Map<String, String>> tableColumnAndTypes = dataBaseSource.getTableColumnAndTypes(table);
//
//            Map<String, Object> res = Maps.newHashMap();
//            res.put("total", tableColumnAndTypes.size());
//           // res.put("pageSize", tableColumnAndTypes.size());
//            res.put("records", tableColumnAndTypes);
//            return ResponseHelper.success(res);
//        } catch (Exception e) {
//            String format = String.format("获取ID为%s的表%s的字段信息失败.", datasourceid, table);
//            error = String.format("%s: %s", format, e.getMessage());
//            logger.error(format, e);
//        }
//
//
//        return ResponseHelper.error(error);
//    }
//
//    @ApiOperation(value = "获取数据源的所有表")
//    @ApiImplicitParam(name = "datasourceid", value = "数据源ID", required = true, dataType = "Long", paramType = "get")
//    @RequestMapping(value = "list/tables", method = {RequestMethod.GET})
//    @ResponseBody
//    public Object getTables(HttpServletRequest request, @RequestParam Long datasourceid){
//
//        String error;
//        try {
//            StringBuilder sb = new StringBuilder("获取数据源的所有表, 参数如下：\n\t");
//            sb.append("datasourceid = ").append(datasourceid);
//
//            DataSourceMeta dataSourceMeta = getDataSourceMeta(request, datasourceid);
//            logger.info(sb.toString());
//
//            DataBaseSource dataBaseSource = BIContext.get().newDataBaseSource(dataSourceMeta.getSourceType(),
//                    dataSourceMeta.getServers(), dataSourceMeta.getDatabaseUser(), dataSourceMeta.getDatabasePassword(),
//                    dataSourceMeta.getDatabaseName(), null, null, null, null);
//
//            List<String> tables = dataBaseSource.listTables();
//
//            Map<String, Object> res = Maps.newHashMap();
//            res.put("total", tables.size());
//            //res.put("pageSize", tables.size());
//            res.put("records", tables);
//            return ResponseHelper.success(res);
//        } catch (Exception e) {
//            String format = String.format("获取ID为%s的所有表失败.", datasourceid);
//            error = String.format("%s: %s", format, e.getMessage());
//            logger.error(format, e);
//        }
//
//
//        return ResponseHelper.error(error);
//    }
//
//
//    @ApiOperation(value = "获取数据表数据")
//    @RequestMapping(value = "list/data", method = {RequestMethod.POST})
//    @ResponseBody
//    public Object readData(HttpServletRequest request,
//                           @ApiParam(name="param",value="数据查询参数", required = true) @RequestBody QureyParam param){
//
//        String error;
//        try {
//            StringBuilder sb = new StringBuilder("获取数据表数据, 参数如下：\n\t");
//            Long datasourceid = param.datasourceid;
//            Preconditions.checkNotNull(datasourceid, "datasourceid不能为空。");
//            sb.append("datasourceid = ").append(datasourceid).append(", ");
//
//            String table = param.table;
//            Preconditions.checkNotNull(table, "table不能为空。");
//            sb.append("table = ").append(table).append(", ");
//
//            String columns = param.columns;
//            Preconditions.checkNotNull(columns, "columns不能为空。");
//            sb.append("columns = ").append(columns).append(", ");
//
//            String charttype = param.charttype;
//            Preconditions.checkNotNull(charttype, "charttype不能为空。");
//            sb.append("charttype = ").append(charttype).append(".");
//
//            DataSourceMeta dataSourceMeta = getDataSourceMeta(request, datasourceid);
//            logger.info(sb.toString());
//
//            DataBaseSource dataBaseSource = BIContext.get().newDataBaseSource(dataSourceMeta.getSourceType(),
//                    dataSourceMeta.getServers(), dataSourceMeta.getDatabaseUser(), dataSourceMeta.getDatabasePassword(),
//                    dataSourceMeta.getDatabaseName(), table, columns, charttype, null);
//
//            Object objects = dataBaseSource.readData();
//
//            Map<String, Object> res = Maps.newHashMap();
//            res.put("records", objects);
//            return ResponseHelper.success(res);
//        } catch (Exception e) {
//            String format = String.format("获取数据失败： \n\t %s", param);
//            error = String.format("%s: %s", format, e.getMessage());
//            logger.error(format, e);
//        }
//
//        return ResponseHelper.error(error);
//    }
//
//
//    private DataSourceMeta getDataSourceMeta(HttpServletRequest request, Long datasourceid){
//        HttpSession session = request.getSession();
//        String id = String.format("datasources-%s", session.getId());
//        Object datasources = session.getAttribute(id);
//
//        Map<Long, DataSourceMeta> sourceMetaMap =  (Map<Long, DataSourceMeta>) datasources;
//        DataSourceMeta dataSourceMeta = sourceMetaMap.get(datasourceid);
//        Preconditions.checkNotNull(dataSourceMeta, String.format("不存在ID为%s的数据源.", datasourceid));
//
//        return dataSourceMeta;
//    }
//
//    @ApiModel(value = "param", description = "查询数据参数实体")
//    public static class QureyParam{
//
//        @ApiModelProperty(name = "datasourceid", value = "数据源ID", required = true, dataType = "Long")
//        private Long datasourceid;
//
//        @ApiModelProperty(name = "table", value = "表名称", required = true, dataType = "String")
//        private String table;
//
//        @ApiModelProperty(name = "columns", value = "字段列表，英文逗号分隔", required = true, dataType = "String", example = "age,salary")
//        private String columns;
//
//        @ApiModelProperty(name = "charttype", value = "图类型: BAR：柱状图, LINE: 折线图,PIE: 饼图,SCATTER：散点图,HEAT：热力图, NONE：无",
//                required = true, dataType = "String",  allowableValues = "BAR, LINE, PIE, SCATTER, HEAT, NONE")
//        private String charttype;
//
//
//        public Long getDatasourceid() {
//            return datasourceid;
//        }
//
//        public void setDatasourceid(Long datasourceid) {
//            this.datasourceid = datasourceid;
//        }
//
//        public String getTable() {
//            return table;
//        }
//
//        public void setTable(String table) {
//            this.table = table;
//        }
//
//        public String getColumns() {
//            return columns;
//        }
//
//        public void setColumns(String columns) {
//            this.columns = columns;
//        }
//
//        public String getCharttype() {
//            return charttype;
//        }
//
//        public void setCharttype(String charttype) {
//            this.charttype = charttype;
//        }
//    }

    private final Logger logger = LogManager.getLogger(BIController.class);

    private final static String LOG_TAG_MODEL_SERVICE = "[Service Model IDE]";

    @Autowired
    private BIModelService biModelService;
//
//    // 根据库名与表名获取hive表字段和字段类型
//    @ResponseBody
//    @RequestMapping(value = "get/hiveColumnInfo", method = RequestMethod.GET)
//    @ApiOperation(value = "获取hive表的字段与字段类型")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "hive库的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "tableName", value = "hive表的名称", dataType = "String", paramType = "query")
//    })
//    public Object getHiveColumnInfo(HttpServletRequest req, HttpServletResponse response, String databaseName, String
//            tableName) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        responseObj.addKeyVal("tableName", tableName);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Hive schame. databaseName=" + databaseName + "\n,tableName=" + tableName);
//            HashMap<String, String> columnNameAndType = biModelService.getColumnNameAndType(databaseName, tableName);
//            if (columnNameAndType != null && columnNameAndType.size() > 0) {
//                responseObj.setData(columnNameAndType);
//                responseObj.buildSuccessMsg("Query hive table successfully.");
//            } else {
//                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
//            }
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
//        }
//        return responseObj;
//    }
//
//    // 根据库名与表名获取hive表前N行指定列数据
//    @ResponseBody
//    @RequestMapping(value = "get/HiveHeadNByCol", method = RequestMethod.GET)
//    @ApiOperation(value = "获取hive表n条指定列数据数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "hive库的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "tableName", value = "hive表的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "colNames", value = "列名，用；号隔开，查询所有列用*号", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "number", value = "获取数据条数", dataType = "int", paramType = "query")
//    })
//    public Object getHiveHeadNByCol(HttpServletRequest req, HttpServletResponse response, String databaseName, String
//            tableName, String colNames, int number) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        responseObj.addKeyVal("tableName", tableName);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Hive schame. databaseName=" + databaseName);
//            List data = biModelService.getHiveHeadNByCol(databaseName, tableName, colNames, number);
//            if (data != null) {
//                responseObj.setData(data);
//                responseObj.buildSuccessMsg("Query hive table successfully.");
//            } else {
//                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
//            }
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to Query hive table. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
//        }
//        return responseObj;
//    }
//
//    // 获取数据源所有表信息
//    @ResponseBody
//    @RequestMapping(value = "get/AllTables", method = RequestMethod.GET)
//    @ApiOperation(value = "获取hive数据源所有表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "hive库的名称", dataType = "String", paramType = "query")
//    })
//    public Object getAllTables(HttpServletRequest req, HttpServletResponse response, String databaseName) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Hive schame. databaseName=" + databaseName);
//            HashMap<String, String> allTables = biModelService.getAllTables(databaseName);
//            if (allTables != null && allTables.size() > 0) {
//                responseObj.setData(allTables);
//                responseObj.buildSuccessMsg("Query hive table successfully.");
//            } else {
//                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName, null);
//            }
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query hive table. databaseName=" + databaseName,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to Query hive table. databaseName=" + databaseName, t);
//        }
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "mysql/getMysqlTableColumnAndType", method = RequestMethod.GET)
//    @ApiOperation(value = "根据库名与表名，查询mysql表字段和类型")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "mysql库的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "tableName", value = "mysql库中表的名称", dataType = "String", paramType = "query")
//    })
//    public Object getMysqlTableColumnAndType(@RequestParam String databaseName, @RequestParam String tableName) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        responseObj.addKeyVal("tableName", tableName);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql schame. databaseName=" + databaseName);
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql table. tableName=" + tableName);
//            HashMap<String, String> tableColumnAndType = biModelService.getTableColumnAndType(databaseName, tableName);
//            if (tableColumnAndType != null)
//                responseObj.setData(tableColumnAndType);
//            responseObj.buildSuccessMsg("Get mysql table column and type successfully.");
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Get mysql table column and type. databaseName=" + databaseName + ",tableName="+tableName,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to  Get mysql table column and type. databaseName=" + databaseName + ",tableName="+tableName, t);
//        }
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "mysql/getTableSpecifieldColumnData", method = RequestMethod.GET)
//    @ApiOperation(value = "根据指定列,查询mysql指定列的数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "mysql库的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "tableName", value = "mysql库中表的名称", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "columnNameList", value = "mysql库中表的列集合", dataType = "List", paramType = "query")
//    })
//    public Object getTableSpecifieldColumnData(@RequestParam String databaseName, @RequestParam String tableName, @RequestParam List<String> columnNameList) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        responseObj.addKeyVal("tableName", tableName);
//        responseObj.addKeyVal("columnNameList", columnNameList);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql schame. databaseName=" + databaseName);
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql table. tableName=" + tableName);
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql table. columnNameList=" + columnNameList);
//            List<Map<String, Object>> tableSpecifiedColumnData = biModelService.getTableSpecifiedColumnData(databaseName, tableName, columnNameList);
//            if (tableSpecifiedColumnData != null && tableSpecifiedColumnData.size() > 0)
//                responseObj.setData(tableSpecifiedColumnData);
//            responseObj.buildSuccessMsg("Get mysql table specifield column data successfully.");
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Get mysql table specifield column data. databaseName=" + databaseName + ",tableName="+tableName + ",columnNameList=" + columnNameList,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to  Get mysql table specifield column data. databaseName=" + databaseName + ",tableName="+tableName + ",columnNameList=" + columnNameList, t);
//        }
//        return responseObj;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "mysql/getTableNameByDatabaseName", method = RequestMethod.GET)
//    @ApiOperation(value = "根据库名，获取mysql全部的表名")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "databaseName", value = "mysql库的名称", dataType = "String", paramType = "query")
//    })
//    public Object getTableNameByDatabaseName(@RequestParam String databaseName) {
//        ResponseObj responseObj = ResponseObj.createResponseObj();
//        responseObj.addKeyVal("databaseName", databaseName);
//        try {
//            logger.info(LOG_TAG_MODEL_SERVICE + "Mysql schame. databaseName=" + databaseName);
//            List<String> tableNameByDatabaseName = biModelService.getTableNameByDatabaseName(databaseName);
//            if (tableNameByDatabaseName != null && tableNameByDatabaseName.size() > 0)
//                responseObj.setData(tableNameByDatabaseName);
//            responseObj.buildSuccessMsg("Get mysql table name by database name successfully.");
//        } catch (Throwable t) {
//            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Get mysql table name by database name. databaseName=" + databaseName,
//                    Exceptions.getStackTraceAsString(t));
//            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to  Get mysql table name by database name. databaseName=" + databaseName, t);
//        }
//        return responseObj;
//    }

    // 数据预览
    @ResponseBody
    @RequestMapping(value = "get/dataPreview", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定数据库n条指定列数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "colNames", value = "列名，用；号隔开，查询所有列用*号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "获取数据条数", dataType = "int", paramType = "query")
    })
    public Object dataPreview(String dataresourceType,
                              String ip,
                              String port,
                              String databaseUsername,
                              String databasePassword,
                              String databaseName,
                              String tableName,
                              String colNames,
                              int number) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Object data = biModelService.dataPreview(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colNames, number);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Query special column data of different database successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to Query special column data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    // 获取字段名及类型
    @ResponseBody
    @RequestMapping(value = "get/getTableColumnAndTypes", method = RequestMethod.GET)
    @ApiOperation(value = "数据预览通用接口，获取指定数据库字段名及类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name="dataresourceType", value = "数据库的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "端口号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseUsername", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databasePassword", value = "密码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "databaseName", value = "数据库的名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", paramType = "query")
    })
    public Object getTableColumnAndTypes(String dataresourceType,
                                         String ip,
                                         String port,
                                         String databaseUsername,
                                         String databasePassword,
                                         String databaseName,
                                         String tableName) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Map<String, String> data = biModelService.getTableColumnAndTypes(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Get table columns and types successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    //获取类别列列值
    @ResponseBody
    @RequestMapping(value = "get/getCategoryValue", method = RequestMethod.GET)
    @ApiOperation(value = "类别列数据接口")
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
    public Object getCategoryValue(String dataresourceType,
                                         String ip,
                                         String port,
                                         String databaseUsername,
                                         String databasePassword,
                                         String databaseName,
                                         String tableName,
                                         String colName) {
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Set<Object> data = biModelService.getCategoryValue(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colName);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Get table columns and types successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to get columns and types from database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

    // 按类别列列值获取特定列数据
    @ResponseBody
    @RequestMapping(value = "get/getDataByCategory", method = RequestMethod.GET)
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
        ResponseObj responseObj = ResponseObj.createResponseObj();
        responseObj.addKeyVal("dataresourceType", dataresourceType);
        responseObj.addKeyVal("databaseName", databaseName);
        responseObj.addKeyVal("tableName", tableName);
        try {
            logger.info(LOG_TAG_MODEL_SERVICE + "DB schame. dataresourceType=" + dataresourceType + ",databaseName=" + databaseName + ",tableName=" + tableName);

            Object data = biModelService.getDataByCategory(dataresourceType, ip, port, databaseUsername, databasePassword, databaseName, tableName, colName, colNames, value);
            if (data != null) {
                responseObj.setData(data);
                responseObj.buildSuccessMsg("Query special column and value data of different database successfully.");
            } else {
                responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column and value data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, null);
            }
        } catch (Throwable t) {
            responseObj.buildFailMsg(ResponseObj.CODE_FAIL, "Failed to Query special column and value data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName,
                    Exceptions.getStackTraceAsString(t));
            logger.error(LOG_TAG_MODEL_SERVICE + "Failed to Query special column and value data of different database. databaseName=" + databaseName + "\n,tableName=" + tableName, t);
        }
        return responseObj;
    }

}