package com.dfssi.dataplatform.ide.datasource.mvc.restful;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DicItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SourceDicItemHystrix implements ISourceDicItemFeign {
@Override
    public List<DicItemEntity> listGetValidSource(){
        return null;
    }
}
