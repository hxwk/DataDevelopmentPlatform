package com.dfssi.dataplatform.external.common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AESOperator {

    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    /*private String sKey = "1234567890abcdef";//key，可自行修改
    private String ivParameter = "1234567890abcdef";//偏移量,可自行修改*/
    private static AESOperator instance = null;

    private AESOperator() {

    }

    public static AESOperator getInstance() {
        if (instance == null)
            instance = new AESOperator();
        return instance;
    }

    public static String Encrypt(String encData, String secretKey, String vector) throws Exception {

        if (secretKey == null) {
            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
    }


    /**
     * 加密
     *
     * @param sSrc
     * @return
     * @throws Exception
     */
    public String encrypt(String sSrc,String operatorId) throws Exception {

        String dataSecret= PubGlobal.secretMap.get(operatorId).get("DataSecret");
        String dataSecretIv=PubGlobal.secretMap.get(operatorId).get("DataSecretIV");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        byte[] raw = sKey.getBytes();
        byte[] raw = dataSecret.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
//        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        IvParameterSpec iv = new IvParameterSpec(dataSecretIv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new BASE64Encoder().encode(encrypted).replaceAll("\r|\n", "");// 此处使用BASE64做转码。
    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     * @throws Exception
     */
    public String decrypt(String sSrc,String operatorId) throws Exception {
        try {
//            byte[] raw = sKey.getBytes("ASCII");
            byte[] raw = PubGlobal.secretMap.get(operatorId).get("DataSecret").getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            IvParameterSpec iv = new IvParameterSpec(PubGlobal.secretMap.get(operatorId).get("DataSecretIV").getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public String decrypt(String sSrc, String key, String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static void main(String[] args) throws Exception {
        // 需要加密的字串
        String cSrc = "[{\"request_no\":\"1001\",\"service_code\":\"FS0001\",\"contract_id\":\"100002\",\"order_id\":\"0\",\"phone_id\":\"13913996922\",\"plat_offer_id\":\"100094\",\"channel_id\":\"1\",\"activity_id\":\"100045\"}]";

//        cSrc = "{\"total\":1,\"stationStatusInfo\":{\"operationID\":\"123456789\",\"stationID\":\"111111111111111\",\"connectorStatusInfos\":{\"connectorID\":1,\"equipmentID\":\"10000000000000000000001\",\"status\":4,\"currentA\":0,\"currentB\":0,\"currentC\":0,\"voltageA\":0,\"voltageB\":0,\"voltageC\":0,\"soc\":10,}}}";
        cSrc="{\"StationInfo\": {\"StationID\": \"a1\",\"OperatorID\": \"123456789\",\"EquipmentOwnerID\": \"123456789\",\"StationName\": \"\\u5145\\u7535\\u7ad9\\u540d\\u79f0\",\"CountryCode\": \"CN\",\"AreaCode\": \"310107\",\"Address\": \"\\u5730\\u5740\",\"StationTel\": \"123456789\",\"ServiceTel\": \"123456789\",\"StationType\": 1,\"StationStatus\": 50,\"ParkNums\": 3,\"StationLng\": 119.97049,\"StationLat\": 31.717877,\"SiteGuide\": \"111111\",\"OpenAllDay\": 1,\"MinElectricityPrice\": 5.5,\"Construction\": 0,\"ParkFree\": 1,\"Pictures\":[\"http:\\/\\/www.xxx.com\\/uploads\\/plugs\\/e5\\/eb\\/cd\\/f0469308d9bbd99496618d6d87\",\"http:\\/\\/www.xxx.com\\/uploads\\/plugs\\/7c\\/0c\\/81\\/a8ed867ffdfb597abaf9982b2c\"],\"Payment\": \"1\",\"SupportOrder\": 1,\"EquipmentInfos\": [{\"EquipmentID\": \"a1\",\"EquipmentName\": \"电桩 001\",\"ManufacturerID\": \"123456789\",\"EquipmentModel\": \"p3\",\"ProductionDate\": \"2016-04-26\",\"EquipmentType\": 3,\"EquipmentStatus \": 50,\"EquipmentPower \": 3.3,\"NewNationalStandard\": 1,\"ConnectorInfos\": [{\"ConnectorID\": \"a1\",\"ConnectorName\": \"枪 1\",\"ConnectorType\": 1,\"VoltageUpperLimits\": 220,\"VoltageLowerLimits\": 220,\"Current\": 15,\"Power\": 3.3}]}]}}";
//        cSrc="{\"ConnectorStatusInfo\": {\"ConnectorID\": \"1\",\"Status\": 4,\"CurrentA\": 0,\"CurrentB\": 0,\"CurrentC\": 0,\"VoltageA\": 0,\"VoltageB\": 0,\"VoltageC\": 0,\"ParkStatus\": 10,\"LockStatus\": 10,\"SOC\": 10}}";
        // 加密
        long lStart = System.currentTimeMillis();
        String enString = AESOperator.getInstance().encrypt(cSrc, "123456789");
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
//        String DeString = AESOperator.getInstance().decrypt(enString);
//        String ccc="525AE30D815E05BE1F23F24127DCF2D2";
        String ccc="Bq+0LsmzLSCEoMjxBr7JEZiqs8Goy0eNs0xUSBu5loQOXwSXF993QRZOTTiTspAI1DbrtxsFzRJMAJyEdDW41/06wCaDhxqRbvmb5d8egxukXVo1qIsbKdN7utRpaIFleKJ7uDDbN4CmbXkODqg+3A==";
        String DeString = AESOperator.getInstance().decrypt(ccc,"123456789");
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }

}
