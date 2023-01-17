package com.yaxon.vn.nd.ne.tas.exception;

import com.yaxon.vn.nd.ne.tas.common.NEStateConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JianKang
 * @date 2018/2/23
 * @description
 */
public class ParseException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(ParseException.class);
    private static final String delimiter = "#";
    private static final int ABORMAL1 = 254;
    private static final int ABORMAL2 = 65534;
    private static final long ABORMAL3 = 4294967294L;
    private static final int INVALID1 = 255;
    private static final int INVALID2 = 65535;
    private static final long INVALID3 = 4294967295L;
    private static final String ZERO = "0";
    /**
     * 数据异常处理
     * @param code
     * @param dataField
     */
    public ParseException(long code, String dataField) {
        String abnormal = dataField + delimiter + NEStateConstant.ABNORMAL;
        String invalid = dataField + delimiter + NEStateConstant.INVALID;
        if (ABORMAL1 == code || ABORMAL2 == code || ABORMAL3 == code) {
            printExceptionMsg(abnormal);
        } else if (INVALID1 == code || INVALID2 == code || INVALID3 == code) {
            printExceptionMsg(invalid);
        } else if(0 == code){
            printExceptionMsg(ZERO);
        }
    }
    /**
     * 打印异常信息
     */
    void printExceptionMsg(String msg){
        logger.debug(msg);
        System.out.println(msg);
    }
}
