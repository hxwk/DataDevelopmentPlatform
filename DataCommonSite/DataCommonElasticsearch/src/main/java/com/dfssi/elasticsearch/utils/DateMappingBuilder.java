package com.dfssi.elasticsearch.utils;

import com.dfssi.common.DateSuffixAppender;
import com.dfssi.common.LRUCache;
import com.dfssi.elasticsearch.mapping.MappingCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/6 13:49
 */
public class DateMappingBuilder {
    private final Logger logger = LoggerFactory.getLogger(DateMappingBuilder.class);

    private MappingCreator creator;
    private DateSuffixAppender appender;

    private LRUCache<String, Boolean> existCache;

    public DateMappingBuilder(String client, DateSuffixAppender appender){
        this.creator = new MappingCreator(client);
        this.appender = appender;
        this.existCache = new LRUCache<>(100, 30 * 24 * 60 * 60 * 1000L);
    }

    public void createMapping(String index, String type, Date date){
        if(appender != null)index = appender.append(index, date);
        if(existCache.get(index) == null){
            creator.createMapping(index, type);
            existCache.put(index, true);
        }
    }

}
