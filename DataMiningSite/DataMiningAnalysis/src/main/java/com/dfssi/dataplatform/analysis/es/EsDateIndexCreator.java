package com.dfssi.dataplatform.analysis.es;

import com.dfssi.common.LRUCache;
import com.dfssi.common.json.Jsons;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.hadoop.cfg.PropertiesSettings;
import org.elasticsearch.hadoop.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * es 按日期创建索引
 *
 * @author LiXiaoCong
 * @version 2018/2/7 8:53
 */
public class EsDateIndexCreator {
    private static final String MAPPING_FILE_PATH = "/elasticsearch/mappings/";

    private final Logger logger = LoggerFactory.getLogger(EsDateIndexCreator.class);
    private final Charset charset = Charset.forName("utf-8");

    private SimpleRestClient restClient;
    private LRUCache<String, Boolean> existCache;

    public EsDateIndexCreator(String esNodes, int esPort, String esClusterName) {
        PropertiesSettings propertiesSettings = new PropertiesSettings();
        propertiesSettings.setProperty("es.nodes", esNodes);
        propertiesSettings.setProperty("es.clustername", esClusterName);
        propertiesSettings.setProperty("es.port", String.valueOf(esPort));

        this.restClient = new SimpleRestClient(propertiesSettings);
        this.existCache = new LRUCache<>(100, 30 * 24 * 60 * 60 * 1000L);
    }

    public EsDateIndexCreator(Settings settings) {
        this.restClient = new SimpleRestClient(settings);
        this.existCache = new LRUCache<>(100, 30 * 24 * 60 * 60 * 1000L);
    }

    public void createMapping(String index, String type) throws Exception {
        if (existCache.get(index) == null) {

            String mapping = readMapping(type);
            Map<String, Map<String, Map<String, Object>>> tmp = Jsons.toMap(mapping);
            Map<String, Map<String, Object>> typeSetting = tmp.get(type);
            Preconditions.checkNotNull(typeSetting, String.format("type: %s对应的mapping配置为空。", type));

            Map<String, Object> settings = typeSetting.remove("settings");

            byte[] indexSettings = null;
            if (settings != null) {
                indexSettings = Jsons.obj2JsonString(settings).getBytes(charset);
            }

            byte[] mappingSettings = Jsons.obj2JsonString(tmp).getBytes(charset);
            restClient.putMapping(index, indexSettings, type, mappingSettings);
            existCache.put(index, true);

            logger.info(String.format("索引创建成功：%s/%s", index, type));
        } else {
            logger.info(String.format("索引已存在：%s/%s", index, type));
        }
    }

    public Map<String, String> getMappingFieldType(String type) throws Exception {
        String mapping = readMapping(type);
        Map<String, Map<String, Map<String, Object>>> tmp = Jsons.toMap(mapping);
        Map<String, Map<String, Object>> typeSetting = tmp.get(type);
        Preconditions.checkNotNull(typeSetting, String.format("type: %s对应的mapping配置为空。", type));

        Map<String, Object> properties = typeSetting.get("properties");
        Preconditions.checkNotNull(properties, String.format("type: %s对应的properties配置为空。", type));

        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        Map<String, String> fieldTypeMap = Maps.newHashMapWithExpectedSize(entries.size());

        Map<String, Object> info;
        Object o;
        for (Map.Entry<String, Object> entry : entries) {
            info = (Map<String, Object>) entry.getValue();
            o = info.get("type");
            if (o != null) fieldTypeMap.put(entry.getKey(), String.valueOf(o));
        }

        return fieldTypeMap;
    }

    private String readMapping(String type) {
        String mapping = readExternalMapping(type);
        if (mapping == null) {
            mapping = readInternalMapping(type);
        }
        return mapping;
    }

    private String readExternalMapping(String type) {
        File file = new File(String.format("/usr/conf%s%s.json", MAPPING_FILE_PATH, type));
        String mapping = null;
        if (file.exists()) {
            try {
                mapping = FileUtils.readFileToString(file);
            } catch (IOException e) {
                logger.error(String.format("读取文件失败：%s", file), e);
            }
        }
        return mapping;
    }

    private String readInternalMapping(String type) {

        String format = String.format("%s%s.json", MAPPING_FILE_PATH, type);
        String mapping = null;
        try {
            InputStream resourceAsStream = EsDateIndexCreator.class.getResourceAsStream(format);
            mapping = IOUtils.toString(resourceAsStream);
            resourceAsStream.close();
        } catch (IOException e) {
            logger.error(String.format("读取系统文件失败：%s", format), e);
        }
        return mapping;
    }
}
