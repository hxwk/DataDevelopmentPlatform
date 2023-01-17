package com.yaxon.vn.nd.tas;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yaxon.vn.nd.tas.canFileParse.CanFileParse;
import com.yaxon.vn.nd.tas.common.CanConstants;
import com.yaxon.vn.nd.tas.handler.CanInformationPH;
import com.yaxon.vn.nd.tas.util.ByteBufCustomTool;
import com.yaxon.vn.nd.tas.util.FastDFSHandler;
import com.yaxon.vn.nd.tbp.si.CanAnalyzeBeanItem;
import com.yaxon.vn.nd.tbp.si.CanAnalyzeSignal;
import com.yaxon.vn.nd.tbp.si.CanBusParamItem;
import com.yaxon.vn.nd.tbp.si.Req_0705;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static com.yaxon.vn.nd.tas.util.ByteBufUtil.pad;
import static com.yaxon.vn.nd.tas.util.ProtoUtil.readTime1;

/**
 * Created by jian on 2018/1/29.
 */
public class TestCanX {
    private static String AT="@";
    private static Properties prop;
    private static String dbcFastDFSFileId ;
    private static String dbcFormat;

    public static void main(String[] args) {
        CanInformationPH can = new CanInformationPH();
       /* String value = can.readObjectFromRedis("7c4c631ef0bc444684d4d406be17668e");
        System.out.println("value "+value);
        String obj = value;
        List<String> objs;

        if(obj.contains(AT)){
            objs = Arrays.asList(obj.split(AT));
            dbcFastDFSFileId = objs.get(2);
            CanConstants.dbcFastDFSFileId = dbcFastDFSFileId;
            System.out.println("dbcFastDFSFileId "+dbcFastDFSFileId);
        }
        downloadDBCFile(dbcFastDFSFileId);*/
        Map<String,Object> props = readProperty();
        dbcFormat = "old";

        Req_0705 q = new Req_0705();
        CanBusParamItem cpi;
        CanAnalyzeBeanItem analyzeBean =null;
        List<CanAnalyzeSignal> analyzeSignals = Lists.newArrayList();
        /**
         * CanFileParse: Can file parse
         * CanConstants have values
         */
        CanFileParse t38CanParse = new CanFileParse();
        try{
            String sim = "11111111111";
            q.setSim(String.valueOf(sim));
            q.setVid("7c4c631ef0bc444684d4d406be17668e");
            analyzeBean = new CanAnalyzeBeanItem();
            ByteBufCustomTool tool = new ByteBufCustomTool();
            //String body="00 96 00 00 04 00 00 0C F0 03 31 F0 6C 2B FF FF FF FF FF 0C F0 03 31 F0 6C 2B FF FF FF FF FF 0C F0 03 31 F0 6C 2B FF FF FF FF FF 0C F0 03 31 F0 6B 2B FF FF FF FF FF 0C F0 03 31 F0 6B 2A FF FF FF FF FF 0C F0 03 31 F0 6B 2B FF FF FF FF FF 0C F0 03 31 F0 6C 2B FF FF FF FF FF 0C F0 03 31 F0 6D 2B FF FF FF FF FF 0C F0 03 31 F0 6D 2B FF FF FF FF FF 0C F0 03 31 F0 6E 2C FF FF FF FF FF 0C F0 03 31 F0 6D 2B FF FF FF FF FF 0C F0 03 31 F0 6A 2A FF FF FF FF FF 0C F0 03 31 F0 66 29 FF FF FF FF FF 0C F0 03 31 F0 65 28 FF FF FF FF FF 0C F0 03 31 F0 65 28 FF FF FF FF FF 0C F0 03 31 F0 66 28 FF FF FF FF FF 0C F0 03 31 F0 69 2A FF FF FF FF FF 0C F0 03 31 F0 6D 2B FF FF FF FF FF 0C F0 03 31 F0 6E 2C FF FF FF FF FF 0C F0 03 31 F0 6D 2B FF FF FF FF FF 0C F0 03 31 F0 6C 2B FF FF FF FF FF 0C F0 03 31 F0 69 2A FF FF FF FF FF 0C F0 03 31 F0 67 29 FF FF FF FF FF 0C F0 03 31 F0 66 28 FF FF FF FF FF 0C F0 03 31 F0 68 29 FF FF FF FF FF 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 52 04 FF FF FF FF 93 0C FF 01 31 01 4D 04 FF FF FF FF 93 0C FF 01 31 01 48 04 FF FF FF FF 93 0C FF 01 31 01 48 04 FF FF FF FF 93 0C FF 01 31 01 48 04 FF FF FF FF 93 0C FF 01 31 01 4D 04 FF FF FF FF 93 0C FF 01 31 01 4D 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 57 04 FF FF FF FF 93 0C FF 01 31 01 5C 04 FF FF FF FF 93 0C FF 01 31 01 60 04 FF FF FF FF 93 0C FF 01 31 01 65 04 FF FF FF FF 93 0C FF 01 31 01 65 04 FF FF FF FF 93 0C FF 01 31 01 6A 04 FF FF FF FF 93 0C FF 01 31 01 65 04 FF FF FF FF 93 0C FF 01 31 01 60 04 FF FF FF FF 93 0C FF 01 31 01 52 04 FF FF FF FF 93 0C FF 01 31 01 44 04 FF FF FF FF 93 0C FF 01 31 01 30 04 FF FF FF FF 93 0C FF 01 31 01 1D 04 FF FF FF FF 93 0C FF 01 31 01 0F 04 FF FF FF FF 93 0C FF 01 31 01 0A 04 FF FF FF FF 93 0C FF 01 31 01 0A 04 FF FF FF FF 93 0C FF 01 31 01 0A 04 FF FF FF FF 93 0C FF 01 31 01 0F 04 FF FF FF FF 93 0C FF 01 31 01 18 04 FF FF FF FF 93 0C FF 01 31 01 22 04 FF FF FF FF 93 0C FF 01 31 01 35 04 FF FF FF FF 93 0C FF 01 31 01 48 04 FF FF FF FF 93 0C FF 01 31 01 5C 04 FF FF FF FF 93 0C FF 01 31 01 6A 04 FF FF FF FF 93 0C FF 01 31 01 6F 04 FF FF FF FF 93 0C FF 01 31 01 6A 04 FF FF FF FF 93 0C FF 01 31 01 65 04 FF FF FF FF 93 0C FF 01 31 01 5C 04 FF FF FF FF 93 0C FF 01 31 01 52 04 FF FF FF FF 93 0C FF 01 31 01 44 04 FF FF FF FF 93 0C FF 01 31 01 3A 04 FF FF FF FF 93 0C FF 01 31 01 30 04 FF FF FF FF 93 0C FF 01 31 01 27 04 FF FF FF FF 93 0C FF 01 31 01 1D 04 FF FF FF FF 93 0C FF 01 31 01 18 04 FF FF FF FF 93 0C FF 01 31 01 1D 04 FF FF FF FF 93 0C FF 01 31 01 2C 04 FF FF FF FF 93 0C FF 01 31 01 3A 04 FF FF FF FF 93 18 FE F1 31 F3 D4 2C 80 00 00 00 00 18 FE F1 31 F3 CB 2C 80 00 00 00 00 18 FE F1 31 F3 5B 2C 80 00 00 00 00 18 FE F1 31 F3 6E 2C 80 00 00 00 00 18 FE F1 31 F3 09 2D 80 20 08 70 FC";
            String body="00 50 01 36 00 00 00 4C FE 6C EE 3F 3F FF FF 4D 11 33 20 \n" +
                    "4C F0 04 00 51 7D 01 85 D0 15 21 F3 \n" +
                    "85 4C FE 6C EE 3F 3F FF FF 4D 11 33 \n" +
                    "20 4C FF 03 31 C1 3F 00 FF 07 13 FF \n" +
                    "05 4C F0 04 00 41 7D 01 85 D8 15 21 \n" +
                    "F3 85 58 FE F1 31 F3 AA 20 90 00 00 \n" +
                    "00 00 4C FE 6C EE 3F 3F FF FF 8A 11 \n" +
                    "A4 20 4C FE \n" +
                    "6C EE 3F 3F FF FF 8A 11 \n" +
                    "A4 20 4C FF \n" +
                    "03 31 C1 3F 00 FF 07 13 \n" +
                    "FF 16 4C F0 \n" +
                    "04 00 41 7D 01 85 D8 15 \n" +
                    "21 F3 85 4C \n" +
                    "FE 6C EE 3F 3F FF FF 8A \n" +
                    "11 A4 20 4C \n" +
                    "FE 6C EE 3F 3F FF FF 8A \n" +
                    "11 A4 20 4C \n" +
                    "FF 03 31 C1 3F 00 FF 07 \n" +
                    "13 FF 27 58 \n" +
                    "FE F5 3D FF FF FF C3 23 \n" +
                    "FF FF FF 4C \n" +
                    "F0 04 00 61 7D 01 85 D0 \n" +
                    "15 21 F3 85 \n" +
                    "4C F0 03 31 F1 00 00 FF \n" +
                    "FF FF FF FF \n" +
                    "4C F0 04 00 61 7D 01 85 \n" +
                    "CC 15 21 F3 \n" +
                    "85 58 FE F6 00 00 35 3F \n" +
                    "35 FF 20 22 \n" +
                    "FF 4C FE 6C EE 3F 3F FF \n" +
                    "FF 51 11 3A \n" +
                    "20 4C F0 03 31 F1 00 00 \n" +
                    "FF FF FF FF FF 4C F0 04 00 71 7D 01 85 C8 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 4B 11 2E 20 58 FE F1 31 F3 A4 20 90 00 00 00 00 4C FF 03 31 C1 3F 00 FF 07 13 FF 20 4C F0 04 00 01 7D 01 86 C8 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 8D 11 AA 20 4C F0 04 00 01 7D 01 86 CC 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 8D 11 AA 20 4C F0 04 00 01 7D 01 86 CC 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 5D 11 50 20 4C FE 6C EE 3F 3F FF FF 5D 11 50 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 05 4C F0 04 00 71 7D 01 85 D0 15 21 F3 85 4C F0 04 00 71 7D 01 85 D4 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 5D 11 50 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 16 4C F0 04 00 51 7D 01 85 DC 15 21 F3 85 4C F0 04 00 61 7D 01 85 DC 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 37 11 09 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 27 4C F0 04 00 61 7D 01 85 D8 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 40 11 1B 20 4C F0 03 31 F1 00 00 FF FF FF FF FF 4C F0 04 00 61 7D 01 85 D8 15 21 F3 85 58 F0 01 0B C0 FF C0 FF FF 0D FF FF 4C FE 6C EE 3F 3F FF FF 40 11 1B 20 4C F0 04 00 01 7D 01 86 D0 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 89 11 A2 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 20 4C F0 04 00 01 7D 01 86 D0 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 89 11 A2 20 4C F0 03 31 F1 00 00 FF FF FF FF FF 4C F0 04 00 01 7D 01 86 D0 15 21 F3 86 4C FF 03 31 C1 3F 00 FF 07 13 FF 52 4C F0 04 00 01 7D 01 86 D0 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 89 11 A2 20 4C F0 04 00 01 7D 01 86 D0 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 71 11 75 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 63 4C F0 04 00 11 7D 01 86 D0 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 4F 11 35 20 4C FE 6C EE 3F 3F FF FF 4F 11 35 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 74 4C F0 04 00 11 7D 01 86 D0 15 21 F3 86 58 FE F1 31 F3 AA 20 90 00 00 00 00 4C F0 04 00 11 7D 01 86 D0 15 21 F3 86 58 F0 01 0B C0 FF C0 FF FF 0D FF FF 4C FE 6C EE 3F 3F FF FF 4F 11 35 20 4C FF 03 31 C1 3F 00 FF 07 13 FF 05 4C FE 6C EE 3F 3F FF FF 82 11 95 20 4C F0 03 31 F1 00 00 FF FF FF FF FF 4C F0 04 00 01 7D 01 86 D4 15 21 F3 86 4C FE 6C EE 3F 3F FF FF 82 11 95 20 4C FE 6C EE 3F 3F FF FF 82 11 95 20 4C F0 04 00 01 7D 01 86 D8 15 21 F3 85 58 FE EF 00 00 FF FF 36 FF FF FF 02 4C FE 6C EE 3F 3F FF FF 82 11 95 20 4C F0 04 00 61 7D 01 85 E0 15 21 F3 85 4C FE 6C EE 3F 3F FF FF 59 11 49 20 4C F0 03 31 F1 00 00 FF FF FF FF FF 65 7E";
            //String body="00 02 10 00 03 62 10 18 FE C1 EE 00 00 00 04 00 00 00 02";
            //0C F0 04 00 01 02 03 00 04 00 00 05
            ByteBuf byteBuf = tool.hexStringToByteBuf(body);
            ByteBuf msgData = byteBuf;
            int dataCount = msgData.readShort()& 0xFFFF;
            analyzeBean.setItemNum(dataCount);
            Date receiveTime = readTime1(msgData.readBytes(5));
            analyzeBean.setReceiveTime(receiveTime==null?0L:receiveTime.getTime());
            q.setCanBusDataReceiveTime(receiveTime==null?"0L":String.valueOf(receiveTime.getTime()));
            int itemNum = 0;
            while(msgData.readableBytes()>=12 && itemNum<=dataCount){
                cpi = new CanBusParamItem();
                String canID = Integer.toHexString(msgData.readInt());
                String canData = pad(Long.toHexString(msgData.readLong()),16,true);
                cpi.setCanId(canID);
                cpi.setCanData(canData);
                analyzeSignals.addAll(t38CanParse.getAnalyzeSignalList(cpi, dbcFormat));
                itemNum++;
            }
        }catch (Exception e){
            return;
        }finally {
            analyzeBean.setMsgId(String.valueOf("0705"));
            analyzeBean.setMessageBeanList(analyzeSignals);
            q.setCanBusParamItems(analyzeSignals);
            System.out.println(q);

        }

    }

    /**
     * 将dbc文件下载到 user.home 目录下,通过dbcFastDFSFileId
     * @param dbcFastDFSFileId
     */
    private static void downloadDBCFile(String dbcFastDFSFileId) {
        try {
            FastDFSHandler.download2LocalWithFileId(dbcFastDFSFileId);
        } catch (Exception ex) {
        }
    }

    private static synchronized Map<String,Object> readProperty(){
        Map<String,Object> props = Maps.newConcurrentMap();
        String fileIdName;
        prop = new Properties();
        try {
            String classRootPath = CanInformationPH.class.getClassLoader().getResource("").getFile();
            prop.load(new FileInputStream(new File(classRootPath, "dbc.properties")));

        }catch (Exception ex){

        }

        CanConstants.dbcFormat = "old";
        CanConstants.charSet = "utf-8";
        CanConstants.fileIdName = "rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc";

        if(null!=dbcFastDFSFileId&& !StringUtils.EMPTY.equals(dbcFastDFSFileId)){
            fileIdName =dbcFastDFSFileId.substring(dbcFastDFSFileId.lastIndexOf("/")+1);
            props.put("dbcFormat",prop.getProperty("dbcFormat"));
            props.put("charset",prop.getProperty("charset"));
            props.put("dbcfile",fileIdName);
            CanConstants.dbcFormat = prop.getProperty("dbcFormat");
            CanConstants.charSet = prop.getProperty("charset");
            CanConstants.fileIdName = "rBAByVppoEmAeFn0AAAY9ktMiAQ724.dbc";
        }
        return props;
    }
}
