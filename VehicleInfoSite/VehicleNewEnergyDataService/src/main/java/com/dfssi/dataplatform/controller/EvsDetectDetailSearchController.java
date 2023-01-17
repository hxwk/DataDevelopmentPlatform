package com.dfssi.dataplatform.controller;

import com.dfssi.dataplatform.cache.database.EvsDetectDetailCache;
import com.dfssi.dataplatform.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/17 19:35
 */
@Api(tags = {"新能源数据质量规则查询控制器"})
@RestController
@RequestMapping(value="/detect/rules/")
public class EvsDetectDetailSearchController {
    private final Logger logger = LoggerFactory.getLogger(EvsDetectDetailSearchController.class);

    @Autowired
    private EvsDetectDetailCache evsDetectDetailCache;


    @ApiOperation(value = "查询所有基础国标检测规则" ,
            notes="detect_type:检测类型，1：范围(包含边界) 2：枚举，\n null_able：是否可空 1为true， 0为false, \n id: 前两位为九项数据标识 \n" +
                    "数据项标识：00-整车数据，01-驱动电机数据，02-燃料电池，03-发动机数据 04-车辆位置数据，05-极值数据，06-报警数据, 07-可充电储能装置温度数据,08-可充电储能装置电压数据")
    @RequestMapping(value = "baseRules", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryBaseRules(){
        logger.info("查询所有基础国标检测规则。");

        Map<String, Object> res;
        List<Map<String, Object>> rules;
        try {
            rules = evsDetectDetailCache.getBaseEvsDetectRules();
            res = ResponseUtil.success(rules.size(), rules);
        } catch (Exception e) {
            logger.error("查询所有基础国标检测规则失败。", e);
            res = ResponseUtil.error(e);
        }
        return res;
    }
}
