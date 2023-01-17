package com.dfssi.dataplatform.analysis.service.controller.jasper;

import com.dfssi.dataplatform.analysis.common.util.ResponseUtils;
import com.dfssi.dataplatform.analysis.service.entity.ChartPublishEntity;
import com.dfssi.dataplatform.analysis.service.entity.jasper.VoJasperParam;
import com.dfssi.dataplatform.analysis.service.service.ChartPublishService;
import com.dfssi.dataplatform.analysis.service.service.jasper.AbstractJasperReportDesigner;
import com.dfssi.dataplatform.analysis.service.service.jasper.JasperDesignerFactory;
import io.swagger.annotations.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description jasper report报表接口
 * @Author zhangcheng
 * @Date 2018/9/28 16:09
 */
@RestController
@Api(tags = {"jasper report报表控制器"})
public class JasperReportCustomController {
    /**
     * 日志
     */
    private final static Logger logger = LoggerFactory.getLogger(JasperReportCustomController.class);
    @Autowired
    private ChartPublishService chartPublishService;

    /**
     * 预览接口
     *
     * @param voJasperParam
     * @return 处理状态、报表数据等
     */
    @PostMapping("jasper/preview")
    @ApiOperation(value = "报表预览接口")
    public Map<String, Object> preview(@ApiParam @RequestBody VoJasperParam voJasperParam) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", -1);
        result.put("status", "fail");
        if (null == voJasperParam) {
            result.put("message", "报表参数不能为空");
            return result;
        }
        try {
            String chartType = voJasperParam.getChartType();
            AbstractJasperReportDesigner jasperDesigner = JasperDesignerFactory.createJasperDesigner(chartType);
            String htmlStr = jasperDesigner.renderHtmlStr(voJasperParam);
            if (StringUtils.isBlank(htmlStr)) {
                return result;
            }
            Document doc = Jsoup.parse(htmlStr);
            Elements img = doc.select("img");
            if (img != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", img.outerHtml());
                return result;
            }
        } catch (JRException e) {
            result.put("message", e.getMessage());
            logger.error("jasper report创建失败，{}", e.getMessage(), e);
        } catch (SQLException e) {
            result.put("message", e.getMessage());
            logger.error("sql异常，{}", e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            result.put("message", e.getMessage());
            logger.error("class not found exception，{}", e.getMessage(), e);
        } catch (IOException e) {
            result.put("message", e.getMessage());
            logger.error("IO异常，{}", e.getMessage(), e);
        }
        return result;
    }

    @PostMapping("jasper/publish")
    @ApiOperation(value = "报表发布接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportParam", value = "报表参数实体的json串", required = true),
            @ApiImplicitParam(name = "reportContent", value = "报表内容", required = true),
            @ApiImplicitParam(name = "serialNum", value = "报表的排列序号，按数字先后顺序排列")
    })
    public Map<String, Object> publishChart(String reportParam, String reportContent, int serialNum) {
        ChartPublishEntity chartPublishEntity = new ChartPublishEntity();
        chartPublishEntity.setReportParam(reportParam);
        chartPublishEntity.setReportContent(reportContent);
        chartPublishEntity.setSerialNum(serialNum);
        chartPublishEntity.setPublishTime(new Date());
        if (chartPublishService.insert(chartPublishEntity) > 0) {
            return ResponseUtils.buildSuccessResult("发布成功");
        }
        return ResponseUtils.buildFailResult("发布失败");
    }

    @PostMapping("jasper/update")
    @ApiOperation(value = "报表重新发布或修改接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "报表id"),
            @ApiImplicitParam(name = "reportParam", value = "报表参数实体的json串", required = true),
            @ApiImplicitParam(name = "reportContent", value = "报表内容", required = true),
            @ApiImplicitParam(name = "serialNum", value = "报表的排列序号，按数字先后顺序排列")
    })
    public Map<String, Object> updateChart(long id, String reportParam, String reportContent, Integer serialNum) {
        ChartPublishEntity chartPublishEntity = chartPublishService.get(id);
        if (chartPublishEntity == null) {
            return ResponseUtils.buildFailResult("该记录不存在");
        }
        chartPublishEntity.setReportParam(reportParam);
        chartPublishEntity.setReportContent(reportContent);
        chartPublishEntity.setSerialNum(serialNum);
        chartPublishEntity.setPublishTime(new Date());
        if (chartPublishService.update(chartPublishEntity) > 0) {
            return ResponseUtils.buildSuccessResult("更新成功");
        }
        return ResponseUtils.buildFailResult("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除已发布的图表")
    @ApiImplicitParam(name = "id", value = "已发布的图表id")
    public Map<String, Object> delete(@PathVariable long id) {
        if (chartPublishService.delete(id) > 0) {
            return ResponseUtils.buildSuccessResult("删除成功");
        }
        return ResponseUtils.buildFailResult("删除失败");
    }

    @GetMapping("/jasper/findAll")
    @ApiOperation(value = "查询已发布的图表接口")
    public Map<String, Object> findList() {
        return ResponseUtils.buildSuccessResult("", chartPublishService.findAllList());
    }
}
