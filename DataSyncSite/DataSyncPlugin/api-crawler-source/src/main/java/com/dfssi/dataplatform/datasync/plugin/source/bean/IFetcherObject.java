package com.dfssi.dataplatform.datasync.plugin.source.bean;

/**
 * handle fetcher object interface,such as weather, oil etc.
 * @author jianKang
 * @date 2017/12/01
 */
public interface IFetcherObject {

    /**
     * @return the sqls with insert to db
     */
    String insertSQL();

    /**
     * @return the sqls with delete from db
     */
    String deleteSQL();

    /**
     * @return the sqls with select all columns from db
     */
    String selectAllSQL();

    /**
     * @return the sqls with select all columns by params
     */
    String selectByParamSQL();

    /**
     * @return delete object by parameter
     */
    String deleteObjByParam();

}
