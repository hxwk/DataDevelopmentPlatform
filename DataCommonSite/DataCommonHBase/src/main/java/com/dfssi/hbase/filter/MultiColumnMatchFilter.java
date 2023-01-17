package com.dfssi.hbase.filter;

import com.dfssi.hbase.protobuf.generated.MultiColumnMatchFilterProto;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.ByteStringer;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * 
 * 	  多字段名称过滤
 *                  
 * @author  LiXiaoCong
 * @date    2016-8-11 上午11:37:59 
 * @version 1.0
 * @since   JDK 1.7
 */
public class MultiColumnMatchFilter extends FilterBase {

	static final Log LOG = LogFactory.getLog(MultiColumnMatchFilter.class);

	private byte[] columnFamily;
	private Set<String> mustMatchColumns = Sets.newHashSet();
	private Set<String> mayMatchColumns  = Sets.newHashSet();

	private Map<String, String> mustUnitMap = Maps.newHashMap();
	private Map<String, String> mayUnitMap  = Maps.newHashMap();
	private int minMatch;

	private int mustCount;
	private int matchCount;

	private Set<String> matchedFields = Sets.newHashSet();


	public MultiColumnMatchFilter(byte[] columnFamily,
								  Collection<String> mustMatchColumns){
		this(columnFamily, mustMatchColumns, null,
				mustMatchColumns == null ? 0 : mustMatchColumns.size());
	}

	public MultiColumnMatchFilter(byte[] columnFamily,
								  Collection<String> mustMatchColumns,
								  Collection<String> mayMatchColumns){
		this(columnFamily, mustMatchColumns, mayMatchColumns,
				mustMatchColumns == null ? 0 : mustMatchColumns.size());
	}



	public MultiColumnMatchFilter(byte[] columnFamily,
								  Collection<String> mustMatchColumns,
								  Collection<String> mayMatchColumns,
								  int minMatch) {

		Preconditions.checkNotNull(columnFamily, "columnFamily 不能为null.");
		Preconditions.checkArgument(minMatch >= 0, "最小匹配个数不能小于0.");

		this.columnFamily = columnFamily;

		String[] split;
		if(mustMatchColumns != null) {
			for (String field : mustMatchColumns) {
				this.mustMatchColumns.add(field);
				split = field.split(",");
				for(int i = 0; i < split.length; i++)
					mustUnitMap.put(split[i], split[0]);
			}
		}

		if(mayMatchColumns != null) {
			for (String field : mayMatchColumns) {
				this.mayMatchColumns.add(field);
				split = field.split(",");
				for(int i = 0; i < split.length; i++)
					mayUnitMap.put(split[i], split[0]);
			}
		}
		this.minMatch = minMatch;
	}


	public ReturnCode filterKeyValue(Cell v) throws IOException {
		
		if(mustCount == mustMatchColumns.size() &&
				matchCount >= minMatch)return ReturnCode.NEXT_ROW;

		if (!CellUtil.matchingFamily(v, this.columnFamily)) {
			return ReturnCode.NEXT_COL;
		}

		String column = Bytes.toString(v.getQualifierArray(),
				v.getQualifierOffset(), v.getQualifierLength());

		if(mustUnitMap.containsKey(column)){
			if(matchedFields.add(mustUnitMap.get(column))) {
				matchCount++;
				mustCount++;
			}
			return ReturnCode.INCLUDE_AND_NEXT_COL;
		}else if(mayUnitMap.containsKey(column)){
			if(matchedFields.add(mayUnitMap.get(column))) {
				matchCount++;
			}
			return ReturnCode.INCLUDE_AND_NEXT_COL;
		}
		return ReturnCode.SKIP;
	}
	
	@Override
	public boolean hasFilterRow() {
		return true;
	}

	@Override
	public boolean filterRow() throws IOException {

		/*LOG.error(String.format("mustCount : %s, mustMatchColumns.size() : %s,  matchCount : %s,  minMatch : %s",
				mustCount, mustMatchColumns.size(), matchCount, minMatch));*/

		return (mustCount != mustMatchColumns.size() || matchCount < minMatch);
	}

	@Override
	public void reset() throws IOException {
		this.matchedFields = Sets.newHashSet();
		this.matchCount = 0;
		this.mustCount  = 0;
	}
	

	@Override
	public byte[] toByteArray() throws IOException {

		return convert().toByteArray();
	}

	MultiColumnMatchFilterProto.MultiColumnMatchFilter convert(){

		MultiColumnMatchFilterProto.MultiColumnMatchFilter.Builder filter =
				MultiColumnMatchFilterProto.MultiColumnMatchFilter.newBuilder();

		filter.setColumnFamily(ByteStringer.wrap(this.columnFamily));

		for(String field : mustMatchColumns) {
			filter.addMustMatchColumns(
					ByteStringer.wrap(Bytes.toBytes(field)));
		}

		for(String field : mayMatchColumns) {
			filter.addMayMatchColumns(
					ByteStringer.wrap(Bytes.toBytes(field)));
		}
		filter.setMinMatch(minMatch);

		return filter.build();
	}
	
	public static MultiColumnMatchFilter parseFrom(final byte [] pbBytes)
			  throws DeserializationException {

		MultiColumnMatchFilterProto.MultiColumnMatchFilter proto;
	    try {
	        proto = MultiColumnMatchFilterProto.MultiColumnMatchFilter.parseFrom(pbBytes);

			byte[] cf = proto.getColumnFamily().toByteArray();

			Set<String> may = null;
			int mayMatchColumnsCount = proto.getMayMatchColumnsCount();
			if(mayMatchColumnsCount > 0) {
				may = Sets.newHashSetWithExpectedSize(mayMatchColumnsCount);
				for(int i = 0; i < mayMatchColumnsCount; i++){
					may.add(Bytes.toString(proto.getMayMatchColumns(i).toByteArray()));
				}
			}

			Set<String> must = null;
			int mustMatchColumnsCount = proto.getMustMatchColumnsCount();
			if(mustMatchColumnsCount > 0) {
				must = Sets.newHashSetWithExpectedSize(mustMatchColumnsCount);
				for(int i = 0; i < mustMatchColumnsCount; i++){
					must.add(Bytes.toString(proto.getMustMatchColumns(i).toByteArray()));
				}
			}

			return new MultiColumnMatchFilter(cf, must, may, proto.getMinMatch());

		} catch (InvalidProtocolBufferException e) {
			LOG.error(null, e);
	        throw new DeserializationException(e);
	    }

	  }

	boolean areSerializedFieldsEqual(Filter o) {
	    if (o == this) return true;
	    if (!(o instanceof MultiColumnMatchFilter)) return false;

	    MultiColumnMatchFilter other = (MultiColumnMatchFilter)o;
	    return toString().equals(other.toString());
	 }

	
	public boolean isFamilyEssential(byte[] name) {
	    return Bytes.equals(name, this.columnFamily);
	  }

	public byte[] getColumnFamily() {
		return columnFamily;
	}

	public Set<String> getMustMatchColumns() {
		return mustMatchColumns;
	}

	public Set<String> getMayMatchColumns() {
		return mayMatchColumns;
	}

	@Override
	public String toString() {
		return "MultiColumnMatchFilter{" +
				"columnFamily=" + Arrays.toString(columnFamily) +
				", mustMatchColumns=" + mustMatchColumns +
				", mayMatchColumns=" + mayMatchColumns +
				", minMatch=" + minMatch + '}';
	}

}
