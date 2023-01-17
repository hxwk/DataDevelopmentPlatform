package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.dao.DicDao;
import com.dfssi.dataplatform.userhome.entity.DicEntity;
import com.dfssi.dataplatform.userhome.service.DicItemService;
import com.dfssi.dataplatform.userhome.service.DicService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private DicDao dicDao;

    @Autowired
    private DicItemService dicItemService;

    @Override
    public PageResult<DicEntity> listGetDic(PageParam pageParam,DicEntity entity) {
        Page<DicEntity> page= PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize());
        List<DicEntity> list=dicDao.listGetDic(entity);
        PageResult<DicEntity> pageResult=new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    public String insert(DicEntity dicEntity) {
        String result="";
            int countId=dicDao.countById(dicEntity.getId());
            if(countId==0) {
                //新增
                int count=dicDao.countByDicType(dicEntity.getDicType());
                if (count != 0) {
                    result = "字典类型已存在";
                } else {
                    dicDao.set0();
                    dicDao.insert(dicEntity);
                    dicDao.set1();
                }
            }else {
                //修改
                int countUpdate=dicDao.countByUpdate(dicEntity);
                if (countUpdate != 0) {
                    result = "与其他类型名称冲突";
                } else {
                    dicDao.set0();
                    dicItemService.updateByType(dicEntity);
                    dicDao.insert(dicEntity);
                    dicDao.set1();
                }
            }
        return result;
    }

    @Override
    public String deleteByType(String dicType) {
        String result="";
            dicDao.set0();
            dicDao.deleteByType(dicType);
            dicItemService.deleteByType(dicType);
            dicDao.set1();
        return result;
    }

    @Override
    public String changeValid(DicEntity dicEntity) {
        String result="";
        try {
            dicDao.changeValid(dicEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    public List<DicEntity> listGetDicType() {

        return dicDao.listGetDicType();
    }

}
