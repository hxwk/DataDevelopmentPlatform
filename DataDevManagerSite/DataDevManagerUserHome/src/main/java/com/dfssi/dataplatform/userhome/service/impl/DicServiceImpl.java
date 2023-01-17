package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.Entity.DicEntity;
import com.dfssi.dataplatform.userhome.dao.DicDao;
import com.dfssi.dataplatform.userhome.service.DicService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DicServiceImpl implements DicService {
    @Autowired
    private DicDao dicDao;

    @Override
    public PageResult<DicEntity> listGetDic(PageParam pageParam) {
        Page<DicEntity> page= PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize());
        List<DicEntity> list=dicDao.listGetDic();
        PageResult<DicEntity> pageResult=new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    public String insert(DicEntity dicEntity) {
        String result="";
        try {
            int countId=dicDao.countByDicType(dicEntity.getId());
            if(countId==0) {
                //新增
                int count=dicDao.countByDicType(dicEntity.getDicType());
                if (count != 0) {
                    result = "字典类型已存在";
                } else {
                    dicDao.insert(dicEntity);
                }
            }else {
                //修改
                dicDao.insert(dicEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    public String deleteById(String id) {
        String result="";
        return result;
    }

    @Override
    public String changeValid(DicEntity dicEntity) {
        String result="";
        return result;
    }
}
