package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.annotation.DataSource;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.ftp.FtpClientPool;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.ftp.FtpTemplate;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ExportConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.DataStatisticsEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.HistoryDataQueryEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.ExportFactory;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.Exportor;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper.DataFieldMapper;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper.DataStatisticsMapper;
import com.google.common.collect.Maps;
import org.apache.commons.net.ftp.FTPClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/30 11:33
 */
@Component
public class DataStatisticsDao extends ElasticSearchDao{
    private final Logger logger = LoggerFactory.getLogger(DataStatisticsDao.class);

    @Autowired
    private ExportConfig exportConfig;

    @Autowired
    private DataStatisticsMapper dataStatisticsMapper;

    @Autowired
    private DataFieldMapper dataFieldMapper;

    @Autowired
    private FtpTemplate ftpTemplate;

    private String[] excludes = new String[]{"location",
            "expend_time", "expend_idle_time", "expend_idle", "expend_mile", "expend_gpsMile", "expend_fuel", "speed"};

    public SearchHits queryHistoryData(HistoryDataQueryEntity historyDataQueryEntity){

        SearchRequestBuilder prepareSearch =
                newSearchRequestBuilder(historyDataQueryEntity.getStartTime(), historyDataQueryEntity.getEndTime());

        String[] columns = historyDataQueryEntity.getColumns();
        if(columns != null && columns.length > 0){
            prepareSearch.setFetchSource(columns, excludes);
        }else{
            prepareSearch.setFetchSource(null, excludes);
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] vid = historyDataQueryEntity.getVid();
        if(vid != null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vid));
        }

        SearchResponse searchResponse = prepareSearch
                .setQuery(boolQueryBuilder)
                .setFrom((historyDataQueryEntity.getPageNow() - 1) * historyDataQueryEntity.getPageSize())
                .setSize(historyDataQueryEntity.getPageSize())
                .addSort("receiveTime", SortOrder.DESC)
                .get();

        return searchResponse.getHits();

    }

    /**
     * 存在并发问题
     * @param historyDataQueryEntity
     * @return
     */
    public Map<String, Object> exportHistoryData(HistoryDataQueryEntity historyDataQueryEntity){

        SearchRequestBuilder prepareSearch =
                newSearchRequestBuilder(historyDataQueryEntity.getStartTime(), historyDataQueryEntity.getEndTime());

        String[] columns = historyDataQueryEntity.getColumns();
        if(columns != null && columns.length > 0){
            prepareSearch.setFetchSource(columns, excludes);
        }else{
            prepareSearch.setFetchSource(null, excludes);
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] vid = historyDataQueryEntity.getVid();
        if(vid != null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vid));
        }

        prepareSearch.setScroll(TimeValue.timeValueMinutes(5));
        prepareSearch.setSize(500);

        //使用深分页查询 并导出为文件
        Exportor exportor = ExportFactory.newExportor(historyDataQueryEntity.getExportType(), exportConfig);
        int n = 0;
        SearchResponse searchResponse = prepareSearch.setFetchSource(null, new String[]{"location"}).get();
        long total = searchResponse.getHits().getTotalHits();
        Map<String, Object> res = Maps.newHashMap();
        FtpClientPool pool = ftpTemplate.getPool();
        FTPClient ftpClient = null;
        try {
            ftpClient = pool.borrowObject();
            logger.info(String.format("开始执行文件导出操作， 导出目录为：%s", exportor.getDir()));
            do {
                SearchHit[] hits = searchResponse.getHits().getHits();
                int length = hits.length;
                for(int i = 0; i < length; i++){
                    exportor.addRowMap(hits[i].getSourceAsMap());
                }
                if(exportor.getSize() > historyDataQueryEntity.getMaxRowPerFile()){
                    n = n + 1;
                    exportor.finish(String.valueOf(n), true);
                }
                searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId())
                        .setScroll(TimeValue.timeValueMinutes(5)).get();

            } while(searchResponse.getHits().getHits().length != 0);

            if(exportor.getSize() > 0){
                n = n + 1;
                exportor.finish(String.valueOf(n), true);
            }

            //将文件上传至ftp
            String ftpDir = exportor.dir2Ftp(ftpClient);

            res.put("total", total);
            res.put("files", n);
            res.put("zipFile", ftpDir);

        } catch (Exception e) {
            String format = String.format("导出数据失败：%s", historyDataQueryEntity);
            logger.error(format, e);
            res.put("fail", format);
            res.put("error", e.getMessage());
        }finally {
            if(ftpClient != null){
                try {
                    pool.returnObject(ftpClient);
                } catch (Exception e) {
                    logger.error("关闭ftp客户端失败.", e);
                }
            }
        }

        return res;

    }

    private boolean upload2Ftp(String file) throws IOException {
        FTPClient ftpClient = exportConfig.getFtpClient();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        ftpClient.changeWorkingDirectory(exportConfig.getParentDir());

        File f = new File(file);
        FileInputStream is = new FileInputStream(f);

        boolean b = ftpClient.storeFile(f.getName(), is);
        ftpClient.logout();
        ftpClient.disconnect();
        is.close();

        System.err.println("文件上传成功。" + b);

        return b;
    }

    private String dataStatisticsEntity2SQL(DataStatisticsEntity dataStatisticsEntity){

       /* String[] indexs = getIndexs(dataStatisticsEntity.getStartTime(), dataStatisticsEntity.getEndTime());
        if(indexs.length > 0) {
            new SQL() {
                {
                    String[] columns = dataStatisticsEntity.getColumns();
                    if (columns != null) {
                        SELECT(columns);
                    } else {
                        SELECT("*");
                    }

                    FROM(indexs);


                }
            };
        }
*/

        return null;
    }

    @DataSource
    public List<Map<String, Object>>  findAllFields(String label){
        return dataFieldMapper.findAll(label);
    }


    @DataSource
    public List<Map<String, Object>> queryStatisticsByDay(String vid, String startDay, String stopDay){
        return dataStatisticsMapper.queryStatisticsByDay(vid, startDay, stopDay);
    }

    @DataSource
    public List<Map<String, Object>> queryStatisticsTrip(String vid, String startDay, String stopDay){
        return dataStatisticsMapper.queryStatisticsTrip(vid, startDay, stopDay);
    }

    @DataSource
    public List<Map<String, Object>> queryStatisticsByMonth(String vid, String startDay, String stopDay){
        return dataStatisticsMapper.queryStatisticsByMonth(vid, startDay, stopDay);
    }

}
