package com.dfssi.dataplatform.datasync.plugin.interceptor.factory;

import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class MaskHandler extends BaseHandler {
    static final Logger logger = LoggerFactory.getLogger(MaskHandler.class);
    @Override
    public String columnCleanTransform(String srcText, MapperRule mapperRule) {
        StringBuilder stringBuilder = new StringBuilder();
        Integer maskStartIdx = Integer.parseInt(mapperRule.getMaskStart());
        Integer maskEndIdx = Integer.parseInt(mapperRule.getMaskEnd());
        String insertString = mapperRule.getInsertString();
        stringBuilder.append(srcText).replace(maskStartIdx,maskEndIdx,insertString);

        logger.debug("srcText:{},destText:{}",srcText,stringBuilder.toString());

        return stringBuilder.toString();
    }
}
