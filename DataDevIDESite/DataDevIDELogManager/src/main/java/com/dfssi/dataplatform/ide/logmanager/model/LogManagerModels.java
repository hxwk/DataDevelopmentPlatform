package com.dfssi.dataplatform.ide.logmanager.model;

import com.dfssi.common.Dates;
import com.dfssi.common.regex.Validations;
import com.dfssi.elasticsearch.ElasticsearchContext;
import com.dfssi.elasticsearch.searchs.SearchCenter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/8 14:02
 */
@Service
public class LogManagerModels {
    private final Logger logger = LogManager.getLogger(LogManagerModels.class);
    private String indexBaseName = "logstash";
    private String typeName = "logs";

    public Map<String, Object> findAll(int pageNum, int pageSize){

        String sql = String.format("select level, timeMillis, message, source from %s-*/%s order by timeMillis desc limit %s,%s",
                indexBaseName, typeName, (pageNum -1) * pageSize, pageSize);

        ElasticsearchContext es = ElasticsearchContext.get();
        SearchCenter searcher = es.getSearcher();
        SearchHits searchHits = searcher.searchSQLForHits(null, sql);

        return parseHits(searchHits);
    }

    public Map<String, Object> findByCondition(String condition, String level, int pageNum, int pageSize){

        StringBuilder sql = new StringBuilder("select level, timeMillis, message, source from ");
        sql.append(String.format("%s-*/%s ", indexBaseName, typeName));
        boolean hasLevel = (level != null);
        if(hasLevel){
            sql.append("where level='").append(level).append("' ");
        }

        if(condition != null){
            if(hasLevel){
                 sql.append("and ");
            }else {
               sql.append("where ");
            }
            sql.append("(source.class like '%").append(condition).append("%' ");
            if(Validations.isINTEGER_NEGATIVE(condition)) {
                sql.append("or ").append("source.line = ").append(condition).append(" ");
            }
            sql.append("or ").append("source.method like '%").append(condition).append("%') ");
        }

        sql.append("order by timeMillis desc limit ").append((pageNum -1) * pageSize).append(",").append(pageSize);

        ElasticsearchContext es = ElasticsearchContext.get();
        SearchCenter searcher = es.getSearcher();
        SearchHits searchHits = searcher.searchSQLForHits(null, sql.toString());
        return parseHits(searchHits);
    }

    private Map<String, Object> parseHits(SearchHits searchHits) {
        SearchHit[] hits = searchHits.getHits();
        int size = hits.length;

        List<Map<String, Object>> logs = Lists.newArrayListWithCapacity(size);
        Map<String, Object> log;
        for(SearchHit hit : hits){
            log = hit.getSourceAsMap();
            formatMsTime(log, "timeMillis");
            formatLogSource(log, "source");
            logs.add(log);
        }

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", searchHits.getTotalHits());
        res.put("pageSize", size);
        res.put("records", logs);
        return res;
    }

    private void formatMsTime(Map<String, Object> log, String field){
        Object timeMillisObj = log.get(field);
        long time;
        if(timeMillisObj != null){
            time = (long) timeMillisObj;
        }else {
            time = System.currentTimeMillis();
        }
        log.put(field, Dates.long2Str(time, "yyyy-MM-dd HH:mm"));
    }

    private void formatLogSource(Map<String, Object> log, String field){
        Object sourceObj = log.get(field);
        String source;
        if(sourceObj != null){
            Map<String, Object> map = (Map<String, Object>) sourceObj;
            source = String.format("%s#%s-%s", map.get("class"), map.get("method"), map.get("line"));
        }else {
            source = "Unknown";
        }

        log.put(field, source);
    }

    public static void main(String[] args) {
        LogManagerModels logManagerModels = new LogManagerModels();
        Map<String, Object> error = logManagerModels.findByCondition("10", "INFO", 1, 10);
        System.out.println(error);
    }
}
