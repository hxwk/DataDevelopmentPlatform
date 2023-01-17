package com.dfssi.dataplatform.datasync.plugin.source.common;

import com.google.common.collect.Lists;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Parsing a string for the collection of web information tools,
 * filter crawler content by rules
 * @author jianKang
 * @date 2017/11/29
 */
public class UtilTools {
    static final Logger logger = LoggerFactory.getLogger(UtilTools.class);
    static InputStream fis= null;
    static Calendar c ;
    static SimpleDateFormat f;
    static final String one_cross = "-";

    public UtilTools() {
        fis = UtilTools.class.getClassLoader().getResourceAsStream("citycode.properties");
    }

    /**
     * get current data
     * @return such as "20171201" date
     */
    public static String getCurrentData(){
        c = java.util.Calendar.getInstance();
        f=new SimpleDateFormat("yyyyMMdd");
        return f.format(c.getTime());
    }

    public static String getPinYin(String inputString) {

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = inputString.trim().toCharArray();
        StringBuffer output = new StringBuffer(StringUtils.SPACE);

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                    //output.append(one_blank);
                } else {
                    output.append(Character.toString(input[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            logger.error("get pin yin from hanzi error, please check.",e.getMessage());
        }
        return output.toString();
    }


    public static Map<String,String> getCodeByCity(){
        Map<String,String> codes = new HashMap<>();
        String value = null;
        try {
            value = IOUtils.toString(fis,"UTF-8");
        } catch (IOException e) {
            logger.error("utiltools class,getCodeByCity() get code by city, please check.",e.getMessage());

        }

        List<String> cityCodes = Arrays.asList(value.split("\\|"));
        for(String cityCode:cityCodes){
            String[] cc = cityCode.split(StringUtils.SPACE);
            String[] cityname = cc[1].split(one_cross);
            String[] citycode = cc[0].split(one_cross);
            codes.put(cityname[0],citycode[0]);
            logger.info(cityname[0]+ StringUtils.SPACE+citycode[0]);
        }
        return codes;
    }

    public List<String> getLinks(String urlPrefix){
        String url;
        Map<String,String> cityCodes = getCodeByCity();
        List<String> links = Lists.newArrayList();
        Iterator iterator=cityCodes.entrySet().iterator();
        while(iterator.hasNext()){
               Map.Entry<String, String> entry= (Map.Entry<String, String>) iterator.next();
               logger.info("key:"+entry.getKey()+" value"+entry.getValue());
            url = urlPrefix+"/"+getPinYin(entry.getKey())+"/"+entry.getValue()+".htm";
               links.add(url);
            logger.info("url "+url);
        }
        return links;
    }

    /**
     * city code + city pinyin
     * @return suffix such as "/yubei/70966.htm"
     */
    public List<String> getSuffixLinks(){
        String url;
        Map<String,String> cityCodes = getCodeByCity();
        List<String> links = Lists.newArrayList();
        Iterator iterator=cityCodes.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String> entry= (Map.Entry<String, String>) iterator.next();
            url = "/"+getPinYin(entry.getKey())+"/"+entry.getValue()+".htm";
            links.add(url);
            logger.info("url link "+url+" ");
        }
        return links;
    }
    public static String generateUUID(){
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    /**
     * get sub string by prefix String
     * @param srcStr
     * @param filterStrArr
     * @param replaceStr
     * @return Substring
     */
    public static String getSubStringByStr(String srcStr,List<String> filterStrArr,String replaceStr){
        String destStr = srcStr;
        for(String filter:filterStrArr){
                    destStr = destStr.replaceAll(filter, replaceStr);
            }
            return destStr;
    }

    public static List<String> assemblyList(){
        List<String> filterWords = Lists.newArrayList();
        filterWords.add("\\([\\u4e00-\\u9fa5]+\\)");
        filterWords.add("查看今日天气详情");
        filterWords.add("查看明日天气详情");
        filterWords.add("天气预报");

        return filterWords;
    }

    /**
     * filter specified characters
     * @return String contents
     */
    public String getFilterWeaContent(String resourceFileName){
        String str=StringUtils.EMPTY;
        logger.info("current weather file path is "+resourceFileName);
        InputStream is = null;
        try {
            is = new FileInputStream(new File(resourceFileName));
            str = IOUtils.toString(is,"UTF-8");
            logger.info("weather data file content is "+str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String destString = UtilTools.getSubStringByStr(str,UtilTools.assemblyList(),StringUtils.EMPTY);
        //String source = "东北风2级 东北风 2级 东北风 2级 东北风2级";
        String dest = destString.replaceAll("风(\\d)","风 $1");

        logger.info("filter String contents length is: "+dest.length());
        return dest;
    }

    /**
     * filter oil price rule
     * @param resourceFileName
     * @return oil price content
     */
    public String getFilterOilContent(String resourceFileName){
        String str=StringUtils.EMPTY;
        logger.info("current oil price file path is "+resourceFileName);
        InputStream is = null;
        try {
            is = new FileInputStream(new File(resourceFileName));
            str = IOUtils.toString(is,"UTF-8");
            logger.info("oil price data file content is "+str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String dest = str.replaceAll("地区 89号汽油 92号汽油 95号汽油 0号柴油 更新日期 ",StringUtils.EMPTY);

        logger.info("filter oilp rice String contents length is: "+dest.length());
        return dest;
    }
}
