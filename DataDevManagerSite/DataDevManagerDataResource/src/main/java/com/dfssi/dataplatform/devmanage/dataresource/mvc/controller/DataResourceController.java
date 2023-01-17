package com.dfssi.dataplatform.devmanage.dataresource.mvc.controller;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseController;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.entity.DataResourceEntity;
import com.dfssi.dataplatform.devmanage.dataresource.mvc.service.IDataResourceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/devmanage/")
public class DataResourceController extends BaseController {

    @Autowired
    private IDataResourceService dataResourceService;

//    @ResponseBody
//    @RequestMapping(value = "dataresource", method = RequestMethod.POST)
//    public String saveSink(HttpServletRequest req, HttpServletResponse response, DataResourceEntity entity, String request) {
//        logger.info(request);
//        dataResourceService.saveEntity(entity);
//        return RETURN_TAG_SUCCESS;
//    }

//    @ResponseBody
//    @RequestMapping(value = "dataresource/{id}", method = RequestMethod.DELETE)
//    public String deleteModel(HttpServletRequest req, HttpServletResponse response,DataResourceEntity entity, @PathVariable String
//            id) {
//        dataResourceService.deleteEntity(entity);
//
//        return RETURN_TAG_SUCCESS;
//    }

   @ResponseBody
    @RequestMapping(value = "dataresource/list", method = RequestMethod.GET)
    public Object findByPage(HttpServletRequest req, HttpServletResponse response, DataResourceEntity entity) {
       Object result=null;
       Page<?> page = PageHelper.startPage(entity.getCurPage(),entity.getPageLength());
       List<DataResourceEntity> list = dataResourceService.findEntityList(entity);
       result=new PageInfo<>(list);

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "dataresource/shareresource", method = RequestMethod.GET)
    public Map<String, Object> shareResource(HttpServletRequest req, HttpServletResponse response, DataResourceEntity entity) {

        return dataResourceService.shareResource(entity);
    }
}
