package com.dfssi.dataplatform.ide.dataresource.mvc.restful;

import com.dfssi.dataplatform.ide.dataresource.mvc.entity.DicItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResourceDicItemHystrix implements IResourceDicItemFeign {
@Override
    public List<DicItemEntity> listGetValidResource(){
        return null;
    }
}
