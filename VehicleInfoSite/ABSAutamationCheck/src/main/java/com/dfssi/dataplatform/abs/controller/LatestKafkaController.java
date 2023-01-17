package com.dfssi.dataplatform.abs.controller;

import com.dfssi.dataplatform.abs.service.LatestDataService;
import com.dfssi.dataplatform.abs.utils.ResponseObj;
import com.dfssi.dataplatform.abs.utils.ResponseUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/29 15:23
 */
@Controller
@RequestMapping(value = "latest/")
@Api(value = "LatestKafkaController", description = "最新数据接口")
@Slf4j
public class LatestKafkaController {

    @Autowired
    private LatestDataService latestDataService;

    @ResponseBody
    @ApiImplicitParam(name = "vid", value = "车辆vid,多个则用英文逗号分隔", dataType = "string", paramType = "query")
    @RequestMapping(value = "latestRecord", method = RequestMethod.GET)
    @ApiOperation(value = "最新数据")
    public Object getVendorTestInfo(String vid) {
        log.info(String.format("获取%s的最新数据", vid));
        ResponseObj responseObj = null;
        try {

            List<String> vids = Lists.newArrayList();
            if(vid != null){
                vids = Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(vid));
            }

            Object object = latestDataService.latest(vids);
            responseObj = ResponseUtil.success(object);

        } catch (Exception e) {
            log.error("获取最新数据失败。", e);
            responseObj = ResponseUtil.error(e.getMessage());
        }

        return responseObj;
    }
}
