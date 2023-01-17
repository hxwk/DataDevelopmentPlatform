package com.dfssi.elasticsearch.searchs;

import com.dfssi.elasticsearch.ElasticsearchContext;
import com.dfssi.elasticsearch.EsClient;
import com.dfssi.elasticsearch.searchs.extracter.MapExtractorException;
import com.dfssi.elasticsearch.searchs.extracter.SimpleMapResultsExtractor;
import com.dfssi.elasticsearch.sql611.SearchDao;
import com.dfssi.elasticsearch.sql611.query.SqlElasticRequestBuilder;
import com.dfssi.elasticsearch.sql611.query.SqlElasticSearchRequestBuilder;
import com.google.common.collect.Lists;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCenter {

	private final Logger logger = LoggerFactory.getLogger(SearchCenter.class);

	public Map<String, Object> searchSQL(String clientId, String eql) {

		long t = System.currentTimeMillis();
		Map<String, Object> res = emptyResult();

		EsClient client = ElasticsearchContext.get().getEsClient(clientId);
		if (client != null) {
			SearchDao searchDao = new SearchDao(client.getClient());
			SqlElasticRequestBuilder select = null;
			try {
				select = searchDao.explain(eql).explain();
			} catch (Exception e) {
				logger.error(String.format("在es集群 %s 中使用 sql %s查询失败。", clientId, eql), e);
			}

			if (select != null) {
				SearchResponse searchResponse = (SearchResponse) select.get();
				SearchHits hits = searchResponse.getHits();
				Aggregations aggregations = searchResponse.getAggregations();

				Object queryResult = (aggregations == null) ? hits : aggregations;
				SimpleMapResultsExtractor resultsExtractor = new SimpleMapResultsExtractor(false, false);
				List<Map<String, Object>> result;
				try {
					result = resultsExtractor.extractResults(queryResult);
					res.put("total", hits.getTotalHits());
					res.put("pageSize", result.size());
					res.put("records", result);
				} catch (MapExtractorException e) {
					logger.error(String.format("在es集群 %s 中使用 sql %s查询成功，结果解析失败。", clientId, eql), e);
				}
			}
		} else {
			logger.error(String.format("不存在指定 %s es集群 。", clientId));
		}
		logger.info(String.format("本次查询耗时： %s 毫秒", (System.currentTimeMillis() - t)));

		return res;
	}

	public Map<String, Object> deleteSQL(String clientId, String eql) {

		long t = System.currentTimeMillis();
		Map<String, Object> res = emptyResult();

		EsClient client = ElasticsearchContext.get().getEsClient(clientId);
		if (client != null) {
			SearchDao searchDao = new SearchDao(client.getClient());
			SqlElasticRequestBuilder delete = null;
			try {
				delete = searchDao.explain(eql).explain();
			} catch (Exception e) {
				logger.error(String.format("在es集群 %s 中使用 sql %s删除数据失败。", clientId, eql), e);
			}

			if (delete != null) {
				BulkByScrollResponse response = (BulkByScrollResponse) delete.get();
				int batches = response.getBatches();
				res.put("total", batches);
				res.put("pageSize", batches);
				res.put("records", response.getStatus());
			}
		} else {
			logger.error(String.format("不存在指定 %s es集群 。", clientId));
		}
		logger.info(String.format("本次查询耗时： %s 毫秒", (System.currentTimeMillis() - t)));

		return res;
	}

	public SearchHits searchSQLForHits(String clientId, String eql) {

		long t = System.currentTimeMillis();
		EsClient client = ElasticsearchContext.get().getEsClient(clientId);
		if (client != null) {
			SearchDao searchDao = new SearchDao(client.getClient());
			SqlElasticSearchRequestBuilder select = null;
			try {
				select = (SqlElasticSearchRequestBuilder) searchDao.explain(eql).explain();
			} catch (Exception e) {
				logger.error(String.format("在es集群 %s 中使用 sql %s查询失败。", clientId, eql), e);
			}

			if (select != null) {
				SearchResponse searchResponse = (SearchResponse) select.get();
				return searchResponse.getHits();
			}
		} else {
			logger.error(String.format("不存在指定 %s es集群 。", clientId));
		}
		logger.info(String.format("本次查询耗时： %s 毫秒", (System.currentTimeMillis() - t)));

		return null;
	}

	private Map<String, Object> emptyResult() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("total", 0);
		map.put("pageSize", 0);
		map.put("records", Lists.newArrayList());
		return map;
	}
}
