package com.dfssi.dataplatform.datasync.plugin.sqlsource.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * util tools
 * @author jianKang
 * @date 2017/12/15
 */
public class UtilTools {

    private static final String delimiter = ",";
    /**
     * 列表转字符串
     * @param lists
     * @return 字符串
     */
    public static String list2String(List<String> lists){
        Preconditions.checkNotNull(lists,"List can not be null");
        StringBuilder stringBuilder = new StringBuilder();
        for(String word : lists){
            stringBuilder.append(word);
            stringBuilder.append(delimiter);
        }
        return stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
    }

}
