package com.dfssi.elasticsearch.mapping;

import com.dfssi.common.json.Jsons;
import com.dfssi.elasticsearch.ElasticsearchContext;
import com.dfssi.elasticsearch.EsClient;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MappingUtil {
	private static Logger LOG = LoggerFactory.getLogger(MappingUtil.class);

	public static boolean buildMapping(TransportClient client, String index, String type, XContentBuilder mapping) {
		boolean bool = false;
		try {
			ImmutableOpenMap<String, MappingMetaData> mappingMap = checkIndexAndType(client, index, null);

			if (null == mappingMap || !mappingMap.containsKey(type)) {
				client.admin().indices().preparePutMapping(index)
						.setType(type)
						.setSource(mapping)
						.get();
				LOG.info(index + "/" + type + "映射已经建立：" + mapping.string());
			}

			bool = true;
		} catch (Exception e) {
			LOG.error(null, e);
		}

		return bool;
	}

	public static boolean buildJsonMapping(TransportClient client, String index, String type, String mapping) {
		boolean bool = false;
		try {

			Map<Object, Map<String, Map<String, Object>>> tmp = Jsons.toMap(mapping);
			Map<String,  Map<String, Object>> typeSetting = tmp.get(type);
			Preconditions.checkNotNull(typeSetting, String.format("type: %s对应的mapping配置为空。", type));

			Map<String, Object> settings = typeSetting.remove("settings");

			ImmutableOpenMap<String, MappingMetaData> mappingMap = checkIndexAndType(client, index, settings);

			if (null == mappingMap || !mappingMap.containsKey(type)) {
				client.admin().indices().preparePutMapping(index)
						.setType(type)
						.setSource(Jsons.obj2JsonString(tmp), XContentType.JSON)
						.get();
				LOG.info(index + "/" + type + "映射已经建立：" + mapping);
			}

			bool = true;
		} catch (Exception e) {
			LOG.error("client.admin().indices()异常", e);
		}

		return bool;
	}

	public static boolean buildIndex(TransportClient client, String index){
		try {
			client.admin().indices().create(new CreateIndexRequest(index)).actionGet();
			// waitForYellow
			client.admin()
					.cluster()
					.health(new ClusterHealthRequest(index)
							.waitForYellowStatus()).actionGet();
		}catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}

	private static ImmutableOpenMap<String, MappingMetaData> checkIndexAndType(TransportClient client, String index,  Map settings){
		MetaData metaData = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData();
		ImmutableOpenMap<String, IndexMetaData> indexMap = metaData.getIndices();
		if (!indexMap.containsKey(index) && !metaData.hasAlias(index)) {
			CreateIndexRequest request = new CreateIndexRequest(index);
			if(settings != null)request.settings(settings);
			client.admin().indices().create(request).actionGet();
			// waitForYellow
			client.admin()
					.cluster()
					.health(new ClusterHealthRequest(index)
							.waitForYellowStatus()).actionGet();
		}

		ImmutableOpenMap<String, MappingMetaData> mappingMap = null;
		if (null != indexMap.get(index)) {
			mappingMap = indexMap.get(index).getMappings();
		}
		return mappingMap;
	}

	public static void main(String[] args) throws IOException {

		EsClient esClient = ElasticsearchContext.get().getEsClient();

		String mapping = FileUtils.readFileToString(new File("D:/BDMS/demo/terminal_0200.json"));
		buildJsonMapping(esClient.getClient(), "2018_terminal_0200", "terminal_0200", mapping);

	}

}
