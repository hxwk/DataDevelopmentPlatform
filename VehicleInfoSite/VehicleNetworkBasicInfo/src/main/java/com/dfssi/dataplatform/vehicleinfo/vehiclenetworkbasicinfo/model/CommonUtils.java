package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model;

import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.BaseRourse;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 公共工具类
 * Created by yanghs on 2018/4/11.
 */
public class CommonUtils {

    /**
     * 一、数字和字母的数值
     * 阿拉伯数字指定值为实际数字，罗马字母数值如下：
     * A B C D E F G H J K L M N P R S T U V W X Y Z
     * 1 2 3 4 5 6 7 8 1 2 3 4 5 7 9 2 3 4 5 6 7 8 9
     */
    private static Map<Character, Integer> vinMapValue = new HashMap<Character, Integer>();
    /**
     * 二、位置加权系数
     * 1 2 3 4 5 6 7 8  9 10 11 12 13 14 15 16 17
     * 8 7 6 5 4 3 2 10 0 9  8  7  6  5  4  3  2
     */
    private static Map<Integer, Integer> vinMapWeighting = new HashMap<Integer, Integer>();

    static {
        vinMapValue.put('0', 0);
        vinMapValue.put('1', 1);
        vinMapValue.put('2', 2);
        vinMapValue.put('3', 3);
        vinMapValue.put('4', 4);
        vinMapValue.put('5', 5);
        vinMapValue.put('6', 6);
        vinMapValue.put('7', 7);
        vinMapValue.put('8', 8);
        vinMapValue.put('9', 9);
        vinMapValue.put('A', 1);
        vinMapValue.put('B', 2);
        vinMapValue.put('C', 3);
        vinMapValue.put('D', 4);
        vinMapValue.put('E', 5);
        vinMapValue.put('F', 6);
        vinMapValue.put('G', 7);
        vinMapValue.put('H', 8);
        vinMapValue.put('J', 1);
        vinMapValue.put('K', 2);
        vinMapValue.put('L', 3);
        vinMapValue.put('M', 4);
        vinMapValue.put('N', 5);
        vinMapValue.put('P', 7);
        vinMapValue.put('R', 9);
        vinMapValue.put('S', 2);
        vinMapValue.put('T', 3);
        vinMapValue.put('U', 4);
        vinMapValue.put('V', 5);
        vinMapValue.put('W', 6);
        vinMapValue.put('X', 7);
        vinMapValue.put('Y', 8);
        vinMapValue.put('Z', 9);

        vinMapWeighting.put(1, 8);
        vinMapWeighting.put(2, 7);
        vinMapWeighting.put(3, 6);
        vinMapWeighting.put(4, 5);
        vinMapWeighting.put(5, 4);
        vinMapWeighting.put(6, 3);
        vinMapWeighting.put(7, 2);
        vinMapWeighting.put(8, 10);
        vinMapWeighting.put(9, 0);
        vinMapWeighting.put(10, 9);
        vinMapWeighting.put(11, 8);
        vinMapWeighting.put(12, 7);
        vinMapWeighting.put(13, 6);
        vinMapWeighting.put(14, 5);
        vinMapWeighting.put(15, 4);
        vinMapWeighting.put(16, 3);
        vinMapWeighting.put(17, 2);
    }

    /**
     * vin码检测
     * 车辆识别代号应由三个部分组成：
     * 第一部分（1～3位）是世界制造厂识别代号（WMI）；
     * 第二部分（4～9位）是车辆说明部分（VDS）；
     * 第三部分（10～17位）是车辆指示部分（VIS）。
     * 世界制造厂识别代号：用以标示车辆的制造厂，如中国一汽大众为LFV。如果某制造厂的年产量少于500辆，其识别代码的第三个字码就是9。
     * 车辆说明部分：提供说明车辆一般特性的资料。第4～8位表示车辆特征；第9位为校验位，通过一定的算法防止输入错误。
     * 车辆指示部分：制造厂为区别不同车辆而指定的一组字码。这组字码连同VDS部分一起，足以保证每个制造厂在30年之内生产的每辆车辆的识别代号具有唯一性。
     * 第10位为车型年份，即厂家规定的型年（ModelYear），不一定是实际生产的年份，但一般与实际生产的年份之差不超过1年；
     * 第11位为装配厂；第12～17位：顺序号。
     *
     * @param vin
     * @return
     */
    public static boolean checkVIN(String vin) {
        boolean reultFlag = false;
        //排除字母O、I、Q
        if (vin == null || vin.indexOf("O") >= 0 || vin.indexOf("I") >= 0 || vin.indexOf("Q") >= 0
                || vin.indexOf("o") >= 0 || vin.indexOf("i") >= 0 || vin.indexOf("q") >= 0) {
            reultFlag = false;
        } else {
            //1:长度为17
            if (vin.length() == 17) {
                char[] vinArr = vin.toCharArray();
                int amount = 0;
                for (int i = 0; i < vinArr.length; i++) {
                    int temp = (int) vinArr[i];
                    if (!((temp > 64 && temp < 91) ||(temp > 47 && temp < 58)) ) {
                        return reultFlag;
                    }
                    //VIN码从从第一位开始，码数字的对应值×该位的加权值，计算全部17位的乘积值相加
                    amount += vinMapValue.get(vinArr[i])
                            * vinMapWeighting.get(i + 1);
                }
                //三、用VIN码各位字母的数值乘以位置加权系数，总和除以11，余数则为检验数。当余数为10时，检验数为X
                if (amount % 11 == vinMapValue.get(vinArr[8])) {
                    reultFlag = true;
                } else {
                    reultFlag = true;
                }
            }
        }
        return reultFlag;
    }

    /**
     * IccId校验
     * @param iccId
     * @return
     */
    public static boolean checkIccId(String iccId){
        boolean returnFlag = true;
        if(iccId==null||iccId.length()!=20){
            return false;
        }
        char[] iccIdArr = iccId.toCharArray();
        for (int i = 0; i < iccIdArr.length; i++) {
            int temp = (int) iccIdArr[i];
            if ((temp > 47 && temp < 58) ||(temp > 64 && temp < 91)||(temp > 96 && temp < 123)) {
                continue;
            }
            returnFlag=false;
            break;
        }
        return returnFlag;
    }




    /**
     * 获取当前日期时间
     * @param format
     * @return
     */
    public static String getCurrentDateTime(String format) {
        LocalDateTime now = LocalDateTime.now();
        if(format==null||"".equals(format)){
            format="yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter dateToStrFormatter = DateTimeFormatter.ofPattern(format);
        String dateStr = dateToStrFormatter.format(now);
        return dateStr;
    }

    /**
     * 截取日期时间中日期部分
     * @param str
     * @return
     */
    public static String getDateFromString(String str){
        if(str==null||str.length()<10){
            return "";
        }
        return str.substring(0,10);
    }

    /**
     * 获取公共日期查询条件语句
     * @param baseRourse
     * @param queryString
     */
    public static void getCommonDateSql(BaseRourse baseRourse,StringBuffer queryString){
        if (StringUtils.isNotEmpty(baseRourse.getCreateTime())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" createTime like '");
            queryString.append(CommonUtils.getDateFromString(baseRourse.getCreateTime()));
            queryString.append("%' ");
        }

        if (StringUtils.isNotEmpty(baseRourse.getUpdateTime())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" updateTime like '");
            queryString.append(CommonUtils.getDateFromString(baseRourse.getUpdateTime()));
            queryString.append("%' ");
        }

        if (StringUtils.isNotEmpty(baseRourse.getCreateUser())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" createUser='");
            queryString.append(baseRourse.getCreateUser());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(baseRourse.getUpdateUser())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" updateUser='");
            queryString.append(baseRourse.getUpdateUser());
            queryString.append("' ");
        }
    }

    /**
     * 字符串转换成指定格式日期验证
     * @param date
     * @return
     */
    public static boolean covertDate(String date){
        boolean returnFlag = false;
        if (date==null||date.length()!=19){
            return returnFlag;
        }
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        try {
            sdf.parse(date);
            returnFlag=true;
        } catch (ParseException e) {

        }
        return returnFlag;
    }

    /**
     * 查询时间范围内的日期
     * @param dBegin
     * @param dEnd
     * @return
     */
    public static List<String> findDates(Date dBegin, Date dEnd, String format)
    {
        List lDate = new ArrayList();
        if(format==null||"".equals(format)){
            format="yyyyMMdd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        lDate.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(sdf.format(calBegin.getTime()));
        }
        return lDate;
    }


    /**
     * null 转空字符串
     * @param str
     * @return
     */
    public static String object2String(Object str){
        return str==null?"":String.valueOf(str);
    }



    public static final void main(String[] args){
        System.out.println(checkVIN("LGA000000000YPT03"));


    }
}
