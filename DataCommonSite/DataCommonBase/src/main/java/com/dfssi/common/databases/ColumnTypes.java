/*
 * Copyright (c) 1996, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.dfssi.common.databases;

import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.Map;

/**
 * <P>The class that defines the constants that are used to identify generic
 * SQL types, called JDBC types.
 * <p>
 * This class is never instantiated.
 */
public class ColumnTypes {

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BIT</code>.
     */
    public final static int BIT             =  -7;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TINYINT</code>.
     */
    public final static int TINYINT         =  -6;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>SMALLINT</code>.
     */
    public final static int SMALLINT        =   5;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>INTEGER</code>.
     */
    public final static int INTEGER         =   4;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BIGINT</code>.
     */
    public final static int BIGINT          =  -5;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>FLOAT</code>.
     */
    public final static int FLOAT           =   6;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>REAL</code>.
     */
    public final static int REAL            =   7;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DOUBLE</code>.
     */
    public final static int DOUBLE          =   8;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>NUMERIC</code>.
     */
    public final static int NUMERIC         =   2;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DECIMAL</code>.
     */
    public final static int DECIMAL         =   3;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>CHAR</code>.
     */
    public final static int CHAR            =   1;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>VARCHAR</code>.
     */
    public final static int VARCHAR         =  12;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>LONGVARCHAR</code>.
     */
    public final static int LONGVARCHAR     =  -1;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DATE</code>.
     */
    public final static int DATE            =  91;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TIME</code>.
     */
    public final static int TIME            =  92;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TIMESTAMP</code>.
     */
    public final static int TIMESTAMP       =  93;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BINARY</code>.
     */
    public final static int BINARY          =  -2;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>VARBINARY</code>.
     */
    public final static int VARBINARY       =  -3;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>LONGVARBINARY</code>.
     */
    public final static int LONGVARBINARY   =  -4;

    /**
     * <P>The constant in the Java programming language
     * that identifies the generic SQL value
     * <code>NULL</code>.
     */
    public final static int NULL            =   0;

    /**
     * The constant in the Java programming language that indicates
     * that the SQL type is database-specific and
     * gets mapped to a Java object that can be accessed via
     * the methods <code>getObject</code> and <code>setObject</code>.
     */
    public final static int OTHER           = 1111;



    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>JAVA_OBJECT</code>.
     * @since 1.2
     */
    public final static int JAVA_OBJECT         = 2000;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>DISTINCT</code>.
     * @since 1.2
     */
    public final static int DISTINCT            = 2001;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>STRUCT</code>.
     * @since 1.2
     */
    public final static int STRUCT              = 2002;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>ARRAY</code>.
     * @since 1.2
     */
    public final static int ARRAY               = 2003;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>BLOB</code>.
     * @since 1.2
     */
    public final static int BLOB                = 2004;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>CLOB</code>.
     * @since 1.2
     */
    public final static int CLOB                = 2005;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type
     * <code>REF</code>.
     * @since 1.2
     */
    public final static int REF                 = 2006;

    /**
     * The constant in the Java programming language, somtimes referred to
     * as a type code, that identifies the generic SQL type <code>DATALINK</code>.
     *
     * @since 1.4
     */
    public final static int DATALINK = 70;

    /**
     * The constant in the Java programming language, somtimes referred to
     * as a type code, that identifies the generic SQL type <code>BOOLEAN</code>.
     *
     * @since 1.4
     */
    public final static int BOOLEAN = 16;

    //------------------------- JDBC 4.0 -----------------------------------

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>ROWID</code>
     *
     * @since 1.6
     *
     */
    public final static int ROWID = -8;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NCHAR</code>
     *
     * @since 1.6
     */
    public static final int NCHAR = -15;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NVARCHAR</code>.
     *
     * @since 1.6
     */
    public static final int NVARCHAR = -9;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>LONGNVARCHAR</code>.
     *
     * @since 1.6
     */
    public static final int LONGNVARCHAR = -16;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>NCLOB</code>.
     *
     * @since 1.6
     */
    public static final int NCLOB = 2011;

    /**
     * The constant in the Java programming language, sometimes referred to
     * as a type code, that identifies the generic SQL type <code>XML</code>.
     *
     * @since 1.6
     */
    public static final int SQLXML = 2009;


    public static String typeName(int typeNum){
        switch (typeNum){
            case -7 : return "BIT";
            case -6 : return "TINYINT";
            case 5 : return "SMALLINT";
            case 4 : return "INTEGER";
            case -5 : return "BIGINT";
            case 6 : return "FLOAT";
            case 7 : return "REAL";
            case 8 : return "DOUBLE";
            case 2 : return "NUMERIC";
            case 3 : return "DECIMAL";
            case 1 : return "CHAR";
            case 12 : return "VARCHAR";
            case -1 : return "LONGVARCHAR";
            case 91 : return "DATE";
            case 92 : return "TIME";
            case 93 : return "TIMESTAMP";
            case -2 : return "BINARY";
            case -3 : return "VARBINARY";
            case -4 : return "LONGVARBINARY";
            case 0 : return "NULL";
            case 1111 : return "OTHER";
            case 2000 : return "JAVA_OBJECT";
            case 2001 : return "DISTINCT";
            case 2002 : return "STRUCT";
            case 2003 : return "ARRAY";
            case 2004 : return "BLOB";
            case 2005 : return "CLOB";
            case 2006 : return "REF";
            case 70 : return "DATALINK";
            case 16 : return "BOOLEAN";
            case -8 : return "ROWID";
            case -15 : return "NCHAR";
            case -9 : return "NVARCHAR";
            case -16 : return "LONGNVARCHAR";
            case 2011 : return "NCLOB";
            case 2009 : return "SQLXML";
            default:
                throw new IllegalArgumentException(String.format("不是的字段数据：%s", typeNum));
        }
    }

    public static int typeNum(String typeName){

        switch (typeName){
            case "BIT" : return -7;
            case "TINYINT" : return -6;
            case "SMALLINT" : return 5;
            case "INTEGER" : return 4;
            case "BIGINT" : return -5;
            case "FLOAT" : return 6;
            case "REAL" : return 7;
            case "DOUBLE" : return 8;
            case "NUMERIC" : return 2;
            case "DECIMAL" : return 3;
            case "CHAR" : return 1;
            case "VARCHAR" : return 12;
            case "LONGVARCHAR" : return -1;
            case "DATE" : return 91;
            case "TIME" : return 92;
            case "TIMESTAMP" : return 93;
            case "BINARY" : return -2;
            case "VARBINARY" : return -3;
            case "LONGVARBINARY" : return -4;
            case "NULL" : return 0;
            case "OTHER" : return 1111;
            case "JAVA_OBJECT" : return 2000;
            case "DISTINCT" : return 2001;
            case "STRUCT" : return 2002;
            case "ARRAY" : return 2003;
            case "BLOB" : return 2004;
            case "CLOB" : return 2005;
            case "REF" : return 2006;
            case "DATALINK" : return 70;
            case "BOOLEAN" : return 16;
            case "ROWID" : return -8;
            case "NCHAR" : return -15;
            case "NVARCHAR" : return -9;
            case "LONGNVARCHAR" : return -16;
            case "NCLOB" : return 2011;
            case "SQLXML" : return 2009;
            default:
                throw new IllegalArgumentException(String.format("不是的字段类型名称：%s", typeName));
        }
    }


    private static Map<Integer, Class<?>> jdbcJavaTypes = Maps.newHashMap(); // jdbc type to java type

    static {

        // 初始化jdbcJavaTypes：
        jdbcJavaTypes.put(LONGNVARCHAR, String.class);  // -16 字符串
        jdbcJavaTypes.put(NCHAR, String.class);    // -15 字符串
        jdbcJavaTypes.put(NVARCHAR, String.class);   // -9 字符串
        jdbcJavaTypes.put(ROWID, String.class);    // -8 字符串
        jdbcJavaTypes.put(BIT, Boolean.class);    // -7 布尔
        jdbcJavaTypes.put(TINYINT, Byte.class);    // -6 数字
        jdbcJavaTypes.put(BIGINT, Long.class);    // -5 数字 
        jdbcJavaTypes.put(LONGVARBINARY, Blob.class);  // -4 二进制
        jdbcJavaTypes.put(VARBINARY, Blob.class);   // -3 二进制
        jdbcJavaTypes.put(BINARY, Blob.class);    // -2 二进制
        jdbcJavaTypes.put(LONGVARCHAR, String.class);  // -1 字符串
        jdbcJavaTypes.put(NULL, String.class);    // 0 /
        jdbcJavaTypes.put(CHAR, String.class);    // 1 字符串
        jdbcJavaTypes.put(NUMERIC, BigDecimal.class);  // 2 数字
        jdbcJavaTypes.put(DECIMAL, BigDecimal.class);  // 3 数字
        jdbcJavaTypes.put(INTEGER, Integer.class);   // 4 数字
        jdbcJavaTypes.put(SMALLINT, Short.class);   // 5 数字
        jdbcJavaTypes.put(FLOAT, BigDecimal.class);   // 6 数字
        jdbcJavaTypes.put(REAL, BigDecimal.class);   // 7 数字
        jdbcJavaTypes.put(DOUBLE, BigDecimal.class);  // 8 数字
        jdbcJavaTypes.put(VARCHAR, String.class);   // 12 字符串
        jdbcJavaTypes.put(BOOLEAN, Boolean.class);   // 16 布尔
        jdbcJavaTypes.put(DATALINK, String.class);   // 70 /
        jdbcJavaTypes.put(DATE, Date.class);    // 91 日期
        jdbcJavaTypes.put(TIME, Date.class);    // 92 日期
        jdbcJavaTypes.put(TIMESTAMP, Date.class);   // 93 日期
        jdbcJavaTypes.put(OTHER, Object.class);    // 1111 其他类型？
        //jdbcJavaTypes.put(JAVA_OBJECT, Object.class);  // 2000
        //jdbcJavaTypes.put(DISTINCT, String.class);   // 2001
        //jdbcJavaTypes.put(STRUCT, String.class);   // 2002
        //jdbcJavaTypes.put(ARRAY), String.class);    // 2003
        jdbcJavaTypes.put(BLOB, Blob.class);    // 2004 二进制
        jdbcJavaTypes.put(CLOB, Clob.class);    // 2005 大文本
       //jdbcJavaTypes.put(REF, String.class);    // 2006
       //jdbcJavaTypes.put(SQLXML, String.class);   // 2009
        jdbcJavaTypes.put(NCLOB, Clob.class);    // 2011 大文本
    }

    public static Class<?> jdbcTypeToJavaType(int jdbcType) {
        return jdbcJavaTypes.get(jdbcType);
    }

    public static boolean isJavaNumberType(int jdbcType) {
        Class<?> type = jdbcJavaTypes.get(jdbcType);
        return (type == null) ? false : (Number.class.isAssignableFrom(type))? true : false;
    }

    public static void main(String[] args) {
        System.out.println(jdbcTypeToJavaType(INTEGER));
    }

    private ColumnTypes() {}
}
