package com.dfssi.dataplatform.vehicleinfo.vehicleroad.cache;


import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Sets;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/27 8:38
 */
public class ElasticSearchIndexNameCache extends AbstractCache<String, List<String>> {

    private final String cacheKey = "allIndexName";

    @Autowired
    private TransportClient transportClient;

    public ElasticSearchIndexNameCache(ExecutorService executorService) {
        super(1, 5, TimeUnit.HOURS, executorService);
    }

    @Override
    protected List<String> loadData(String s) {
        return ElasticSearchUtil.getIndices(transportClient);
    }

    public boolean exist(String index){
        List<String> indexs = getCache(cacheKey);
        return indexs.contains(index);
    }

    public String[] selectExist(String ... indexs){
        return selectExist(Sets.newHashSet(indexs));
    }

    public String[] selectExist(Collection<String> indexs){
        List<String> all = getCache(cacheKey);
        indexs.retainAll(all);
        return indexs.toArray(new String[indexs.size()]);
    }
}
