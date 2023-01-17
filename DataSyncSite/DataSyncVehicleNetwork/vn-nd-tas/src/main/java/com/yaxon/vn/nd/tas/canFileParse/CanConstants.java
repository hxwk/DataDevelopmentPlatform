package com.yaxon.vn.nd.tas.canFileParse;

import com.yaxon.vn.nd.tbp.si.CanMessageBean;
import com.yaxon.vn.nd.tbp.si.CanSignalBean;

import java.util.List;
import java.util.Map;

/**
 * @author JianKang
 * @date 2018-02-07
 * @description can constants
 */
public class CanConstants {
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String AT = "@";
    public static final String DBCFORMAT ="dbcFormat";
    public static final String CHARSET = "charset";
    public static final String DBCFILE = "dbcfile";
    //very important store dbc parse object
    public static Map<CanMessageBean, List<CanSignalBean>> t38Msg = null;

}
