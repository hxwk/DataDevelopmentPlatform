package com.dfssi.elasticsearch.mapping;

import com.dfssi.elasticsearch.ElasticsearchContext;
import com.dfssi.elasticsearch.EsClient;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/6 11:47
 */
public class MappingCreator {
    private final Logger logger = LoggerFactory.getLogger(MappingCreator.class);

    private static final String MAPPING_FILE_PATH = "/elasticsearch/mappings/";

    private EsClient esClient;

    public MappingCreator(String client){
       if(client == null){
           this.esClient = ElasticsearchContext.get().getEsClient();
       }else {
           this.esClient = ElasticsearchContext.get().getEsClient(client);
       }
    }

    /**
     *
     * 系统会根据 type名称去路径 /usr/conf/elasticsearch/mappings/ 或者系统相对路径下的 /elasticsearch/mappings/下
     * 寻找 ${type}.json的mapping映射文件。
     *
     *
     * @param type
     */
    public void createMapping(String index, String type){
        String mapping = readMapping(type);
        if(mapping != null){
            boolean b = MappingUtil.buildJsonMapping(esClient.getClient(), index, type, mapping);
            Preconditions.checkArgument(b, String.format("创建%s/%s失败。", index, type));
        }else {
            Preconditions.checkArgument(false, String.format("创建%s/%s失败，找不到对应的Mapping的json文件", index, type));
        }
    }

    private String readMapping(String type){
        String mapping = readExternalMapping(type);
        if(mapping == null){
            mapping = readInternalMapping(type);
        }
        return mapping;
    }

    private String readExternalMapping(String type){
        File file = new File(String.format("/usr/conf%s%s.json", MAPPING_FILE_PATH, type));
        String mapping = null;
        if(file.exists()) {
            try {
                mapping = FileUtils.readFileToString(file);
            } catch (IOException e) {
                logger.error(String.format("读取文件失败：%s", file), e);
            }
        }
        return mapping;
    }

    private String readInternalMapping(String type){

        String format = String.format("%s%s.json", MAPPING_FILE_PATH, type);
        String mapping = null;
        try {
            InputStream resourceAsStream = MappingCreator.class.getResourceAsStream(format);
            mapping = IOUtils.toString(resourceAsStream);
            resourceAsStream.close();
        } catch (IOException e){
            logger.error(String.format("读取系统文件失败：%s", format), e);
        }
        return mapping;
    }

    public static void main(String[] args) {

        MappingCreator mappingCreator = new MappingCreator(null);

        mappingCreator.createMapping("terminal_0200","terminal_0200");

    }

}
