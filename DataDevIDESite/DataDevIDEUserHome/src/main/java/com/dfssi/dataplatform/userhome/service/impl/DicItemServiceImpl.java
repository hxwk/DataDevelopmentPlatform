package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.cloud.common.entity.PageParam;
import com.dfssi.dataplatform.cloud.common.entity.PageResult;
import com.dfssi.dataplatform.userhome.dao.DicItemDao;
import com.dfssi.dataplatform.userhome.entity.DicEntity;
import com.dfssi.dataplatform.userhome.entity.DicItemEntity;
import com.dfssi.dataplatform.userhome.service.DicItemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DicItemServiceImpl implements DicItemService{
    @Autowired
    private DicItemDao dicItemDao;

    @Override
    public PageResult<DicItemEntity> listGetDicItem(PageParam pageParam,DicItemEntity entity) {
        Page<DicItemEntity> page= PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize());
        List<DicItemEntity> list=dicItemDao.listGetDicItem(entity);
        PageResult<DicItemEntity> pageResult=new PageResult<>();
        pageResult.setRows(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    public String insert(DicItemEntity entity) {
        String result="";
            int countId=dicItemDao.countById(entity.getId());
            if(countId==0){
                //新增
                int count=dicItemDao.countByItemName(entity);
                if (count != 0) {
                    result = "名称重复";
                } else {
                    dicItemDao.insert(entity);
                }
            }else{
                //修改
                int countUpdate=dicItemDao.countByUpdate(entity);
                if (countUpdate != 0) {
                    result = "与同类型里的其他名称冲突";
                } else {
                    dicItemDao.insert(entity);
                }
            }
        return result;
    }

    @Override
    public String deleteByIds(String ids) {
        String result="";
        try {
            String[] recordIDArray = ids.split(",");
            List<String> recordIDList = new ArrayList<String>();
            for(int i=0;i<recordIDArray.length;i++){
                recordIDList.add(recordIDArray[i]);
            }
            dicItemDao.deleteById(recordIDList);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    public String changeValid(DicItemEntity entity) {
        String result="";
        try {
            dicItemDao.changeValid(entity);
        } catch (Exception e) {
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    @Override
    public List<DicItemEntity> listGetByDicType(String dicType) {
        return dicItemDao.listGetByDicType(dicType);
    }

    @Override
    public int deleteByType(String dicType) {
        return dicItemDao.deleteByType(dicType);
    }

    @Override
    public int updateByType(DicEntity entity) {
        return dicItemDao.updateByType(entity);
    }

    @Override
    public List<DicItemEntity> listGetValidSource() {
        return dicItemDao.listGetValidSource();
    }

    @Override
    public List<DicItemEntity> listGetValidResource() {
        return dicItemDao.listGetValidResource();
    }

    @Override
    public List<DicItemEntity> listGetValidDataItem() {
        return dicItemDao.listGetValidDataItem();
    }

    @Override
    public List<DicItemEntity> listSelectDataItem(String dicType) {
        return dicItemDao.listSelectDataItem(dicType);
    }

    @Override
    public List<DicItemEntity> listSelectSystem() {
        return dicItemDao.listSelectSystem();
    }
}
