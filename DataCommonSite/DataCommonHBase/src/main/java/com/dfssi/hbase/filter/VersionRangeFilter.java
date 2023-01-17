package com.dfssi.hbase.filter;

import com.dfssi.hbase.protobuf.generated.VersionRangeFilterProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.filter.ParseFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Description:
 * 
 * 	   提供字段的版本范围过滤
 * 
 *                  
 * @author  LiXiaoCong
 * @date    2016-8-11 上午11:37:59 
 * @version 1.0
 * @since   JDK 1.7
 */
public class VersionRangeFilter extends FilterBase {
	
	static final Log LOG = LogFactory.getLog(VersionRangeFilter.class);
	
	private String columnFamily;                   //版本过滤的列族
	private String columnQualifier;                //版本过滤字段
	private long upperBound;                 	   //版本上界
	private long lowerBound = -1L;                 //版本下界
	private boolean   filterIfMissing; 
	
	private byte[] columnFamilyByte;
	private byte[] columnQualifierByte;
	
	
	private boolean exist;

	public VersionRangeFilter(String columnFamily, String columnQualifier,
			long upperBound, long lowerBound) {
		this(columnFamily, columnQualifier, upperBound, lowerBound, false);
	}

	public VersionRangeFilter(String columnFamily, String columnQualifier,
			long upperBound, long lowerBound, boolean filterIfMissing) {
		super();
		
		checkArgument(columnFamily != null, "参数 columnFamily 不能为null");
		checkArgument(columnQualifier != null, "参数 columnQualifier 不能为null");
		checkArgument(upperBound >= lowerBound, "upperBound 必须 大于或等于下界 ");
		
		this.columnFamily = columnFamily;
		this.columnQualifier = columnQualifier;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.filterIfMissing = filterIfMissing;
		
		columnFamilyByte = columnFamily.getBytes();
		columnQualifierByte = columnQualifier.getBytes();
		
		if(this.upperBound <= 0L)this.upperBound = Long.MAX_VALUE;
	}
	

	public ReturnCode filterKeyValue(Cell v) throws IOException {
		
		  if (!CellUtil.matchingColumn(v, this.columnFamilyByte, this.columnQualifierByte)) {
		      return ReturnCode.INCLUDE;
		    }
		  
		  long timestamp = v.getTimestamp();
		 
		 
		  if(Long.compare(timestamp, this.lowerBound) >= 0 && Long.compare(timestamp, this.upperBound) <= 0){
			  this.exist = true;
			  return ReturnCode.INCLUDE;
		  }
		  
		return ReturnCode.SKIP;
	}

	
	@Override
	public boolean hasFilterRow() {
		return true;
	}

	@Override
	public boolean filterRow() throws IOException {
		
		return this.exist? !this.exist: this.filterIfMissing;
	}

	@Override
	public void reset() throws IOException {
		this.exist = false;;
	}
	

	@Override
	public byte[] toByteArray() throws IOException {
		
		return convert().toByteArray();
	}
	
	VersionRangeFilterProto.VersionRangeFilter convert(){
		
		VersionRangeFilterProto.VersionRangeFilter.Builder filter =
				VersionRangeFilterProto.VersionRangeFilter.newBuilder();
		filter.setColumnFamily(columnFamily);
		filter.setColumnQualifier(columnQualifier);
		filter.setUpperBound(upperBound);
		filter.setLowerBound(lowerBound);
		filter.setFilterIfMissing(filterIfMissing);
		
		return filter.build();
	}
	
	public static VersionRangeFilter parseFrom(final byte [] pbBytes)
			  throws DeserializationException {
		
		VersionRangeFilterProto.VersionRangeFilter proto;
	    try {
	      proto = VersionRangeFilterProto.VersionRangeFilter.parseFrom(pbBytes);
	    } catch (InvalidProtocolBufferException e) {
	      throw new DeserializationException(e);
	    }
	    return new VersionRangeFilter(proto.getColumnFamily(), proto.getColumnQualifier(),
	    		       proto.getUpperBound(), proto.getLowerBound(), proto.getFilterIfMissing());
	  }

	public static Filter createFilterFromArguments(ArrayList<byte []> filterArguments) {
		
	   checkArgument(filterArguments.size() == 4 || filterArguments.size() == 5,
	                                "Expected 4 or 5 but got: %s", filterArguments.size());
	    
	    byte [] family = ParseFilter.removeQuotesFromByteArray(filterArguments.get(0));
	    byte [] qualifier = ParseFilter.removeQuotesFromByteArray(filterArguments.get(1));
	    long upper = ParseFilter.convertByteArrayToLong(filterArguments.get(2));
	    long lower = ParseFilter.convertByteArrayToLong(filterArguments.get(3));
	  
	    VersionRangeFilter filter = new VersionRangeFilter(new String(family),
	    							new String(qualifier), upper, lower);
	    
	    if(filterArguments.size() == 5){
	    	 boolean filterIfMissing = ParseFilter.convertByteArrayToBoolean(filterArguments.get(4));
	    	 filter.setFilterIfMissing(filterIfMissing);
	    }
	   
	    return filter;
	}
	
	boolean areSerializedFieldsEqual(Filter o) {
	    if (o == this) return true;
	    if (!(o instanceof VersionRangeFilter)) return false;

	    VersionRangeFilter other = (VersionRangeFilter)o;
	    return toString().equals(other.toString());
	 }

	
	public boolean isFamilyEssential(byte[] name) {
	    return !this.filterIfMissing || Bytes.equals(name, this.columnFamily.getBytes());
	  }

	
	public String getColumnFamily() {
		return columnFamily;
	}

	public String getColumnQualifier() {
		return columnQualifier;
	}

	public long getUpperBound() {
		return upperBound;
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public boolean isFilterIfMissing() {
		return filterIfMissing;
	}

	/**
	 * true : 当指定列中不存在满足条件的数据时  ， 将整条数据过滤点
	 * @param filterIfMissing
	 */
	public void setFilterIfMissing(boolean filterIfMissing) {
		this.filterIfMissing = filterIfMissing;
	}

	@Override
	public String toString() {
		return "VersionRangeFilter [columnFamily=" + columnFamily
				+ ", columnQualifier=" + columnQualifier + ", upperBound="
				+ upperBound + ", lowerBound=" + lowerBound
				+ ", filterIfMissing=" + filterIfMissing + "]";
	}
}
