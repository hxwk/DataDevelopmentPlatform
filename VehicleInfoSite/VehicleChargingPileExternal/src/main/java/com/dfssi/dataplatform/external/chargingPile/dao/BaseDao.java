package com.dfssi.dataplatform.external.chargingPile.dao;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/30 17:09
 */
public interface BaseDao<T> {
    public void insert(T entity);

    public void delete(String id);

}
