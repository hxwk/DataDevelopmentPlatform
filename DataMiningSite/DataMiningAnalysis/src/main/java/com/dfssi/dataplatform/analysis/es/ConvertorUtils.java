package com.dfssi.dataplatform.analysis.es;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * 转换器工具
 * @author pengwk
 * @version 2018-4-12 13:34:46
 */
public class ConvertorUtils {
    public final Logger logger = LogManager.getLogger(ConvertorUtils.class);

    private ConvertorUtils(){}

    public static Double KeepNDecimal(Double a, int b){
        BigDecimal bigDecimal = new BigDecimal(a);
        Double a1 = bigDecimal.setScale(b, BigDecimal.ROUND_HALF_UP).doubleValue();
        return a1;
    }
}
