package com.dfssi.dataplatform.datasync.plugin.sink.hbase;

import com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.ByteUtils;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf.ParameterConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import static com.dfssi.dataplatform.datasync.plugin.sink.hbase.common.HbaseUtils.logger;

public class RuleRowKeyGenerator {
    public static  byte[] rowkeyRule(String rule,String ruleParam,Object field,String separator ,Object... concat){
        if(field==null||concat==null) {
            logger.warn("field,concat 不能为空" );
            return null;
        }
        String hashPrefix = null;
        Integer param = null;
        int paramLength = 0;
        int modLength =0;
        long  mod =0;
        byte[] rowkey =null;
        if("default".equals(separator)){
            separator = "";
        }
        byte[] bytes =null;
        if(field!=null) {
            bytes= ByteUtils.toBytes(field);
        }
        if(StringUtils.isNotBlank(ruleParam)) {
            try {
                param = Integer.valueOf(ruleParam);
            } catch (Exception e) {
                logger.error("ruleParam 格式错误", e.getMessage());
            }
        }
        switch (rule){
            //md5 加密
            case ParameterConstant.ROWKEY_RULE_HASH:
                if(StringUtils.isNotBlank(ruleParam)&&param!=null) {
                    hashPrefix = MD5Hash.getMD5AsHex(bytes).substring(0,param);
                }else{
                    hashPrefix = MD5Hash.getMD5AsHex(bytes).substring(0, 5);//默认去五位
                }
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //mod 取模
            case ParameterConstant.ROWKEY_RULE_MOD:
                if(param==null){
                    param = 1000;
                }

                if(field instanceof Integer ||field instanceof Short || field instanceof Long || field instanceof Byte ){
                    paramLength = String.valueOf(param).length();
                    mod =  Math.floorMod(  Long.valueOf(String.valueOf(field)),param);
                    modLength = String.valueOf(mod).length();
                }else{
                    int hashCode = field.hashCode();
                    paramLength = String.valueOf(param).length();
                    mod =  Math.floorMod(  hashCode,Long.valueOf(param));
                    modLength = String.valueOf(mod).length();
                }
                if(paramLength==modLength){
                    hashPrefix=""+mod;
                }else {
                    for (int i = 0; i < paramLength - modLength; i++) {
                        if (i == 0) {
                            hashPrefix = "0" + mod;
                        } else {
                            hashPrefix = "0" + hashPrefix;
                        }
                    }
                }

                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //mod 随机数
            case ParameterConstant.ROWKEY_RULE_RANDOM:
                if(param==null){
                    param = 1000;
                }
                mod = Math.floorMod(Math.round( Math.random()*100000),param);
                paramLength = String.valueOf(param).length();
                modLength = String.valueOf(mod).length();
                if(paramLength==modLength){
                    hashPrefix=""+mod;
                }else {
                    for (int i = 0; i < paramLength - modLength; i++) {
                        if (i == 0) {
                            hashPrefix = "0" + mod;
                        } else {
                            hashPrefix = "0" + hashPrefix;
                        }
                    }
                }
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //默认时间戳反转
            case ParameterConstant.ROWKEY_RULE_INVERTED:
                ;
                //;break;
                //时间戳反转
            case ParameterConstant.ROWKEY_RULE_INVERTED_TIME:
                hashPrefix =  new StringBuilder(String.valueOf(field)).reverse().toString();
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                Long time =Long.MAX_VALUE - Long.valueOf(String.valueOf(concat[0]));
                byte[] bytes2 = Bytes.toBytes(hashPrefix);
                bytes = ByteUtils.toBytes(time);
                rowkey = Bytes.add(bytes2, bytes);
                ;break;
            //字符串反转
            case ParameterConstant.ROWKEY_RULE_INVERTED_STRING:
                hashPrefix =  new StringBuilder(String.valueOf(field)).reverse().toString();
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;
            //截取
            case ParameterConstant.ROWKEY_RULE_SUBSTRING:
                if(param==null)
                    param = 1;
                hashPrefix = String.valueOf(field).substring(0,param);
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                ;break;

            default:
                hashPrefix=String.valueOf(field);
                if(hashPrefix!=null&&StringUtils.isNotBlank(separator)){
                    hashPrefix=hashPrefix+separator;
                }
                break;

            //

        }
        //链接key
        if(rowkey==null) {
            byte[] bytes2 = Bytes.toBytes(hashPrefix);
            if (concat == null || concat.length == 0) {
                rowkey = bytes2;//Bytes.add(bytes2, bytes);
            } else {
                for (int i = 0; i < concat.length; i++) {
                    Object obj = concat[i];
                    bytes = ByteUtils.toBytes(obj);
                    if (i == 0) {
                        rowkey = Bytes.add(bytes2, bytes);
                    } else {
                        rowkey = Bytes.add(rowkey, bytes);
                    }
                }
            }
        }
        return rowkey;
    }
}
