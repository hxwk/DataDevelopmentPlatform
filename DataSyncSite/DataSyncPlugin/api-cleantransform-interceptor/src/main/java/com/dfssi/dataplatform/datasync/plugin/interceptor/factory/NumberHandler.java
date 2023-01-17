package com.dfssi.dataplatform.datasync.plugin.interceptor.factory;

import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class NumberHandler extends BaseHandler {
    static final Logger logger = LoggerFactory.getLogger(NumberHandler.class);
    @Override
    public String columnCleanTransform(String srcText, MapperRule mapperRule) {
        DecimalFormat df = new DecimalFormat("#######0.0#");
        Double oriNum = Double.parseDouble(srcText);
        Double factor = Double.parseDouble(mapperRule.getFactor());
        Double offset = Double.parseDouble(mapperRule.getOffset());
        String result = df.format(oriNum * factor + offset);

        logger.debug("srcText:{},destText:{}",srcText,result);
        return String.valueOf(result);
    }
}
