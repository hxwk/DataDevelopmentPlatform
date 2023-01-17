package com.dfssi.dataplatform.datasync.plugin.source.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * generate five digit file name 2017112113097
 * @author jianKang
 * @date 2017/11/21
 */
public class RandomSequenceUtil {
    /**
     * generate five digit file name：
     * @return random sequence by current_time and random
     */
    public static String getRandomFileName() {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        Random random = new Random();
        /**
         * generate five digite number
         */
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;

        return str + rannum;
    }

    public static void main(String[] args) {
        RandomSequenceUtil.getRandomFileName();
    }
}
