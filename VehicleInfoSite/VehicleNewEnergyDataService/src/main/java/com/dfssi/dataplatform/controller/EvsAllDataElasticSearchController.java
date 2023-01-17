package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.service.EvsAllDataElasticSearchService;
import com.dfssi.dataplatform.utils.ResponseUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/2 19:35
 */
@Api(tags = {"新能源全量数据es统计查询控制器"})
@RestController
@RequestMapping(value="/es/all/")
public class EvsAllDataElasticSearchController {
    private final Logger logger = LoggerFactory.getLogger(EvsAllDataElasticSearchController.class);

    @Autowired
    private EvsAllDataElasticSearchService evsAllDataElasticSearchService;

    @ApiOperation(value = "查询全量数据详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterprises", value = "车企, 多个使用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hatchbacks", value = "车型, 多个使用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "vins", value = "车辆VIN, 多个使用英文逗号分隔", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNow", value = "查询页码", dataType = "int", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", dataType = "int", defaultValue = "10", paramType = "query")
    })
    @RequestMapping(value = "data", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchData(String enterprises,
                             String hatchbacks,
                             String vins,
                             String columns,
                             Long startTime,
                             Long endTime,
                             @RequestParam(defaultValue = "1") int pageNow,
                             @RequestParam(defaultValue = "10") int pageSize){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();
        param.put("enterprises", enterprises);
        param.put("hatchbacks", hatchbacks);
        param.put("vins", vins);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("pageNow", pageNow);
        param.put("pageSize", pageSize);
        logger.info(String.format("查询全量数据详情，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }

            String[] enterprise = (enterprises != null) ?
                    enterprises.split(",") : null;
            String[] hatchback = (hatchbacks != null) ?
                    hatchbacks.split(",") : null;
            String[] vin = (vins != null) ?
                    vins.split(",") : null;


            int from = (pageNow - 1) * pageSize;
            if(from + pageSize >= 10000){
                from = 10000 - pageSize;
            }
            Map<String, Object> map = evsAllDataElasticSearchService.searchData(enterprise,
                    hatchback, vin, columnSet, startTime, endTime, from, pageSize);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询全量数据详情失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询固定时间范围里指定车辆的抽样行驶轨迹", notes = "gcj02: 高德坐标； bd09： 百度坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vin", value = "车辆VIN", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始日期时间戳", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束日期时间戳", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stepLength", value = "步长", dataType = "int", defaultValue = "5", paramType = "query"),
            @ApiImplicitParam(name = "posType", value = "坐标转换类型", dataType = "string", defaultValue = "gcj02",
                    paramType = "query", allowableValues = "gcj02,bd09")
    })
    @RequestMapping(value = "samplingTrackLocations", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchSamplingTrackLocations(@RequestParam String vin,
                                               @RequestParam Long startTime,
                                               @RequestParam Long endTime,
                                               @RequestParam(defaultValue = "5") Integer stepLength,
                                               @RequestParam(defaultValue = "gcj02") String posType){

        //查询日志
        Map<String, Object> param = Maps.newHashMap();

        param.put("vin", vin);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("stepLength", stepLength);
        param.put("posType", posType);
        logger.info(String.format("查询固定时间范围里指定车辆的抽样行驶轨迹，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {

            List<Map<String, Object>> list = evsAllDataElasticSearchService
                    .searchSamplingTrackLocations(vin, startTime, endTime, stepLength, posType);
            res = ResponseUtil.success(list.size(), list);
        } catch (Exception e) {
            logger.error("查询固定时间范围里指定车辆的抽样行驶轨迹失败。", e);
            res = ResponseUtil.error(e);
        }

        return res;
    }

    @ApiOperation(value = "查询指定车辆的指定索引数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "数据索引", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "数据id", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "columns", value = "返回数据字段列表，英文逗号分隔",
                    dataType = "string", paramType = "query")
    })
    @RequestMapping(value = "dataByIndexId", method = {RequestMethod.GET})
    @ResponseBody
    public Object getDataByIndexId(@RequestParam String index,
                                     @RequestParam String id,
                                     String columns){

        Map<String, Object> param = Maps.newHashMap();
        param.put("index", index);
        param.put("id", id);
        param.put("columns", columns);
        logger.info(String.format("查询指定车辆的指定索引数据，参数如下：\n\t %s", param));

        Map<String, Object> res;
        try {
            Set<String> columnSet = null;
            if(columns != null){
                columnSet = Sets.newHashSet(columns.split(","));
            }
            Map<String, Object> map = evsAllDataElasticSearchService.getDataByIndexId(index, id, columnSet);
            res = ResponseUtil.success(map);
        } catch (Exception e) {
            logger.error("查询指定车辆的指定索引数据失败。", e);
            res = ResponseUtil.error(e.toString());
        }

        return res;
    }


}
