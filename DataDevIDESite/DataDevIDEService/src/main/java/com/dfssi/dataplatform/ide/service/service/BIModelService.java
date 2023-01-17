package com.dfssi.dataplatform.ide.service.service;

import com.dfssi.dataplatform.ide.service.resource.DBConnectEntity;
import com.dfssi.dataplatform.ide.service.resource.DataResource;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(value = "biModelService")
@Transactional(value = "analysis", readOnly = true)
public class BIModelService {

    //数据预览
    public List<LinkedHashMap<String, Object>> dataPreview(String dataresourceType,
                                                 String host,
                                                 String port,
                                                 String username,
                                                 String password,
                                                 String databaseName,
                                                 String tableName,
                                                 String colNames,
                                                 int number) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        return dataResource.getTableData(tableName, colNames, "", number);

    }

    //获取数据库字段名及类型
    public Map<String, String> getTableColumnAndTypes(String dataresourceType,
                                                      String host,
                                                      String port,
                                                      String username,
                                                      String password,
                                                      String databaseName,
                                                      String tableName) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        return dataResource.getTableColumnAndTypes(tableName);

    }

    //类别列值
    public Set<Object> getCategoryValue(String dataresourceType,
                                        String host,
                                        String port,
                                        String username,
                                        String password,
                                        String databaseName,
                                        String tableName,
                                        String colName) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);

        List<LinkedHashMap<String, Object>> maps = dataResource.getTableData(tableName, colName, "", 0);
        Set<Object> record = Sets.newHashSet();
        for (Map<String, Object> map : maps) {
            record.add(map.get(colName));
        }
        return record;
    }

    //根据类别列列值获取数据
    public Object getDataByCategory(String dataresourceType,
                                                       String host,
                                                       String port,
                                                       String username,
                                                       String password,
                                                       String databaseName,
                                                       String tableName,
                                                       String colName,
                                                       String colNames,
                                                       String value) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);
        StringBuffer condition = new StringBuffer("");
        if (null != colName && null != value && !value.isEmpty()) {
            condition.append(colName + "=\"");
            condition.append(Joiner.on("\" OR " + colName + "=\"").skipNulls().join(value.split(";")));
            condition.append("\"");
        }
        return dataResource.getTableDataAsArray(tableName, colNames, condition.toString(), 0);
    }

//    //数据格式转换
//    public Object biDataConvert(List<LinkedHashMap<String, Object>> data, String biType) {
//        switch (biType) {
//            case "HeatMap" :
//                return null;
//            case "PieGraph" :
//                return null;
//            case "BarGraph" :
//                List<ArrayList<Object>> res = Lists.newArrayList();
//                data.forEach(record ->{
//                    int i = 0;
//                    ArrayList<Object> re = Lists.newArrayList();
//                    Set<String> colNames = record.keySet();
//                    for(String colName: colNames){
//                        Object value = record.get(colName);
//                        re.add(i, value);
//                        i++;
//                    }
//                    res.add(re);
//                });
//                return res;
//            case "LineGraph" :
//                return null;
//            case "ScatterPlot" :
//                return null;
//            default:
//                return data;
//        }
//    }
}
