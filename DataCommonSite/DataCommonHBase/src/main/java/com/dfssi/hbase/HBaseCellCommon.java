package com.dfssi.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/10/11 13:45
 */
public class HBaseCellCommon {

    private HBaseCellCommon(){};

    public static byte[] getCellByteFamily(Cell cell){
        return Bytes.copy(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
    }

    public static String getCellStringFamily(Cell cell){
        return Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
    }

    public static byte[] getCellByteQualifier(Cell cell){
        return Bytes.copy(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
    }

    public static String getCellStringQualifier(Cell cell){
        return Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
    }

    public static byte[] getCellByteRowKey(Cell cell){
        return  Bytes.copy(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
    }

    public static String getCellStringRowKey(Cell cell){
        return  Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
    }

    public static long getTimeStamp(Cell cell){
        return cell.getTimestamp();
    }

    public static byte[] getCellByteValue(Cell cell){
        return  Bytes.copy(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
    }

    public static String getCellStringValue(Cell cell){
        return  Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
    }

    public static long getCellLongValue(Cell cell){
        return  Bytes.toLong(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
    }

    public static int getCellIntValue(Cell cell){
        return  Bytes.toInt(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
    }
}
