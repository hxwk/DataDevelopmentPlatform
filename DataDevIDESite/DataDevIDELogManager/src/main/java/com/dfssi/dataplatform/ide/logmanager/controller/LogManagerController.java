package com.dfssi.dataplatform.ide.logmanager.controller;

import com.dfssi.common.net.ResponseHelper;
import com.dfssi.dataplatform.ide.logmanager.model.LogManagerModels;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/8 13:37
 */
@Api(tags = {"日志管理控制器"})
@RestController
@RequestMapping(value="/logmanager/")
public class LogManagerController {
    private final Logger logger = LogManager.getLogger(LogManagerController.class);

    @Autowired
    private LogManagerModels logManagerModels;

    @ApiOperation(value = "列举日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "Integer", defaultValue="1", paramType = "get"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "Integer", defaultValue="10", paramType = "get")
    })
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    public Object list(@RequestParam(defaultValue = "1") int pagenum,
                       @RequestParam(defaultValue = "10")int pagesize){
        String error;
        try {
            Map<String, Object> res =
                    logManagerModels.findAll(pagenum, pagesize);
            return ResponseHelper.success(res);
        } catch (Exception e) {
            error = e.getMessage();
            logger.error("列举日志信息失败。", e);
        }
        return ResponseHelper.error(error);
    }

    @ApiOperation(value = "根据条件查询日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String", paramType = "get"),
            @ApiImplicitParam(name = "level", value = "日志级别", dataType = "String", paramType = "get"),
            @ApiImplicitParam(name = "pagenum", value = "页码, 默认1", dataType = "Integer", defaultValue="1", paramType = "get"),
            @ApiImplicitParam(name = "pagesize", value = "页记录数, 默认10", dataType = "Integer", defaultValue="10", paramType = "get")
    })
    @RequestMapping(value = "findlog", method = {RequestMethod.POST})
    @ResponseBody
    public Object findByCondition(@ApiParam(hidden = true) @RequestBody Map<String, String> param){
        String error;
        try {
            StringBuilder sb = new StringBuilder("条件查询日志信息, 参数如下：\n\t");
            sb.append("word = ").append(param.get("word")).append(", ");
            sb.append("level = ").append(param.get("level")).append(", ");

            String pagenumStr = param.get("pagenum");
            int pagenum = pagenumStr == null ? 1 : Integer.parseInt(pagenumStr);

            String pagesizeStr = param.get("pagesize");
            int pagesize = pagesizeStr == null ? 10 : Integer.parseInt(pagesizeStr);

            sb.append("pagenum = ").append(pagenum).append(", ");
            sb.append("pagesize = ").append(pagesize);
            logger.info(sb.toString());

            Map<String, Object> condition =
                    logManagerModels.findByCondition(param.get("word"),
                            param.get("level"), pagenum, pagesize);
            return ResponseHelper.success(condition);
        } catch (Exception e) {
            error = e.getMessage();
            logger.error("条件查询日志信息失败。", e);
        }

        return ResponseHelper.error(error);
    }
}
