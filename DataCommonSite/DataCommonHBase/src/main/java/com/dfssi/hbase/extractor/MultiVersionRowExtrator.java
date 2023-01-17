package com.dfssi.hbase.extractor;

import com.dfssi.hbase.entity.HBaseRow;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class MultiVersionRowExtrator implements RowExtractor<HBaseRow> {
	
	private HBaseRow row;
	
	public HBaseRow extractRowData(Result result, int rowNum) throws IOException {
		
		row = new HBaseRow(Bytes.toString(result.getRow()));
		
		String field = null;
		String value = null;
		long capTime = 0L;
		for(Cell cell : result.listCells()){
			field = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
			value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			capTime = cell.getTimestamp();
			
			row.addCell(field, value, capTime);
		}
		return  row ;
	}

}
