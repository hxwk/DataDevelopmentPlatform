package com.dfssi.dataplatform.ide.datasource.mvc.service;

import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataConnectionTestEntity;
import com.dfssi.dataplatform.ide.datasource.util.ResultVo;

/**
 * @date 2018/10/10
 * @description 测试连接接口
 */
public interface CheckConnectionService {
    /**
     * jbdc测试连接
     */
    ResultVo exceQuery(DataConnectionTestEntity info, String sql, String dbType) throws Exception;

    /**
     *hdfs测试连接
     */
    boolean checkHdfsIsActive(String address, String port) throws Exception;

    /**
     *hdfs测试连接
     */
    ResultVo checkHdfsFilePathIsExist(String HdfsURI, String HdfsActiveNodeUser, String filePath);

    /**
     *kafka测试连接
     */
    ResultVo getTopics(DataConnectionTestEntity info);

    /**
     * hbase测试连接
     * @param info
     * @return
     */
    ResultVo checkHbase(DataConnectionTestEntity info);

    /**
     * es测试连接
     * @param info
     * @return
     */
    ResultVo checkElasticSearch(DataConnectionTestEntity info);

    /**
     * mongodb测试连接
     * @param info
     * @return
     */
    ResultVo checkMongodb(DataConnectionTestEntity info);

    /**
     * geode测试连接
     * @param info
     * @return
     */
    ResultVo checkGeode(DataConnectionTestEntity info);

    /**
     * UDP测试连接
     * @param info
     * @return
     */
    ResultVo checkUDP(DataConnectionTestEntity info);

    /**
     * TCP测试连接
     * @param info
     * @return
     */
    ResultVo checkTCP(DataConnectionTestEntity info);

    /**
     * http测试
     * @param info
     * @return
     */
    ResultVo checkHttp(DataConnectionTestEntity info);
}