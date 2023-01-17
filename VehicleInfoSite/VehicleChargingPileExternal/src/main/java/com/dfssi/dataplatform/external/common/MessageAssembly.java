package com.dfssi.dataplatform.external.common;

import java.io.*;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/9/28 14:40
 */
public class MessageAssembly {
    public static void main(String[] args) throws Exception {
        MessageAssembly messageassembly = new MessageAssembly();
        messageassembly.process("1", 200);
    }

    public void process(String id, int count) throws Exception {
        File file = new File("D://1.txt");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 1; i <= count; i++) {
                String data = "{\"StationInfo\": {\"StationID\": \"" + id + i + "\",\"OperatorID\": \"123456789\",\"EquipmentOwnerID\": \"123456789\",\"StationName\": \"\\u5145\\u7535\\u7ad9\\u540d\\u79f0\",\"CountryCode\": \"UK\",\"AreaCode\": \"310107\",\"Address\": \"\\u5730\\u5740\",\"StationTel\": \"13999999999\",\"ServiceTel\": \"13888888888\",\"StationType\": 1,\"StationStatus\": 50,\"ParkNums\": 3,\"StationLng\": 119.97049,\"StationLat\": 31.717877,\"SiteGuide\": \"111111\",\"OpenAllDay\": 1,\"MinElectricityPrice\": 5.5,\"Construction\": 0,\"ParkFree\": 1,\"Pictures\":[\"111\",\"222\"],\"Payment\": \"1\",\"SupportOrder\": 1,\"EquipmentInfos\": [{\"EquipmentID\": \"" + id + i + "\",\"EquipmentName\": \"电桩 007\",\"ManufacturerID\": \"123456789\",\"EquipmentModel\": \"p3\",\"ProductionDate\": \"2016-04-26\",\"EquipmentType\": 3,\"EquipmentStatus \": 50,\"EquipmentPower \": 3.3,\"NewNationalStandard\": 1,\r\n" +
                        "\"ConnectorInfos\": [{\"ConnectorID\": \"" + id + i + "\",\"ConnectorName\": \"枪 7\",\"ConnectorType\": 1,\"VoltageUpperLimits\": 220,\"VoltageLowerLimits\": 330,\"Current\": 15,\"Power\": 6.6}]}]}}	";
                String enString = AESOperator.getInstance().encrypt(data,"123456789").replaceAll("\r|\n", "");
                StringBuilder builder = new StringBuilder();
                builder.append("731043872").append(enString).append("20180602164111").append("0001");
                String cc = builder.toString();
                String Sig = HMacMD5.getHmacMd5Str(PubGlobal.secretMap.get("123456789").get("SignSecret"), builder.toString());
                String ccc = enString + "," + Sig;
                System.out.println(enString + "," + Sig);
                fos.write(ccc.getBytes());
                fos.write("\r\n".getBytes());
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

        }
    }
    public void process2(String id, int count) throws Exception {
        File file = new File("D://1.txt");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 1; i <= count; i++) {
                String data ="{\"OrderInfo\": {\"OperatorID\" : \"731043872\",\"ConnectorID\" : \"1052\",\"StartChargeSeq\" : \""+id+i+"\",\"UserChargeType\" : 1,\"MobileNumber\" : \"13800138000\",\"Money\" : 20.80,\"ElectMoney\" : 10.80,\"ServiceMoney\" : 10.00,\"Elect\" : 5.8,\"CuspElect\" : 0,\"CuspElectPrice\" : 0,\"CuspServicePrice\" : 0,\"CuspMoney\" : 0,\"CuspElectMoney\" : 0,\"CuspServiceMoney\" : 0,\"PeakElect\" : 0,\"PeakElectPrice\" : 0,\"PeakServicePrice\" : 0,\"PeakMoney\" : 0,\"PeakElectMoney\" : 0,\"PeakServiceMoney\" : 0,\"FlatElect\" : 0,\"FlatElectPrice\" : 0,\"FlatServicePrice\" : 0,\"FlatMoney\" : 0,\"FlatElectMoney\" : 0,\"FlatServiceMoney\" : 0,\"ValleyElect\" : 0,\"ValleyElectPrice\" : 0,\"ValleyServicePrice\" : 0,\"ValleyMoney\" : 0,\"ValleyElectMoney\" : 0,\"ValleyServiceMoney\" : 0,\"StartTime\" : \"2016-08-09 10:00:00\",\"EndTime\" : \"2016-08-09 11:00:00\",\"PaymentAmount\" : \"20.80\",\"PayTime\" : \"2016-08-09 11:05:58\",\"PayChannel\" : 1,\"DiscountInfo\" : \"无\"}}";
                String enString = AESOperator.getInstance().encrypt(data, "731043872").replaceAll("\r|\n", "");
                StringBuilder builder = new StringBuilder();
                builder.append("731043872").append(enString).append("20180602164111").append("0001");
                String cc = builder.toString();
                String Sig = HMacMD5.getHmacMd5Str(PubGlobal.secretMap.get("123456789").get("SignSecret"), builder.toString());
                String ccc = enString + "," + Sig;
                System.out.println(enString + "," + Sig);
                fos.write(ccc.getBytes());
                fos.write("\r\n".getBytes());
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

        }
    }
}
