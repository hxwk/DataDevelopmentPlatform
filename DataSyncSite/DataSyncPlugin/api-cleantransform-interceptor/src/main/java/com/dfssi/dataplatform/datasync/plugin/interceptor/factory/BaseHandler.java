package com.dfssi.dataplatform.datasync.plugin.interceptor.factory;

import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;

/**
 * @author JianKang
 * @date 2018/5/9
 * @description base event body Object change abstract class
 */
public abstract class BaseHandler {
    protected static final String TYPE_KEY = "type";
    /**带因子和偏移量*/
    protected static final String FACTOR_KEY = "factor";
    protected static final String OFFSET_KEY = "offset";
    /**查找和替换*/
    protected static final String SEARCHPATTERN_KEY = "searchPattern";
    protected static final String REPLACESTRING_KEY = "replaceString";
    /**脱敏替换*/
    protected static final String MASKSTART = "maskStart";
    protected static final String MASKEND = "maskEnd";

    protected String type;

    /**
     *getBody 清洗转换成新的列文本
     */
    public abstract String columnCleanTransform(String srcText,MapperRule mapperRule);

}
