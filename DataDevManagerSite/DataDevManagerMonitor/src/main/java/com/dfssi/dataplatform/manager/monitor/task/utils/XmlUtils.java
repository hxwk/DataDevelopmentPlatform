package com.dfssi.dataplatform.manager.monitor.task.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;


public class XmlUtils {

    public static Element getSingleSubElement(Element el, String nodePath) {
        if (el == null || nodePath == null || nodePath.trim().length() == 0)
            return null;

        return (Element) el.selectSingleNode(nodePath);
    }

    public static String getSubElementAttributeValue(Element el, String nodePath, String attribute) {
        Element subEl = getSingleSubElement(el, nodePath);

        return getAttributeValue(subEl, attribute);
    }

    public static String getAttributeValue(Element element, String attributeKey) {
        if (element == null || attributeKey == null)
            return null;

        String value = element.attributeValue(attributeKey);
        if (value == null) {
            value = element.attributeValue(attributeKey.toUpperCase());
        }
        if (value == null) {
            value = element.attributeValue(attributeKey.toLowerCase());
        }
        if (value == null) {
            return null;
        }

        return value;
    }

    public static String getSubElementText(Element el, String nodePath) {
        Element subEl = getSingleSubElement(el, nodePath);
        if (subEl == null)
            return null;

        return subEl.getText();
    }

    public static String prettyFormat(String xmlStr) throws IOException, DocumentException {
        Document document = DocumentHelper.parseText(xmlStr);

        return prettyFormat(document);
    }

    public static String prettyFormat(Document document) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        xmlWriter.write(document);
        xmlWriter.close();

        return writer.toString();
    }
}
