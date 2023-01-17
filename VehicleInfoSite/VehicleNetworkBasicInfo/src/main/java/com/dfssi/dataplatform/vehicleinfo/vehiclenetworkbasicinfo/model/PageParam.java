package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model;

import java.io.Serializable;

/**
 * 页面分页参数传递实体类
 * Created by yanghs on 2018/4/8.
 */
public class PageParam implements Serializable {
    /********方式一********/
    //偏移值
    private int offset;
    //每页大小
    private int limit=10;

    /********方式二********/
    //页码
    private int pageNum=0;
    //每页大小
    private int pageSize=10;

    /********方式三********/
    //页码
    private int page;
    //每页大小
    private int rows=10;

    //排序字段
    private String sort;
    //排序方式 asc、desc
    private String order;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
