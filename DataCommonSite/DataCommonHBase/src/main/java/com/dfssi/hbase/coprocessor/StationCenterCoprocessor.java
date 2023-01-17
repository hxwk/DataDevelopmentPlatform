/**
 * 
 */
package com.dfssi.hbase.coprocessor;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Description:
 * 		这是一个例子。
 * 
 * History：
 * =============================================================
 * Date                      Version        Memo
 * 2015-12-3上午9:23:26            1.0            Created by LiXiaoCong
 * 
 * =============================================================
 * 
 * Copyright 2015, 武汉白虹软件科技有限公司 。
 */

public class StationCenterCoprocessor extends BaseRegionObserver {

	private static final Logger LOG = LoggerFactory.getLogger(StationCenterCoprocessor.class);
	
	private static final byte[] CF = Bytes.toBytes("CF");
	private static final byte[] INDEXFIELD = Bytes.toBytes("STATIONMAC");       //索引字段
	private static final byte[] CLPREFIXFIELD = Bytes.toBytes("SN");            //列前缀 
	private static final byte[] CLSUFFIXFIELD = Bytes.toBytes("CAPTURE_TIME");  //列后缀
	
	private static final TableName INDEXTABLENAME = TableName.valueOf( "MAC_INDEX" );
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy") ;
	
	
	
	public void postPut(ObserverContext<RegionCoprocessorEnvironment> e,
			Put put, WALEdit edit, Durability durability) throws IOException {
		
		LOG.info( "协处理器被触发, 开始往  MAC_INDEX插入数据." );
	  
		HTableInterface table = e.getEnvironment().getTable(INDEXTABLENAME);
		String mac = getCellValue(put.get(CF,INDEXFIELD).get(0));
		String sn =  getCellValue(put.get(CF,CLPREFIXFIELD).get(0));
		String longTimeStr =  getCellValue(put.get(CF,CLSUFFIXFIELD).get(0));
		
		String yearStr = getYearStr( longTimeStr );
		
		Put row = new Put( Bytes.toBytes( mac + "_" + yearStr ) );
		row.setDurability( Durability.SKIP_WAL);
		row.add(CF, Bytes.toBytes( sn + "_" + longTimeStr ), put.getRow() );
		
		table.put(row);
		table.close();
		
	}
	

	private String getCellValue( Cell cell ){
		
		 return  Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
	}
	
	private String getYearStr( String longTime ){
		
		return format.format( new Date( Long.parseLong(longTime) ) );
	}
	
	
}
