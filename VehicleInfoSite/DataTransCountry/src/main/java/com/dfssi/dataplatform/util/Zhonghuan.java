package com.dfssi.dataplatform.util;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Zhonghuan {
    private static Logger logger = Logger.getLogger(Zhonghuan.class);

    private  static HashSet  zhonghuanSimHashSet = new HashSet();
    private  static HashSet  notZhonghuanSimHashSet  = new HashSet();;
    //首先解析报文，判断报文中的sim卡是否属于中寰系统，如果是则返回true 否则返回false
    public static boolean belongToZhonghuan(String data){
        //解析data 根据报文中的sim去判断是否数据中寰系统
        String sim = getSim(data);
        //每次都去查sim太浪费性能了，优化：可以使用hashset(性能比ArrayList好，hashset时间复杂度是O(1) )去缓存属于中寰的sim  --todo  需要确认业务是否变更
        if(zhonghuanSimHashSet.contains(sim)){
            logger.info("该sim已在geode中查询过，属于中寰系统");
            return true;
        }else if(notZhonghuanSimHashSet.contains(sim)){
            logger.info("该sim已在geode中查询过，不属于中寰系统");
            return false;
        }else{
            String sql="select * from  /cvVehicleBaseInfo where sim='"+sim+"'";
            logger.info("在geode判断是否属于中寰系统,sql:"+sql);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println("查询之前："+df.format(new Date()));// new Date()为获取当前系统时间
            Region regeion = GeodeTool.getRegeion("cvVehicleBaseInfo");

            SelectResults query=null;
            try{
                query = regeion.query(sql);
                System.out.println("查询之后："+df.format(new Date()));// new Date()为获取当前系统时间
            }catch (Exception e){
                logger.info("查询geode判断是否属于中寰系统报错，请检查geode,sql:"+sql,e);
                return false;
            }
            if(query.size()>0){
                zhonghuanSimHashSet.add(sim);
                return true;
            }else{
                notZhonghuanSimHashSet.add(sim);
                return false;
            }
        }

    }

    private  static String getSim(String data){
        //parse data报文  确定格式后分解出sim
        //ex:232302FE4C474A453133454130484D38383838383801006912081D0E0F37010103020000000000000000271000010000000000020101010000000000000000000003000000000000000000000100000100000101040200000000050000000000000000000601010001010100010101010101010700000000000000000008000900A6
        //return "13476073257";
        String sim = data.substring(17,28);

        return  sim;
    }

    public static void main(String[] args) {
        String data = "232302FE4C474A453133454130484D38383838383801006912081D0E0F37010103020000000000000000271000010000000000020101010000000000000000000003000000000000000000000100000100000101040200000000050000000000000000000601010001010100010101010101010700000000000000000008000900A6";
        String sim = getSim(data);
        System.out.println(sim);
    }
}
