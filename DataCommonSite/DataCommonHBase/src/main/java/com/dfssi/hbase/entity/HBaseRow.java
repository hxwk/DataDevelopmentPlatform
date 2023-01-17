/**
  * Copyright (c) 2016, jechedo All Rights Reserved.
  *
 */
package com.dfssi.hbase.entity;


/**
 * Description:
 * 
 *  Date    2016-6-2 下午12:49:20   
 *                  
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
public class HBaseRow extends AbstractRow<HBaseCell> {
	
	public HBaseRow(String rowKey){
		super(rowKey);
	}
	
	public boolean[] addCell(String field, HBaseCell ... cells){
		
		boolean[] status = new boolean[cells.length];
		for(int i = 0; i < cells.length; i++){
			status[i] = addCell(field, cells[i]);
		}
		return status;
	}
	
	protected HBaseCell createCell(String field, String value, long capTime) {
		return new HBaseCell(field, value, capTime);
	}

}
