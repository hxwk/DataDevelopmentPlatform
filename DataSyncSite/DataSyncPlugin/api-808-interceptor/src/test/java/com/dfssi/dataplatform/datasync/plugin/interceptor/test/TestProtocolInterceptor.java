package com.dfssi.dataplatform.datasync.plugin.interceptor.test;

import com.dfssi.dataplatform.datasync.plugin.interceptor.ProtocolInterceptor;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.event.EventBuilder;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.InterceptorBuilderFactory;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.InterceptorType;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.util.HexUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jian on 2017/12/19.
 */
public class TestProtocolInterceptor {
    static final Logger logger = LoggerFactory.getLogger(TestProtocolInterceptor.class);


    public void testProtocol() {

        //Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(InterceptorType.PROTOCOLINTERCEPTOR.toString());
        //Interceptor interceptor = builder.build();
        Interceptor interceptor = new ProtocolInterceptor();
        Map<String, String> headers = Maps.newHashMap();
        headers.put("taskId", "001");
        headers.put("msgId", "0702");
        /**
         * 0200
         */
        //Event eventBeforeIntercept = EventBuilder.withBody("00 00 00 00 00 0C 00 03 02 1B FE 14 06 F4 24 98 00 33 00 00 00 2B 17 12 14 09 43 14 01 04 00 06 62 0C 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 1B 31 01 15 E1 02 00 FD",Charsets.UTF_8,headers);
        //Event eventBeforeIntercept = EventBuilder.withBody("00 00 00 00 00 0C 03 00 02 7D 01 91 2B 07 58 F5 10 00 00 00 00 00 00 17 12 13 10 40 25 01 04 00 00 74 19 02 02 0C 54 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 00 00 30 01 00 31 01 00 91 01 FF 92 04 00 00 00 00 14 04 00 00 00 00 E1 02 00 00 E2 04 00 04 D0 DE 00 59 00 00 00 00 00 0C 03 00 02 7D 01 91 2B 07 58 F5 10 00 00 00 00 00 00 17 12 14 09 42 19 01 04 00 00 74 19 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 6F 5C A7 E7 30 01 00 31 01 00 91 01 FF 92 04 00 00 00 00 14 04 00 00 00 00 E1 02 0A BE E2 04 00 04 D0 DE 00 59 00 00 00 00 00 0C 23 01 02 7D 01 91 2B 07 58 F5 10 00 00 00 00 00 00 17 12 14 09 42 24 01 04 00 00 74 19 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 6F 5C A7 6E 30 01 00 31 01 00 91 01 FF 92 04 00 00 00 00 14 04 00 00 00 00 E1 02 0A BE E2 04 00 04 D0 DE",Charsets.UTF_8,headers);
        //Event eventBeforeIntercept = EventBuilder.withBody("00 00 00 00 00 0C 00 02 01 D1 7C 5B 06 CE 7C 20 00 1C 00 06 01 5D 17 12 27 18 55 02 01 04 00 00 00 02 02 02 00 00 03 02 00 00 04 02 00 00 25 04 00 00 00 00 2B 04 00 00 00 00 30 01 14 31 01 00 E1 02 00 F3", Charsets.UTF_8, headers);

        /**
         * 0704
         */
        //Event eventBeforeIntercept = EventBuilder.withBody("00 05 01 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 16 00 00 00 00 17 08 13 07 20 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 15 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 21 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 16 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 12 00 00 00 00 17 08 13 07 22 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 16 31 01 13 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 24 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C DF 30 01 17 31 01 14 E1 02 00 FE 00 44 00 00 00 00 00 0C 7C 03 02 3D 30 3C 07 0F 71 F6 00 10 00 00 00 00 17 08 13 07 23 01 01 04 00 2E B5 11 02 02 00 00 03 02 00 00 25 04 00 00 00 00 2A 02 00 00 2B 04 00 00 0C E0 30 01 15 31 01 13 E1 02 00 FE",Charsets.UTF_8,headers);

        /**
         * 0705
         */
        //Event eventBeforeIntercept = EventBuilder.withBody("002816342000004CFE6CEE3F3FFFFF000098044CF0040038E1BE1E210004BE58FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF0000F3044CF0040021A4A4FA250004A458FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF00000C054CF00400017F7DA41B00047F58FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF000086044CF00400107D9CEE1500049C58FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF00007E044CF00400707D7EEC1800047F58FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF0000B9044CF00400107D83CE1600048358FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF000046044CF00400007D83921600048358FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF00008F034CF00400307D832E1600048358FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF0000E3024CF00400407D99A41500049A58FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF00004B034CF00400107D83CE1600048358FEEE0076FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF000017044CF00400607D833B1600048458FEEE0077FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF0000D3034CF00400107D832A1600048358FEEE0077FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF00004F034CF00400107D830A1600048358FEEE0077FFFFFFFFFFFFFF4CFE6CEE3F3FFFFF0000E401",Charsets.UTF_8,headers);
        //Event eventBeforeIntercept = EventBuilder.withBody("00 50 20 43 44 00 00 4C FF 01 31 01 19 00 FF FF FF FF 97 4C F0 04 00 21 7F 86 D0 15 21 F3 86 4C F0 04 00 61 80 87 D0 15 21 F3 87 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 21 82 89 D8 15 21 F3 89 4C FF 03 31 C6 3F A0 1F C3 13 FF 46 4C F0 04 00 71 84 88 E8 15 21 F3 8A 4C FF 03 31 C6 3F A0 1F C3 13 FF 57 4C FF 01 31 01 52 00 FF FF FF FF 97 4C F0 03 31 F1 06 09 FF FF FF FF FF 4C F0 04 00 51 85 8A 04 16 21 F3 8A 4C FF 03 31 C6 3F A0 1F C3 13 FF 60 4C F0 04 00 11 85 8A 18 16 21 F3 8A 4C F0 04 00 71 85 89 2C 16 21 F3 89 4C FF 01 31 01 3C 00 FF FF FF FF 97 4C F0 04 00 71 84 89 2C 16 21 F3 88 4C FF 03 31 C6 3F A0 1F C3 13 FF 71 4C FF 01 31 01 1E 00 FF FF FF FF 97 4C F0 03 31 F1 04 06 FF FF FF FF FF 4C F0 04 00 71 84 87 44 16 21 F3 87 4C FF 01 31 01 16 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 21 80 84 80 16 21 F3 83 4C FF 01 31 01 0F 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 24 4C F0 03 31 F1 02 03 FF FF FF FF FF 4C F0 04 00 61 7F 80 A0 16 21 F3 80 4C FF 01 31 01 0B 00 FF FF FF FF 97 4C F0 04 00 41 7F 80 A0 16 21 F3 7F 4C FF 01 31 01 09 00 FF FF FF FF 97 4C F0 03 31 F1 00 01 FF FF FF FF FF 4C FF 01 31 01 15 00 FF FF FF FF 97 4C F0 04 00 21 7D 02 7F 9C 16 21 F3 7F 58 FE F1 31 F3 B9 00 40 00 00 00 00 4C FF 03 31 C6 3F A0 1F C3 13 FF 35 58 FE DF 00 84 50 02 11 7D 01 04 05 FF 58 FE EF 00 00 FF FF 2F FF FF FF 02 4C F0 04 00 41 8D 8D 68 1F 21 F3 8D 4C FF 01 31 01 3D 01 FF FF FF FF 97 4C F0 04 00 41 8F 8D 5C 1F 21 F3 8F 4C FF 01 31 01 59 01 FF FF FF FF 97 4C F0 04 00 51 92 8F 54 1F 21 F3 92 4C F0 03 31 F0 1E 1B FF FF FF FF FF 4C FF 01 31 01 69 01 FF FF FF FF 97 4C F0 04 00 51 94 94 58 1F 21 F3 94 4C FF 01 31 01 73 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 46 4C F0 04 00 71 96 96 64 1F 21 F3 96 4C F0 03 31 F0 23 1F FF FF FF FF FF 4C FF 03 31 C6 3F A0 1F C3 13 FF 02 4C F0 04 00 71 98 98 8C 1F 21 F3 98 4C F0 04 00 01 9A 9A B0 1F 21 F3 9A 4C FF 01 31 01 70 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 01 9A 9A C4 1F 21 F3 9A 4C FF 01 31 01 25 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 24 4C F0 03 31 F0 21 1D FF FF FF FF FF 4C F0 04 00 51 99 9A 08 20 21 F3 99 4C FF 01 31 01 E9 00 FF FF FF FF 97 4C F0 04 00 01 98 9A 50 20 21 F3 98 58 FE F1 31 F3 63 00 40 00 00 00 00 4C F0 04 00 21 98 98 80 20 21 F3 98 4C F0 04 00 71 95 95 9C 20 21 F3 95 4C F0 04 00 71 95 95 B8 20 21 F3 95 4C FF 01 31 01 A9 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 35 4C F0 04 00 11 93 93 CC 20 21 F3 93 4C FF 01 31 01 57 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 60 4C F0 04 00 11 93 93 D4 20 21 F3 93 4C F0 04 00 41 90 90 D0 20 21 F3 90 4C FF 01 31 01 3E 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 71 4C F0 04 00 11 8E 8E C8 20 21 F3 8E 4C FF 01 31 01 2C 00 FF FF FF FF 97 4C F0 03 31 F0 10 0E FF FF FF FF FF 4C FF 01 31 01 1F 00 FF FF FF FF 97 4C F0 04 00 01 8C 8C B4 20 21 F3 8C 58 FE F1 31 F3 63 00 40 00 00 00 00",Charsets.UTF_8,headers);

        /**
         * 0702
         */
        Event eventBeforeIntercept = EventBuilder.withBody("00 50 20 43 44 00 00 4C FF 01 31 01 19 00 FF FF FF FF 97 4C F0 04 00 21 7F 86 D0 15 21 F3 86 4C F0 04 00 61 80 87 D0 15 21 F3 87 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 21 82 89 D8 15 21 F3 89 4C FF 03 31 C6 3F A0 1F C3 13 FF 46 4C F0 04 00 71 84 88 E8 15 21 F3 8A 4C FF 03 31 C6 3F A0 1F C3 13 FF 57 4C FF 01 31 01 52 00 FF FF FF FF 97 4C F0 03 31 F1 06 09 FF FF FF FF FF 4C F0 04 00 51 85 8A 04 16 21 F3 8A 4C FF 03 31 C6 3F A0 1F C3 13 FF 60 4C F0 04 00 11 85 8A 18 16 21 F3 8A 4C F0 04 00 71 85 89 2C 16 21 F3 89 4C FF 01 31 01 3C 00 FF FF FF FF 97 4C F0 04 00 71 84 89 2C 16 21 F3 88 4C FF 03 31 C6 3F A0 1F C3 13 FF 71 4C FF 01 31 01 1E 00 FF FF FF FF 97 4C F0 03 31 F1 04 06 FF FF FF FF FF 4C F0 04 00 71 84 87 44 16 21 F3 87 4C FF 01 31 01 16 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 21 80 84 80 16 21 F3 83 4C FF 01 31 01 0F 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 24 4C F0 03 31 F1 02 03 FF FF FF FF FF 4C F0 04 00 61 7F 80 A0 16 21 F3 80 4C FF 01 31 01 0B 00 FF FF FF FF 97 4C F0 04 00 41 7F 80 A0 16 21 F3 7F 4C FF 01 31 01 09 00 FF FF FF FF 97 4C F0 03 31 F1 00 01 FF FF FF FF FF 4C FF 01 31 01 15 00 FF FF FF FF 97 4C F0 04 00 21 7D 02 7F 9C 16 21 F3 7F 58 FE F1 31 F3 B9 00 40 00 00 00 00 4C FF 03 31 C6 3F A0 1F C3 13 FF 35 58 FE DF 00 84 50 02 11 7D 01 04 05 FF 58 FE EF 00 00 FF FF 2F FF FF FF 02 4C F0 04 00 41 8D 8D 68 1F 21 F3 8D 4C FF 01 31 01 3D 01 FF FF FF FF 97 4C F0 04 00 41 8F 8D 5C 1F 21 F3 8F 4C FF 01 31 01 59 01 FF FF FF FF 97 4C F0 04 00 51 92 8F 54 1F 21 F3 92 4C F0 03 31 F0 1E 1B FF FF FF FF FF 4C FF 01 31 01 69 01 FF FF FF FF 97 4C F0 04 00 51 94 94 58 1F 21 F3 94 4C FF 01 31 01 73 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 46 4C F0 04 00 71 96 96 64 1F 21 F3 96 4C F0 03 31 F0 23 1F FF FF FF FF FF 4C FF 03 31 C6 3F A0 1F C3 13 FF 02 4C F0 04 00 71 98 98 8C 1F 21 F3 98 4C F0 04 00 01 9A 9A B0 1F 21 F3 9A 4C FF 01 31 01 70 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 13 4C F0 04 00 01 9A 9A C4 1F 21 F3 9A 4C FF 01 31 01 25 01 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 24 4C F0 03 31 F0 21 1D FF FF FF FF FF 4C F0 04 00 51 99 9A 08 20 21 F3 99 4C FF 01 31 01 E9 00 FF FF FF FF 97 4C F0 04 00 01 98 9A 50 20 21 F3 98 58 FE F1 31 F3 63 00 40 00 00 00 00 4C F0 04 00 21 98 98 80 20 21 F3 98 4C F0 04 00 71 95 95 9C 20 21 F3 95 4C F0 04 00 71 95 95 B8 20 21 F3 95 4C FF 01 31 01 A9 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 35 4C F0 04 00 11 93 93 CC 20 21 F3 93 4C FF 01 31 01 57 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 60 4C F0 04 00 11 93 93 D4 20 21 F3 93 4C F0 04 00 41 90 90 D0 20 21 F3 90 4C FF 01 31 01 3E 00 FF FF FF FF 97 4C FF 03 31 C6 3F A0 1F C3 13 FF 71 4C F0 04 00 11 8E 8E C8 20 21 F3 8E 4C FF 01 31 01 2C 00 FF FF FF FF 97 4C F0 03 31 F0 10 0E FF FF FF FF FF 4C FF 01 31 01 1F 00 FF FF FF FF 97 4C F0 04 00 01 8C 8C B4 20 21 F3 8C 58 FE F1 31 F3 63 00 40 00 00 00 00",Charsets.UTF_8,headers);
        Event eventAfterIntercept = interceptor.intercept(eventBeforeIntercept);

        String eventBody = new String(eventAfterIntercept.getBody());
        logger.info("...\n " + eventBody);

    }

    //"0200"-> 0x0200
    public void returnShort() {
        long x = 0x9d53069fL;
        System.out.println(x);
    }

    public void test() {
        String className = "com.dfssi.dataplatform.datasync.plugin.interceptor.ProtocolInterceptor$Builder";
        try {
            Class<?> builder = Class.forName(className);
            // Method method = builder.getMethod("intercept");
            //int parameterCount = method.getParameterCount();
            logger.info("builder " + builder.getDeclaredFields().length);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void readRealVehicle() {
        //InputStream resourceAsStream = TestProtocolInterceptor.class.getClassLoader().getResourceAsStream("鄂YPT0002-YPT0002 NERTVM02.log");

        //Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
        //InterceptorType.toString());

        //InputStream resourceAsStream = TestProtocolInterceptor.class.getClassLoader().getResourceAsStream("LGAG4DY30H8010711-H4116022D01.log");
    InputStream resourceAsStream = null;
    try {
        resourceAsStream = new FileInputStream(new File("F:\\鄂YPT0004-YPT0005.log"));
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    Interceptor interceptor = new ProtocolInterceptor();
        Map<String, String> headers;

        try {
            LineIterator lineIterator = IOUtils.lineIterator(resourceAsStream, Charsets.UTF_8);
            while (lineIterator.hasNext()) {
                String text = lineIterator.next();
                //logger.info(text);
                if (text.contains("7E 02 00")) {
                    logger.info("7E 02 00 index of " + text.indexOf("7E 02 00"));
                    String anaText = text.substring(text.indexOf("7E 02 00")).replace(StringUtils.SPACE, StringUtils.EMPTY);
                    anaText = anaText.substring(26);
                    logger.info(anaText);

                    headers = Maps.newHashMap();
                    headers.put("taskId", "001");
                    headers.put("msgId", "02000");
                    /**
                     * 0200
                     */
                    Event eventBeforeIntercept = EventBuilder.withBody(anaText, Charsets.UTF_8, headers);
                    Event eventAfterIntercept = interceptor.intercept(eventBeforeIntercept);
                    String eventBody = new String(eventAfterIntercept.getBody());
                    logger.info("<<<<<" + eventBody);
                    FileUtils.write(new File("f:\\parse\\sample0200.txt"),eventBody,true);
                    FileUtils.write(new File("f:\\parse\\sample0200.txt"),"\r\n",true);

                } else if (text.contains("7E 07 04")) {
                    String anaText = text.substring(text.indexOf("7E 07 04")).replace(StringUtils.SPACE, StringUtils.EMPTY);
                    anaText = anaText.substring(26);
                    logger.info(anaText);

                    headers = Maps.newHashMap();
                    headers.put("taskId", "002");
                    headers.put("msgId", "07040");
                    /**
                     * 0704
                     */
                    Event eventBeforeIntercept = EventBuilder.withBody(anaText, Charsets.UTF_8, headers);
                    Event eventAfterIntercept = interceptor.intercept(eventBeforeIntercept);
                    String eventBody = new String(eventAfterIntercept.getBody());
                    logger.info("<<< \n " + eventBody);
                    FileUtils.write(new File("f:\\parse\\sample0704.txt"),eventBody,true);
                    FileUtils.write(new File("f:\\parse\\sample0704.txt"),"\r\n",true);

                } else if (text.contains("7E 07 05")) {
                    String anaText = text.substring(text.indexOf("7E 07 05")).replace(StringUtils.SPACE, StringUtils.EMPTY);
                    anaText = anaText.substring(26); //34
                    logger.info(anaText);
                    headers = Maps.newHashMap();
                    headers.put("taskId", "003");
                    headers.put("msgId", "0705");
                    /**
                     * 0705 can protocol
                     */
                    Event eventBeforeIntercept = EventBuilder.withBody(anaText, Charsets.UTF_8, headers);
                    Event eventAfterIntercept = interceptor.intercept(eventBeforeIntercept);
                    String eventBody = new String(eventAfterIntercept.getBody());
                    logger.info("<<< \n " + eventBody);
                    FileUtils.write(new File("f:\\parse\\sample0705.txt"),eventBody,true);
                    FileUtils.write(new File("f:\\parse\\sample0705.txt"),"\r\n",true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    private void getTime(){
    String time=DateFormatUtils.format(Calendar.getInstance(),"yyyyMMdd HH:mm:ss");
        logger.info("time:{}",time);
    }
}
