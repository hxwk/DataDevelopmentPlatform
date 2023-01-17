package com.dfssi.hbase.filter;

import com.dfssi.hbase.protobuf.generated.DeviceRecordsTimeRangeFilterProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.filter.ParseFilter;
import org.apache.hadoop.hbase.util.ByteStringer;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Description:
 * 
 * 	   T_DeviceRecords的时间范围过滤
 *                  
 * @author  LiXiaoCong
 * @date    2016-8-11 上午11:37:59 
 * @version 1.0
 * @since   JDK 1.7
 */
public class DeviceRecordsTimeRangeFilter extends FilterBase {
	
	static final Log LOG = LogFactory.getLog(DeviceRecordsTimeRangeFilter.class);
	
	private byte[] columnFamily;                   //版本过滤的列族
	private byte[] columnQualifier;                //版本过滤字段
	private long lowerBound = -1L;                 //版本下界
	private long upperBound;                 	   //版本上界
	private boolean  filterOnMatch;                //当条件满足则马上过滤,不再往下匹配
	private boolean  filterIfMissing; 
	
	private boolean exist;
	
	public DeviceRecordsTimeRangeFilter(byte[] columnFamily, byte[] columnQualifier,
			long lowerBound, long upperBound) {
		this(columnFamily, columnQualifier, lowerBound, upperBound, false, false);
	}
	
	public DeviceRecordsTimeRangeFilter(byte[] columnFamily, byte[] columnQualifier,
			long lowerBound, long upperBound, boolean filterOnMatch) {
		this(columnFamily, columnQualifier, lowerBound, upperBound, filterOnMatch, false);
	}

	public DeviceRecordsTimeRangeFilter(byte[] columnFamily, byte[] columnQualifier,
			long lowerBound, long upperBound, boolean filterOnMatch, boolean filterIfMissing) {
		super();
		
		checkArgument(columnFamily != null, "参数 columnFamily 不能为null");
		checkArgument(columnQualifier != null, "参数 columnQualifier 不能为null");
		checkArgument(upperBound >= lowerBound, "upperBound 必须 大于或等于 lowerBound");
		
		this.columnFamily = columnFamily;
		this.columnQualifier = columnQualifier;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.filterOnMatch = filterOnMatch;
		this.filterIfMissing = filterIfMissing;
		
		if(this.upperBound <= 0L)this.upperBound = Long.MAX_VALUE;
	}

	public ReturnCode filterKeyValue(Cell v) throws IOException {
		
		  if(exist && filterOnMatch)return ReturnCode.NEXT_ROW;
		
		  if (!CellUtil.matchingColumn(v, this.columnFamily, this.columnQualifier)) {
		      return ReturnCode.INCLUDE;
		    }
		  
		  long timestamp = v.getTimestamp();
		  long capTime = Long.parseLong(Bytes.toString(v.getValueArray(), v.getValueOffset(), v.getValueLength()));
		 
		  if(Long.compare(timestamp, this.lowerBound) >= 0 && Long.compare(capTime, this.upperBound) <= 0){
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
		this.exist = false;
	}
	

	@Override
	public byte[] toByteArray() throws IOException {
		
		return convert().toByteArray();
	}
	
	DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter convert(){
		
		DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter.Builder filter = 
				DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter.newBuilder();
		filter.setColumnFamily(ByteStringer.wrap(this.columnFamily));
		filter.setColumnQualifier(ByteStringer.wrap(this.columnQualifier));
		filter.setLowerBound(this.lowerBound);
		filter.setUpperBound(this.upperBound);
		filter.setFilterOnMatch(this.filterOnMatch);
		filter.setFilterIfMissing(this.filterIfMissing);
		
		return filter.build();
	}
	
	public static DeviceRecordsTimeRangeFilter parseFrom(final byte [] pbBytes)
			  throws DeserializationException {
		
		DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter proto;
	    try {
	      proto = DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter.parseFrom(pbBytes);
	    } catch (InvalidProtocolBufferException e) {
	      throw new DeserializationException(e);
	    }
	    return new DeviceRecordsTimeRangeFilter(proto.getColumnFamily().toByteArray(), 
	    		                                proto.getColumnQualifier().toByteArray(), 
	    		                                proto.getLowerBound(),
	    		                                proto.getUpperBound(), 
	    		                                proto.getFilterOnMatch(),
	    		                                proto.getFilterIfMissing());
	  }

	public static Filter createFilterFromArguments(ArrayList<byte []> filterArguments) {
		
	   checkArgument(filterArguments.size() == 4 || filterArguments.size() == 6,
	                                "Expected 4 or 6 but got: %s", filterArguments.size());
	    
	    byte [] family = ParseFilter.removeQuotesFromByteArray(filterArguments.get(0));
	    byte [] qualifier = ParseFilter.removeQuotesFromByteArray(filterArguments.get(1));
	    long lower = ParseFilter.convertByteArrayToLong(filterArguments.get(2));
	    long upper = ParseFilter.convertByteArrayToLong(filterArguments.get(3));
	  
	    DeviceRecordsTimeRangeFilter filter = new DeviceRecordsTimeRangeFilter(family, qualifier, upper, lower); 
	    
	    if(filterArguments.size() == 6){
	    	 boolean filterOnMatch = ParseFilter.convertByteArrayToBoolean(filterArguments.get(4));
	    	 filter.setFilterOnMatch(filterOnMatch);
	    	 boolean filterIfMissing = ParseFilter.convertByteArrayToBoolean(filterArguments.get(5));
	    	 filter.setFilterIfMissing(filterIfMissing);
	    }
	   
	    return filter;
	}
	
	boolean areSerializedFieldsEqual(Filter o) {
	    if (o == this) return true;
	    if (!(o instanceof DeviceRecordsTimeRangeFilter)) return false;

	    DeviceRecordsTimeRangeFilter other = (DeviceRecordsTimeRangeFilter)o;
	    return toString().equals(other.toString());
	 }

	
	public boolean isFamilyEssential(byte[] name) {
	    return !this.filterIfMissing || Bytes.equals(name, this.columnFamily);
	  }

	
	public byte[] getColumnFamily() {
		return columnFamily;
	}

	public byte[] getColumnQualifier() {
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

	public boolean isFilterOnMatch() {
		return filterOnMatch;
	}

	/**true : 只要找到一个cell满足条件则不再往下匹配
	 * @param filterOnMatch
	 */
	public void setFilterOnMatch(boolean filterOnMatch) {
		this.filterOnMatch = filterOnMatch;
	}

	@Override
	public String toString() {
		return "DeviceRecordsTimeRangeFilter [columnFamily="
				+ Arrays.toString(columnFamily) + ", columnQualifier="
				+ Arrays.toString(columnQualifier) + ", lowerBound="
				+ lowerBound + ", upperBound=" + upperBound
				+ ", filterOnMatch=" + filterOnMatch + ", filterIfMissing="
				+ filterIfMissing + "]";
	}
	
}
