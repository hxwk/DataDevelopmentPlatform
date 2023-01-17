package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model;

import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.TimeoutException;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Geode存储服务
 * Created by yanghs on 2018/4/3.
 */
@Service
public class GeodeService<T> {

    protected final Logger logger = LoggerFactory.getLogger(GeodeService.class);

    private static Map<String,Region<Object,Object>> regionMap=new HashMap<String,Region<Object,Object>>();

    @Autowired
    private ClientRegionFactory clientRegionFactory;

    @Autowired
    private QueryService queryService;

    /**
     * 获取数据存储region
     * @param regionName 数据表region名称
     * @return 连接实例
     */
    public  Region<Object,Object> getRegion(String regionName){
        if(regionMap.get(regionName)!=null){
            return regionMap.get(regionName);
        }
        Region<Object,Object> region= clientRegionFactory.create(regionName);
        regionMap.put(regionName,region);
        return region;
    }

    /**
     * @param regionName 数据区域名称
     * @param key 数据项键值对key
     * @param value 数据项键值对value
     * @return 保存操作结果 为空字符串则为保存成功
     */
    public String saveEntry(String regionName,Object key,Object value){
        Region<Object,Object> region=getRegion(regionName);
        String result = "";
        try {
            logger.debug("保存数据到geode key =" + key + ", value = " + value.toString());
            region.put(key, value);
            logger.debug("保存结束！");
        } catch (TimeoutException e) {
            result = "连接超时...";
            logger.debug(result);
            e.printStackTrace();
        } catch (CacheWriterException e) {
            result = "缓存写入异常";
            logger.debug(result);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除键为key的数据项目
     * @param regionName 数据区域名称
     * @param key 数据项键值对key
     * @return 删除操作结果 为空字符串则为删除成功
     */
    public String destroyEntry(String regionName,Object key){
        String resultFlag="";
        Region<Object,Object> region=getRegion(regionName);
        logger.debug("开始删除数据项:key="+key);
        if (region.containsKey(key)){
            try {
                region.destroy(key);
                logger.debug("删除成功:key="+key);
            } catch (TimeoutException e) {
                resultFlag="geode数据destroy时，连接超时...";
                logger.error(resultFlag);
                e.printStackTrace();
            } catch (EntryNotFoundException e) {
                resultFlag="geode数据destroy时，数据项未找到...";
                logger.error(resultFlag);
                e.printStackTrace();
            } catch (CacheWriterException e) {
                resultFlag="geode数据destroy时，缓存写错误...";
                logger.error(resultFlag);
                e.printStackTrace();
            }
        }else{
            resultFlag="数据项未找到...";
        }
        logger.debug("结束删除数据项:key="+key);
        return resultFlag;
    }

    /**
     * 执行查询返回列表
     * @param oql 查询语句
     * @return 查询结果List
     */
    public List<T> findGeodeQueryResultList(String oql) {
        SelectResults results=excuteQuery(oql);
        List<T> list = new ArrayList<T>();
        Iterator it = results.iterator();
        while (it.hasNext()) {
            list.add((T) it.next());
        }
        return list;
    }

    /**
     * 统计数量
     * @param oql
     * @return
     */
    public int findfindGeodeQueryResultCount(String oql){
        SelectResults results=excuteQuery(oql);
        int count=0;
        Iterator it = results.iterator();
        while (it.hasNext()) {
            count=(int)it.next();
        }
        return count;
    }

    /**
     * 执行查询
     * @param oql 查询语句
     * @return 查询结果
     */
    public SelectResults excuteQuery(String oql){
        logger.debug("执行语句："+oql);
        Query query = queryService.newQuery(oql);
        // Execute the query locally. It returns the results set.
        SelectResults results = null;
        long startTIme=System.currentTimeMillis();
//        logger.info("当前时间："+startTIme+" 开始执行...");
        try {
            results = (SelectResults) query.execute();
        } catch (FunctionDomainException e) {
            e.printStackTrace();
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (QueryInvocationTargetException e) {
            e.printStackTrace();
        }catch (QueryExecutionTimeoutException e){
            e.printStackTrace();
        }catch (QueryExecutionLowMemoryException e){
            e.printStackTrace();
        }

        long endTIme=System.currentTimeMillis();
//        logger.info("当前时间："+endTIme+" 结束执行...");
        logger.debug("执行时间："+(endTIme-startTIme)+" 毫秒");
        return results;
    }
}
