package com.dfssi.dataplatform.devmanager.analysistask.mvc.controller;

import com.dfssi.dataplatform.devmanager.analysistask.mvc.base.BaseController;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisApplicationFormEntity;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.entity.AnalysisModelEntity;
import com.dfssi.dataplatform.devmanager.analysistask.mvc.service.IAnalysisManagerTaskService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/analysistask/")
public class AnalysisManagerController extends BaseController {

    @Autowired
    private IAnalysisManagerTaskService analysisTaskManagerService;

    @ResponseBody
    @RequestMapping(value = "analysis/list", method = RequestMethod.GET)
    public Object findByPage(HttpServletRequest req, HttpServletResponse response, AnalysisModelEntity entity) {
       Object result=null;
       Page<?> page = PageHelper.startPage(entity.getCurPage(),entity.getPageLength());
       List<AnalysisModelEntity> list = analysisTaskManagerService.findEntityList(entity);
       result = new PageInfo<>(list);

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "analysis/verifytask", method = RequestMethod.POST)
    public Map<String, Object> verifyTask(HttpServletRequest req, HttpServletResponse response, AnalysisApplicationFormEntity entity) {
        return analysisTaskManagerService.verifyTask(entity);
    }

    @ResponseBody
    @RequestMapping(value = "analysis/applicationform/", method = RequestMethod.GET)
    public Map<String, Object> getApplicationformById(HttpServletRequest req, HttpServletResponse response, String id) {
        return analysisTaskManagerService.getApplicationformById(id);
    }
}
