package com.dfssi.spark.config.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/3/1 13:44
 */
public class XmlConfigReader {
    private final Logger logger = LoggerFactory.getLogger(XmlConfigReader.class);

    private XmlConfigReader(){}

    public static XmlConfig readXml(InputStream inputStream, String rootTag) throws DocumentException {
        XmlConfig config = null;

        if(inputStream != null) {
            if (rootTag == null) {
                rootTag = XmlConfigDefTag.ELEMENT_TAG_DEFAULT_ROOT;
            }

            Document xml = new SAXReader().read(inputStream);
            Node node = xml.selectSingleNode(rootTag);
            if(node != null){
                Element rootNode = (Element) node;
                config = createXmlConfig(rootNode);

                loadConfig(config, rootNode);
            }
        }
        return config;
    }

    private static XmlConfig createXmlConfig(Element rootNode){
        String id = rootNode.attributeValue(XmlConfigDefTag.ATTR_TAG_ID);
        String name = rootNode.attributeValue(XmlConfigDefTag.ATTR_TAG_NAME);
        return new XmlConfig(id, name);
    }

    private static void loadConfig(XmlConfig xmlConfig, Element rootNode){

        List<ConfigEntity> inputsConfig = createInputConfigEntitys(rootNode);
        List<ConfigEntity> processConfig = createProcessConfigEntitys(rootNode);
        List<ConfigEntity> outputConfig = createOutputConfigEntitys(rootNode);

        xmlConfig.setInputsConfig(inputsConfig);
        xmlConfig.setProcesssConfig(processConfig);
        xmlConfig.setOutputsConfig(outputConfig);
    }

    private static List<ConfigEntity> createInputConfigEntitys(Element rootNode){
        List<ConfigEntity> configEntities = null;
        Node node = rootNode.selectSingleNode(XmlConfigDefTag.ELEMENT_TAG_INPUTS);
        if(node != null){
            configEntities = createConfigEntitys((Element) node, XmlConfigDefTag.ELEMENT_TAG_INPUT);
        }
        return configEntities;
    }

    private static List<ConfigEntity> createProcessConfigEntitys(Element rootNode){
        List<ConfigEntity> configEntities = null;
        Node node = rootNode.selectSingleNode(XmlConfigDefTag.ELEMENT_TAG_PREPROCESS);
        if(node != null){
            configEntities = createConfigEntitys((Element) node, XmlConfigDefTag.ELEMENT_TAG_PROCESS);
        }
        return configEntities;
    }

    private static List<ConfigEntity> createOutputConfigEntitys(Element rootNode){
        List<ConfigEntity> configEntities = null;
        Node node = rootNode.selectSingleNode(XmlConfigDefTag.ELEMENT_TAG_OUTPUTS);
        if(node != null){
            configEntities = createConfigEntitys((Element) node, XmlConfigDefTag.ELEMENT_TAG_OUTPUT);
        }
        return configEntities;
    }

    private static List<ConfigEntity> createConfigEntitys(Element entityNode, String configEntityTag){

        List<Element> elements = entityNode.selectNodes(configEntityTag);
        List<ConfigEntity> configEntities = new ArrayList<>();
        if(elements != null) {
            ConfigEntity configEntity;
            for (Element element : elements) {
                 configEntity = createConfigEntity(element);
                 loadParam(configEntity, element);

                 configEntities.add(configEntity);
            }
        }
        return configEntities;
    }

    private static ConfigEntity createConfigEntity(Element element){

        String id = element.attributeValue(XmlConfigDefTag.ATTR_TAG_ID);
        String type = element.attributeValue(XmlConfigDefTag.ATTR_TAG_TYPE);

        String inputIdsStr = element.attributeValue(XmlConfigDefTag.ATTR_TAG_INPUTIDS);
        String[] inputIds = null;
        if(inputIdsStr != null){
            inputIds = inputIdsStr.split(",");
        }

        return new ConfigEntity(id, type, inputIds);
    }

    private static void loadParam(ConfigEntity configEntity, Element element){

        Node node = element.selectSingleNode(XmlConfigDefTag.ELEMENT_TAG_PARAMS);
        if(node != null) {
            List<Element> params = node.selectNodes(XmlConfigDefTag.ELEMENT_TAG_PARAM);
            String name;
            String value;
            for (Element param : params) {
                name = param.attributeValue(XmlConfigDefTag.ATTR_TAG_NAME);
                value = param.attributeValue(XmlConfigDefTag.ATTR_TAG_VALUE);
                configEntity.addConfig(name, value);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        FileInputStream inputStream = new FileInputStream("D:\\BDMS\\demo\\demo.xml");

        XmlConfig config = XmlConfigReader.readXml(inputStream, "spark-task-def");
        System.out.println(config);
    }
}
