package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze.BaseProtoHandler;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.Iterator;
import java.util.List;

import static com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common.HandlersManger.msgHandlers;


/**
 * Created by Hannibal on 2018-02-28.
 */
public class MessageHandlerReader{

    private static Logger logger = Logger.getLogger(MessageHandlerReader.class);

    private static final String UP_MESSAGE_HANDLER_CONFIG_PATH = "sink-upproto-routing.xml";

    private static MessageHandlerReader instance = null;

    static {
        instance = new MessageHandlerReader();
    }

    private MessageHandlerReader() {
        readUPXMLConfig();
    }

    public static MessageHandlerReader getInstance() {
        if (null == instance) {
            instance = new MessageHandlerReader();
        }

        return instance;
    }

    private void readUPXMLConfig() {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(ConfigurationUtils.locate(UP_MESSAGE_HANDLER_CONFIG_PATH));
        } catch (DocumentException e) {
            logger.error(null, e);
        }

        Element root = document.getRootElement();

        initSkipPackMergeProtos(root);

        initUpHandlers(root);

    }

    private void initSkipPackMergeProtos(Element root) {
        String skipPackMergeProtos = root.element("skipPackMergeProtos").getTextTrim();

        if (StringUtils.isNotBlank(skipPackMergeProtos)) {
            String[] skipPackMergeProtoArr = skipPackMergeProtos.split(",");

            for (String proto : skipPackMergeProtoArr) {
                HandlersManger.SKIPPACKMERGEPROTOS.add(proto.trim());
            }
        }
    }

    private void initUpHandlers(Element root) {
        List<Element> protoElementList = root.element("protoMaps").elements("upProto");
        Iterator<Element> protoElementIterator = protoElementList.iterator();

        while (protoElementIterator.hasNext()) {
            try {
                Element protoElement = protoElementIterator.next();
                String[] msgIds = protoElement.attributeValue("msgIds").split(",");
                String handlerClass = protoElement.attributeValue("handler");

                try {
                    BaseProtoHandler handler = (BaseProtoHandler)Class.forName(handlerClass).newInstance();
                    //handler.setup();

                    msgHandlers.put(handlerClass, handler);

                    for (String msgId : msgIds) {
                        short reqId = (short)Integer.parseInt(msgId, 16);

                        HandlersManger.registerUpHandler(reqId, handler);
                    }

                } catch (Exception e) {
                    logger.error("upProto {} [@handler] 获取实例失败:" + handlerClass);
                }
            } catch (Exception e) {
                logger.error("upProto 获取实例失败:", null);
            }

        }
    }

    public static void main(String[] args) {
        getInstance();

        System.out.println("1 " + HandlersManger.SKIPPACKMERGEPROTOS);

        System.out.println("2 " + HandlersManger.getAllUpMsgHandlers());
    }

}
