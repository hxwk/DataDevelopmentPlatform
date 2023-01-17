package com.dfssi.dataplatform.cloud.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 列表结果
 * @author yanghs
 *
 * @param <T>
 */
@Data
public class PageResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/********方式一********/
	//列数据
	private List<T> rows;
	//总条数
	private long total;

	/********方式二********/
	//列数据
	private List<T> content;
	//总条数
	private long totalElements;

}
