package com.dfssi.dataplatform.datasync.plugin.interceptor.factory;

import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description
 */
public class SearchAndReplaceHandler extends BaseHandler {
    static final Logger logger = LoggerFactory.getLogger(SearchAndReplaceHandler.class);
    @Override
    public String columnCleanTransform(String origBody, MapperRule mapperRule) {
        String searchPattern = mapperRule.getSearchPattern();
        String replaceString = mapperRule.getReplaceString();
        Pattern searchRegex = Pattern.compile(searchPattern);
        Matcher matcher = searchRegex.matcher(origBody);

        String destText = matcher.replaceAll(replaceString);

        logger.debug("srcText:{},destText:{}",origBody,destText);

        return destText;
    }
}
