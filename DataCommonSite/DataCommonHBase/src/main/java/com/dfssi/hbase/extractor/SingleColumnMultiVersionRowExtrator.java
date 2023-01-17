package com.dfssi.hbase.extractor;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Set;

public class SingleColumnMultiVersionRowExtrator implements RowExtractor<String> {
	
	private Set<String> values;
	private boolean returnRowKeyStr;
	private byte[] cf;
	private byte[] cl;

	public SingleColumnMultiVersionRowExtrator(byte[] cf, byte[] cl, Set<String> values){
		this(cf, cl, values, false);
	}
	
	public SingleColumnMultiVersionRowExtrator(byte[] cf, byte[] cl, Set<String> values, boolean returnRowKeyStr){
		this.cf = cf;
		this.cl = cl;
		this.values = values;
		this.returnRowKeyStr = returnRowKeyStr;
	}
	
	public String extractRowData(Result result, int rowNum) throws IOException {
		
		for(Cell cell : result.getColumnCells(cf, cl)){
			values.add(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
		}
		if(returnRowKeyStr){
			return Bytes.toString(result.getRow());
		}
		return null;
	}

}
